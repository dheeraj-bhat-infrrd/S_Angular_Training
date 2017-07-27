DROP TABLE IF EXISTS `overview_user_month`;
CREATE TABLE `overview_user_month` (
  `overview_user_month_id` varchar(45) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `processed` int(10) DEFAULT NULL,
  `completed` int(10) DEFAULT NULL,
  `incomplete` int(10) DEFAULT NULL,
  `social_posts` int(10) DEFAULT NULL,
  `zillow_reviews` int(10) DEFAULT NULL,
  `unprocessed` int(10) DEFAULT NULL,
  `unassigned` int(10) DEFAULT NULL,
  `duplicate` int(10) DEFAULT NULL,
  `corrupted` int(10) DEFAULT NULL,
  `other` int(10) DEFAULT NULL,
  `complete_percentage` decimal(10,2) DEFAULT NULL,
  `incomplete_percentage` decimal(10,2) DEFAULT NULL,
  `rating` decimal(10,2) DEFAULT NULL,
  `total_review` int(10) DEFAULT NULL,
  `month` int(10) DEFAULT NULL,
  `year` int(10) DEFAULT NULL,
  PRIMARY KEY (`overview_user_month_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `overview_branch_month`;
CREATE TABLE `overview_branch_month` (
  `overview_branch_month_id` varchar(45) NOT NULL,
  `branch_id` int(11) DEFAULT NULL,
  `processed` int(10) DEFAULT NULL,
  `completed` int(10) DEFAULT NULL,
  `incomplete` int(10) DEFAULT NULL,
  `social_posts` int(10) DEFAULT NULL,
  `zillow_reviews` int(10) DEFAULT NULL,
  `unprocessed` int(10) DEFAULT NULL,
  `unassigned` int(10) DEFAULT NULL,
  `duplicate` int(10) DEFAULT NULL,
  `corrupted` int(10) DEFAULT NULL,
  `other` int(10) DEFAULT NULL,
  `complete_percentage` decimal(10,2) DEFAULT NULL,
  `incomplete_percentage` decimal(10,2) DEFAULT NULL,
  `rating` decimal(10,2) DEFAULT NULL,
  `total_review` int(10) DEFAULT NULL,
  `month` int(10) DEFAULT NULL,
  `year` int(10) DEFAULT NULL,
  PRIMARY KEY (`overview_branch_month_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `overview_region_month`;
CREATE TABLE `overview_region_month` (
  `overview_region_month_id` varchar(45) NOT NULL,
  `region_id` int(11) DEFAULT NULL,
  `processed` int(10) DEFAULT NULL,
  `completed` int(10) DEFAULT NULL,
  `incomplete` int(10) DEFAULT NULL,
  `social_posts` int(10) DEFAULT NULL,
  `zillow_reviews` int(10) DEFAULT NULL,
  `unprocessed` int(10) DEFAULT NULL,
  `unassigned` int(10) DEFAULT NULL,
  `duplicate` int(10) DEFAULT NULL,
  `corrupted` int(10) DEFAULT NULL,
  `other` int(10) DEFAULT NULL,
  `complete_percentage` decimal(10,2) DEFAULT NULL,
  `incomplete_percentage` decimal(10,2) DEFAULT NULL,
  `rating` decimal(10,2) DEFAULT NULL,
  `total_review` int(10) DEFAULT NULL,
  `month` int(10) DEFAULT NULL,
  `year` int(10) DEFAULT NULL,
  PRIMARY KEY (`overview_region_month_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `overview_company_month`;
CREATE TABLE `overview_company_month` (
  `overview_company_month_id` varchar(45) NOT NULL,
  `company_id` int(11) DEFAULT NULL,
  `processed` int(10) DEFAULT NULL,
  `completed` int(10) DEFAULT NULL,
  `incomplete` int(10) DEFAULT NULL,
  `social_posts` int(10) DEFAULT NULL,
  `zillow_reviews` int(10) DEFAULT NULL,
  `unprocessed` int(10) DEFAULT NULL,
  `unassigned` int(10) DEFAULT NULL,
  `duplicate` int(10) DEFAULT NULL,
  `corrupted` int(10) DEFAULT NULL,
  `other` int(10) DEFAULT NULL,
  `complete_percentage` decimal(10,2) DEFAULT NULL,
  `incomplete_percentage` decimal(10,2) DEFAULT NULL,
  `rating` decimal(10,2) DEFAULT NULL,
  `total_review` int(10) DEFAULT NULL,
  `month` int(10) DEFAULT NULL,
  `year` int(10) DEFAULT NULL,
  PRIMARY KEY (`overview_company_month_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;