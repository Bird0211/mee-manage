package com.mee.manage.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PDFUtil {

    public static List<BufferedImage> readPDF(InputStream pdfInputStream) throws IOException {
        if(pdfInputStream == null)
            return null;

        PDDocument document = PDDocument.load(pdfInputStream);
        List<BufferedImage> bufferedImageList = new ArrayList<>();

        PDFRenderer pdfRenderer = new PDFRenderer(document);
        for (int page = 0; page < document.getNumberOfPages(); page++){
            BufferedImage image = pdfRenderer.renderImageWithDPI(page,350, ImageType.RGB);

            bufferedImageList.add(image);
        }

        document.close();
        return bufferedImageList;
    }
}
