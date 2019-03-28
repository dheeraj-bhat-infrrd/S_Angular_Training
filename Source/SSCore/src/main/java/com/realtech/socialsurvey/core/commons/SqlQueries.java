package com.realtech.socialsurvey.core.commons;

public interface SqlQueries
{
    /**
     * This query is used to retreive the necessary information required to display on the admin dashboard
     */
    String COMPANY_STATISTICS_QUERY = "select c.company_id as companyId, c.user_count as userCount,"
        + " c.verified_user_count as verifiedUserCount, " + "c.region_count as regionCount, c.branch_count as branchCount,"
        + " c.verified_gmb as branchVerifiedGmb, c.missing_gmb as branchMissingGmb, "
        + " c.region_verified_gmb as regionVerifiedGmb, c.region_missing_gmb as regionMissingGmb,"
        + " c.twitter_connection_count as twitterConnectionCount,"
        + " c.facebook_connection_count as facebookConnectionCount,"
        + " c.linkedin_connection_count as linkedinConnectionCount, c.mismatch_count as mismatchCount,"
        + " c.missing_photo_count as missingPhotoCountForUsers, ifnull(spi.mismatchCount_90days , 0) as mismatchCount90Days, "
        + " ifnull(sds.completed_survey_count_90days, 0) as completedSurveyCount90Days,"
        + " ifnull(oc.totrev, 0) as totalReviews, ifnull(oc.completedall, 0) as completedSurveyCountAllTime,"
        + " ifnull(ocm.completed,0) as completedSurveyCountThisMonth,"
        + " ifnull(ocy.completed,0) as completedSurveyCountThisYear"
        + " from company_details_report c left join "
        + " (select s.COMPANY_ID, count(s.SURVEY_PRE_INITIATION_ID) as"
        + " mismatchCount_90days from survey_pre_initiation s where s.COMPANY_ID = :companyId  "
        + " and s.CREATED_ON_EST >= DATE_ADD(NOW(),INTERVAL -90 DAY) and s.STATUS = 10 ) spi "
        + " on c.COMPANY_ID = spi.COMPANY_ID left join "
        + " (select sd.COMPANY_ID, count(distinct(sd.SURVEY_DETAILS_ID)) as completed_survey_count_90days from "
        + " survey_details sd where sd.COMPANY_ID = :companyId"
        + " and sd.SURVEY_COMPLETED_DATE_EST >= DATE_ADD(NOW(),INTERVAL -90 DAY) "
        + " and sd.STAGE = -1 ) sds on c.COMPANY_ID = sds.COMPANY_ID left join ("
        + " select o.COMPANY_ID, o.total_reviews as totrev, o.completed as completedall from"
        + " overview_company o where o.COMPANY_ID = :companyId) oc on c.COMPANY_ID = oc.COMPANY_ID left join ("
        + " select m.company_id, m.completed as completed from overview_company_month m where m.company_id = :companyId  and"
        + " m.month = month(curdate())and m.year = year(curdate())) ocm on c.COMPANY_ID = ocm.company_id"
        + " left join (select y.company_id, y.completed as completed from overview_company_year y where"
        + " y.company_id = :companyId  and y.year = year(curdate())) ocy on c.COMPANY_ID = ocy.company_id"
        + " where c.COMPANY_ID = :companyId";


    /**
     * Query for fetching all the active ss-admins
     */
     String SS_ADMINS_QUERY = "select USER_ID as userId, CONCAT(FIRST_NAME, ( CASE WHEN LAST_NAME IS NOT NULL THEN CONCAT (' ', LAST_NAME) ELSE '' END)) as fullName, EMAIL_ID as emailId from USERS "
         + "where USER_ID in (select USER_ID from USER_PROFILE where PROFILES_MASTER_ID = 5 and STATUS = 1 and USER_ID > 0 and EMAIL_ID IS NOT NULL)";
     
     /**
      * Query for fetching fullname
      */
     String USER_NAME = "select concat(FIRST_NAME, ( case when LAST_NAME is not null then concat (' ', LAST_NAME) else '' end)) from USERS where USER_ID = ?";
}
