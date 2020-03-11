package com.mee.manage.controller;

import java.util.List;

import com.mee.manage.service.IDataMiningService;
import com.mee.manage.service.IOCRService;
import com.mee.manage.util.StatusCode;
import com.mee.manage.vo.AuthenticationVo;
import com.mee.manage.vo.InventoryRequest;
import com.mee.manage.vo.InvoiceVo;
import com.mee.manage.vo.MatchingRequest;
import com.mee.manage.vo.MeeProductVo;
import com.mee.manage.vo.MeeResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@ResponseBody
@RequestMapping("/api")
@CrossOrigin
public class OCRController {


    private static final Logger logger = LoggerFactory.getLogger(OCRController.class);

    @Autowired
    IOCRService iocrService;

    @Autowired
    IDataMiningService dataMiningService;

    @RequestMapping(value = "/imageRecognition", method = RequestMethod.POST)
    public MeeResult imageRecognition(@RequestParam(value = "file") MultipartFile file) {
        String GRAPH_PATH = "/data/ocr/train";
//      String GRAPH_PATH = "/Users/bb_bird/work/OCR/train";
        MeeResult meeResult = new MeeResult();
        try {
            iocrService.loadTrainingData(GRAPH_PATH);
            String result = iocrService.imageRecognition(file.getBytes());
            meeResult.setData(result);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        }catch (Exception ex) {
            logger.error("OCR error: {} ",ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }

    @RequestMapping(value = "/textocr", method = RequestMethod.POST)
    public MeeResult ocr(@RequestParam MultipartFile[] file) {
        MeeResult meeResult = new MeeResult();
        try {
            InvoiceVo result = iocrService.textOCR(file,"eng+chi_sim");
            meeResult.setData(result);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        }catch (Exception ex) {
            logger.error("OCR error: {} ",ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }

    @RequestMapping(value = "/matching", method = RequestMethod.POST)
    public MeeResult matchingInvoice(@RequestBody MatchingRequest request,
                                     @RequestHeader ("bizId") String bizId) {
        MeeResult meeResult = new MeeResult();
        try {
            List<MeeProductVo> result = dataMiningService.classification(request,bizId);
            meeResult.setData(result);
            meeResult.setStatusCode(StatusCode.SUCCESS.getCode());

        }catch (Exception ex) {
            logger.error("OCR error: {} ",ex);
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }

    @RequestMapping(value = "/inventory/update", method = RequestMethod.POST)
    public MeeResult updateInventory(@RequestBody InventoryRequest request,
                                     @RequestHeader ("bizId") String bizId,
                                     @RequestHeader ("time") String time,
                                     @RequestHeader ("nonce") String nonce,
                                     @RequestHeader ("sign") String sign) {
        MeeResult meeResult = null;
        try {
            AuthenticationVo auth = new AuthenticationVo(bizId,null,time,nonce,sign);
            meeResult = iocrService.updateInventory(request,auth);
        } catch (Exception ex) {
            logger.error("OCR error: {} ",ex);
            meeResult = new MeeResult();
            meeResult.setStatusCode(StatusCode.FAIL.getCode());
        }
        return meeResult;
    }
}
