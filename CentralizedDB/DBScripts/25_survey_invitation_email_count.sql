DROP TABLE IF EXISTS `invitation_mail_count_month`;
CREATE TABLE `invitation_mail_count_month` (
  `invitation_mail_count_month_id` int(11) NOT NULL AUTO_INCREMENT,
  `agent_id` int(10) DEFAULT NULL,
  `agent_name` varchar(200) DEFAULT NULL,
  `agent_email` varchar(200) DEFAULT NULL,
  `company_id` int(10) DEFAULT NULL,
  `month` int(10) DEFAULT NULL,
  `year` int(10) DEFAULT NULL,
  `attempted_count` int(10) DEFAULT NULL,
  `delivered` int(10) DEFAULT NULL,
  `deffered` int(10) DEFAULT NULL,
  `blocked` int(10) DEFAULT NULL,
  `opened` int(10) DEFAULT NULL,
  `spam` int(10) DEFAULT NULL,
  `unsubscribed` int(10) DEFAULT NULL,
  `bounced` int(10) DEFAULT NULL,
  `link_clicked` int(10) DEFAULT NULL,
  `received` int(10) DEFAULT NULL,
  `dropped` int(10) DEFAULT NULL,
  PRIMARY KEY (`invitation_mail_count_month_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
