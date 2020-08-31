package com.mee.manage.vo.Yiyun;

import lombok.Data;

@Data
public class YiyunlogisticInfo {

    YiyunlogisticInfo() {

    }

    YiyunlogisticInfo(String logisticInfo) {
        if(logisticInfo != null) {
            String[] infos = logisticInfo.split("\\|");
            if (infos != null && infos.length == 3) {
                this.logisticId = infos[0].trim();
                this.date = infos[1];
                this.operator = infos[2];
            } else if (infos != null && infos.length == 4) {
                this.logisticCom = infos[0];
                this.logisticId = infos[1].trim();
                this.date = infos[2];
                this.operator = infos[3];
            } 
        }
    }

    String logisticCom;

    String logisticId;

    String date;

    String operator;
    
}