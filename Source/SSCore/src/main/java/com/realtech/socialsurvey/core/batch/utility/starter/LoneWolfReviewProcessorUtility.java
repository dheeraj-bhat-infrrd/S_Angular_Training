package com.realtech.socialsurvey.core.batch.utility.starter;

import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.crmbatchtracker.CRMBatchTrackerService;
import com.realtech.socialsurvey.core.services.crmbatchtrackerhistory.CRMBatchTrackerHistoryService;
import com.realtech.socialsurvey.core.services.lonewolf.LoneWolfIntegrationService;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.starter.LoneWolfReviewProcessor;
import com.realtech.socialsurvey.core.utils.LoneWolfRestUtils;


@Component
public class LoneWolfReviewProcessorUtility implements BatchUtilityProcessor
{

    @Autowired
    private LoneWolfReviewProcessor processor;

    @Autowired
    private BatchTrackerService batchTrackerService;

    @Autowired
    private CRMBatchTrackerService crmBatchTrackerService;

    @Autowired
    private CRMBatchTrackerHistoryService crmBatchTrackerHistoryService;

    @Autowired
    private EmailServices emailServices;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private SurveyHandler surveyHandler;

    @Autowired
    private Utils utils;

    @Autowired
    private LoneWolfIntegrationService loneWolfIntegrationService;

    @Autowired
    private LoneWolfRestUtils loneWolfRestUtils;

    @Value ( "${APPLICATION_ADMIN_EMAIL}")
    private String applicationAdminEmail;

    @Value ( "${APPLICATION_ADMIN_NAME}")
    private String applicationAdminName;

    @Value ( "${MASK_EMAIL_ADDRESS}")
    private String maskEmail;

    @Value ( "${LONEWOLF_API_TOKEN}")
    private String apiToken;

    @Value ( "${LONEWOLF_SECRET_KEY}")
    private String secretKey;


    @Override
    public void execute()
    {
        processor.initializeDependencies( getJobDataMap() );
        processor.executeLoneWolfFeed();
    }


    private JobDataMap getJobDataMap()
    {
        JobDataMap jobMap = new JobDataMap();
        jobMap.put( "batchTrackerService", batchTrackerService );
        jobMap.put( "crmBatchTrackerService", crmBatchTrackerService );
        jobMap.put( "crmBatchTrackerHistoryService", crmBatchTrackerHistoryService );
        jobMap.put( "emailServices", emailServices );
        jobMap.put( "organizationManagementService", organizationManagementService );
        jobMap.put( "userManagementService", userManagementService );
        jobMap.put( "surveyHandler", surveyHandler );
        jobMap.put( "utils", utils );
        jobMap.put( "maskEmail", maskEmail );
        jobMap.put( "apiToken", apiToken );
        jobMap.put( "secretKey", secretKey );
        jobMap.put( "applicationAdminEmail", applicationAdminEmail );
        jobMap.put( "applicationAdminName", applicationAdminName );
        jobMap.put( "loneWolfIntegrationService", loneWolfIntegrationService );
        jobMap.put( "loneWolfRestUtils", loneWolfRestUtils );
        return jobMap;
    }
}
