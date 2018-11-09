package com.quarkdata.yunpan.api.model.common;

/**
 * Created by yanyq1129@thundersoft.com on 2018/9/10.
 */
public class DocumentPermissionConstants {

    /**
     * 权限说明:    0-无权限 1-有权限
     */
    public static final String PERMISSION_PATTERN = "00000000000000";
    public static final String NO_PERMISSION = "0";
    public static final String HAS_PERMISSION = "1";


    /**
     * 权限与角标对应关系
     */
    public interface PermissionIndex {
        String PREVIEW = "1";                // 预览
        String CREATE_DIR = "2";             // 新建文件夹
        String UPLOAD = "3";                 // 上传
        String DOWNLOAD = "4";               // 下载
        String LINK = "5";                   // 外链
        String MOVE = "6";                   // 移动
        String COPY = "7";                   // 复制
        String RENAME = "8";                 // 重命名
        String DELETE = "9";                 // 删除
        String LOCK = "10";                  // 加锁
        String SET_PERMISSION = "11";        // 权限设置
        String VERSION_MANAGEMENT = "12";    // 版本管理
        String COLLECTION = "13";            // 收藏
        String TAG = "14";                   // 标签
        String ARCHIVE = "15";               // 归档
    }

    /**
     * 角色对应的权限
     */
    public interface PermissionRole {
        String ASSOCIATED_VISIBLE = "000000000000000"; // 关联可见
        String ADMIN_OR_CREATOR = "111111111111111"; // 管理员 or 创建者
        String PREVIEW = "100000000000100"; // 预览者
        String UPLOAD = "111000000100100"; // 上传者
        String DOWNLOAD = "100100000000100"; // 下载者
        String EDIT = "111111111101110"; // 编辑者
        String UPLOAD_OR_DOWNLOAD = "101100000100100"; // 上传者 or 下载者
        String UPLOAD_OR_DOWNLOAD_OR_LINK = "101110000100110"; // 上传者 or 下载者 or 外链者
        String LINK = "100010000000100"; // 外链者
    }
}
