package com.mee.manage.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

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

    public static String getNumber(String str) {
        if(str == null || str.isEmpty())
            return "0";
        // 需要取整数和小数的字符串
        // 控制正则表达式的匹配行为的参数(小数)
        Pattern p = Pattern.compile("(\\d+\\.\\d+)");
        //Matcher类的构造方法也是私有的,不能随意创建,只能通过Pattern.matcher(CharSequence input)方法得到该类的实例.
        Matcher m = p.matcher(str);
        //m.find用来判断该字符串中是否含有与"(\\d+\\.\\d+)"相匹配的子串
        if (m.find()) {
            //如果有相匹配的,则判断是否为null操作
            //group()中的参数：0表示匹配整个正则，1表示匹配第一个括号的正则,2表示匹配第二个正则,在这只有一个括号,即1和0是一样的
            str = m.group(1) == null ? "" : m.group(1);
        } else {
            //如果匹配不到小数，就进行整数匹配
            p = Pattern.compile("(\\d+)");
            m = p.matcher(str);
            if (m.find()) {
                //如果有整数相匹配
                str = m.group(1) == null ? "" : m.group(1);
            } else {
                //如果没有小数和整数相匹配,即字符串中没有整数和小数，就设为空
                str = "";
            }
        }
        return str;
    }

    public static BigDecimal readStrAsBigDecimalAndCheckFormat(String str) {
        DecimalFormat format = new DecimalFormat();
        format.setParseBigDecimal(true);
        ParsePosition position = new ParsePosition(0);
        BigDecimal parse = (BigDecimal) format.parse(str, position);

        if (str.length() == position.getIndex()) {
            return parse;
        }
        return null;
    }

     /**
	 * 将url参数转换成map
	 * @param param aa=11&bb=22&cc=33
	 * @return
	 */
	public static Map<String, Object> getUrlParams(String param) {
		Map<String, Object> map = new HashMap<String, Object>(0);
		if (StringUtils.isBlank(param)) {
			return map;
		}
		String[] params = param.split("&");
		for (int i = 0; i < params.length; i++) {
			String[] p = params[i].split("=");
			if (p.length == 2) {
				map.put(p[0], p[1]);
			}
		}
		return map;
	}
 
	/**
	 * 将map转换成url
	 * @param map
	 * @return
	 */
	public static String getUrlParamsByMap(Map<String, Object> map) {
		if (map == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			sb.append(entry.getKey() + "=" + entry.getValue());
			sb.append("&");
		}
		String s = sb.toString();
		if (s.endsWith("&")) {
			s = StringUtils.substringBeforeLast(s, "&");
		}
		return s;
	}

    public static void main(String[] args) {

        String str = "2.000 Unit(s)";
        System.out.println(getNumber(str));
    }

}
