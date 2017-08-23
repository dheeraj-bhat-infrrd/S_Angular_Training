DROP TABLE IF EXISTS `score_stats_overall_company`;

CREATE TABLE `score_stats_overall_company` (
  `score_stats_overall_company_id` varchar(36) NOT NULL,
  `company_id` int(11) unsigned DEFAULT NULL,
  `five_star` int(11) DEFAULT NULL,
  `four_star` int(11) DEFAULT NULL,
  `three_star` int(11) DEFAULT NULL,
  `two_star` int(11) DEFAULT NULL,
  `one_star` int(11) DEFAULT NULL,
  `avg_score` decimal(16,2) DEFAULT NULL,
  `month_val` int(11) DEFAULT NULL,
  `year_val` int(11) DEFAULT NULL,
  PRIMARY KEY (`score_stats_overall_company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `score_stats_overall_region`;

CREATE TABLE `score_stats_overall_region` (
  `score_stats_overall_region_id` varchar(36) NOT NULL,
  `region_id` int(11) unsigned DEFAULT NULL,
  `five_star` int(11) DEFAULT NULL,
  `four_star` int(11) DEFAULT NULL,
  `three_star` int(11) DEFAULT NULL,
  `two_star` int(11) DEFAULT NULL,
  `one_star` int(11) DEFAULT NULL,
  `avg_score` decimal(16,2) DEFAULT NULL,
  `month_val` int(11) DEFAULT NULL,
  `year_val` int(11) DEFAULT NULL,
  PRIMARY KEY (`score_stats_overall_region_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `score_stats_overall_branch`;

CREATE TABLE `score_stats_overall_branch` (
  `score_stats_overall_branch_id` varchar(36) NOT NULL,
  `branch_id` int(11) unsigned DEFAULT NULL,
  `five_star` int(11) DEFAULT NULL,
  `four_star` int(11) DEFAULT NULL,
  `three_star` int(11) DEFAULT NULL,
  `two_star` int(11) DEFAULT NULL,
  `one_star` int(11) DEFAULT NULL,
  `avg_score` decimal(16,2) DEFAULT NULL,
  `month_val` int(11) DEFAULT NULL,
  `year_val` int(11) DEFAULT NULL,
  PRIMARY KEY (`score_stats_overall_branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `score_stats_overall_user`;

CREATE TABLE `score_stats_overall_user` (
  `score_stats_overall_user_id` varchar(36) NOT NULL,
  `user_id` int(11) unsigned DEFAULT NULL,
  `five_star` int(11) DEFAULT NULL,
  `four_star` int(11) DEFAULT NULL,
  `three_star` int(11) DEFAULT NULL,
  `two_star` int(11) DEFAULT NULL,
  `one_star` int(11) DEFAULT NULL,
  `avg_score` decimal(16,2) DEFAULT NULL,
  `month_val` int(11) DEFAULT NULL,
  `year_val` int(11) DEFAULT NULL,
  PRIMARY KEY (`score_stats_overall_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `score_stats_question_company`;

CREATE TABLE `score_stats_question_company` (
  `score_stats_question_company_id` varchar(36) NOT NULL,
  `company_id` int(11) unsigned DEFAULT NULL,
  `question_id` int(10) unsigned DEFAULT NULL,
  `question` text,
  `five_star` int(11) DEFAULT NULL,
  `four_star` int(11) DEFAULT NULL,
  `three_star` int(11) DEFAULT NULL,
  `two_star` int(11) DEFAULT NULL,
  `one_star` int(11) DEFAULT NULL,
  `avg_score` decimal(16,2) DEFAULT NULL,
  `month_val` int(11) DEFAULT NULL,
  `year_val` int(11) DEFAULT NULL,
  PRIMARY KEY (`score_stats_question_company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `score_stats_question_region`;

CREATE TABLE `score_stats_question_region` (
  `score_stats_question_region_id` varchar(36) NOT NULL,
  `region_id` int(11) unsigned DEFAULT NULL,
  `question_id` int(10) unsigned DEFAULT NULL,
  `question` text,
  `five_star` int(11) DEFAULT NULL,
  `four_star` int(11) DEFAULT NULL,
  `three_star` int(11) DEFAULT NULL,
  `two_star` int(11) DEFAULT NULL,
  `one_star` int(11) DEFAULT NULL,
  `avg_score` decimal(16,2) DEFAULT NULL,
  `month_val` int(11) DEFAULT NULL,
  `year_val` int(11) DEFAULT NULL,
  PRIMARY KEY (`score_stats_question_region_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `score_stats_question_branch`;

CREATE TABLE `score_stats_question_branch` (
  `score_stats_question_branch_id` varchar(36) NOT NULL,
  `branch_id` int(11) unsigned DEFAULT NULL,
  `question_id` int(10) unsigned DEFAULT NULL,
  `question` text,
  `five_star` int(11) DEFAULT NULL,
  `four_star` int(11) DEFAULT NULL,
  `three_star` int(11) DEFAULT NULL,
  `two_star` int(11) DEFAULT NULL,
  `one_star` int(11) DEFAULT NULL,
  `avg_score` decimal(16,2) DEFAULT NULL,
  `month_val` int(11) DEFAULT NULL,
  `year_val` int(11) DEFAULT NULL,
  PRIMARY KEY (`score_stats_question_branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `score_stats_question_user`;

CREATE TABLE `score_stats_question_user` (
  `score_stats_question_user_id` varchar(36) NOT NULL,
  `user_id` int(11) unsigned DEFAULT NULL,
  `question_id` int(10) unsigned DEFAULT NULL,
  `question` text,
  `five_star` int(11) DEFAULT NULL,
  `four_star` int(11) DEFAULT NULL,
  `three_star` int(11) DEFAULT NULL,
  `two_star` int(11) DEFAULT NULL,
  `one_star` int(11) DEFAULT NULL,
  `avg_score` decimal(16,2) DEFAULT NULL,
  `month_val` int(11) DEFAULT NULL,
  `year_val` int(11) DEFAULT NULL,
  PRIMARY KEY (`score_stats_question_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
