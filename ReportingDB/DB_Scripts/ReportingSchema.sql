CREATE DATABASE  IF NOT EXISTS `ss_reporting` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `ss_reporting`;
-- MySQL dump 10.13  Distrib 5.7.12, for Win32 (AMD64)
--
-- Host: localhost    Database: ss_reporting
-- ------------------------------------------------------
-- Server version	5.7.17-log

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
-- Table structure for table `branch`
--

DROP TABLE IF EXISTS `branch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `branch` (
  `BRANCH_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `REGION_ID` int(10) unsigned NOT NULL,
  `BRANCH` varchar(250) NOT NULL,
  `PROFILE_NAME` varchar(250) DEFAULT NULL,
  `IS_DEFAULT_BY_SYSTEM` int(1) NOT NULL COMMENT 'In case, the company does not have this profile, a default will be created by the system',
  `SETTINGS_LOCK_STATUS` varchar(50) NOT NULL DEFAULT '0',
  `SETTINGS_SET_STATUS` varchar(50) NOT NULL DEFAULT '0',
  `IS_ZILLOW_CONNECTED` int(1) NOT NULL DEFAULT '0',
  `ZILLOW_REVIEW_COUNT` int(1) NOT NULL DEFAULT '0',
  `ZILLOW_AVERAGE_SCORE` double DEFAULT '0',
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`BRANCH_ID`),
  KEY `fk_BRANCH_REGION1_idx` (`REGION_ID`),
  KEY `fk_BRANCH_COMPANY1_idx` (`COMPANY_ID`),
  CONSTRAINT `fk_BRANCH_COMPANY1` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_BRANCH_REGION1` FOREIGN KEY (`REGION_ID`) REFERENCES `region` (`REGION_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5694 DEFAULT CHARSET=utf8 COMMENT='Branch details under a region. In case, there are no branches under a region, a default row will be added.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `company`
--

