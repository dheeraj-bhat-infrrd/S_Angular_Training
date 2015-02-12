-- Inserting default values into COMPANY
INSERT INTO `ss_user`.`COMPANY` (`COMPANY_ID`, `COMPANY`, `IS_REGISTRATION_COMPLETE`, `STATUS`, `CREATED_ON`, `CREATED_BY`, `MODIFIED_ON`, `MODIFIED_BY`) VALUES ('1', 'Default', 1, '1', '2014-11-25 19:34:12', 'ADMIN', '2014-11-25 19:34:12', 'ADMIN');

-- Alter script DEC-1-2014
INSERT INTO `ss_user`.`PROFILES_MASTER` VALUES (10,'NP',1,'2014-11-26 08:33:56','ADMIN','2014-11-25 14:07:12','ADMIN'),(1,'CA',1,'2014-11-25 14:07:12','ADMIN','2014-11-25 14:07:12','ADMIN'),(2,'RA',1,'2014-11-25 14:07:12','ADMIN','2014-11-25 14:07:12','ADMIN'),(3,'BA',1,'2014-11-25 14:07:12','ADMIN','2014-11-25 14:07:12','ADMIN'),(4,'AN',1,'2014-11-25 14:07:12','ADMIN','2014-11-25 14:07:12','ADMIN');

-- Alter script DEC-4-2014
INSERT into ss_user.ACCOUNTS_MASTER values(5,"Free Account",1,365,0.0,1,'2014-11-28 04:00:00',"1",'2014-11-28 04:00:00',"1");
INSERT into ss_user.ACCOUNTS_MASTER values(1,"Individual",1,365,35.99,1,'2014-11-28 04:00:00',"1",'2014-11-28 04:00:00',"1");
INSERT into ss_user.ACCOUNTS_MASTER values(2,"Team",30,365,45.99,1,'2014-11-28 04:00:00',"1",'2014-11-28 04:00:00',"1");
INSERT into ss_user.ACCOUNTS_MASTER values(3,"Company",60,365,65.99,1,'2014-11-28 04:00:00',"1",'2014-11-28 04:00:00',"1");
INSERT into ss_user.ACCOUNTS_MASTER values(4,"Enterprise",100,365,99.99,1,'2014-11-28 04:00:00',"1",'2014-11-28 04:00:00',"1");

-- Insert verticals_master table data Feb-2-2015
INSERT INTO `ss_user`.`VERTICALS_MASTER` VALUES (1,'BANKING',1,'2015-02-02 07:00:00','1','2015-02-02 07:00:00','1'),(2,'MORTGAGE',1,'2015-02-02 07:00:00','1','2015-02-02 07:00:00','1'),(3,'REALTOR',1,'2015-02-02 07:00:00','1','2015-02-02 07:00:00','1');

-- Insert crm_master table data Feb-4-2015
INSERT INTO `ss_user`.`CRM_MASTER` (`CRM_MASTER_ID`, `CRM_NAME`, `STATUS`, `CREATED_ON`, `CREATED_BY`, `MODIFIED_ON`, `MODIFIED_BY`) VALUES ('1', 'ENCOMPASS', '1', '2015-02-04 12:30:00', '1', '2015-02-04 12:30:00', '1');
INSERT INTO `ss_user`.`CRM_MASTER` (`CRM_MASTER_ID`, `CRM_NAME`, `STATUS`, `CREATED_ON`, `CREATED_BY`, `MODIFIED_ON`, `MODIFIED_BY`) VALUES ('2', 'DOTLOOP', '1', '2015-02-04 12:30:00', '1', '2015-02-04 12:30:00', '1');
