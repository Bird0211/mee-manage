package com.mee.manage.service;

import com.mee.manage.vo.AuthenticationVo;
import com.mee.manage.vo.InventoryRequest;
import com.mee.manage.vo.InvoiceVo;
import com.mee.manage.vo.MeeResult;

import org.springframework.web.multipart.MultipartFile;


public interface IOCRService {

    //加载训练
    void loadTrainingData(String path);

    //识别图片
    String imageRecognition(byte[] imageBytes);

    InvoiceVo textOCR(MultipartFile[] inputStream, String language);

    MeeResult updateInventory(InventoryRequest request, AuthenticationVo auth);

}
