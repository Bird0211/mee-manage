package com.mee.manage.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.mee.manage.config.Config;
import com.mee.manage.config.MeeConfig;
import com.mee.manage.service.*;
import com.mee.manage.util.*;
import com.mee.manage.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@EnableConfigurationProperties(Config.class)
public class OCRSeviceImpl implements IOCRService {

    private static final Logger logger = LoggerFactory.getLogger(IOCRService.class);

    private byte[] graphDef;

    private List<String> labels;

    @Autowired
    private Config config;

    @Autowired
    private IAuthenticationService authService;

    @Autowired
    IPDFService pdfService;

    @Autowired
    ITesseractService tesseractService;

    @Autowired
    IProductsService productsService;

    @Autowired
    IConfigurationService configurationService;


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
    public InvoiceVo textOCR(MultipartFile[] file, String language) {
        Integer ocrMode = configurationService.getIntValue(Config.OCR_MODE);
        InvoiceVo ocrSpaceResult = null;
        if(ocrMode == null || ocrMode == 1) {
            ocrSpaceResult = tesseractService.tassOcr(file,language);
        }else if(ocrMode == null || ocrMode == 2) {
            ocrSpaceResult = ocrSpfiace(file, language);
        }
        return ocrSpaceResult;
    }

    @Override
    public MeeResult updateInventory(InventoryRequest request,AuthenticationVo auth) {
        MeeResult result = new MeeResult();

        if(auth == null || auth.isEmpty()) {
            result.setStatusCode(StatusCode.AUTH_FAIL.getCode());
            return result;
        }


        Long time = DateUtil.getCurrentTime();
        String token = authService.getMeeToken(auth.getBizId());

        String url = config.getStockIntake();
        Map<String,Object> params = new HashMap<>();
        params.put("bizid",auth.getBizId());
        params.put("time",time);
        params.put("nonce",auth.getNonce());
        params.put("intake",JSON.toJSONString(request));
        params.put("sign", MeeConfig.getMeeSign(auth.getBizId(),null,time,token,auth.getNonce()));

        String invoiceResult = JoddHttpUtils.sendPost(url,params);
        logger.info(invoiceResult);
        List<MeeInvoiceResponse> invoiceResponse = JSON.parseArray(invoiceResult,MeeInvoiceResponse.class);
        if(invoiceResponse == null || invoiceResponse.isEmpty()) {
            result.setStatusCode(StatusCode.FAIL.getCode());
        }

        MeeInvoiceResponse meeInvoice = invoiceResponse.get(0);
        if (meeInvoice.getResult().equals("SUCCESS")) {
            List<ComparePricesVo> comparePricesVos = productsService.getComparePrice(request.getProducts(),auth.getBizId());
            result.setData(comparePricesVos);
            result.setStatusCode(StatusCode.SUCCESS.getCode());
        } else {
            result.setData(meeInvoice.getError());
            result.setStatusCode(StatusCode.FAIL.getCode());
        }
        return result;
    }



    private InvoiceVo ocrSpfiace(MultipartFile[] files, String language) {
        if(files == null || files.length <= 0)
            return null;
        InvoiceVo invoice = null;
        try {
            List<String> base64Imgs = FileUtil.files2Base64PdfImgs(files);
//            List<BufferedImage> textImages = FileUtil.files2BufferedImg(files);

            List<TextOverlayVo> textOverlayVos = getTextOvers(base64Imgs,language);
            invoice = getInvoiceResult(textOverlayVos);
            /*if (invoice != null)
                result = invoice.toString();*/
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("ocrSpfiace error", e);
        }
        return invoice;
    }

    private List<TextOverlayVo> getTextOvers(MultipartFile[] files,String language) throws Exception{
        List<TextOverlayVo> textOverlayVos = null;

        if(files != null && files.length > 0) {
            textOverlayVos = Lists.newArrayList();
            for (MultipartFile file: files) {
                TextOverlayVo textOver = this.getTextOver(file,language);
                if(textOver != null)
                    textOverlayVos.add(textOver);
            }
        }
        return textOverlayVos;
    }

    private List<TextOverlayVo> getTextOvers(List<String> base64Imgs,String language) throws Exception{
        List<TextOverlayVo> textOverlayVos = null;

        if(base64Imgs != null && base64Imgs.size() > 0) {
            textOverlayVos = Lists.newArrayList();
            for (String base64Img: base64Imgs) {
                TextOverlayVo textOver = this.getTextOver(base64Img,language);
                if(textOver != null)
                    textOverlayVos.add(textOver);
            }
        }
        return textOverlayVos;
    }

    private TextOverlayVo getTextOver(MultipartFile file, String language) throws Exception {
        TextOverlayVo textVo = null;
        String apiKey = config.getOcrApiKey();
        String url = config.getOcrUrl();
        Map<String, Object> parames = new HashMap<>();
        parames.put("apikey", apiKey);
        parames.put("file", FileUtil.multipartFileToFile(file));
        parames.put("language", language);
        parames.put("isOverlayRequired", true);
        parames.put("filetype", "JPG");
        parames.put("detectOrientation", true);
        parames.put("isCreateSearchablePdf", false);
        parames.put("isSearchablePdfHideTextLayer", false);
        parames.put("scale", true);
        parames.put("isTable", true);
//            parames.put("OCREngine",2);

        String result = JoddHttpUtils.sendPost(url, parames);
        logger.info(result);
        OcrSpaceResponseVo vo = JSON.parseObject(result, OcrSpaceResponseVo.class);
        if (vo.getOCRExitCode() == 1) {
            List<ParsedResultsVo> parsedResultsVos = vo.getParsedResults();
            if (parsedResultsVos != null && parsedResultsVos.size() > 0) {
                ParsedResultsVo parsedResultsVo = parsedResultsVos.get(0);
//                    result = parsedResultsVo.getParsedText();
//                    logger.info(result);
//                    List<ProductsVo> productsVos = getProducts(result);
//                    logger.info(JSON.toJSONString(productsVos));
                textVo = parsedResultsVo.getTextOverlay();
            }
        } else {
            logger.info("OCR error!", vo.getErrorMessage());
        }

        return textVo;
    }

    private TextOverlayVo getTextOver(String base64Image,String language) throws Exception {
        TextOverlayVo textVo = null;
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
//            parames.put("OCREngine",2);

        String result = JoddHttpUtils.sendPost(url, parames);
        logger.info(result);
        OcrSpaceResponseVo vo = JSON.parseObject(result, OcrSpaceResponseVo.class);
        if (vo.getOCRExitCode() == 1) {
            List<ParsedResultsVo> parsedResultsVos = vo.getParsedResults();
            if (parsedResultsVos != null && parsedResultsVos.size() > 0) {
                ParsedResultsVo parsedResultsVo = parsedResultsVos.get(0);
//                    result = parsedResultsVo.getParsedText();
//                    logger.info(result);
//                    List<ProductsVo> productsVos = getProducts(result);
//                    logger.info(JSON.toJSONString(productsVos));
                textVo = parsedResultsVo.getTextOverlay();
            }
        } else {
            logger.info("OCR error!", vo.getErrorMessage());
        }

        return textVo;
    }

    private InvoiceVo getInvoiceResult(List<TextOverlayVo> textVos) {
        if(textVos == null || textVos.isEmpty())
            return null;


        TextOverlayVo mergeText = FileUtil.mergeTextOver(textVos);
        if (mergeText == null)
            return null;

        return mergeText.getInVoice();
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

}
