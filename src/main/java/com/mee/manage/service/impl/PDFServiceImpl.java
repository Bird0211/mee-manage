package com.mee.manage.service.impl;

import com.mee.manage.service.IPDFService;
import com.mee.manage.util.PDFUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

@Service
public class PDFServiceImpl implements IPDFService {


    @Override
    public List<String> pDF2base64Image(InputStream pdfInputStream) throws IOException {
        List<BufferedImage> images = PDFUtil.readPDF(pdfInputStream);
        List<String> base64Imgs = null;
        if (images != null) {
            base64Imgs = new ArrayList<>();
            for (BufferedImage image : images) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", outputStream);
                base64Imgs.add(Base64.getEncoder().encodeToString(outputStream.toByteArray()));
            }
        }

        return base64Imgs;
    }

    @Override
    public String readPDFText(InputStream pdfInputStream) throws IOException {
        if(pdfInputStream == null)
            return null;

        PDDocument document = PDDocument.load(pdfInputStream);
        PDFTextStripper pdfStripper = new PDFTextStripper();

        String text = pdfStripper.getText(document);
        document.close();
        return text;
    }

    public void pdfPage2Img(InputStream pdfInputStream){
        try {
            List<BufferedImage> readPDF = PDFUtil.readPDF(pdfInputStream);
            int i = 0;
            for (BufferedImage pdf : readPDF) {
                pdfPage2Img(pdf,"/Users/bb_bird/Downloads/"+(i++)+".jpeg","JPEG");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * pdf页转换成图片
     * @param saveFileName
     * @throws IOException
     */
    public void pdfPage2Img(BufferedImage img,String saveFileName,String imgType) throws IOException {
        Iterator<ImageWriter> it = ImageIO.getImageWritersBySuffix(imgType);
        ImageWriter writer = (ImageWriter) it.next();
        ImageOutputStream imageout = ImageIO.createImageOutputStream(new FileOutputStream(saveFileName));
        writer.setOutput(imageout);
        writer.write(new IIOImage(img, null, null));
    }

}
