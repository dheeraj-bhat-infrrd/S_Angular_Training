--
-- Table structure for table `SURVEY_0TO10_QUESTIONS`
--
use ss_user;
DROP TABLE IF EXISTS `SURVEY_0TO10_QUESTIONS`;

CREATE TABLE `SURVEY_0TO10_QUESTIONS` (
  `SURVEY_0TO10_ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `SURVEY_QUESTIONS_ID` INT UNSIGNED NOT NULL,
  `IS_NPS_QUESTION` TINYINT NOT NULL,
  `CONSIDER_FOR_SCORE` TINYINT NOT NULL,
  `NOT_AT_ALL_LIKELY` VARCHAR(100) DEFAULT "NOT_AT_ALL_LIKELY",
  `VERY_LIKELY` VARCHAR(100) DEFAULT "VERY_LIKELY",
  `STATUS` TINYINT NOT NULL,
  `CREATED_ON` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `MODIFIED_ON` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `CREATED_BY` VARCHAR(20) NOT NULL,
  `MODIFIED_BY` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`SURVEY_0TO10_ID`),
  UNIQUE KEY (`SURVEY_QUESTIONS_ID`),
  FOREIGN KEY (`SURVEY_QUESTIONS_ID`) references SURVEY_QUESTIONS (`SURVEY_QUESTIONS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;