package com.mee.manage.vo;

import lombok.Data;

import java.util.List;

@Data
public class OcrSpaceResponseVo {

    List<ParsedResultsVo> ParsedResults;

    int OCRExitCode;

    boolean IsErroredOnProcessing;

    String ErrorMessage;

    String ErrorDetails;

    String SearchablePDFURL;

    long ProcessingTimeInMilliseconds;

}
