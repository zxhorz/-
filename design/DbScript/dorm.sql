-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               5.5.16 - MySQL Community Server (GPL)
-- Server OS:                    Win64
-- HeidiSQL Version:             9.5.0.5196
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for dorm_mg
CREATE DATABASE IF NOT EXISTS `dorm_mg` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `dorm_mg`;

-- Dumping structure for table dorm_mg.application
CREATE TABLE IF NOT EXISTS `application` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` varchar(45) NOT NULL,
  `subject` varchar(45) NOT NULL,
  `student_id` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL,
  `type` varchar(45) NOT NULL,
  `priority` varchar(45) NOT NULL,
  `content` text NOT NULL,
  `info` varchar(45) NOT NULL,
  `status` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- Dumping data for table dorm_mg.application: ~2 rows (approximately)
/*!40000 ALTER TABLE `application` DISABLE KEYS */;
REPLACE INTO `application` (`id`, `date`, `subject`, `student_id`, `email`, `type`, `priority`, `content`, `info`, `status`) VALUES
	(8, '2019/04/12', '1', '31501105', '15382327056@163.com', '????', '?', '', '', 'isFinished'),
	(9, '2019/04/15', '1', '31501105', '15382327056@163.com', '??', '?', '', '', 'isWaiting');
/*!40000 ALTER TABLE `application` ENABLE KEYS */;

-- Dumping structure for procedure dorm_mg.checkUser
DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `checkUser`()
begin
update user SET send_time = send_time + 10;

delete from user where user.id in 
(select id from (SELECT user.id FROM user where send_time > 1440 and state = 'nonactive') as temp);
end//
DELIMITER ;

-- Dumping structure for table dorm_mg.dorm
CREATE TABLE IF NOT EXISTS `dorm` (
  `id` varchar(45) NOT NULL,
  `volume` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table dorm_mg.dorm: ~33 rows (approximately)
/*!40000 ALTER TABLE `dorm` DISABLE KEYS */;
REPLACE INTO `dorm` (`id`, `volume`) VALUES
	('101', 4),
	('102', 4),
	('103', 4),
	('104', 4),
	('105', 4),
	('119', 4),
	('120', 4),
	('121', 4),
	('122', 4),
	('123', 4),
	('124', 4),
	('125', 4),
	('126', 4),
	('127', 4),
	('128', 4),
	('129', 4),
	('130', 4),
	('131', 4),
	('132', 4),
	('133', 4),
	('134', 4),
	('135', 4),
	('136', 4),
	('201', 4),
	('202', 4),
	('203', 4),
	('204', 4),
	('205', 4),
	('206', 4),
	('207', 4),
	('208', 4),
	('209', 4),
	('210', 4);
/*!40000 ALTER TABLE `dorm` ENABLE KEYS */;

-- Dumping structure for table dorm_mg.notice
CREATE TABLE IF NOT EXISTS `notice` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(45) NOT NULL,
  `date` varchar(45) NOT NULL,
  `content` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- Dumping data for table dorm_mg.notice: ~4 rows (approximately)
/*!40000 ALTER TABLE `notice` DISABLE KEYS */;
REPLACE INTO `notice` (`id`, `title`, `date`, `content`) VALUES
	(1, '1', '2019/11/06', '1'),
	(2, '1', '2019/01/02', '1'),
	(3, '1', '2019/02/01', '1'),
	(4, '1', '2018/01/06', '1');
/*!40000 ALTER TABLE `notice` ENABLE KEYS */;

-- Dumping structure for table dorm_mg.operation_log
CREATE TABLE IF NOT EXISTS `operation_log` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `manipulator_id` varchar(45) NOT NULL,
  `manipulated_id` varchar(45) DEFAULT NULL,
  `role_id` varchar(45) DEFAULT NULL,
  `operation_type` varchar(100) NOT NULL,
  `operation_detail` varchar(100) DEFAULT NULL,
  `operation_time` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_operation_userId` (`manipulator_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- Dumping data for table dorm_mg.operation_log: ~8 rows (approximately)
/*!40000 ALTER TABLE `operation_log` DISABLE KEYS */;
REPLACE INTO `operation_log` (`id`, `manipulator_id`, `manipulated_id`, `role_id`, `operation_type`, `operation_detail`, `operation_time`) VALUES
	(1, '31501105', NULL, NULL, 'LOGIN', 'UserId : 1 loged in the bwb system.', '2019-05-09 15:32:03'),
	(2, '31501105', NULL, NULL, 'LOGIN', 'UserId : 1 loged in the bwb system.', '2019-05-09 15:38:48'),
	(3, '31501105', NULL, NULL, 'LOGIN', 'UserId : 1 loged in the bwb system.', '2019-05-09 16:03:28'),
	(4, '31501000', NULL, NULL, 'LOGIN', 'UserId : 4 loged in the bwb system.', '2019-05-09 16:31:52'),
	(5, '31501000', NULL, NULL, 'LOGIN', 'UserId : 4 loged in the bwb system.', '2019-05-09 16:39:45'),
	(6, 'admin', NULL, NULL, 'LOGIN', 'UserId : 1 loged in the bwb system.', '2019-05-09 16:40:38'),
	(7, '31501000', NULL, NULL, 'LOGIN', 'UserId : 4 loged in the bwb system.', '2019-05-09 16:44:54'),
	(8, 'admin', NULL, NULL, 'LOGIN', 'UserId : 1 loged in the bwb system.', '2019-05-09 16:45:18');
/*!40000 ALTER TABLE `operation_log` ENABLE KEYS */;

-- Dumping structure for table dorm_mg.permission
CREATE TABLE IF NOT EXISTS `permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `permission` varchar(45) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_role_permission_roleId` (`role_id`),
  CONSTRAINT `fk_role_permission_roleId` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

-- Dumping data for table dorm_mg.permission: ~14 rows (approximately)
/*!40000 ALTER TABLE `permission` DISABLE KEYS */;
REPLACE INTO `permission` (`id`, `permission`, `role_id`) VALUES
	(1, 'all', 1),
	(2, 'all', 3),
	(3, 'all', 4),
	(4, 'all', 5),
	(5, 'all', 6),
	(6, 'all', 7),
	(7, 'all', 8),
	(8, 'all', 9),
	(9, 'all', 10),
	(10, 'all', 11),
	(11, 'all', 12),
	(12, 'all', 13),
	(13, 'all', 14),
	(14, 'all', 15);
/*!40000 ALTER TABLE `permission` ENABLE KEYS */;

-- Dumping structure for table dorm_mg.role
CREATE TABLE IF NOT EXISTS `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(45) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `level` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_role_userId` (`user_id`),
  CONSTRAINT `fk_user_role_userId` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

-- Dumping data for table dorm_mg.role: ~15 rows (approximately)
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
REPLACE INTO `role` (`id`, `role_name`, `user_id`, `level`) VALUES
	(1, 'root', 1, 3),
	(2, 'user', 2, 1),
	(3, 'user', 4, 1),
	(4, 'user', 5, 1),
	(5, 'user', 6, 1),
	(6, 'user', 7, 1),
	(7, 'user', 8, 1),
	(8, 'user', 9, 1),
	(9, 'user', 10, 1),
	(10, 'user', 11, 1),
	(11, 'user', 12, 1),
	(12, 'user', 13, 1),
	(13, 'user', 14, 1),
	(14, 'user', 15, 1),
	(15, 'user', 16, 1);
/*!40000 ALTER TABLE `role` ENABLE KEYS */;

-- Dumping structure for table dorm_mg.student
CREATE TABLE IF NOT EXISTS `student` (
  `id` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  `dorm` varchar(45) NOT NULL,
  `branch` varchar(45) NOT NULL,
  `tel` varchar(45) NOT NULL,
  `class` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL,
  `dorm_pos` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_dormId` (`dorm`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table dorm_mg.student: ~14 rows (approximately)
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
REPLACE INTO `student` (`id`, `name`, `dorm`, `branch`, `tel`, `class`, `email`, `dorm_pos`) VALUES
	('31501000', 'zzz', '101', '1', '1', '1', '15382327056@163.com', '1'),
	('31501001', '??', '102', '1', '1', '3', '15382327056@163.com', '1'),
	('31501002', '??', '101', '1', '1', '1', '15382327056@163.com', '2'),
	('31501003', '??', '103', '1', '1', '2', '15382327056@163.com', '1'),
	('31501004', '??', '101', '1', '1', '1', '15382327056@163.com', '3'),
	('31501006', '??', '104', '2', '1', '1', '15382327056@163.com', '1'),
	('31501007', '??', '105', '2', '1', '4', '15382327056@163.com', '1'),
	('31501008', '??', '104', '2', '1', '1', '15382327056@163.com', '2'),
	('31501009', '10', '105', '2', '1', '4', '15382327056@163.com', '2'),
	('31501010', '11', '104', '2', '1', '1', '15382327056@163.com', '3'),
	('31501011', '12', '104', '2', '1', '1', '15382327056@163.com', '4'),
	('31501012', '13', '103', '2', '1', '2', '15382327056@163.com', '2'),
	('31501013', '14', '119', '2', '1', '1', '15382327056@163.com', '1'),
	('31501105', '郑十', '101', '1', '1', '1', '15382327056@163.com', '4');
/*!40000 ALTER TABLE `student` ENABLE KEYS */;

-- Dumping structure for event dorm_mg.ten_minute_event
DELIMITER //
CREATE DEFINER=`root`@`localhost` EVENT `ten_minute_event` ON SCHEDULE EVERY 10 MINUTE STARTS '2019-01-01 00:00:00' ON COMPLETION PRESERVE ENABLE DO call checkUser()//
DELIMITER ;

-- Dumping structure for table dorm_mg.user
CREATE TABLE IF NOT EXISTS `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `activation_code` varchar(45) DEFAULT NULL,
  `state` varchar(45) NOT NULL,
  `send_time` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_userId` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;

-- Dumping data for table dorm_mg.user: ~15 rows (approximately)
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
REPLACE INTO `user` (`id`, `name`, `password`, `activation_code`, `state`, `send_time`) VALUES
	(1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', NULL, 'active', NULL),
	(2, '31501105', 'e10adc3949ba59abbe56e057f20f883e', NULL, 'active', NULL),
	(4, '31501000', 'e1fd31e7b07c8cab14add2f02569b138', NULL, 'active', NULL),
	(5, '31501001', 'f1e771333dfa4d56d3e3a0affc392a2a', NULL, 'active', NULL),
	(6, '31501002', '5ba14476534fed600005504cc2f9ac18', NULL, 'active', NULL),
	(7, '31501003', 'c90bcc4bd7b1f223593395c6bc5a0dc7', NULL, 'active', NULL),
	(8, '31501004', '132a186e17de6b32cd1b21693a6cd6c0', NULL, 'active', NULL),
	(9, '31501006', 'b74febb5dba93f147ac6d1d3bd8309ef', NULL, 'active', NULL),
	(10, '31501007', 'f88b7d67ecc2f549234f4758a0f57a91', NULL, 'active', NULL),
	(11, '31501008', '1716775d01fd9b805aff5201be5578e6', NULL, 'active', NULL),
	(12, '31501009', '86023c4c68373014f09e4ad60e03bf7e', NULL, 'active', NULL),
	(13, '31501010', 'bfc0678bcbedb7fa4822477d31c8ee3d', NULL, 'active', NULL),
	(14, '31501011', '2c410776955566674edf8861b7f1eae9', NULL, 'active', NULL),
	(15, '31501012', 'b8671cf35e5356d7f5e5e49535c5c822', NULL, 'active', NULL),
	(16, '31501013', '6ee918e07e60aa95ee0be7bd2956c7fe', NULL, 'active', NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

