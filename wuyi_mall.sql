/*
 Navicat Premium Data Transfer

 Source Server         : Mysql
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : localhost:3306
 Source Schema         : wuyi_mall

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 09/12/2025 21:25:40
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for cart_item
-- ----------------------------
DROP TABLE IF EXISTS `cart_item`;
CREATE TABLE `cart_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `quantity` int NOT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_cart_item_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_cart_item_product_id`(`product_id` ASC) USING BTREE,
  CONSTRAINT `fk_cart_item_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_cart_item_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of cart_item
-- ----------------------------
INSERT INTO `cart_item` VALUES (26, 12, 18, 1, '2025-12-09 20:58:50');

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `parent_id` bigint NULL DEFAULT NULL,
  `sort` int NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_parent_id`(`parent_id` ASC) USING BTREE,
  CONSTRAINT `fk_category_parent` FOREIGN KEY (`parent_id`) REFERENCES `category` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, '书画', NULL, 1);
INSERT INTO `category` VALUES (2, '雕刻', NULL, 2);
INSERT INTO `category` VALUES (16, '山水画', 1, 1);
INSERT INTO `category` VALUES (17, '版画', 1, 2);
INSERT INTO `category` VALUES (18, '泥塑', 2, 1);
INSERT INTO `category` VALUES (19, '木雕', 2, 2);
INSERT INTO `category` VALUES (29, '现代工艺品', NULL, 3);
INSERT INTO `category` VALUES (30, '黑旋风李逵', 29, 1);

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK7mwcjtab8q4b44jw3bur1okk8`(`user_id` ASC, `product_id` ASC) USING BTREE,
  INDEX `idx_favorite_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_favorite_product_id`(`product_id` ASC) USING BTREE,
  CONSTRAINT `fk_favorite_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of favorite
-- ----------------------------
INSERT INTO `favorite` VALUES (13, 19, 16, '2025-12-08 20:54:31');
INSERT INTO `favorite` VALUES (28, 12, 18, '2025-12-09 21:05:40');

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` bigint NOT NULL,
  `total_amount` decimal(38, 2) NOT NULL DEFAULT 0.00,
  `status` int NOT NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_order_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_order_status`(`status` ASC) USING BTREE,
  INDEX `idx_order_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES (12, '17652788700251ca2a0d5', 12, 210.00, 1, '2025-12-09 19:14:30');
INSERT INTO `order` VALUES (13, '17652830114080164edbb', 12, 39200.00, 1, '2025-12-09 20:23:31');
INSERT INTO `order` VALUES (14, '1765283162398626020c3', 12, 10.00, 1, '2025-12-09 20:26:02');
INSERT INTO `order` VALUES (15, '1765284263499e4bbdbf7', 12, 10.00, 1, '2025-12-09 20:44:23');
INSERT INTO `order` VALUES (16, '1765284511573c8f019c1', 12, 110.00, 0, '2025-12-09 20:48:31');

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `product_price` decimal(10, 2) NOT NULL,
  `quantity` int NOT NULL,
  `create_time` datetime(6) NULL DEFAULT CURRENT_TIMESTAMP(6),
  `receive_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `receive_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `receive_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `total_price` decimal(10, 2) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKs234mi6jususbx4b37k44cipy`(`order_id` ASC) USING BTREE,
  INDEX `idx_order_item_product_id`(`product_id` ASC) USING BTREE,
  CONSTRAINT `fk_order_item_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `FKs234mi6jususbx4b37k44cipy` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (12, 12, 16, '测试商品2', 200.00, 1, '2025-12-09 19:14:30.037019', '沈哲梁', '018267573025', '下沙区学正街17号', 200.00);
INSERT INTO `order_item` VALUES (13, 12, 18, '我是商品', 10.00, 1, '2025-12-09 19:14:30.054812', '沈哲梁', '018267573025', '下沙区学正街17号', 10.00);
INSERT INTO `order_item` VALUES (14, 13, 16, '测试商品2', 200.00, 196, '2025-12-09 20:23:31.426309', '泛溪', '13670165161', '下沙区学正街17号', 39200.00);
INSERT INTO `order_item` VALUES (15, 14, 18, '我是商品', 10.00, 1, '2025-12-09 20:26:02.413076', '泛溪', '13670165163', '下沙区学正街17号', 10.00);
INSERT INTO `order_item` VALUES (16, 15, 18, '我是商品', 10.00, 1, '2025-12-09 20:44:23.637911', '泛溪', '13670165161', '下沙区学正街17号', 10.00);
INSERT INTO `order_item` VALUES (17, 16, 18, '我是商品', 10.00, 1, '2025-12-09 20:48:31.590114', '泛溪', '13670165161', '下沙区学正街17号', 10.00);
INSERT INTO `order_item` VALUES (18, 16, 19, '我是商品2', 100.00, 1, '2025-12-09 20:48:31.604728', '泛溪', '13670165161', '下沙区学正街17号', 100.00);

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_id` bigint NULL DEFAULT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `price` decimal(38, 2) NULL DEFAULT NULL,
  `stock` int NOT NULL,
  `main_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '',
  `status` int NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_product_status`(`status` ASC) USING BTREE,
  INDEX `idx_product_name`(`name` ASC) USING BTREE,
  CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (16, 17, '测试商品2', '哈哈哈哈哈', 200.00, 0, 'https://images.pexels.com/photos/29538904/pexels-photo-29538904.jpeg', 1, '2025-12-05 18:48:21');
INSERT INTO `product` VALUES (17, 18, '我是商品', '新增商品', 123.00, 200, 'https://cdn.pixabay.com/photo/2025/10/26/13/00/ai-generated-9917901_1280.png', 1, '2025-12-08 13:58:35');
INSERT INTO `product` VALUES (18, 19, '我是商品', '11', 10.00, 96, 'https://cdn.pixabay.com/photo/2025/10/26/13/00/ai-generated-9917901_1280.png', 1, '2025-12-09 17:43:39');
INSERT INTO `product` VALUES (19, 16, '我是商品2', '11', 100.00, 99, 'https://cdn.pixabay.com/photo/2025/10/26/13/00/ai-generated-9917901_1280.png', 1, '2025-12-09 19:18:47');

-- ----------------------------
-- Table structure for product_image
-- ----------------------------
DROP TABLE IF EXISTS `product_image`;
CREATE TABLE `product_image`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `sort` int NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_image
-- ----------------------------
INSERT INTO `product_image` VALUES (1, 16, 'https://cdn.pixabay.com/photo/2024/03/30/15/51/cat-8664948_1280.jpg', 1, '2025-12-09 15:09:28');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` int NULL DEFAULT NULL,
  `is_admin` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `idx_user_phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (12, 'wuyi', '$2a$10$dBGrek5eSSID1eL/unEy9Od8.iJOKACDZazGYRdm7ymAX.VXikyxi', 'wuyi', NULL, '2025-12-07 00:07:40', NULL, '', NULL, 0, 0);
INSERT INTO `user` VALUES (13, 'testuser3', '$2a$10$SzG/8RnUdN.WE3snypQLtu23TpzIY.lLd.0x4fJ7/04/bBBIXI.Uu', 'testuser3', NULL, '2025-12-07 00:15:55', NULL, '', NULL, 0, 0);
INSERT INTO `user` VALUES (19, 'admin', '$2a$10$E6aM7ZdsJKo0ia6f6TF8j.6xI2k91nqxFawQP.8bvxMl9X7d8R1hy', 'admin', NULL, '2025-12-07 20:36:54', NULL, '', NULL, 0, 1);

SET FOREIGN_KEY_CHECKS = 1;
