CREATE DATABASE  IF NOT EXISTS `ss_report_demo`;
USE `ss_report_demo`;

DROP TABLE IF EXISTS `overview_branch_month`;

CREATE TABLE `overview_branch_month` (
  `overview_branch_id` varchar(36) NOT NULL,
  `branch_id` int(10) unsigned DEFAULT NULL,
  `sps_score` int(11) NOT NULL DEFAULT '0',
  `total_detractors` int(11) NOT NULL DEFAULT '0',
  `detractors_percentage` int(11) NOT NULL DEFAULT '0',
  `total_passives` int(11) NOT NULL DEFAULT '0',
  `passives_percentage` int(11) NOT NULL DEFAULT '0',
  `total_promoters` int(11) NOT NULL DEFAULT '0',
  `promoter_percentage` int(11) NOT NULL DEFAULT '0',
  `total_corrupted` int(11) NOT NULL DEFAULT '0',
  `corrupted_percentage` int(11) NOT NULL DEFAULT '0',
  `total_mismatched` int(11) NOT NULL DEFAULT '0',
  `mismatched_percentage` int(11) NOT NULL DEFAULT '0',
  `total_duplicate` int(11) NOT NULL DEFAULT '0',
  `duplicate_percentage` int(11) NOT NULL DEFAULT '0',
  `total_archieved` int(11) NOT NULL DEFAULT '0',
  `archieved_percentage` int(11) NOT NULL DEFAULT '0',
  `total_incomplete_transactions` int(11) NOT NULL DEFAULT '0',
  `total_survey_sent` int(11) NOT NULL DEFAULT '0',
  `total_survey_completed` int(11) NOT NULL DEFAULT '0',
  `total_social_post` int(11) NOT NULL DEFAULT '0',
  `total_zillow_reviews` int(11) NOT NULL DEFAULT '0',
  `month` int(11),
  `year` int(11),
  PRIMARY KEY (`overview_branch_id`),
  KEY `FX_OVERVIEW_BRANCH_MONTH_idx` (`branch_id`),
  CONSTRAINT `FX_OVERVIEW_BRANCH_MONTH` FOREIGN KEY (`branch_id`) REFERENCES `branch` (`BRANCH_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `overview_company_month`;

CREATE TABLE `overview_company_month` (
  `overview_company_id` varchar(36) NOT NULL,
  `company_id` int(10) unsigned DEFAULT NULL,
  `sps_score` int(11) NOT NULL DEFAULT '0',
  `total_detractors` int(11) NOT NULL DEFAULT '0',
  `detractors_percentage` int(11) NOT NULL DEFAULT '0',
  `total_passives` int(11) NOT NULL DEFAULT '0',
  `passives_percentage` int(11) NOT NULL DEFAULT '0',
  `total_promoters` int(11) NOT NULL DEFAULT '0',
  `promoter_percentage` int(11) NOT NULL DEFAULT '0',
  `total_corrupted` int(11) NOT NULL DEFAULT '0',
  `corrupted_percentage` int(11) NOT NULL DEFAULT '0',
  `total_mismatched` int(11) NOT NULL DEFAULT '0',
  `mismatched_percentage` int(11) NOT NULL DEFAULT '0',
  `total_duplicate` int(11) NOT NULL DEFAULT '0',
  `duplicate_percentage` int(11) NOT NULL DEFAULT '0',
  `total_archieved` int(11) NOT NULL DEFAULT '0',
  `archieved_percentage` int(11) NOT NULL DEFAULT '0',
  `total_incomplete_transactions` int(11) NOT NULL DEFAULT '0',
  `total_survey_sent` int(11) NOT NULL DEFAULT '0',
  `total_survey_completed` int(11) NOT NULL DEFAULT '0',
  `total_social_post` int(11) NOT NULL DEFAULT '0',
  `total_zillow_reviews` int(11) NOT NULL DEFAULT '0',
  `month` int(11),
  `year` int(11),
  PRIMARY KEY (`overview_company_id`),
  KEY `FX_OVERVIEW_COMPANY_MONTH_idx` (`company_id`),
  CONSTRAINT `FX_OVERVIEW_COMPANY_MONTH` FOREIGN KEY (`company_id`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `overview_region_month`;

CREATE TABLE `overview_region_month` (
  `overview_region_id` varchar(36) NOT NULL,
  `region_id` int(10) unsigned DEFAULT NULL,
  `sps_score` int(11) NOT NULL DEFAULT '0',
  `total_detractors` int(11) NOT NULL DEFAULT '0',
  `detractors_percentage` int(11) NOT NULL DEFAULT '0',
  `total_passives` int(11) NOT NULL DEFAULT '0',
  `passives_percentage` int(11) NOT NULL DEFAULT '0',
  `total_promoters` int(11) NOT NULL DEFAULT '0',
  `promoter_percentage` int(11) NOT NULL DEFAULT '0',
  `total_corrupted` int(11) NOT NULL DEFAULT '0',
  `corrupted_percentage` int(11) NOT NULL DEFAULT '0',
  `total_mismatched` int(11) NOT NULL DEFAULT '0',
  `mismatched_percentage` int(11) NOT NULL DEFAULT '0',
  `total_duplicate` int(11) NOT NULL DEFAULT '0',
  `duplicate_percentage` int(11) NOT NULL DEFAULT '0',
  `total_archieved` int(11) NOT NULL DEFAULT '0',
  `archieved_percentage` int(11) NOT NULL DEFAULT '0',
  `total_incomplete_transactions` int(11) NOT NULL DEFAULT '0',
  `total_survey_sent` int(11) NOT NULL DEFAULT '0',
  `total_survey_completed` int(11) NOT NULL DEFAULT '0',
  `total_social_post` int(11) NOT NULL DEFAULT '0',
  `total_zillow_reviews` int(11) NOT NULL DEFAULT '0',
  `month` int(11),
  `year` int(11),
  PRIMARY KEY (`overview_region_id`),
  KEY `FX_OVERVIEW_REGION_MONTH_idx` (`region_id`),
  CONSTRAINT `FX_OVERVIEW_REGION_MONTH` FOREIGN KEY (`region_id`) REFERENCES `region` (`REGION_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `overview_user_month`;

CREATE TABLE `overview_user_month` (
 `overview_user_id` varchar(36) NOT NULL,
  `user_id` int(10) unsigned DEFAULT NULL,
  `sps_score` int(11) NOT NULL DEFAULT '0',
  `total_detractors` int(11) NOT NULL DEFAULT '0',
  `detractors_percentage` int(11) NOT NULL DEFAULT '0',
  `total_passives` int(11) NOT NULL DEFAULT '0',
  `passives_percentage` int(11) NOT NULL DEFAULT '0',
  `total_promoters` int(11) NOT NULL DEFAULT '0',
  `promoter_percentage` int(11) NOT NULL DEFAULT '0',
  `total_corrupted` int(11) NOT NULL DEFAULT '0',
  `corrupted_percentage` int(11) NOT NULL DEFAULT '0',
  `total_mismatched` int(11) NOT NULL DEFAULT '0',
  `mismatched_percentage` int(11) NOT NULL DEFAULT '0',
  `total_duplicate` int(11) NOT NULL DEFAULT '0',
  `duplicate_percentage` int(11) NOT NULL DEFAULT '0',
  `total_archieved` int(11) NOT NULL DEFAULT '0',
  `archieved_percentage` int(11) NOT NULL DEFAULT '0',
  `total_incomplete_transactions` int(11) NOT NULL DEFAULT '0',
  `total_survey_sent` int(11) NOT NULL DEFAULT '0',
  `total_survey_completed` int(11) NOT NULL DEFAULT '0',
  `total_social_post` int(11) NOT NULL DEFAULT '0',
  `total_zillow_reviews` int(11) NOT NULL DEFAULT '0',
  `month` INT(11),
  `year` INT(11),
  PRIMARY KEY (`overview_user_id`),
  KEY `FX_OVERVIEW_USER_MONTH_idx` (`user_id`),
  CONSTRAINT `FX_OVERVIEW_USER_MONTH` FOREIGN KEY (`user_id`) REFERENCES `users` (`USER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;