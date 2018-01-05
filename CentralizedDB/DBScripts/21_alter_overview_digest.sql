ALTER TABLE `overview_branch_month` ADD COLUMN `user_count` INT(10) NULL DEFAULT 0 AFTER `year`;
ALTER TABLE `overview_branch_month` ADD COLUMN `cumulative_user_count` INT(10) NULL DEFAULT 0 AFTER `user_count`;
ALTER TABLE `overview_region_month` ADD COLUMN `user_count` INT(10) NULL DEFAULT 0 AFTER `year`;
ALTER TABLE `overview_region_month` ADD COLUMN `cumulative_user_count` INT(10) NULL DEFAULT 0 AFTER `user_count`;
ALTER TABLE `overview_company_month` ADD COLUMN `user_count` INT(10) NULL DEFAULT 0 AFTER `year`;
ALTER TABLE `overview_company_month` ADD COLUMN `cumulative_user_count` INT(10) NULL DEFAULT 0 AFTER `user_count`;
