CREATE DATABASE  IF NOT EXISTS `ss_centralized_mongodb` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `ss_centralized_mongodb`;
-- MySQL dump 10.13  Distrib 5.7.12, for Win32 (AMD64)
--
-- Host: localhost    Database: ss_centralized_mongodb
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
-- Table structure for table `abuse_reporter_details`
--

DROP TABLE IF EXISTS `abuse_reporter_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `abuse_reporter_details` (
  `ABUSE_REPORTER_DETAILS_ID` varchar(36) NOT NULL,
  `SURVEY_ID` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`ABUSE_REPORTER_DETAILS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `abuse_reporters`
--

DROP TABLE IF EXISTS `abuse_reporters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `abuse_reporters` (
  `ABUSE_REPORTERS_ID` int(11) NOT NULL,
  `ABUSE_REPORTER_DETAILS_ID` varchar(36) DEFAULT NULL,
  `REPORTER_NAME` varchar(45) DEFAULT NULL,
  `REPORTER_EMAIL` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`ABUSE_REPORTERS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `accounts_master`
--

DROP TABLE IF EXISTS `accounts_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `accounts_master` (
  `ACCOUNTS_MASTER_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ACCOUNT_NAME` varchar(45) NOT NULL,
  `MAX_USERS_ALLOWED` int(11) NOT NULL,
  `MIN_USERS_ALLOWED` int(11) NOT NULL DEFAULT '-1',
  `MAX_TIME_VALIDITY_ALLOWED_IN_DAYS` int(11) NOT NULL,
  `AMOUNT` float NOT NULL,
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`ACCOUNTS_MASTER_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='Holds the details of accounts possible in the application.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `agent_settings`
--

DROP TABLE IF EXISTS `agent_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `agent_settings` (
  `AGENT_SETTINGS_ID` varchar(36) NOT NULL,
  `USER_ID` int(10) unsigned DEFAULT NULL,
  `SOCIAL_MEDIA_TOKENS_ID` varchar(45) DEFAULT NULL,
  `STATUS` varchar(45) DEFAULT NULL,
  `CREATED_BY` varchar(45) DEFAULT NULL,
  `CREATED_ON_MONGO` int(11) DEFAULT NULL,
  `CREATED_ON` datetime DEFAULT NULL,
  `MODIFIED_BY` varchar(45) DEFAULT NULL,
  `MODIFIED_ON_MONGO` int(11) DEFAULT NULL,
  `MODIFIED_ON` datetime DEFAULT NULL,
  `ALLOW_OVERRIDE_FOR_SOCIAL_MEDIA` tinyint(4) DEFAULT NULL,
  `ALLOW_ZILLOW_AUTO_POST` tinyint(4) DEFAULT NULL,
  `DISCLAIMER` varchar(45) DEFAULT NULL,
  `EXPERTISE` text,
  `HIDDEN_AGENT_NAME` tinyint(4) DEFAULT NULL,
  `HIDDEN_SECTION` tinyint(4) DEFAULT NULL,
  `HOBBIES` text,
  `IS_ACCOUNT_DISABLED` tinyint(4) DEFAULT NULL,
  `IS_DEFAULT_BY_SYSTEM` tinyint(4) DEFAULT NULL,
  `IS_LOCATION_ENABLED` tinyint(4) DEFAULT NULL,
  `IS_LOGO_IMAGE_PROCESSED` tinyint(4) DEFAULT NULL,
  `IS_PROFILE_IMAGE_PROCESSED` tinyint(4) DEFAULT NULL,
  `IS_SEO_CONTENT_MODIFIED` tinyint(4) DEFAULT NULL,
  `LOGO` varchar(45) DEFAULT NULL,
  `LOGO_THUMBNAIL` varchar(45) DEFAULT NULL,
  `PROFILE_COMPLETION` float DEFAULT NULL,
  `PROFILE_IMAGE_URL` longtext,
  `PROFILE_IMAGE_URL_THUMBNAIL` longtext,
  `PROFILE_NAME` varchar(45) DEFAULT NULL,
  `PROFILE_URL` varchar(45) DEFAULT NULL,
  `REVIEW_COUNT` int(11) DEFAULT NULL,
  `SEND_EMAIL_FROM_COMPANY` tinyint(4) DEFAULT NULL,
  `USER_ENCRYPTED_ID` varchar(45) DEFAULT NULL,
  `VENDASTA_ACCESSIBLE` tinyint(4) DEFAULT NULL,
  `VERTICAL` varchar(45) DEFAULT NULL,
  `ZILLOW_REVIEW_AVERAGE` float DEFAULT NULL,
  `ZILLOW_REVIEW_COUNT` int(11) DEFAULT NULL,
  PRIMARY KEY (`AGENT_SETTINGS_ID`),
  KEY `AGENT_ID_idx` (`USER_ID`),
  KEY `FK_SOCIAL_MEDIA_TOKEN_ID_idx` (`SOCIAL_MEDIA_TOKENS_ID`),
  CONSTRAINT `FK_SOCIAL_MEDIA_TOKEN_ID` FOREIGN KEY (`SOCIAL_MEDIA_TOKENS_ID`) REFERENCES `social_media_tokens` (`SOCIAL_MEDIA_TOKENS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FX1_USER_ID` FOREIGN KEY (`USER_ID`) REFERENCES `users` (`USER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `agent_settings_licenses`
--

DROP TABLE IF EXISTS `agent_settings_licenses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `agent_settings_licenses` (
  `AGENT_SETTINGS_LICENSES_ID` int(10) NOT NULL,
  `AGENT_SETTINGS_ID` varchar(36) DEFAULT NULL,
  `AUTHORIZED_IN` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`AGENT_SETTINGS_LICENSES_ID`),
  KEY `GX1_AGENT_SETTINGS_ID_idx` (`AGENT_SETTINGS_ID`),
  CONSTRAINT `GX1_AGENT_SETTINGS_ID` FOREIGN KEY (`AGENT_SETTINGS_ID`) REFERENCES `agent_settings` (`AGENT_SETTINGS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `api_request_details`
--

