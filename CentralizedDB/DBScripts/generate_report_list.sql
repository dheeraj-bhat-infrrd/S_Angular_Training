
--
-- Table structure for table `generate_report_list`
--
DROP TABLE IF EXISTS `generate_report_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ss_centralized_mongodb`.`generate_report_list` (
  `generate_report_list_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `current_date` DATETIME NULL,
  `report_name` VARCHAR(45) NULL,
  `start_date` DATE NULL,
  `end_date` DATE NULL,
  `first_name` VARCHAR(45) NULL,
  `last_name` VARCHAR(45) NULL,
  `entity_id` INT(10) NULL,
  `entity_type` VARCHAR(45) NULL,
  `status` INT NULL,
  PRIMARY KEY (`generate_report_list_id`));
  
--
-- Dumping data for table `generate_report_list`
--

LOCK TABLES `generate_report_list` WRITE;
/*!40000 ALTER TABLE `generate_report_list` DISABLE KEYS */;
/*!40000 ALTER TABLE `generate_report_list` ENABLE KEYS */;
UNLOCK TABLES;