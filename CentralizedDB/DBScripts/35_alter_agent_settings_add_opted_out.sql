ALTER TABLE `agent_settings` 
ADD COLUMN `opted_out` tinyint(4) DEFAULT 0 AFTER `USER_ID`;
