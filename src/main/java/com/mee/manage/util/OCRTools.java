package com.mee.manage.util;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.util.ImageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class OCRTools {

    private static final Logger logger = LoggerFactory.getLogger(OCRTools.class);


    private final static String DATA_PATH = "/data/ocr/tessdata";

    /**
     *
     * @param inputStream 图片路径
     * @param ZH_CN 是否使用中文训练库,true-是
     * @return 识别结果
     */
    public static String FindOCR(InputStream inputStream, boolean ZH_CN) {
        try {
            logger.info("OCR-start");
            double start = System.currentTimeMillis();
            BufferedImage textImage = ImageIO.read(inputStream);
            logger.info("OCR-ImageRead");
            ITesseract instance = new Tesseract();
            logger.info("Instance Tesseract");
            instance.setDatapath(DATA_PATH);//设置训练库
            if (ZH_CN)
                instance.setLanguage("chi_sim");//中文识别

            String result = null;
            BufferedImage converImg = convertImage(textImage);
            result = instance.doOCR(converImg);
            logger.info("Finish doOCR");
            double end = System.currentTimeMillis();
            logger.info("耗时"+(end-start)/1000+" s");
            logger.info("Result:{}",result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Tesseract error",e);
            return "发生未知错误";
        }
    }

    //对图片进行处理 - 提高识别度
    public static BufferedImage convertImage(BufferedImage image) throws Exception {
        logger.info("Start - convertImage");
        //按指定宽高创建一个图像副本
        //image = ImageHelper.getSubImage(image, 0, 0, image.getWidth(), image.getHeight());
        //图像转换成灰度的简单方法 - 黑白处理
        image = ImageHelper.convertImageToGrayscale(image);
        //图像缩放 - 放大n倍图像
        image = ImageHelper.getScaledInstance(image, image.getWidth() * 3, image.getHeight() * 3);
        logger.info("END - converImage");
        return image;
    }

}
