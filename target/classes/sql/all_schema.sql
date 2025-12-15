-- 吴义商城数据库完整SQL脚本
-- 包含所有表结构、索引、外键约束等

-- 创建数据库
CREATE DATABASE IF NOT EXISTS wuyi_mall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE wuyi_mall;

-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键自增',
    `username` varchar(50) NOT NULL COMMENT '用户名，唯一',
    `password` varchar(255) NOT NULL COMMENT '密码，BCrypt加密存储',
    `phone` varchar(11) NOT NULL COMMENT '手机号，唯一',
    `default_address` varchar(255) DEFAULT NULL COMMENT '默认交易地址',
    `role` varchar(20) NOT NULL DEFAULT '客户' COMMENT '用户角色，默认客户',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除，0-正常，1-删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_update_time` (`update_time`)
);

-- 创建用户收藏表
CREATE TABLE IF NOT EXISTS `collect` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID，主键自增',
    `user_id` bigint NOT NULL COMMENT '用户ID，外键关联user表',
    `product_id` bigint NOT NULL COMMENT '商品ID，外键关联product表',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`,`product_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`),
    CONSTRAINT `fk_collect_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_collect_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 创建商品表
CREATE TABLE IF NOT EXISTS `product` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID，主键自增',
    `name` varchar(255) NOT NULL COMMENT '商品名称',
    `price` decimal(10,2) NOT NULL COMMENT '商品价格',
    `stock_num` int NOT NULL DEFAULT '0' COMMENT '商品库存数量',
    `status` tinyint NOT NULL DEFAULT '0' COMMENT '商品状态，0-下架，1-上架',
    `image` varchar(255) DEFAULT NULL COMMENT '商品图片',
    `description` text COMMENT '商品描述',
    `category_id` bigint DEFAULT NULL COMMENT '分类ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除，0-正常，1-删除',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_update_time` (`update_time`)
);

-- 创建购物车表
CREATE TABLE IF NOT EXISTS `cart` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '购物车项ID，主键自增',
    `user_id` bigint NOT NULL COMMENT '用户ID，外键关联user表',
    `product_id` bigint NOT NULL COMMENT '商品ID，外键关联product表',
    `quantity` int NOT NULL DEFAULT '1' COMMENT '购买数量',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`,`product_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_update_time` (`update_time`),
    CONSTRAINT `fk_cart_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 创建订单表
CREATE TABLE IF NOT EXISTS `order` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID，主键自增',
    `order_no` varchar(50) NOT NULL COMMENT '订单编号，唯一',
    `user_id` bigint NOT NULL COMMENT '用户ID，外键关联user表',
    `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
    `status` tinyint NOT NULL DEFAULT '0' COMMENT '订单状态：0-取消、1-下单、2-确认、3-备货、4-发货、5-完成',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_update_time` (`update_time`),
    CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 创建订单详情表
CREATE TABLE IF NOT EXISTS `order_item` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单详情ID，主键自增',
    `order_id` bigint NOT NULL COMMENT '订单ID，外键关联order表',
    `product_id` bigint NOT NULL COMMENT '商品ID，外键关联product表',
    `quantity` int NOT NULL DEFAULT '1' COMMENT '购买数量',
    `price` decimal(10,2) NOT NULL COMMENT '商品价格（快照）',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_product_id` (`product_id`),
    CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_order_item_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 创建订单操作日志表
CREATE TABLE IF NOT EXISTS `order_operate_log` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID，主键自增',
    `order_id` bigint NOT NULL COMMENT '订单ID，外键关联order表',
    `operate_user_id` bigint NOT NULL COMMENT '操作人ID，外键关联user表',
    `operate_type` varchar(50) NOT NULL COMMENT '操作类型：CONFIRM-确认、PREPARE-备货、DELIVER-发货、CANCEL_BY_CUSTOMER-客户取消、CANCEL_BY_MERCHANT-商家取消',
    `before_status` tinyint NOT NULL COMMENT '操作前状态',
    `after_status` tinyint NOT NULL COMMENT '操作后状态',
    `operate_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_operate_user_id` (`operate_user_id`),
    KEY `idx_operate_type` (`operate_type`),
    CONSTRAINT `fk_order_operate_log_order` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_order_operate_log_user` FOREIGN KEY (`operate_user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 创建支付记录表
CREATE TABLE IF NOT EXISTS `payment_record` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '支付记录ID，主键自增',
    `order_id` bigint NOT NULL COMMENT '订单ID，外键关联order表',
    `pay_type` varchar(20) NOT NULL COMMENT '支付方式（ALIPAY/WECHAT/CREDIT_CARD）',
    `amount` decimal(10,2) NOT NULL COMMENT '支付金额',
    `status` tinyint NOT NULL DEFAULT '0' COMMENT '支付状态：0-待支付，1-支付成功，2-支付失败，3-已取消',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_pay_type` (`pay_type`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_payment_order` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 创建支付配置表
CREATE TABLE IF NOT EXISTS `payment_config` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `pay_type` varchar(20) NOT NULL COMMENT '支付方式（ALIPAY/WECHAT/CREDIT_CARD）',
    `app_id` varchar(50) NOT NULL COMMENT '应用ID',
    `merchant_id` varchar(50) NOT NULL COMMENT '商户ID',
    `private_key` text NOT NULL COMMENT '私钥（加密存储）',
    `public_key` text NOT NULL COMMENT '公钥',
    `gateway_url` varchar(200) NOT NULL COMMENT '支付网关URL',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态（0：禁用，1：启用）',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_pay_type` (`pay_type`)
);

-- 创建统计缓存表
CREATE TABLE IF NOT EXISTS `stat_cache` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '统计缓存ID，主键自增',
    `stat_type` varchar(50) NOT NULL COMMENT '统计类型（MERCHANT_RANK/PRODUCT_SALES/PAY_TREND/MARKETING_EFFECT）',
    `time_type` varchar(20) NOT NULL COMMENT '时间维度（DAY/WEEK/MONTH/CUSTOM）',
    `shop_id` bigint DEFAULT NULL COMMENT '商铺ID（NULL表示全平台）',
    `stat_key` varchar(100) NOT NULL COMMENT '统计键（用于区分不同统计条件）',
    `stat_value` JSON NOT NULL COMMENT '统计结果（JSON格式）',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `expire_time` datetime NOT NULL COMMENT '过期时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_stat_key` (`stat_key`),
    KEY `idx_stat_type_time` (`stat_type`, `time_type`),
    KEY `idx_shop_id` (`shop_id`),
    KEY `idx_expire_time` (`expire_time`)
);

