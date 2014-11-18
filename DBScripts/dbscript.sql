-- DBScripts for the database

CREATE DATABASE  IF NOT EXISTS `ss_user` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `ss_user`;

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
-- Table structure for table `accounts_master`
--

DROP TABLE IF EXISTS `accounts_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `accounts_master` (
  `ACCOUNTS_MASTER_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ACCOUNT_NAME` varchar(45) NOT NULL,
  `MAX_USERS_ALLOWED` int(11) NOT NULL,
  `MAX_TIME_VALIDITY_ALLOWED_IN_DAYS` int(11) NOT NULL,
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`ACCOUNTS_MASTER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Holds the details of accounts possible in the application.';
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
  `IS_DEFAULT_BY_SYSTEM` int(1) NOT NULL COMMENT 'In case, the company does not have this profile, a default will be created by the system',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Branch details under a region. In case, there are no branches under a region, a default row will be added.';
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
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`COMPANY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Holds the company meta data';
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
-- Table structure for table `license_details`
--

DROP TABLE IF EXISTS `license_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `license_details` (
  `LICENSE_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ACCOUNTS_MASTER_ID` int(10) unsigned NOT NULL,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `LICENSE_START_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `LICENSE_END_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `PAYMENT_MODE` char(1) NOT NULL COMMENT 'M for manual, A for auto',
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`LICENSE_ID`),
  KEY `fk_LICENCE_DETAILS_ACCOUNTS_MASTER1_idx` (`ACCOUNTS_MASTER_ID`),
  KEY `fk_LICENCE_DETAILS_COMPANY1_idx` (`COMPANY_ID`),
  CONSTRAINT `fk_LICENCE_DETAILS_ACCOUNTS_MASTER1` FOREIGN KEY (`ACCOUNTS_MASTER_ID`) REFERENCES `accounts_master` (`ACCOUNTS_MASTER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_LICENCE_DETAILS_COMPANY1` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Holds the license details for a company';
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Available profiles in the application.';
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
  `IS_DEFAULT_BY_SYSTEM` int(1) NOT NULL COMMENT 'In case, the company does not have this profile, a default will be created by the system',
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`REGION_ID`),
  KEY `fk_REGION_COMPANY1_idx` (`COMPANY_ID`),
  CONSTRAINT `fk_REGION_COMPANY1` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Region details of a company. In case, if the admin decides there is no region for the company, then a default row will be added.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `survey`
--

DROP TABLE IF EXISTS `survey`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `survey` (
  `SURVEY_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`SURVEY_ID`),
  KEY `fk_SURVEY_COMPANY1_idx` (`COMPANY_ID`),
  CONSTRAINT `fk_SURVEY_COMPANY1` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
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
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `SURVEY_ID` int(10) unsigned NOT NULL,
  `SURVEY_QUESTIONS_CODE` varchar(10) NOT NULL COMMENT 'Pre defined code for survey questions. Determines the type of questions',
  `SURVEY_QUESTION` varchar(500) NOT NULL,
  `ORDER` int(11) NOT NULL,
  `IS_RATING_QUESTION` int(1) NOT NULL COMMENT '0 for no, 1 for yes',
  `STATUS` int(1) NOT NULL,
  `CREATED_ON` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`SURVEY_QUESTIONS_ID`),
  KEY `fk_SURVEY_QUESTIONS_COMPANY1_idx` (`COMPANY_ID`),
  KEY `fk_SURVEY_QUESTIONS_SURVEY1_idx` (`SURVEY_ID`),
  CONSTRAINT `fk_SURVEY_QUESTIONS_COMPANY1` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_SURVEY_QUESTIONS_SURVEY1` FOREIGN KEY (`SURVEY_ID`) REFERENCES `survey` (`SURVEY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
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
-- Table structure for table `user_invite`
--

DROP TABLE IF EXISTS `user_invite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_invite` (
  `USER_INVITE_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `PROFILE_MASTERS_ID` int(10) unsigned NOT NULL,
  `INVITATION_KEY` varchar(500) NOT NULL,
  `INVITATION_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `INVITATION_EMAIL_ID` varchar(250) NOT NULL,
  `INVITATION_VALID_UNTIL` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `INVITATION_SENT_BY` int(10) unsigned NOT NULL,
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
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `USER_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `LOGIN_NAME` varchar(45) NOT NULL,
  `LOGIN_PASSWORD` varchar(75) NOT NULL,
  `EMAIL_ID` varchar(250) DEFAULT NULL,
  `DISPLAY_NAME` varchar(75) DEFAULT NULL,
  `SOURCE` varchar(2) NOT NULL COMMENT 'Source of record',
  `SOURCE_USER_ID` int(11) DEFAULT NULL,
  `STATUS` int(1) NOT NULL,
  `LAST_LOGIN` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CREATED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `CREATED_BY` varchar(45) NOT NULL,
  `MODIFIED_ON` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` varchar(45) NOT NULL,
  PRIMARY KEY (`USER_ID`),
  KEY `fk_USERS_COMPANY_idx` (`COMPANY_ID`),
  CONSTRAINT `fk_USERS_COMPANY` FOREIGN KEY (`COMPANY_ID`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Holds the user details. A user can have multiple profiles as mapped with user profile table';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-11-18 20:43:37
