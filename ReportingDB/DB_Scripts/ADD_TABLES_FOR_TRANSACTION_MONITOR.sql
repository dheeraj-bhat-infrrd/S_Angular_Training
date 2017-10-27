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


/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-10-06 15:09:12
