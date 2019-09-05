package com.mee.manage.service;

import com.mee.manage.vo.MatchingRequest;
import org.jdmp.core.dataset.ListDataSet;

import java.io.IOException;

public interface IDataMiningService {

    String classification(MatchingRequest request) throws IOException;

}