-- 创建商家申请表
CREATE TABLE IF NOT EXISTS `merchant_apply` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `merchant_name` varchar(100) NOT NULL COMMENT '商家名称',
    `legal_person` varchar(50) NOT NULL COMMENT '法人姓名',
    `id_card` varchar(50) NOT NULL COMMENT '法人身份证号（脱敏存储）',
    `business_license` varchar(50) NOT NULL COMMENT '营业执照编号',
    `phone` varchar(20) NOT NULL COMMENT '联系电话',
    `email` varchar(100) NOT NULL COMMENT '联系邮箱',
    `address` varchar(200) NOT NULL COMMENT '商家地址',
    `bank_account` varchar(50) NOT NULL COMMENT '对公银行卡号（脱敏存储）',
    `bank_name` varchar(100) NOT NULL COMMENT '开户银行',
    `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0-待审核，1-已通过，2-已拒绝',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
    `audit_user_id` bigint DEFAULT NULL COMMENT '审核人ID',
    `remark` varchar(500) DEFAULT NULL COMMENT '审核备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_merchant_name` (`merchant_name`),
    UNIQUE KEY `uk_business_license` (`business_license`),
    KEY `idx_status` (`status`),
    KEY `idx_audit_time` (`audit_time`)
);

-- 创建商铺申请表
CREATE TABLE IF NOT EXISTS `shop_apply` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `merchant_id` bigint NOT NULL COMMENT '商家ID（关联user表）',
    `shop_name` varchar(100) NOT NULL COMMENT '商铺名称',
    `shop_desc` varchar(500) DEFAULT NULL COMMENT '商铺描述',
    `category_id` bigint NOT NULL COMMENT '商铺分类ID',
    `logo_url` varchar(200) DEFAULT NULL COMMENT '商铺LOGO URL',
    `contact_phone` varchar(20) NOT NULL COMMENT '商铺联系电话',
    `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0-待审核，1-已通过，2-已拒绝',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
    `audit_user_id` bigint DEFAULT NULL COMMENT '审核人ID',
    `remark` varchar(500) DEFAULT NULL COMMENT '审核备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_shop_name` (`shop_name`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
);

-- 创建审核日志表
CREATE TABLE IF NOT EXISTS `audit_log` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `operate_type` varchar(50) NOT NULL COMMENT '操作类型：MERCHANT_AUDIT-商家审核，SHOP_AUDIT-商铺审核，SHOP_STATUS_UPDATE-商铺状态更新',
    `target_id` bigint NOT NULL COMMENT '目标ID（申请ID/商铺ID）',
    `operate_user_id` bigint NOT NULL COMMENT '操作人ID',
    `operate_result` tinyint NOT NULL COMMENT '操作结果：0-拒绝，1-通过/启用，2-禁用',
    `remark` varchar(500) DEFAULT NULL COMMENT '操作备注',
    `operate_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_operate_type` (`operate_type`),
    KEY `idx_target_id` (`target_id`),
    KEY `idx_operate_user_id` (`operate_user_id`),
    KEY `idx_operate_time` (`operate_time`)
);

