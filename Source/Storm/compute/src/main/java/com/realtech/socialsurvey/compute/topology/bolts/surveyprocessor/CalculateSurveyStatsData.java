package com.realtech.socialsurvey.compute.topology.bolts.surveyprocessor;

import java.util.Arrays;
import java.util.List;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.entities.SurveyData;
import com.realtech.socialsurvey.compute.entities.response.EntitySurveyStatsVO;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;

/**
 * 
 * @author rohitpatidar
 *
 */
public class CalculateSurveyStatsData  extends BaseComputeBoltWithAck
{

	
	private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( CalculateSurveyStatsData.class );
	
    public static final String SURVEY_MOOD_GREAT = "Great";
    public static final String SURVEY_MOOD_OK = "OK";
    public static final String SURVEY_MOOD_UNPLEASANT = "Unpleasant";
    
	@Override
	public void declareOutputFields( OutputFieldsDeclarer outputFieldsDeclarer ) 
	{
		outputFieldsDeclarer.declare( new Fields( "surveyData", "agentSurveyStatsVO", "branchSurveyStatsVO", "companySurveyStatsVO"  ) );
		
	}
	
	
	@Override
	public void executeTuple(Tuple input) 
	{
		LOG.info("Starting execution of CalculateSurveyStatsData ");
		SurveyData surveyData = (SurveyData) input.getValueByField("surveyData");
		EntitySurveyStatsVO agentSurveyStatsVO =updateEntitySurveyStatsVO(surveyData, (EntitySurveyStatsVO) input.getValueByField("agentSurveyStatsVO"));
		EntitySurveyStatsVO branchSurveyStatsVO = updateEntitySurveyStatsVO(surveyData, (EntitySurveyStatsVO) input.getValueByField("branchSurveyStatsVO"));
		EntitySurveyStatsVO companySurveyStatsVO = updateEntitySurveyStatsVO(surveyData, (EntitySurveyStatsVO) input.getValueByField("companySurveyStatsVO"));
		  _collector.emit( input, Arrays.asList( surveyData, agentSurveyStatsVO, branchSurveyStatsVO, companySurveyStatsVO ) );
	}
	
	private EntitySurveyStatsVO updateEntitySurveyStatsVO(SurveyData surveyData, EntitySurveyStatsVO entitySurveyStatsVO) {
		// calculate sps score
		double spsScore = calculateSpsScore(entitySurveyStatsVO);
		entitySurveyStatsVO.setSpsScore(spsScore);
		// calculate search ranking sore
		double searchRankingScore = calculateSearchRankingScore(entitySurveyStatsVO);
		// set latest review
		entitySurveyStatsVO.setLatestReview(surveyData.getReview());
		entitySurveyStatsVO.setSearchRankingScore(searchRankingScore);
		return entitySurveyStatsVO;
	}
	
	/**
	 * 
	 * @param entitySurveyStatsVO
	 * @return
	 */
	private double calculateSearchRankingScore(EntitySurveyStatsVO entitySurveyStatsVO) 
	{
		LOG.info("Method calculateSearchRankingScore started");
		
		double avgScore = entitySurveyStatsVO.getAvgScore();
		long surveyCount = entitySurveyStatsVO.getSurveyCount();
		long incompleteSurveyCount = entitySurveyStatsVO.getIncompleteSurveyCount();
		double spsScore = entitySurveyStatsVO.getSpsScore();
		int defaultOffset = entitySurveyStatsVO.getDefaultOffset();
		float completionRatio = entitySurveyStatsVO.getCompletionRatio();
		int spsOffset = entitySurveyStatsVO.getSpsOffset();
		double spsRatio = entitySurveyStatsVO.getSpsRatio();
		
		
		
		double completionRate = 0;
		if(surveyCount > 0)		
			completionRate = surveyCount / (surveyCount + incompleteSurveyCount) * 100;
		
		double searchRankingScore  = 0l;
		if((surveyCount > 0)) {
			double searchRankingSurveyCountPart = ( (surveyCount * avgScore ) + defaultOffset ) / (surveyCount + 1) ;
			double searchRankingCompletionPart =  (completionRate * completionRatio) ;
			double searchRankingSPSPart = (spsScore - spsOffset ) * spsRatio;
			searchRankingScore =  searchRankingSurveyCountPart + searchRankingCompletionPart  + searchRankingSPSPart;
			
		}
		
		LOG.info("Calculated search ranking score is {}" , searchRankingScore);
		
		LOG.info("Method calculateSearchRankingScore finished");
		return searchRankingScore;
	}
	
	private double calculateSpsScore(EntitySurveyStatsVO entitySurveyStatsVO) 
	{
		LOG.info("Method calculateSpsScore started");
		//TODO : calculate sps score
		double spsScore = ((entitySurveyStatsVO.getGatewayResponseGreat() - entitySurveyStatsVO.getGatewayResponseUnpleasant()) * 100 )/(entitySurveyStatsVO.getSurveyCount());
		
		LOG.info("Method calculateSpsScore finished");
		return spsScore;
	}

	@Override
	public List<Object> prepareTupleForFailure() 
	{
		// TODO Auto-generated method stub
		return null;
	}
}
