-- Alter script DEC-1-2014
INSERT INTO `ss_user`.`PROFILES_MASTER` VALUES (10,'NP',1, NOW(),'ADMIN', NOW(),'ADMIN'),(1,'CA',1,NOW(),'ADMIN',NOW(),'ADMIN'),(2,'RA',1,NOW(),'ADMIN',NOW(),'ADMIN'),(3,'BA',1,NOW(),'ADMIN',NOW(),'ADMIN'),(4,'AN',1,NOW(),'ADMIN',NOW(),'ADMIN');

-- Alter script DEC-4-2014
INSERT into ss_user.ACCOUNTS_MASTER values(1,"Individual",1,365,35.99,1,NOW(),"1",NOW(),"1");
INSERT into ss_user.ACCOUNTS_MASTER values(2,"Team",30,365,45.99,1,NOW(),"1",NOW(),"1");
INSERT into ss_user.ACCOUNTS_MASTER values(3,"Company",60,365,65.99,1,NOW(),"1",NOW(),"1");
INSERT into ss_user.ACCOUNTS_MASTER values(4,"Enterprise",-1,365,99.99,1,NOW(),"1",NOW(),"1");
INSERT into ss_user.ACCOUNTS_MASTER values(5,"Free Account",1,365,0.0,1,NOW(),"1",NOW(),"1");

-- Insert verticals_master table data Feb-2-2015
INSERT INTO `ss_user`.`VERTICALS_MASTER` VALUES (-1,'CUSTOM',1,NOW(),'1',NOW(),'1'),(1,'MORTGAGE',1,NOW(),'1',NOW(),'1');

-- Inserting default values into COMPANY
INSERT INTO `ss_user`.`COMPANY` (`COMPANY_ID`, `COMPANY`,`VERTICAL_ID`,`IS_REGISTRATION_COMPLETE`, `STATUS`, `CREATED_ON`, `CREATED_BY`, `MODIFIED_ON`, `MODIFIED_BY`) VALUES ('1', 'Default',-1, 1, '1', NOW(), 'ADMIN', NOW(), 'ADMIN');

-- Insert crm_master table data Feb-4-2015
INSERT INTO `ss_user`.`CRM_MASTER` (`CRM_MASTER_ID`, `CRM_NAME`, `STATUS`, `CREATED_ON`, `CREATED_BY`, `MODIFIED_ON`, `MODIFIED_BY`) VALUES ('1', 'ENCOMPASS', '1', NOW(), '1', NOW(), '1');
-- INSERT INTO `ss_user`.`CRM_MASTER` (`CRM_MASTER_ID`, `CRM_NAME`, `STATUS`, `CREATED_ON`, `CREATED_BY`, `MODIFIED_ON`, `MODIFIED_BY`) VALUES ('2', 'DOTLOOP', '1', '2015-02-04 12:30:00', '1', '2015-02-04 12:30:00', '1');
