package com.quarkdata.yunpan.api.dal.rest.onespace;

import com.quarkdata.yunpan.api.util.PropertiesUtils;

/**
 * Created by yanyq1129@thundersoft.com on 2018/8/6.
 */
public class OneSpaceApiConstants {

    public static String get_ios_certificate = "/api/onespace/getServerDEREcodeCA";
    public interface ClientFlag {

        static String CLIENT_ANDROID = "1";
        static String CLIENT_IOS = "2";
    }

    /**
     * one space api base path
     */
    static String oneSpaceApiBasePath = PropertiesUtils.prop.get("oneSpaceApiBasePath");

    /**
     * 获取客户端版本信息
     */
    static String client_version_list = "/api/getEMMLists";

    /**
     * 获取客户端版本信息
     */
    static String client_download_path = "/api/down_load_client_version";

    /**
     * 获取客户端版本信息
     */
    static String client_download_path_default = "/api/ios/yunpanClient";

    /**
     * 验证token
     */
    public static final String VERIFY = "/verify_st";
}
