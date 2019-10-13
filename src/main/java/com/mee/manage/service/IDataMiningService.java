package com.mee.manage.service;

import com.mee.manage.vo.MatchResult;
import com.mee.manage.vo.MatchingRequest;
import com.mee.manage.vo.MeeProductVo;
import org.jdmp.core.dataset.ListDataSet;

import java.io.IOException;
import java.util.List;

public interface IDataMiningService {

    List<MeeProductVo> classification(MatchingRequest request,String bizId) throws IOException;

}
