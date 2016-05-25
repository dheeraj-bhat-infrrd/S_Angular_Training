ALTER TABLE `ss_user`.`VERTICALS_MASTER` 
ADD COLUMN `PRIORITY_ORDER` INT NOT NULL DEFAULT 0;

UPDATE `ss_user`.`VERTICALS_MASTER` SET `PRIORITY_ORDER`='1' WHERE `VERTICALS_MASTER_ID`='1';
UPDATE `ss_user`.`VERTICALS_MASTER` SET `PRIORITY_ORDER`='2' WHERE `VERTICALS_MASTER_ID`='2';
UPDATE `ss_user`.`VERTICALS_MASTER` SET `PRIORITY_ORDER`='3' WHERE `VERTICALS_MASTER_ID`='52';
UPDATE `ss_user`.`VERTICALS_MASTER` SET `PRIORITY_ORDER`='4' WHERE `VERTICALS_MASTER_ID`='29';
UPDATE `ss_user`.`VERTICALS_MASTER` SET `PRIORITY_ORDER`='5' WHERE `VERTICALS_MASTER_ID`='51';


ALTER TABLE `ss_user`.`USERS` 
ADD COLUMN `REGISTRATION_STAGE` VARCHAR(45) DEFAULT "NOT_STARTED",
ADD COLUMN `FORCE_PASSWORD` INT NOT NULL DEFAULT 0 ;






