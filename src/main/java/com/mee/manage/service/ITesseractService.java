package com.mee.manage.service;

import com.mee.manage.vo.InvoiceVo;
import org.springframework.web.multipart.MultipartFile;

public interface ITesseractService {

    InvoiceVo tassOcr(MultipartFile[] file, String language);

}
