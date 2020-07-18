package com.mee.manage.vo.Yiyun;

import lombok.Data;

@Data
public class YiyunlogisticInfo {

    YiyunlogisticInfo() {

    }

    YiyunlogisticInfo(String logisticInfo) {
        if(logisticInfo != null) {
            String[] infos = logisticInfo.split("\\|");
            if(infos != null && infos.length >= 3) {
                this.logisticId = infos[0];
                this.date = infos[1];
                this.operator = infos[2];
            } 
        }
    }

    String logisticId;

    String date;

    String operator;
    
}