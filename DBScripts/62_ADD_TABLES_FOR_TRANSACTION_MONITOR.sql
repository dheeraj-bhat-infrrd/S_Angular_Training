-- MySQL dump 10.13  Distrib 5.7.17, for Linux (x86_64)
--
-- Host: localhost    Database: ss_report
-- ------------------------------------------------------
-- Server version	5.7.17

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
-- Table structure for table `company_avtive_users`
--

use 

DROP TABLE IF EXISTS `company_avtive_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company_avtive_users` (
  `company_avtive_users_id` varchar(45) NOT NULL,
  `company_id` int(11) unsigned DEFAULT NULL,
  `date_val` date DEFAULT NULL,
  `active_users` int(11) DEFAULT NULL,
  PRIMARY KEY (`company_avtive_users_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company_avtive_users`
--

LOCK TABLES `company_avtive_users` WRITE;
/*!40000 ALTER TABLE `company_avtive_users` DISABLE KEYS */;
/*!40000 ALTER TABLE `company_avtive_users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `company_source_count`
--

DROP TABLE IF EXISTS `company_source_count`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company_source_count` (
  `company_source_count_id` varchar(45) NOT NULL,
  `company_id` int(11) unsigned DEFAULT NULL,
  `date_val` date DEFAULT NULL,
  `API` int(11) DEFAULT NULL,
  `LONEWOLF` int(11) DEFAULT NULL,
  `FTP` int(11) DEFAULT NULL,
  `DOTLOOP` int(11) DEFAULT NULL,
  `encompass` int(11) DEFAULT NULL,
  `CSV_UPLOAD` int(11) DEFAULT NULL,
  `total` int(11) DEFAULT NULL,
  PRIMARY KEY (`company_source_count_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company_source_count`
--

LOCK TABLES `company_source_count` WRITE;
/*!40000 ALTER TABLE `company_source_count` DISABLE KEYS */;
INSERT INTO `company_source_count` VALUES ('3d8d30c5-9c41-11e7-b43b-d981e33dc632',0,'2017-09-18',1,0,0,0,0,0,NULL),('3d8d7ee6-9c41-11e7-b43b-d981e33dc632',7,'2017-09-18',0,1453,0,0,0,0,1460),('3d8dcd08-9c41-11e7-b43b-d981e33dc632',1246,'2017-09-18',7,0,0,0,0,0,7),('3d8e423b-9c41-11e7-b43b-d981e33dc632',1225,'2017-09-18',0,0,0,0,0,0,0),('3d8e694c-9c41-11e7-b43b-d981e33dc632',1241,'2017-09-18',0,0,0,0,0,0,0),('3d8e6951-9c41-11e7-b43b-d981e33dc632',1236,'2017-09-18',0,0,0,0,0,0,0),('3d8e69we-9c41-11e7-b43b-d981e33dc632',1246,'2017-09-19',0,4,0,0,0,0,4);
/*!40000 ALTER TABLE `company_source_count` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `company_status_count`
--

DROP TABLE IF EXISTS `company_status_count`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company_status_count` (
  `company_status_count_id` varchar(45) NOT NULL,
  `company_id` int(11) unsigned DEFAULT NULL,
  `date_val` date DEFAULT NULL,
  `sent` int(11) DEFAULT NULL,
  `received` int(11) DEFAULT NULL,
  `completed` int(11) DEFAULT NULL,
  `reminder_sent` int(11) DEFAULT NULL,
  `corrupted` int(11) DEFAULT NULL,
  `duplicate` int(11) DEFAULT NULL,
  `old_record` int(11) DEFAULT NULL,
  `ignored` int(11) DEFAULT NULL,
  `mismatched` int(11) DEFAULT NULL,
  `not_allowed` int(11) DEFAULT NULL,
  PRIMARY KEY (`company_status_count_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company_status_count`
--

LOCK TABLES `company_status_count` WRITE;
/*!40000 ALTER TABLE `company_status_count` DISABLE KEYS */;
INSERT INTO `company_status_count` VALUES ('105d3138-9c57-11e7-b43b-d981e33dc632',0,'2017-09-18',0,1,0,0,0,0,1,0,0,0),('105d5849-9c57-11e7-b43b-d981e33dc632',7,'2017-09-18',5,1460,0,0,0,0,653,0,797,0),('105d7f5a-9c57-11e7-b43b-d981e33dc632',1225,'2017-09-18',2,2,0,0,0,0,0,0,0,0),('105da66b-9c57-11e7-b43b-d981e33dc632',1236,'2017-09-18',13,14,0,0,0,0,1,0,0,0),('105df48c-9c57-11e7-b43b-d981e33dc632',1241,'2017-09-18',2,2,0,0,0,0,0,0,0,0),('10cfc92d-9c57-11e7-b43b-d981e33dc632',1246,'2017-09-18',19,42,17,19,0,6,2,0,0,0);
/*!40000 ALTER TABLE `company_status_count` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-10-06 15:09:12
