use ss_user;
-- -----------------------------------------------------
-- Table `ss_user`.`COMPANY_HIDDEN_NOTIFICATION`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ss_user`.`COMPANY_HIDDEN_NOTIFICATION` (
  `COMPANY_HIDDEN_NOTIFICATION_ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `COMPANY_ID` INT UNSIGNED NOT NULL,
  `STATUS` INT(1) NOT NULL,
  `HIDDEN` BOOLEAN NOT NULL,
  `CREATED_ON` TIMESTAMP NOT NULL Default CURRENT_TIMESTAMP,
  `MODIFIED_ON` TIMESTAMP NOT NULL Default CURRENT_TIMESTAMP,
  PRIMARY KEY (`COMPANY_HIDDEN_NOTIFICATION_ID`),
  INDEX `fk_Company_Hidden_Notification_idx` (`COMPANY_ID` ASC),
  CONSTRAINT `fk_Company_Hidden_Notification` 
 		FOREIGN KEY (`COMPANY_ID`)
		REFERENCES `ss_user`.`COMPANY` (`COMPANY_ID`)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = 'Update hiddenFromSearchResults Field each time when batch runs.' ;





