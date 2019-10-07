package com.mee.manage.controller;

import com.mee.manage.service.IPDFService;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.MeeResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@ResponseBody
@RequestMapping("/api")
@CrossOrigin
public class PDFController {

    private static final Logger logger = LoggerFactory.getLogger(PDFController.class);

    @Autowired
    IPDFService pdfService;

    @RequestMapping(value = "/pdf/text", method = RequestMethod.POST)
    public MeeResult pdfText(@RequestParam(value = "file") MultipartFile file) {
        MeeResult meeResult = new MeeResult();
        try {
            String text = pdfService.readPDFText(file.getInputStream());
            meeResult.setData(text);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
        } catch (Exception ex) {
            logger.error("PDF error: {} ",ex);
            meeResult = new MeeResult();
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }

    @RequestMapping(value = "/pdf/img", method = RequestMethod.POST)
    public MeeResult pdfImg(@RequestParam(value = "file") MultipartFile file) {
        MeeResult meeResult = new MeeResult();
        try {
            pdfService.pdfPage2Img(file.getInputStream());
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());
        } catch (Exception ex) {
            logger.error("PDF error: {} ",ex);
            meeResult = new MeeResult();
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }
}
