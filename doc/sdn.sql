/*
 Navicat MySQL Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 50726
 Source Host           : localhost:3306
 Source Schema         : sdn

 Target Server Type    : MySQL
 Target Server Version : 50726
 File Encoding         : 65001

 Date: 04/08/2019 16:08:03
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for device
-- ----------------------------
DROP TABLE IF EXISTS `device`;
CREATE TABLE `device`  (
  `device_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `mac_address` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '设备mac地址，16进制',
  `device_type` int(11) NOT NULL DEFAULT 1 COMMENT '设备类型，1-pc，2-服务器，3-外网',
  `user_id` int(11) NOT NULL COMMENT '外键，用户id',
  PRIMARY KEY (`device_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `device_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of device
-- ----------------------------
INSERT INTO `device` VALUES (1, '00:00:00:00:00:04', 1, 1);
INSERT INTO `device` VALUES (2, '00:00:00:00:00:03', 1, 2);
INSERT INTO `device` VALUES (3, '00:00:00:00:00:01', 1, 3);
INSERT INTO `device` VALUES (4, '00:00:00:00:00:02', 2, 4);
INSERT INTO `device` VALUES (5, '00:00:00:00:00:06', 3, 4);

-- ----------------------------
-- Table structure for rule
-- ----------------------------
DROP TABLE IF EXISTS `rule`;
CREATE TABLE `rule`  (
  `rule_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `user_level` int(11) NOT NULL DEFAULT 0 COMMENT '用户等级',
  `device_type` int(11) NOT NULL DEFAULT 0 COMMENT '设备类型',
  `dest_user_level` int(11) NOT NULL DEFAULT 0 COMMENT '目的用户等级',
  `dest_device_type` int(11) NOT NULL DEFAULT 0 COMMENT '目的设备类型',
  `action` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'deny' COMMENT '操作',
  PRIMARY KEY (`rule_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of rule
-- ----------------------------
INSERT INTO `rule` VALUES (1, 1, 1, 5, 2, 'deny');
INSERT INTO `rule` VALUES (2, 5, 3, 5, 2, 'deny');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户主键，自增',
  `username` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '用户名',
  `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '姓名',
  `sex` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '性别',
  `password` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '密码',
  `level` int(11) NOT NULL DEFAULT 1 COMMENT '用户权限等级，1-5',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'trainee', '张三', '男', '123456', 1);
INSERT INTO `user` VALUES (2, 'staff', '李四', '男', '123456', 2);
INSERT INTO `user` VALUES (3, 'boss', '王五', '女', '123456', 3);
INSERT INTO `user` VALUES (4, 'root', '赵六', '女', '123456', 5);

SET FOREIGN_KEY_CHECKS = 1;
