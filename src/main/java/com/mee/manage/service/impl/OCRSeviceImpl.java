package com.mee.manage.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.houbb.word.checker.core.impl.EnWordChecker;
import com.mee.manage.service.IOCRService;
import com.mee.manage.util.Config;
import com.mee.manage.util.JoddHttpUtils;
import com.mee.manage.vo.*;
import com.recognition.software.jdeskew.ImageDeskew;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.util.ImageHelper;
import net.sourceforge.tess4j.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;


@Service
@EnableConfigurationProperties(Config.class)
public class OCRSeviceImpl implements IOCRService {

    private static final Logger logger = LoggerFactory.getLogger(IOCRService.class);

    private byte[] graphDef;

    private List<String> labels;

    static final double MINIMUM_DESKEW_THRESHOLD = 0.05d;
    ///tessdata
    private final static String DATA_PATH = "/data/ocr/tessdata";
    @Autowired
    private Config config;

    @Override
    public void loadTrainingData(String path) {
        graphDef = readAllBytesOrExit(Paths.get(path, "tensorflow_inception_graph.pb"));
        labels = readAllLinesOrExit(Paths.get(path, "imagenet_comp_graph_label_strings.txt"));
    }

    @Override
    public String imageRecognition(byte[] imageBytes) {

        try (Tensor image = Tensor.create(imageBytes)) {
            float[] labelProbabilities = executeInceptionGraph(graphDef, image);

            int bestLabelIdx = maxIndex(labelProbabilities);
            logger.info("BestLabelIndex: {}",bestLabelIdx);
            String result = labels.get(bestLabelIdx);
            float likely = labelProbabilities[bestLabelIdx] * 100;
            logger.info("Best Result = {} , Likely ({}%) ",result,likely);

            return result +"("+(float)(Math.round(likely*100))/100+"%)";
        }

    }

