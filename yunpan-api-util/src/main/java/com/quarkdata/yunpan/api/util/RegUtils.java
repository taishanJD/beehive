package com.quarkdata.yunpan.api.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yanyq1129@thundersoft.com on 2018/10/8.
 * 正则表达式工具类
 */
public class RegUtils {

    /**
     * 邮件正则
     */
    public static final String REGEX_MAIL =
            "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";

    /**
     * 一个或多个汉字
     */
    public static final String REGEX_CHINESE = "^[\u0391-\uFFE5]+$";

    /**
     * 用户名（字母开头 + 数字/字母/下划线）
     */
    public static final String REGEX_USERNAME = "^[A-Za-z][A-Za-z1-9_-]+$";

    /**
     * 手机号，+86 13120000288
     */
    public static final String REGEX_MOBILE = "^((\\+[0-9]{2})|([0-9]{2}))?(\\s)?[1]\\d{10}$";

    /**
     * 判断是否匹配给定正则，true：是；false：否
     *
     * @Author skh
     * @Date 2017年11月21日
     *
     * @param checkStr String
     * @return boolean
     */
    public static boolean isMatch(String checkStr, String regEx) {

        Pattern pattern = Pattern.compile(regEx);

        Matcher matcher = pattern.matcher(checkStr);

        // 字符串是否与正则表达式相匹配
        boolean result = matcher.matches();

        return result;
    }
}
