package com.realtech.socialsurvey.compute.common;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.realtech.socialsurvey.compute.entities.EmailMessage;
import com.realtech.socialsurvey.compute.entities.SmsInfo;
import com.realtech.socialsurvey.compute.entities.SolrEmailMessageWrapper;
import com.realtech.socialsurvey.compute.entities.SolrSmsWrapper;
import com.realtech.socialsurvey.compute.entities.UserEvent;
import com.realtech.socialsurvey.compute.entities.request.SolrAdd;
import com.realtech.socialsurvey.compute.entities.request.SolrRequest;
import com.realtech.socialsurvey.compute.entities.response.SOLRResponse;
import com.realtech.socialsurvey.compute.entities.response.SOLRResponseObject;
import com.realtech.socialsurvey.compute.exception.APIIntegrationException;
import com.realtech.socialsurvey.compute.exception.SolrProcessingException;
import com.realtech.socialsurvey.compute.utils.ChararcterUtils;

import retrofit2.Call;
import retrofit2.Response;



/**
 * API Operations
 * @author nishit
 *
 */
public class APIOperations
{
    private static final Logger LOG = LoggerFactory.getLogger( APIOperations.class );
    private static APIOperations apiOperations;


    private APIOperations()
    {}


    public static synchronized APIOperations getInstance()
    {
        if ( apiOperations == null ) {
            apiOperations = new APIOperations();
        }
        return apiOperations;
    }


    /**
     * Searches for email message from SOLR based on UUID
     * @param emailMessage
     * @return
     */
    public Optional<SolrEmailMessageWrapper> getEmailMessageFromSOLR( EmailMessage emailMessage )
    {
        LOG.debug( "Getting the message based on UUID." );
        return getUniqueEmailMessage( "*:*", "randomUUID:" + emailMessage.getRandomUUID() );
    }
    
    /**
     * Searches for sms from SOLR based on UUID
     * @param smsInfo
     * @return
     */
    public Optional<SolrSmsWrapper> getSmsFromSOLR( SmsInfo smsInfo )
    {
        LOG.debug( "Getting the sms based on UUID." );
        return getUniqueSms( "*:*", "randomUUID:" + smsInfo.getRandomUUID() );
    }


    /**
     * Searches for email message from SOLR based on sendgridMessageId
     * @param emailMessage
     * @return
     */
    public Optional<SolrEmailMessageWrapper> getEmailMessageFromSOLRBySendgridMsgId( String sendgridMessageId )
    {
        LOG.debug( "Getting the message based on sendgrid message id" );
        return getUniqueEmailMessage( "*:*", "sendgridMessageId:" + ChararcterUtils.escapeSOLRQueryChars( sendgridMessageId ) );
    }


    /**
     * Searches for email message from SOLR based on UUID
     * @param uuid
     * @return
     */
    public Optional<SolrEmailMessageWrapper> getEmailMessageFromSOLRByRandomUUID( String uuid )
    {
        LOG.debug( "Getting the message based on random UUID" );
        return getUniqueEmailMessage( "*:*", "randomUUID:" + uuid );
    }


