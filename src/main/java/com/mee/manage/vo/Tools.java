package com.mee.manage.vo;

import com.alibaba.druid.filter.config.ConfigTools;
import com.github.houbb.word.checker.core.impl.EnWordChecker;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {

    public static void main(String[] args) {
/*
//        String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIS39+jnjLSkSL0ig/IJsjwHnZ9gagoWxDzaoTOhds01oymDQsBP+0hlGs6QKDFWk4BSWp0xpvt30weZhuz04TUCAwEAAQ==";

        String password = "mvpWSjWQ2itv3KgYAZqSLV94WXe6l5sDoc5a5CTlTQBkRKSGf4yXy9ZuAohrzseVeGvZKpdB9VvdkojtAm0n1A==";
        try {
//            String publicKey = ConfigTools.getPublicKey(null);
//            String pwd = ConfigTools.encrypt("397SKa8j");
            String pwd = ConfigTools.decrypt(password);
            System.out.printf(pwd);

        } catch (Exception e) {
            e.printStackTrace();
        }

        */
        String word = "Descrip@on";
        String target = "description";
/*
        String correct = EnWordChecker.getInstance().correct(word.toLowerCase());

        System.out.println("word = "+word + ",correct = "+correct);*/

        float similarity = SimilarityStrUtil.getSimilarityRatio(target.toLowerCase().trim(),word.toLowerCase().trim());
        System.out.println(similarity);

    }

    /**
     * 匹配是否为数字
     * @param str 可能为中文，也可能是-19162431.1254，不使用BigDecimal的话，变成-1.91624311254E7
     * @return
     * @author yutao
     * @date 2016年11月14日下午7:41:22
     */
    public static boolean isNumeric(String str) {
        // 该正则表达式可以匹配所有的数字 包括负数
        Pattern pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
}