    @Override
    public String textOCR(MultipartFile file, String language) {
        /*
        try {
            InputStream inputStream = file.getInputStream();
            int engineMode = ITessAPI.TessOcrEngineMode.OEM_LSTM_ONLY;
            int pageSegMode = ITessAPI.TessPageSegMode.PSM_AUTO;//PSM_SINGLE_BLOCK

            BufferedImage textImage = ImageIO.read(inputStream);
            logger.info(" engineMode = {} , pageSegMode = {} ",engineMode,pageSegMode);
            String result = findOCR(textImage, language, engineMode, pageSegMode);
            logger.info(result);

        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        String ocrSpaceResult = ocrSpfiace(file, language);
        return ocrSpaceResult;
    }

    private String ocrSpfiace(MultipartFile file, String language) {
        String result = null;
        try {
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            String encoded = "data:image/jpg;base64," + base64Image.replaceAll("[\\s*\t\n\r]", "");
            String apiKey = config.getOcrApiKey();
            String url = config.getOcrUrl();
            Map<String, Object> parames = new HashMap<>();
            parames.put("apikey", apiKey);
            parames.put("base64Image", encoded);
            parames.put("language", language);
            parames.put("isOverlayRequired", true);
            parames.put("filetype", "JPG");
            parames.put("detectOrientation", true);
            parames.put("isCreateSearchablePdf", false);
            parames.put("isSearchablePdfHideTextLayer", false);
            parames.put("scale", true);
            parames.put("isTable", true);

            result = JoddHttpUtils.sendPost(url, parames);

            OcrSpaceResponseVo vo = JSON.parseObject(result, OcrSpaceResponseVo.class);
            if (vo.getOCRExitCode() == 1) {
                List<ParsedResultsVo> parsedResultsVos = vo.getParsedResults();
                if (parsedResultsVos != null && parsedResultsVos.size() > 0) {
                    ParsedResultsVo parsedResultsVo = parsedResultsVos.get(0);
                    result = parsedResultsVo.getParsedText();
                    logger.info(result);
//                    List<ProductsVo> productsVos = getProducts(result);
//                    logger.info(JSON.toJSONString(productsVos));

                    TextOverlayVo textVo = parsedResultsVo.getTextOverlay();
                    if(textVo != null) {
                        List<ProductsVo> productsVos = getProducts(textVo.getLines());
                        logger.info(JSON.toJSONString(productsVos));
                    }

                }
            } else {
                logger.info("OCR error!", vo.getErrorMessage());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
    private List<ProductsVo> getProducts(String invoiceStr){
        logger.info("Begin getProducts");
        if(StringUtils.isEmpty(invoiceStr))
            return null;

        String[] lines = invoiceStr.split("\r\n");
        if(lines == null || lines.length <= 0)
            return null;

        Integer[] location = null;
        List<ProductsVo> products = new ArrayList<>();
        for(String line  : lines){
            if(line == null)
                continue;

            logger.info(" === Line : {}",line);
            String[] words = line.split("\t");

            if(location == null) {
                location = getFirstLine(words);
                if(location == null && isEnd(words)) {
                    if (products.isEmpty()) {
                        logger.info("Line End; Nothing found!");
                    }
                    logger.info("=== End line : ", line);
                    break;
                }
                if(location != null)
                    logger.info("location : {}",JSON.toJSONString(location));
            }else {
                ProductsVo product = getProduct(words,location);
                if(product == null){
                    logger.info("not get prodect = {}",line);
                    if(isEnd(words)) {
                        if (products.isEmpty()) {
                            logger.info("Line End; Nothing found!");
                        }
                        logger.info("=== End line : ", line);
                        break;
                    }

                }else {
                    logger.info("Get prodect = {}",JSON.toJSONString(product));
                    products.add(product);
                }
            }

        }

        logger.info("End getProducts");
        return products;
    }

    private List<ProductsVo> getProducts(List<LineVo> lines){
        if(lines == null || lines.isEmpty())
            return null;

        List<ProductsVo> products = new ArrayList<>();
        for(LineVo line : lines) {
            if (line == null)
                continue;

            List<WordsVo> words = line.getWords();
            if(words == null || words.isEmpty())
                continue;

            logger.info("line start");
            isFirstLine(words);
            logger.info("line end");

        }

        return products;
    }

    private boolean isFirstLine(List<WordsVo> words){
        if(words == null || words.isEmpty())
            return false;

        for(WordsVo word : words){
            logger.info("Word = {}, [{},{}],Weight = {},Hight = {}",word.getWordText(),word.getTop(),word.getLeft(),word.getWidth(),word.getHeight());
        }

        return true;
    }


    private Integer[] getFirstLine(String[] words){
        if(words == null || words.length <= 0)
            return null;

        Integer[] location = null;
        for(int i = 0; i < words.length; i++){
            if(words[i] == null || StringUtils.isEmpty(words[i]))
                continue;

            if (isCorrect(words[i],"description")){
                if(location == null)
                    location = new Integer[3];

                location[0] = i;
            } else if(isCorrect(words[i],"qty") || isCorrect(words[i],"quantity")) {
                if(location == null)
                    location = new Integer[3];

                location[1] = i;
            } else if(isCorrect(words[i],"unit price")) {
                if (location == null)
                    location = new Integer[3];

                location[2] = i;
            }
        }

        return location;
    }

    private boolean isEnd(String[] words){
        for(String word : words) {
            if(word == null)
                continue;

//            String correct = EnWordChecker.getInstance().correct(word.toLowerCase());
            String correct = word.toLowerCase();
            logger.info("isEnd = {}",word);

            if(correct.indexOf("total") >= 0) {
                logger.info("IsEnd = true");
                return true;
            }

        }

        return false;
    }

    private ProductsVo getProduct(String[] words,Integer[] location){
        if(words == null || location.length <= 0 || words.length < location.length)
            return null;

        String name = null;
        if(location[0] != null)
            name = words[location[0]];

        String num = null;
        if(location[1] != null)
            num = words[location[1]];

        String price = null;
        if(location[2] != null)
            price = words[location[2]];

        if(StringUtils.isEmpty(name) && StringUtils.isEmpty(num) && StringUtils.isEmpty(price)) {
            return null;
        }
        ProductsVo products = new ProductsVo();

        if(!StringUtils.isEmpty(name))
            products.setName(name);

        if (!StringUtils.isEmpty(num) && Tools.isNumeric(num)){
            products.setNum(Integer.parseInt(num));
        }

        if (!StringUtils.isEmpty(price) && Tools.isNumeric(price))
            products.setPrice(new BigDecimal(price));


        return products;
    }


    private boolean isCorrect(String word,String target){
        if(word == null || StringUtils.isEmpty(word))
            return false;

//        logger.info("word = {}, target = {}",word.toLowerCase(),target);
        boolean flag = false;
        //String correct = EnWordChecker.getInstance().correct(word.toLowerCase());
        float similarity = SimilarityStrUtil.getSimilarityRatio(word.toLowerCase().trim(),target.toLowerCase().trim());
        logger.info("word = {}/ target = {} = {}",word,target.toLowerCase(),similarity);
        if(similarity > 0.80)
            flag = true;

        return flag;
    }

    /**
     * MultipartFile 转换成File
     *
     * @param multfile 原文件类型
     * @return File
     * @throws IOException
     */
    private File multipartToFile(MultipartFile multfile) throws IOException {
        // 获取文件名
        String fileName = multfile.getOriginalFilename();
        // 获取文件后缀
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        // 用uuid作为文件名，防止生成的临时文件重复
        String s = UUID.randomUUID().toString();//用来生成数据库的主键id非常不错。。

        final File excelFile = File.createTempFile(s, prefix);
        // MultipartFile to File
        multfile.transferTo(excelFile);

        return excelFile;
    }


    private byte[] readAllBytesOrExit(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            System.err.println("Failed to read [" + path + "]: " + e.getMessage());
            System.exit(1);
        }
        return null;
    }