DROP TABLE IF EXISTS `api_request_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `api_request_details` (
  `API_REQUEST_DETAILS_ID` varchar(36) NOT NULL,
  `COMPANY_ID` int(10) unsigned DEFAULT NULL,
  `CREATED_ON` datetime DEFAULT NULL,
  PRIMARY KEY (`API_REQUEST_DETAILS_ID`),
  KEY `FK_API_REQUEST_DETAILS_COMPANY_COMPANY_ID_idx` (`COMPANY_ID`),
  CONSTRAINT `FK_API_REQUEST_DETAILS_COMPANY_COMPANY_ID` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `associations`
--

DROP TABLE IF EXISTS `associations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `associations` (
  `ASSOCIATIONS_ID` int(11) NOT NULL,
  `AGENT_SETTINGS_ID` varchar(36) DEFAULT NULL,
  `NAME` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`ASSOCIATIONS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `batch_tracker`
--

DROP TABLE IF EXISTS `batch_tracker`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `batch_tracker` (
  `BATCH_TRACKER_ID` int(11) NOT NULL AUTO_INCREMENT,
  `BATCH_TYPE` varchar(45) NOT NULL,
  `BATCH_NAME` text NOT NULL,
  `LAST_START_TIME` timestamp NOT NULL DEFAULT '1970-01-01 18:30:00',
  `LAST_END_TIME` timestamp NOT NULL DEFAULT '1970-01-01 18:30:00',
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  `ERROR` text,
  `DESCRIPTION` text,
  PRIMARY KEY (`BATCH_TRACKER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=5674 DEFAULT CHARSET=utf8 COMMENT='Branch details under a region. In case, there are no branches under a region, a default row will be added.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `branch_city_history`
--

DROP TABLE IF EXISTS `branch_city_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `branch_city_history` (
  `BRANCH_CITY_HISTORY_ID` int(10) NOT NULL,
  `HIERARCHY_UPLOAD_BRANCHES_ID` int(10) DEFAULT NULL,
  `TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`BRANCH_CITY_HISTORY_ID`),
  KEY `HIERARCHY_UPLOAD_BRANCHES_ID_idx` (`HIERARCHY_UPLOAD_BRANCHES_ID`),
  CONSTRAINT `HIERARCHY_UPLOAD_BRANCHES_ID` FOREIGN KEY (`HIERARCHY_UPLOAD_BRANCHES_ID`) REFERENCES `hierarchy_upload_branches` (`HIERARCHY_UPLOAD_BRANCHES_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `branch_settings`
--

DROP TABLE IF EXISTS `branch_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `branch_settings` (
  `BRANCH_SETTINGS_ID` varchar(36) NOT NULL,
  `BRANCH_ID` int(10) unsigned DEFAULT NULL,
  `SOCIAL_MEDIA_TOKENS_ID` varchar(45) DEFAULT NULL,
  `CREATED_BY` varchar(45) DEFAULT NULL,
  `CREATED_ON_MONGO` int(17) DEFAULT NULL,
  `CREATED_ON` datetime DEFAULT NULL,
  `MODIFIED_BY` varchar(45) DEFAULT NULL,
  `MODIFIED_ON_MONGO` int(17) DEFAULT NULL,
  `MODIFIED_ON` datetime DEFAULT NULL,
  `STATUS` varchar(45) DEFAULT NULL,
  `ALLOW_OVERRIDE_FOR_SOCIAL_MEDIA` varchar(5) DEFAULT NULL,
  `ALLOW_ZILLOW_AUTO_POST` varchar(5) DEFAULT NULL,
  `HIDDEN_SECTION` varchar(5) DEFAULT NULL,
  `IS_ACCOUNT_DISABLED` varchar(5) DEFAULT NULL,
  `IS_DEFAULT_BY_SYSTEM` varchar(5) DEFAULT NULL,
  `IS_LOCATION_ENABLED` varchar(5) DEFAULT NULL,
  `IS_LOGO_IMAGE_PROCESSED` varchar(5) DEFAULT NULL,
  `IS_PROFILE_IMAGE_PROCESSED` varchar(5) DEFAULT NULL,
  `IS_SEO_CONTENT_MODIFIED` varchar(5) DEFAULT NULL,
  `LOGO` blob,
  `LOGO_THUMBNAIL` text,
  `PROFILE_COMPLETION` int(11) DEFAULT NULL,
  `PROFILE_IMAGE_URL` text,
  `PROFILE_IMAGE_URL_THUMBNAIL` text,
  `PROFILE_NAME` varchar(100) DEFAULT NULL,
  `PROFILE_URL` text,
  `SEND_EMAIL_FROM_COMPANY` varchar(5) DEFAULT NULL,
  `VENDASTA_ACCESSIBLE` varchar(5) DEFAULT NULL,
  `VERTICAL` varchar(45) DEFAULT NULL,
  `ZILLOW_REVIEW_AVERAGE` float DEFAULT NULL,
  `ZILLOW_REVIEW_COUNT` int(10) DEFAULT NULL,
  PRIMARY KEY (`BRANCH_SETTINGS_ID`),
  KEY `FX1_BRANCH_ID_idx` (`BRANCH_ID`),
  KEY `FK_BRS_SOCIAL_MEDIA_TOKEN_idx` (`SOCIAL_MEDIA_TOKENS_ID`),
  CONSTRAINT `FK_BRS_SOCIAL_MEDIA_TOKEN` FOREIGN KEY (`SOCIAL_MEDIA_TOKENS_ID`) REFERENCES `social_media_tokens` (`SOCIAL_MEDIA_TOKENS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FX1_BRANCH_ID` FOREIGN KEY (`BRANCH_ID`) REFERENCES `branch` (`BRANCH_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `branch_state_history`
--

DROP TABLE IF EXISTS `branch_state_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `branch_state_history` (
  `BRANCH_STATE_HISTORY_ID` int(10) NOT NULL,
  `HIERARCHY_UPLOAD_BRANCHES_ID` int(10) DEFAULT NULL,
  `TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`BRANCH_STATE_HISTORY_ID`),
  KEY `FX_HIERARCHY_UPLOAD_BRANCHES_ID_idx` (`HIERARCHY_UPLOAD_BRANCHES_ID`),
  CONSTRAINT `FX_HIERARCHY_UPLOAD_BRANCHES_ID` FOREIGN KEY (`HIERARCHY_UPLOAD_BRANCHES_ID`) REFERENCES `hierarchy_upload_branches` (`HIERARCHY_UPLOAD_BRANCHES_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `collection_dotloop_profilemapping`
--

DROP TABLE IF EXISTS `collection_dotloop_profilemapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `collection_dotloop_profilemapping` (
  `COLLECTION_PROFILE_MAPPING_ID` bigint(11) NOT NULL AUTO_INCREMENT,
  `COMPANY_ID` bigint(11) NOT NULL DEFAULT '-1',
  `PROFILE_ID` varchar(255) NOT NULL,
  `PROFILE_EMAIL_ADDRESS` varchar(100) DEFAULT NULL,
  `PROFILE_NAME` varchar(100) DEFAULT NULL,
  `PROFILE_ACTIVE` tinyint(4) DEFAULT NULL,
  `REGION_ID` bigint(11) NOT NULL DEFAULT '-1',
  `BRANCH_ID` bigint(11) NOT NULL DEFAULT '-1',
  `AGENT_ID` bigint(11) NOT NULL DEFAULT '-1',
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `CREATED_ON` timestamp NULL DEFAULT NULL,
  `MODIFIED_BY` varchar(255) DEFAULT NULL,
  `MODIFIED_ON` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`COLLECTION_PROFILE_MAPPING_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1161 DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB AUTO_INCREMENT=904 DEFAULT CHARSET=utf8 COMMENT='Holds the company meta data';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `company_dotloop_profilemapping`
--

DROP TABLE IF EXISTS `company_dotloop_profilemapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company_dotloop_profilemapping` (
  `COMPANY_PROFILE_MAPPING_ID` bigint(11) NOT NULL AUTO_INCREMENT,
  `COMPANY_ID` bigint(11) NOT NULL,
  `PROFILE_ID` varchar(255) NOT NULL,
  `PROFILE_EMAIL_ADDRESS` varchar(100) DEFAULT NULL,
  `PROFILE_NAME` varchar(100) DEFAULT NULL,
  `PROFILE_ACTIVE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`COMPANY_PROFILE_MAPPING_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `company_hidden_notification`
--

DROP TABLE IF EXISTS `company_hidden_notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company_hidden_notification` (
  `COMPANY_HIDDEN_NOTIFICATION_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `STATUS` int(1) NOT NULL,
  `HIDDEN` tinyint(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`COMPANY_HIDDEN_NOTIFICATION_ID`),
  KEY `fk_Company_Hidden_Notification_idx` (`COMPANY_ID`),
  CONSTRAINT `fk_Company_Hidden_Notification` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='Update hiddenFromSearchResults Field each time when batch runs.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `company_ignored_email_mapping`
--

DROP TABLE IF EXISTS `company_ignored_email_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company_ignored_email_mapping` (
  `COMPANY_IGNORED_EMAIL_MAPPING_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `EMAIL_ID` varchar(255) NOT NULL,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) DEFAULT NULL,
  `MODIFIED_ON` timestamp NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`COMPANY_IGNORED_EMAIL_MAPPING_ID`),
  KEY `fk_COMPANY_IGNORED_EMAIL_MAPPING_1_idx` (`COMPANY_ID`),
  CONSTRAINT `fk_COMPANY_IGNORED_EMAIL_MAPPING_1` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `company_invitation_license_key`
--

DROP TABLE IF EXISTS `company_invitation_license_key`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company_invitation_license_key` (
  `COMPANY_INVITATION_LICENSE_KEY_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ACCOUNTS_MASTER_ID` int(10) unsigned NOT NULL,
  `LICENSE_KEY` varchar(100) DEFAULT NULL COMMENT 'License key against which registration will be validated.',
  `VALID_UNTIL` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`COMPANY_INVITATION_LICENSE_KEY_ID`),
  UNIQUE KEY `LICENSE_KEY_UNIQUE` (`LICENSE_KEY`),
  KEY `fk_COMPANY_INVITATION_LICENSE_KEY_ACCOUNTS_MASTER1_idx` (`ACCOUNTS_MASTER_ID`),
  CONSTRAINT `fk_COMPANY_INVITATION_LICENSE_KEY_ACCOUNTS_MASTER1` FOREIGN KEY (`ACCOUNTS_MASTER_ID`) REFERENCES `accounts_master` (`ACCOUNTS_MASTER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='This table will store the license key if the invitation is sent by SAAS control panel. The account created using this key, will not be tracked for payment from the application. The license table will set the payment mode to ''M'' for accounts created by this key.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `company_settings`
--

DROP TABLE IF EXISTS `company_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company_settings` (
  `COMPANY_SETTINGS_ID` varchar(36) NOT NULL,
  `COMPANY_ID` int(10) unsigned DEFAULT NULL,
  `SOCIAL_MEDIA_TOKENS_ID` varchar(45) DEFAULT NULL,
  `CREATED_BY` varchar(45) DEFAULT NULL,
  `CREATED_ON_MONGO` int(10) DEFAULT NULL,
  `CREATED_ON` datetime DEFAULT NULL,
  `MODIFIED_BY` varchar(45) DEFAULT NULL,
  `MODIFIED_ON_MONGO` int(10) DEFAULT NULL,
  `MODIFIED_ON` datetime DEFAULT NULL,
  `STATUS` varchar(45) DEFAULT NULL,
  `UNIQUE_IDENTIFIER` varchar(45) DEFAULT NULL,
  `ALLOW_OVERRIDE_FOR_SOCIAL_MEDIA` tinyint(4) DEFAULT NULL,
  `ALLOW_ZILLOW_AUTO_POST` tinyint(4) DEFAULT NULL,
  `DISCLAIMER` varchar(45) DEFAULT NULL,
  `HIDDEN_SECTION` tinyint(4) DEFAULT NULL,
  `HIDE_SECTIONS_FROM_PROFILE_PAGE` text,
  `IS_ACCOUNT_DISABLED` tinyint(4) DEFAULT NULL,
  `IS_DEFAULT_BY_SYSTEM` tinyint(4) DEFAULT NULL,
  `IS_LOCATION_ENABLED` tinyint(4) DEFAULT NULL,
  `IS_LOGO_IMAGE_PROCESSED` tinyint(4) DEFAULT NULL,
  `IS_PROFILE_IMAGE_PROCESSED` tinyint(4) DEFAULT NULL,
  `IS_SEO_CONTENT_MODIFIED` tinyint(4) DEFAULT NULL,
  `LOGO` varchar(45) DEFAULT NULL,
  `LOGO_THUMBNAIL` varchar(45) DEFAULT NULL,
  `PROFILE_COMPLETION` int(11) DEFAULT NULL,
  `PROFILE_IMAGE_URL` varchar(45) DEFAULT NULL,
  `PROFILE_IMAGE_URL_THUMBNAIL` varchar(45) DEFAULT NULL,
  `PROFILE_NAME` varchar(45) DEFAULT NULL,
  `PROFILE_URL` varchar(45) DEFAULT NULL,
  `REVIEW_SORT_CRITERIA` varchar(45) DEFAULT NULL,
  `SEND_EMAIL_FROM_COMPANY` tinyint(4) DEFAULT NULL,
  `SEND_EMAIL_THROUGH` varchar(45) DEFAULT NULL,
  `VENDASTA_ACCESSIBLE` tinyint(4) DEFAULT NULL,
  `VERTICAL` varchar(45) DEFAULT NULL,
  ` ZILLOW_REVIEW_AVERAGE` float DEFAULT NULL,
  `ZILLOW_REVIEW_COUNT` int(10) DEFAULT NULL,
  PRIMARY KEY (`COMPANY_SETTINGS_ID`),
  KEY `COMPANY_ID_idx` (`COMPANY_ID`),
  KEY `FK_CS_SOCIAL_MEDIA_TOKEN_idx` (`SOCIAL_MEDIA_TOKENS_ID`),
  CONSTRAINT `FK_CS_SOCIAL_MEDIA_TOKEN` FOREIGN KEY (`SOCIAL_MEDIA_TOKENS_ID`) REFERENCES `social_media_tokens` (`SOCIAL_MEDIA_TOKENS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FX_COMPANY_ID` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `complaint_res_settings`
--

DROP TABLE IF EXISTS `complaint_res_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `complaint_res_settings` (
  `COMPLAINT_RES_SETTINGS_ID` int(11) NOT NULL,
  `SURVEY_SETTINGS_ID` int(11) DEFAULT NULL,
  `ENABLED` tinyint(4) DEFAULT NULL,
  `MAIL_ID` varchar(45) DEFAULT NULL,
  `MOOD` varchar(45) DEFAULT NULL,
  `RATING` int(11) DEFAULT NULL,
  PRIMARY KEY (`COMPLAINT_RES_SETTINGS_ID`),
  KEY `SURVEY_SETTINGS_ID                                         _idx` (`SURVEY_SETTINGS_ID`),
  CONSTRAINT `SURVEY_SETTINGS_ID` FOREIGN KEY (`SURVEY_SETTINGS_ID`) REFERENCES `survey_settings` (`SURVEY_SETTINGS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contact_details`
--

DROP TABLE IF EXISTS `contact_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contact_details` (
  `CONTACT_DETAILS _ID` int(11) NOT NULL,
  `AGENT_SETTINGS_ID` varchar(36) DEFAULT NULL,
  `BRANCH_SETTINGS_ID` varchar(36) DEFAULT NULL,
  `REGION_SETTINGS_ID` varchar(36) DEFAULT NULL,
  `COMPANY_SETTINGS_ID` varchar(36) DEFAULT NULL,
  `SURVEY_DETAILS_ID` varchar(36) DEFAULT NULL,
  `ABOUT_ME` varchar(45) DEFAULT NULL,
  `ADDRESS` varchar(45) DEFAULT NULL,
  `ADDRESS1` varchar(45) DEFAULT NULL,
  `ADDRESS2` varchar(45) DEFAULT NULL,
  `CITY` varchar(45) DEFAULT NULL,
  `COUNTRY` varchar(45) DEFAULT NULL,
  `COUNTRY_CODE` varchar(45) DEFAULT NULL,
  `FIRST_NAME` varchar(45) DEFAULT NULL,
  `LAST_NAME` varchar(45) DEFAULT NULL,
  `INDUSTRY` varchar(45) DEFAULT NULL,
  `LOCATION` varchar(45) DEFAULT NULL,
  `NAME` varchar(45) DEFAULT NULL,
  `STATE` varchar(45) DEFAULT NULL,
  `TITLE` varchar(45) DEFAULT NULL,
  `ZIPCODE` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`CONTACT_DETAILS _ID`),
  KEY `AGENT_SETTINGS_ID_idx` (`AGENT_SETTINGS_ID`),
  KEY `BRANCH_SETTINGS_ID                                         _idx` (`BRANCH_SETTINGS_ID`),
  KEY ` REGION_SETTINGS_ID                                        _idx` (`REGION_SETTINGS_ID`),
  KEY `COMPANY_SETTINGS_ID                                        _idx` (`COMPANY_SETTINGS_ID`),
  KEY `SURVEY_DETAILS_ID                                          _idx` (`SURVEY_DETAILS_ID`),
  CONSTRAINT `AGENT_SETTINGS_ID` FOREIGN KEY (`AGENT_SETTINGS_ID`) REFERENCES `agent_settings` (`AGENT_SETTINGS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `BRANCH_SETTINGS_ID` FOREIGN KEY (`BRANCH_SETTINGS_ID`) REFERENCES `branch_settings` (`BRANCH_SETTINGS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `COMPANY_SETTINGS_ID` FOREIGN KEY (`COMPANY_SETTINGS_ID`) REFERENCES `company_settings` (`COMPANY_SETTINGS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `REGION_SETTINGS_ID        ` FOREIGN KEY (`REGION_SETTINGS_ID`) REFERENCES `region_settings` (`REGION_SETTINGS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `SURVEY_DETAILS_ID ` FOREIGN KEY (`SURVEY_DETAILS_ID`) REFERENCES `survey_details` (`SURVEY_DETAILS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contact_numbers`
--

DROP TABLE IF EXISTS `contact_numbers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contact_numbers` (
  `CONTACT_NUMBERS_ID` int(11) NOT NULL,
  `CONTACT_DETAILS _ID` int(11) DEFAULT NULL,
  `PERSONAL` varchar(45) DEFAULT NULL,
  `WORK` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`CONTACT_NUMBERS_ID`),
  KEY `CONTACT_DETAILS_ID_idx` (`CONTACT_DETAILS _ID`),
  CONSTRAINT `CONTACT_DETAILS_ID` FOREIGN KEY (`CONTACT_DETAILS _ID`) REFERENCES `contact_details` (`CONTACT_DETAILS _ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `crm_batch_tracker`
--

DROP TABLE IF EXISTS `crm_batch_tracker`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `crm_batch_tracker` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `COMPANY_ID` int(11) DEFAULT NULL,
  `REGION_ID` int(11) DEFAULT NULL,
  `BRANCH_ID` int(11) DEFAULT NULL,
  `AGENT_ID` int(11) DEFAULT NULL,
  `SOURCE` varchar(50) NOT NULL,
  `LAST_RUN_START_DATE` timestamp NOT NULL DEFAULT '1970-01-01 18:30:00',
  `LAST_RUN_END_DATE` timestamp NOT NULL DEFAULT '1970-01-01 18:30:00',
  `RECENT_RECORD_FETCHED_DATE` timestamp NOT NULL DEFAULT '1970-01-01 18:30:00',
  `LAST_RUN_RECORD_FETCHED_COUNT` int(11) DEFAULT '0',
  `CREATED_ON` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `MODIFIED_ON` timestamp NULL DEFAULT NULL,
  `ERROR` text,
  `DESCRIPTION` text,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=94 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `crm_batch_tracker_history`
--

DROP TABLE IF EXISTS `crm_batch_tracker_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `crm_batch_tracker_history` (
  `HISTORY_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `CRM_BATCH_TRACKER_ID` int(11) NOT NULL,
  `STATUS` int(1) NOT NULL,
  `COUNT_OF_RECORDS_FETCHED` int(11) DEFAULT '0',
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`HISTORY_ID`),
  KEY `fk_History_Crm_batch_tracker` (`CRM_BATCH_TRACKER_ID`),
  CONSTRAINT `fk_History_Crm_batch_tracker` FOREIGN KEY (`CRM_BATCH_TRACKER_ID`) REFERENCES `crm_batch_tracker` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1117 DEFAULT CHARSET=utf8 COMMENT='History details of no of records fetched each time when batch runs.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `crm_info`
--

DROP TABLE IF EXISTS `crm_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `crm_info` (
  `CRM_INFO_ID` int(11) NOT NULL,
  `AGENT_SETTINGS_ID` varchar(36) DEFAULT NULL,
  `REGION_SETTINGS_ID` varchar(36) DEFAULT NULL,
  `COMPANY_SETTINGS_ID` varchar(36) DEFAULT NULL,
  `AGENT_ID` int(10) DEFAULT NULL,
  `BRANCH_ID` int(10) DEFAULT NULL,
  `REGION_ID` int(10) DEFAULT NULL,
  `COMPANY_ID` int(10) DEFAULT NULL,
  `API` varchar(45) DEFAULT NULL,
  `CLIENT_CODE` varchar(45) DEFAULT NULL,
  `CONNECTION_SUCCESSFUL` tinyint(4) DEFAULT NULL,
  `CRM_FIELD_ID` varchar(45) DEFAULT NULL,
  `CRM_PASSWORD` varchar(45) DEFAULT NULL,
  `CRM_SOURCE` varchar(45) DEFAULT NULL,
  `CRM_USERNAME` varchar(45) DEFAULT NULL,
  `EMAIL_ADDRESS_FOR_REPORT` varchar(45) DEFAULT NULL,
  `GENERATE_REPORT` tinyint(4) DEFAULT NULL,
  `NUMBER_OF_DAYS` int(11) DEFAULT NULL,
  `RECORDS_BEEN_FETCHED` tinyint(4) DEFAULT NULL,
  `STATE` varchar(45) DEFAULT NULL,
  `TRANSACTION_START_DATE` datetime DEFAULT NULL,
  `URL` varchar(45) DEFAULT NULL,
  `VERSION` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`CRM_INFO_ID`),
  KEY `FX_AGENT_SETTINGS_ID_idx` (`AGENT_SETTINGS_ID`),
  KEY `FX_REGION_SETTINGS_ID_idx` (`REGION_SETTINGS_ID`),
  KEY `FX_COMPANY_SETTINGS_ID_idx` (`COMPANY_SETTINGS_ID`),
  CONSTRAINT `FX_AGENT_SETTINGS_ID` FOREIGN KEY (`AGENT_SETTINGS_ID`) REFERENCES `agent_settings` (`AGENT_SETTINGS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FX_COMPANY_SETTINGS_ID` FOREIGN KEY (`COMPANY_SETTINGS_ID`) REFERENCES `company_settings` (`COMPANY_SETTINGS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FX_REGION_SETTINGS_ID` FOREIGN KEY (`REGION_SETTINGS_ID`) REFERENCES `region_settings` (`REGION_SETTINGS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `crm_master`
--

DROP TABLE IF EXISTS `crm_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `crm_master` (
  `CRM_MASTER_ID` int(11) NOT NULL,
  `CRM_NAME` varchar(45) NOT NULL,
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`CRM_MASTER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `disabled_accounts`
--

DROP TABLE IF EXISTS `disabled_accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `disabled_accounts` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `LICENSE_ID` int(10) unsigned NOT NULL,
  `DISABLE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  `IS_FORCE_DELETE` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `fk_DISABLED_ACCOUNTS_COMPANY1_idx` (`COMPANY_ID`),
  KEY `fk_DISABLED_ACCOUNTS_LICENSE_DETAILS1_idx` (`LICENSE_ID`),
  CONSTRAINT `fk_DISABLED_ACCOUNTS_COMPANY1` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_DISABLED_ACCOUNTS_LICENSE_DETAILS1` FOREIGN KEY (`LICENSE_ID`) REFERENCES `license_details` (`LICENSE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=150 DEFAULT CHARSET=utf8 COMMENT='Holds the disabled account details for a company';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dotloop_profile_loop_mapping`
--

DROP TABLE IF EXISTS `dotloop_profile_loop_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dotloop_profile_loop_mapping` (
  `PROFILE_LOOP_MAPPING_ID` bigint(11) NOT NULL AUTO_INCREMENT,
  `PROFILE_ID` varchar(255) NOT NULL,
  `PROFILE_LOOP_ID` varchar(255) NOT NULL,
  `PROFILE_LOOP_VIEW_ID` varchar(255) NOT NULL,
  `LOOP_CLOSED_TIME` timestamp NULL DEFAULT NULL,
  `COMPANY_ID` bigint(11) DEFAULT NULL,
  `REGION_ID` bigint(11) DEFAULT NULL,
  `BRANCH_ID` bigint(11) DEFAULT NULL,
  `AGENT_ID` bigint(11) DEFAULT NULL,
  `COLLECTION_NAME` varchar(100) DEFAULT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`PROFILE_LOOP_MAPPING_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=102728 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `email_entity`
--

DROP TABLE IF EXISTS `email_entity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `email_entity` (
  `ID` bigint(11) NOT NULL AUTO_INCREMENT,
  `EMAIL_OBJECT` blob,
  `HOLD_SENDING_EMAIL` int(1) NOT NULL,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `CREATED_ON` timestamp NULL DEFAULT NULL,
  `MODIFIED_BY` varchar(255) DEFAULT NULL,
  `MODIFIED_ON` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2862 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `event`
--

DROP TABLE IF EXISTS `event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event` (
  `AGENT_ID` int(11) NOT NULL DEFAULT '0',
  `REGION_ID` int(11) NOT NULL DEFAULT '0',
  `BRANCH_ID` int(11) NOT NULL DEFAULT '0',
  `COMPANY_ID` int(11) NOT NULL,
  `EVENT_TYPE` varchar(45) NOT NULL,
  `ACTION` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `MODIFIED_BY` varchar(100) NOT NULL,
  PRIMARY KEY (`COMPANY_ID`,`MODIFIED_ON`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `external_api_call_details`
--

DROP TABLE IF EXISTS `external_api_call_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `external_api_call_details` (
  `EXTERNAL_API_CALL_DETAILS_ID` varchar(36) NOT NULL,
  `HTTP_METHOD` varchar(45) DEFAULT NULL,
  `REQUEST` longtext,
  `REQUEST_TIME` datetime DEFAULT NULL,
  `RESPONSE` longtext,
  `SOURCE` varchar(45) DEFAULT NULL,
  `REQUEST_BODY` longtext,
  PRIMARY KEY (`EXTERNAL_API_CALL_DETAILS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `external_survey_tracker`
--

DROP TABLE IF EXISTS `external_survey_tracker`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `external_survey_tracker` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `ENTITY_COLUMN_NAME` varchar(50) NOT NULL,
  `ENTITY_ID` int(11) NOT NULL,
  `REVIEW_SOURCE` varchar(50) NOT NULL,
  `REVIEW_SOURCE_LINK` varchar(500) NOT NULL,
  `REVIEW_RATING` double NOT NULL DEFAULT '0',
  `REVIEW_SOURCE_URL` varchar(500) NOT NULL,
  `AUTO_POST_STATUS` int(1) NOT NULL DEFAULT '0',
  `COMPLAINT_RES_STATUS` int(1) NOT NULL DEFAULT '0',
  `REVIEW_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `POSTED_ON` varchar(100) NOT NULL DEFAULT '0',
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=753 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `facebook_pages`
--

DROP TABLE IF EXISTS `facebook_pages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `facebook_pages` (
  `FACEBOOK_PAGES_ID` varchar(45) NOT NULL,
  `FACEBOOK_TOKEN_ID` varchar(45) DEFAULT NULL,
  `ACCESS_TOKEN` varchar(45) DEFAULT NULL,
  `CATEGORY` varchar(45) DEFAULT NULL,
  `NAME` varchar(45) DEFAULT NULL,
  `PROFILE_URL` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`FACEBOOK_PAGES_ID`),
  KEY `FK_FACEBOOK_TOKEN_ID_idx` (`FACEBOOK_TOKEN_ID`),
  CONSTRAINT `FK_FACEBOOK_TOKEN_ID` FOREIGN KEY (`FACEBOOK_TOKEN_ID`) REFERENCES `facebook_token` (`FACEBOOK_TOKEN_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `facebook_post_response_list`
--

DROP TABLE IF EXISTS `facebook_post_response_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `facebook_post_response_list` (
  `FACEBOOK_POST_RESPONSE_LIST_ID` int(10) NOT NULL,
  `ACCESS_TOKEN` varchar(45) DEFAULT NULL,
  `POST_DATE` datetime DEFAULT NULL,
  `RESPONSE_MESSAGE` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`FACEBOOK_POST_RESPONSE_LIST_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `facebook_token`
--

DROP TABLE IF EXISTS `facebook_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `facebook_token` (
  `FACEBOOK_TOKEN_ID` varchar(45) NOT NULL,
  `FACEBOOK_ACCESS_TOKEN` text,
  `FACEBOOK_ACCESS_TOKEN_CREATED_ON` datetime DEFAULT NULL,
  `FACEBOOK_ACCESS_TOKEN_EXPIRES_ON` int(10) DEFAULT NULL,
  `FACEBOOK_ACCESS_TOKEN_TO_POST` text,
  `FACEBOOK_PAGE_LINK` text,
  `TOKEN_EXPIRY_ALERT_EMAIL` varchar(45) DEFAULT NULL,
  `TOKEN_EXPIRY_ALERT_SENT` varchar(45) DEFAULT NULL,
  `TOKEN_EXPIRY_ALERT_TIME` datetime DEFAULT NULL,
  `type` int(11) NOT NULL,
  `id` int(10) NOT NULL,
  PRIMARY KEY (`type`,`id`),
  UNIQUE KEY `FACEBOOK_TOKEN_ID_UNIQUE` (`FACEBOOK_TOKEN_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `feed_status`
--

DROP TABLE IF EXISTS `feed_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `feed_status` (
  `FEED_STATUS_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `COMPANY_ID` int(10) unsigned DEFAULT NULL,
  `REGION_ID` int(10) unsigned DEFAULT NULL,
  `BRANCH_ID` int(10) unsigned DEFAULT NULL,
  `AGENT_ID` int(10) unsigned DEFAULT NULL,
  `FEED_SOURCE` varchar(250) NOT NULL COMMENT 'Source of feed',
  `LAST_FETCHED_POST_ID` varchar(500) NOT NULL,
  `LAST_FETCHED_TILL` timestamp NULL DEFAULT NULL,
  `RETRIES` int(11) DEFAULT NULL,
  `REMINDERS_SENT` int(11) DEFAULT '0',
  `REMINDER_SENT_ON` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`FEED_STATUS_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=274 DEFAULT CHARSET=utf8 COMMENT='Holds the Social Feed Status meta data';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `file_upload`
--

DROP TABLE IF EXISTS `file_upload`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `file_upload` (
  `FILE_UPLOAD_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `ADMIN_USER_ID` int(11) NOT NULL,
  `FILE_NAME` varchar(250) NOT NULL,
  `UPLOAD_TYPE` int(1) NOT NULL,
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `START_DATE` timestamp NULL DEFAULT NULL,
  `END_DATE` timestamp NULL DEFAULT NULL,
  `PROFILE_LEVEL` varchar(50) DEFAULT NULL,
  `PROFILE_VALUE` int(10) DEFAULT '0',
  PRIMARY KEY (`FILE_UPLOAD_ID`),
  KEY `fk_FILE_UPLOAD_COMPANY1` (`COMPANY_ID`),
  CONSTRAINT `fk_FILE_UPLOAD_COMPANY1` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=378 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `forward_mail_details`
--

DROP TABLE IF EXISTS `forward_mail_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `forward_mail_details` (
  `FORWARD_MAIL_DETAILS_ID` varchar(36) NOT NULL,
  `CREATED_BY` varchar(45) DEFAULT NULL,
  `CREATED_ON` datetime DEFAULT NULL,
  `MESSAGE_ID` longtext,
  `MODIFIED_BY` varchar(45) DEFAULT NULL,
  `MODIFIED_ON` datetime DEFAULT NULL,
  `RECIPIENT_MAIL_ID` longtext,
  `SENDER_MAIL_ID` varchar(45) DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  PRIMARY KEY (`FORWARD_MAIL_DETAILS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `google_business_token`
--

DROP TABLE IF EXISTS `google_business_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `google_business_token` (
  `GOOGLE_BUSINESS_TOKEN_ID` varchar(45) NOT NULL,
  `GOOGLE_BUSINESS_LINK` text,
  `type` int(11) NOT NULL,
  `id` int(10) NOT NULL,
  PRIMARY KEY (`type`,`id`),
  UNIQUE KEY `GOOGLE_BUSINESS_TOKEN_ID_UNIQUE` (`GOOGLE_BUSINESS_TOKEN_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `google_token`
--

DROP TABLE IF EXISTS `google_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `google_token` (
  `GOOGLE_TOKEN_ID` varchar(45) NOT NULL,
  `GOOGLE_ACCESS_TOKEN` text,
  `GOOGLE_ACCESS_TOKEN_CREATED_ON` datetime DEFAULT NULL,
  `GOOGLE_ACCESS_TOKEN_EXPIRES_IN` int(10) DEFAULT NULL,
  `GOOGLE_REFRESH_TOKEN` text,
  `PROFILE_LINK` text,
  `type` int(11) NOT NULL,
  `id` int(10) NOT NULL,
  PRIMARY KEY (`type`,`id`),
  UNIQUE KEY `GOOGLE_TOKEN_ID_UNIQUE` (`GOOGLE_TOKEN_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hierarchy_upload`
--

DROP TABLE IF EXISTS `hierarchy_upload`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hierarchy_upload` (
  `HIERARCHY_UPLOAD_ID` varchar(36) NOT NULL,
  `COMPANY_ID` int(10) unsigned DEFAULT NULL,
  `IS_MODIFIED_FROM_UI` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`HIERARCHY_UPLOAD_ID`),
  KEY `FX_COMPANY_ID_idx` (`COMPANY_ID`),
  KEY `GX_COMPANY_ID_idx` (`COMPANY_ID`),
  CONSTRAINT `GX_COMPANY_ID` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hierarchy_upload_branch_source_mapping`
--

DROP TABLE IF EXISTS `hierarchy_upload_branch_source_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hierarchy_upload_branch_source_mapping` (
  `HIERARCHY_UPLOAD_BRANCH_SOURCE_MAPPING_ID` int(10) NOT NULL,
  `HIERARCHY_UPLOAD_ID` varchar(36) DEFAULT NULL,
  `GENERATED_UNIQUE_KEYS` int(10) DEFAULT NULL,
  PRIMARY KEY (`HIERARCHY_UPLOAD_BRANCH_SOURCE_MAPPING_ID`),
  KEY `HIERARCHY_UPLOAD_ID_idx` (`HIERARCHY_UPLOAD_ID`),
  CONSTRAINT `FX_HIERARCHY_UPLOAD_ID` FOREIGN KEY (`HIERARCHY_UPLOAD_ID`) REFERENCES `hierarchy_upload` (`HIERARCHY_UPLOAD_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hierarchy_upload_branches`
--

DROP TABLE IF EXISTS `hierarchy_upload_branches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hierarchy_upload_branches` (
  `HIERARCHY_UPLOAD_BRANCHES_ID` int(10) NOT NULL,
  `HIERARCHY_UPLOAD_ID` varchar(36) DEFAULT NULL,
  `BRANCH_ID` int(10) unsigned DEFAULT NULL,
  `REGION_ID` int(10) unsigned DEFAULT NULL,
  `ASSIGNED_REGION_NAME` varchar(45) DEFAULT NULL,
  `ASSIGN_TO_COMPANY` tinyint(4) DEFAULT NULL,
  `BRANCH_ADDRESS1` varchar(45) DEFAULT NULL,
  `BRANCH_ADDRESS2` varchar(45) DEFAULT NULL,
  `BRANCH_CITY` varchar(45) DEFAULT NULL,
  `BRANCH_COUNTRY` varchar(45) DEFAULT NULL,
  `BRANCH_COUNTRY_CODE` varchar(45) DEFAULT NULL,
  `BRANCH_NAME` varchar(45) DEFAULT NULL,
  `BRANCH_STATE` varchar(45) DEFAULT NULL,
  `BRANCH_ZIPCODE` varchar(45) DEFAULT NULL,
  `IS_ADDRESS_SET` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_ADDED` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_ADDRESS1_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_ADDRESS2_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_CITY_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_COUNTRY_CODE_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_COUNTRY_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_ID_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_NAME_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_STATE_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_ZIPCODE_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_DELETED_RECORD` tinyint(4) DEFAULT NULL,
  `IS_ERROR_RECORD` tinyint(4) DEFAULT NULL,
  `IS_IN_APPEND_MODE` tinyint(4) DEFAULT NULL,
  `IS_REGION_ID_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_SOURCE_BRANCH_ID_GENERATED` tinyint(4) DEFAULT NULL,
  `IS_SOURCE_BRANCH_ID_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_SOURCE_REGION_ID_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_WARNING_RECORD` tinyint(4) DEFAULT NULL,
  `ROW_NUM` varchar(45) DEFAULT NULL,
  `SOURCE_BRANCH_ID` varchar(45) DEFAULT NULL,
  `SOURCE_REGION_ID` varchar(45) DEFAULT NULL,
  `VALIDATION_ERRORS` text,
  `VALIDATION_WARNINGS` text,
  PRIMARY KEY (`HIERARCHY_UPLOAD_BRANCHES_ID`),
  KEY `HIERARCHY_UPLOAD_ID_idx` (`HIERARCHY_UPLOAD_ID`),
  KEY `BRANCH_ID_idx` (`BRANCH_ID`),
  KEY `REGION_ID_idx` (`REGION_ID`),
  CONSTRAINT `BRANCH_ID` FOREIGN KEY (`BRANCH_ID`) REFERENCES `branch` (`BRANCH_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `HIERARCHY_UPLOAD_ID` FOREIGN KEY (`HIERARCHY_UPLOAD_ID`) REFERENCES `hierarchy_upload` (`HIERARCHY_UPLOAD_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `REGION_ID` FOREIGN KEY (`REGION_ID`) REFERENCES `region` (`REGION_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hierarchy_upload_region_source_mapping`
--

DROP TABLE IF EXISTS `hierarchy_upload_region_source_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hierarchy_upload_region_source_mapping` (
  `HIERARCHY_UPLOAD_REGION_SOURCE_MAPPING_ID` int(10) NOT NULL,
  `HIERARCHY_UPLOAD_ID` varchar(36) DEFAULT NULL,
  `GENERATED_UNIQUE_KEYS` int(10) DEFAULT NULL,
  PRIMARY KEY (`HIERARCHY_UPLOAD_REGION_SOURCE_MAPPING_ID`),
  KEY `FX3_HIERARCHY_UPLOAD_ID                                    _idx` (`HIERARCHY_UPLOAD_ID`),
  CONSTRAINT `FX3_HIERARCHY_UPLOAD_ID                                    ` FOREIGN KEY (`HIERARCHY_UPLOAD_ID`) REFERENCES `hierarchy_upload` (`HIERARCHY_UPLOAD_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hierarchy_upload_regions`
--

DROP TABLE IF EXISTS `hierarchy_upload_regions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hierarchy_upload_regions` (
  `HIERARCHY_UPLOAD_REGIONS_ID` int(10) NOT NULL,
  `HIERARCHY_UPLOAD_ID` varchar(36) DEFAULT NULL,
  `REGION_ID` int(10) unsigned DEFAULT NULL,
  `IS_ADDRESS_SET` tinyint(4) DEFAULT NULL,
  `IS_DELETED_RECORD` tinyint(4) DEFAULT NULL,
  `IS_ERROR_RECORD` tinyint(4) DEFAULT NULL,
  `IS_IN_APPEND_MODE` tinyint(4) DEFAULT NULL,
  `IS_REGION_ADDED` tinyint(4) DEFAULT NULL,
  `IS_REGION_ADDRESS1_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_REGION_ADDRESS2_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_REGION_CITY_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_REGION_COUNTRY_CODE_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_REGION_COUNTRY_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_REGION_ID_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_REGION_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_REGION_NAME_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_REGION_STATE_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_REGION_ZIPCODE_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_SOURCE_REGION_ID_GENERATED` tinyint(4) DEFAULT NULL,
  `IS_SOURCE_REGION_ID_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_WARNING_RECORD` tinyint(4) DEFAULT NULL,
  `REGION_ADDRESS1` varchar(45) DEFAULT NULL,
  `REGION_ADDRESS2` varchar(45) DEFAULT NULL,
  `REGION_CITY` varchar(45) DEFAULT NULL,
  `REGION_COUNTRY` varchar(45) DEFAULT NULL,
  `REGION_COUNTRY_CODE` varchar(45) DEFAULT NULL,
  `REGION_NAME` varchar(45) DEFAULT NULL,
  `REGION_STATE` varchar(45) DEFAULT NULL,
  `REGION_ZIPCODE` varchar(45) DEFAULT NULL,
  `ROW_NUM` int(11) DEFAULT NULL,
  `SOURCE_REGION_ID` varchar(45) DEFAULT NULL,
  `VALIDATION_ERRORS` text,
  `VALIDATION_WARNINGS` text,
  PRIMARY KEY (`HIERARCHY_UPLOAD_REGIONS_ID`),
  KEY `FX1_HIERARCHY_UPLOAD_ID                                    _idx` (`HIERARCHY_UPLOAD_ID`),
  KEY `FX1_REGION_ID_idx` (`REGION_ID`),
  CONSTRAINT `FX1_HIERARCHY_UPLOAD_ID                                     ` FOREIGN KEY (`HIERARCHY_UPLOAD_ID`) REFERENCES `hierarchy_upload` (`HIERARCHY_UPLOAD_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FX1_REGION_ID` FOREIGN KEY (`REGION_ID`) REFERENCES `region` (`REGION_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hierarchy_upload_users`
--

DROP TABLE IF EXISTS `hierarchy_upload_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hierarchy_upload_users` (
  `HIERARCHY_UPLOAD_USERS_ID` int(10) NOT NULL,
  `HIERARCHY_UPLOAD_ID` varchar(36) DEFAULT NULL,
  `REGION_ID` int(10) unsigned DEFAULT NULL,
  `USER_ID` int(10) unsigned DEFAULT NULL,
  `BRANCH_ID` int(10) unsigned DEFAULT NULL,
  `ABOUT_ME_DESCRIPTION` varchar(45) DEFAULT NULL,
  `ASSIGNED_BRANCHES` text,
  `ASSIGNED_BRANCHES_ADMIN` text,
  `ASSIGNED_REGIONS` text,
  `ASSIGNED_REGIONS_ADMIN` text,
  `HIERARCHY_UPLOAD_USERScol` text,
  `ASSIGN_TO_COMPANY` tinyint(4) DEFAULT NULL,
  `BELONGS_TO_COMPANY` tinyint(4) DEFAULT NULL,
  `EMAIL_ID` varchar(45) DEFAULT NULL,
  `FIRST_NAME` varchar(45) DEFAULT NULL,
  `IS_ABOUT_ME_DESCRIPTION_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_AGENT` tinyint(4) DEFAULT NULL,
  `IS_AGENT_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_ASSIGNED_BRACHES_ADMIN_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_ASSIGNED_BRANCHES_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_ASSIGNED_BRANCH_NAME_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_ASSIGNED_REGION_NAME_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_ASSIGNED_REGIONS_ADMIN_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_ASSIGNED_REGIONS_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_ASSIGN_TO_COMPANY_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_BELONGS_TO_COMPANY_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_ADMIN` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_ADMIN_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_ID_MODIFIED             IS_BRANCH_ID_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_DELETED_RECORD` tinyint(4) DEFAULT NULL,
  `IS_EMAIL_ID_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_ERROR_RECORD` tinyint(4) DEFAULT NULL,
  `IS_FIRST_NAME_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_IN_APPEND_MODE` tinyint(4) DEFAULT NULL,
  `IS_LAST_NAME_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_LEGAL_DISCLAIMER_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_LICENSE_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_PHONE_NUMBER_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_REGION_ADMIN` tinyint(4) DEFAULT NULL,
  `IS_REGION_ADMIN_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_REGION_ID_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_SOURCE_BRANCH_ID_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_SOURCE_REGION_ID_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_SOURCE_USER_ID_GENERATED` tinyint(4) DEFAULT NULL,
  `IS_TITLE_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_USER_ADDED` tinyint(4) DEFAULT NULL,
  `IS_USER_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_USER_PHOTO_URL_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_USER_VERIFIED` tinyint(4) DEFAULT NULL,
  `IS_WARNING_RECORD` tinyint(4) DEFAULT NULL,
  `IS_WEBSITE_URL_MODIFIED` tinyint(4) DEFAULT NULL,
  `LAST_NAME` varchar(45) DEFAULT NULL,
  `LEGAL_DISCLAIMER` varchar(45) DEFAULT NULL,
  `PHONE_NUMBER` varchar(45) DEFAULT NULL,
  `ROW_NUM` int(11) DEFAULT NULL,
  `SEND_MAIL` tinyint(4) DEFAULT NULL,
  `SOURCE_USER_ID` varchar(45) DEFAULT NULL,
  `TITLE` varchar(45) DEFAULT NULL,
  `VALIDATION_ERRORS` text,
  `VALIDATION_WARNINGS` text,
  `WEBSITE_URL` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`HIERARCHY_UPLOAD_USERS_ID`),
  KEY `FX4_HIERARCHY_UPLOAD_ID_idx` (`HIERARCHY_UPLOAD_ID`),
  KEY `FX3_REGION_ID_idx` (`REGION_ID`),
  KEY `FX3_USER_ID_idx` (`USER_ID`),
  KEY `FX3_BRANCH_ID_idx` (`BRANCH_ID`),
  CONSTRAINT `FX3_BRANCH_ID` FOREIGN KEY (`BRANCH_ID`) REFERENCES `branch` (`BRANCH_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FX3_REGION_ID` FOREIGN KEY (`REGION_ID`) REFERENCES `region` (`REGION_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FX3_USER_ID` FOREIGN KEY (`USER_ID`) REFERENCES `users` (`USER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FX4_HIERARCHY_UPLOAD_ID` FOREIGN KEY (`HIERARCHY_UPLOAD_ID`) REFERENCES `hierarchy_upload` (`HIERARCHY_UPLOAD_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hierarchy_upload_users_source_mapping`
--

DROP TABLE IF EXISTS `hierarchy_upload_users_source_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hierarchy_upload_users_source_mapping` (
  `HIERARCHY_UPLOAD_USERS_SOURCE_MAPPING_ID` int(10) NOT NULL,
  `HIERARCHY_UPLOAD_ID` varchar(36) DEFAULT NULL,
  `GENERATED_UNIQUE_KEYS` int(10) DEFAULT NULL,
  PRIMARY KEY (`HIERARCHY_UPLOAD_USERS_SOURCE_MAPPING_ID`),
  KEY `FX5_HIERARCHY_UPLOAD_ID_idx` (`HIERARCHY_UPLOAD_ID`),
  CONSTRAINT `FX5_HIERARCHY_UPLOAD_ID` FOREIGN KEY (`HIERARCHY_UPLOAD_ID`) REFERENCES `hierarchy_upload` (`HIERARCHY_UPLOAD_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lender_ref`
--

DROP TABLE IF EXISTS `lender_ref`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lender_ref` (
  `LENDER_REF_ID` int(10) NOT NULL,
  `ZILLOW_TOKEN_ID` varchar(45) DEFAULT NULL,
  `NMLS_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`LENDER_REF_ID`),
  KEY `FK_ZILLOW_TOKEN_ID_idx` (`ZILLOW_TOKEN_ID`),
  CONSTRAINT `FK_ZILLOW_TOKEN_ID` FOREIGN KEY (`ZILLOW_TOKEN_ID`) REFERENCES `zillow_token` (`ZILLOW_TOKEN_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lending_tree_token`
--

DROP TABLE IF EXISTS `lending_tree_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lending_tree_token` (
  `LENDING_TREE_TOKEN_ID` varchar(45) NOT NULL,
  `LENDING_TREE_PROFILE_LINK` text,
  `type` int(11) NOT NULL,
  `id` int(10) NOT NULL,
  PRIMARY KEY (`type`,`id`),
  UNIQUE KEY `LENDING_TREE_TOKEN_ID_UNIQUE` (`LENDING_TREE_TOKEN_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `license_details`
--

DROP TABLE IF EXISTS `license_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `license_details` (
  `LICENSE_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `SUBSCRIPTION_ID` varchar(20) DEFAULT NULL,
  `ACCOUNTS_MASTER_ID` int(10) unsigned NOT NULL,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `LICENSE_START_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `LICENSE_END_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `PAYMENT_MODE` char(1) NOT NULL COMMENT '''M'' for manual, ''A'' for auto',
  `NEXT_RETRY_TIME` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `PAYMENT_RETRIES` int(2) NOT NULL,
  `IS_SUBSCRIPTION_DUE` int(1) DEFAULT '0',
  `STATUS` int(1) NOT NULL,
  `SUBSCRIPTION_ID_SOURCE` varchar(20) DEFAULT NULL,
  `INVOICE_CYCLE_DATE` int(4) DEFAULT '0',
  `INVOICE_CYCLE_PERIOD_MONTH` int(4) DEFAULT '0',
  `NEXT_INVOICE_BILLING_DATE` timestamp NULL DEFAULT NULL,
  `RECIPIENT_MAIL_ID` varchar(255) DEFAULT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`LICENSE_ID`),
  KEY `fk_LICENCE_DETAILS_ACCOUNTS_MASTER1_idx` (`ACCOUNTS_MASTER_ID`),
  KEY `fk_LICENCE_DETAILS_COMPANY1_idx` (`COMPANY_ID`),
  CONSTRAINT `fk_LICENCE_DETAILS_ACCOUNTS_MASTER1` FOREIGN KEY (`ACCOUNTS_MASTER_ID`) REFERENCES `accounts_master` (`ACCOUNTS_MASTER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_LICENCE_DETAILS_COMPANY1` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=646 DEFAULT CHARSET=utf8 COMMENT='Holds the license details for a company';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `linked_in_country`
--

DROP TABLE IF EXISTS `linked_in_country`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `linked_in_country` (
  `LINKED_IN_COUNTRY_ID` int(11) NOT NULL,
  `LINKED_IN_LOCATION_ID` int(11) DEFAULT NULL,
  ` CODE` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`LINKED_IN_COUNTRY_ID`),
  KEY `LINKED_IN_LOCATION_ID_idx` (`LINKED_IN_LOCATION_ID`),
  CONSTRAINT `LINKED_IN_LOCATION_ID` FOREIGN KEY (`LINKED_IN_LOCATION_ID`) REFERENCES `linked_in_location` (`LINKED_IN_LOCATION_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `linked_in_location`
--

DROP TABLE IF EXISTS `linked_in_location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `linked_in_location` (
  `LINKED_IN_LOCATION_ID` int(11) NOT NULL,
  `LINKED_IN_PROFILE_DATA_ID` varchar(45) DEFAULT NULL,
  `NAME` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`LINKED_IN_LOCATION_ID`),
  KEY `LINKED_IN_PROFILE_DATA_ID_idx` (`LINKED_IN_PROFILE_DATA_ID`),
  CONSTRAINT `LINKED_IN_PROFILE_DATA_ID` FOREIGN KEY (`LINKED_IN_PROFILE_DATA_ID`) REFERENCES `linked_in_profile_data` (`LINKED_IN_PROFILE_DATA_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `linked_in_picture_urls`
--

DROP TABLE IF EXISTS `linked_in_picture_urls`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `linked_in_picture_urls` (
  `LINKED_IN_PICTURE_URLS_ID` int(11) NOT NULL,
  `LINKED_IN_PROFILE_DATA_ID` varchar(45) DEFAULT NULL,
  `TOTAL` int(11) DEFAULT NULL,
  `VALUES` text,
  PRIMARY KEY (`LINKED_IN_PICTURE_URLS_ID`),
  KEY `FX_LINKED_IN_PROFILE_DATA_ID_idx` (`LINKED_IN_PROFILE_DATA_ID`),
  CONSTRAINT `FX_LINKED_IN_PROFILE_DATA_ID` FOREIGN KEY (`LINKED_IN_PROFILE_DATA_ID`) REFERENCES `linked_in_profile_data` (`LINKED_IN_PROFILE_DATA_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `linked_in_positions`
--

DROP TABLE IF EXISTS `linked_in_positions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `linked_in_positions` (
  `LINKED_IN_POSITIONS_ID` int(11) NOT NULL,
  `LINKED_IN_PROFILE_DATA_ID` varchar(45) DEFAULT NULL,
  `TOTAL` int(11) DEFAULT NULL,
  PRIMARY KEY (`LINKED_IN_POSITIONS_ID`),
  KEY `FX1_LINKED_IN_PROFILE_DATA_ID_idx` (`LINKED_IN_PROFILE_DATA_ID`),
  CONSTRAINT `FX1_LINKED_IN_PROFILE_DATA_ID` FOREIGN KEY (`LINKED_IN_PROFILE_DATA_ID`) REFERENCES `linked_in_profile_data` (`LINKED_IN_PROFILE_DATA_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `linked_in_profile_data`
--

DROP TABLE IF EXISTS `linked_in_profile_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `linked_in_profile_data` (
  `LINKED_IN_PROFILE_DATA_ID` varchar(45) NOT NULL,
  `AGENT_SETTINGS_ID` varchar(36) DEFAULT NULL,
  `FIRST_NAME` varchar(45) DEFAULT NULL,
  `HEADLINE` varchar(45) DEFAULT NULL,
  `INDUSTRY` varchar(45) DEFAULT NULL,
  `LAST_NAME` varchar(45) DEFAULT NULL,
  `PUBLIC_PROFILE_URL` varchar(45) DEFAULT NULL,
  `SPECIALTIES` varchar(45) DEFAULT NULL,
  `SUMMARY` varchar(45) DEFAULT NULL,
  `PICTURE_URL` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`LINKED_IN_PROFILE_DATA_ID`),
  KEY `FX6_AGENT_SETTINGS_ID_idx` (`AGENT_SETTINGS_ID`),
  CONSTRAINT `FX6_AGENT_SETTINGS_ID` FOREIGN KEY (`AGENT_SETTINGS_ID`) REFERENCES `agent_settings` (`AGENT_SETTINGS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `linked_in_profile_data_company`
--

DROP TABLE IF EXISTS `linked_in_profile_data_company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `linked_in_profile_data_company` (
  `LINKED_IN_PROFILE_DATA_COMPANY_ID` int(11) NOT NULL,
  `LINKED_IN_PROFILE_DATA_VALUES_ID` int(10) DEFAULT NULL,
  `LINKED_IN_PROFILE_DATA_NAME` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`LINKED_IN_PROFILE_DATA_COMPANY_ID`),
  KEY `LINKED_IN_PROFILE_DATA_VALUES_ID_idx` (`LINKED_IN_PROFILE_DATA_VALUES_ID`),
  CONSTRAINT `LINKED_IN_PROFILE_DATA_VALUES_ID` FOREIGN KEY (`LINKED_IN_PROFILE_DATA_VALUES_ID`) REFERENCES `linked_in_profile_data_values` (`LINKED_IN_PROFILE_DATA_VALUES_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `linked_in_profile_data_values`
--

DROP TABLE IF EXISTS `linked_in_profile_data_values`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `linked_in_profile_data_values` (
  `LINKED_IN_PROFILE_DATA_VALUES_ID` int(11) NOT NULL,
  `LINKED_IN_PROFILE_DATA_ID` varchar(45) DEFAULT NULL,
  `IS_CURRENT` tinyint(4) DEFAULT NULL,
  `SUMMARY` varchar(45) DEFAULT NULL,
  `TITLE` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`LINKED_IN_PROFILE_DATA_VALUES_ID`),
  KEY `FX2_LINKED_IN_PROFILE_DATA_ID_idx` (`LINKED_IN_PROFILE_DATA_ID`),
  CONSTRAINT `FX2_LINKED_IN_PROFILE_DATA_ID` FOREIGN KEY (`LINKED_IN_PROFILE_DATA_ID`) REFERENCES `linked_in_profile_data` (`LINKED_IN_PROFILE_DATA_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `linked_in_start_date`
--

DROP TABLE IF EXISTS `linked_in_start_date`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `linked_in_start_date` (
  `LINKED_IN_START_DATE_ID` int(11) NOT NULL,
  `LINKED_IN_PROFILE_DATA_VALUES_ID` int(10) DEFAULT NULL,
  `MONTH` varchar(45) DEFAULT NULL,
  `YEAR` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`LINKED_IN_START_DATE_ID`),
  KEY `FX_LINKED_IN_PROFILE_DATA_VALUES_ID_idx` (`LINKED_IN_PROFILE_DATA_VALUES_ID`),
  CONSTRAINT `FX_LINKED_IN_PROFILE_DATA_VALUES_ID` FOREIGN KEY (`LINKED_IN_PROFILE_DATA_VALUES_ID`) REFERENCES `linked_in_profile_data_values` (`LINKED_IN_PROFILE_DATA_VALUES_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `linked_in_token`
--

DROP TABLE IF EXISTS `linked_in_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `linked_in_token` (
  `LINKED_IN_TOKEN_ID` varchar(45) NOT NULL,
  `LINKED_IN_ACCESS_TOKEN` text,
  `LINKED_IN_ACCESS_TOKEN_CREATED_ON` datetime DEFAULT NULL,
  `LINKED_IN_ACCESS_TOKEN_EXPIRES_IN` int(10) DEFAULT NULL,
  `LINKED_IN_PAGE_LINK` text,
  `TOKEN_EXPIRY_ALERT_EMAIL` varchar(45) DEFAULT NULL,
  `TOKEN_EXPIRY_ALERT_SENT` varchar(6) DEFAULT NULL,
  `TOKEN_EXPIRY_ALERT_TIME` datetime DEFAULT NULL,
  `type` int(11) NOT NULL,
  `id` int(10) NOT NULL,
  PRIMARY KEY (`type`,`id`),
  UNIQUE KEY `LINKED_IN_TOKEN_ID_UNIQUE` (`LINKED_IN_TOKEN_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `linkedin_post_response_list`
--

DROP TABLE IF EXISTS `linkedin_post_response_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `linkedin_post_response_list` (
  `LINKEDIN_POST_RESPONSE_LIST_ID` int(10) NOT NULL,
  `ACCESS_TOKEN` varchar(45) DEFAULT NULL,
  `POST_DATE` datetime DEFAULT NULL,
  `REFERENCE_URL` varchar(45) DEFAULT NULL,
  `RESPONSE_MESSAGE` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`LINKEDIN_POST_RESPONSE_LIST_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lock_settings`
--

DROP TABLE IF EXISTS `lock_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lock_settings` (
  `LOCK_SETTINGS_ID` int(11) NOT NULL,
  `BRANCH_SETTINGS_ID` varchar(36) DEFAULT NULL,
  `REGION_SETTINGS_ID` varchar(36) DEFAULT NULL,
  `COMPANY_SETTINGS_ID` varchar(36) DEFAULT NULL,
  `SURVEY_DETAILS_ID` varchar(36) DEFAULT NULL,
  `IS_ABOUT_ME_LOCKED` tinyint(4) DEFAULT NULL,
  `IS_ADDRESS_LOCKED` tinyint(4) DEFAULT NULL,
  `IS_BLOG_ADDRESS_LOCKED` tinyint(4) DEFAULT NULL,
  `IS_DISPLAY_NAME_LOCKED` tinyint(4) DEFAULT NULL,
  `IS_FAX_PHONE_LOCKED` tinyint(4) DEFAULT NULL,
  `IS_LOGO_LOCKED` tinyint(4) DEFAULT NULL,
  `IS_MIN_SCORE_LOCKED` tinyint(4) DEFAULT NULL,
  `IS_PERSONAL_PHONE_LOCKED` tinyint(4) DEFAULT NULL,
  `IS_WEB_ADDRESS_LOCKED` tinyint(4) DEFAULT NULL,
  `IS_WORK_EMAIL_LOCKED` tinyint(4) DEFAULT NULL,
  `IS_WORK_PHONE_LOCKED` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`LOCK_SETTINGS_ID`),
  KEY `FX3_BRANCH_SETTINGS_ID_idx` (`BRANCH_SETTINGS_ID`),
  KEY `FX4_REGION_SETTINGS_ID_idx` (`REGION_SETTINGS_ID`),
  KEY `FX4_COMPANY_SETTINGS_ID_idx` (`COMPANY_SETTINGS_ID`),
  KEY `FX3_SURVEY_DETAILS_ID_idx` (`SURVEY_DETAILS_ID`),
  CONSTRAINT `FX3_BRANCH_SETTINGS_ID` FOREIGN KEY (`BRANCH_SETTINGS_ID`) REFERENCES `branch_settings` (`BRANCH_SETTINGS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FX3_SURVEY_DETAILS_ID` FOREIGN KEY (`SURVEY_DETAILS_ID`) REFERENCES `survey_details` (`SURVEY_DETAILS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FX4_COMPANY_SETTINGS_ID` FOREIGN KEY (`COMPANY_SETTINGS_ID`) REFERENCES `company_settings` (`COMPANY_SETTINGS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FX4_REGION_SETTINGS_ID` FOREIGN KEY (`REGION_SETTINGS_ID`) REFERENCES `region_settings` (`REGION_SETTINGS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mail`
--

DROP TABLE IF EXISTS `mail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mail` (
  `MAIL_IDS` int(11) NOT NULL,
  `CONTACT_DETAILS _ID` int(11) DEFAULT NULL,
  `IS_PERSONAL_EMAIL_VERIFIED` tinyint(4) DEFAULT NULL,
  `IS_WORK_EMAIL_VERIFIED` tinyint(4) DEFAULT NULL,
  `IS_WORK_MAIL_VERIFIED_BY_ADMIN` tinyint(4) DEFAULT NULL,
  `WORK` varchar(45) DEFAULT NULL,
  `WORK_EMAIL_TO_VERIFY` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`MAIL_IDS`),
  KEY `CONTACT_DETAILS _ID_idx` (`CONTACT_DETAILS _ID`),
  CONSTRAINT `FX_CONTACT_DETAILS _ID` FOREIGN KEY (`MAIL_IDS`) REFERENCES `contact_details` (`CONTACT_DETAILS _ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `organization_level_settings`
--

DROP TABLE IF EXISTS `organization_level_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organization_level_settings` (
  `ORGANIZATION_LEVEL_SETTINGS_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `REGION_ID` int(11) NOT NULL,
  `BRANCH_ID` int(11) NOT NULL,
  `AGENT_ID` int(11) NOT NULL,
  `SETTING_KEY` varchar(45) NOT NULL,
  `SETTING_VALUE` varchar(500) NOT NULL,
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL COMMENT 'More details need to be added',
  PRIMARY KEY (`ORGANIZATION_LEVEL_SETTINGS_ID`),
  KEY `fk_ORGANIZATION_PROFILE_COMPANY1_idx` (`COMPANY_ID`),
  CONSTRAINT `fk_ORGANIZATION_PROFILE_COMPANY1` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Holds the settings for organisation level. The level could be company, region or branch';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `phone`
--

DROP TABLE IF EXISTS `phone`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `phone` (
  `PHONE_ID` int(11) NOT NULL,
  `CONTACT_NUMBERS_ID` int(11) DEFAULT NULL,
  `COUNTRY_ABBR` varchar(45) DEFAULT NULL,
  `COUNTRY_CODE` varchar(45) DEFAULT NULL,
  `EXTENSION` varchar(45) DEFAULT NULL,
  `FORMATTED_PHONE_NUMBER` varchar(45) DEFAULT NULL,
  `NUMBER` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`PHONE_ID`),
  KEY `CONTACT_NUMBERS_ID_idx` (`CONTACT_NUMBERS_ID`),
  CONSTRAINT `CONTACT_NUMBERS_ID` FOREIGN KEY (`CONTACT_NUMBERS_ID`) REFERENCES `contact_numbers` (`CONTACT_NUMBERS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `positions`
--

DROP TABLE IF EXISTS `positions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `positions` (
  `POSITIONS_ID` int(11) NOT NULL,
  `AGENT_SETTINGS_ID` varchar(36) DEFAULT NULL,
  `END_MONTH` int(11) DEFAULT NULL,
  `END_TIME` varchar(45) DEFAULT NULL,
  `END_YEAR` int(11) DEFAULT NULL,
  `IS_CURRENT` tinyint(4) DEFAULT NULL,
  `NAME` varchar(45) DEFAULT NULL,
  `START_MONTH` int(11) DEFAULT NULL,
  `START_TIME` varchar(45) DEFAULT NULL,
  `START_YEAR` int(11) DEFAULT NULL,
  `TITLE` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`POSITIONS_ID`),
  KEY `FX1_AGENT_SETTINGS_ID_idx` (`AGENT_SETTINGS_ID`),
  CONSTRAINT `FX1_AGENT_SETTINGS_ID` FOREIGN KEY (`AGENT_SETTINGS_ID`) REFERENCES `agent_settings` (`AGENT_SETTINGS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `profile_stages`
--

DROP TABLE IF EXISTS `profile_stages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `profile_stages` (
  `PROFILE_STAGES_ID` int(11) NOT NULL,
  `AGENT_SETTINGS_ID` varchar(36) DEFAULT NULL,
  `BRANCH_SETTINGS_ID` varchar(36) DEFAULT NULL,
  `REGION_SETTINGS_ID` varchar(36) DEFAULT NULL,
  `COMPANY_SETTINGS_ID` varchar(36) DEFAULT NULL,
  `SURVEY_DETAILS_ID` varchar(36) DEFAULT NULL,
  `ORDER` int(11) DEFAULT NULL,
  `PROFILE_STAGE_KEY` varchar(45) DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  PRIMARY KEY (`PROFILE_STAGES_ID`),
  KEY `FX2_AGENT_SETTINGS_ID_idx` (`AGENT_SETTINGS_ID`),
  KEY `FX_BRANCH_SETTINGS_ID_idx` (`BRANCH_SETTINGS_ID`),
  KEY `FX1_REGION_SETTINGS_ID_idx` (`REGION_SETTINGS_ID`),
  KEY `FX1_COMPANY_SETTINGS_ID_idx` (`COMPANY_SETTINGS_ID`),
  KEY `FX_SURVEY_DETAILS_ID_idx` (`SURVEY_DETAILS_ID`),
  CONSTRAINT `FX1_COMPANY_SETTINGS_ID` FOREIGN KEY (`COMPANY_SETTINGS_ID`) REFERENCES `company_settings` (`COMPANY_SETTINGS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FX1_REGION_SETTINGS_ID` FOREIGN KEY (`REGION_SETTINGS_ID`) REFERENCES `region_settings` (`REGION_SETTINGS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FX2_AGENT_SETTINGS_ID` FOREIGN KEY (`AGENT_SETTINGS_ID`) REFERENCES `agent_settings` (`AGENT_SETTINGS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FX_BRANCH_SETTINGS_ID` FOREIGN KEY (`BRANCH_SETTINGS_ID`) REFERENCES `branch_settings` (`BRANCH_SETTINGS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FX_SURVEY_DETAILS_ID` FOREIGN KEY (`SURVEY_DETAILS_ID`) REFERENCES `survey_details` (`SURVEY_DETAILS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `profiles_master`
--

DROP TABLE IF EXISTS `profiles_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `profiles_master` (
  `PROFILE_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `PROFILE` varchar(45) DEFAULT NULL,
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`PROFILE_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='Available profiles in the application.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `query_params`
--

DROP TABLE IF EXISTS `query_params`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `query_params` (
  `QUERY_PARAMS_ID` varchar(36) NOT NULL,
  `URL_DETAILS_ID` varchar(36) DEFAULT NULL,
  `USER_ID` int(10) unsigned DEFAULT NULL,
  `AGENT_ID` int(10) unsigned DEFAULT NULL,
  `COMPANY_ID` int(10) unsigned DEFAULT NULL,
  `SURVEY_PRE_INTITIATION_ID` int(10) unsigned DEFAULT NULL,
  `API_KEY` varchar(45) DEFAULT NULL,
  `COMPANY` varchar(45) DEFAULT NULL,
  `CREATOR_EMAIL_ID` text,
  `CURRENT_TIMESTAMP` varchar(45) DEFAULT NULL,
  `CUSTOMER_EMAIL` text,
  `EMAILTYPE` varchar(45) DEFAULT NULL,
  `ENTITY_ID` varchar(45) DEFAULT NULL,
  `ENTITY_TYPE` varchar(45) DEFAULT NULL,
  `FIRST_NAME` text,
  `LAST_NAME` varchar(45) DEFAULT NULL,
  `NAME` varchar(45) DEFAULT NULL,
  `PLAN_ID` varchar(45) DEFAULT NULL,
  `REFERRALCODE` varchar(45) DEFAULT NULL,
  `RESETORSET` varchar(45) DEFAULT NULL,
  `RETAKE_SURVEY` varchar(45) DEFAULT NULL,
  `UNIQUE_IDENTIFIER` varchar(45) DEFAULT NULL,
  `VERIFICATION_REQUEST_TYPE` varchar(45) DEFAULT NULL,
  `EMAIL_ID` text,
  PRIMARY KEY (`QUERY_PARAMS_ID`),
  KEY `URL_DETAILS_ID   _idx` (`URL_DETAILS_ID`),
  KEY `GX8_USER_ID_idx` (`USER_ID`),
  KEY `GX8_COMPANY_ID_idx` (`COMPANY_ID`),
  KEY `GX8_SURVEY_PRE_INTITIATION_ID_idx` (`SURVEY_PRE_INTITIATION_ID`),
  KEY `GX08_USER_ID_idx` (`USER_ID`),
  KEY `GX08_COMPANY_ID_idx` (`COMPANY_ID`),
  KEY `GX08_SURVEY_PRE_INTITIATION_ID_idx` (`SURVEY_PRE_INTITIATION_ID`),
  KEY `GX11_USER_ID_idx` (`USER_ID`),
  KEY `GX11_COMPANY_ID_idx` (`COMPANY_ID`),
  KEY `GX11_SURVEY_PRE_INTITIATION_ID_idx` (`SURVEY_PRE_INTITIATION_ID`),
  CONSTRAINT `GX11_COMPANY_ID` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `GX11_SURVEY_PRE_INTITIATION_ID` FOREIGN KEY (`SURVEY_PRE_INTITIATION_ID`) REFERENCES `survey_pre_initiation` (`SURVEY_PRE_INITIATION_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `GX11_USER_ID` FOREIGN KEY (`USER_ID`) REFERENCES `users` (`USER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `URL_DETAILS_ID   ` FOREIGN KEY (`URL_DETAILS_ID`) REFERENCES `url_details` (`URL_DETAILS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `realtor_token`
--

DROP TABLE IF EXISTS `realtor_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `realtor_token` (
  `REALTOR_TOKEN_ID` varchar(45) NOT NULL,
  `REALTOR_PROFILE_LINK` text,
  `type` int(11) NOT NULL,
  `id` int(10) NOT NULL,
  PRIMARY KEY (`type`,`id`),
  UNIQUE KEY `REALTOR_TOKEN_ID_UNIQUE` (`REALTOR_TOKEN_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `referral_invitation`
--

DROP TABLE IF EXISTS `referral_invitation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `referral_invitation` (
  `REFERRAL_INVITATION_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `REFERRAL_ID` varchar(250) NOT NULL,
  `REFERRAL_NAME` varchar(250) DEFAULT NULL,
  `REFERRAL_DESCRIPTION` varchar(500) DEFAULT NULL,
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(50) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`REFERRAL_INVITATION_ID`),
  UNIQUE KEY `REFERRAL_ID` (`REFERRAL_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB AUTO_INCREMENT=1558 DEFAULT CHARSET=utf8 COMMENT='Region details of a company. In case, if the admin decides there is no region for the company, then a default row will be added.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `region_settings`
--

DROP TABLE IF EXISTS `region_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `region_settings` (
  `REGION_SETTINGS_ID` varchar(36) NOT NULL,
  `REGION_ID` int(10) unsigned DEFAULT NULL,
  `SOCIAL_MEDIA_TOKENS_ID` varchar(45) DEFAULT NULL,
  `CREATED_BY` varchar(45) DEFAULT NULL,
  `CREATED_ON_MONGO` int(10) DEFAULT NULL,
  `CREATED_ON` datetime DEFAULT NULL,
  `MODIFIED_BY` varchar(45) DEFAULT NULL,
  `MODIFIED_ON_MONGO` int(10) DEFAULT NULL,
  `MODIFIED_ON` datetime DEFAULT NULL,
  `STATUS` varchar(45) DEFAULT NULL,
  `ALLOW_OVERRIDE_FOR_SOCIAL_MEDIA` tinyint(4) DEFAULT NULL,
  `ALLOW_ZILLOW_AUTO_POST` tinyint(4) DEFAULT NULL,
  `DISCLAIMER` varchar(45) DEFAULT NULL,
  `HIDDEN_SECTION` tinyint(4) DEFAULT NULL,
  `IS_ACCOUNT_DISABLED` tinyint(4) DEFAULT NULL,
  `IS_DEFAULT_BY_SYSTEM` tinyint(4) DEFAULT NULL,
  `IS_LOCATION_ENABLED` tinyint(4) DEFAULT NULL,
  `IS_LOGO_IMAGE_PROCESSED` tinyint(4) DEFAULT NULL,
  `IS_PROFILE_IMAGE_PROCESSED` tinyint(4) DEFAULT NULL,
  `IS_SEO_CONTENT_MODIFIED` tinyint(4) DEFAULT NULL,
  `LOGO` varchar(45) DEFAULT NULL,
  `LOGO_THUMBNAIL` varchar(45) DEFAULT NULL,
  `PROFILE_COMPLETION` int(11) DEFAULT NULL,
  `PROFILE_IMAGE_URL` varchar(45) DEFAULT NULL,
  `PROFILE_IMAGE_URL_THUMBNAIL` varchar(45) DEFAULT NULL,
  `PROFILE_NAME` varchar(45) DEFAULT NULL,
  `PROFILE_URL` varchar(45) DEFAULT NULL,
  `SEND_EMAIL_FROM_COMPANY` tinyint(4) DEFAULT NULL,
  `VENDASTA_ACCESSIBLE` tinyint(4) DEFAULT NULL,
  `VERTICAL` varchar(45) DEFAULT NULL,
  `ZILLOW_REVIEW_AVERAGE` float DEFAULT NULL,
  `ZILLOW_REVIEW_COUNT` int(10) DEFAULT NULL,
  PRIMARY KEY (`REGION_SETTINGS_ID`),
  KEY `GX_REGION_ID_idx` (`REGION_ID`),
  KEY `FK_RS_SOCIAL_MEDIA_TOKEN_idx` (`SOCIAL_MEDIA_TOKENS_ID`),
  CONSTRAINT `FK_RS_SOCIAL_MEDIA_TOKEN` FOREIGN KEY (`SOCIAL_MEDIA_TOKENS_ID`) REFERENCES `social_media_tokens` (`SOCIAL_MEDIA_TOKENS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `GX_REGION_ID` FOREIGN KEY (`REGION_ID`) REFERENCES `region` (`REGION_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `removed_user`
--

DROP TABLE IF EXISTS `removed_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `removed_user` (
  `REMOVED_USER_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `USER_ID` int(10) unsigned NOT NULL,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`REMOVED_USER_ID`),
  KEY `fk_REMOVED_USER_1_idx` (`USER_ID`),
  KEY `fk_REMOVED_USER_2_idx` (`COMPANY_ID`),
  CONSTRAINT `fk_REMOVED_USER_1` FOREIGN KEY (`USER_ID`) REFERENCES `users` (`USER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_REMOVED_USER_2` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=25369 DEFAULT CHARSET=utf8 COMMENT='Holds the information of users which have been removed from the company.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `request`
--

DROP TABLE IF EXISTS `request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `request` (
  `REQUEST_ID` int(11) NOT NULL,
  `API_REQUEST_DETAILS_ID` varchar(36) DEFAULT NULL,
  `BODY` varchar(45) DEFAULT NULL,
  `URL` varchar(45) DEFAULT NULL,
  `REQUEST_METHOD` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`REQUEST_ID`),
  KEY `API_REQUEST_DETAILS_ID _idx` (`API_REQUEST_DETAILS_ID`),
  CONSTRAINT `API_REQUEST_DETAILS_ID ` FOREIGN KEY (`API_REQUEST_DETAILS_ID`) REFERENCES `api_request_details` (`API_REQUEST_DETAILS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `response`
--

DROP TABLE IF EXISTS `response`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `response` (
  `RESPONSE_ID` int(11) NOT NULL,
  `API_REQUEST_DETAILS_ID` varchar(36) DEFAULT NULL,
  `BODY` varchar(45) DEFAULT NULL,
  `HEADER` varchar(45) DEFAULT NULL,
  `STATUS_CODE` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`RESPONSE_ID`),
  KEY `API_REQUEST_DETAILS_ID_idx1` (`API_REQUEST_DETAILS_ID`),
  CONSTRAINT `FK_API_REQUEST_DETAILS_ID` FOREIGN KEY (`API_REQUEST_DETAILS_ID`) REFERENCES `api_request_details` (`API_REQUEST_DETAILS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `retried_transactions`
--

DROP TABLE IF EXISTS `retried_transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `retried_transactions` (
  `RETRY_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `TRANSACTION_ID` varchar(20) NOT NULL,
  `LICENSE_ID` int(10) unsigned NOT NULL,
  `PAYMENT_TOKEN` varchar(20) NOT NULL,
  `AMOUNT` float NOT NULL,
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`RETRY_ID`),
  KEY `fk_RETRIED_TRANSACTIONS_LICENSE_DETAILS1_idx` (`LICENSE_ID`),
  CONSTRAINT `fk_RETRIED_TRANSACTIONS_LICENSE_DETAILS1` FOREIGN KEY (`LICENSE_ID`) REFERENCES `license_details` (`LICENSE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Holds the retried payment transaction details for a company';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `social_connection_history`
--

DROP TABLE IF EXISTS `social_connection_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `social_connection_history` (
  `SOCIAL_CONNECTION_HISTORY_ID` varchar(36) NOT NULL,
  `ACTION` varchar(45) DEFAULT NULL,
  `AGENT_ID` int(10) unsigned DEFAULT NULL,
  `BRANCH_ID` int(10) unsigned DEFAULT NULL,
  `COMPANY_ID` int(10) unsigned DEFAULT NULL,
  `REGION_ID` int(10) unsigned DEFAULT NULL,
  `SOCIAL_MEDIA_SOURCE` varchar(45) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `LINK` text,
  PRIMARY KEY (`SOCIAL_CONNECTION_HISTORY_ID`),
  KEY `GX1_AGENT_ID_idx` (`AGENT_ID`),
  KEY `GX1_COMPANY_ID_idx` (`COMPANY_ID`),
  KEY `GX1_REGION_ID_idx` (`REGION_ID`),
  KEY `GX1_BRANCH_ID_idx` (`BRANCH_ID`),
  CONSTRAINT `GX1_AGENT_ID` FOREIGN KEY (`AGENT_ID`) REFERENCES `users` (`USER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `GX1_BRANCH_ID` FOREIGN KEY (`BRANCH_ID`) REFERENCES `branch` (`BRANCH_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `GX1_COMPANY_ID` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `GX1_REGION_ID` FOREIGN KEY (`REGION_ID`) REFERENCES `region` (`REGION_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `social_media_post_details`
--

DROP TABLE IF EXISTS `social_media_post_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `social_media_post_details` (
  `SOCIAL_MEDIA_POST_DETAILS_ID` int(10) NOT NULL,
  `SURVEY_DETAILS_ID` varchar(36) DEFAULT NULL,
  `ENTITY_TYPE` varchar(45) DEFAULT NULL,
  `ENTITY_ID` int(10) DEFAULT NULL,
  `SHARED_ON_FACEBOOK` tinyint(4) DEFAULT NULL,
  `SHARED_ON_LINKEDIN` tinyint(4) DEFAULT NULL,
  `SHARED_ON_TWITTER` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`SOCIAL_MEDIA_POST_DETAILS_ID`),
  KEY `SURVEY_DETAILS_ID_idx` (`SURVEY_DETAILS_ID`),
  CONSTRAINT `FX6_SURVEY_DETAILS_ID` FOREIGN KEY (`SURVEY_DETAILS_ID`) REFERENCES `survey_details` (`SURVEY_DETAILS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `social_media_post_response_details`
--

DROP TABLE IF EXISTS `social_media_post_response_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `social_media_post_response_details` (
  `SOCIAL_MEDIA_POST_RESPONSE_DETAILS_ID` int(10) NOT NULL,
  `SURVEY_DETAILS_ID` varchar(36) DEFAULT NULL,
  `ENTITY_TYPE` varchar(45) DEFAULT NULL,
  `ENTITY_ID` int(10) DEFAULT NULL,
  `FACEBOOK_POST_RESPONSE_LIST_ID` int(10) DEFAULT NULL,
  `LINKEDIN_POST_RESPONSE_LIST_ID` int(10) DEFAULT NULL,
  `TWITTER_POST_RESPONSE_LIST_ID` int(10) DEFAULT NULL,
  PRIMARY KEY (`SOCIAL_MEDIA_POST_RESPONSE_DETAILS_ID`),
  KEY `SURVEY_DETAILS_ID_idx` (`SURVEY_DETAILS_ID`),
  KEY `FACEBOOK_POST_RESPONSE_LIST_ID_idx` (`FACEBOOK_POST_RESPONSE_LIST_ID`),
  KEY `LINKEDIN_POST_RESPONSE_LIST_ID_idx` (`LINKEDIN_POST_RESPONSE_LIST_ID`),
  KEY `TWITTER_POST_RESPONSE_LIST_ID_idx` (`TWITTER_POST_RESPONSE_LIST_ID`),
  CONSTRAINT `FACEBOOK_POST_RESPONSE_LIST_ID` FOREIGN KEY (`FACEBOOK_POST_RESPONSE_LIST_ID`) REFERENCES `facebook_post_response_list` (`FACEBOOK_POST_RESPONSE_LIST_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FX7_SURVEY_DETAILS_ID` FOREIGN KEY (`SURVEY_DETAILS_ID`) REFERENCES `survey_details` (`SURVEY_DETAILS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `LINKEDIN_POST_RESPONSE_LIST_ID` FOREIGN KEY (`LINKEDIN_POST_RESPONSE_LIST_ID`) REFERENCES `linkedin_post_response_list` (`LINKEDIN_POST_RESPONSE_LIST_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `TWITTER_POST_RESPONSE_LIST_ID` FOREIGN KEY (`TWITTER_POST_RESPONSE_LIST_ID`) REFERENCES `twitter_post_response_list` (`TWITTER_POST_RESPONSE_LIST_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `social_media_tokens`
--

DROP TABLE IF EXISTS `social_media_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `social_media_tokens` (
  `SOCIAL_MEDIA_TOKENS_ID` varchar(45) NOT NULL,
  `FACEBOOK_TOKEN_ID` varchar(45) DEFAULT NULL,
  `GOOGLE_BUSINESS_TOKEN_ID` varchar(45) DEFAULT NULL,
  `GOOGLE_TOKEN_ID` varchar(45) DEFAULT NULL,
  `LENDING_TREE_TOKEN_ID` varchar(45) DEFAULT NULL,
  `LINKED_IN_TOKEN_ID` varchar(45) DEFAULT NULL,
  `REALTOR_TOKEN_ID` varchar(45) DEFAULT NULL,
  `TWITTER_TOKEN_ID` varchar(45) DEFAULT NULL,
  `YELP_TOKEN_ID` varchar(45) DEFAULT NULL,
  `ZILLOW_TOKEN_ID` varchar(45) DEFAULT NULL,
  `type` int(11) NOT NULL,
  `id` int(10) NOT NULL,
  PRIMARY KEY (`type`,`id`),
  UNIQUE KEY `SOCIAL_MEDIA_TOKENS_ID_UNIQUE` (`SOCIAL_MEDIA_TOKENS_ID`),
  KEY `FK_FB_TOKEN_ID` (`FACEBOOK_TOKEN_ID`),
  KEY `FK_GB_TOKEN_ID` (`GOOGLE_BUSINESS_TOKEN_ID`),
  KEY `FK_GOOGLE_TOKEN_ID` (`GOOGLE_TOKEN_ID`),
  KEY `FK_LENDING_TREE_TOKEN_ID` (`LENDING_TREE_TOKEN_ID`),
  KEY `LINKED_IN_TOKEN_ID` (`LINKED_IN_TOKEN_ID`),
  KEY `REALTOR_TOKEN_ID` (`REALTOR_TOKEN_ID`),
  KEY `TWITTER_TOKEN_ID` (`TWITTER_TOKEN_ID`),
  KEY `YELP_TOKEN_ID` (`YELP_TOKEN_ID`),
  KEY `ZILLOW_TOKEN_ID` (`ZILLOW_TOKEN_ID`),
  CONSTRAINT `FK_FB_TOKEN_ID` FOREIGN KEY (`FACEBOOK_TOKEN_ID`) REFERENCES `facebook_token` (`FACEBOOK_TOKEN_ID`),
  CONSTRAINT `FK_GB_TOKEN_ID` FOREIGN KEY (`GOOGLE_BUSINESS_TOKEN_ID`) REFERENCES `google_business_token` (`GOOGLE_BUSINESS_TOKEN_ID`),
  CONSTRAINT `FK_GOOGLE_TOKEN_ID` FOREIGN KEY (`GOOGLE_TOKEN_ID`) REFERENCES `google_token` (`GOOGLE_TOKEN_ID`),
  CONSTRAINT `FK_LENDING_TREE_TOKEN_ID` FOREIGN KEY (`LENDING_TREE_TOKEN_ID`) REFERENCES `lending_tree_token` (`LENDING_TREE_TOKEN_ID`),
  CONSTRAINT `LINKED_IN_TOKEN_ID` FOREIGN KEY (`LINKED_IN_TOKEN_ID`) REFERENCES `linked_in_token` (`LINKED_IN_TOKEN_ID`),
  CONSTRAINT `REALTOR_TOKEN_ID` FOREIGN KEY (`REALTOR_TOKEN_ID`) REFERENCES `realtor_token` (`REALTOR_TOKEN_ID`),
  CONSTRAINT `TWITTER_TOKEN_ID` FOREIGN KEY (`TWITTER_TOKEN_ID`) REFERENCES `twitter_token` (`TWITTER_TOKEN_ID`),
  CONSTRAINT `YELP_TOKEN_ID` FOREIGN KEY (`YELP_TOKEN_ID`) REFERENCES `yelp_token` (`YELP_TOKEN_ID`),
  CONSTRAINT `ZILLOW_TOKEN_ID` FOREIGN KEY (`ZILLOW_TOKEN_ID`) REFERENCES `zillow_token` (`ZILLOW_TOKEN_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `social_post`
--

DROP TABLE IF EXISTS `social_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `social_post` (
  `SOCIAL_POST_ID` varchar(36) NOT NULL,
  `AGENT_ID` int(10) DEFAULT NULL,
  `BRANCH_ID` int(10) DEFAULT NULL,
  `REGION_ID` int(10) DEFAULT NULL,
  `COMPANY_ID` int(10) DEFAULT NULL,
  `POSTED_BY` varchar(45) DEFAULT NULL,
  `POST_ID` varchar(45) DEFAULT NULL,
  `POST_TEXT` blob,
  `POST_URL` longtext,
  `SOURCE` longtext,
  `TIME_IN_MILLIS` bigint(10) DEFAULT NULL,
  `TOKEN` text,
  PRIMARY KEY (`SOCIAL_POST_ID`),
  KEY `GX2_AGENT_ID_idx` (`AGENT_ID`),
  KEY `GX2_BRANCH_ID_idx` (`BRANCH_ID`),
  KEY `GX2_REGION_ID_idx` (`REGION_ID`),
  KEY `GX2_COMPANY_ID    _idx` (`COMPANY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `statelookup`
--

DROP TABLE IF EXISTS `statelookup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `statelookup` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `statecode` varchar(45) DEFAULT NULL,
  `statename` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `survey`
--

DROP TABLE IF EXISTS `survey`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `survey` (
  `SURVEY_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `SURVEY_NAME` varchar(100) DEFAULT NULL,
  `STATUS` int(1) NOT NULL,
  `IS_SURVEY_BUILDING_COMPLETE` int(1) DEFAULT '0',
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`SURVEY_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=543 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `survey_company_mapping`
--

DROP TABLE IF EXISTS `survey_company_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `survey_company_mapping` (
  `SURVEY_COMPANY_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `SURVEY_ID` int(10) unsigned NOT NULL,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`SURVEY_COMPANY_ID`),
  KEY `fk_SURVEY_COMPANY_MAPPING_COMPANY1_idx` (`COMPANY_ID`),
  KEY `fk_SURVEY_COMPANY_MAPPING_SURVEY1_idx` (`SURVEY_ID`),
  CONSTRAINT `fk_SURVEY_COMPANY_MAPPING_COMPANY1` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_SURVEY_COMPANY_MAPPING_SURVEY1` FOREIGN KEY (`SURVEY_ID`) REFERENCES `survey` (`SURVEY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1141 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `survey_details`
--

DROP TABLE IF EXISTS `survey_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `survey_details` (
  `SURVEY_DETAILS_ID` varchar(36) NOT NULL,
  `IDEN` int(10) DEFAULT NULL,
  `AGENT_ID` int(10) unsigned DEFAULT NULL,
  `AGENT_NAME` varchar(45) DEFAULT NULL,
  `BRANCH_ID` int(10) unsigned DEFAULT NULL,
  `COMPANY_ID` int(10) unsigned DEFAULT NULL,
  `REGION_ID` int(10) unsigned DEFAULT NULL,
  `SURVEY_PRE_INTITIATION_ID` int(10) unsigned DEFAULT NULL,
  `CUSTOMER_EMAIL` varchar(45) DEFAULT NULL,
  `CUSTOMER_FIRST_NAME` varchar(45) DEFAULT NULL,
  `CUSTOMER_LAST_NAME` varchar(45) DEFAULT NULL,
  `CREATED_BY` varchar(45) DEFAULT NULL,
  `CREATED_ON` datetime DEFAULT NULL,
  `MODIFIED_BY` varchar(45) DEFAULT NULL,
  `MODIFIED_ON` datetime DEFAULT NULL,
  `SOURCE` varchar(45) DEFAULT NULL,
  `SOURCE_ID` varchar(45) DEFAULT NULL,
  `STAGE` int(11) DEFAULT NULL,
  `STATE` varchar(45) DEFAULT NULL,
  `STATUS` varchar(45) DEFAULT NULL,
  `MOOD` varchar(45) DEFAULT NULL,
  `SURVEY_CLICKED` tinyint(4) DEFAULT NULL,
  `SURVEY_COMPLETED_DATE` datetime DEFAULT NULL,
  `SURVEY_TRANSACTION_DATE` datetime DEFAULT NULL,
  `SURVEY_UPDATED_DATE` datetime DEFAULT NULL,
  `AGREED_TO_SHARE` varchar(45) DEFAULT NULL,
  `ALLOW_OVERRIDE_FOR_SOCIAL_MEDIA` tinyint(4) DEFAULT NULL,
  `ALLOW_ZILLOW_AUTO_POST` tinyint(4) DEFAULT NULL,
  `CITY` varchar(45) DEFAULT NULL,
  `COMPLETE_PROFILE_URL` varchar(45) DEFAULT NULL,
  `EDITABLE` tinyint(4) DEFAULT NULL,
  `HIDDEN_SECTION` tinyint(4) DEFAULT NULL,
  `IS_ABUSE_REP_BY_USER` tinyint(4) DEFAULT NULL,
  `IS_ABUSIVE` tinyint(4) DEFAULT NULL,
  `IS_ACCOUNT_DISABLED` tinyint(4) DEFAULT NULL,
  `IS_DEFAULT_BY_SYSTEM` tinyint(4) DEFAULT NULL,
  `IS_LOCATION_ENABLED` tinyint(4) DEFAULT NULL,
  `IS_LOGO_IMAGE_PROCESSED` tinyint(4) DEFAULT NULL,
  `IS_PROFILE_IMAGE_PROCESSED` tinyint(4) DEFAULT NULL,
  `IS_SEO_CONTENT_MODIFIED` tinyint(4) DEFAULT NULL,
  `LAST_REMINDER_FOR_SOCIAL_POST` datetime DEFAULT NULL,
  `LOGO` varchar(45) DEFAULT NULL,
  `LOGO_THUMBNAIL` varchar(45) DEFAULT NULL,
  `PROFILE_COMPLETION` int(11) DEFAULT NULL,
  `PROFILE_NAME` varchar(45) DEFAULT NULL,
  `PROFILE_URL` varchar(45) DEFAULT NULL,
  `REMINDER_COUNT` int(11) DEFAULT NULL,
  `REMINDERS_FOR_SOCIAL_POSTS` text,
  `RETAKE_SURVEY` tinyint(4) DEFAULT NULL,
  `REVIEW` varchar(45) DEFAULT NULL,
  `REVIEW_SORT_CRITERIA` varchar(45) DEFAULT NULL,
  `SCORE` int(11) DEFAULT NULL,
  `SEND_EMAIL_FROM_COMPANY` tinyint(4) DEFAULT NULL,
  `SHOW_SURVEY_ON_UI` tinyint(4) DEFAULT NULL,
  `SOCIAL_POSTS_REMINDER` int(11) DEFAULT NULL,
  `SUMMARY` varchar(45) DEFAULT NULL,
  `UNDER_RESOLUTION` tinyint(4) DEFAULT NULL,
  `URL` varchar(45) DEFAULT NULL,
  `VENDASTA_ACCESSIBLE` tinyint(4) DEFAULT NULL,
  `VERTICAL` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`SURVEY_DETAILS_ID`),
  KEY `GX3_AGENT_ID_idx` (`AGENT_ID`),
  KEY `GX3_BRANCH_ID_idx` (`BRANCH_ID`),
  KEY `GX3_COMPANY_ID_idx` (`COMPANY_ID`),
  KEY `GX3_REGION_ID_idx` (`REGION_ID`),
  KEY `GX3_SURVEY_PRE_INTITIATION_ID_idx` (`SURVEY_PRE_INTITIATION_ID`),
  CONSTRAINT `GX3_AGENT_ID` FOREIGN KEY (`AGENT_ID`) REFERENCES `users` (`USER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `GX3_BRANCH_ID` FOREIGN KEY (`BRANCH_ID`) REFERENCES `branch` (`BRANCH_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `GX3_COMPANY_ID` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `GX3_REGION_ID` FOREIGN KEY (`REGION_ID`) REFERENCES `region` (`REGION_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `GX3_SURVEY_PRE_INTITIATION_ID` FOREIGN KEY (`SURVEY_PRE_INTITIATION_ID`) REFERENCES `survey_pre_initiation` (`SURVEY_PRE_INITIATION_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `survey_mongo_data`
--

DROP TABLE IF EXISTS `survey_mongo_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `survey_mongo_data` (
  `TMP_ezadv_mongo_survey_details_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `isAbusive` text,
  `_id` text,
  `score` text,
  `mood` text,
  `surveyUpdatedDate` text,
  `editable` text,
  `customerEmail` text,
  `surveyTransactionDate` text,
  `companyId` text,
  `reminderCount` text,
  `remindersForSocialPosts` text,
  `surveyPreIntitiationId` text,
  `underResolution` text,
  `isAbuseRepByUser` text,
  `socialPostsReminder` text,
  `sourceId` text,
  `customerLastName` text,
  `source` text,
  `lastReminderForSocialPost` text,
  `customerFirstName` text,
  `branchId` text,
  `createdOn` text,
  `modifiedOn` text,
  `agentId` text,
  `regionId` text,
  `agreedToShare` text,
  `agentName` text,
  `showSurveyOnUI` text,
  `retakeSurvey` text,
  `stage` text,
  `surveyCompletedDate` text,
  `surveyClicked` text,
  `isUnmarkedAbusive` text,
  PRIMARY KEY (`TMP_ezadv_mongo_survey_details_id`),
  UNIQUE KEY `TMP_ezadv_mongo_survey_details_id_UNIQUE` (`TMP_ezadv_mongo_survey_details_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `survey_pre_initiation`
--

DROP TABLE IF EXISTS `survey_pre_initiation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `survey_pre_initiation` (
  `SURVEY_PRE_INITIATION_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `SURVEY_SOURCE` varchar(100) NOT NULL,
  `SURVEY_SOURCE_ID` varchar(250) DEFAULT NULL,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `AGENT_ID` int(11) NOT NULL,
  `AGENT_NAME` varchar(250) DEFAULT NULL,
  `AGENT_EMAILID` varchar(255) DEFAULT NULL,
  `CUSTOMER_FIRST_NAME` varchar(100) NOT NULL,
  `CUSTOMER_LAST_NAME` varchar(100) DEFAULT NULL,
  `CUSTOMER_EMAIL_ID` varchar(250) NOT NULL,
  `CUSTOMER_INTERACTION_DETAILS` varchar(500) DEFAULT NULL,
  `ENGAGEMENT_CLOSED_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `REMINDER_COUNTS` int(11) NOT NULL,
  `LAST_REMINDER_TIME` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `REGION_COLLECTION_ID` int(11) NOT NULL DEFAULT '-1',
  `BRANCH_COLLECTION_ID` int(11) NOT NULL DEFAULT '-1',
  `COLLECTION_NAME` varchar(200) DEFAULT NULL,
  `ERROR_CODE` varchar(100) DEFAULT NULL,
  `STATE` varchar(100) DEFAULT NULL,
  `CITY` varchar(100) DEFAULT NULL,
  `TRANSACTION_TYPE` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`SURVEY_PRE_INITIATION_ID`),
  KEY `index_agent_id` (`AGENT_ID`),
  KEY `index_status` (`STATUS`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Holds survey pre-initiation details';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `survey_pre_initiation_status_lookup`
--

DROP TABLE IF EXISTS `survey_pre_initiation_status_lookup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `survey_pre_initiation_status_lookup` (
  `STATUS_ID` int(2) NOT NULL,
  `STATUS_NAME` varchar(45) NOT NULL,
  PRIMARY KEY (`STATUS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `survey_questions`
--

DROP TABLE IF EXISTS `survey_questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `survey_questions` (
  `SURVEY_QUESTIONS_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `SURVEY_QUESTIONS_CODE` varchar(25) NOT NULL COMMENT 'Pre defined code for survey questions. Determines the type of questions',
  `SURVEY_QUESTION` varchar(500) NOT NULL,
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`SURVEY_QUESTIONS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Holds the survey questions for a survey';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `survey_questions_answer_options`
--

DROP TABLE IF EXISTS `survey_questions_answer_options`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `survey_questions_answer_options` (
  `SURVEY_QUESTIONS_ANSWER_OPTIONS_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `SURVEY_QUESTIONS_ID` int(10) unsigned NOT NULL,
  `ANSWER` varchar(250) NOT NULL,
  `ANSWER_ORDER` int(2) DEFAULT NULL,
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`SURVEY_QUESTIONS_ANSWER_OPTIONS_ID`),
  KEY `fk_SURVEY_QUESTIONS_ANSWER_OPTIONS_SURVEY_QUESTIONS1_idx` (`SURVEY_QUESTIONS_ID`),
  CONSTRAINT `fk_SURVEY_QUESTIONS_ANSWER_OPTIONS_SURVEY_QUESTIONS1` FOREIGN KEY (`SURVEY_QUESTIONS_ID`) REFERENCES `survey_questions` (`SURVEY_QUESTIONS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `survey_questions_mapping`
--

DROP TABLE IF EXISTS `survey_questions_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `survey_questions_mapping` (
  `SURVEY_QUESTIONS_MAPPING_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `SURVEY_ID` int(10) unsigned NOT NULL,
  `SURVEY_QUESTIONS_ID` int(10) unsigned NOT NULL,
  `QUESTION_ORDER` int(2) NOT NULL,
  `IS_RATING_QUESTION` int(1) NOT NULL,
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`SURVEY_QUESTIONS_MAPPING_ID`),
  KEY `fk_SURVEY_QUESTIONS_MAPPING_SURVEY1_idx` (`SURVEY_ID`),
  KEY `fk_SURVEY_QUESTIONS_MAPPING_SURVEY_QUESTIONS1_idx` (`SURVEY_QUESTIONS_ID`),
  CONSTRAINT `fk_SURVEY_QUESTIONS_MAPPING_SURVEY1` FOREIGN KEY (`SURVEY_ID`) REFERENCES `survey` (`SURVEY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_SURVEY_QUESTIONS_MAPPING_SURVEY_QUESTIONS1` FOREIGN KEY (`SURVEY_QUESTIONS_ID`) REFERENCES `survey_questions` (`SURVEY_QUESTIONS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `survey_settings`
--

DROP TABLE IF EXISTS `survey_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `survey_settings` (
  `SURVEY_SETTINGS_ID` int(11) NOT NULL,
  `AGENT_SETTINGS_ID` varchar(36) DEFAULT NULL,
  `BRANCH_SETTINGS_ID` varchar(36) DEFAULT NULL,
  `REGION_SETTINGS_ID` varchar(36) DEFAULT NULL,
  `COMPANY_SETTINGS_ID` varchar(36) DEFAULT NULL,
  `SURVEY_DETAILS_ID` varchar(36) DEFAULT NULL,
  `AUTO_POST_SCORE` int(11) DEFAULT NULL,
  `AUTO_POST_ENABLED` tinyint(4) DEFAULT NULL,
  `AUTO_POST_LINK_TO_USER_SITE_ENABLED` tinyint(4) DEFAULT NULL,
  `DUPLICATE_SURVEY_INTERVAL` int(11) DEFAULT NULL,
  `HAPPY_TEXT` varchar(45) DEFAULT NULL,
  `HAPPY_TEXT_COMPLETE` varchar(45) DEFAULT NULL,
  `IS_REMINDER_DISABLED` tinyint(4) DEFAULT NULL,
  `IS_SOCIAL_POST_REMINDER_DISABLED` tinyint(4) DEFAULT NULL,
  `MAX_NUMBER_OF_SOCIAL_POS_REMINDERS` int(11) DEFAULT NULL,
  `MAX_NUMBER_OF_SURVEY_REMINDERS` int(11) DEFAULT NULL,
  `NEUTRAL_TEXT` varchar(45) DEFAULT NULL,
  `NEUTRAL_TEXT_COMPLETE` varchar(45) DEFAULT NULL,
  `SAD_TEXT` varchar(45) DEFAULT NULL,
  `SAD_TEXT_COMPLETE` varchar(45) DEFAULT NULL,
  `SHOW_SURVEY_ABOVE_SCORE` int(11) DEFAULT NULL,
  `SOCIAL_POST_REMINDER_INTERVAL_IN_DAYS` int(11) DEFAULT NULL,
  `SURVEY_REMINDER_INTERVAL_IN_DAYS` int(11) DEFAULT NULL,
  PRIMARY KEY (`SURVEY_SETTINGS_ID`),
  KEY `FX4_AGENT_SETTINGS_ID_idx` (`AGENT_SETTINGS_ID`),
  KEY `FX2_BRANCH_SETTINGS_ID_idx` (`BRANCH_SETTINGS_ID`),
  KEY `FX3_REGION_SETTINGS_ID_idx` (`REGION_SETTINGS_ID`),
  KEY `FX3_COMPANY_SETTINGS_ID_idx` (`COMPANY_SETTINGS_ID`),
  KEY `FX2_SURVEY_DETAILS_ID_idx` (`SURVEY_DETAILS_ID`),
  CONSTRAINT `FX2_BRANCH_SETTINGS_ID` FOREIGN KEY (`BRANCH_SETTINGS_ID`) REFERENCES `branch_settings` (`BRANCH_SETTINGS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FX2_SURVEY_DETAILS_ID` FOREIGN KEY (`SURVEY_DETAILS_ID`) REFERENCES `survey_details` (`SURVEY_DETAILS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FX3_COMPANY_SETTINGS_ID` FOREIGN KEY (`COMPANY_SETTINGS_ID`) REFERENCES `company_settings` (`COMPANY_SETTINGS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FX3_REGION_SETTINGS_ID` FOREIGN KEY (`REGION_SETTINGS_ID`) REFERENCES `region_settings` (`REGION_SETTINGS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FX4_AGENT_SETTINGS_ID` FOREIGN KEY (`AGENT_SETTINGS_ID`) REFERENCES `agent_settings` (`AGENT_SETTINGS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `survey_vertical_mapping`
--

DROP TABLE IF EXISTS `survey_vertical_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `survey_vertical_mapping` (
  `SURVEY_VERTICAL_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `SURVEY_ID` int(10) unsigned NOT NULL,
  `VERTICAL_ID` int(11) NOT NULL,
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`SURVEY_VERTICAL_ID`),
  KEY `fk_SURVEY_VERTICAL_MAPPING_VERTICAL1_idx` (`VERTICAL_ID`),
  KEY `fk_SURVEY_VERTICAL_MAPPING_SURVEY1_idx` (`SURVEY_ID`),
  CONSTRAINT `fk_SURVEY_VERTICAL_MAPPING_SURVEY1` FOREIGN KEY (`SURVEY_ID`) REFERENCES `survey` (`SURVEY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_SURVEY_VERTICAL_MAPPING_VERTICAL1` FOREIGN KEY (`VERTICAL_ID`) REFERENCES `verticals_master` (`VERTICALS_MASTER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `testtable`
--

DROP TABLE IF EXISTS `testtable`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `testtable` (
  `SOCIAL_MEDIA_TOKENS_ID` varchar(45) NOT NULL,
  `FACEBOOK_TOKEN_ID` varchar(45) DEFAULT NULL,
  `GOOGLE_BUSINESS_TOKEN_ID` varchar(45) DEFAULT NULL,
  `GOOGLE_TOKEN_ID` varchar(45) DEFAULT NULL,
  `LENDING_TREE_TOKEN_ID` varchar(45) DEFAULT NULL,
  `LINKED_IN_TOKEN_ID` varchar(45) DEFAULT NULL,
  `REALTOR_TOKEN_ID` varchar(45) DEFAULT NULL,
  `TWITTER_TOKEN_ID` varchar(45) DEFAULT NULL,
  `YELP_TOKEN_ID` varchar(45) DEFAULT NULL,
  `ZILLOW_TOKEN_ID` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `twitter_post_response_list`
--

DROP TABLE IF EXISTS `twitter_post_response_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `twitter_post_response_list` (
  `TWITTER_POST_RESPONSE_LIST_ID` int(10) NOT NULL,
  `ACCESS_TOKEN` varchar(45) DEFAULT NULL,
  `POST_DATE` datetime DEFAULT NULL,
  `RESPONSE_MESSAGE` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`TWITTER_POST_RESPONSE_LIST_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `twitter_token`
--

DROP TABLE IF EXISTS `twitter_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `twitter_token` (
  `TWITTER_TOKEN_ID` varchar(45) NOT NULL,
  `TWITTER_ACCESS_TOKEN` text,
  `TWITTER_ACCESS_TOKEN_CREATED_ON` datetime DEFAULT NULL,
  `TWITTER_ACCESS_TOKEN_SECRET` varchar(45) DEFAULT NULL,
  `TWITTER_PAGE_LINK` text,
  `type` int(11) NOT NULL,
  `id` int(10) NOT NULL,
  PRIMARY KEY (`type`,`id`),
  UNIQUE KEY `TWITTER_TOKEN_ID_UNIQUE` (`TWITTER_TOKEN_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `upload_hierarchy_details`
--

DROP TABLE IF EXISTS `upload_hierarchy_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `upload_hierarchy_details` (
  `UPLOAD_HIERARCHY_DETAILS_ID` varchar(36) NOT NULL,
  `COMPANY_ID` int(10) unsigned DEFAULT NULL,
  `IS_MODIFIED_FROM_UI` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`UPLOAD_HIERARCHY_DETAILS_ID`),
  KEY `GX4_COMPANY_ID_idx` (`COMPANY_ID`),
  CONSTRAINT `GX4_COMPANY_ID` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `upload_hierarchy_details_branch_source_mapping`
--

DROP TABLE IF EXISTS `upload_hierarchy_details_branch_source_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `upload_hierarchy_details_branch_source_mapping` (
  `UPLOAD_HIERARCHY_DETAILS_BRANCH_SOURCE_MAPPING_ID` int(10) NOT NULL,
  `UPLOAD_HIERARCHY_DETAILS_ID` varchar(36) DEFAULT NULL,
  `GENERATED_UNIQUE_KEYS` int(10) DEFAULT NULL,
  PRIMARY KEY (`UPLOAD_HIERARCHY_DETAILS_BRANCH_SOURCE_MAPPING_ID`),
  KEY `UPLOAD_HIERARCHY_DETAILS_ID_idx` (`UPLOAD_HIERARCHY_DETAILS_ID`),
  CONSTRAINT `FX_UPLOAD_HIERARCHY_DETAILS_ID` FOREIGN KEY (`UPLOAD_HIERARCHY_DETAILS_ID`) REFERENCES `upload_hierarchy_details` (`UPLOAD_HIERARCHY_DETAILS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `upload_hierarchy_details_branches`
--

DROP TABLE IF EXISTS `upload_hierarchy_details_branches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `upload_hierarchy_details_branches` (
  `UPLOAD_HIERARCHY_DETAILS_BRANCHES_ID` int(10) NOT NULL,
  `UPLOAD_HIERARCHY_DETAILS_ID` varchar(36) DEFAULT NULL,
  `BRANCH_ID` int(10) unsigned DEFAULT NULL,
  `REGION_ID` int(10) unsigned DEFAULT NULL,
  `ASSIGNED_REGION_NAME` varchar(45) DEFAULT NULL,
  `ASSIGN_TO_COMPANY` tinyint(4) DEFAULT NULL,
  `BRANCH_ADDRESS1` varchar(45) DEFAULT NULL,
  `BRANCH_ADDRESS2` varchar(45) DEFAULT NULL,
  `BRANCH_CITY` varchar(45) DEFAULT NULL,
  `BRANCH_COUNTRY` varchar(45) DEFAULT NULL,
  `BRANCH_COUNTRY_CODE` varchar(45) DEFAULT NULL,
  `BRANCH_NAME` varchar(45) DEFAULT NULL,
  `BRANCH_STATE` varchar(45) DEFAULT NULL,
  `BRANCH_ZIPCODE` varchar(45) DEFAULT NULL,
  `IS_ADDRESS_SET` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_ADDED` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_ADDRESS1_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_ADDRESS2_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_CITY_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_COUNTRY_CODE_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_COUNTRY_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_ID_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_NAME_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_STATE_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_ZIPCODE_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_DELETED_RECORD` tinyint(4) DEFAULT NULL,
  `IS_ERROR_RECORD` tinyint(4) DEFAULT NULL,
  `IS_IN_APPEND_MODE` tinyint(4) DEFAULT NULL,
  `IS_REGION_ID_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_SOURCE_BRANCH_ID_GENERATED` tinyint(4) DEFAULT NULL,
  `IS_SOURCE_BRANCH_ID_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_SOURCE_REGION_ID_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_WARNING_RECORD` tinyint(4) DEFAULT NULL,
  `ROW_NUM` varchar(45) DEFAULT NULL,
  `SOURCE_BRANCH_ID` varchar(45) DEFAULT NULL,
  `SOURCE_REGION_ID` varchar(45) DEFAULT NULL,
  `VALIDATION_ERRORS` text,
  `VALIDATION_WARNINGS` text,
  PRIMARY KEY (`UPLOAD_HIERARCHY_DETAILS_BRANCHES_ID`),
  KEY `UPLOAD_HIERARCHY_DETAILS_ID_idx` (`UPLOAD_HIERARCHY_DETAILS_ID`),
  KEY `GX4_BRANCH_ID_idx` (`BRANCH_ID`),
  KEY `GX4_REGION_ID_idx` (`REGION_ID`),
  CONSTRAINT `GX4_BRANCH_ID` FOREIGN KEY (`BRANCH_ID`) REFERENCES `branch` (`BRANCH_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `GX4_REGION_ID` FOREIGN KEY (`REGION_ID`) REFERENCES `region` (`REGION_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `UPLOAD_HIERARCHY_DETAILS_ID` FOREIGN KEY (`UPLOAD_HIERARCHY_DETAILS_ID`) REFERENCES `upload_hierarchy_details` (`UPLOAD_HIERARCHY_DETAILS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `upload_hierarchy_details_region_source_mapping`
--

DROP TABLE IF EXISTS `upload_hierarchy_details_region_source_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `upload_hierarchy_details_region_source_mapping` (
  `UPLOAD_HIERARCHY_DETAILS_REGION_SOURCE_MAPPING_ID` int(10) NOT NULL,
  `UPLOAD_HIERARCHY_DETAILS_ID` varchar(36) DEFAULT NULL,
  `GENERATED_UNIQUE_KEYS` int(10) DEFAULT NULL,
  PRIMARY KEY (`UPLOAD_HIERARCHY_DETAILS_REGION_SOURCE_MAPPING_ID`),
  KEY `FX3_UPLOAD_HIERARCHY_DETAILS_ID_idx` (`UPLOAD_HIERARCHY_DETAILS_ID`),
  CONSTRAINT `FX3_UPLOAD_HIERARCHY_DETAILS_ID` FOREIGN KEY (`UPLOAD_HIERARCHY_DETAILS_ID`) REFERENCES `upload_hierarchy_details` (`UPLOAD_HIERARCHY_DETAILS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `upload_hierarchy_details_regions`
--

DROP TABLE IF EXISTS `upload_hierarchy_details_regions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `upload_hierarchy_details_regions` (
  `UPLOAD_HIERARCHY_DETAILS_REGIONS_ID` int(10) NOT NULL,
  `UPLOAD_HIERARCHY_DETAILS_ID` varchar(36) DEFAULT NULL,
  `REGION_ID` int(10) unsigned DEFAULT NULL,
  `IS_ADDRESS_SET` tinyint(4) DEFAULT NULL,
  `IS_DELETED_RECORD` tinyint(4) DEFAULT NULL,
  `IS_ERROR_RECORD` tinyint(4) DEFAULT NULL,
  `IS_IN_APPEND_MODE` tinyint(4) DEFAULT NULL,
  `IS_REGION_ADDED` tinyint(4) DEFAULT NULL,
  `IS_REGION_ADDRESS1_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_REGION_ADDRESS2_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_REGION_CITY_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_REGION_COUNTRY_CODE_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_REGION_COUNTRY_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_REGION_ID_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_REGION_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_REGION_NAME_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_REGION_STATE_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_REGION_ZIPCODE_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_SOURCE_REGION_ID_GENERATED` tinyint(4) DEFAULT NULL,
  `IS_SOURCE_REGION_ID_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_WARNING_RECORD` tinyint(4) DEFAULT NULL,
  `REGION_ADDRESS1` varchar(45) DEFAULT NULL,
  `REGION_ADDRESS2` varchar(45) DEFAULT NULL,
  `REGION_CITY` varchar(45) DEFAULT NULL,
  `REGION_COUNTRY` varchar(45) DEFAULT NULL,
  `REGION_COUNTRY_CODE` varchar(45) DEFAULT NULL,
  `REGION_NAME` varchar(45) DEFAULT NULL,
  `REGION_STATE` varchar(45) DEFAULT NULL,
  `REGION_ZIPCODE` varchar(45) DEFAULT NULL,
  `ROW_NUM` int(11) DEFAULT NULL,
  `SOURCE_REGION_ID` varchar(45) DEFAULT NULL,
  `VALIDATION_ERRORS` text,
  `VALIDATION_WARNINGS` text,
  PRIMARY KEY (`UPLOAD_HIERARCHY_DETAILS_REGIONS_ID`),
  KEY `FX1_UPLOAD_HIERARCHY_DETAILS_ID_idx` (`UPLOAD_HIERARCHY_DETAILS_ID`),
  KEY `GX5_REGION_ID_idx` (`REGION_ID`),
  KEY `GX6_REGION_ID_idx` (`REGION_ID`),
  CONSTRAINT `FX1_UPLOAD_HIERARCHY_DETAILS_ID` FOREIGN KEY (`UPLOAD_HIERARCHY_DETAILS_ID`) REFERENCES `upload_hierarchy_details` (`UPLOAD_HIERARCHY_DETAILS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `GX6_REGION_ID` FOREIGN KEY (`REGION_ID`) REFERENCES `region` (`REGION_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `upload_hierarchy_details_users`
--

DROP TABLE IF EXISTS `upload_hierarchy_details_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `upload_hierarchy_details_users` (
  `UPLOAD_HIERARCHY_DETAILS_USERS_ID` int(10) NOT NULL,
  `UPLOAD_HIERARCHY_DETAILS_ID` varchar(36) DEFAULT NULL,
  `BRANCH_ID` int(10) unsigned DEFAULT NULL,
  `REGION_ID` int(10) unsigned DEFAULT NULL,
  `USER_ID` int(10) unsigned DEFAULT NULL,
  `ABOUT_ME_DESCRIPTION` varchar(45) DEFAULT NULL,
  `ASSIGNED_BRANCHES` text,
  `ASSIGNED_BRANCHES_ADMIN` text,
  `ASSIGNED_REGIONS` text,
  `ASSIGNED_REGIONS_ADMIN` text,
  `UPLOAD_HIERARCHY_DETAILS_USERScol` text,
  `ASSIGN_TO_COMPANY` tinyint(4) DEFAULT NULL,
  `BELONGS_TO_COMPANY` tinyint(4) DEFAULT NULL,
  `EMAIL_ID` varchar(45) DEFAULT NULL,
  `FIRST_NAME` varchar(45) DEFAULT NULL,
  `IS_ABOUT_ME_DESCRIPTION_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_AGENT` tinyint(4) DEFAULT NULL,
  `IS_AGENT_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_ASSIGNED_BRACHES_ADMIN_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_ASSIGNED_BRANCHES_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_ASSIGNED_BRANCH_NAME_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_ASSIGNED_REGION_NAME_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_ASSIGNED_REGIONS_ADMIN_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_ASSIGNED_REGIONS_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_ASSIGN_TO_COMPANY_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_BELONGS_TO_COMPANY_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_ADMIN` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_ADMIN_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_BRANCH_ID_MODIFIED             IS_BRANCH_ID_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_DELETED_RECORD` tinyint(4) DEFAULT NULL,
  `IS_EMAIL_ID_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_ERROR_RECORD` tinyint(4) DEFAULT NULL,
  `IS_FIRST_NAME_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_IN_APPEND_MODE` tinyint(4) DEFAULT NULL,
  `IS_LAST_NAME_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_LEGAL_DISCLAIMER_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_LICENSE_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_PHONE_NUMBER_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_REGION_ADMIN` tinyint(4) DEFAULT NULL,
  `IS_REGION_ADMIN_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_REGION_ID_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_SOURCE_BRANCH_ID_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_SOURCE_REGION_ID_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_SOURCE_USER_ID_GENERATED` tinyint(4) DEFAULT NULL,
  `IS_TITLE_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_USER_ADDED` tinyint(4) DEFAULT NULL,
  `IS_USER_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_USER_PHOTO_URL_MODIFIED` tinyint(4) DEFAULT NULL,
  `IS_USER_VERIFIED` tinyint(4) DEFAULT NULL,
  `IS_WARNING_RECORD` tinyint(4) DEFAULT NULL,
  `IS_WEBSITE_URL_MODIFIED` tinyint(4) DEFAULT NULL,
  `LAST_NAME` varchar(45) DEFAULT NULL,
  `LEGAL_DISCLAIMER` varchar(45) DEFAULT NULL,
  `PHONE_NUMBER` varchar(45) DEFAULT NULL,
  `ROW_NUM` int(11) DEFAULT NULL,
  `SEND_MAIL` tinyint(4) DEFAULT NULL,
  `SOURCE_USER_ID` varchar(45) DEFAULT NULL,
  `TITLE` varchar(45) DEFAULT NULL,
  `VALIDATION_ERRORS` text,
  `VALIDATION_WARNINGS` text,
  `WEBSITE_URL` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`UPLOAD_HIERARCHY_DETAILS_USERS_ID`),
  KEY `FX4_UPLOAD_HIERARCHY_DETAILS_ID_idx` (`UPLOAD_HIERARCHY_DETAILS_ID`),
  KEY `GX7_BRANCH_ID_idx` (`BRANCH_ID`),
  KEY `GX7_REGION_ID_idx` (`REGION_ID`),
  KEY `GX7_USER_ID_idx` (`USER_ID`),
  CONSTRAINT `FX4_UPLOAD_HIERARCHY_DETAILS_ID` FOREIGN KEY (`UPLOAD_HIERARCHY_DETAILS_ID`) REFERENCES `upload_hierarchy_details` (`UPLOAD_HIERARCHY_DETAILS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `GX7_BRANCH_ID` FOREIGN KEY (`BRANCH_ID`) REFERENCES `branch` (`BRANCH_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `GX7_REGION_ID` FOREIGN KEY (`REGION_ID`) REFERENCES `region` (`REGION_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `GX7_USER_ID` FOREIGN KEY (`USER_ID`) REFERENCES `users` (`USER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `upload_hierarchy_details_users_source_mapping`
--

DROP TABLE IF EXISTS `upload_hierarchy_details_users_source_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `upload_hierarchy_details_users_source_mapping` (
  `UPLOAD_HIERARCHY_DETAILS_USERS_SOURCE_MAPPING_ID` int(10) NOT NULL,
  `UPLOAD_HIERARCHY_DETAILS_ID` varchar(36) DEFAULT NULL,
  `GENERATED_UNIQUE_KEYS` int(10) DEFAULT NULL,
  PRIMARY KEY (`UPLOAD_HIERARCHY_DETAILS_USERS_SOURCE_MAPPING_ID`),
  KEY `FX5_UPLOAD_HIERARCHY_DETAILS_ID_idx` (`UPLOAD_HIERARCHY_DETAILS_ID`),
  CONSTRAINT `FX5_UPLOAD_HIERARCHY_DETAILS_ID` FOREIGN KEY (`UPLOAD_HIERARCHY_DETAILS_ID`) REFERENCES `upload_hierarchy_details` (`UPLOAD_HIERARCHY_DETAILS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `upload_status`
--

DROP TABLE IF EXISTS `upload_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `upload_status` (
  `UPLOAD_STATUS_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `ADMIN_USER_ID` int(11) NOT NULL,
  `MESSAGE` varchar(500) NOT NULL,
  `STATUS` int(1) NOT NULL,
  `UPLOAD_MODE` char(1) NOT NULL DEFAULT 'A',
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`UPLOAD_STATUS_ID`),
  KEY `fk_UPLOAD_STATUS_COMPANY1` (`COMPANY_ID`),
  CONSTRAINT `fk_UPLOAD_STATUS_COMPANY1` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `url_details`
--

DROP TABLE IF EXISTS `url_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `url_details` (
  `URL_DETAILS_ID` varchar(36) NOT NULL,
  `ACCESS_DATES` text,
  `CREATED_BY` varchar(45) DEFAULT NULL,
  `CREATED_ON` datetime DEFAULT NULL,
  `MODIFIED_BY` varchar(45) DEFAULT NULL,
  `MODIFIED_ON` datetime DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `URL` text,
  `URL_TYPE` text,
  PRIMARY KEY (`URL_DETAILS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_api_keys`
--

DROP TABLE IF EXISTS `user_api_keys`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_api_keys` (
  `USER_API_KEY_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `API_SECRET` varchar(250) NOT NULL,
  `API_KEY` varchar(512) NOT NULL,
  `COMPANY_ID` bigint(20) DEFAULT '-1',
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`USER_API_KEY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Holds user api keys';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_email_mapping`
--

DROP TABLE IF EXISTS `user_email_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_email_mapping` (
  `USER_EMAIL_MAPPING_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `USER_ID` int(10) unsigned NOT NULL,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `EMAIL_ID` varchar(250) NOT NULL,
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`USER_EMAIL_MAPPING_ID`),
  KEY `fk_USER_EMAIL_MAPPING_USER1_idx` (`USER_ID`),
  KEY `fk_USER_EMAIL_MAPPING_1` (`COMPANY_ID`),
  CONSTRAINT `fk_USER_EMAIL_MAPPING_1` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_USER_EMAIL_MAPPING_USER1` FOREIGN KEY (`USER_ID`) REFERENCES `users` (`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_invite`
--

DROP TABLE IF EXISTS `user_invite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_invite` (
  `USER_INVITE_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `PROFILE_MASTERS_ID` int(10) unsigned NOT NULL,
  `INVITATION_KEY` varchar(500) DEFAULT NULL,
  `INVITATION_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `INVITATION_EMAIL_ID` varchar(250) NOT NULL,
  `INVITATION_PARAMETERS` varchar(500) DEFAULT NULL,
  `INVITATION_VALID_UNTIL` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `INVITATION_SENT_BY` int(10) unsigned DEFAULT NULL,
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`USER_INVITE_ID`),
  KEY `fk_USER_INVITE_COMPANY1_idx` (`COMPANY_ID`),
  KEY `fk_USER_INVITE_PROFILES_MASTER1_idx` (`PROFILE_MASTERS_ID`),
  CONSTRAINT `fk_USER_INVITE_COMPANY1` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_USER_INVITE_PROFILES_MASTER1` FOREIGN KEY (`PROFILE_MASTERS_ID`) REFERENCES `profiles_master` (`PROFILE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Holds the user invitation record';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_profile`
--

DROP TABLE IF EXISTS `user_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_profile` (
  `USER_PROFILE_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `USER_ID` int(10) unsigned NOT NULL,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `EMAIL_ID` varchar(250) NOT NULL,
  `REGION_ID` int(11) NOT NULL,
  `BRANCH_ID` int(11) NOT NULL,
  `AGENT_ID` int(11) NOT NULL,
  `PROFILES_MASTER_ID` int(10) unsigned NOT NULL,
  `USER_PROFILE_TYPE` varchar(3) DEFAULT NULL COMMENT 'Mostly used to differentiate agents. In case, if the agent is a loan officer or realtor',
  `PROFILE_COMPLETION_STAGE` varchar(45) DEFAULT NULL,
  `IS_PROFILE_COMPLETE` int(1) NOT NULL,
  `IS_PRIMARY` int(1) NOT NULL DEFAULT '0',
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`USER_PROFILE_ID`),
  KEY `fk_USER_PROFILE_PROFILES_MASTER1_idx` (`PROFILES_MASTER_ID`),
  KEY `fk_USER_PROFILE_COMPANY1_idx` (`COMPANY_ID`),
  KEY `fk_USER_PROFILE_USERS1_idx` (`USER_ID`),
  CONSTRAINT `fk_USER_PROFILE_COMPANY1` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_USER_PROFILE_PROFILES_MASTER1` FOREIGN KEY (`PROFILES_MASTER_ID`) REFERENCES `profiles_master` (`PROFILE_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_USER_PROFILE_USERS1` FOREIGN KEY (`USER_ID`) REFERENCES `users` (`USER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Holds the details of profile for the company. A row is entered for an association with an organisation level.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_referral_mapping`
--

DROP TABLE IF EXISTS `user_referral_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_referral_mapping` (
  `USER_REFERRAL_MAPPING_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `REFERRAL_INVITATION_ID` int(10) unsigned NOT NULL,
  `USER_ID` int(10) unsigned NOT NULL,
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(50) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`USER_REFERRAL_MAPPING_ID`),
  KEY `fk_ss_user.USER_REFERRAL_MAPPING_REFERRAL_INVITATION` (`REFERRAL_INVITATION_ID`),
  KEY `fk_ss_user.USER_REFERRAL_MAPPING_USERS` (`USER_ID`),
  CONSTRAINT `fk_ss_user.USER_REFERRAL_MAPPING_REFERRAL_INVITATION` FOREIGN KEY (`REFERRAL_INVITATION_ID`) REFERENCES `referral_invitation` (`REFERRAL_INVITATION_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_ss_user.USER_REFERRAL_MAPPING_USERS` FOREIGN KEY (`USER_ID`) REFERENCES `users` (`USER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usercount_modification_notification`
--

DROP TABLE IF EXISTS `usercount_modification_notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usercount_modification_notification` (
  `USERCOUNT_MODIFICATION_NOTIFICATION_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`USERCOUNT_MODIFICATION_NOTIFICATION_ID`),
  KEY `fk_USERCOUNT_MODIFICATION_NOTIFICATION_COMPANY1_idx` (`COMPANY_ID`),
  CONSTRAINT `fk_USERCOUNT_MODIFICATION_NOTIFICATION_COMPANY1` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Holds records if any active user has been added or deleted from a company';
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
) ENGINE=InnoDB AUTO_INCREMENT=37335 DEFAULT CHARSET=utf8 COMMENT='Holds the user details. A user can have multiple profiles as mapped with user profile table';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vendasta_rm_settings`
--

DROP TABLE IF EXISTS `vendasta_rm_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vendasta_rm_settings` (
  `VENDASTA_RM_SETTINGS_ID` int(11) NOT NULL,
  `COMPANY_SETTINGS_ID` varchar(36) DEFAULT NULL,
  `ACCOUNT_ID` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`VENDASTA_RM_SETTINGS_ID`),
  KEY `FX5_COMPANY_SETTINGS_ID_idx` (`COMPANY_SETTINGS_ID`),
  CONSTRAINT `FX5_COMPANY_SETTINGS_ID` FOREIGN KEY (`COMPANY_SETTINGS_ID`) REFERENCES `company_settings` (`COMPANY_SETTINGS_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vendasta_sso_ticket`
--

DROP TABLE IF EXISTS `vendasta_sso_ticket`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vendasta_sso_ticket` (
  `VENDASTA_SSO_TICKET_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `VENDASTA_SSO_TICKET` varchar(32) NOT NULL,
  `PRODUCT_ID` varchar(2) NOT NULL,
  `VENDASTA_SSO_TOKEN` varchar(32) NOT NULL,
  `STATUS` tinyint(4) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(20) NOT NULL,
  `MODIFIED_BY` varchar(20) NOT NULL,
  PRIMARY KEY (`VENDASTA_SSO_TICKET_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='vendasta single use short lived ticket';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vertical_crm_mapping`
--

DROP TABLE IF EXISTS `vertical_crm_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vertical_crm_mapping` (
  `VERTICAL_CRM_MAPPING_ID` int(11) NOT NULL,
  `VERTICAL_ID` int(11) NOT NULL,
  `CRM_ID` int(11) NOT NULL,
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`VERTICAL_CRM_MAPPING_ID`),
  UNIQUE KEY `uk_vertical_crm_mapping` (`VERTICAL_ID`,`CRM_ID`,`STATUS`),
  KEY `VERTICAL_ID_idx` (`VERTICAL_ID`),
  KEY `CRM_ID_idx` (`CRM_ID`),
  CONSTRAINT `CRM_ID` FOREIGN KEY (`CRM_ID`) REFERENCES `crm_master` (`CRM_MASTER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `VERTICAL_ID` FOREIGN KEY (`VERTICAL_ID`) REFERENCES `verticals_master` (`VERTICALS_MASTER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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

--
-- Table structure for table `web_addresses`
--

DROP TABLE IF EXISTS `web_addresses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_addresses` (
  `WEB_ADDRESSES_ID` int(11) NOT NULL,
  `CONTACT_DETAILS _ID` int(11) DEFAULT NULL,
  `WORK` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`WEB_ADDRESSES_ID`),
  KEY `CONTACT_DETAILS _ID_idx` (`CONTACT_DETAILS _ID`),
  CONSTRAINT `CONTACT_DETAILS _ID` FOREIGN KEY (`CONTACT_DETAILS _ID`) REFERENCES `contact_details` (`CONTACT_DETAILS _ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `yelp_token`
--

DROP TABLE IF EXISTS `yelp_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `yelp_token` (
  `YELP_TOKEN_ID` varchar(45) NOT NULL,
  `YELP_PAGE_LINK` text,
  `type` int(11) NOT NULL,
  `id` int(10) NOT NULL,
  PRIMARY KEY (`type`,`id`),
  UNIQUE KEY `YELP_TOKEN_ID_UNIQUE` (`YELP_TOKEN_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `zillow_call_count`
--

DROP TABLE IF EXISTS `zillow_call_count`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zillow_call_count` (
  `ZILLOW_CALL_COUNT_ID` varchar(36) NOT NULL,
  `COUNT` int(11) DEFAULT NULL,
  PRIMARY KEY (`ZILLOW_CALL_COUNT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `zillow_temp_post`
--

DROP TABLE IF EXISTS `zillow_temp_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zillow_temp_post` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `ENTITY_COLUMN_NAME` varchar(50) NOT NULL,
  `ENTITY_ID` int(11) NOT NULL,
  `ZILLOW_REVIEW_URL` varchar(100) NOT NULL,
  `ZILLOW_REVIEW_SOURCE_LINK` varchar(100) NOT NULL,
  `ZILLOW_REVIEW_RATING` double NOT NULL DEFAULT '0',
  `ZILLOW_REVIEWER_NAME` varchar(50) NOT NULL,
  `ZILLOW_REVIEW_SUMMARY` varchar(200) NOT NULL,
  `ZILLOW_REVIEW_DESCRIPTION` varchar(2000) NOT NULL,
  `ZILLOW_REVIEW_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `ZILLOW_SURVEY_ID` varchar(30) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `zillow_token`
--

DROP TABLE IF EXISTS `zillow_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zillow_token` (
  `ZILLOW_TOKEN_ID` varchar(45) NOT NULL,
  `ZILLOW_LENDER_ID` varchar(45) DEFAULT NULL,
  `ZILLOW_PROFILE_LINK` text,
  `ZILLOW_SCREEN_NAME` varchar(45) DEFAULT NULL,
  `type` int(11) NOT NULL,
  `id` int(10) NOT NULL,
  PRIMARY KEY (`type`,`id`),
  UNIQUE KEY `ZILLOW_TOKEN_ID_UNIQUE` (`ZILLOW_TOKEN_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `zipcodelookup`
--

DROP TABLE IF EXISTS `zipcodelookup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zipcodelookup` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `state_id` int(11) DEFAULT NULL,
  `zipcode` varchar(45) DEFAULT NULL,
  `countyname` varchar(45) DEFAULT NULL,
  `cityname` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_state_lookup_idx` (`state_id`),
  CONSTRAINT `fk_state_lookup` FOREIGN KEY (`state_id`) REFERENCES `statelookup` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping events for database 'ss_centralized_mongodb'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-06-02 19:59:46
