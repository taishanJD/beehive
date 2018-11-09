package com.quarkdata.yunpan.api.model.common;

public class Messages {

    public static int SUCCESS_CODE = 0;
    public static String SUCCESS_MSG = "success";

    public static int MISSING_INPUT_CODE = 10000;
    public static String MISSING_INPUT_MSG = "missing required input params";

    public static int API_ERROR_CODE = 90000;
    public static String API_ERROR_MSG = "接口繁忙，请稍后重试";

    public static final String CONTAINS_SENSITIVE_WORD_MSG = "文档中包含敏感词";
    public static final int CONTAINS_SENSITIVE_WORD_CODE = 4444;

    public static int API_INSERT_ERROR_CODE = 90001;
    public static String API_INSERT_ERROR_MSG = "数据库插入异常";


    public static final int API_AUTHENTICATION_FAILED_CODE = 40001;
    public static final String API_AUTHENTICATION_FAILED_MSG = "token不存在，请重新登录";

    public static final int API_ADD_LINK_FAILED_CODE = 60001;
    public static final String API_ADD_LINK_FAILED_MSG = "新建外链失败";

    public static final int API_DEL_LINK_FAILED_CODE = 60002;
    public static final String API_DEL_LINK_FAILED_MSG = "删除外链失败";

    public static final int API_DEL_SHARE_FAILED_CODE = 60003;
    public static final String API_DEL_SHARE_FAILED_MSG = "删除与我共享失败";

    public static final int API_ADD_SHARE_FAILED_CODE = 60004;
    public static final String API_ADD_SHARE_FAILED_MSG = "新建共享失败";

    public static final int API_SHARE_LIST_FAILED_CODE = 60005;
    public static final String API_SHARE_LIST_FAILED_MSG = "获取与我共享列表失败";

    public static final int API_VIEW_COUNT_CODE = 60006;
    public static final String API_VIEW_COUNT_MSG = "增加浏览次数失败";

    public static final int API_LINK_IS_NULL_CODE = 60007;
    public static final String API_LINK_IS_NULL_MSG = "该链接文件不存在";

    public static final int API_LINK_IS_EXPIRE_CODE = 60008;
    public static final String API_LINK_IS_EXPIRE_MSG = "文件已过期失效";

    public static final int API_LINK_LOGIN_CODE = 60009;
    public static final String API_LINK_LOGIN_MSG = "访问该链接需要登录";

    public static final int API_LINK_PERMISSION_CODE = 60010;
    public static final String API_LINK_PERMISSION_MSG = "无访问权限";

    public static final int API_LINK_EXCEPTION_CODE = 60011;
    public static final String API_LINK_EXCEPTION_MSG = "访问链接异常";

    public static final int API_LINK_DOC_CODE = 60012;
    public static final String API_LINK_DOC_MSG = "通过链接访问文件失败";

    public static final int API_GET_MY_LINK_CODE = 60013;
    public static final String API_GET_MY_LINK_MSG = "获取链接列表失败";

    public static final int API_LINK_LEGAL_CODE = 60014;
    public static final String API_LINK_LEGAL_MSG = "下载链接非法";

    public static final int API_SHARE_LEGAL_CODE = 60015;
    public static final String API_SHARE_LEGAL_MSG = "只能分享个人文件";

    public static final int API_LINK_OWNER_CODE = 60016;
    public static final String API_LINK_OWNER_MSG = "获取链接创建者是失败";

    public static final int API_DOCUMENT_EXITS_CODE = 60017;
    public static final String API_DOCUMENT_EXITS_MSG = "文件或文件夹已存在，操作失败!";

    public static final int API_DOCUMENT_SHARESUB_CODE = 60020;
    public static final String API_DOCUMENT_SHARESUB_MSG = "获取分享文件的子文件!";

    public static final int API_DOCUMENT_EXTERNALSUB_CODE = 60021;
    public static final String API_DOCUMENT_EXTERNALSUB_MSG = "无法获取外链文件夹中的文件!";

