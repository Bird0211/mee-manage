package com.mee.manage.vo;

import com.alibaba.druid.filter.config.ConfigTools;

public class Tools {

    public static void main(String[] args) {

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

    }
}
