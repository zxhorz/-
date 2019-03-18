-- MySQL dump 10.13  Distrib 5.5.16, for Win64 (x86)
--
-- Host: localhost    Database: dorm_mg
-- ------------------------------------------------------
-- Server version	5.5.16

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `dorm`
--
use dorm_MG;

DROP TABLE IF EXISTS `dorm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dorm` (
  `id` varchar(45) NOT NULL,
  `volume` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dorm`
--

LOCK TABLES `dorm` WRITE;
/*!40000 ALTER TABLE `dorm` DISABLE KEYS */;
INSERT INTO `dorm` VALUES ('101',0),('102',0),('103',0),('104',0),('105',0),('106',0),('107',0),('108',0),('109',0),('110',0),('201',0),('202',0),('203',0),('204',0),('205',0),('206',0),('207',0),('208',0),('209',0),('210',0);
/*!40000 ALTER TABLE `dorm` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notice`
--

DROP TABLE IF EXISTS `notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notice` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(45) NOT NULL,
  `date` varchar(45) NOT NULL,
  `content` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notice`
--

LOCK TABLES `notice` WRITE;
/*!40000 ALTER TABLE `notice` DISABLE KEYS */;
INSERT INTO `notice` VALUES (1,'工卡遗失','2019/03/18','<p>ss</p>'),(2,'sss','2019/03/18','<p>sss</p>'),(3,'????','2019/03/18','<p>sss</p>');
/*!40000 ALTER TABLE `notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `operation_log`
--

DROP TABLE IF EXISTS `operation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operation_log`
--

LOCK TABLES `operation_log` WRITE;
/*!40000 ALTER TABLE `operation_log` DISABLE KEYS */;
INSERT INTO `operation_log` VALUES (9,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-04 11:00:27'),(10,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-14 14:26:34'),(11,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-15 11:35:15'),(12,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-15 12:12:13'),(13,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-15 12:16:14'),(14,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-15 12:24:08'),(15,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-15 12:27:43'),(16,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-15 12:31:57'),(17,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-15 13:21:37'),(18,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-15 13:26:29'),(19,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-15 13:27:53'),(20,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-15 13:30:08'),(21,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-15 13:33:50'),(22,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-15 13:35:48'),(23,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-15 13:45:32'),(24,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-15 13:48:37'),(25,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-15 13:59:43'),(26,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-15 14:05:04'),(27,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-15 14:11:08'),(28,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-15 14:13:25'),(29,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-15 14:16:49'),(30,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-15 14:18:04'),(31,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-15 14:22:27'),(32,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-15 14:24:22'),(33,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-15 14:26:10'),(34,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-15 14:28:23'),(35,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-21 10:50:18'),(36,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-21 11:24:21'),(37,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-21 11:27:34'),(38,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-21 11:40:07'),(39,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-21 12:09:34'),(40,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-21 12:29:59'),(41,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-21 12:32:05'),(42,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-21 13:08:00'),(43,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-21 13:28:24'),(44,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-21 13:32:51'),(45,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-21 13:46:24'),(46,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-21 13:49:31'),(47,'31501105',NULL,NULL,'LOGIN','UserId : 1 loged in the bwb system.','2019-01-21 16:50:02'),(48,'xihaozhou@hengtiansoft.com',NULL,NULL,'LOGIN','UserId : 3 loged in the bwb system.','2019-01-22 15:12:00');
/*!40000 ALTER TABLE `operation_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permission`
--

DROP TABLE IF EXISTS `permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `permission` varchar(45) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_role_permission_roleId` (`role_id`),
  CONSTRAINT `fk_role_permission_roleId` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permission`
--

LOCK TABLES `permission` WRITE;
/*!40000 ALTER TABLE `permission` DISABLE KEYS */;
INSERT INTO `permission` VALUES (1,'all',1);
/*!40000 ALTER TABLE `permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(45) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_role_userId` (`user_id`),
  CONSTRAINT `fk_user_role_userId` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'admin',1);
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `student` (
  `id` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  `dorm` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_dormId` (`dorm`),
  CONSTRAINT `fk_dormId` FOREIGN KEY (`dorm`) REFERENCES `dorm` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` VALUES ('31501105','zxh','101'),('xihaozhou@hengtiansoft.com','xxx','101');
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `activation_code` varchar(45) DEFAULT NULL,
  `state` varchar(45) NOT NULL,
  `send_time` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_userId` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'31501105','e10adc3949ba59abbe56e057f20f883e',NULL,'active',NULL),(2,'admin','e10adc3949ba59abbe56e057f20f883e',NULL,'active',NULL),(3,'xihaozhou@hengtiansoft.com','eba38517f67556e8e30c8bddca8fcfe0',NULL,'active',NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;


delimiter //
create procedure checkUser()
begin
update user SET send_time=send_time + 10;

delete from user where user.id in 
(select id from (SELECT user.id FROM user where send_time > 1440 and state = 'nonactive') as temp);
end
//
delimiter ;

create event ten_minute_event
on schedule every 10 minute STARTS TIMESTAMP '2019-01-01 00:00:00'
on completion preserve enable
do call checkUser();

set GLOBAL event_scheduler=1;

-- Dump completed on 2019-03-18 11:19:42
