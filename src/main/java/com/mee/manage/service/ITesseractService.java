package com.mee.manage.service;

import org.springframework.web.multipart.MultipartFile;

public interface ITesseractService {

    String tassOcr(MultipartFile[] file, String language);

}
