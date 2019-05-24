package com.mee.manage.vo;

import lombok.Data;

import java.util.List;

@Data
public class ParsedResultsVo {

    TextOverlayVo TextOverlay;

    int FileParseExitCode;

    String ParsedText;

    String ErrorMessage;

    String ErrorDetails;

}
