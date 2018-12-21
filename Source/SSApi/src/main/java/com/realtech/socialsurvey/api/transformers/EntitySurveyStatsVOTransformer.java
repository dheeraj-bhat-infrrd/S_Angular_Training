package com.realtech.socialsurvey.api.transformers;

import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.api.models.response.EntitySurveyStatsVO;
import com.realtech.socialsurvey.core.entities.LOSearchEngine;
import com.realtech.socialsurvey.core.entities.SurveyStats;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

@Component
public class EntitySurveyStatsVOTransformer implements Transformer<EntitySurveyStatsVO, SurveyStats, EntitySurveyStatsVO> {

	@Override
	public SurveyStats transformApiRequestToDomainObject(EntitySurveyStatsVO a, Object... objects)
			throws InvalidInputException 
	{
		SurveyStats surveyStats = new SurveyStats();
		surveyStats.setAvgScore(a.getAvgScore());
		surveyStats.setIncompleteSurveyCount(a.getIncompleteSurveyCount());
		surveyStats.setSpsScore(a.getSpsScore());
		surveyStats.setSurveyCount(a.getSurveyCount());
		surveyStats.setRecentSurveyCount(a.getRecentSurveyCount());
		surveyStats.setSearchRankingScore(a.getSearchRankingScore());
		surveyStats.setLatestReview(a.getLatestReview());
		
		return surveyStats;
	}

	@Override
	public EntitySurveyStatsVO transformDomainObjectToApiResponse(SurveyStats d, Object... objects) 
	{
		EntitySurveyStatsVO entitySurveyStatsVO = new EntitySurveyStatsVO();
		LOSearchEngine loSearchEngineSettings = (LOSearchEngine) objects[0];
		
		entitySurveyStatsVO.setAvgScore(d.getAvgScore());
		entitySurveyStatsVO.setIncompleteSurveyCount(d.getIncompleteSurveyCount());
		entitySurveyStatsVO.setSpsScore(d.getSpsScore());
		entitySurveyStatsVO.setSurveyCount(d.getSurveyCount());
		entitySurveyStatsVO.setRecentSurveyCount(d.getRecentSurveyCount());
		entitySurveyStatsVO.setSearchRankingScore(d.getSearchRankingScore());
		entitySurveyStatsVO.setLatestReview(d.getLatestReview());
		entitySurveyStatsVO.setGatewayResponseGreat(d.getGatewayResponseGreat());
		entitySurveyStatsVO.setGatewayResponseUnpleasant(d.getGatewayResponseUnpleasant());
		
		entitySurveyStatsVO.setCompletionRatio(loSearchEngineSettings.getCompletionRatio());
		entitySurveyStatsVO.setDefaultOffset(loSearchEngineSettings.getDefaultOffset());
		entitySurveyStatsVO.setSpsOffset(loSearchEngineSettings.getSpsOffset());
		entitySurveyStatsVO.setSpsRatio(loSearchEngineSettings.getSpsRatio());

		return entitySurveyStatsVO;
	}

}
