package com.mee.manage.vo;

import lombok.Data;

import java.util.List;

@Data
public class KdnVo {

    String EBusinessID;

    boolean Success;

    String LogisticCode;

    List<KdnShipperCodeVo> ShipperCode;

}
