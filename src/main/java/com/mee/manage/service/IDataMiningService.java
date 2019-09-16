package com.mee.manage.service;

import com.mee.manage.vo.MatchingRequest;
import org.jdmp.core.dataset.ListDataSet;

import java.io.IOException;
import java.util.List;

public interface IDataMiningService {

    List<String> classification(MatchingRequest request) throws IOException;

}
