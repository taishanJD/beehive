package com.quarkdata.yunpan.api.dal.rest.quarkshare;

import com.quarkdata.yunpan.api.util.PropertiesUtils;

public class QuarkShareApiConstants {

	/**
	 * quark share api base path
	 */
	static String apiBasePath = PropertiesUtils.prop.get("quarkShareApiBasePathInternal");

	/**
	 * 登录 获取token
	 */
	static String login = "/internal/login";
	static String oneSpaceLogin = "/login";

	/**
	 * 验证token
	 */
	static String validate_token = "/internal/validate_token";

	/**
	 * 获取指定用户的新token
	 */
	static String get_token_by_username ="/internal/get_token_by_username";

	/**
	 * 登出 销毁token
	 */
	static String logout = "/internal/logout";

	/**
	 * 统计企业用户使用情况(用户限额,现有用户数,活跃用户数)
	 */
	static String user_count = "/internal/oneshare/admin/count";

	/**
	 * 获取管理员列表
	 */
	static String admin_list = "/internal/oneshare/admin/list";

	/**
	 * 批量添加管理员
	 */
	static String admin_add = "/internal/oneshare/admin/add";

	/**
	 * 根据ID撤销系统管理员
	 */
	static String admin_revoke = "/internal/oneshare/admin/revoke";

	/**
	 * 根据ID撤销组织管理员
	 */
	static String admin_revoke_org = "/internal/oneshare/admin/revoke/org";

	/**
	 * 根据ID编辑系统管理员
	 */
	static String admin_edit = "/internal/oneshare/admin/edit";

	/**
	 * 根据ID编辑组织管理员
	 */
	static String admin_edit_org = "/internal/oneshare/admin/edit/org";

	/**
	 * 根据企业ID查询企业信息
	 */
	static String incorporation_info_check = "/internal/inc/check";

	/**
	 * 根据企业ID修改企业信息
	 */
	static String incorporation_info_update = "/internal/inc/update";

	/**
	 * 根据企业ID修改企业信息
	 */
	static String incorporation_info_get_by_domain = "/internal/inc/get_inc_by_domain";

	/**
	 * 根据管理员ID查询管理员角色信息
	 */
	static String get_user_role_by_id = "/internal/oneshare/admin/role";

	/**
	 * 根据用户名模糊查询用户信息
	 */
	static String get_user_by_userName = "/internal/oneshare/admin/check_by_username";

	/**
	 * 根据用户姓名模糊查询用户列表
	 */
//	static String user_list = "/internal/user/get_user_list";
	static String user_list = "/internal/user/get_order_userlist_by_search"; // 按关键字匹配度排序

	static String super_admin_list = "/internal/admin/list";//获取全部系统管理员id

	//	static String user_list_by_dept_id = "/internal/user/get_user_list_by_dept_id";
	static String user_list_by_dept_id = "/internal/user/get_order_userlist_page_by_deptid"; // 按关键字匹配度排序

	static String user_list_by_group_id = "/internal/group/get_user_list_by_group_id";
	/**
	 * 根据用户组名称模糊查询用户组列表
	 */
	static String group_By_id = "/internal/group/get_group_by_id";
	static String group_list = "/internal/group/page";
	static String group_list_by_incId = "/internal/group/get_group_list";

	static String group_list_by_dept_id = "/internal/group/get_group_list_by_dept_id";

	static String group_list_by_group_id = "/internal/group/get_group_list_by_group_id";

	static String get_group_list_by_inc_id_and_source = "/internal/group/get_group_list_by_inc_id_and_source";
	/**
	 * 根据父节点id和名称查询部门
	 */
	static String department_list = "/internal/department/get_department_list_no_page";


	/**
	 * 根据ID查询用户信息
	 */
	static String get_user_by_id = "/internal/user/get_user_by_id";

	/**
	 * 更新个人信息
	 */
	static String update_user="/internal/user/update_user";

	/**
	 * 保存个人信息
	 */
	static String save_user="/internal/user/add_user";
	/**
	 * 删除个人信息
	 */
	static String del_user="/internal/user/delete_user";

	/**
	 * 批量删除用户
	 */
	static String del_user_batch="/internal/user/delete_batch_user";

	/**
	 * 获取用户详情列表
	 */
	static String user_details="/internal/user/get_user_list_by_ids";

	/**
	 * 获取用户组详情列表
	 */
	static String group_details="/internal/group/get_group_list_by_ids";

	/**
	 * 获取部门详情列表
	 */
	static String department_details="/internal/department/get_department_list_by_ids";

	/**
	 * 根据用户名查询用户
	 */
	static String find_user_by_username ="/internal/user/get_user_by_user_name_and_inc_id";

	/**
	 * 根据用户组id查询用户
	 */
	static String group_userlist="/internal/group/get_user_list_by_group_id";

	/**
	 * 根据部门id查询用户
	 */
	static String dept_userlist="/internal/user/get_user_list_by_dept_id";

	/**
	 * 根据用户id查询用户所在组
	 */
    public static String group_list_by_user_id = "/internal/group/get_group_list_by_user_id";

	/**
	 * 根据用户id查询用户所在部门
	 */
	public static String get_department_by_user_id = "/internal/department/get_department_by_user_id";

	/**
	 * 查询部门列表,无结构
	 */
	public static String department_list_no_structure = "/internal/department/get_department_list";
	/**
	 * 根据id查询部门数据
	 */
	public static String get_department_by_id = "/internal/department/get_department_by_id";
	/**
	 * 判定当前登录用户是否是超级管理员
	 */
	public static final String GET_ONE_SPACE_ADMIN_BY_USERID = "/internal/admin/get_admin_by_user_id";
}
