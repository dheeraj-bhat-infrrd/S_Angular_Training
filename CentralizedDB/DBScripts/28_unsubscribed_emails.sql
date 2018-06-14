DROP TABLE IF EXISTS `unsubscribed_emails`;
CREATE TABLE `unsubscribed_emails` (
  `unsubscribed_emails_id` varchar(45) NOT NULL,
  `company_id` int(11) DEFAULT NULL,
  `email_id` varchar(450) DEFAULT NULL,
  `agent_id` int(11) DEFAULT NULL,
  `status` tinyint(2) DEFAULT NULL,
  `level` tinyint(2) DEFAULT NULL,
  `created_on` bigint(20) NULL DEFAULT NULL,
  `modified_on` bigint(20) NULL DEFAULT NULL,
  `created_on_est` varchar(450) DEFAULT NULL,
  `modified_on_est` varchar(450) DEFAULT NULL,
  PRIMARY KEY (`unsubscribed_emails_id`),
  KEY `company_id` (`company_id`),
  KEY `agent_id` (`agent_id`),
  KEY `status` (`status`),
  KEY `level` (`level`) USING BTREE
) ENGINE=InnoDB;