DROP TABLE IF EXISTS `company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company` (
  `COMPANY_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `COMPANY` varchar(250) NOT NULL COMMENT 'Company name',
  `VERTICAL_ID` int(1) NOT NULL,
  `REGISTRATION_STAGE` varchar(45) DEFAULT NULL,
  `IS_REGISTRATION_COMPLETE` int(1) NOT NULL,
  `STATUS` int(1) NOT NULL,
  `BILLING_MODE` varchar(1) NOT NULL DEFAULT 'A',
  `SETTINGS_LOCK_STATUS` varchar(50) NOT NULL DEFAULT '0',
  `SETTINGS_SET_STATUS` varchar(50) NOT NULL DEFAULT '0',
  `IS_ZILLOW_CONNECTED` int(1) NOT NULL DEFAULT '0',
  `ZILLOW_REVIEW_COUNT` int(1) NOT NULL DEFAULT '0',
  `ZILLOW_AVERAGE_SCORE` double DEFAULT '0',
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`COMPANY_ID`),
  KEY `fk_COMPANY_VERTICALS_MASTER1_idx` (`VERTICAL_ID`),
  CONSTRAINT `fk_COMPANY_VERTICALS_MASTER1` FOREIGN KEY (`VERTICAL_ID`) REFERENCES `verticals_master` (`VERTICALS_MASTER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=912 DEFAULT CHARSET=utf8 COMMENT='Holds the company meta data';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `company_user_report`
--

DROP TABLE IF EXISTS `company_user_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company_user_report` (
  `company_user_report_id` varchar(45) NOT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `access_level` varchar(45) DEFAULT NULL,
  `branch_assignment` varchar(45) DEFAULT NULL,
  `region_assignment` varchar(45) DEFAULT NULL,
  `office_admin_privilage` varchar(45) DEFAULT NULL,
  `region_admin_privilage` varchar(45) DEFAULT NULL,
  `social_survey_invite_sent_date` datetime DEFAULT NULL,
  `email_verified` tinyint(4) DEFAULT NULL,
  `last_login_date` datetime DEFAULT NULL,
  `profile_complete` varchar(250) DEFAULT NULL,
  `disclaimer` text,
  `address` text,
  `facebook_data_connection` tinyint(4) DEFAULT NULL,
  `facebook_connection` tinyint(4) DEFAULT NULL,
  `fb_last_post_date` datetime DEFAULT NULL,
  `twitter_data_connection` tinyint(4) DEFAULT NULL,
  `twitter_connection` tinyint(4) DEFAULT NULL,
  `twitter_last_post_date` datetime DEFAULT NULL,
  `linkedin_data_connection` tinyint(4) DEFAULT NULL,
  `linkedin_connection` tinyint(4) DEFAULT NULL,
  `linkedin_last_post_date` datetime DEFAULT NULL,
  `google_url` text,
  `zillow_url` text,
  `yelp_url` text,
  `realtor_url` text,
  `google_business_url` text,
  `lending_tree_url` text,
  `email_verified_date` datetime DEFAULT NULL,
  `adoption_completed_date` datetime DEFAULT NULL,
  `last_survey_sent_date` datetime DEFAULT NULL,
  `last_survey_posted_date` datetime DEFAULT NULL,
  `social_survey_profile` varchar(45) DEFAULT NULL,
  `total_reviews` int(11) DEFAULT NULL,
  `ss_reviews` int(11) DEFAULT NULL,
  `zillow_reviews` int(11) DEFAULT NULL,
  `abusive_reviews` int(11) DEFAULT NULL,
  `third_party_reviews` int(11) DEFAULT NULL,
  PRIMARY KEY (`company_user_report_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `region`
--

DROP TABLE IF EXISTS `region`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `region` (
  `REGION_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `REGION` varchar(250) NOT NULL COMMENT 'Region name',
  `PROFILE_NAME` varchar(250) DEFAULT NULL,
  `IS_DEFAULT_BY_SYSTEM` int(1) NOT NULL COMMENT 'In case, the company does not have this profile, a default will be created by the system',
  `SETTINGS_LOCK_STATUS` varchar(50) NOT NULL DEFAULT '0',
  `SETTINGS_SET_STATUS` varchar(50) NOT NULL DEFAULT '0',
  `IS_ZILLOW_CONNECTED` int(1) NOT NULL DEFAULT '0',
  `ZILLOW_REVIEW_COUNT` int(1) NOT NULL DEFAULT '0',
  `ZILLOW_AVERAGE_SCORE` double DEFAULT '0',
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`REGION_ID`),
  KEY `fk_REGION_COMPANY1_idx` (`COMPANY_ID`),
  CONSTRAINT `fk_REGION_COMPANY1` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1569 DEFAULT CHARSET=utf8 COMMENT='Region details of a company. In case, if the admin decides there is no region for the company, then a default row will be added.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `survey_response`
--

DROP TABLE IF EXISTS `survey_response`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `survey_response` (
  `SURVEY_RESPONSE_ID` varchar(36) NOT NULL,
  `SURVEY_DETAILS_ID` varchar(36) DEFAULT NULL,
  `ANSWER` varchar(45) NOT NULL,
  `QUESTION` text NOT NULL,
  `QUESTION_TYPE` varchar(45) NOT NULL,
  PRIMARY KEY (`SURVEY_RESPONSE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `survey_results_company_report`
--

DROP TABLE IF EXISTS `survey_results_company_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `survey_results_company_report` (
  `survey_results_company_report_id` varchar(45) NOT NULL,
  `user_id` int(10) DEFAULT NULL,
  `user_first_name` varchar(45) DEFAULT NULL,
  `user_last_name` varchar(45) DEFAULT NULL,
  `customer_first_name` varchar(45) DEFAULT NULL,
  `customer_last_name` varchar(45) DEFAULT NULL,
  `survey_sent_date` datetime DEFAULT NULL,
  `survey_completed_date` datetime DEFAULT NULL,
  `time_interval` varchar(45) DEFAULT NULL,
  `survey_source` varchar(100) DEFAULT NULL,
  `survey_source_id` varchar(250) DEFAULT NULL,
  `survey_score` float DEFAULT NULL,
  `survey_response_id` varchar(36) DEFAULT NULL,
  `gateway` int(11) DEFAULT NULL,
  `customer_comment` text,
  `agreed_to_share` tinyint(4) DEFAULT NULL,
  `branch_name` text,
  `company_click_through` text,
  `region_click_through` text,
  `branch_click_through` text,
  PRIMARY KEY (`survey_results_company_report_id`),
  KEY `FK_SURVEY_RESULTS_COMPANY_SURVEY_RESPONSE_idx` (`survey_response_id`),
  CONSTRAINT `FK_SURVEY_RESULTS_COMPANY_SURVEY_RESPONSE` FOREIGN KEY (`survey_response_id`) REFERENCES `survey_response` (`SURVEY_RESPONSE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `survey_transaction_summary_report`
--

DROP TABLE IF EXISTS `survey_transaction_summary_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `survey_transaction_summary_report` (
  `survey_transaction_summary_report_id` varchar(45) NOT NULL,
  `user_id` int(10) unsigned DEFAULT NULL,
  `name` varchar(45) DEFAULT NULL,
  `nmls_id` int(11) DEFAULT NULL,
  `license_id` int(10) unsigned DEFAULT NULL,
  `company_name` text,
  `region_name` text,
  `branch_name` text,
  `total_reviews` int(11) DEFAULT NULL,
  `total_zillow_reviews` int(11) DEFAULT NULL,
  `total_third_party_reviews` int(11) DEFAULT NULL,
  `total_verified_customer_reviews` int(11) DEFAULT NULL,
  `total_unverified_customer_reviews` int(11) DEFAULT NULL,
  `total_ss_reviews` int(11) DEFAULT NULL,
  `total_abusive_reviews` int(11) DEFAULT NULL,
  `total_retake_reviews` int(11) DEFAULT NULL,
  `total_retake_completed` int(11) DEFAULT NULL,
  `transactions_received_by_source_count` int(11) DEFAULT NULL,
  `transactions_sent_count` int(11) DEFAULT NULL,
  `transaction_unprocessable_count` int(11) DEFAULT NULL,
  `transaction_clicked_count` int(11) DEFAULT NULL,
  `transaction_completed_count` int(11) DEFAULT NULL,
  `transaction_partially_completed_count` int(11) DEFAULT NULL,
  `transaction_unopened_count` int(11) DEFAULT NULL,
  `transaction_duplicate_count` int(11) DEFAULT NULL,
  `transactions_mismatched_count` int(11) DEFAULT NULL,
  `transactions_unassigned_count` int(11) DEFAULT NULL,
  PRIMARY KEY (`survey_transaction_summary_report_id`),
  KEY `FK_SURVEY_TRANSACTIONS_USERS_idx` (`user_id`),
  CONSTRAINT `FK_SURVEY_TRANSACTIONS_USERS` FOREIGN KEY (`user_id`) REFERENCES `users` (`USER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_adoption_branch`
--

DROP TABLE IF EXISTS `user_adoption_branch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_adoption_branch` (
  `user_adoption_branch_id` varchar(45) NOT NULL,
  `branch_id` int(10) unsigned DEFAULT NULL,
  `branch_name` varchar(250) DEFAULT NULL,
  `invited_users_num` int(11) DEFAULT NULL,
  `active_users_num` int(11) DEFAULT NULL,
  `adoption_rate` float DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  PRIMARY KEY (`user_adoption_branch_id`),
  KEY `FK_user_adoption_branch_branch_idx` (`branch_id`),
  CONSTRAINT `FK_user_adoption_branch_branch` FOREIGN KEY (`branch_id`) REFERENCES `branch` (`BRANCH_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_adoption_company`
--

DROP TABLE IF EXISTS `user_adoption_company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_adoption_company` (
  `user_adoption_company_id` varchar(45) NOT NULL,
  `company_id` int(10) unsigned DEFAULT NULL,
  `company_name` varchar(250) DEFAULT NULL,
  `invited_users_num` int(11) DEFAULT NULL,
  `active_users_num` int(11) DEFAULT NULL,
  `adoption_rate` float DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  PRIMARY KEY (`user_adoption_company_id`),
  KEY `FK_user_adoption_company_company_idx` (`company_id`),
  CONSTRAINT `FK_user_adoption_company_company` FOREIGN KEY (`company_id`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_adoption_region`
--

DROP TABLE IF EXISTS `user_adoption_region`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_adoption_region` (
  `user_adoption_region_id` varchar(45) NOT NULL,
  `region_id` int(10) unsigned DEFAULT NULL,
  `region_name` varchar(250) DEFAULT NULL,
  `invited_users_num` int(11) DEFAULT NULL,
  `active_users_num` int(11) DEFAULT NULL,
  `adoption_rate` float DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  PRIMARY KEY (`user_adoption_region_id`),
  KEY `FK_user_adoption_region_region_idx` (`region_id`),
  CONSTRAINT `FK_user_adoption_region_region` FOREIGN KEY (`region_id`) REFERENCES `region` (`REGION_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_rank_sps_data`
--

DROP TABLE IF EXISTS `user_rank_sps_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_rank_sps_data` (
  `user_rank_sps_data_id` varchar(45) NOT NULL,
  `user_id` int(10) unsigned DEFAULT NULL,
  `rank` float DEFAULT NULL,
  `sps_score` float DEFAULT NULL,
  PRIMARY KEY (`user_rank_sps_data_id`),
  KEY `FK_USER_RANK_SPS_USER_idx` (`user_id`),
  CONSTRAINT `FK_USER_RANK_SPS_USER` FOREIGN KEY (`user_id`) REFERENCES `users` (`USER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='It stores user ranking value and SPS score for each user.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_ranking_report`
--

DROP TABLE IF EXISTS `user_ranking_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_ranking_report` (
  `user_ranking_report_id` varchar(45) NOT NULL,
  `user_id` int(10) unsigned DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `nmlsId` int(11) DEFAULT NULL,
  `company_name` text,
  `region_name` text,
  `branch_name` text,
  `total_reviews` int(11) DEFAULT NULL,
  `average_score_of_reviews` float DEFAULT NULL,
  `user_rank_sps_data_id` varchar(45) DEFAULT NULL,
  `position_in_company` int(11) DEFAULT NULL,
  `position_in_region` int(11) DEFAULT NULL,
  `position_in_branch` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_ranking_report_id`),
  KEY `FK_USER_RANKING_USER_idx` (`user_id`),
  KEY `FK_USER_RANKING_RANK_SPS_idx` (`user_rank_sps_data_id`),
  CONSTRAINT `FK_USER_RANKING_RANK_SPS` FOREIGN KEY (`user_rank_sps_data_id`) REFERENCES `user_rank_sps_data` (`user_rank_sps_data_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_USER_RANKING_USER` FOREIGN KEY (`user_id`) REFERENCES `users` (`USER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `USER_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `LOGIN_NAME` varchar(100) NOT NULL,
  `LOGIN_PASSWORD` varchar(200) DEFAULT NULL,
  `EMAIL_ID` varchar(250) DEFAULT NULL,
  `FIRST_NAME` varchar(45) DEFAULT NULL,
  `LAST_NAME` varchar(45) DEFAULT NULL,
  `SOURCE` varchar(2) NOT NULL COMMENT 'Source of record',
  `SOURCE_USER_ID` int(11) DEFAULT NULL,
  `IS_ATLEAST_ONE_USERPROFILE_COMPLETE` int(1) NOT NULL,
  `IS_OWNER` int(1) DEFAULT '0',
  `STATUS` int(1) NOT NULL,
  `LAST_LOGIN` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `NUM_OF_LOGINS` int(11) NOT NULL,
  `SUPER_ADMIN` int(1) NOT NULL DEFAULT '0',
  `IS_ZILLOW_CONNECTED` int(1) NOT NULL DEFAULT '0',
  `ZILLOW_REVIEW_COUNT` int(1) NOT NULL DEFAULT '0',
  `ZILLOW_AVERAGE_SCORE` double DEFAULT '0',
  `CREATED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  `REGISTRATION_STAGE` varchar(45) DEFAULT 'NOT_STARTED',
  `FORCE_PASSWORD` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`USER_ID`),
  UNIQUE KEY `LOGIN_NAME_UNIQUE` (`LOGIN_NAME`,`COMPANY_ID`),
  KEY `fk_USERS_COMPANY_idx` (`COMPANY_ID`),
  CONSTRAINT `fk_USERS_COMPANY` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=37405 DEFAULT CHARSET=utf8 COMMENT='Holds the user details. A user can have multiple profiles as mapped with user profile table';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `verticals_master`
--

DROP TABLE IF EXISTS `verticals_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `verticals_master` (
  `VERTICALS_MASTER_ID` int(11) NOT NULL,
  `VERTICAL_NAME` varchar(45) NOT NULL,
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  `PRIORITY_ORDER` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`VERTICALS_MASTER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-06-20 17:22:15
