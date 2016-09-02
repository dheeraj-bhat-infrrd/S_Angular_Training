package com.realtech.socialsurvey.api.transformers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.api.models.SurveyVO;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.vo.SurveysAndReviewsVO;

@Component
public class SurveysAndReviewsVOTransformer implements Transformer<List<SurveyVO>, SurveysAndReviewsVO, List<SurveyVO>>{

	@Autowired
	SurveyTransformer surveyTransformer;
	
	@Override
	public SurveysAndReviewsVO transformApiRequestToDomainObject(List<SurveyVO> a,
			Object... objects) throws InvalidInputException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SurveyVO> transformDomainObjectToApiResponse(SurveysAndReviewsVO d,
			Object... objects) {
		
		List<SurveyVO> surveyVOs = new ArrayList<SurveyVO>();
		
		for(Entry<SurveyDetails, SurveyPreInitiation> entry : d.getInitiatedSurveys().entrySet()){
			SurveyVO surveyVO =  surveyTransformer.transformDomainObjectToApiResponse(entry.getKey(), entry.getValue());
			surveyVOs.add(surveyVO);
		}
		
		for(SurveyPreInitiation surveyPreInitiation : d.getPreInitiatedSurveys()){
			SurveyVO surveyVO =  surveyTransformer.transformDomainObjectToApiResponse(null,surveyPreInitiation);
			surveyVOs.add(surveyVO);
		}
		
		return surveyVOs;
	}

}
