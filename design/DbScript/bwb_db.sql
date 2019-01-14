# --------------------------------------------------------
# Host:                         localhost
# Server version:               5.6.17
# Server OS:                    Win64
# HeidiSQL version:             6.0.0.3603
# Date/time:                    2018-05-15 10:32:48
# --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

# Dumping database structure for bwb
DROP DATABASE IF EXISTS `bwb`;
CREATE DATABASE IF NOT EXISTS `bwb` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `bwb`;

# Dumping structure for table bwb.analysis_dependency
DROP TABLE IF EXISTS `analysis_dependency`;
CREATE TABLE IF NOT EXISTS `analysis_dependency` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `relier_id` int(10) NOT NULL,
  `reliered_id` int(10) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

# Dumping data for table bwb.analysis_dependency: ~0 rows (approximately)
/*!40000 ALTER TABLE `analysis_dependency` DISABLE KEYS */;
INSERT INTO `analysis_dependency` (`id`, `relier_id`, `reliered_id`) VALUES
	(1, 2, 1),
	(2, 3, 1),
	(3, 4, 1),
	(4, 5, 1),
	(5, 6, 1),
	(6, 7, 2),
	(7, 7, 3),
	(8, 7, 5),
	(9, 8, 1),
	(10, 9, 1);
/*!40000 ALTER TABLE `analysis_dependency` ENABLE KEYS */;

# Dumping structure for table bwb.analysis_type
DROP TABLE IF EXISTS `analysis_type`;
CREATE TABLE IF NOT EXISTS `analysis_type` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `analysis_name` varchar(50) NOT NULL,
  `discription` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

# Dumping data for table bwb.analysis_type: ~7 rows (approximately)
/*!40000 ALTER TABLE `analysis_type` DISABLE KEYS */;
INSERT INTO `analysis_type` (`id`, `analysis_name`, `discription`) VALUES
	(1, 'SO', 'generate system ontology'),
	(2, 'CLONE_CODE', 'do clone code analysis'),
	(3, 'COMPLEXITY', 'do complexity analysis'),
	(4, 'DATA_MAPPING', 'prepare  info of data mapping'),
	(5, 'DEAD_CODE', 'do dead code analysis'),
	(6, 'DEPENDENCY', 'prepare dependency information'),
	(7, 'COST_ESTIMATION', 'prepare data for cost estimation'),
	(8, 'CLUSTERING', 'prepare data for clustering'),
	(9, 'DATA_DEPENDENCY', 'prepare data for data dependency'),
	(10, 'SYSTEM_DOCUMENTATION', 'prepare data for system documentation');
/*!40000 ALTER TABLE `analysis_type` ENABLE KEYS */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

# Dumping structure for table bwb.group
DROP TABLE IF EXISTS `group`;
CREATE TABLE IF NOT EXISTS `group` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `project_id` varchar(45) NOT NULL,
  `description` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

# Dumping data for table bwb.group: ~0 rows (approximately)
DELETE FROM `group`;
/*!40000 ALTER TABLE `group` DISABLE KEYS */;
INSERT INTO `group` (`id`, `name`, `description`, `project_id`) VALUES
	(1, 'everyone', 'the default group', 'all');
/*!40000 ALTER TABLE `group` ENABLE KEYS */;


# Dumping structure for table bwb.operation_log
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE IF NOT EXISTS `operation_log` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `manipulator_id` varchar(45) NOT NULL,
  `manipulated_id` varchar(45),
  `project_id` varchar(45) DEFAULT NULL,
  `role_id` varchar(45) DEFAULT NULL,
  `operation_type` varchar(100) NOT NULL,
  `operation_detail` varchar(100),
  `operation_time` varchar(45) NOT NULL,
  `job_id` varchar(45),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

# Dumping data for table bwb.operation_log: ~0 rows (approximately)
DELETE FROM `operation_log`;
/*!40000 ALTER TABLE `operation_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `operation_log` ENABLE KEYS */;


# Dumping structure for table bwb.permission
DROP TABLE IF EXISTS `permission`;
CREATE TABLE IF NOT EXISTS `permission` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(60) NOT NULL,
  `description` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

