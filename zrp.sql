/*
 Navicat Premium Data Transfer

 Source Server         : 本地连接
 Source Server Type    : MySQL
 Source Server Version : 80013
 Source Host           : localhost:3306
 Source Schema         : zrp

 Target Server Type    : MySQL
 Target Server Version : 80013
 File Encoding         : 65001

 Date: 14/08/2020 16:37:07
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '管理员id',
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '管理员账号名称',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '管理员账号密码',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE COMMENT '管理员账号不允许重复'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for client
-- ----------------------------
DROP TABLE IF EXISTS `client`;
CREATE TABLE `client`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '唯一id',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名，不允许重复',
  `client_key` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户授权码，不允许重复',
  `status` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户当前状态',
  `start_time` datetime(0) NOT NULL COMMENT '开始授权时间',
  `stop_time` datetime(0) NOT NULL COMMENT '授权截止时间',
  `comment` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '授权备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE COMMENT '客户名，不允许重复',
  UNIQUE INDEX `client_key`(`client_key`) USING BTREE COMMENT '授权码，不允许重复'
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for log
-- ----------------------------
DROP TABLE IF EXISTS `log`;
CREATE TABLE `log`  (
  `id` int(255) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志id',
  `client_key` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '授权码',
  `port` int(6) UNSIGNED NULL DEFAULT NULL COMMENT '流量通过的端口',
  `flow` double(10, 3) UNSIGNED NULL DEFAULT NULL COMMENT '流量，以kb为单位',
  `date` date NULL DEFAULT NULL COMMENT '记录时间，按天记录',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1241 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
