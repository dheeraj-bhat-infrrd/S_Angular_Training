package com.realtech.socialsurvey.compute.common;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.entities.Keyword;
import com.realtech.socialsurvey.compute.entities.SocialPost;
import com.realtech.socialsurvey.compute.entity.SurveyInvitationEmailCountMonth;
import com.realtech.socialsurvey.compute.services.api.APIIntergrationException;

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
        Call<List<Keyword>> requestCall = RetrofitApiBuilder.apiBuilderInstance()
            .getSSAPIIntergrationService().getKeywordsForCompanyId( companyId );
        try {
            Response<List<Keyword>> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "response {}", response.body() );
            }
            return Optional.of(response.body());
        } catch ( IOException | APIIntergrationException e ) {
            LOG.error( "IOException/ APIIntergrationException caught", e );
            return Optional.empty();
        }
    }
    
    /**
     * Save feed api call
     * @param companyId
     * @return
     */
    public boolean saveFeedToMongo(SocialPost socialPostToMongo )
    {
        LOG.info( "Executing saveFeedToMongo method." );
        Call<SocialPost> requestCall = RetrofitApiBuilder.apiBuilderInstance()
            .getSSAPIIntergrationService().saveSocialFeed( socialPostToMongo );
        try {
            Response<SocialPost> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "response {}", response.body() );
            }
            return true;
        } catch ( IOException | APIIntergrationException e ) {
            LOG.error( "IOException/ APIIntergrationException caught", e );
            return false;
        }
    }

	public List<SurveyInvitationEmailCountMonth> getReceivedCountsMonth(long startDate, long endDate, int startIndex, int batchSize) throws IOException {
		Call<List<SurveyInvitationEmailCountMonth>> request = RetrofitApiBuilder.apiBuilderInstance()
				.getReportingSSAPIIntergrationService().getReceivedCountsMonth(startDate,endDate, startIndex, batchSize);
		Response<List<SurveyInvitationEmailCountMonth>> response = request.execute();
		RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
		return response.body();
		
	}


	public boolean saveEmailCountMonthData(List<SurveyInvitationEmailCountMonth> agentEmailCountsMonth) {
        Call<Boolean> requestCall = RetrofitApiBuilder.apiBuilderInstance()
            .getReportingSSAPIIntergrationService().saveEmailCountMonthData( agentEmailCountsMonth );
        try {
            Response<Boolean> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "response {}", response.body() );
            }
            return true;
        } catch ( IOException | APIIntergrationException e ) {
            LOG.error( "IOException/ APIIntergrationException caught", e );
            return false;
        }
		
	}
}
