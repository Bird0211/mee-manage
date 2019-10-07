package com.mee.manage.service;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface IPDFService {

    List<BufferedImage> readPDF(InputStream pdfInputStream) throws IOException;

    List<String> pDF2base64Image(InputStream pdfInputStream) throws IOException;

    String readPDFText(InputStream pdfInputStream) throws IOException;

    void pdfPage2Img(InputStream pdfInputStream) throws IOException;
}
