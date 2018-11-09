package com.quarkdata.yunpan.api.model.common;

/**
 * Document常量
 * 
 * @author typ
 * 	2017年12月11日
 */
public class DocumentConstants {

	/**
	 * 文档类型
	 */
	public static final String DOCUMENT_TYPE_ORGANIZED="0";//组织
	public static final String DOCUMENT_TYPE_PERSONAL="1";//个人
	public static final String DOCUMENT_TYPE_ARCHIVE="2";//归档


	public static final String DOCUMENT_IS_DELETE_NO="0";//否
	public static final String DOCUMENT_IS_DELETE_YES="1";//是
	
	
	public static final Long DOCUMENT_ROOT_PARENTID=0L;//根目录的parentId
	
	public static final int UPLOAD_IS_OVERWRITE_YES=1;//上传文档是否覆盖-是
	
	public static final String IS_DELETE_YES="1";//是否删除：0否，1是
	public static final String IS_DELETE_NO="0";

	public static final String IS_LOCK_YES="1";//是否删除：0否，1是
	public static final String IS_LOCK_NO="0";
	
	public static final String DOCUMENT_OPERATE_TYPE_UPLOAD="0";//操作类型：0上传文件、1同步文件、2文档协作
	public static final String DOCUMENT_OPERATE_TYPE_SYNC="1";
	public static final String DOCUMENT_OPERATE_TYPE_COOPERATION="2";
	
	public static final String DOCUMENT_IS_SHARE_YES="1";//是否共享：0否，1是
	public static final String DOCUMENT_IS_SHARE_NO="0";
	
	public static final String DOCUMENT_DOCUMENT_TYPE_DIR="dir";//文档类型：dir，docx、pdf、xls等
	

	public static final long MIN_FILE_SIZE = 2048;//文件上传最小值

	/**
	 * 权限接收者类型: 0用户、1用户组、2部门
	 */
	public static final String DOCUMENT_PERMISSION_RECEIVER_TYPE_USER = "0";
	public static final String DOCUMENT_PERMISSION_RECEIVER_TYPE_GROUP = "1";
	public static final String DOCUMENT_PERMISSION_RECEIVER_TYPE_DEPT = "2";
	public static final String DOCUMENT_PERMISSION_RECEIVER_TYPE_GENERATE_ACCOUNT = "3";

	// 组织文档权限: -1关联只读、0只读、1读写、2管理、3创建者、99超级管理权限
	public static final String DOCUMENT_PERMISSION_ASSOCIATION_READ = "-1";
	public static final String DOCUMENT_PERMISSION_READ = "0";
	public static final String DOCUMENT_PERMISSION_READANDWRITE = "1";
	public static final String DOCUMENT_PERMISSION_MANAGE= "2";
	public static final String DOCUMENT_PERMISSION_CREATE = "3";
	public static final String DOCUMENT_PERMISSION_SUPER_MANAGE = "99";

	public static final String DOCUMENT_OVERWRITE_ONLY = "1"; //只能覆盖
	public static final String DOCUMENT_RENAME_ONLY = "-1"; //只可重命名后上传
	public static final String DOCUMENT_OVERWRITE_AND_RENAME = "0"; //上述二者均可

	/**
	 * 共享接收者类型:0用户、1用户组、2部门
	 */
	public static final String DOCUMENT_SHARE_RECEIVER_TYPE_USER = "0";
	public static final String DOCUMENT_SHARE_RECEIVER_TYPE_GROUP = "1";
	public static final String DOCUMENT_SHARE_RECEIVER_TYPE_DEPT = "2";


	/**
	 * 外链
	 */
    public interface Link {
    	String ALLOW_DOWNLOAD_NO = "0";
    	String ALLOW_PREVIEW_NO = "0";
		String IS_SECRET_NO = "0";
		String IS_SECRET_YES = "1";
		String ALLOW_PREVIEW_YES = "1";
		String ALLOW_DOWNLOAD_YES = "1";
		String NOTIFICATION_RECEIVE_TYPE_TELEPHONE = "0";
		String NOTIFICATION_RECEIVE_TYPE_EMAIL = "1";
    }
}
