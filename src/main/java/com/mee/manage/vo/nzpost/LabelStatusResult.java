package com.mee.manage.vo.nzpost;

import java.util.List;

import lombok.Data;

@Data
public class LabelStatusResult {
    
    String consignmentId;

    List<LabelTrack> tracks;

}