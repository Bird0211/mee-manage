package com.mee.manage.util;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PDFUtil {

    // private static final Logger logger = LoggerFactory.getLogger(PDFUtil.class);


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
