package com.realtech.socialsurvey.core.starter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;


public class CrmDataAgentIdMapper extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( AccountDeactivator.class );

    private SurveyHandler surveyHandler;
    private EmailServices emailServices;
    private UserManagementService userManagementService;
    private String companyAdminEnabled;
    private String adminEmailId;
    private String adminName;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {

        LOG.info( "Executing CrmDataAgentIdMapper" );
        // initialize the dependencies
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        Map<String, Object> corruptRecords = surveyHandler.mapAgentsInSurveyPreInitiation();

        sendCorruptDataFromCrmNotificationMail( corruptRecords );

        LOG.info( "Completed CrmDataAgentIdMapper" );

    }


    private void sendCorruptDataFromCrmNotificationMail( Map<String, Object> corruptRecords )
    {
        @SuppressWarnings ( "unchecked") List<SurveyPreInitiation> unavailableAgents = (List<SurveyPreInitiation>) corruptRecords
            .get( "unavailableAgents" );
        @SuppressWarnings ( "unchecked") List<SurveyPreInitiation> customersWithoutFirstName = (List<SurveyPreInitiation>) corruptRecords
            .get( "customersWithoutFirstName" );
        @SuppressWarnings ( "unchecked") List<SurveyPreInitiation> customersWithoutEmailId = (List<SurveyPreInitiation>) corruptRecords
            .get( "customersWithoutEmailId" );
        @SuppressWarnings ( "unchecked") Set<Long> companies = (Set<Long>) corruptRecords.get( "companies" );

        for ( Long companyId : companies ) {

            String unavailableAgentsDetails = "";
            String customersWithoutFirstNameDetails = "";
            String customersWithoutEmailIdDetails = "";

            for ( SurveyPreInitiation survey : unavailableAgents ) {
                if ( survey.getCompanyId() == companyId )
                    unavailableAgentsDetails += "Source Id " + survey.getSurveySourceId() + ", Source : "
                        + survey.getSurveySource() + ";";
            }
            for ( SurveyPreInitiation survey : customersWithoutFirstName ) {
                if ( survey.getCompanyId() == companyId )
                    customersWithoutFirstNameDetails += "Source Id " + survey.getSurveySourceId() + ", Source : "
                        + survey.getSurveySource() + ";";
            }
            for ( SurveyPreInitiation survey : customersWithoutEmailId ) {
                if ( survey.getCompanyId() == companyId )
                    customersWithoutEmailIdDetails += "Source Id " + survey.getSurveySourceId() + ", Source : "
                        + survey.getSurveySource() + ";";
            }
            try {
                if ( !unavailableAgentsDetails.isEmpty() || !customersWithoutFirstNameDetails.isEmpty()
                    || !customersWithoutEmailIdDetails.isEmpty() ) {
                    if ( companyAdminEnabled == "1" ) {
                        User companyAdmin = userManagementService.getCompanyAdmin( companyId );
                        if ( companyAdmin != null ) {
                            emailServices.sendCorruptDataFromCrmNotificationMail( companyAdmin.getFirstName(),
                                companyAdmin.getLastName(), companyAdmin.getEmailId(), unavailableAgentsDetails,
                                customersWithoutFirstNameDetails, customersWithoutEmailIdDetails );
                        }
                    } else {
                        emailServices.sendCorruptDataFromCrmNotificationMail( adminName, "", adminEmailId,
                            unavailableAgentsDetails, customersWithoutFirstNameDetails, customersWithoutEmailIdDetails );
                    }
                }
            } catch ( InvalidInputException | UndeliveredEmailException e ) {
                LOG.error( "Exception caught in sendCorruptDataFromCrmNotificationMail() while sending mail to company admin" );
            }
        }
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        surveyHandler = (SurveyHandler) jobMap.get( "surveyHandler" );
        emailServices = (EmailServices) jobMap.get( "emailServices" );
        userManagementService = (UserManagementService) jobMap.get( "userManagementService" );
        companyAdminEnabled = (String) jobMap.get( "companyAdminEnabled" );
        adminEmailId = (String) jobMap.get( "adminEmailId" );
        adminName = (String) jobMap.get( "adminName" );
    }
}
