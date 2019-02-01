ALTER TABLE `company_user_report`
ADD COLUMN `opted_out` tinyint(4) DEFAULT 0 AFTER `USER_ID`;
