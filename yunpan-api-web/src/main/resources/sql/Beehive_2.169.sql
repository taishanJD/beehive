/*
Navicat MySQL Data Transfer

Source Server         : 192.168.2.169
Source Server Version : 50722
Source Host           : 192.168.2.169:3306
Source Database       : Beehive

Target Server Type    : MYSQL
Target Server Version : 50722
File Encoding         : 65001

Date: 2018-10-12 09:58:08
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for client_download_path
-- ----------------------------
DROP TABLE IF EXISTS `client_download_path`;
CREATE TABLE `client_download_path` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `domain` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '企业域名',
  `platform` varchar(55) COLLATE utf8_bin NOT NULL COMMENT '客户端类型',
  `download_path` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '客户端下载路径',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for collect_document_rel
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
) ENGINE=InnoDB AUTO_INCREMENT=3278 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for document
-- ----------------------------
DROP TABLE IF EXISTS `document`;
CREATE TABLE `document` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `inc_id` bigint(20) NOT NULL COMMENT '组织id',
  `parent_id` bigint(20) NOT NULL COMMENT '父文件夹id',
  `document_version_id` bigint(20) DEFAULT NULL COMMENT '文档版本表id',
  `document_name` varchar(255) NOT NULL COMMENT '文档名称',
  `type` char(1) NOT NULL COMMENT '类型：0组织，1个人，2归档，3共享空间',
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
  `is_lock` char(1) NOT NULL DEFAULT '0' COMMENT '是否锁定: 0-非锁定 1-锁定',
  `lock_time` datetime DEFAULT NULL COMMENT '锁定时间',
  `lock_user` bigint(20) DEFAULT NULL COMMENT '锁定者',
  `lock_username` varchar(50) DEFAULT NULL COMMENT '锁定者姓名',
  PRIMARY KEY (`id`),
  KEY `idx_document_name` (`document_name`) USING BTREE,
  KEY `idx_parent_id` (`parent_id`) USING BTREE,
  KEY `idx_create_user_type_inc_id` (`create_user`,`type`,`inc_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=50655 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for document_ceph_delete
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
) ENGINE=InnoDB AUTO_INCREMENT=10431 DEFAULT CHARSET=utf8 COMMENT='删除ceph文件表';

-- ----------------------------
-- Table structure for document_es_index
-- ----------------------------
DROP TABLE IF EXISTS `document_es_index`;
CREATE TABLE `document_es_index` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 自增',
  `document_version_id` bigint(20) NOT NULL COMMENT '文档版本ID',
  `document_type` varchar(20) COLLATE utf8_bin NOT NULL,
  `inc_id` bigint(20) NOT NULL COMMENT '企业ID',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建者ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for document_permission
-- ----------------------------
DROP TABLE IF EXISTS `document_permission`;
CREATE TABLE `document_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `inc_id` bigint(20) NOT NULL COMMENT '企业id',
  `document_id` bigint(2) NOT NULL COMMENT '文档id',
  `receiver_type` char(1) NOT NULL COMMENT '接收者类型：0用户、1用户组、2部门、3系统创建账号',
  `receiver_id` bigint(20) NOT NULL COMMENT '接收者id：用户id或用户组id或部门id',
  `permission` char(15) NOT NULL COMMENT '权限：-1关联只读、0只读、1读写、2管理、3创建者、99超级管理',
  `share_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_permission` (`inc_id`,`document_id`,`receiver_type`,`receiver_id`) USING BTREE,
  KEY `idx_document_id` (`document_id`,`inc_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=62416 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for document_permission_inherit
-- ----------------------------
DROP TABLE IF EXISTS `document_permission_inherit`;
CREATE TABLE `document_permission_inherit` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `inc_id` bigint(20) NOT NULL COMMENT '企业id',
  `document_id` bigint(20) NOT NULL COMMENT '文档id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `inc_id_document_id` (`inc_id`,`document_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8 COMMENT='单独设置权限的文档数据';

-- ----------------------------
-- Table structure for document_share
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
  `update_user` bigint(20) DEFAULT NULL,
  `update_username` varchar(50) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `is_delete` char(1) DEFAULT '0' COMMENT '是否删除：0否，1是',
  PRIMARY KEY (`id`),
  KEY `idx_did_incid` (`document_id`,`inc_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5741 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for document_version
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
) ENGINE=InnoDB AUTO_INCREMENT=26237 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for external_link
-- ----------------------------
DROP TABLE IF EXISTS `external_link`;
CREATE TABLE `external_link` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `inc_id` bigint(20) NOT NULL COMMENT '企业id',
  `document_id` bigint(20) NOT NULL COMMENT '文档id',
  `is_enable_external_link` char(1) DEFAULT '1' COMMENT '是否启用外链：0否，1是',
  `external_link_type` char(1) NOT NULL COMMENT '外链访问权限：0任何人，1组织内所有用户，2仅具有访问权限的用户,3共享空间内用户',
  `external_link_code` varchar(100) NOT NULL COMMENT '外链资源标识',
  `external_link_expire_time` datetime DEFAULT NULL COMMENT '外链到期时间',
  `view_count` int(11) DEFAULT '0' COMMENT '浏览次数',
  `download_count` int(11) DEFAULT '0' COMMENT '下载次数',
  `create_user` bigint(20) NOT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_username` varchar(50) NOT NULL,
  `allow_preview` char(1) NOT NULL DEFAULT '1' COMMENT '允许预览: 0-不允许, 1-允许',
  `allow_download` char(1) NOT NULL DEFAULT '1' COMMENT '允许下载: 0-不允许, 1-允许',
  `is_secret` char(1) NOT NULL DEFAULT '0' COMMENT '是否加密: 0-否;1-是',
  `fetch_code` varchar(50) DEFAULT NULL COMMENT '加密后的提取码',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_external_code` (`external_link_code`) USING BTREE,
  KEY `idx_document_id` (`document_id`,`inc_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1395 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for external_user
-- ----------------------------
DROP TABLE IF EXISTS `external_user`;
CREATE TABLE `external_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `inc_id` bigint(20) NOT NULL COMMENT '企业id',
  `user_id` bigint(20) NOT NULL COMMENT '系统生成账号id',
  `create_user` bigint(20) NOT NULL COMMENT '创建者id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `expiry_date` datetime NOT NULL COMMENT '系统生成账号有效期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=566 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for inc_config
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
  UNIQUE KEY `inc_id_unique` (`inc_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='企业配置信息';

-- ----------------------------
-- Table structure for inc_user_config
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
) ENGINE=InnoDB AUTO_INCREMENT=198 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for log
-- ----------------------------
DROP TABLE IF EXISTS `log`;
CREATE TABLE `log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 自增',
  `inc_id` bigint(20) NOT NULL COMMENT '企业id',
  `create_user_id` bigint(20) NOT NULL COMMENT '操作用户id',
  `create_username` varchar(255) NOT NULL,
  `action_type` varchar(200) NOT NULL COMMENT '动作类型',
  `action_detail` varchar(555) NOT NULL COMMENT '动作详情',
  `user_ip` varchar(255) NOT NULL COMMENT '操作者ip',
  `create_time` datetime NOT NULL COMMENT '操作时间',
  `is_delete` varchar(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_createtiem_action_type_inc_id` (`create_time`,`action_type`,`inc_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=180987 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for log_document_rel
-- ----------------------------
DROP TABLE IF EXISTS `log_document_rel`;
CREATE TABLE `log_document_rel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 自增',
  `inc_id` bigint(20) NOT NULL COMMENT '企业ID',
  `log_id` bigint(20) NOT NULL COMMENT '日志ID',
  `document_id` bigint(20) NOT NULL COMMENT '文档ID',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建者ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=165546 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 自增',
  `inc_id` bigint(20) NOT NULL COMMENT '企业id',
  `message` varchar(100) NOT NULL COMMENT '消息内容，例：小高对我授予访问权限、小高对我所在部门：研发部授予读写权限',
  `create_username` varchar(20) NOT NULL COMMENT '创建人 姓名',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `message_type` int(11) DEFAULT '2' COMMENT '消息类型（1权限提醒，2分享提醒，3收藏提醒,4邀请提醒）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10285 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for message_push_channel
-- ----------------------------
DROP TABLE IF EXISTS `message_push_channel`;
CREATE TABLE `message_push_channel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `inc_id` bigint(20) NOT NULL COMMENT '企业id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `token` varchar(255) NOT NULL COMMENT '客户端唯一识别码UDID',
  `client_type` varchar(255) NOT NULL COMMENT '设备类型: iOS,Android,Mac,PC',
  `is_online` char(1) NOT NULL DEFAULT '0' COMMENT '是否在线: 0-不在线 1-在线',
  `create_time` datetime DEFAULT NULL COMMENT '通道创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=265 DEFAULT CHARSET=utf8 COMMENT='消息推送设备-用户-channel对应表';

-- ----------------------------
-- Table structure for message_user_document_rel
-- ----------------------------
DROP TABLE IF EXISTS `message_user_document_rel`;
CREATE TABLE `message_user_document_rel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 自增',
  `inc_id` bigint(20) NOT NULL COMMENT '企业id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `message_id` bigint(20) NOT NULL COMMENT '消息id',
  `document_id` bigint(20) NOT NULL COMMENT '文档id',
  `is_read` char(1) NOT NULL COMMENT '是否已读：0否、1是',
  `is_delete` char(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0否，1是',
  PRIMARY KEY (`id`),
  KEY `idx_user_id_is_read_inc_id` (`user_id`,`is_read`,`inc_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12677 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for recycle
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
) ENGINE=InnoDB AUTO_INCREMENT=11412 DEFAULT CHARSET=utf8 COMMENT='回收站';

-- ----------------------------
-- Table structure for sys_dict
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
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8 COMMENT='字典表';

-- ----------------------------
-- Table structure for tag
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
) ENGINE=InnoDB AUTO_INCREMENT=936 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tag_document_rel
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
) ENGINE=InnoDB AUTO_INCREMENT=2399 DEFAULT CHARSET=utf8;
DROP TRIGGER IF EXISTS `ins_doc_version`;
DELIMITER ;;
CREATE TRIGGER `ins_doc_version` BEFORE INSERT ON `document_version` FOR EACH ROW BEGIN
set @doc_type = (select type from document where id = new.document_id);
if @doc_type = 0 then
set @version =  (select max(version) from document_version where document_id = new.document_id);
if @version is null then
set @version = 0;
end if;
set new.version = @version + 1;
end if;
END
;;
DELIMITER ;
