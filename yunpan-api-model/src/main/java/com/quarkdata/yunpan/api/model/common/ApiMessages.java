package com.quarkdata.yunpan.api.model.common;

public class ApiMessages {

	//文件上传-已有同名文件

	public static final int UPLOADED_DOCUMENT_ALREADY_EXISTED_CODE_RENAME_ONLY=30008;
	public static final int UPLOADED_DOCUMENT_ALREADY_EXISTED_CODE_OVERWRITE_ONLY=30009;
	public static final int UPLOADED_DOCUMENT_ALREADY_EXISTED_CODE=30001;
	public static final String UPLOADED_DOCUMENT_ALREADY_EXISTED_MSG="已有同名文件";

	//创建文件夹-已有同名文件
	public static final int CREATE_DIR_ALREADY_EXISTED_CODE=30002;
	public static final String CREATE_DIR_ALREADY_EXISTED_MSG="已有同名文件夹";
	
	public static final int NO_ORG_ADMIN_PERMISSION_CODE=30003;
	public static final String NO_ORG_ADMIN_PERMISSION_MSG="没有组织管理员权限";
	
	public static final int NO_WRITE_PERMISSION_CODE=30004;
	public static final String NO_WRITE_PERMISSION_MSG="没有写权限";

	public static final int NO_READ_PERMISSION_CODE=30005;
	public static final String NO_READ_PERMISSION_MSG="没有读权限";

	public static final int DOCUMENT_NOT_EXIST_CODE =30006;
	public static final String DOCUMENT_NOT_EXIST_MSG="文档不存在";

	public static final int NO_MANAGE_PERMISSION_CODE=30007;
	public static final String NO_MANAGE_PERMISSION_MSG="没有管理权限";
}
