package com.mee.manage.vo;

import lombok.Data;

import java.util.List;

@Data
public class LineVo{

    List<WordsVo> Words;

    double MaxHeight;

    double MinTop;

}
