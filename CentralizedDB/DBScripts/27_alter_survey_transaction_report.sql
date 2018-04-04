ALTER TABLE `survey_transaction_report` 
DROP COLUMN `transaction_unassigned`,
DROP COLUMN `transaction_mismatched`,
CHANGE COLUMN `survey_transaction_report_id` `survey_transaction_report_id` INT(11) NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN `transaction_completed_` `transaction_completed` INT(11) NULL DEFAULT NULL,
ADD COLUMN email_id VARCHAR(450) NULL ;

ALTER TABLE `survey_transaction_report_branch` 
DROP COLUMN `transaction_unassigned`,
DROP COLUMN `transaction_mismatched`,
ADD COLUMN `email_id` VARCHAR(45) NULL AFTER `transaction_duplicates`,
CHANGE COLUMN `transaction_completed_` `transaction_completed` INT(11) NULL DEFAULT NULL;

ALTER TABLE `ss_centralized_mongodb`.`survey_transaction_report_region` 
DROP COLUMN `transaction_unassigned`,
DROP COLUMN `transaction_mismatched`,
ADD COLUMN `email_id` VARCHAR(45) NULL AFTER `transaction_duplicates`,
CHANGE COLUMN `transaction_completed_` `transaction_completed` INT(11) NULL DEFAULT NULL;
