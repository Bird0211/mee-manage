package com.mee.manage.vo;

import lombok.Data;

@Data
public class ParsedResultsVo {

    TextOverlayVo TextOverlay;

    int FileParseExitCode;

    String ParsedText;

    String ErrorMessage;

    String ErrorDetails;

}
