package com.mee.manage.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mee.manage.mapper.IDataStatisticsMapper;
import com.mee.manage.po.DataStatistics;
import com.mee.manage.service.IDataStatisticsService;
import com.mee.manage.service.IDataTopService;
import com.mee.manage.service.IOrderService;
import com.mee.manage.vo.Yiyun.YiyunErrorVo;
import com.mee.manage.vo.Yiyun.YiyunNoShipVo;
import com.mee.manage.vo.Yiyun.YiyunTopProduct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataStatisticsServiceImpl extends ServiceImpl<IDataStatisticsMapper, DataStatistics> 
    implements IDataStatisticsService {

	@Autowired
	IOrderService orderService;

	@Autowired
	IDataTopService dataTopService;

	@Override
	public DataStatistics getErrorOrder(Long bizId) {
		return getById(bizId);
	}

	@Override
	public boolean saveErrorOrder(DataStatistics data) {
        boolean flag = updateById(data);
        if(!flag)
            flag = save(data);
		return flag;
	}

	@Override
	public boolean saveStaticOrder(Long bizId) {
		YiyunNoShipVo noShipData = orderService.getNoShipData(bizId);
		YiyunErrorVo errorData = orderService.getErrorData(bizId);
		boolean flag = false;
		DataStatistics statistics = new DataStatistics();
		statistics.setBizId(bizId);

		if(noShipData != null)  {
			statistics.setNoShip(noShipData.getNoShipOrder());
		}

		if(errorData != null) {
			statistics.setError(errorData.getErrorOrder());
		}
		flag = saveErrorOrder(statistics);
		return flag;
	}

	@Override
	public boolean saveTopProduct(Long bizId) {
		List<YiyunTopProduct> topProducts = orderService.getTopProductsByDays(bizId,30,10);
		boolean flag = false;
		if(topProducts != null) {
			flag = dataTopService.saveDataTop(bizId, topProducts);
		}
		return flag;
	}
      
}