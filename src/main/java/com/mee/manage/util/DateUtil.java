package com.mee.manage.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by bshn on 2016/9/25.
 */
public class DateUtil {
    public static final String formatPattern = "yyyy-MM-dd";

    public static final String formatPattern_Short = "yyyyMMdd";

    public static final String formatPattern_Full = "yyyy-MM-dd hh:mm:ss";

    public static final String formatPattern_UserAuth = "yyyy-MM-dd'T'hh:mm:ss";

    public static final String formatPattern_ch = "yyyy年MM月dd日";

    /**
     * 获取当前日期
     *
     * @return
     */
    public static String getCurrentDate() {
        SimpleDateFormat format = new SimpleDateFormat(formatPattern);
        return format.format(new Date());
    }

    public static Long getCurrentTime() {
        return new Date().getTime();
    }

    /**
     * 获取制定毫秒数之前的日期
     *
     * @param timeDiff
     * @return
     */
    public static String getDesignatedDate(long timeDiff) {
        SimpleDateFormat format = new SimpleDateFormat(formatPattern);
        long nowTime = System.currentTimeMillis();
        long designTime = nowTime - timeDiff;
        return format.format(designTime);
    }

    /**
     * 获取前几天的日期
     */
    public static String getPrefixDateFormat(int day) {
        Calendar cal = Calendar.getInstance();
        day = 0 - day;
        cal.add(Calendar.DATE, day);   // int amount   代表天数
        Date datNew = cal.getTime();
        SimpleDateFormat format = new SimpleDateFormat(formatPattern);
        return format.format(datNew);
    }

    /**
     * 获取前几后的日期
     */
    public static String getSuffixDateFormat(int day) {
        Date datNew = getSuffixDate(day);
        SimpleDateFormat format = new SimpleDateFormat(formatPattern);
        return format.format(datNew);
    }

    /**
     * 获取几个月之后的日期
     * @param date
     * @param month
     * @return
     */
    public static Date getSuffixMonth(Date date,int month){
        if(month < 1)
            return date;

        Calendar calendar=Calendar.getInstance();//创建实例
        calendar.setTime(date);
        calendar.add(Calendar.MONTH,month);//三个月后的日期
        return calendar.getTime();//三个月后的日期（Date类型）
    }

    /**
     * 获取几天之后的日期
     * @param day
     * @return
     */
    public static Date getSuffixDate(int day) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, day);   // int
        return cal.getTime();
    }

    /**
     * 获取指定日期，之后几天的日期
     * @param date
     * @param day
     * @return
     */
    public static Date getSuffixDate(Date date,int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, day);   // int
        return cal.getTime();
    }

    /**
     * 获取几秒之后的日期
     * @param seconds
     * @return
     */
    public static Date getSuffixSecond(int seconds){

        return getSuffixSecond(new Date(),seconds);
    }

    /**
     * 获取指定日期，之后几秒的日期
     * @param date
     * @param seconds
     * @return
     */
    public static Date getSuffixSecond(Date date,int seconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, seconds);   // int
        return cal.getTime();
    }

    public static Date getSuffixHour(Date date,int hour) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hour);   // int
        return cal.getTime();
    }

    public static Date getPrefixMinute(int minute) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, -minute);   // int
        return cal.getTime();
    }

    public static Date stringToSuffixDate(String str,String format,int day){
        return getSuffixDate(stringToDate(str,format),day);
    }

    public static Date getPrefixDate(int day) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -day);   // int
        return cal.getTime();
    }

    /**
     * 获取多少秒后的日期
     *
     * @param second
     * @return
     */
    public static Date getFixTime(int second) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, second);
        return cal.getTime();
    }

    /**
     * 日期转换成字符串
     *
     * @param date
     * @return
     */
    public static String dateToString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(formatPattern);
        return format.format(date);
    }

    public static String dateToStringFormat(Date date,String formatPattern){
        SimpleDateFormat format = new SimpleDateFormat(formatPattern);
        return format.format(date);
    }

    /**
     * 字符串转换日期
     *
     * @param str
     * @return
     */
    public static Date stringToDate(String str) {
        //str =  " 2008-07-10 19:20:00 " 格式
        return stringToDate(str, formatPattern);
    }

    public static Date stringToDate(String str, String formatPattern) {
        SimpleDateFormat format = new SimpleDateFormat(formatPattern);
        if (str != null && !str.equals("")) {
            try {
                return format.parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println("日期：" + dateToStringFormat(new Date(),formatPattern_ch));

        DateUtil dt = new DateUtil();
        Integer num = 0;
        dt.test1(num);
        System.out.println("Num:" + num);
    }

    public void test1(Integer num){
        num = 1;

    }

}
