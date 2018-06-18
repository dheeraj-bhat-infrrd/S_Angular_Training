package com.realtech.socialsurvey.compute.common;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.entities.FailedFtpRequest;
import com.realtech.socialsurvey.compute.entities.Keyword;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokensPaginated;
import com.realtech.socialsurvey.compute.entities.TransactionIngestionMessage;
import com.realtech.socialsurvey.compute.entities.response.FtpSurveyResponse;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.entity.SurveyInvitationEmailCountMonth;
import com.realtech.socialsurvey.compute.exception.APIIntegrationException;

import retrofit2.Call;
import retrofit2.Response;


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
}