    private List<String> readAllLinesOrExit(Path path) {
        try {
            return Files.readAllLines(path, Charset.forName("UTF-8"));
        } catch (IOException e) {
            System.err.println("Failed to read [" + path + "]: " + e.getMessage());
            System.exit(0);
        }
        return null;
    }

    private float[] executeInceptionGraph(byte[] graphDef, Tensor image) {
        try (Graph g = new Graph()) {
            g.importGraphDef(graphDef);
            try (Session s = new Session(g);
                 Tensor result = s.runner().feed
                         ("DecodeJpeg/contents", image).fetch("softmax").run().get(0)) {
                final long[] rshape = result.shape();
                if (result.numDimensions() != 2 || rshape[0] != 1) {
                    throw new RuntimeException(
                            String.format(
                                    "Expected model to produce a [1 N] shaped tensor where N is the number of labels, instead it produced one with shape %s",
                                    Arrays.toString(rshape)));
                }
                int nlabels = (int) rshape[1];
                float[][] floats = (float[][]) result.copyTo(new float[1][nlabels]);
                return floats[0];
            }
        }
    }

    private static int maxIndex(float[] probabilities) {
        int best = 0;
        for (int i = 1; i < probabilities.length; ++i) {
            if (probabilities[i] > probabilities[best]) {
                best = i;
            }
        }
        return best;
    }


