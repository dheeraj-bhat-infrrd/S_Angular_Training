DROP TABLE IF EXISTS `user_ranking_report_branch`;
CREATE TABLE `user_ranking_report_branch` (
  `user_ranking_report_branch_id` varchar(45) NOT NULL,
  `rank_within_company` int(11) DEFAULT NULL,
  `branch_id` int(10) DEFAULT NULL,
  `branch_name` varchar(450) DEFAULT NULL,
  `region_id` int(10) DEFAULT NULL,
  `region_name` varchar(450) DEFAULT NULL,
  `company_id` int(10) DEFAULT NULL,
  `user_count` int(10) DEFAULT NULL,
  `avg_score` decimal(16,3) DEFAULT NULL,
  `ranking_score` decimal(16,3) DEFAULT NULL,
  `completion_percentage` decimal(16,2) DEFAULT NULL,
  `total_reviews` int(10) DEFAULT NULL,
  `sps` decimal(16,2) DEFAULT NULL,
  `public_page_url` text,
  `is_eligible` int(10),
  `month` int(10),
  `year` int(10),
  PRIMARY KEY (`user_ranking_report_branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `user_ranking_report_branch_year`;
CREATE TABLE `user_ranking_report_branch_year` (
  `user_ranking_report_branch_year_id` varchar(45) NOT NULL,
  `rank_within_company` int(11) DEFAULT NULL,
  `branch_id` int(10) DEFAULT NULL,
  `branch_name` varchar(450) DEFAULT NULL,
  `region_id` int(10) DEFAULT NULL,
  `region_name` varchar(450) DEFAULT NULL,
  `company_id` int(10) DEFAULT NULL,
  `user_count` int(10) DEFAULT NULL,
  `avg_score` decimal(16,3) DEFAULT NULL,
  `ranking_score` decimal(16,3) DEFAULT NULL,
  `completion_percentage` decimal(16,2) DEFAULT NULL,
  `total_reviews` int(10) DEFAULT NULL,
  `sps` decimal(16,2) DEFAULT NULL,
  `public_page_url` text,
  `is_eligible` int(10),
  `year` int(10),
  PRIMARY KEY (`user_ranking_report_branch_year_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

