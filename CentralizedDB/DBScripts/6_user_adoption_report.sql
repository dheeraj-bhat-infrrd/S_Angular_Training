CREATE DATABASE  IF NOT EXISTS `ss_report_demo` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `ss_report_demo`;
drop table if exists `user_adoption_report`;

CREATE TABLE `user_adoption_report` (
  `user_adoption_report_id` varchar(36) NOT NULL,
  `company_id` int(10) unsigned DEFAULT NULL,
  `company_name` varchar(45) DEFAULT NULL,
  `region_id` int(10) unsigned DEFAULT NULL,
  `region_name` varchar(45) DEFAULT NULL,
  `branch_id` int(10) unsigned DEFAULT NULL,
  `branch_name` varchar(45) DEFAULT NULL,
  `invited_users` int(11) DEFAULT NULL,
  `active_users` int(11) DEFAULT NULL,
  `adoption_rate` decimal(6,2) DEFAULT NULL,
  PRIMARY KEY (`user_adoption_report_id`),
  KEY `FK_ADOPTION_COMPANY_idx` (`company_id`),
  KEY `FK_ADOPTION_REGION_idx` (`region_id`),
  KEY `FK_ADOPTION_BRANCH_idx` (`branch_id`),
  CONSTRAINT `FK_ADOPTION_BRANCH` FOREIGN KEY (`branch_id`) REFERENCES `branch` (`BRANCH_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_ADOPTION_COMPANY` FOREIGN KEY (`company_id`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_ADOPTION_REGION` FOREIGN KEY (`region_id`) REFERENCES `region` (`REGION_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