    /**
     * @param textImage
     * @param language
     * @param engineMode
     * @param pageSegMode
     * @return
     */
    private String findOCR(BufferedImage textImage, String language,
                           int engineMode, int pageSegMode
    ) {
        try {
            logger.info("OCR-start");
            double start = System.currentTimeMillis();
            if (textImage == null) {
                logger.info("textImage = null");
                return null;
            }

            logger.info("OCR-ImageRead");
            ITesseract instance = new Tesseract();
            logger.info("Instance Tesseract");
            instance.setDatapath(DATA_PATH);//设置训练库
            logger.info("DATA_PATH = {}", DATA_PATH);
            instance.setLanguage(language);
            instance.setOcrEngineMode(engineMode);
            instance.setPageSegMode(pageSegMode);
            logger.info("page mode = {}", 3);

            String result = null;
            textImage = convertImage(textImage);
            result = instance.doOCR(textImage);
            logger.info("Finish doOCR");
            double end = System.currentTimeMillis();
            logger.info("Time:" + (end - start) / 1000 + " s");
            logger.info("Result:{}", result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Tesseract error", e);
            return "发生未知错误";
        }
    }

    //对图片进行处理 - 提高识别度
    private BufferedImage convertImage(BufferedImage image) throws Exception {
        //按指定宽高创建一个图像副本
        //image = ImageHelper.getSubImage(image, 0, 0, image.getWidth(), image.getHeight());
        //图像转换成灰度的简单方法 - 黑白处理
//        image = ImageHelper.convertImageToGrayscale(image);
        //锐化
//        image = ImageHelper.convertImageToBinary(image);
        //图像缩放 - 放大n倍图像
//        image = ImageHelper.getScaledInstance(image, image.getWidth() * 3, image.getHeight() * 3);

        //去除歪斜
//        image = rotateImage(image);

        //降噪
        image = ssn(image);

        //二值化
//        image = ImageHelper.convertImageToBinary(image);

        return image;
    }

    private BufferedImage ssn(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        int[] pix = new int[w * h];
        image.getRGB(0, 0, w, h, pix, 0, w);
        int[] newpix = snnFiltering(pix, w, h);
        image.setRGB(0, 0, w, h, newpix, 0, w);
        return image;
    }

    /**
     * 对称近邻均值滤波
     *
     * @param pix 像素矩阵数组
     * @param w   矩阵的宽
     * @param h   矩阵的高
     * @return 处理后的数组
     */
    private int[] snnFiltering(int[] pix, int w, int h) {
        int[] newpix = new int[w * h];
        int n = 9;
        int temp, i1, i2, sum;
        int[] temp1 = new int[n];
        int[] temp2 = new int[n / 2];
        ColorModel cm = ColorModel.getRGBdefault();
        int r = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (x != 0 && x != w - 1 && y != 0 && y != h - 1) {
                    sum = 0;
                    temp1[0] = cm.getRed(pix[x - 1 + (y - 1) * w]);
                    temp1[1] = cm.getRed(pix[x + (y - 1) * w]);
                    temp1[2] = cm.getRed(pix[x + 1 + (y - 1) * w]);
                    temp1[3] = cm.getRed(pix[x - 1 + (y) * w]);
                    temp1[4] = cm.getRed(pix[x + (y) * w]);
                    temp1[5] = cm.getRed(pix[x + 1 + (y) * w]);
                    temp1[6] = cm.getRed(pix[x - 1 + (y + 1) * w]);
                    temp1[7] = cm.getRed(pix[x + (y + 1) * w]);
                    temp1[8] = cm.getRed(pix[x + 1 + (y + 1) * w]);
                    for (int k = 0; k < n / 2; k++) {
                        i1 = Math.abs(temp1[n / 2] - temp1[k]);
                        i2 = Math.abs(temp1[n / 2] - temp1[n - k - 1]);
                        temp2[k] = i1 < i2 ? temp1[k] : temp1[n - k - 1];  //选择最接近原像素值的一个邻近像素
                        sum = sum + temp2[k];
                    }
                    r = sum / (n / 2);
                    //System.out.println("pix:" + temp1[4] + "  r:" + r);
                    newpix[y * w + x] = 255 << 24 | r << 16 | r << 8 | r;
                } else {
                    newpix[y * w + x] = pix[y * w + x];
                }
            }
        }
        return newpix;
    }

    private BufferedImage rotateImage(BufferedImage textImg) {
        ImageDeskew id = new ImageDeskew(textImg);
        double imageSkewAngle = id.getSkewAngle(); // determine skew angle
        if ((imageSkewAngle > MINIMUM_DESKEW_THRESHOLD || imageSkewAngle < -(MINIMUM_DESKEW_THRESHOLD))) {
            textImg = ImageHelper.rotateImage(textImg, -imageSkewAngle); // deskew image
        }
        return textImg;

    }

    public List<Rectangle> getetSegmentedRegions(BufferedImage image) throws Exception {
        logger.info("getSegmentedRegions at given TessPageIteratorLevel");
        int level = ITessAPI.TessPageIteratorLevel.RIL_SYMBOL;
        logger.info("PageIteratorLevel: " + Utils.getConstantName(level, ITessAPI.TessPageIteratorLevel.class));
        ITesseract instance = new Tesseract();
        List<Rectangle> result = instance.getSegmentedRegions(image, level);
        for (int i = 0; i < result.size(); i++) {
            Rectangle rect = result.get(i);
            logger.info(String.format("Box[%d]: x=%d, y=%d, w=%d, h=%d", i, rect.x, rect.y, rect.width, rect.height));
        }

        return result;
    }

    public String doOCR_File_Rectangle(BufferedImage image, Rectangle rect) throws Exception {
        logger.info("doOCR on a BMP image with bounding rectangle");
        ITesseract instance = new Tesseract();

        //设置语言库
        instance.setDatapath(DATA_PATH);
        instance.setLanguage("eng");
        //划定区域
        // x,y是以左上角为原点，width和height是以xy为基础
        String result = instance.doOCR(image, rect);
        logger.info(result);
        return result;
    }


}
