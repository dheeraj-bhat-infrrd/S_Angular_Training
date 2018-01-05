DROP TABLE IF EXISTS digest;

CREATE TABLE `digest` (
  `digest_id` varchar(45) NOT NULL,
  `company_id` int(11) unsigned DEFAULT NULL,
  `company_name` varchar(250) DEFAULT NULL,
  `month` int(10) DEFAULT NULL,
  `year` int(10) DEFAULT NULL,
  `average_score_rating` decimal(10,2) DEFAULT NULL,
  `user_count` int(10) DEFAULT NULL,
  `total_transactions` int(10) DEFAULT NULL,
  `completed_transactions` int(10) DEFAULT NULL,
  `survey_completion_rate` decimal(10,2) DEFAULT NULL,
  `sps` decimal(10,2) DEFAULT NULL,
  `promoters` int(10) DEFAULT NULL,
  `detractors` int(10) DEFAULT NULL,
  `passives` int(10) DEFAULT NULL,
  `total_completed_reviews` int(10) DEFAULT NULL,
  `trx_month` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`digest_id`),
  UNIQUE KEY `unique_digest_key` (`company_id`,`month`,`year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
