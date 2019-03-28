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
import com.realtech.socialsurvey.core.entities.FacebookToken;
import com.realtech.socialsurvey.core.entities.LinkedInToken;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.social.SocialMediaExceptionHandler;


@Component ( "socialMediaTokenExpiryScheduler")
public class SocialMediaTokenExpiryScheduler extends QuartzJobBean
{
    private static final Logger LOG = LoggerFactory.getLogger( SocialMediaTokenExpiryScheduler.class );

    private BatchTrackerService batchTrackerService;
    private EmailServices emailServices;
    private OrganizationManagementService organizationManagementService;
    private SocialMediaExceptionHandler socialMediaExceptionHandler;
    private SocialManagementService socialManagementService;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        try {
            LOG.info( "Executing SocialMediaTokenExpiryScheduler" );

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
                batchTrackerService.sendMailToAdminRegardingBatchError( CommonConstants.BATCH_NAME_SOCIAL_MEDIA_TOKEN_EXPIRY_SCHEDULER,
                    System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in social media token expiry scheduler" );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report execption mail to admin " );
            }
        }
    }


    /**
     * 
     * @param collectionName
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    private void startProcessingForCollection( String collectionName ) throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Inside method startProcessingForCollection started." );

        List<OrganizationUnitSettings> orgUnitList = organizationManagementService
            .fetchUnitSettingsForSocialMediaTokens( collectionName );
        for ( OrganizationUnitSettings orgUnit : orgUnitList ) {
            SocialMediaTokens smTokens = orgUnit.getSocialMediaTokens();

            if ( smTokens != null ) {
                
                if ( smTokens.getLinkedInV2Token() != null
                    && smTokens.getLinkedInV2Token().getLinkedInAccessTokenExpiresIn() != 0L ) {
                    if ( socialManagementService.checkLinkedInV2TokenExpiry( orgUnit, collectionName ) ) {
                        LinkedInToken linkedInToken = smTokens.getLinkedInV2Token();
                        if ( !linkedInToken.isTokenExpiryAlertSent() ) {
                            //send alert mail to entity 
                            String emailId = socialMediaExceptionHandler.generateAndSendSocialMedialTokenExpiryMail( orgUnit,
                                collectionName, CommonConstants.LINKEDIN_SOCIAL_SITE );
                            //update alert mail detail in linkedin token
                            if ( emailId != null ) {
                                linkedInToken.setTokenExpiryAlertEmail( emailId );
                                linkedInToken.setTokenExpiryAlertSent( true );
                                linkedInToken.setTokenExpiryAlertTime( new Date() );
                                socialManagementService.updateLinkedinV2Token( collectionName, orgUnit.getIden(), linkedInToken );
                            }
                        }
                    }
                } else if ( smTokens.getLinkedInToken() != null
                    && smTokens.getLinkedInToken().getLinkedInAccessTokenExpiresIn() != 0L ) {
                    if ( socialManagementService.checkLinkedInTokenExpiry( orgUnit, collectionName ) ) {
                        LinkedInToken linkedInToken = smTokens.getLinkedInToken();
                        if ( !linkedInToken.isTokenExpiryAlertSent() ) {
                            //send alert mail to entity 
                            String emailId = socialMediaExceptionHandler.generateAndSendSocialMedialTokenExpiryMail( orgUnit,
                                collectionName, CommonConstants.LINKEDIN_SOCIAL_SITE );
                            //update alert mail detail in linkedin token
                            if ( emailId != null ) {
                                linkedInToken.setTokenExpiryAlertEmail( emailId );
                                linkedInToken.setTokenExpiryAlertSent( true );
                                linkedInToken.setTokenExpiryAlertTime( new Date() );
                                socialManagementService.updateLinkedinToken( collectionName, orgUnit.getIden(), linkedInToken );
                            }
                        }

                    }
                }

                if ( smTokens.getFacebookToken() != null ) {
                    if ( socialManagementService.checkFacebookTokenExpiry( orgUnit, collectionName ) ) {
                        FacebookToken facebookToken = smTokens.getFacebookToken();
                        if ( !facebookToken.isTokenExpiryAlertSent() ) {
                            LOG.debug( "Alert Mail hasn't send to sending alert mail for entity" );
                            String emailId = socialMediaExceptionHandler.generateAndSendSocialMedialTokenExpiryMail( orgUnit,
                                collectionName, CommonConstants.FACEBOOK_SOCIAL_SITE );
                            //update alert detail in token
                            if ( emailId != null ) {
                                facebookToken.setTokenExpiryAlertEmail( emailId );
                                facebookToken.setTokenExpiryAlertSent( true );
                                facebookToken.setTokenExpiryAlertTime( new Date() );
                                socialManagementService.updateFacebookToken( collectionName, orgUnit.getIden(), facebookToken );
                            }
                        }
                    }
                }

            }
        }

        LOG.debug( "Inside method startProcessingForCollection finished." );

    }


    private boolean checkTokenExpiry( long tokenCreatedOn, long expirySeconds )
    {
        long expiryHours = expirySeconds / 3600;
        Calendar cal = Calendar.getInstance();

        // adding 7 days to created on time
        cal = Calendar.getInstance();
        cal.add( Calendar.HOUR, 168 );
        Date currentDatePlusSeven = cal.getTime();

        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis( tokenCreatedOn );

        cal2.add( Calendar.HOUR, (int) expiryHours );
        Date expiresOn = cal2.getTime();

        if ( currentDatePlusSeven.after( expiresOn ) )
            return true;

        return false;
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        batchTrackerService = (BatchTrackerService) jobMap.get( "batchTrackerService" );
        emailServices = (EmailServices) jobMap.get( "emailServices" );
        organizationManagementService = (OrganizationManagementService) jobMap.get( "organizationManagementService" );
        socialMediaExceptionHandler = (SocialMediaExceptionHandler) jobMap.get( "socialMediaExceptionHandler" );
        socialManagementService = (SocialManagementService) jobMap.get( "socialManagementService" );
    }
}
