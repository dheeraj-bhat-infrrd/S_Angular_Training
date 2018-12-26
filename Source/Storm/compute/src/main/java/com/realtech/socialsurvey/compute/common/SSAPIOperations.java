package com.realtech.socialsurvey.compute.common;

import com.realtech.socialsurvey.compute.entities.*;
import com.realtech.socialsurvey.compute.entities.response.BulkWriteErrorVO;
import com.realtech.socialsurvey.compute.entities.response.FtpSurveyResponse;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.entity.SurveyInvitationEmailCountMonth;
import com.realtech.socialsurvey.compute.exception.APIIntegrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * SS-API Operations
 * @author manish
 *
 */
public class SSAPIOperations
{
    private static final Logger LOG = LoggerFactory.getLogger( SSAPIOperations.class );
    private static SSAPIOperations apiOperations;
    
    private static final String AUTH_HEADER = LocalPropertyFileHandler.getInstance()
        .getProperty( ComputeConstants.APPLICATION_PROPERTY_FILE, ComputeConstants.AUTH_HEADER ).orElse( null );


    private SSAPIOperations()
    {}


    public static synchronized SSAPIOperations getInstance()
    {
        if ( apiOperations == null ) {
            apiOperations = new SSAPIOperations();
        }
        return apiOperations;
    }