# Dumping data for table bwb.permission: ~4 rows (approximately)
DELETE FROM `permission`;
/*!40000 ALTER TABLE `permission` DISABLE KEYS */;
INSERT INTO `permission` (`id`, `name`, `description`) VALUES
	(1, 'privilege_change', 'change the privilege of other user'),
	(2, 'analyse', 'analyse the project'),
	(3, 'read_ouput', 'read the output for specific project'),
	(4, 'read_code', 'read the code in specific project');
/*!40000 ALTER TABLE `permission` ENABLE KEYS */;


# Dumping structure for table bwb.project
DROP TABLE IF EXISTS `project`;
CREATE TABLE IF NOT EXISTS `project` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `path` varchar(100) NOT NULL,
  `creater_id` varchar(45) NOT NULL,
  `created_time` date NOT NULL,
  `description` VARCHAR(200) NOT NULL,
  `filemapping` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

# Dumping data for table bwb.project: ~0 rows (approximately)
DELETE FROM `project`;
/*!40000 ALTER TABLE `project` DISABLE KEYS */;
/*!40000 ALTER TABLE `project` ENABLE KEYS */;


# Dumping structure for table bwb.role
DROP TABLE IF EXISTS `role`;
CREATE TABLE IF NOT EXISTS `role` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

# Dumping data for table bwb.role: ~3 rows (approximately)
DELETE FROM `role`;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` (`id`, `name`, `description`) VALUES
	(1, 'project-admin', 'the admin for specific project'),
	(2, 'analyser', 'the user can analyse project'),
	(3, 'member', 'the user can only read the code and analysis resul');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;


# Dumping structure for table bwb.role_permission
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE IF NOT EXISTS `role_permission` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `role_id` varchar(45) NOT NULL,
  `permission_id` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;

# Dumping data for table bwb.role_permission: ~0 rows (approximately)
DELETE FROM `role_permission`;
/*!40000 ALTER TABLE `role_permission` DISABLE KEYS */;
INSERT INTO `role_permission` (`id`, `role_id`, `permission_id`) VALUES
	(1, '1', '1'),
	(2, '1', '2'),
	(3, '1', '3'),
	(4, '1', '4'),
	(5, '2', '2'),
	(6, '2', '3'),
	(7, '2', '4'),
	(8, '3', '3'),
	(9, '3', '4');
/*!40000 ALTER TABLE `role_permission` ENABLE KEYS */;

# Dumping structure for table bwb.user
DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `password` varchar(100) NOT NULL,
  `activation_code` varchar(100) NOT NULL,
  `state` varchar(50) NOT NULL,
  `send_time` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

# Dumping data for table bwb.user: ~0 rows (approximately)
DELETE FROM `user`;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` (`id`, `username`, `password`, `activation_code`, `state`) VALUES
	(1, 'Admin', '827ccb0eea8a706c4c34a16891f84e7b', 'a1b2', 'active');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;


# Dumping structure for table bwb.user_group
DROP TABLE IF EXISTS `user_group`;
CREATE TABLE IF NOT EXISTS `user_group` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` varchar(45) NOT NULL,
  `group_id` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

# Dumping data for table bwb.user_group: ~0 rows (approximately)
DELETE FROM `user_group`;
/*!40000 ALTER TABLE `user_group` DISABLE KEYS */;
INSERT INTO `user_group` (`id`, `user_id`, `group_id`) VALUES
	(1, '1', '1');
/*!40000 ALTER TABLE `user_group` ENABLE KEYS */;


# Dumping structure for table bwb.user_role_in_project
DROP TABLE IF EXISTS `user_role_in_project`;
CREATE TABLE IF NOT EXISTS `user_role_in_project` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` varchar(45) NOT NULL,
  `role_id` varchar(45) NOT NULL,
  `project_id` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

# Dumping data for table bwb.user_role_in_project: ~0 rows (approximately)
DELETE FROM `user_role_in_project`;
/*!40000 ALTER TABLE `user_role_in_project` DISABLE KEYS */;
INSERT INTO `user_role_in_project` (`id`, `user_id`, `role_id`, `project_id`) VALUES
	(1, '1', '1', 'all');
