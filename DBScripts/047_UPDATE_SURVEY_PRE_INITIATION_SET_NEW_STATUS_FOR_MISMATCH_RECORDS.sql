update ss_user.SURVEY_PRE_INITIATION set STATUS = 10 where AGENT_ID = 0 
	and (AGENT_EMAILID is not null AND AGENT_EMAILID <> "")
	and (CUSTOMER_EMAIL_ID is not null AND CUSTOMER_EMAIL_ID <> "")
	and (`CUSTOMER_FIRST_NAME` IS NOT NULL AND CUSTOMER_FIRST_NAME <> "")
	and STATUS = 3;
	
alter table ss_user.SURVEY_PRE_INITIATION
	add column ERROR_CODE varchar(100) ;
	
update ss_user.SURVEY_PRE_INITIATION set error_code = "CORRUPT_RECORD_AGENT_EMAIL_ID_NULL" where (AGENT_EMAILID IS NULL OR AGENT_EMAILID = "")
	and STATUS = 3;
	
update ss_user.SURVEY_PRE_INITIATION set error_code = "CORRUPT_RECORD_CUSTOMER_EMAIL_ID_NULL" where  (`CUSTOMER_EMAIL_ID` is null OR CUSTOMER_EMAIL_ID = "")
	and STATUS = 3;
	
update ss_user.SURVEY_PRE_INITIATION set error_code = "CORRUPT_RECORD_CUSTOMER_FIRST_NAME_NULL" where (`CUSTOMER_FIRST_NAME` is null OR CUSTOMER_FIRST_NAME = "")
	and STATUS = 3;

update ss_user.SURVEY_PRE_INITIATION set error_code = "NOT_KNOWN" where error_code is null
	and STATUS = 3;

update ss_user.SURVEY_PRE_INITIATION set error_code = "MISMATCH_RECORD_AGENT_NOT_FOUND" where agent_id = 0 
	and status = 10;
