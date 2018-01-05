ALTER TABLE `ss_centralized_mongodb`.`overview_branch_month` ADD COLUMN `user_count` INT(10) NULL DEFAULT 0 AFTER `year`;
ALTER TABLE `ss_centralized_mongodb`.`overview_branch_month` ADD COLUMN `cumulative_user_count` INT(10) NULL DEFAULT 0 AFTER `user_count`;
ALTER TABLE `ss_centralized_mongodb`.`overview_region_month` ADD COLUMN `user_count` INT(10) NULL DEFAULT 0 AFTER `year`;
ALTER TABLE `ss_centralized_mongodb`.`overview_region_month` ADD COLUMN `cumulative_user_count` INT(10) NULL DEFAULT 0 AFTER `user_count`;
ALTER TABLE `ss_centralized_mongodb`.`overview_company_month` ADD COLUMN `user_count` INT(10) NULL DEFAULT 0 AFTER `year`;
ALTER TABLE `ss_centralized_mongodb`.`overview_company_month` ADD COLUMN `cumulative_user_count` INT(10) NULL DEFAULT 0 AFTER `user_count`;