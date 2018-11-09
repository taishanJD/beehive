package com.quarkdata.yunpan.api.model.common;


public class Constants {

    public static String SYSTEM = "SYSTEM";
    public static String SUCCESS = "SUCCESS";
    public static String FAIL = "FAIL";

    public static final String ONESHARE_REDIS_PREFIX = "os";
    public static final String ONESHARE_REDIS_DELIMITER = ":";
    public static final String ONESHARE_REDIS_USER_INFO_VO = "userInfoVO";
    /*public static final String ONESHARE_REDIS_GROUP = "group";
    public static final String ONESHARE_REDIS_DEPARTMENT = "department";
    public static final String ONESHARE_REDIS_ROLE = "role";*/
    public static final String ONESHARE_REDIS_INC = "inc";

    public static final String ONESHARE_REDIS_TOKEN = "token";

    public static final String DOCUMENT_FOLDER_TYPE = "dir"; //文件夹在docment表的document_type存的名称

    public static final String LOGO_SUFFIX = "jpg|png";//企业logo格式
    public static final Integer LOGO_SIZE = 100;//企业Logo大小,单位kb
    public static final Integer LOGO_WIDTH = 200;//企业logo宽度200
    public static final Integer LOGO_HEIGHT = 50;//企业logo高度50

    //配置密码规则
    public static final boolean UPERCASE = true;//大写
    public static final boolean LOWERCASE = true;//小写
    public static final boolean HAVE_NUMBER = true;//数字
    public static final boolean SPECIAL_CHARACER = true;//特殊字符
    public static final String PWD_MIN_LENGTH = "6";//密码长度
    /**
     * 忘记密码--重置密码-用于邮件中的url
     */
    public static final String FORGOT_PASSWORD_RESET = "1";
    /**
     * 忘记密码--重置密码-链接有效时长
     */
    public static final String RESET_PWD_URL_VALID_TIME = "1";

    public static final String USER_SOURCE_AD = "IAM";
    public static final String USER_SOURCE_OUTSIDE_ACCOUNT = "oneshare";//云盘创建的外部账号标志
    public static final String USER_SOURCE_GENERATE_ACCOUNT = "oneshare";//云盘创建的共享账号source

    public static final String TYPE_USER = "0";
    public static final String TYPE_GROUP = "1";
    public static final String TYPE_DEPARTMENT = "2";

    /**
     * session key userId
     */
    public static final String SESSION_ATTRIBUTE_USERID = "userId";

    /**
     * session key incId
     */
    public static final String SESSION_ATTRIBUTE_INCID = "incId";

    /**
     * 客户端代号
     */
    public static final String CLIENT_ANDROID = "android";
    public static final String CLIENT_IOS = "ios";
}
