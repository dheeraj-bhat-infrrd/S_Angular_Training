TRUNCATE `user_ranking_past_month_branch`;
TRUNCATE `user_ranking_past_month_main`;
TRUNCATE `user_ranking_past_month_region`;
TRUNCATE `user_ranking_past_year_branch`;
TRUNCATE `user_ranking_past_year_main`;
TRUNCATE `user_ranking_past_year_region`;
TRUNCATE `user_ranking_past_years_branch`;
TRUNCATE `user_ranking_past_years_main`;
TRUNCATE `user_ranking_past_years_region`;
TRUNCATE `user_ranking_this_month_branch`;
TRUNCATE `user_ranking_this_month_main`;
TRUNCATE `user_ranking_this_month_region`;
TRUNCATE `user_ranking_this_year_branch`;
TRUNCATE `user_ranking_this_year_main`;
TRUNCATE `user_ranking_this_year_region`;

ALTER TABLE `user_ranking_past_month_branch` 
CHANGE COLUMN `average_rating` `average_rating` DECIMAL(10,3) NULL DEFAULT NULL, 
CHANGE COLUMN `ranking_score` `ranking_score` DECIMAL(10,3) NULL DEFAULT NULL ;
ALTER TABLE `user_ranking_past_month_main` 
CHANGE COLUMN `average_rating` `average_rating` DECIMAL(10,3) NULL DEFAULT NULL, 
CHANGE COLUMN `ranking_score` `ranking_score` DECIMAL(10,3) NULL DEFAULT NULL ;
ALTER TABLE `user_ranking_past_month_region` 
CHANGE COLUMN `average_rating` `average_rating` DECIMAL(10,3) NULL DEFAULT NULL, 
CHANGE COLUMN `ranking_score` `ranking_score` DECIMAL(10,3) NULL DEFAULT NULL ;
ALTER TABLE `user_ranking_past_year_branch` 
CHANGE COLUMN `average_rating` `average_rating` DECIMAL(10,3) NULL DEFAULT NULL, 
CHANGE COLUMN `ranking_score` `ranking_score` DECIMAL(10,3) NULL DEFAULT NULL ;
ALTER TABLE `user_ranking_past_year_main`
CHANGE COLUMN `average_rating` `average_rating` DECIMAL(10,3) NULL DEFAULT NULL,  
CHANGE COLUMN `ranking_score` `ranking_score` DECIMAL(10,3) NULL DEFAULT NULL ;
ALTER TABLE `user_ranking_past_year_region` 
CHANGE COLUMN `average_rating` `average_rating` DECIMAL(10,3) NULL DEFAULT NULL, 
CHANGE COLUMN `ranking_score` `ranking_score` DECIMAL(10,3) NULL DEFAULT NULL ;
ALTER TABLE `user_ranking_past_years_branch`
CHANGE COLUMN `average_rating` `average_rating` DECIMAL(10,3) NULL DEFAULT NULL,  
CHANGE COLUMN `ranking_score` `ranking_score` DECIMAL(10,3) NULL DEFAULT NULL ;
ALTER TABLE `user_ranking_past_years_main` 
CHANGE COLUMN `average_rating` `average_rating` DECIMAL(10,3) NULL DEFAULT NULL, 
CHANGE COLUMN `ranking_score` `ranking_score` DECIMAL(10,3) NULL DEFAULT NULL ;
ALTER TABLE `user_ranking_past_years_region` 
CHANGE COLUMN `average_rating` `average_rating` DECIMAL(10,3) NULL DEFAULT NULL, 
CHANGE COLUMN `ranking_score` `ranking_score` DECIMAL(10,3) NULL DEFAULT NULL ;
ALTER TABLE `user_ranking_this_month_branch` 
CHANGE COLUMN `average_rating` `average_rating` DECIMAL(10,3) NULL DEFAULT NULL, 
CHANGE COLUMN `ranking_score` `ranking_score` DECIMAL(10,3) NULL DEFAULT NULL ;
ALTER TABLE `user_ranking_this_month_main` 
CHANGE COLUMN `average_rating` `average_rating` DECIMAL(10,3) NULL DEFAULT NULL, 
CHANGE COLUMN `ranking_score` `ranking_score` DECIMAL(10,3) NULL DEFAULT NULL ;
ALTER TABLE `user_ranking_this_month_region`
CHANGE COLUMN `average_rating` `average_rating` DECIMAL(10,3) NULL DEFAULT NULL,  
CHANGE COLUMN `ranking_score` `ranking_score` DECIMAL(10,3) NULL DEFAULT NULL ;
ALTER TABLE `user_ranking_this_year_branch` 
CHANGE COLUMN `average_rating` `average_rating` DECIMAL(10,3) NULL DEFAULT NULL, 
CHANGE COLUMN `ranking_score` `ranking_score` DECIMAL(10,3) NULL DEFAULT NULL ;
ALTER TABLE `user_ranking_this_year_main` 
CHANGE COLUMN `average_rating` `average_rating` DECIMAL(10,3) NULL DEFAULT NULL, 
CHANGE COLUMN `ranking_score` `ranking_score` DECIMAL(10,3) NULL DEFAULT NULL ;
ALTER TABLE `user_ranking_this_year_region` 
CHANGE COLUMN `average_rating` `average_rating` DECIMAL(10,3) NULL DEFAULT NULL, 
CHANGE COLUMN `ranking_score` `ranking_score` DECIMAL(10,3) NULL DEFAULT NULL ;
ALTER TABLE `min_requirement_for_ranking` 
CHANGE COLUMN `MONTH_OFFSET` `MONTH_OFFSET` DECIMAL(10,3) NULL DEFAULT NULL ;
