/*
Navicat MySQL Data Transfer

Source Server         : db
Source Server Version : 50721
Source Host           : localhost:3306
Source Database       : dorm_mg

Target Server Type    : MYSQL
Target Server Version : 50721
File Encoding         : 65001

Date: 2019-03-15 13:02:54
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for dorm
-- ----------------------------
DROP TABLE IF EXISTS `dorm`;
CREATE TABLE `dorm` (
  `id` varchar(45) NOT NULL,
  `volume` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of dorm
-- ----------------------------
INSERT INTO `dorm` VALUES ('101', '0');
INSERT INTO `dorm` VALUES ('102', '0');
INSERT INTO `dorm` VALUES ('103', '0');
INSERT INTO `dorm` VALUES ('104', '0');
INSERT INTO `dorm` VALUES ('105', '0');
INSERT INTO `dorm` VALUES ('106', '0');
INSERT INTO `dorm` VALUES ('107', '0');
INSERT INTO `dorm` VALUES ('108', '0');
INSERT INTO `dorm` VALUES ('109', '0');
INSERT INTO `dorm` VALUES ('110', '0');
INSERT INTO `dorm` VALUES ('201', '0');
INSERT INTO `dorm` VALUES ('202', '0');
INSERT INTO `dorm` VALUES ('203', '0');
INSERT INTO `dorm` VALUES ('204', '0');
INSERT INTO `dorm` VALUES ('205', '0');
INSERT INTO `dorm` VALUES ('206', '0');
INSERT INTO `dorm` VALUES ('207', '0');
INSERT INTO `dorm` VALUES ('208', '0');
INSERT INTO `dorm` VALUES ('209', '0');
INSERT INTO `dorm` VALUES ('210', '0');

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
  `id` varchar(10) NOT NULL,
  `title` varchar(40) DEFAULT NULL,
  `date` varchar(40) DEFAULT NULL,
  `content` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of notice
-- ----------------------------
INSERT INTO `notice` VALUES ('1', '1', '1', '1');

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `manipulator_id` varchar(45) NOT NULL,
  `manipulated_id` varchar(45) DEFAULT NULL,
  `role_id` varchar(45) DEFAULT NULL,
  `operation_type` varchar(100) NOT NULL,
  `operation_detail` varchar(100) DEFAULT NULL,
  `operation_time` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_operation_userId` (`manipulator_id`),
  CONSTRAINT `fk_user_operation_userId` FOREIGN KEY (`manipulator_id`) REFERENCES `user` (`name`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of operation_log
-- ----------------------------
INSERT INTO `operation_log` VALUES ('9', '31501105', null, null, 'LOGIN', 'UserId : 1 loged in the bwb system.', '2019-01-04 11:00:27');
INSERT INTO `operation_log` VALUES ('10', '31501105', null, null, 'LOGIN', 'UserId : 1 loged in the bwb system.', '2019-01-14 14:26:34');
INSERT INTO `operation_log` VALUES ('11', '31501105', null, null, 'LOGIN', 'UserId : 1 loged in the bwb system.', '2019-03-04 18:08:19');

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `permission` varchar(45) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_role_permission_roleId` (`role_id`),
  CONSTRAINT `fk_role_permission_roleId` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of permission
-- ----------------------------
INSERT INTO `permission` VALUES ('1', 'all', '1');

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(45) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_role_userId` (`user_id`),
  CONSTRAINT `fk_user_role_userId` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES ('1', 'admin', '1');

-- ----------------------------
-- Table structure for student
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `id` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  `dorm` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_dormId` (`dorm`),
  CONSTRAINT `fk_dormId` FOREIGN KEY (`dorm`) REFERENCES `dorm` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of student
-- ----------------------------
INSERT INTO `student` VALUES ('31501105', 'zxh', '101');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `activation_code` varchar(45) NOT NULL,
  `state` varchar(45) NOT NULL,
  `send_time` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_userId` (`name`),
  CONSTRAINT `fk_student_user_name` FOREIGN KEY (`name`) REFERENCES `student` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', '31501105', 'e10adc3949ba59abbe56e057f20f883e', '1111', 'active', null);

-- ----------------------------
-- Procedure structure for checkUser
-- ----------------------------
DROP PROCEDURE IF EXISTS `checkUser`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `checkUser`()
begin
update user SET send_time=send_time + 10;

delete from user where user.id in 
(select id from (SELECT user.id FROM user where send_time > 1440 and state = 'nonactive') as temp);
end
;;
DELIMITER ;

-- ----------------------------
-- Event structure for ten_minute_event
-- ----------------------------
DROP EVENT IF EXISTS `ten_minute_event`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` EVENT `ten_minute_event` ON SCHEDULE EVERY 10 MINUTE STARTS '2019-01-01 00:00:00' ON COMPLETION PRESERVE ENABLE DO call checkUser()
;;
DELIMITER ;
