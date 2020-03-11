package com.mee.manage.service;

import com.mee.manage.vo.SettleFeeVo;
import com.mee.manage.vo.SettleVo;

import java.util.List;

public interface ISettleService {

    boolean checkParams(SettleVo settleVo);

    List<SettleFeeVo> getSettleFee(SettleVo settleVo);

}
