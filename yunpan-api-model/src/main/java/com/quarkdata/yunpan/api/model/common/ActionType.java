package com.quarkdata.yunpan.api.model.common;

/**
 * @Author yanyq1129@thundersoft.com
 * @Description 动作类型列表
 */
public class ActionType {
	public static final Integer LOGIN = 0; // 登录
	public static final Integer LOGOUT = 1; // 退出

	public static final Integer UPLOAD = 2; // 上传文件

	public static final Integer CREATE_FOLDER = 3; // 新建文件夹

	public static final Integer CREATE_ORGANIZED_SPACE = 4; // 新建组织空间
	public static final Integer CREATE_SHARE_SPACE = 5; // 新建外部共享空间
	public static final Integer GENERATE_EXTERNAL_ACCOUNT = 6; // 生成外部账号

	public static final Integer DOWNLOAD = 7; //下载

	public static final Integer COLLECTION = 8; // 收藏
	public static final Integer DELETE_COLLECTION = 9; // 取消收藏

	public static final Integer OUTERLINK = 10; // 外链
	public static final Integer DELETE_OUTERLINK = 11; // 删除外链

	public static final Integer SHARE = 12; // 共享
	public static final Integer DELETE_SHARE = 13; // 取消共享

	public static final Integer DELETE_DOC_VERSION = 14; // 删除文档版本
	public static final Integer RESTORING_HISTORICAL_VERSION = 15; // 恢复历史版本

	public static final Integer DELETE = 16; // 删除
	public static final Integer DELETE_FROM_RECYCLE = 17; // 从回收站删除

	public static final Integer MOVE = 18; // 移动

	public static final Integer COPY = 19; // 复制

	public static final Integer RENAME = 20; // 重命名

	public static final Integer RESTORE = 21; // 还原

	public static final Integer ADD_TAG = 22; // 添加文档标签
	public static final Integer DELETE_TAG = 23; // 删除文档标签

	public static final Integer EXPORT = 24; // 导出

	public static final Integer ADD_SYSTEM_ADMINISTRATOR = 25; // 添加系统管理员
	public static final Integer ADD_ORGANIZATION_ADMINISTRATOR = 26; // 添加组织管理员
	public static final Integer DELETE_THE_SYSTEM_ADMINISTRATOR = 27; // 删除系统管理员
	public static final Integer DELETE_THE_ORGANIZATION_ADMINISTRATOR = 28; // 删除组织管理员

	public static final Integer SET_DOCUMENT_PRIVILEGE = 29; // 设置文档权限

	public static final Integer UPDATE_INC_INFO_AND_INC_CONFIG = 30; // 修改企业信息和云盘配置
	public static final Integer UPDATE_INC_INFO = 31; // 修改企业信息
	public static final Integer UPDATE_SPACE_QUOTA = 32; // 修改空间配额
	public static final Integer UPDATE_HISTORICAL_VERSION_RESERVATION= 33; // 修改历史版本保留方式

	public static final Integer PREVIEW = 34; // 预览

	public static final Integer ACCESS_LINK = 35; // 访问外链



}
