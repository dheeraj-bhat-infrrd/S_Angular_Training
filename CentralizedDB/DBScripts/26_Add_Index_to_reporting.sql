#Score Stats Indexes for ETL
ALTER TABLE `score_stats_question_user` ADD INDEX `ETL_Index` USING BTREE (`user_id` ASC, `month_val` ASC, `year_val` ASC, `question_id` ASC);

ALTER TABLE `score_stats_question_branch` CHANGE COLUMN `branch_id` `branch_id` INT(11) NOT NULL DEFAULT 0 ,ADD INDEX `ETL_index` (`branch_id` ASC, `month_val` ASC, `year_val` ASC, `question_id` ASC);

ALTER TABLE `score_stats_question_company` ADD INDEX `ETL_Index` (`company_id` ASC, `month_val` ASC, `year_val` ASC, `question_id` ASC);

ALTER TABLE `score_stats_question_region` ADD INDEX `ETL_Index` (`region_id` ASC, `month_val` ASC, `year_val` ASC, `question_id` ASC);

ALTER TABLE `score_stats_overall_user` ADD INDEX `ETL_Index` USING BTREE (`user_id` ASC, `month_val` ASC, `year_val` ASC);

ALTER TABLE `score_stats_overall_company` ADD INDEX `ETL_Index` USING BTREE (`company_id` ASC, `month_val` ASC, `year_val` ASC);

ALTER TABLE `score_stats_overall_region` ADD INDEX `ETL_Index` USING BTREE (`region_id` ASC, `month_val` ASC, `year_val` ASC);

ALTER TABLE `score_stats_overall_branch` ADD INDEX `ETL_Index` USING BTREE (`branch_id` ASC, `month_val` ASC, `year_val` ASC);

# Overview Indexes for ETL
ALTER TABLE `overview_branch_month` ADD INDEX `ETL_Index` USING BTREE (`branch_id` ASC, `month` ASC, `year` ASC);

ALTER TABLE `overview_branch` CHANGE COLUMN `branch_id` `branch_id` INT(10) UNSIGNED NOT NULL DEFAULT 0 ,ADD INDEX `ETL_Index` USING BTREE (`branch_id` ASC);

ALTER TABLE `overview_branch_year` ADD INDEX `ETL_Index` USING BTREE (`branch_id` ASC, `year` ASC);

ALTER TABLE `overview_company` ADD INDEX `ETL_Index` USING BTREE (`company_id` ASC);

ALTER TABLE `overview_company_month` ADD INDEX `ETL_Index` USING BTREE (`company_id` ASC, `month` ASC, `year` ASC);

ALTER TABLE `overview_company_year` ADD INDEX `ETL_Index` USING BTREE (`company_id` ASC, `year` ASC);

ALTER TABLE `overview_region` ADD INDEX `ETL_Index` USING BTREE (`region_id` ASC);

ALTER TABLE `overview_region_month` ADD INDEX `ETL_Index` USING BTREE (`region_id` ASC, `month` ASC, `year` ASC);

ALTER TABLE `overview_region_year` ADD INDEX `ETL_Index` USING BTREE (`region_id` ASC, `year` ASC);

ALTER TABLE `overview_user` ADD INDEX `ETL_Index` USING BTREE (`user_id` ASC);

ALTER TABLE `overview_user_month` ADD INDEX `ETL_Index` USING BTREE (`user_id` ASC, `month` ASC, `year` ASC);

ALTER TABLE `overview_user_year` CHANGE COLUMN `user_id` `user_id` INT(11) NOT NULL DEFAULT 0,
ADD INDEX `ETL_Index` USING BTREE (`user_id` ASC, `year` ASC);

#Survey transaction report indexes.
ALTER TABLE `survey_transaction_report` DROP INDEX `UserIdYearMonth` ,ADD UNIQUE INDEX `UserIdYearMonth` USING BTREE (`month` ASC, `year` ASC, `user_id` ASC);

ALTER TABLE `survey_transaction_report_region` DROP INDEX `UserIdYearMonthRegion` ,ADD UNIQUE INDEX `UserIdYearMonthRegion` USING BTREE (`user_id` ASC, `region_id` ASC, `month` ASC, `year` ASC);

ALTER TABLE `survey_transaction_report_branch` DROP INDEX `userIdMonthYearBranch` ,ADD UNIQUE INDEX `userIdMonthYearBranch` USING BTREE (`user_id` ASC, `branch_id` ASC, `month` ASC, `year` ASC);