    /**
     * Get keyword for company id
     * @param companyId
     * @return
     */
    public Optional<List<Keyword>> getKeywordsForCompany( long companyId )
    {
        LOG.info( "Executing getKeywordsForCompany method." );
        Call<List<Keyword>> requestCall = RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationService()
            .getKeywordsForCompanyId( companyId, AUTH_HEADER );
        try {
            Response<List<Keyword>> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "getKeywordsForCompany response {}", response.body() );
            }
            return Optional.of( response.body() );
        } catch ( IOException | APIIntegrationException e ) {
            LOG.error( "getKeywordsForCompany IOException/ APIIntegrationException caught", e );
            return Optional.empty();
        }
    }


    /**
     * Get keyword for company id
     * @param batchSize 
     * @param skipCount 
     * @return
     */
    public Optional<SocialMediaTokensPaginated> getMediaTokensPaginated(int skipCount, int batchSize)
    {
        LOG.info( "Executing getMediaTokensPaginated method." );
        Call<SocialMediaTokensPaginated> requestCall = RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationServiceWithIncreasedTimeOut()
            .getMediaTokensPaginated( skipCount, batchSize, AUTH_HEADER);
        try {
            Response<SocialMediaTokensPaginated> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "getMediaTokens response {}", response.body() );
            }
            return Optional.of( response.body() );
        } catch ( IOException | APIIntegrationException e ) {
            LOG.error( "getMediaTokens IOException/ APIIntegrationException caught", e );
            return Optional.empty();
        }
    }


    /**
     * Save feed api call
     * @param socialPostToMongo
     * @return
     */
    public boolean saveFeedToMongo( SocialResponseObject<?> socialPostToMongo ) throws IOException {
        LOG.info( "Executing saveFeedToMongo method." );
        Call<SocialResponseObject> requestCall = RetrofitApiBuilder.apiBuilderInstance()
            .getSSAPIIntergrationService().saveSocialFeed( socialPostToMongo, AUTH_HEADER );
            Response response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateSavePostToMongoResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "saveFeedToMongo response {}", response.body() );
            }
            return true;
    }

    public Optional<Long> updateSocialPostDuplicateCount( int hash, long comapnyId, String id ) throws IOException {
        LOG.info( "Executing updateSocialPostDuplicateCount method" );
        Call<Long> requestCall = RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationService()
            .updateDuplicateCount( hash, comapnyId, id, AUTH_HEADER);
            Response<Long> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "updateSocialPostDuplicateCount response {}", response.body() );
            }
            return Optional.of( response.body() );
    }

	public List<SurveyInvitationEmailCountMonth> getReceivedCountsMonth(long startDate, long endDate, int startIndex, int batchSize) throws IOException {
		Call<List<SurveyInvitationEmailCountMonth>> request = RetrofitApiBuilder.apiBuilderInstance()
				.getSSAPIIntergrationServiceWithIncreasedTimeOut().getReceivedCountsMonth(startDate,endDate, startIndex, batchSize, AUTH_HEADER);
		Response<List<SurveyInvitationEmailCountMonth>> response = request.execute();
		RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
		return response.body();
		
	}
	

    public boolean saveEmailCountMonthData( List<SurveyInvitationEmailCountMonth> agentEmailCountsMonth )
    {
        Call<Boolean> requestCall = RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationServiceWithIncreasedTimeOut()
            .saveEmailCountMonthData( agentEmailCountsMonth );
        try {
            Response<Boolean> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "response {}", response.body() );
            }
            return true;
        } catch ( IOException | APIIntegrationException e ) {
            LOG.error( "IOException/ APIIntergrationException caught", e );
            return false;
        }
    }


	public List<SurveyInvitationEmailCountMonth> getAllTimeDataForSurveyInvitationMail(int startIndex, int bATCH_SIZE) throws IOException {
		Call<List<SurveyInvitationEmailCountMonth>> request = RetrofitApiBuilder.apiBuilderInstance()
				.getSSAPIIntergrationServiceWithIncreasedTimeOut().getAllTimeDataForSurveyInvitationMail(startIndex,bATCH_SIZE);
		Response<List<SurveyInvitationEmailCountMonth>> response = request.execute();
		RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
		return response.body();
		
	}


	public List<SurveyInvitationEmailCountMonth> getDataForEmailReport(int month, int year, long companyId) throws IOException {
		Call<List<SurveyInvitationEmailCountMonth>> request = RetrofitApiBuilder.apiBuilderInstance()
				.getSSAPIIntergrationServiceWithIncreasedTimeOut().getDataForEmailReport(month,year,companyId);
		Response<List<SurveyInvitationEmailCountMonth>> response = request.execute();
		RetrofitApiBuilder.apiBuilderInstance().validateResponse(response);
		return response.body();
	}


    public Optional<Boolean> updateTokenExpiryAlert( long iden, String fieldToUpdate, boolean value, String profileTypeValue )
    {
        Call<Boolean> requestCall = RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationService().
            updateSocialMediaToken(iden, fieldToUpdate, value, profileTypeValue, AUTH_HEADER);
        Response<Boolean> response = null;
        try {
            response = requestCall.execute();
        } catch ( IOException e ) {
            LOG.error( "Exception while updating tokenExpiryAlert", e.getMessage() );
        }
        RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
        if ( LOG.isTraceEnabled() ) {
            LOG.trace( "updateTokenExpiryAlert response {}", response.body() );
        }
        return Optional.of( response.body() );
    }


    public Optional<List<SocialResponseObject>> getDataForSocialMonitorReport( long companyId, String keyword, long startTime, long endTime, int pageSize,
        int skips ) throws IOException
    {
        Call<List<SocialResponseObject>> requestCall =  RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationServiceWithIncreasedTimeOut().
            getSocialFeedData(companyId, keyword, startTime, endTime, pageSize, skips, AUTH_HEADER);
        Response<List<SocialResponseObject>> response = requestCall.execute();
        return Optional.of( response.body() );
    }


    public Optional<List<SocialResponseObject>> getDataForSocialMonitorReport( long companyId, long startTime, long endTime, int pageSize, int skips )
        throws IOException
    {
        Call<List<SocialResponseObject>> requestCall =  RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationServiceWithIncreasedTimeOut().
            getSocialFeedData(companyId, startTime, endTime, pageSize, skips, AUTH_HEADER);
        Response<List<SocialResponseObject>> response = requestCall.execute();
        return Optional.of( response.body() );
    }
    
    public String processFailedFtpRequest( String errorMessage, TransactionIngestionMessage transactionIngestionMessage, boolean sendOnlyToSocialSurveyAdmin )
        throws IOException
    {
        LOG.info( "method processFailedFtpRequest() called." );

        FailedFtpRequest failedFtpRequest = new FailedFtpRequest();
        failedFtpRequest.setReasonForFailure( errorMessage );
        failedFtpRequest.setTransactionIngestionMessage( transactionIngestionMessage );
        failedFtpRequest.setSendOnlyToSocialSurveyAdmin( sendOnlyToSocialSurveyAdmin );

        Call<String> requestCall = RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationService()
            .manageFtpFileProcessingFailure( failedFtpRequest );

        try {
            Response<String> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "response {}", response.body() );
            }
            return response.body();
        } catch ( IOException | APIIntegrationException e ) {
            LOG.error( "IOException/ APIIntergrationException caught", e );
            throw new IOException( e );
        }
    }
    
    public String processSuccessFtpRequest( long companyId , long ftpId , String s3FileLocation ,FtpSurveyResponse ftpSurveyResponse )
        throws IOException
    {
        LOG.info( "method processSuccessFtpRequest() called." );

        Call<String> requestCall = RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationService().sendCompletionMail(companyId,ftpId,s3FileLocation, ftpSurveyResponse );
        try {
            Response<String> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "response {}", response.body() );
            }
            return response.body();
        } catch ( IOException | APIIntegrationException e ) {
            LOG.error( "IOException/ APIIntergrationException caught", e );
            throw new IOException( e );
        }        
    }


    public Optional<Long> savePostAndUpdateSocialPostDuplicateCount( SocialResponseObject<?> socialPost ) throws IOException
    {
        LOG.info( "Executing savePostAndUpdateSocialPostDuplicateCount method" );
        Call<Long> requestCall = RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationService()
            .savePostAndupdateDuplicateCount( socialPost, AUTH_HEADER );
        Response<Long> response = requestCall.execute();
        RetrofitApiBuilder.apiBuilderInstance().validateSavePostToMongoResponse( response );
        if ( LOG.isTraceEnabled() ) {
            LOG.trace( "updateSocialPostDuplicateCount response {}", response.body() );
        }
        return Optional.of( response.body() );
    }

    public boolean isEmailUnsubscribed( String recipient, long companyId )
    {
        Call<Boolean> request = RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationService()
            .isEmailUnsubscribed( recipient, companyId );
        Response<Boolean> response = null;
        try {
            response = request.execute();
        } catch ( IOException e ) {
            LOG.error( "Exception while fetching email unsubscribed status." );
        }
        if ( response != null ) {
            return response.body();
        }
        return false;
    }

    //checkIfSurveyIsOld
    public String checkIfSurveyEmailIsOld(String recepientEmailId) throws IOException 
    {
    	Call<String> requestCall = RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationService().checkIfSurveyIsOld(recepientEmailId);
    		try {
                Response<String> response = requestCall.execute();
                RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
                if ( LOG.isTraceEnabled() ) {
                    LOG.info( "checkIfSurveyEmailIsOld response {}", response.body() );
                }
                return response.body();
            } catch ( IOException | APIIntegrationException e ) {
                LOG.error( "IOException/ APIIntergrationException caught", e );
                throw new IOException( e );
            }  
    }

    public Optional<List<BulkWriteErrorVO>> bulkInsertToMongo( List<SocialResponseObject> socialPosts ) throws IOException
    {
        LOG.debug( "Executing bulkInsertToMongo method" );
        Call<List<BulkWriteErrorVO>> requestCall = RetrofitApiBuilder.apiBuilderInstance().
            getSSAPIIntergrationServiceWithIncreasedTimeOut().saveSocialFeeds( socialPosts, AUTH_HEADER );
        Response<List<BulkWriteErrorVO>> response = requestCall.execute();
        RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
        if ( LOG.isTraceEnabled() ) {
            LOG.trace( "updateSocialPostDuplicateCount response {}", response.body() );
        }
        return Optional.of( response.body() );
    }

    public Map<String, Map<String, String>> getProfileNameDateDataForWidgetReport( long companyId ) throws IOException
    {
        Call<Map<String, Map<String, String>>> request = RetrofitApiBuilder.apiBuilderInstance()
            .getSSAPIIntergrationServiceWithIncreasedTimeOut().getProfileNameDateDataForWidgetReport( companyId );
        Response<Map<String, Map<String, String>>> response = request.execute();
        RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
        return response.body();
    }


    public List<String> getTemplateDateDataForWidgetReport() throws IOException
    {
        Call<List<String>> request = RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationService().getWidgetScripts();
        Response<List<String>> response = request.execute();
        RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
        return response.body();
    }

    /**
     * Get all the facebook tokens in all hierarchies
     * @param batchSize
     * @param skipCount
     * @return
     */
    public Optional<SocialMediaTokensPaginated> getFbTokensPaginated(int skipCount, int batchSize)
    {
        LOG.info( "Executing getMediaTokensPaginated method." );
        Call<SocialMediaTokensPaginated> requestCall = RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationServiceWithIncreasedTimeOut()
            .getFbTokensPaginated( skipCount, batchSize, AUTH_HEADER);
        try {
            Response<SocialMediaTokensPaginated> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "getMediaTokens response {}", response.body() );
            }
            return Optional.of( response.body() );
        } catch ( IOException | APIIntegrationException e ) {
            LOG.error( "getMediaTokens IOException/ APIIntegrationException caught", e );
            return Optional.empty();
        }
    }


    /**
     * Fetches the branch details for given branch id
     * @param iden
     * @return
     * @throws IOException
     */
    public Optional<BranchVO> getBranchDetails( long iden ) throws APIIntegrationException, IOException
    {
        Call<BranchVO> requestCall =  RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationService().getBranchDetails(iden, AUTH_HEADER);
        Response<BranchVO> response = null;
        response = requestCall.execute();
        RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
        if ( LOG.isTraceEnabled() ) {
            LOG.trace( "getBranchDetails response {}", response.body() );
        }
        return Optional.of( response.body() ) ;
    }


    public Optional<Map<String, Long>> findPrimaryUserProfileByAgentId( long iden ) throws APIIntegrationException, IOException
    {
        final Call<Map<String, Long>> requestCall = RetrofitApiBuilder.apiBuilderInstance()
            .getSSAPIIntergrationService().findPrimaryUserProfileByAgentId( iden, AUTH_HEADER );
        Response<Map<String,Long>> response = null;
        response = requestCall.execute();
        RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
        if ( LOG.isTraceEnabled() ) {
            LOG.trace( "getBranchDetails response {}", response.body() );
        }
        return Optional.of( response.body() ) ;

    }


    public Optional<List<BulkWriteErrorVO>> saveOrUpdateReviews( List<SurveyDetailsVO> surveyDetails ) throws IOException
    {
        final Call<List<BulkWriteErrorVO>> requestCall = RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationService()
            .saveOrUpdateReviews( surveyDetails );
        Response<List<BulkWriteErrorVO>> response = requestCall.execute();
        RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
        if ( LOG.isTraceEnabled() ) {
            LOG.trace( "saveOrUpdateReviews response {}", response.body() );
        }
        return Optional.of( response.body() );
    }

    public List<OrganizationUnitIds> getDetailsFromPlaceId( String placeId ) throws IOException
    {
        Call<List<OrganizationUnitIds>> request = RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationService().getDetailsFromPlaceId(placeId);
        Response<List<OrganizationUnitIds>> response = request.execute();
        RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
        return response.body();
        
    }


    public Optional<Boolean> updateSocialMediaLastFetched( long iden, long current, long previous, String collection,
        String socialMedia )
    {
        Call<Boolean> requestCall = RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationService().
            updateSocialMediaLastFetched(iden, current, previous, collection, socialMedia, AUTH_HEADER);
        Response<Boolean> response = null;
        try {
            response = requestCall.execute();
        } catch ( IOException e ) {
            LOG.error( "Exception while updating socialMediaLastFetched {}", e.getMessage() );
        }
        RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
        if ( LOG.isTraceEnabled() ) {
            LOG.trace( "updateSocialMediaLastFetched response {}", response.body() );
        }
        return Optional.of( response.body() );
    }


    public Optional<Boolean> resetSocialMediaLastFetched( String profile, long iden, String socialMedia )
    {
        Call<Boolean> requestCall = RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationService().
            resetSocialMediaLastFetched(profile, iden, socialMedia, AUTH_HEADER);
        Response<Boolean> response = null;
        try {
            response = requestCall.execute();
        } catch ( IOException e ) {
            LOG.error( "Exception while resetting socialMedialastFetched {}", e.getMessage() );
        }
        RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
        if ( LOG.isTraceEnabled() ) {
            LOG.trace( "resetSocialMediaLastFetched response {}", response.body() );
        }
        return Optional.of( response.body() );
    }
}
