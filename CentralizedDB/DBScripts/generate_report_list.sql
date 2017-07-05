
--
-- Table structure for table `generate_report_list`
--
DROP TABLE IF EXISTS `generate_report_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ss_centralized_mongodb`.`generate_report_list` (
  `generate_report_list_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `created_on` timestamp NULL DEFAULT NULL,
  `report_name` varchar(45) DEFAULT NULL,
  `start_date` timestamp NULL DEFAULT NULL,
  `end_date` timestamp NULL DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `entity_id` int(10) DEFAULT NULL,
  `entity_type` varchar(45) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`generate_report_list_id`));
  
--
-- Dumping data for table `generate_report_list`
--

LOCK TABLES `generate_report_list` WRITE;
/*!40000 ALTER TABLE `generate_report_list` DISABLE KEYS */;
/*!40000 ALTER TABLE `generate_report_list` ENABLE KEYS */;
UNLOCK TABLES;