    private Optional<SolrEmailMessageWrapper> getUniqueEmailMessage( String query, String fieldQuery )
    {
        LOG.debug( "Getting the unique email b" );
        SolrEmailMessageWrapper solrEmailMessageWrapper = null;
        Call<SOLRResponseObject<SolrEmailMessageWrapper>> requestCall = RetrofitApiBuilder.apiBuilderInstance()
            .getSolrAPIIntergrationService().getEmailMessage( query, fieldQuery, 0, 1, "json" ); // start is 0 and rows 1 for unique messages
        try {
            Response<SOLRResponseObject<SolrEmailMessageWrapper>> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "Unique email message response {}", response.body() );
            }
            if ( response.body().getResponse().getNumFound() > 0 ) {
                solrEmailMessageWrapper = response.body().getResponse().getDocs().get( 0 );
            }
        } catch ( IOException | APIIntegrationException e ) {
            LOG.error( "getUniqueEmailMessage: IOException/ APIIntegrationException caught", e );
        }
        if ( solrEmailMessageWrapper != null ) {
            return Optional.of( solrEmailMessageWrapper );
        } else {
            return Optional.empty();
        }
    }
    
    private Optional<SolrSmsWrapper> getUniqueSms( String query, String fieldQuery )
    {
        LOG.debug( "Getting the unique sms" );
        SolrSmsWrapper solrSmsWrapper = null;
        Call<SOLRResponseObject<SolrSmsWrapper>> requestCall = RetrofitApiBuilder.apiBuilderInstance()
            .getSolrAPIIntergrationService().getSms( query, fieldQuery, 0, 1, "json" ); // start is 0 and rows 1 for unique messages
        try {
            Response<SOLRResponseObject<SolrSmsWrapper>> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "Unique sms response {}", response.body() );
            }
            if ( response.body().getResponse().getNumFound() > 0 ) {
                solrSmsWrapper = response.body().getResponse().getDocs().get( 0 );
            }
        } catch ( IOException | APIIntegrationException e ) {
            LOG.error( "getUniqueSms: IOException/ APIIntegrationException caught", e );
        }
        if ( solrSmsWrapper != null ) {
            return Optional.of( solrSmsWrapper );
        } else {
            return Optional.empty();
        }
    }
    
    /**
     * Searches for sms from SOLR based on twilioSmsId
     * @param twilioSmsId
     * @return
     */
    public Optional<SolrSmsWrapper> getSmsFromSOLRByTwilioSmsId( String twilioSmsId )
    {
        LOG.debug( "Getting the sms on twilioSmsId" );
        return getUniqueSms( "*:*", "twilioSmsId:" + twilioSmsId );
    }

    /**
     * Gets all the email messages of given field using pagination
     * @param query
     * @param fieldQuery
     * @return list of emails
     */
    public SOLRResponse<SolrEmailMessageWrapper> getEmailMessagesFromSolr(String query, String fieldQuery, String fieldList, int batchSize , int pageNum)
            throws IOException, APIIntegrationException
    {
        Call<SOLRResponseObject<SolrEmailMessageWrapper>> requestCall ;
        Response<SOLRResponseObject<SolrEmailMessageWrapper>> response ;
        SOLRResponse<SolrEmailMessageWrapper> solrResponse = null ;
                requestCall = RetrofitApiBuilder.apiBuilderInstance()
                        .getSolrAPIIntergrationService().getEmailMessage( query, fieldQuery, fieldList, (pageNum-1) * batchSize, batchSize, "json");
                response =  requestCall.execute();
                RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
                if ( LOG.isTraceEnabled() )
                    LOG.trace( "Email messages response {}", response.body() );
                solrResponse = response.body().getResponse();
        return solrResponse;
    }

    /**
     * Inserts the eamil message with extra meta data in SOLR
     * @param emailMessageWrapper
     * @return
     */
    public boolean postEmailToSolr( SolrEmailMessageWrapper emailMessageWrapper )
    {
        SolrRequest<SolrEmailMessageWrapper> solrRequest = new SolrRequest<>();
        SolrAdd<SolrEmailMessageWrapper> solrAdd = new SolrAdd<>();
        solrAdd.setDoc( emailMessageWrapper );
        solrRequest.setAdd( solrAdd );
        Call<SOLRResponseObject<SolrEmailMessageWrapper>> requestCall = RetrofitApiBuilder.apiBuilderInstance()
            .getSolrAPIIntergrationService().postEmail( solrRequest, true );
        try {
            Response<SOLRResponseObject<SolrEmailMessageWrapper>> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "post email to solr response {}", response.body() );
            }
            return true;
        } catch ( IOException | APIIntegrationException e ) {
            LOG.error( "postEmailToSolr: IOException/ APIIntegrationException caught", e );
            throw new SolrProcessingException( "Exception while posting email message to solr", e );
        }
    }
    
    /**
     * Inserts the sms with extra meta data in SOLR
     * @param solrSmsWrapper
     * @return
     */
    public boolean postSmsToSolr( SolrSmsWrapper solrSmsWrapper )
    {
        SolrRequest<SolrSmsWrapper> solrRequest = new SolrRequest<>();
        SolrAdd<SolrSmsWrapper> solrAdd = new SolrAdd<>();
        solrAdd.setDoc( solrSmsWrapper );
        solrRequest.setAdd( solrAdd );
        Call<SOLRResponseObject<SolrSmsWrapper>> requestCall = RetrofitApiBuilder.apiBuilderInstance()
            .getSolrAPIIntergrationService().postSms( solrRequest, true );
        try {
            Response<SOLRResponseObject<SolrSmsWrapper>> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "post sms to solr response {}", response.body() );
            }
            return true;
        } catch ( IOException | APIIntegrationException e ) {
            LOG.error( "postSmsToSolr: IOException/ APIIntegrationException caught", e );
            throw new SolrProcessingException( "Exception while posting sms to solr", e );
        }
    }


	public JsonObject getEmailCounts(String query, String fieldQuery, boolean isFacet, String facetField,
			List<String> facetPivots, int facetLimit, int facetMinCount) {
        Response<JsonObject> response = null;
        Call<JsonObject> requestCall = RetrofitApiBuilder.apiBuilderInstance()
                        .getSolrAPIIntergrationServiceWithIncreasedTimeout()
                        .getEmailCounts(query, fieldQuery, 0, "json", isFacet, facetField, facetPivots,
                        		facetLimit, facetMinCount);
                try {
					response =  requestCall.execute();
				} catch (IOException e) {
					LOG.error("Exception occured while fetching the data from solr.",e);
				}
                RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
                if ( LOG.isTraceEnabled() )
                    LOG.trace( "Email messages response {}", response.body() );
        return response.body();
	}
	
	
    /**
     * Inserts the user event in SOLR
     * @param UserEvent
     * @return
     */
    public boolean saveUserEventToSolr( UserEvent userEvent )
    {
        SolrRequest<UserEvent> solrRequest = new SolrRequest<>();
        SolrAdd<UserEvent> solrAdd = new SolrAdd<>();
        solrAdd.setDoc( userEvent );
        solrRequest.setAdd( solrAdd );
        Call<SOLRResponseObject<UserEvent>> requestCall = RetrofitApiBuilder.apiBuilderInstance()
            .getSolrAPIIntergrationService().saveUserEvent( solrRequest, true );
        try {
            Response<SOLRResponseObject<UserEvent>> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "saved user event to solr : {}", response.body() );
            }
            return true;
        } catch ( IOException | APIIntegrationException e ) {
            LOG.error( "saveUserEventToSolr: IOException/ APIIntergrationException caught", e );
            throw new SolrProcessingException( "Exception while saving user event to solr", e );
        }
    }
}
