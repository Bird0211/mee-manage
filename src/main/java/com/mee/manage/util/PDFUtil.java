package com.mee.manage.util;

import com.mee.manage.service.ITesseractService;
import jodd.util.RandomString;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PDFUtil {

    private static final Logger logger = LoggerFactory.getLogger(PDFUtil.class);


    public static int DEFAULT_DPI = 700;

    public static List<BufferedImage> readPDF(InputStream pdfInputStream) throws IOException {
        if(pdfInputStream == null)
            return null;

        PDDocument document = PDDocument.load(pdfInputStream);
        List<BufferedImage> bufferedImageList = new ArrayList<>();

        PDFRenderer pdfRenderer = new PDFRenderer(document);
        for (int page = 0; page < document.getNumberOfPages(); page++) {
            BufferedImage image = pdfRenderer.renderImageWithDPI(page, DEFAULT_DPI, ImageType.RGB);
//            String imageName = "/data/"+RandomStringUtils.random(6)+"myimage.jpg";
//            logger.info("image path = {}",imageName);
//            ImageIO.write(image, "JPEG", new File(imageName));
            bufferedImageList.add(image);
        }

        document.close();
        return bufferedImageList;
    }


}
