alter table survey_response modify `ANSWER` varchar(800) NULL;
alter table survey_response modify `QUESTION_ID` int(10) NULL;
ALTER TABLE `survey_response` ADD COLUMN `IS_NPS_QUESTION` TINYINT NOT NULL;
ALTER TABLE `survey_response` ADD COLUMN `CONSIDER_FOR_SCORE` tinyint(4) NOT NULL;