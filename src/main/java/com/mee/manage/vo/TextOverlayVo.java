package com.mee.manage.vo;

import lombok.Data;

import java.util.List;

@Data
public class TextOverlayVo {

    List<LineVo> Lines;

    boolean HasOverlay;

    String Message;


}
