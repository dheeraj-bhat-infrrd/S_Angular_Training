
CREATE TABLE `survey_pre_initiation_participant_type_lookup` (
  `PARTICIPANT_TYPE_ID` int(2) NOT NULL,
  `PARTICIPANT_TYPE_NAME` varchar(45) NOT NULL,
  PRIMARY KEY (`PARTICIPANT_TYPE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `survey_pre_initiation_participant_type_lookup`(`PARTICIPANT_TYPE_ID`,`PARTICIPANT_TYPE_NAME`)
VALUES(1,'Borrower'),(2,'CoBorrower'),(3,'Buyer Agent'),(4,'Seller Agent');