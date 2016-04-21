update ss_user.SURVEY_PRE_INITIATION set status = 10 where agent_id = 0 
	and agent_emailid is not null 
	and customer_email_id is not null
	and status = 3;
	
alter table ss_user.SURVEY_PRE_INITIATION
	add column ERROR_CODE varchar(100) ;
	
update ss_user.SURVEY_PRE_INITIATION set error_code = "CORRUPT_RECORD_AGENT_EMAIL_ID_NULL" where agent_id = 0 
	and agent_emailid is null
	and status = 3;
	
update ss_user.SURVEY_PRE_INITIATION set error_code = "CORRUPT_RECORD_CUSTOMER_EMAIL_ID_NULL" where agent_id = 0 
	and customer_email_id is null
	and status = 3;
	
update ss_user.SURVEY_PRE_INITIATION set error_code = "CORRUPT_RECORD_CUSTOMER_FIRST_NAME_NULL" where agent_id = 0 
	and customer_first_name is null
	and status = 3;

update ss_user.SURVEY_PRE_INITIATION set error_code = "NOT_KNOWN" where agent_id = 0 
	and error_code is null
	and status = 3;

update ss_user.SURVEY_PRE_INITIATION set error_code = "MISMATCH_RECORD_AGENT_NOT_FOUND" where agent_id = 0 
	and status = 10;
