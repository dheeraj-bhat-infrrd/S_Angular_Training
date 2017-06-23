CREATE DATABASE  IF NOT EXISTS `ss_centralized_mongodb` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `ss_centralized_mongodb`;
-- MySQL dump 10.13  Distrib 5.7.12, for Win64 (x86_64)
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
-- Table structure for table `overview_branch`
--

DROP TABLE IF EXISTS `overview_branch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `overview_branch` (
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
  PRIMARY KEY (`overview_branch_id`),
  KEY `FX_OVERVIEW_BRANCH_idx` (`branch_id`),
  CONSTRAINT `FX_OVERVIEW_BRANCH` FOREIGN KEY (`branch_id`) REFERENCES `branch` (`BRANCH_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `overview_branch`
--

LOCK TABLES `overview_branch` WRITE;
/*!40000 ALTER TABLE `overview_branch` DISABLE KEYS */;
/*!40000 ALTER TABLE `overview_branch` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `overview_company`
--

DROP TABLE IF EXISTS `overview_company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `overview_company` (
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
  PRIMARY KEY (`overview_company_id`),
  KEY `FX_OVERVIEW_COMPANY_idx` (`company_id`),
  CONSTRAINT `FX_OVERVIEW_COMPANY` FOREIGN KEY (`company_id`) REFERENCES `company` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `overview_company`
--

LOCK TABLES `overview_company` WRITE;
/*!40000 ALTER TABLE `overview_company` DISABLE KEYS */;
/*!40000 ALTER TABLE `overview_company` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `overview_region`
--

DROP TABLE IF EXISTS `overview_region`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `overview_region` (
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
  PRIMARY KEY (`overview_region_id`),
  KEY `FX_OVERVIEW_REGION_idx` (`region_id`),
  CONSTRAINT `FX_OVERVIEW_REGION` FOREIGN KEY (`region_id`) REFERENCES `region` (`REGION_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `overview_region`
--

LOCK TABLES `overview_region` WRITE;
/*!40000 ALTER TABLE `overview_region` DISABLE KEYS */;
/*!40000 ALTER TABLE `overview_region` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `overview_user`
--

DROP TABLE IF EXISTS `overview_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `overview_user` (
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
  PRIMARY KEY (`overview_user_id`),
  KEY `FX_OVERVIEW_USER_idx` (`user_id`),
  CONSTRAINT `FX_OVERVIEW_USER` FOREIGN KEY (`user_id`) REFERENCES `users` (`USER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `overview_user`
--

LOCK TABLES `overview_user` WRITE;
/*!40000 ALTER TABLE `overview_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `overview_user` ENABLE KEYS */;
UNLOCK TABLES;



-- Dump completed on 2017-06-21 11:44:52