    public static final int TOKEN_OUT_OF_TIME_CODE = 80000;
    public static final String TOKEN_OUT_OF_TIME_MSG = "身份验证过期,请重新登录";

    public static final int API_GET_COLLECT_CODE = 81000;
    public static final String API_GET_COLLECT_MSG = "获取收藏列表失败";

    public static final int API_DEL_COLLECT_CODE = 81001;
    public static final String API_DEL_COLLECT_MSG = "取消收藏失败";

    public static final int API_ADD_COLLECT_CODE = 81002;
    public static final String API_ADD_COLLECT_MSG = "添加收藏失败";

    public static final int HAS_PARENT_DOC_DELETED_CODE = 70000;
    public static final String HAS_PARENT_DOC_DELETED_MSG = "所选doc存在父目录也被删除的情况";

    public static final int TAG_NAME_EXIST_CODE = 70001;
    public static final String TAG_NAME_EXIST_MSG = "所填tag名称已经存在";

    public static final int API_ADD_ARCHIVAL_FAILED_CODE = 66000;
    public static final String API_ADD_ARCHIVAL_FAILED_MSG = "新建归档失败:用户权限不够";

    public static final int API_ADD_ManualARCHIVAL_FAILED_CODE = 66001;
    public static final String API_ADD_ManualARCHIVAL_FAILED_MSG = "手动新建归档失败";

    public static final int API_ADD_AutoARCHIVAL_FAILED_CODE = 66002;
    public static final String API_ADD_AutoARCHIVAL_FAILED_MSG = "归档失败";

    public static final int API_GET_MESSAGE_LIST_CODE = 66003;
    public static final String API_GET_MESSAGE_LIST_MSG = "消息查询失败";

    public static final int API_UPDATE_MESSAGE_TYPE_CODE = 66004;
    public static final String API_UPDATE_MESSAGE_TYPE_MSG = "修改用户接受消息状态失败";

    public static final int API_UPDATE_MESSAGE_IS_READ_CODE = 66005;
    public static final String API_UPDATE_MESSAGE_IS_READ_MSG = "修改未读消息失败";

    public static final int API_FULLTEXT_SEARCH_CODE = 67000;
    public static final String API_FULLTEXT_SEARCH_MSG = "全文检索失败";

    public static final int API_FULLTEXT_NOINDEX_CODE = 67001;
    public static final String API_FULLTEXT_NOINDEX_MSG = "Es索引名不存在";

    public static final int API_FULLTEXT_CREATE_INDEX_CODE = 67002;
    public static final String API_FULLTEXT_CREATE_INDEX_MSG = "该组织已有索引存在存在";

    public static final int API_FULLTEXT_CREATE_NEWINDEX_CODE = 67003;
    public static final String API_FULLTEXT_CREATE_NEWINDEX_MSG = "新建组织索引失败";

    public static final String EXPORT_LOG_FAIL_MSG = "导出日志报表失败";

    public static final String UPDATE_INCINFO_AND_INCCONFIG = "修改企业信息和云盘配置";
    public static final String UPDATE_INCINFO_AND_INCCONFIG_FAIL_MSG = "修改企业信息和云盘配置失败";

    public static final String GET_ADMIN_LIST_FAIL_MSG = "获取管理员列表失败";

    public static final String GET_USAGE_FAIL_MSG = "获取云盘使用信息失败";

    public static final String GET_MY_SPACE_FAIL_MSG = "获取个人空间使用情况失败";

    public static final String GET_USER_RANK_MSG = "获取用户使用排行失败";

    public static final String GET_LOG_FAIL_MSG = "查询日志列表失败";

    public static final String GET_INCINFO_AND_INCCONFIG_FAIL_MSG = "获取企业信息和云盘配置信息失败";

    public static final String ADD_THE_SYSTEM_ADMINISTRATOR_FAIL_MSG = "添加系统管理员失败";

    public static final String ADD_THE_ORGANIZATION_ADMINISTRATOR_FAIL_MSG = "添加组织管理员失败";

