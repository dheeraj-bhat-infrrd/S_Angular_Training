package com.realtech.socialsurvey.compute.topology.bolts.surveyprocessor;

import static com.realtech.socialsurvey.compute.common.ComputeConstants.ACCESS_TOKEN;
import static com.realtech.socialsurvey.compute.common.ComputeConstants.APPLICATION_PROPERTY_FILE;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.LocalPropertyFileHandler;
import com.realtech.socialsurvey.compute.common.RetrofitApiBuilder;
import com.realtech.socialsurvey.compute.entities.SurveyData;
import com.realtech.socialsurvey.compute.entities.response.EntitySurveyStatsVO;
import com.realtech.socialsurvey.compute.services.FailedMessagesService;
import com.realtech.socialsurvey.compute.services.impl.FailedMessagesServiceImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;

import retrofit2.Call;
import retrofit2.Response;

/**
 * 
 * @author rohitpatidar
 *
 */
public class FetchPrerequisiteSurveyStatsDataBolt  extends BaseComputeBoltWithAck
{

	private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( FetchPrerequisiteSurveyStatsDataBolt.class );
	
	@Override
	public void declareOutputFields( OutputFieldsDeclarer outputFieldsDeclarer ) 
	{
		outputFieldsDeclarer.declare( new Fields( "isSucess", "surveyData", "agentSurveyStatsVO", "branchSurveyStatsVO", "companySurveyStatsVO" ) );
		
	}

	/**
	 * 
	 */
	@Override
	public void executeTuple(Tuple input) 
	{
		LOG.info("Starting execution of FetchPrerequisiteSurveyStatsDataBolt ");
		FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();
		SurveyData surveyData = ConversionUtils.deserialize(input.getString(0), SurveyData.class);
		EntitySurveyStatsVO agentSurveyStatsVO = new EntitySurveyStatsVO();
		EntitySurveyStatsVO branchSurveyStatsVO = new EntitySurveyStatsVO();
		EntitySurveyStatsVO companySurveyStatsVO = new EntitySurveyStatsVO();
		boolean isSucess = false;
		try {
			LOG.info("Fetching prerequisite data related to survey with id {}", surveyData.getId());
			agentSurveyStatsVO = getSurveyStats("agentId", surveyData.getAgentId());
			branchSurveyStatsVO = getSurveyStats("branchId", surveyData.getBranchId());
			companySurveyStatsVO = getSurveyStats("companyId", surveyData.getCompanyId());
			isSucess = true;
		} catch (Exception exception) {
			failedMessagesService.insertTemporaryFailedSurveyProcessor(surveyData);
		}
		_collector.emit( input, Arrays.asList( isSucess, surveyData, agentSurveyStatsVO, branchSurveyStatsVO, companySurveyStatsVO ) );
	}
	
	/**
	 * 
	 * @param entityId
	 * @return
	 */
	private EntitySurveyStatsVO getSurveyStats(String entityType,long entityId) 
	{
		
		LOG.info("Method getSurveyStats started for {} {} " , entityType,entityId);
		
        //add the auth token to properties files
        String token = LocalPropertyFileHandler.getInstance()
            .getProperty( APPLICATION_PROPERTY_FILE, ACCESS_TOKEN ).orElse( null );
        Call<EntitySurveyStatsVO> requestCall = RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationService().getSurveyStatsForEntity(entityType , entityId);
        try {
            Response<EntitySurveyStatsVO> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "response {}", response.body() );
            }
            
            LOG.info("Method getSurveyStats finished for {} {} " , entityType,entityId);
            return response.body();
        } catch ( IOException e ) {
            LOG.error( "IOException/ APIIntergrationException caught in method getSurveyStats", e );
            return null;
        }
        
    }
	
	
	@Override
	public List<Object> prepareTupleForFailure() 
	{
		// TODO Auto-generated method stub
		return null;
	}

}
