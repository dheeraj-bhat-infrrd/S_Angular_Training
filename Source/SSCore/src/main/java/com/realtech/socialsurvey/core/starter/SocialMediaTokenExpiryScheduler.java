package com.realtech.socialsurvey.core.starter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;


@Component ( "socialMediaTokenExpiryScheduler")
public class SocialMediaTokenExpiryScheduler extends QuartzJobBean
{
    private static final Logger LOG = LoggerFactory.getLogger( SocialMediaTokenExpiryScheduler.class );

    private BatchTrackerService batchTrackerService;
    private EmailServices emailServices;
    private OrganizationManagementService organizationManagementService;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        try {
            LOG.info( "Executing lonewolf review processor" );

            initializeDependencies( jobExecutionContext.getMergedJobDataMap() );

            // update last start time
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_SOCIAL_MEDIA_TOKEN_EXPIRY_SCHEDULER,
                CommonConstants.BATCH_NAME_SOCIAL_MEDIA_TOKEN_EXPIRY_SCHEDULER );

            startProcessingForCollection( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
            startProcessingForCollection( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
            startProcessingForCollection( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
            startProcessingForCollection( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

            //updating last run time for batch in database
            batchTrackerService
                .updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_SOCIAL_MEDIA_TOKEN_EXPIRY_SCHEDULER );
        } catch ( Exception e ) {
            LOG.error( "Error in social media token expiry scheduler", e );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType(
                    CommonConstants.BATCH_TYPE_SOCIAL_MEDIA_TOKEN_EXPIRY_SCHEDULER, e.getMessage() );

                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError( CommonConstants.BATCH_NAME_LONE_WOLF_REVIEW_PROCESSOR,
                    System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in social media token expiry scheduler" );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report execption mail to admin " );
            }
        }
    }


    private void startProcessingForCollection( String collectionName ) throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Inside method startProcessingForCollection started." );

        List<OrganizationUnitSettings> orgUnitList = organizationManagementService
            .fetchUnitSettingsForSocialMediaTokens( collectionName );
        for ( OrganizationUnitSettings orgUnit : orgUnitList ) {
            SocialMediaTokens smTokens = orgUnit.getSocialMediaTokens();

            if ( smTokens != null && smTokens.getLinkedInToken() != null
                && smTokens.getLinkedInToken().getLinkedInAccessTokenExpiresIn() != 0L ) {
                long tokenCreatedOn = smTokens.getLinkedInToken().getLinkedInAccessTokenCreatedOn();
                long expirySeconds = smTokens.getLinkedInToken().getLinkedInAccessTokenExpiresIn();
                if ( checkTokenExpiry( tokenCreatedOn, expirySeconds ) ) {
                    String emailId = "test@test.com";
                    String name = "Linkedin User";
                    sendTokenExpiryEmail( collectionName, name, emailId );
                }

            }
            if ( smTokens != null && smTokens.getFacebookToken() != null
                && smTokens.getFacebookToken().getFacebookAccessTokenExpiresOn() != 0L ) {
                long tokenCreatedOn = smTokens.getFacebookToken().getFacebookAccessTokenCreatedOn();
                long expirySeconds = smTokens.getFacebookToken().getFacebookAccessTokenExpiresOn();
                if ( checkTokenExpiry( tokenCreatedOn, expirySeconds ) ) {
                    String emailId = "test@test.com";
                    String name = "Facebook User";
                    sendTokenExpiryEmail( collectionName, name, emailId );
                }
            }
        }
        
        LOG.debug( "Inside method startProcessingForCollection finished." );

    }


    private void sendTokenExpiryEmail( String collectionName, String displayName, String emailId )
    {
        String errorMsg = "";
        errorMsg += "<br>" + "Your social media token has expired." + "<br><br>";
        LOG.info( "Sending bug mail to admin" );
        try {
            emailServices.sendSocialMediaTokenExpiryEmail( displayName, errorMsg, emailId );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            LOG.error( "error while sending report bug mail to admin ", e );
        }
    }


    private boolean checkTokenExpiry( long tokenCreatedOn, long expirySeconds )
    {
        long expiryHours = expirySeconds / 3600;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis( tokenCreatedOn );
        Date createdOn = cal.getTime();

        // adding 7 days to created on time
        cal.add( Calendar.HOUR, 168 );
        Date createdOnPlusSeven = cal.getTime();

        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis( createdOn.getTime() );

        cal2.add( Calendar.HOUR, (int) expiryHours );
        Date expiresOn = cal2.getTime();

        if ( createdOnPlusSeven.after( expiresOn ) )
            return true;

        return false;
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        batchTrackerService = (BatchTrackerService) jobMap.get( "batchTrackerService" );
        emailServices = (EmailServices) jobMap.get( "emailServices" );
        organizationManagementService = (OrganizationManagementService) jobMap.get( "organizationManagementService" );
    }
}