    public static final String DELETE_THE_SYSTEM_ADMINISTRATOR_FAIL_MSG = "删除系统管理员失败";

    public static final String DELETE_THE_ORGANIZATION_ADMINISTRATOR_FAIL_MSG = "删除组织管理员失败";

    public static final String EDIT_THE_SYSTEM_ADMINISTRATOR_FAIL_MSG = "编辑系统管理员失败";

    public static final String EDIT_THE_ORGANIZATION_ADMINISTRATOR_FAIL_MSG = "删除组织管理员失败";

    public static final String FIND_USER_FAIL_MSG = "根据用户名查询用户失败";

    public static final String FIND_CURRENT_USER_FAIL_MSG = "查询当前登录用户失败";

    public static final String FIND_GROUP_FAIL_MSG = "查询用户组信息失败";

    public static final String FIND_DEPARTMENT_FAIL_MSG = "查询部门信息失败";

    public static final String GET_VRTRIFY_CODE_FAIL_MSG = "获取验证码失败";


    public static final int API_FILE_UPLOAD_SUFFIX_CODE = 30001;
    public static final String API_FILE_UPLOAD_SUFFIX_MSG = "文件格式不正确";
    public static final int API_FILE_UPLOAD_SIZE_CODE = 30002;
    public static final String API_FILE_UPLOAD_SIZE_MSG = "文件超过限制大小";

    public static final int API_FILE_UPLOAD_NOTFOUND_CODE = 30404;
    public static final String API_FILE_UPLOAD_NOTFOUND_MSG = "文件不存在";
    public static final int API_FILE_UPLOAD_FAIL_CODE = 30505;
    public static final String API_FILE_UPLOAD_FAIL_MSG = "文件上传失败";

    public static final int API_FILE_UPLOAD_PARAM_CODE = 30506;
    public static final String API_FILE_UPLOAD_PARAM_MSG = "参数错误";
    public static final int API_OBJECT_NOTFOUND_CODE = 30604;
    public static final String API_OBJECT_NOTFOUND_MSG = "对象不存在";

    public static final int API_FILE_UPLOAD_LW_CODE = 30003;
    public static final String API_FILE_UPLOAD_LW_MSG = "图片长宽不符合要求";

    public static final int API_EXCEPTION_CODE = 90500;
    public static final String API_EXCEPTION_MSG = "内部异常,请联系管理员";

    public static final int API_AUTHEXCEPTION_CODE = 90403;
    public static final String API_AUTHEXCEPTION_MSG = "文档操作权限不足，请联系管理员";
    public static final int API_AUTHEXCEPTION_CODE_SOURCE = 90404;
    public static final String API_AUTHEXCEPTION_MSG_SOURCE = "源文档操作权限不足，请联系管理员";
    public static final int API_AUTHEXCEPTION_CODE_TARGET = 90405;
    public static final String API_AUTHEXCEPTION_MSG_TARGET = "目标文档操作权限不足，请联系管理员";

    //忘记密码
    public static final int FORGET_PASSWORD_EXCEPTION_CODE = 20000;
    public static final String VERIFY_CODE_ERROR_MSG = "验证码错误";
    public static final String EMAIL_FORMAT_ERROR_MSG = "邮箱格式错误";
    public static final String USERNAEM_NOT_EXIST_MSG = "用户不存在";
    public static final String USERNAEM_NOT_ACTIVE_MSG = "用户不是激活状态";

    public static final int EMAIL_INVALID_CODE = 20001;
    public static final String EMAIL_INVALID_MSG = "无效的邮箱地址";


    public static final int SERVICE_INTERNAL_EXCEPTION_CODE = 20002;
    public static final String SERVICE_INTERNAL_EXCEPTION_MSG = "服务器异常";

    public static final int USER_SIGN_EXCEPTION_CODE = 20003;
    public static final String USER_SIGN_EXCEPTION_MSG = "签名验证失败";

    public static final int RESET_PASSWORD_URL_INVALID_CODE = 20004;
    public static final String RESET_PASSWORD_URL_INVALID_MSG = "重置密码链接已失效";

