drop table if exists user_ranking_this_month_main;
CREATE TABLE `user_ranking_this_month_main` (
  `user_ranking_this_month_main_id` varchar(36) NOT NULL,
  `user_id` int(11) unsigned DEFAULT NULL,
  `company_id` int(11) unsigned DEFAULT NULL,
  `this_month` int(11) DEFAULT NULL,
  `this_year` int(11) DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `ranking_score` decimal(10,2) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `sps` decimal(10,2) DEFAULT NULL,
  `days_of_registration` int(11) DEFAULT NULL,
  `completed` int(11) DEFAULT NULL,
  `sent` int(11) DEFAULT NULL,
  `completed_percentage` decimal(10,2) DEFAULT NULL,
  `total_reviews` int(11) DEFAULT NULL,
  `is_eligible` int(2) DEFAULT NULL,
  `average_rating` tinyint(4) DEFAULT NULL,
  `max_eligible_rank` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_ranking_this_month_main_id`),
  UNIQUE KEY `unique_keys_f11` (`user_id`,`this_month`,`this_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table if exists user_ranking_this_month_branch;
CREATE TABLE `user_ranking_this_month_branch` (
  `user_ranking_this_month_branch_id` varchar(36) NOT NULL,
  `user_id` int(11) unsigned DEFAULT NULL,
  `company_id` int(11) unsigned DEFAULT NULL,
  `branch_id` int(11) unsigned DEFAULT NULL,
  `this_month` int(11) DEFAULT NULL,
  `this_year` int(11) DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `ranking_score` decimal(10,2) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `sps` decimal(10,2) DEFAULT NULL,
  `days_of_registration` int(11) DEFAULT NULL,
  `completed` int(11) DEFAULT NULL,
  `sent` int(11) DEFAULT NULL,
  `completed_percentage` decimal(10,2) DEFAULT NULL,
  `total_reviews` int(11) DEFAULT NULL,
  `is_eligible` int(2) DEFAULT NULL,
  `average_rating` tinyint(4) DEFAULT NULL,
  `internal_branch_rank` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_ranking_this_month_branch_id`),
  UNIQUE KEY `unique_keys_f10` (`user_id`,`branch_id`,`this_month`,`this_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table if exists user_ranking_this_month_region;
CREATE TABLE `user_ranking_this_month_region` (
  `user_ranking_this_month_region_id` varchar(36) NOT NULL,
  `user_id` int(11) unsigned DEFAULT NULL,
  `company_id` int(11) unsigned DEFAULT NULL,
  `region_id` int(11) unsigned DEFAULT NULL,
  `this_month` int(11) DEFAULT NULL,
  `this_year` int(11) DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `ranking_score` decimal(10,2) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `sps` decimal(10,2) DEFAULT NULL,
  `days_of_registration` int(11) DEFAULT NULL,
  `completed` int(11) DEFAULT NULL,
  `sent` int(11) DEFAULT NULL,
  `completed_percentage` decimal(10,2) DEFAULT NULL,
  `total_reviews` int(11) DEFAULT NULL,
  `is_eligible` int(2) DEFAULT NULL,
  `average_rating` tinyint(4) DEFAULT NULL,
  `internal_region_rank` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_ranking_this_month_region_id`),
  UNIQUE KEY `unique_keys_f12` (`user_id`,`region_id`,`this_month`,`this_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table if exists user_ranking_this_year_main;
CREATE TABLE `user_ranking_this_year_main` (
  `user_ranking_this_year_main_id` varchar(36) NOT NULL,
  `user_id` int(11) unsigned DEFAULT NULL,
  `company_id` int(11) unsigned DEFAULT NULL,
  `this_year` int(11) DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `ranking_score` decimal(10,2) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `sps` decimal(10,2) DEFAULT NULL,
  `days_of_registration` int(11) DEFAULT NULL,
  `completed` int(11) DEFAULT NULL,
  `sent` int(11) DEFAULT NULL,
  `completed_percentage` decimal(10,2) DEFAULT NULL,
  `total_reviews` int(11) DEFAULT NULL,
  `is_eligible` int(2) DEFAULT NULL,
  `average_rating` tinyint(4) DEFAULT NULL,
  `max_eligible_rank` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_ranking_this_year_main_id`),
  UNIQUE KEY `unique_keys_f14` (`user_id`,`this_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table if exists user_ranking_this_year_branch;
CREATE TABLE `user_ranking_this_year_branch` (
  `user_ranking_this_year_branch_id` varchar(36) NOT NULL,
  `user_id` int(11) unsigned DEFAULT NULL,
  `company_id` int(11) unsigned DEFAULT NULL,
  `branch_id` int(11) unsigned DEFAULT NULL,
  `this_year` int(11) DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `ranking_score` decimal(10,2) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `sps` decimal(10,2) DEFAULT NULL,
  `days_of_registration` int(11) DEFAULT NULL,
  `completed` int(11) DEFAULT NULL,
  `sent` int(11) DEFAULT NULL,
  `completed_percentage` decimal(10,2) DEFAULT NULL,
  `total_reviews` int(11) DEFAULT NULL,
  `is_eligible` int(2) DEFAULT NULL,
  `average_rating` tinyint(4) DEFAULT NULL,
  `internal_branch_rank` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_ranking_this_year_branch_id`),
  UNIQUE KEY `unique_keys_f13` (`user_id`,`branch_id`,`this_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table if exists user_ranking_this_year_region;
CREATE TABLE `user_ranking_this_year_region` (
  `user_ranking_this_year_region_id` varchar(36) NOT NULL,
  `user_id` int(11) unsigned DEFAULT NULL,
  `company_id` int(11) unsigned DEFAULT NULL,
  `region_id` int(11) unsigned DEFAULT NULL,
  `this_year` int(11) DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `ranking_score` decimal(10,2) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `sps` decimal(10,2) DEFAULT NULL,
  `days_of_registration` int(11) DEFAULT NULL,
  `completed` int(11) DEFAULT NULL,
  `sent` int(11) DEFAULT NULL,
  `completed_percentage` decimal(10,2) DEFAULT NULL,
  `total_reviews` int(11) DEFAULT NULL,
  `is_eligible` int(2) DEFAULT NULL,
  `average_rating` tinyint(4) DEFAULT NULL,
  `internal_region_rank` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_ranking_this_year_region_id`),
  UNIQUE KEY `unique_keys_f15` (`user_id`,`region_id`,`this_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table if exists user_ranking_past_month_main;
CREATE TABLE `user_ranking_past_month_main` (
  `user_ranking_past_month_main_id` varchar(36) NOT NULL,
  `user_id` int(11) unsigned DEFAULT NULL,
  `company_id` int(11) unsigned DEFAULT NULL,
  `month` int(11) DEFAULT NULL,
  `year` int(11) DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `ranking_score` decimal(10,2) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `sps` decimal(10,2) DEFAULT NULL,
  `days_of_registration` int(11) DEFAULT NULL,
  `completed` int(11) DEFAULT NULL,
  `sent` int(11) DEFAULT NULL,
  `completed_percentage` decimal(10,2) DEFAULT NULL,
  `total_reviews` int(11) DEFAULT NULL,
  `is_eligible` int(2) DEFAULT NULL,
  `month_year` varchar(45) DEFAULT NULL,
  `average_rating` tinyint(4) DEFAULT NULL,
  `max_eligible_rank` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_ranking_past_month_main_id`),
  UNIQUE KEY `unique_key_f1` (`user_id`,`month_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table if exists user_ranking_past_month_branch;
CREATE TABLE `user_ranking_past_month_branch` (
  `user_ranking_past_month_branch_id` varchar(36) NOT NULL,
  `user_id` int(11) unsigned DEFAULT NULL,
  `company_id` int(11) unsigned DEFAULT NULL,
  `branch_id` int(11) unsigned DEFAULT NULL,
  `month` int(11) DEFAULT NULL,
  `year` int(11) DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `ranking_score` decimal(10,2) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `sps` decimal(10,2) DEFAULT NULL,
  `days_of_registration` int(11) DEFAULT NULL,
  `completed` int(11) DEFAULT NULL,
  `sent` int(11) DEFAULT NULL,
  `completed_percentage` decimal(10,2) DEFAULT NULL,
  `total_reviews` int(11) DEFAULT NULL,
  `is_eligible` int(2) DEFAULT NULL,
  `month_year` varchar(45) DEFAULT NULL,
  `average_rating` tinyint(4) DEFAULT NULL,
  `internal_branch_rank` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_ranking_past_month_branch_id`),
  UNIQUE KEY `unique_key_f2` (`user_id`,`branch_id`,`month_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table if exists user_ranking_past_month_region;
CREATE TABLE `user_ranking_past_month_region` (
  `user_ranking_past_month_region_id` varchar(36) NOT NULL,
  `user_id` int(11) unsigned DEFAULT NULL,
  `company_id` int(11) unsigned DEFAULT NULL,
  `region_id` int(11) unsigned DEFAULT NULL,
  `month` int(11) DEFAULT NULL,
  `year` int(11) DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `ranking_score` decimal(10,2) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `sps` decimal(10,2) DEFAULT NULL,
  `days_of_registration` int(11) DEFAULT NULL,
  `completed` int(11) DEFAULT NULL,
  `sent` int(11) DEFAULT NULL,
  `completed_percentage` decimal(10,2) DEFAULT NULL,
  `total_reviews` int(11) DEFAULT NULL,
  `is_eligible` int(2) DEFAULT NULL,
  `month_year` varchar(45) DEFAULT NULL,
  `average_rating` tinyint(4) DEFAULT NULL,
  `internal_region_rank` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_ranking_past_month_region_id`),
  UNIQUE KEY `unique_keys_f3` (`user_id`,`region_id`,`month_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


drop table if exists user_ranking_past_year_main;
CREATE TABLE `user_ranking_past_year_main` (
  `user_ranking_past_year_main_id` varchar(36) NOT NULL,
  `user_id` int(11) unsigned DEFAULT NULL,
  `company_id` int(11) unsigned DEFAULT NULL,
  `year` int(11) DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `ranking_score` decimal(10,2) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `sps` decimal(10,2) DEFAULT NULL,
  `days_of_registration` int(11) DEFAULT NULL,
  `completed` int(11) DEFAULT NULL,
  `sent` int(11) DEFAULT NULL,
  `completed_percentage` decimal(10,2) DEFAULT NULL,
  `total_reviews` int(11) DEFAULT NULL,
  `is_eligible` int(2) DEFAULT NULL,
  `average_rating` tinyint(4) DEFAULT NULL,
  `max_eligible_rank` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_ranking_past_year_main_id`),
  UNIQUE KEY `unique_keys_f5` (`user_id`,`year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


drop table if exists user_ranking_past_year_branch;
CREATE TABLE `user_ranking_past_year_branch` (
  `user_ranking_past_year_branch_id` varchar(36) NOT NULL,
  `user_id` int(11) unsigned DEFAULT NULL,
  `company_id` int(11) unsigned DEFAULT NULL,
  `branch_id` int(11) unsigned DEFAULT NULL,
  `year` int(11) DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `ranking_score` decimal(10,2) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `sps` decimal(10,2) DEFAULT NULL,
  `days_of_registration` int(11) DEFAULT NULL,
  `completed` int(11) DEFAULT NULL,
  `sent` int(11) DEFAULT NULL,
  `completed_percentage` decimal(10,2) DEFAULT NULL,
  `total_reviews` int(11) DEFAULT NULL,
  `is_eligible` int(2) DEFAULT NULL,
  `average_rating` tinyint(4) DEFAULT NULL,
  `internal_branch_rank` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_ranking_past_year_branch_id`),
  UNIQUE KEY `unique_keys_f4` (`user_id`,`branch_id`,`year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table if exists user_ranking_past_year_region;
CREATE TABLE `user_ranking_past_year_region` (
  `user_ranking_past_year_region_id` varchar(36) NOT NULL,
  `user_id` int(11) unsigned DEFAULT NULL,
  `company_id` int(11) unsigned DEFAULT NULL,
  `region_id` int(11) unsigned DEFAULT NULL,
  `year` int(11) DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `ranking_score` decimal(10,2) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `sps` decimal(10,2) DEFAULT NULL,
  `days_of_registration` int(11) DEFAULT NULL,
  `completed` int(11) DEFAULT NULL,
  `sent` int(11) DEFAULT NULL,
  `completed_percentage` decimal(10,2) DEFAULT NULL,
  `total_reviews` int(11) DEFAULT NULL,
  `is_eligible` int(2) DEFAULT NULL,
  `average_rating` tinyint(4) DEFAULT NULL,
  `internal_region_rank` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_ranking_past_year_region_id`),
  UNIQUE KEY `unique_keys_f6` (`user_id`,`region_id`,`year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table if exists user_ranking_past_years_main;
CREATE TABLE `user_ranking_past_years_main` (
  `user_ranking_past_years_main_id` varchar(36) NOT NULL,
  `user_id` int(11) unsigned DEFAULT NULL,
  `company_id` int(11) unsigned DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `ranking_score` decimal(10,2) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `sps` decimal(10,2) DEFAULT NULL,
  `days_of_registration` int(11) DEFAULT NULL,
  `completed` int(11) DEFAULT NULL,
  `sent` int(11) DEFAULT NULL,
  `completed_percentage` decimal(10,2) DEFAULT NULL,
  `total_reviews` int(11) DEFAULT NULL,
  `is_eligible` int(2) DEFAULT NULL,
  `average_rating` tinyint(4) DEFAULT NULL,
  `max_eligible_rank` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_ranking_past_years_main_id`),
  UNIQUE KEY `unique_keys_f8` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table if exists user_ranking_past_years_branch;
CREATE TABLE `user_ranking_past_years_branch` (
  `user_ranking_past_years_branch_id` varchar(36) NOT NULL,
  `user_id` int(11) unsigned DEFAULT NULL,
  `company_id` int(11) unsigned DEFAULT NULL,
  `branch_id` int(11) unsigned DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `ranking_score` decimal(10,2) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `sps` decimal(10,2) DEFAULT NULL,
  `days_of_registration` int(11) DEFAULT NULL,
  `completed` int(11) DEFAULT NULL,
  `sent` int(11) DEFAULT NULL,
  `completed_percentage` decimal(10,2) DEFAULT NULL,
  `total_reviews` int(11) DEFAULT NULL,
  `is_eligible` int(2) DEFAULT NULL,
  `average_rating` tinyint(4) DEFAULT NULL,
  `internal_branch_rank` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_ranking_past_years_branch_id`),
  UNIQUE KEY `unique_keys_f7` (`user_id`,`branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table if exists user_ranking_past_years_region;
CREATE TABLE `user_ranking_past_years_region` (
  `user_ranking_past_years_region_id` varchar(36) NOT NULL,
  `user_id` int(11) unsigned DEFAULT NULL,
  `company_id` int(11) unsigned DEFAULT NULL,
  `region_id` int(11) unsigned DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `ranking_score` decimal(10,2) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `sps` decimal(10,2) DEFAULT NULL,
  `days_of_registration` int(11) DEFAULT NULL,
  `completed` int(11) DEFAULT NULL,
  `sent` int(11) DEFAULT NULL,
  `completed_percentage` decimal(10,2) DEFAULT NULL,
  `total_reviews` int(11) DEFAULT NULL,
  `is_eligible` int(2) DEFAULT NULL,
  `average_rating` tinyint(4) DEFAULT NULL,
  `internal_region_rank` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_ranking_past_years_region_id`),
  UNIQUE KEY `unique_keys_f9` (`user_id`,`region_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
