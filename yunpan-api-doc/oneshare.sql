/*
 Navicat MySQL Data Transfer

 Source Server         : OneShare test 40.125.162.231
 Source Server Type    : MySQL
 Source Server Version : 50621
 Source Host           : 40.125.162.231
 Source Database       : oneshare

 Target Server Type    : MySQL
 Target Server Version : 50621
 File Encoding         : utf-8

 Date: 02/23/2018 14:20:55 PM
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `collect_document_rel`
-- ----------------------------
DROP TABLE IF EXISTS `collect_document_rel`;
CREATE TABLE `collect_document_rel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 自增',
  `inc_id` bigint(20) NOT NULL COMMENT '企业id',
  `user_id` bigint(20) NOT NULL COMMENT '（收藏者）用户id',
  `document_id` bigint(20) NOT NULL COMMENT '文档id',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`,`inc_id`) USING BTREE,
  KEY `idx_document_id` (`document_id`,`inc_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=354 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `document`
-- ----------------------------
DROP TABLE IF EXISTS `document`;
CREATE TABLE `document` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `inc_id` bigint(20) NOT NULL COMMENT '组织id',
  `parent_id` bigint(20) NOT NULL COMMENT '父文件夹id',
  `document_version_id` bigint(20) DEFAULT NULL COMMENT '文档版本表id',
  `document_name` varchar(255) NOT NULL COMMENT '文档名称',
  `type` char(1) NOT NULL COMMENT '类型：0组织，1个人，2归档',
  `document_type` varchar(20) NOT NULL COMMENT '文档类型：dir，docx、pdf、xls等',
  `id_path` varchar(500) DEFAULT NULL COMMENT '文档id路径，例：/1/2/5',
  `size` bigint(20) DEFAULT NULL COMMENT '文件大小，字节',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` bigint(20) NOT NULL COMMENT '创建人',
  `create_username` varchar(50) NOT NULL COMMENT '创建人姓名',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '修改人',
  `update_username` varchar(50) DEFAULT NULL COMMENT '更新人姓名',
  `is_share` char(1) NOT NULL DEFAULT '0' COMMENT '是否共享：0否，1是',
  `description` varchar(100) DEFAULT NULL COMMENT '归档描述信息',
  `is_delete` varchar(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0否，1是',
  PRIMARY KEY (`id`),
  KEY `idx_document_name` (`document_name`) USING BTREE,
  KEY `idx_parent_id` (`parent_id`) USING BTREE,
  KEY `uni_pid_did` (`parent_id`,`document_name`) USING BTREE,
  KEY `idx_create_user_type_inc_id` (`create_user`,`type`,`inc_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3201 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `document_ceph_delete`
-- ----------------------------
DROP TABLE IF EXISTS `document_ceph_delete`;
CREATE TABLE `document_ceph_delete` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `inc_id` bigint(20) NOT NULL COMMENT '企业id',
  `document_version_id` bigint(20) NOT NULL COMMENT '文件版本id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` bigint(20) NOT NULL COMMENT '创建者用户id',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `is_delete` char(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0否、1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `document_version_id_unique` (`document_version_id`) USING BTREE,
  UNIQUE KEY `uni_dvid_isdel` (`document_version_id`,`is_delete`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=777 DEFAULT CHARSET=utf8 COMMENT='删除ceph文件表';

-- ----------------------------
--  Table structure for `document_permission`
-- ----------------------------
DROP TABLE IF EXISTS `document_permission`;
CREATE TABLE `document_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `inc_id` bigint(20) NOT NULL COMMENT '企业id',
  `document_id` bigint(2) NOT NULL COMMENT '文档id',
  `receiver_type` char(1) NOT NULL COMMENT '接收者类型：0用户、1用户组、2部门',
  `receiver_id` bigint(20) NOT NULL COMMENT '接收者id：用户id或用户组id或部门id',
  `permission` char(1) NOT NULL COMMENT '权限：0只读、1读写、2管理',
  `share_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_document_id` (`document_id`,`inc_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2608 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `document_share`
-- ----------------------------
DROP TABLE IF EXISTS `document_share`;
CREATE TABLE `document_share` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `inc_id` bigint(20) NOT NULL COMMENT '企业id',
  `document_id` bigint(20) NOT NULL COMMENT '被分享文档id',
  `receiver_id` bigint(20) NOT NULL COMMENT '接收者id',
  `receiver_type` char(1) NOT NULL COMMENT '接收者类型：0用户、1用户组、2部门',
  `create_user` bigint(20) NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_username` varchar(50) NOT NULL COMMENT '创建人姓名',
  PRIMARY KEY (`id`),
  KEY `idx_did_incid` (`document_id`,`inc_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2181 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `document_version`
-- ----------------------------
DROP TABLE IF EXISTS `document_version`;
CREATE TABLE `document_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 自增',
  `inc_id` bigint(20) NOT NULL COMMENT '企业id',
  `document_id` bigint(2) NOT NULL COMMENT '文档id',
  `version` int(10) NOT NULL COMMENT '文件版本号：从1开始递增',
  `size` bigint(20) DEFAULT NULL COMMENT '文件大小，字节',
  `operate_type` char(1) NOT NULL DEFAULT '0' COMMENT '操作类型：0上传文件、1同步文件、2文档协作、3 复制文件 、 4移动文件',
  `is_delete` char(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0否，1是',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` bigint(20) NOT NULL COMMENT '创建人',
  `create_username` varchar(50) NOT NULL COMMENT '创建人姓名',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '修改人',
  `update_username` varchar(50) DEFAULT NULL COMMENT '更新人姓名',
  `md5` varchar(100) DEFAULT NULL COMMENT '文件md5值',
  `hash` varchar(100) DEFAULT NULL COMMENT '文件hash值',
  `ceph_bucket` varchar(100) NOT NULL COMMENT 'ceph bucket',
  `ceph_bucket_key` varchar(100) NOT NULL COMMENT 'ceph bucket key',
  PRIMARY KEY (`id`),
  KEY `idx_did_version_incid` (`document_id`,`version`,`inc_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2630 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `external_link`
-- ----------------------------
DROP TABLE IF EXISTS `external_link`;
CREATE TABLE `external_link` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `inc_id` bigint(20) NOT NULL COMMENT '企业id',
  `document_id` bigint(20) NOT NULL COMMENT '文档id',
  `is_enable_external_link` char(1) DEFAULT '1' COMMENT '是否启用外链：0否，1是',
  `external_link_type` char(1) NOT NULL COMMENT '外链访问权限：0任何人，1组织内所有用户，2仅具有访问权限的用户',
  `external_link_code` varchar(100) NOT NULL COMMENT '外链资源标识',
  `external_link_expire_time` datetime DEFAULT NULL COMMENT '外链到期时间',
  `view_count` int(11) DEFAULT '0' COMMENT '浏览次数',
  `download_count` int(11) DEFAULT '0' COMMENT '下载次数',
  `create_user` bigint(20) NOT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_username` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_external_code` (`external_link_code`) USING BTREE,
  KEY `idx_document_id` (`document_id`,`inc_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=143 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `inc_config`
-- ----------------------------
DROP TABLE IF EXISTS `inc_config`;
CREATE TABLE `inc_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `inc_id` bigint(20) NOT NULL,
  `history_version_type` char(1) NOT NULL COMMENT '历史版本类型：0保留所有历史版本、1保留最新的xx个历史版本、2保留最近xx天的历史版本、3不保留历史版本',
  `history_version_param` int(10) DEFAULT NULL COMMENT '历史版本参数',
  `inc_ratio` int(10) NOT NULL COMMENT '组织空间占比',
  `user_ratio` int(10) NOT NULL COMMENT '个人空间占比',
  `per_user_quota` int(10) NOT NULL COMMENT '个人空间最高配额：单位MB',
  `inc_total_quota` int(20) NOT NULL COMMENT '组织总空间，单位MB',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `is_delete` char(1) NOT NULL COMMENT '是否删除：0否、1是',
  `link_man` varchar(100) DEFAULT NULL COMMENT '企业联系人',
  `telephone` varchar(255) DEFAULT NULL COMMENT '企业联系方式',
  `email` varchar(255) DEFAULT NULL COMMENT '企业邮箱',
  `ceph_access_key` varchar(50) DEFAULT NULL COMMENT 'ceph access key',
  `ceph_secret_key` varchar(50) DEFAULT NULL COMMENT 'ceph secret key',
  `ceph_url` varchar(50) DEFAULT NULL COMMENT 'ceph 地址',
  `logo` mediumblob COMMENT '企业logo',
  PRIMARY KEY (`id`),
  UNIQUE KEY `inc_id_unique` (`inc_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='企业配置信息';

-- ----------------------------
--  Table structure for `inc_user_config`
-- ----------------------------
DROP TABLE IF EXISTS `inc_user_config`;
CREATE TABLE `inc_user_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `inc_id` bigint(20) NOT NULL COMMENT '企业id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `is_receive_message` char(1) DEFAULT NULL COMMENT '是否接收消息提醒：0否、1是，（字段为空代表1接收消息提醒）',
  `is_receive_email_message` char(1) DEFAULT NULL COMMENT '是否接收邮件消息提醒：0否、1是，（字段为空代表1接收邮件消息提醒）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_unique` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `log`
-- ----------------------------
DROP TABLE IF EXISTS `log`;
CREATE TABLE `log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 自增',
  `inc_id` bigint(20) NOT NULL COMMENT '企业id',
  `create_user_id` bigint(20) NOT NULL COMMENT '操作用户id',
  `create_username` varchar(255) NOT NULL,
  `action_type` varchar(200) NOT NULL COMMENT '动作类型',
  `action_detail` varchar(255) NOT NULL COMMENT '动作详情',
  `user_ip` varchar(255) NOT NULL COMMENT '操作者ip',
  `create_time` datetime NOT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_createtiem_action_type_inc_id` (`create_time`,`action_type`,`inc_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7922 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `message`
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 自增',
  `inc_id` bigint(20) NOT NULL COMMENT '企业id',
  `message` varchar(100) NOT NULL COMMENT '消息内容，例：小高对我授予访问权限、小高对我所在部门：研发部授予读写权限',
  `create_username` varchar(20) NOT NULL COMMENT '创建人 姓名',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=189 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `message_user_document_rel`
-- ----------------------------
DROP TABLE IF EXISTS `message_user_document_rel`;
CREATE TABLE `message_user_document_rel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 自增',
  `inc_id` bigint(20) NOT NULL COMMENT '企业id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `message_id` bigint(20) NOT NULL COMMENT '消息id',
  `document_id` bigint(20) NOT NULL COMMENT '文档id',
  `is_read` char(1) NOT NULL COMMENT '是否已读：0否、1是',
  PRIMARY KEY (`id`),
  KEY `idx_user_id_is_read_inc_id` (`user_id`,`is_read`,`inc_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=193 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `recycle`
-- ----------------------------
DROP TABLE IF EXISTS `recycle`;
CREATE TABLE `recycle` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `inc_id` bigint(20) NOT NULL,
  `document_id` bigint(20) NOT NULL COMMENT '文档id',
  `document_version_id` bigint(20) DEFAULT NULL COMMENT '文档版本id',
  `document_parent_id` bigint(20) NOT NULL,
  `document_id_path` varchar(500) NOT NULL,
  `create_user` bigint(20) NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `is_delete` char(1) NOT NULL COMMENT '是否删除：0否、1是',
  `is_visiable` char(1) NOT NULL DEFAULT '1' COMMENT '是否可见：0否、1是',
  PRIMARY KEY (`id`),
  KEY `idx_document_id_inc_id` (`document_id`,`inc_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1608 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='回收站';

-- ----------------------------
--  Table structure for `sys_dict`
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `value` varchar(20) NOT NULL COMMENT '对应值',
  `name` varchar(20) NOT NULL COMMENT '显示名称',
  `type` varchar(20) NOT NULL COMMENT '类型',
  `sort` int(10) NOT NULL COMMENT '排序',
  `is_delete` char(1) NOT NULL COMMENT '是否删除：0否、1是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='字典表';

-- ----------------------------
--  Table structure for `tag`
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 自增',
  `inc_id` bigint(20) NOT NULL COMMENT '企业id',
  `tag_name` varchar(50) NOT NULL COMMENT '标签名称',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` bigint(20) NOT NULL COMMENT '创建人',
  `is_delete` char(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0否、1是',
  PRIMARY KEY (`id`),
  KEY `idx_tag_name_is_delete_inc_id` (`tag_name`,`is_delete`,`inc_id`) USING BTREE,
  KEY `idx_create_user_is_delete_inc_id` (`create_user`,`is_delete`,`inc_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=141 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `tag_document_rel`
-- ----------------------------
DROP TABLE IF EXISTS `tag_document_rel`;
CREATE TABLE `tag_document_rel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 自增',
  `inc_id` bigint(20) NOT NULL COMMENT '企业id',
  `document_id` bigint(20) NOT NULL COMMENT '文档id',
  `tag_id` bigint(20) NOT NULL COMMENT '标签id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` bigint(20) NOT NULL COMMENT '创建人',
  PRIMARY KEY (`id`),
  KEY `idx_tag_id_inc_id` (`tag_id`,`inc_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=628 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Function structure for `getChildList`
-- ----------------------------
DROP FUNCTION IF EXISTS `getChildList`;
delimiter ;;
CREATE DEFINER=`root`@`%` FUNCTION `getChildList`(rootId varchar(100)) RETURNS varchar(2000) CHARSET utf8 COLLATE utf8_bin
  BEGIN
    DECLARE str varchar(2000);
    DECLARE cid varchar(100);
    SET str = '$';
    SET cid = rootId;
    WHILE cid is not null DO
      SET str = concat(str, ',', cid);
      SELECT group_concat(id) INTO cid FROM document where FIND_IN_SET(parent_id, cid) > 0;
    END WHILE;
    RETURN str;
  END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