    public static final int NOT_FORGOT_PASSWORD_CODE = 20005;
    public static final String NOT_FORGOT_PASSWORD_MSG = "非忘记密码---重置密码";

    public static int USER_ALREADY_EXIST_CODE = 30000;
    public static final String USER_ALREADY_EXIST_MSG = "用户名已存在!";
    public static int UPDATE_PASSWORD_FAIL_CODE = 20000;
    public static final String UPDATE_PASSWORD_FAIL_MSG = "修改密码失败!";
    public static final String ORIGINAL_PASSWORD_ERROR_MSG = "原密码错误!";
    public static final String USER_STATUS_ERROR_MSG = "用户不是激活状态!";
    public static int EXTERNAL_ACCOUNT_NOT_EXIST_CODE = 30001;
    public static final String EXTERNAL_ACCOUNT_NOT_EXIST_MSG = "该外部共享空间不存在外部账号!";

    public static final int USERNAME_ERROR_CODE = 20006;
    public static final String USERNAME_ERROR_MSG = "用户名错误";

    public static final int UPDATE_DOCUMENT_TYPE_FAIL_CODE = 500;
    public static final String UPDATE_DOCUMENT_TYPE_FAIL_MSG = "修改文件类型失败";

    public static final int API_NO_SYSTEM_ADMIN_PERMISSION_CODE = 90000;
    public static final String API_NO_SYSTEM_ADMIN_PERMISSION_MSG = "权限不足,请联系系统管理员!";

    public static int FILE_IS_DELETE_CODE = 70004;
    public static final String FILE_IS_DELETE_MSG = "该文档已被删除,请刷新重试";

    public static int FILE_NOT_EXIST_CODE = 70007;
    public static final String FILE_NOT_EXIST_MSG = "文档不存在";

    public static int FILE_VERISON_NOT_EXIST_CODE = 70017;
    public static final String FILE_VERSION_NOT_EXIST_MSG = "文档版本不存在";

    public static int EXTERNAL_USER_EXPIRE_CODE = 70008;
    public static final String EXTERNAL_USER_EXPIRE_MSG = "该账号已过期";

    public static int FILE_NOT_SHARE_TO_YOU_CODE = 70009;
    public static final String FILE_NOT_SHARE_TO_YOU_MSG = "对方已取消共享或您已移出该共享记录,请刷新重试";

    public static int RENAME_MISSING_DOCUMENT_NAME_CODE = 70010;
    public static final String RENAME_MISSING_DOCUMENT_NAME_MSG = "重命名,文件名不能为空";

    public static final int GET_INC_INFO_FAIL_CODE = 70020;
    public static final String GET_INC_INFO_FAIL_MSG = "获取企业信息失败";

    public static final int NO_SPACE_FAIL_CODE = 70021;
    public static final String NO_SPACE_FAIL_MSG = "服务器磁盘空间已满";

    public static final int LOGIN_USERNAME_PASSWORD_MISMATCH_CODE = 2;
    public static final String LOGIN_USERNAME_PASSWORD_MISMATCH_MSG = "用户名或密码错误";

    public static final int LOGIN_USER_LOCKED_CODE = 100014;
    public static final String LOGIN_USER_LOCKED_MSG = "账号被锁定,请联系管理员";

    public static final int NO_ENOUGH_PERSONAL_SPACE_CODE = 70033;
    public static final String NO_ENOUGH_PERSONAL_SPACE_MSG = "个人空间不足,请联系管理员";

    public static final int NO_ENOUGH_INC_SPACE_CODE = 70044;
    public static final String NO_ENOUGH_INC_SPACE_MSG = "组织空间不足,请联系管理员";

    public static final int NO_SYSTEM_ADMIN_PERMISSION_CODE = 70077;
    public static final String NO_SYSTEM_ADMIN_PERMISSION_MSG = "您不是系统管理员,或已被移出系统管理员权限";

