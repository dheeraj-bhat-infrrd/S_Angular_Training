DROP TABLE IF EXISTS `nps_report_user_week`;
CREATE TABLE `nps_report_user_week` (
  `nps_report_user_week_id` varchar(36) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `week` int(11) DEFAULT NULL,
  `year` int(11) DEFAULT NULL,
  `responders` int(11) DEFAULT NULL,
  `promotors` int(11) DEFAULT NULL,
  `detractors` int(11) DEFAULT NULL,
  `passives` int(11) DEFAULT NULL,
  `sum` int(11) DEFAULT NULL,
  PRIMARY KEY (`nps_report_user_week_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `nps_report_user_month`;
CREATE TABLE `nps_report_user_month` (
  `nps_report_user_month_id` varchar(36) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `month` int(11) DEFAULT NULL,
  `year` int(11) DEFAULT NULL,
  `responders` int(11) DEFAULT NULL,
  `promotors` int(11) DEFAULT NULL,
  `detractors` int(11) DEFAULT NULL,
  `passives` int(11) DEFAULT NULL,
  `sum` int(11) DEFAULT NULL,
  PRIMARY KEY (`nps_report_user_month_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `nps_report_month`;
CREATE TABLE `nps_report_month` (
  `nps_report_month_id` varchar(45) NOT NULL,
  `month` int(11) DEFAULT NULL,
  `year` int(11) DEFAULT NULL,
  `company_id` int(11) DEFAULT NULL,
  `company_name` varchar(450) DEFAULT NULL,
  `region_id` int(11) DEFAULT NULL,
  `region_name` varchar(450) DEFAULT NULL,
  `branch_id` int(11) DEFAULT NULL,
  `branch_name` varchar(450) DEFAULT NULL,
  `nps` decimal(16,2) DEFAULT NULL,
  `previous_month_nps` decimal(16,2) DEFAULT NULL,
  `nps_delta` decimal(16,2) DEFAULT NULL,
  `responders` int(11) DEFAULT NULL,
  `response_percent` decimal(16,2) DEFAULT NULL,
  `avg_nps_rating` decimal(16,2) DEFAULT NULL,
  `promotors` decimal(16,2) DEFAULT NULL,
  `detractors` decimal(16,2) DEFAULT NULL,
  PRIMARY KEY (`nps_report_month_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `nps_report_week`;
CREATE TABLE `nps_report_week` (
  `nps_report_week_id` varchar(45) NOT NULL,
  `week` int(11) DEFAULT NULL,
  `year` int(11) DEFAULT NULL,
  `company_id` int(11) DEFAULT NULL,
  `company_name` varchar(450) DEFAULT NULL,
  `region_id` int(11) DEFAULT NULL,
  `region_name` varchar(450) DEFAULT NULL,
  `branch_id` int(11) DEFAULT NULL,
  `branch_name` varchar(450) DEFAULT NULL,
  `nps` decimal(16,2) DEFAULT NULL,
  `previous_month_nps` decimal(16,2) DEFAULT NULL,
  `nps_delta` decimal(16,2) DEFAULT NULL,
  `responders` int(11) DEFAULT NULL,
  `response_percent` decimal(16,2) DEFAULT NULL,
  `avg_nps_rating` decimal(16,2) DEFAULT NULL,
  `promotors` decimal(16,2) DEFAULT NULL,
  `detractors` decimal(16,2) DEFAULT NULL,
  PRIMARY KEY (`nps_report_week_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;