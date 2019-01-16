package com.realtech.socialsurvey.compute.topology.bolts.surveyprocessor;

import static com.realtech.socialsurvey.compute.common.ComputeConstants.ACCESS_TOKEN;
import static com.realtech.socialsurvey.compute.common.ComputeConstants.APPLICATION_PROPERTY_FILE;

import java.io.IOException;
import java.util.List;

import org.apache.storm.topology.OutputFieldsDeclarer;
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

import retrofit2.Call;
import retrofit2.Response;

/**
 * 
 * @author rohitpatidar
 *
 */
public class UpdateSurveyStatsBolt  extends BaseComputeBoltWithAck
{
	
	private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( UpdateSurveyStatsBolt.class );
	
	@Override
	public void declareOutputFields( OutputFieldsDeclarer outputFieldsDeclarer ) 
	{
		
	}

	
	@Override
	public void executeTuple(Tuple input) 
	{
		LOG.info("Starting execution of CalculateSurveyStatsData ");
		Boolean isSucess = (Boolean) input.getValueByField("isSucess");
		if(isSucess) {
			FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();
			SurveyData surveyData = (SurveyData) input.getValueByField("surveyData");
			EntitySurveyStatsVO agentSurveyStatsVO = (EntitySurveyStatsVO) input.getValueByField("agentSurveyStatsVO");
			EntitySurveyStatsVO branchSurveyStatsVO = (EntitySurveyStatsVO) input.getValueByField("branchSurveyStatsVO");
			EntitySurveyStatsVO companySurveyStatsVO = (EntitySurveyStatsVO) input.getValueByField("companySurveyStatsVO");
			try {
				updateSurveyStats("agentId", surveyData.getAgentId(), agentSurveyStatsVO);
				updateSurveyStats("branchId", surveyData.getBranchId(), branchSurveyStatsVO);
				updateSurveyStats("companyId", surveyData.getCompanyId(), companySurveyStatsVO);
				//delete from failed mesage if exists
				failedMessagesService.deleteFailedSurveyProcessor(surveyData.getSurveyId());
			}catch (Exception exception) {
				failedMessagesService.insertTemporaryFailedSurveyProcessor(surveyData);
			}
		}
		
	}
	
	private boolean updateSurveyStats(String entityType, long entityId, EntitySurveyStatsVO entitySurveyStatsVO) 
	{
		
		LOG.info("Method updateSurveyStats started for {} {} " , entityType,entityId);
		
        //add the auth token to properties files
        String token = LocalPropertyFileHandler.getInstance()
            .getProperty( APPLICATION_PROPERTY_FILE, ACCESS_TOKEN ).orElse( null );
        Call<Boolean> requestCall = RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationService().updateSurveyStatsForEntity(entityType , entityId, entitySurveyStatsVO);
        try {
            Response<Boolean> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "response {}", response.body() );
            }
            
            LOG.info("Method updateSurveyStats finished for {} {} " ,entityType, entityId);
            return response.body();
        } catch ( IOException e ) {
            LOG.error( "IOException/ APIIntergrationException caught in method updateSurveyStats", e );
            return false;
        }
        
    }
	
	@Override
	public List<Object> prepareTupleForFailure() 
	{
		// TODO Auto-generated method stub
		return null;
	}
}