    public static final int GET_CER_FAIL_CODE = 70080;
    public static final String GET_CER_FAIL_MSG = "获取安全证书失败";

    public static final int LOCK_DOCUMENT_TYPE_ERROR_CODE = 70090;
    public static final String LOCK_DOCUMENT_TYPE_ERROR_MSG = "文档类型不支持锁定或解锁操作";
    public static final int UNLOCK_DOCUMENT_NO_AUTH_ERROR_CODE = 70091;
    public static final String UNLOCK_DOCUMENT_NO_AUTH_ERROR_MSG = "没有解锁权限";
    public static final int LOCK_DOCUMENT_STATUS_ERROR_CODE = 70092;
    public static final String LOCK_DOCUMENT_STATUS_ERROR_MSG = "文档已被锁定";
    public static final int UNLOCK_DOCUMENT_STATUS_ERROR_CODE = 70093;
    public static final String UNLOCK_DOCUMENT_STATUS_ERROR_MSG = "文档已解锁";
    public static final int DOCUMENT_STATUS_LOCK_CODE = 70094;
    public static final String DOCUMENT_STATUS_LOCK_MSG = "文档已被锁定,请在解锁后操作";

    public static final int REGISTER_PUSH_ERROR_CODE = 68000;
    public static final String REGISTER_PUSH_ERROR_MSG = "获取推送id失败";
    public static final int REGISTER_PUSH_HIATUS_CODE = 68001;
    public static final String REGISTER_PUSH_HIATUS_MSG = "参数错误,参数全部为空";

    public static final int REGISTER_IOS_ERROR_CODE = 68003;
    public static final String REGISTER_IOS_ERROR_MSG = "ios上传token失败";
    public static final int REGISTER_IOS_ERROR_NULL_CODE = 68004;
    public static final String REGISTER_IOS_ERROR_NULL_MSG = "ios上传token为空";
    public static final int BEELIVE_PUSH_MESSAGE_ERROR_CODE = 68005;
    public static final String BEELIVE_PUSH_MESSAGE_ERROR_MSG = "推送消息失败";
    public static final int GET_NOT_READ_LIST_CODE = 68006;
    public static final String GET_NOT_READ_LIST_MSG = "获取当前用户未读消息失败";

    public static final int CREATE_GENERATE_ACCOUNT_ERROR_CODE = 70100;
    public static final String CREATE_GENERATE_ACCOUNT_ERROR_MSG = "创建外部账户失败";

    public interface Link {
        int MISSING_FETCH_CODE = 70200;
        String MISSING_FETCH_CODE_MSG = "请输入提取码";
        Integer FETCH_CODE_ERROR_CODE = 70201;
        String FETCH_CODE_ERROR_MSG = "提取码不正确";
        int NOT_ALLOW_DOWNLOAD_CODE = 70202;
        String NOT_ALLOW_DOWNLOAD_NO_MSG = "该资源不允许下载";
        int NOT_ALLOW_PREVIEW_CODE = 70203;
        String NOT_ALLOW_PREVIEW_MSG = "该资源不允许预览";
        int EMAIL_FORMAT_NOT_CORRECT_CODE = 70206;
        String EMAIL_FORMAT_NOT_CORRECT_MSG = "邮箱格式不正确";
        int TELEPHONE_FORMAT_NOT_CORRECT_CODE = 70207;
        String TELEPHONE_FORMAT_NOT_CORRECT_MSG = "手机号格式不正确";
        int NOT_EXIST_CODE = 70208;
        String NOT_EXIST_MSG = "外链不存在";
        int MISSING_EXPIRE_TIME_CODE = 70209;
        String MISSING_EXPIRE_TIME_MSG = "失效时间格式不正确";
    }

    public interface Archive {
        int PERMISSION_ILLEGAL_CODE = 70204;
        String PERMISSION_ILLEGAL_MSG = "归档权限参数非法";
    }

    public interface Permission {
        int PERMISSION_ILLEGAL_CODE = 70205;
        String PERMISSION_ILLEGAL_MSG = "权限参数非法";
    }
}
