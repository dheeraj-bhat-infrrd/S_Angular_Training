CREATE DATABASE  IF NOT EXISTS `ss_report_demo` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `ss_report_demo`;
drop table if exists `user_adoption_report`;

CREATE TABLE `user_adoption_report` (
  `user_adoption_report_id` int(11) NOT NULL AUTO_INCREMENT,
  `company_id` int(10) unsigned NOT NULL,
  `company_name` varchar(450) DEFAULT NULL,
  `region_id` int(10) unsigned NOT NULL,
  `region_name` varchar(450) DEFAULT NULL,
  `branch_id` int(10) unsigned NOT NULL,
  `branch_name` varchar(450) DEFAULT NULL,
  `invited_users` int(11) DEFAULT NULL,
  `active_users` int(11) DEFAULT NULL,
  `adoption_rate` decimal(6,2) DEFAULT NULL,
  PRIMARY KEY (`branch_id`,`region_id`,`company_id`),
  UNIQUE KEY `user_adoption_report_id_UNIQUE` (`user_adoption_report_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
