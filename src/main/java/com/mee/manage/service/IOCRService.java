package com.mee.manage.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public interface IOCRService {

    //加载训练
    void loadTrainingData(String path);

    //识别图片
    String imageRecognition(byte[] imageBytes);

    String textOCR(MultipartFile inputStream, String language);

}
