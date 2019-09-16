package com.mee.manage.util;

public class StrUtil {
    // 将字母转换成数字_1
    public static String t1(String input) {
        String reg = "[a-zA-Z]";
        StringBuffer strBuf = new StringBuffer();
        input = input.toLowerCase();
        if (null != input && !"".equals(input)) {
            for (char c : input.toCharArray()) {
                if (String.valueOf(c).matches(reg)) {
                    strBuf.append(c - 96);
                } else {
                    strBuf.append(c);
                }
            }
            return strBuf.toString();
        } else {
            return input;
        }
    }

    // 将字母转换成数字
    public static long letterToNum(String input) {
        StringBuffer sb = new StringBuffer();
        for (byte b : input.getBytes()) {
            sb.append(Math.abs(b-96));
        }
        return Long.parseLong(sb.toString());
    }

    // 将数字转换成字母
    public static String numToLetter(String input) {
        StringBuffer sb = new StringBuffer();
        for (byte b : input.getBytes()) {
            sb.append((char) (b + 48));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String str = "G-2-1-4 Healtheries Milk Bites - Honey 50pc";
        String replaceStr = str.replaceAll("[-,\\s*]","");
        System.out.println(str);
        System.out.println(replaceStr);

        long num = letterToNum(str);

        System.out.println(num);
    }

}
