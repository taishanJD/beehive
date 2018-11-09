create database yunpan;
use yunpan;

CREATE TABLE `bill` (
  `bill_num` varchar(50) NOT NULL COMMENT '支付订单编号',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `user_name` varchar(50) NOT NULL COMMENT '下单人用户名',  
  `status` tinyint NOT NULL comment '状态:0=待支付，1=已支付，2=已退款',
  `product_code` varchar(10) NOT NULL COMMENT '该订单来自什么产品：rc:根云平台基础服务费', 
  `order_code` varchar(100) COMMENT '业务系统订单编号',  
  `channel` varchar(10) COMMENT '支付渠道：offline:线下支付，alipay:支付宝',
  `trade_no` varchar(100) COMMENT '支付渠道的交易号',
  `amount` decimal(14,2) NOT NULL COMMENT '实际价格(单位:元)',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `pay_time` datetime COMMENT '支付时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) NOT NULL COMMENT '删除标记(0:正常;1:已删除)',
  `notify_paid_status` tinyint comment '支付成功回调业务系统状态:0=未成功，1=已成功通知业务系统',
  PRIMARY KEY (`bill_num`),
  UNIQUE KEY `product_order` (`product_code`,`order_code`),
  UNIQUE KEY `channel` (`channel`,`trade_no`),
  KEY `tenant_id` (`tenant_id`,`create_time` desc),
  KEY `create_time` (`create_time` desc)
) ENGINE=InnoDB DEFAULT CHARSET=utf8  COMMENT='支付订单表';

CREATE TABLE `payment_result` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `bill_num` varchar(50) NOT NULL COMMENT '支付订单编号',
  `channel` varchar(10) NOT NULL COMMENT '支付渠道：offline:线下支付，alipay:支付宝',
  `trade_no` varchar(100) COMMENT '支付渠道的交易号',
  `result` varchar(20) NOT NULL comment 'WAIT_PAY 交易创建，等待买家付款。
										CLOSED 在指定时间段内未支付时关闭的交易/在交易完成全额退款成功时关闭的交易。
										SUCCESS 交易成功，且可对该交易做操作，如：多级分润、退款等。
										PENDING 等待卖家收款（买家付款后，如果卖家账号被冻结）。
										FINISHED  交易成功且结束，即不可再做任何操作',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `bill_num` (`bill_num`),
  KEY `channel_trade` (`channel`,`trade_no`),
  KEY `create_time` (`create_time` desc)
) ENGINE=InnoDB DEFAULT CHARSET=utf8  COMMENT='接收支付渠道回调记录表';

CREATE TABLE `notify_business` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `bill_num` varchar(50) NOT NULL COMMENT '支付订单编号',
  `result` varchar(20) NOT NULL comment 'SUCCESS 通知成功，FAIL 通知失败',
  `retry_num` int(11) NOT NULL COMMENT '重试编号',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `bill_num` (`bill_num`,`retry_num` desc),
  KEY `create_time` (`create_time` desc)
) ENGINE=InnoDB DEFAULT CHARSET=utf8  COMMENT='支付回调业务系统记录表';

CREATE TABLE `pay_channel` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `channel` varchar(10) NOT NULL COMMENT '支付渠道：offline:线下支付，alipay:支付宝',
  `name` varchar(20) NOT NULL COMMENT '支付渠道中文名',
  `status` tinyint NOT NULL comment '状态:0=可用，1=停用',
  PRIMARY KEY (`id`)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8  COMMENT='支付方式表';

CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `product_code` varchar(10) NOT NULL COMMENT '该订单来自什么产品：rc:根云平台基础服务费', 
  `name` varchar(20) NOT NULL COMMENT '产品中文名',
  `notify_url` varchar(200) NOT NULL COMMENT '回调地址',
  `return_url` varchar(200) NOT NULL COMMENT '回跳地址',
  PRIMARY KEY (`id`),
  KEY `product_code` (`product_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8  COMMENT='产品表';


insert into product (product_code,name,notify_url,return_url) 
values('rc','根云平台基础服务费','http://10.70.20.157:80/internal/order_payment_succeed','http://www.roottest.com/paysubmit.html');

-- 修改云充值回跳地址（测试/生成环境会不同）
insert into product (product_code,name,notify_url,return_url) 
values('cloud','云充值费用','http://10.70.20.157:80/internal/cloud_recharge_succeed','http://www.roottest.com/paysubmitCloud.html');

CREATE TABLE `global_lock` (
  `name` varchar(250) COMMENT '锁名称',
   PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='全局锁表';

insert into global_lock values('PAYMENT_RETRY');

-- ----------------------------
-- Records of pay_channel
-- ----------------------------
INSERT INTO `pay_channel` VALUES ('1', 'alipay', '支付宝', '0');
INSERT INTO `pay_channel` VALUES ('2', 'offline', '线下支付', '0');
-- 添加微信支付 支付方式
INSERT INTO `pay`.`pay_channel` (`id`, `channel`, `name`, `status`) VALUES ('3', 'wxpay', '微信', '0');

#0317 欠费补交
INSERT  INTO `product`(`id`,`product_code`,`name`,`notify_url`,`return_url`) VALUES (5,'arrearage','根云平台欠费补交','http://10.69.34.125:80/internal/arrearage_payment_succeed','http://portal.rootcloud.com/portal/action?action=/portal/sys/sim/list')



