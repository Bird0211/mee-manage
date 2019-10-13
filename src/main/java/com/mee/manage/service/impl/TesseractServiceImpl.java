package com.mee.manage.service.impl;

import com.alibaba.fastjson.JSON;
import com.mee.manage.service.ITesseractService;
import com.mee.manage.util.FileUtil;
import com.mee.manage.vo.InvoiceVo;
import com.mee.manage.vo.TextOverlayVo;
import com.mee.manage.vo.Tools;
import com.mee.manage.vo.WordsVo;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.Word;
import net.sourceforge.tess4j.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
public class TesseractServiceImpl implements ITesseractService {

    private static final Logger logger = LoggerFactory.getLogger(ITesseractService.class);

    private final static String DATA_PATH = "/data/ocr/tessdata";


    public String tassOcr(MultipartFile[] files, String language) {
        String ocrSpaceResult = null;
        try {

            int engineMode = ITessAPI.TessOcrEngineMode.OEM_LSTM_ONLY;
            int pageSegMode = ITessAPI.TessPageSegMode.PSM_AUTO;//PSM_SINGLE_BLOCK

            logger.info(" engineMode = {} , pageSegMode = {} ", engineMode, pageSegMode);
            List<BufferedImage> textImages = FileUtil.files2BufferedImg(files);
            ocrSpaceResult = findOCR(textImages, language, engineMode, pageSegMode);
            logger.info(ocrSpaceResult);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return ocrSpaceResult;

    }

    /**
     * @param textImages
     * @param language
     * @param engineMode
     * @param pageSegMode
     * @return
     */
    private String findOCR(List<BufferedImage> textImages, String language,
                           int engineMode, int pageSegMode) {

        String result = null;

        try {
            logger.info("OCR-start");
            double start = System.currentTimeMillis();
            if (textImages == null || textImages.size() <= 0) {
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
            logger.info("page mode = {}", pageSegMode);

//            instance.setTessVariable("tessedit_char_whitelist","0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");


            List<TextOverlayVo> textOverlayVos = getTextOverlaies(instance, textImages);
            TextOverlayVo textOverlayVo = FileUtil.mergeTextOver(textOverlayVos);
            if (textOverlayVo == null) {
                result = null;
            } else {
                InvoiceVo invoice = textOverlayVo.getInVoice();
                result = invoice.toString();
            }
            logger.info("Finish doOCR");
            double end = System.currentTimeMillis();
            logger.info("Time:" + (end - start) / 1000 + " s");
            logger.info("Result:{}", result);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Tesseract error", e);
        }
        return result;
    }

    private List<TextOverlayVo> getTextOverlaies(ITesseract instance, List<BufferedImage> textImages) {
        if (instance == null || textImages == null)
            return null;

        List<TextOverlayVo> textOverlayVos = new ArrayList<>();
        for (BufferedImage textImage : textImages) {
            try {
                TextOverlayVo textOverlayVo = getTextOverLay(instance, textImage);
                if (textOverlayVo != null)
                    textOverlayVos.add(textOverlayVo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return textOverlayVos;
    }


    private TextOverlayVo getTextOverLay(ITesseract instance, BufferedImage textImage) throws Exception {
        if (instance == null || textImage == null)
            return null;

        logger.info("GetWords start");
        textImage = Tools.convertImage(textImage);
        int level = ITessAPI.TessPageIteratorLevel.RIL_WORD;
        List<Word> words = instance.getWords(textImage, level);
        logger.info("word = {}", JSON.toJSONString(words));

        List<WordsVo> wordsVos = transferWords(words);
        if (wordsVos == null || wordsVos.size() <= 0)
            return null;

        logger.info("word = {}", JSON.toJSONString(wordsVos));

        logger.info("GetWords End");
        Collections.sort(wordsVos);
        TextOverlayVo textOverlayVo = new TextOverlayVo();
        textOverlayVo.setAllWords(wordsVos);
        return textOverlayVo;
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


    private WordsVo transferWord(Word word) {
        if (word == null) {
            return null;
        }

        Rectangle rectangle = word.getBoundingBox();
        WordsVo wordsVo = new WordsVo();
        wordsVo.setTop(rectangle.getY());
        wordsVo.setLeft(rectangle.getX());
        wordsVo.setHeight(rectangle.getHeight());
        wordsVo.setWidth(rectangle.getWidth());
        wordsVo.setWordText(word.getText());
        return wordsVo;
    }

    private List<WordsVo> transferWords(List<Word> words) {
        if (words == null || words.size() <= 0) {
            return null;
        }

        List<WordsVo> wordsVos = new ArrayList<>();
        for (Word word : words) {
            wordsVos.add(transferWord(word));
        }
        return wordsVos;
    }

}
