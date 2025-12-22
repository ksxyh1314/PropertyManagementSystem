package com.property.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtil {

    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_TIME = "HH:mm:ss";
    public static final String PATTERN_MONTH = "yyyy-MM";
    public static final String PATTERN_YEAR = "yyyy";

    /**
     * 格式化日期
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 格式化日期（默认格式）
     */
    public static String formatDate(Date date) {
        return format(date, PATTERN_DATE);
    }

    /**
     * 格式化日期时间
     */
    public static String formatDateTime(Date date) {
        return format(date, PATTERN_DATETIME);
    }

    /**
     * 解析日期字符串
     */
    public static Date parse(String dateStr, String pattern) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException("日期解析失败: " + dateStr, e);
        }
    }

    /**
     * 解析日期字符串（默认格式）
     */
    public static Date parseDate(String dateStr) {
        return parse(dateStr, PATTERN_DATE);
    }

    /**
     * 获取当前日期
     */
    public static Date now() {
        return new Date();
    }

    /**
     * 获取当前日期字符串
     */
    public static String nowStr() {
        return formatDate(now());
    }

    /**
     * 获取当前日期时间字符串
     */
    public static String nowDateTimeStr() {
        return formatDateTime(now());
    }

    /**
     * 计算两个日期之间的天数
     */
    public static int daysBetween(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        long diff = endDate.getTime() - startDate.getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    /**
     * 添加天数
     */
    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }

    /**
     * 添加月份
     */
    public static Date addMonths(Date date, int months) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        return cal.getTime();
    }

    /**
     * 获取月份的第一天
     */
    public static Date getFirstDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    /**
     * 获取月份的最后一天
     */
    public static Date getLastDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    /**
     * 判断是否过期
     */
    public static boolean isExpired(Date date) {
        if (date == null) {
            return false;
        }
        return date.before(new Date());
    }

    /**
     * 判断是否在指定天数内
     */
    public static boolean isWithinDays(Date date, int days) {
        if (date == null) {
            return false;
        }
        Date futureDate = addDays(new Date(), days);
        return date.before(futureDate) && date.after(new Date());
    }
}