-- 创建商品评价表
CREATE TABLE IF NOT EXISTS `comment` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID，主键自增',
    `order_item_id` bigint NOT NULL COMMENT '订单详情ID，关联订单、商品',
    `product_id` bigint NOT NULL COMMENT '商品ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `content` text NOT NULL COMMENT '评价内容',
    `score` tinyint NOT NULL COMMENT '评分，1-5分',
    `images` varchar(2000) DEFAULT NULL COMMENT '评价图片URL，逗号分隔',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_item_id` (`order_item_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_comment_order_item` FOREIGN KEY (`order_item_id`) REFERENCES `order_item` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_comment_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 创建优惠券表
CREATE TABLE IF NOT EXISTS `coupon` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `shop_id` bigint NOT NULL COMMENT '所属商铺ID',
    `coupon_type` varchar(20) NOT NULL COMMENT '优惠券类型：满减/FIXED_AMOUNT、折扣/DISCOUNT',
    `threshold` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '使用门槛（满减时必填，如满100可用；折扣券填0）',
    `value` decimal(10,2) NOT NULL COMMENT '优惠值（满减填金额，折扣填折扣率如0.8=8折）',
    `total_num` int NOT NULL COMMENT '总库存',
    `remain_num` int NOT NULL COMMENT '剩余库存',
    `start_time` datetime NOT NULL COMMENT '生效时间',
    `end_time` datetime NOT NULL COMMENT '失效时间',
    `product_ids` varchar(2000) DEFAULT NULL COMMENT '适用商品ID列表（逗号分隔，空则全店商品可用）',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1-生效，0-失效',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_shop_id_status` (`shop_id`,`status`),
    KEY `idx_start_time_end_time` (`start_time`,`end_time`),
    KEY `idx_coupon_type` (`coupon_type`),
    CONSTRAINT `fk_coupon_shop` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 创建打折活动表
CREATE TABLE IF NOT EXISTS `discount_activity` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '活动ID，主键自增',
    `shop_id` bigint NOT NULL COMMENT '所属商铺ID',
    `activity_name` varchar(50) NOT NULL COMMENT '活动名称',
    `discount_rate` decimal(10,2) NOT NULL COMMENT '折扣率（如0.8=8折）',
    `start_time` datetime NOT NULL COMMENT '生效时间',
    `end_time` datetime NOT NULL COMMENT '失效时间',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1-生效，0-失效',
    PRIMARY KEY (`id`),
    KEY `idx_shop_id_status` (`shop_id`,`status`),
    KEY `idx_activity_time` (`start_time`,`end_time`),
    CONSTRAINT `fk_discount_shop` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 创建活动商品关联表
CREATE TABLE IF NOT EXISTS `discount_activity_product` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID，主键自增',
    `activity_id` bigint NOT NULL COMMENT '活动ID',
    `product_id` bigint NOT NULL COMMENT '商品ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_activity_product` (`activity_id`,`product_id`),
    KEY `idx_activity_id` (`activity_id`),
    KEY `idx_product_id` (`product_id`),
    CONSTRAINT `fk_discount_activity` FOREIGN KEY (`activity_id`) REFERENCES `discount_activity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_discount_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 创建客户优惠券表
CREATE TABLE IF NOT EXISTS `user_coupon` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `coupon_id` bigint NOT NULL COMMENT '优惠券ID',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1-未使用，2-已使用，3-已过期',
    `receive_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
    `use_time` datetime DEFAULT NULL COMMENT '使用时间',
    `order_id` bigint DEFAULT NULL COMMENT '关联订单ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_coupon` (`user_id`,`coupon_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_coupon_id` (`coupon_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_user_coupon_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_user_coupon_coupon` FOREIGN KEY (`coupon_id`) REFERENCES `coupon` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_user_coupon_order` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
);

-- 为product表添加current_price和shop_id字段（如果不存在）
ALTER TABLE `product`
ADD COLUMN IF NOT EXISTS `current_price` decimal(10,2) DEFAULT NULL COMMENT '商品当前价格',
ADD COLUMN IF NOT EXISTS `shop_id` bigint DEFAULT NULL COMMENT '所属店铺ID',
ADD KEY IF NOT EXISTS `idx_shop_id` (`shop_id`);

-- 为product表添加外键约束（关联shop表）
ALTER TABLE `product`
ADD CONSTRAINT IF NOT EXISTS `fk_product_shop` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- 为product表添加version字段（乐观锁）
ALTER TABLE `product`
ADD COLUMN `version` int NOT NULL DEFAULT '1' COMMENT '版本号，用于乐观锁' AFTER `is_deleted`;

-- 为product表添加average_score和comment_count字段
ALTER TABLE `product`
ADD COLUMN `average_score` decimal(2,1) DEFAULT '0.0' COMMENT '平均评分，保留1位小数',
ADD COLUMN `comment_count` int DEFAULT '0' COMMENT '评价数量';