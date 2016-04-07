-- -----------------------------------------------------
-- Table `ss_user`.`CRM_BATCH_RECORDS_History`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ss_user`.`CRM_BATCH_TRACKER_HISTORY` (
  `HISTORY_ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `CRM_BATCH_TRACKER_ID` int(11)  NOT NULL,
  `STATUS` INT(1) NOT NULL,
  `COUNT_OF_RECORDS_FETCHED` INT DEFAULT 0,
  `CREATED_ON` TIMESTAMP NOT NULL Default CURRENT_TIMESTAMP,
  `CREATED_BY` VARCHAR(45) NOT NULL,
  `MODIFIED_ON` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MODIFIED_BY` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`HISTORY_ID`),
  CONSTRAINT `fk_History_Crm_batch_tracker`
    FOREIGN KEY (`CRM_BATCH_TRACKER_ID`)
    REFERENCES `ss_user`.`crm_batch_tracker` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = 'History details of no of records fetched each time when batch runs.' ;
