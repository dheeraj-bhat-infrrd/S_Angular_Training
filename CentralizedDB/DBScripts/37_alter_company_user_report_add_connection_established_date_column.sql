ALTER TABLE `company_user_report` 
drop COLUMN `fb_data_connection`;

ALTER TABLE `company_user_report` 
ADD COLUMN `fb_connection_established_date` datetime AFTER `fb_data_connection`;

ALTER TABLE `company_user_report` 
drop COLUMN `fb_data_connection`;

ALTER TABLE `company_user_report` 
ADD COLUMN `linkedin_connection_established_date` datetime AFTER `linkedin_data_connection`;

ALTER TABLE `company_user_report` 
drop COLUMN `linkedin_data_connection`;

ALTER TABLE `company_user_report` 
ADD COLUMN `twitter_connection_established_date` datetime AFTER `twitter_data_connection`;

ALTER TABLE `company_user_report` 
drop COLUMN `twitter_data_connection`;
