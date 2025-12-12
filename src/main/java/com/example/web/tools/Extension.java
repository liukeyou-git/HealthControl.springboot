package com.example.web.tools;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.web.tools.dto.PagedInput;
import com.example.web.tools.dto.PagedResult;

/**
 * 扩展方法
 */
public class Extension {

    /**
     * 判断字符串是否为空或者空串
     */
    public static boolean isNotNullOrEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    /**
     * 判断字符串是否是空或者空串
     */
    public static boolean isNullOrEmpty(String str) {
        return !isNotNullOrEmpty(str);
    }

    /**
     * 判断Id是否存在
     */
    public static boolean isNullOrZero(Integer value) {
        return value == null || value == 0;
    }

    /**
     * 拷贝一个列表到另外一个列表
     */
    public static <T> List<T> copyBeanList(List<?> resourceList, Class<T> target) {
        List<T> targetList = new LinkedList<>();
        if (resourceList.isEmpty()) {
            return targetList;
        }
        resourceList.forEach(e -> {
            T o = null;
            try {
                o = target.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
            org.springframework.beans.BeanUtils.copyProperties(e, o);
            targetList.add(o);
        });
        return targetList;
    }

    /**
     * 获取当前的账号信息 根据token
     */
    public static String getTokenInfo(String token, String key) {
        token = token.replace("Bearer ", "");
        DecodedJWT jwt = JWTUtils.getTokenInfo(token);
        return jwt.getClaims().get(key).asString();
    }

    /**
     * LocalDateTime戳转换成字符串
     */
    public static String LocalDateTimeConvertString(LocalDateTime localDateTime, String format) {
        if (localDateTime == null) {
            return "";
        }
        if (format == null) {
            format = format = "yyyy-MM-dd HH:mm:ss";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

        return localDateTime.format(formatter);
    }

    /**
     * 保留4位小数
     */
    public static double ToFixed4(double value) {
        return Math.round(value * Math.pow(10, 4)) / Math.pow(10, 4);
    }

    /**
     * 保留2位小数
     */
    public static double ToFixed2(double value) {
        return Math.round(value * Math.pow(10, 2)) / Math.pow(10, 2);
    }

    /**
     * 去掉小数
     */
    public static double ToFixed0(double value) {
        return Math.round(value * Math.pow(10, 0)) / Math.pow(10, 0);
    }

    /**
     * 构建分页结果
     */
    public static <T> PagedResult PagedResultBuild(List<T> items, PagedInput input) {
        int totalCount = items.size();
        items = items.subList((int) ((input.getPage() - 1) * input.getLimit()),
                (int) Math.min(input.getPage() * input.getLimit(), items.size()));
        // 返回一个分页结构给前端
        return PagedResult.GetInstance(items, (long) totalCount);
    }

    /**
     * 生成一个单号
     * 
     * @return
     */
    public static String GenerateOrderNumber() {
        // 获取当前时间
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeStr = dateFormat.format(now);
        // 生成随机数，例如：123456
        int randomNum = (int) (Math.random() * 900000 + 100000);
        // 拼接单号
        String orderNumber = timeStr + randomNum;
        return orderNumber;
    }

    /**
     * 生成一个UUID
     */
    public static String GenerateUUID() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

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
     * 对字符串进行MD5加密
     * 
     * @param input 需要加密的字符串
     * @return 返回32位小写MD5加密后的字符串
     */
    public static String MD5Encrypt(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return "MD5" + hexString.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }

    /**
     * 判断密码是否是MD5加密了
     */
    public static boolean IsMD5Encrypt(String password) {
        return password.startsWith("MD5");
    }

}