/*!40000 ALTER TABLE `user_role_in_project` ENABLE KEYS */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

DROP TABLE IF EXISTS `job_status`;
CREATE TABLE IF NOT EXISTS `job_status` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `projectId` varchar(45) NOT NULL,
  `job_name` varchar(45) NOT NULL,
  `analysis_type_id` varchar(45) NOT NULL,
  `is_incremental` varchar(45) NOT NULL DEFAULT 'n',
  `increment_base_version` varchar(45) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `stop_time` datetime DEFAULT NULL,
  `code_version` varchar(45) NOT NULL,
  `status` varchar(100) NOT NULL,
  `description` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=latin1;

# Dumping structure for table bwb.abbriviation_dict_corpus
DROP TABLE IF EXISTS `abbriviation_dict_corpus`;
CREATE TABLE IF NOT EXISTS `abbriviation_dict_corpus` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `abbr` varchar(50) NOT NULL,
  `full_phrase` varchar(50) NOT NULL,
  `business_domain_id` int(10) DEFAULT NULL,
  `organization_id` int(10) DEFAULT NULL,
  `system_id` int(10) DEFAULT NULL,
  `code_type` varchar(50) DEFAULT NULL,
  `frequency` varchar(50) DEFAULT NULL,
  `system_rank` varchar(50) DEFAULT NULL,
  `organization_rank` varchar(50) DEFAULT NULL,
  `business_domain_rank` varchar(50) DEFAULT NULL,
  `general_rank` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

# Dumping structure for table bwb.business_domains_corpus
DROP TABLE IF EXISTS `business_domains_corpus`;
CREATE TABLE IF NOT EXISTS `business_domains_corpus` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `domain_name` varchar(50) NOT NULL,
  `sub_domain_of` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

# Dumping structure for table bwb.organizations_corpus
DROP TABLE IF EXISTS `organizations_corpus`;
CREATE TABLE IF NOT EXISTS `organizations_corpus` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `organization_name` varchar(50) NOT NULL,
  `business_domain` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

# Dumping structure for table bwb.projects_corpus
DROP TABLE IF EXISTS `projects_corpus`;
CREATE TABLE IF NOT EXISTS `projects_corpus` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `system` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

# Dumping structure for table bwb.systems_corpus
DROP TABLE IF EXISTS `systems_corpus`;
CREATE TABLE IF NOT EXISTS `systems_corpus` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `system_name` varchar(50) NOT NULL,
  `organization` int(10) NOT NULL,
  `business_domain` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

# Dumping structure for table bwb.word_and_phrase_tag_corpus
DROP TABLE IF EXISTS `word_and_phrase_tag_corpus`;
CREATE TABLE IF NOT EXISTS `word_and_phrase_tag_corpus` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `phrase` varchar(50) NOT NULL,
  `tag` varchar(50) NOT NULL,
  `business_domain_id` int(10) DEFAULT NULL,
  `organization_id` int(10) DEFAULT NULL,
  `system_id` int(10) DEFAULT NULL,
  `code_type` varchar(50) DEFAULT NULL,
  `frequency` varchar(50) DEFAULT NULL,
  `system_rank` varchar(50) DEFAULT NULL,
  `organization_rank` varchar(50) DEFAULT NULL,
  `business_domain_rank` varchar(50) DEFAULT NULL,
  `general_rank` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



# Dumping structure for table bwb.custom_script_run_history
DROP TABLE IF EXISTS `custom_script_run_history`;
CREATE TABLE IF NOT EXISTS `custom_script_run_history` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `run_id` varchar(45) NOT NULL,
  `script_name` varchar(50) NOT NULL,
  `script_path` varchar(50) NOT NULL,
  `command_line_options` varchar(200) DEFAULT NULL,
  `based_projectId` varchar(45) NOT NULL,
  `start_time` datetime DEFAULT NULL,
  `stop_time` datetime DEFAULT NULL,
  `status` varchar(10) NOT NULL,
  `description` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;