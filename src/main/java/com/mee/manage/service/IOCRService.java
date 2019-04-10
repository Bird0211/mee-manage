package com.mee.manage.service;

import java.io.File;

public interface IOCRService {

    //加载训练
    void loadTrainingData(String path);

    //识别图片
    String imageRecognition(byte[] imageBytes);

}
