package com.example.web.tools;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DateExtension {

    /**
     * 根据日期得到今天是星期几
     */
    public static String GetWeek(LocalDate localDate) {
        return localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.CHINESE);
    }

    /**
     * 判断时间是否存在交集
     */
    public static boolean IsOverlap(LocalDateTime beginPeriodTime1, LocalDateTime endPeriodTime1,
                                    LocalDateTime beginPeriodTime2, LocalDateTime endPeriodTime2) {
        return beginPeriodTime1.isBefore(endPeriodTime2) && endPeriodTime1.isAfter(beginPeriodTime2);
    }

    /**
     * 合并日期和时间
     */
    public static LocalDateTime CombineLocalDateTime(String ymd, String time) {
        return LocalDateTime.parse(ymd + " " + time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * A时间段是否是B时间段的子集
     */
    public static boolean IsSubset(LocalDateTime beginPeriodTime1, LocalDateTime endPeriodTime1,
                                   LocalDateTime beginPeriodTime2, LocalDateTime endPeriodTime2) {
        return beginPeriodTime1.isAfter(beginPeriodTime2) && endPeriodTime1.isBefore(endPeriodTime2);
    }

    /**
     * 是否是包括子集和端点
     * 检查第一个时间段是否在第二个时间段内（包括端点相等的情况）
     */
    public static boolean IsSubsetOrEqual(LocalDateTime beginPeriodTime1, LocalDateTime endPeriodTime1,
                                          LocalDateTime beginPeriodTime2, LocalDateTime endPeriodTime2) {
        return (beginPeriodTime1.isEqual(beginPeriodTime2) || beginPeriodTime1.isAfter(beginPeriodTime2))
                && (endPeriodTime1.isEqual(endPeriodTime2) || endPeriodTime1.isBefore(endPeriodTime2));
    }
    /**
     * 把字符串转换成LocalDate列表
     */
    public static List<LocalDateTime> StringToList(String str) {
        if (str==null||str.length()==0) return new ArrayList<>();

        List<LocalDateTime> list = new ArrayList<>();
        for (String s : str.split(",")) {
            LocalDateTime localDateTime = LocalDateTime.parse(s + " 00:00:00",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            list.add(localDateTime);
        }
        return list;
    }
}
