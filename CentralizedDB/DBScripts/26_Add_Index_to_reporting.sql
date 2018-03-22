#Score Stats Indexes for ETL
ALTER TABLE `score_stats_question_user` ADD INDEX `index2` USING BTREE (`user_id` ASC, `month_val` ASC, `year_val` ASC, `question_id` ASC);

ALTER TABLE `score_stats_question_branch` CHANGE COLUMN `branch_id` `branch_id` INT(11) NOT NULL DEFAULT 0 ,
ADD INDEX `ETL_index` (`branch_id` ASC, `month_val` ASC, `year_val` ASC, `question_id` ASC);

ALTER TABLE `score_stats_question_company` 
ADD INDEX `ETL_Index` (`company_id` ASC, `month_val` ASC, `year_val` ASC, `question_id` ASC);

ALTER TABLE `score_stats_question_region` 
ADD INDEX `ETL_Index` (`region_id` ASC, `month_val` ASC, `year_val` ASC, `question_id` ASC);

ALTER TABLE `score_stats_overall_user` 
ADD INDEX `ETL_Index` USING BTREE (`user_id` ASC, `month_val` ASC, `year_val` ASC);

ALTER TABLE `score_stats_overall_company` 
ADD INDEX `ETL_Index` (`company_id` ASC, `month_val` ASC, `year_val` ASC);

ALTER TABLE `score_stats_overall_region` 
ADD INDEX `ETL_Index` (`region_id` ASC, `month_val` ASC, `year_val` ASC);

ALTER TABLE `score_stats_overall_branch` 
ADD INDEX `ETL_Index` (`branch_id` ASC, `month_val` ASC, `year_val` ASC);
