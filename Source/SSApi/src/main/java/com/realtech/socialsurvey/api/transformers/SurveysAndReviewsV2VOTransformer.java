package com.realtech.socialsurvey.api.transformers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.api.models.v2.SurveyGetV2VO;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.vo.SurveysAndReviewsVO;


@Component
public class SurveysAndReviewsV2VOTransformer
    implements Transformer<List<SurveyGetV2VO>, SurveysAndReviewsVO, List<SurveyGetV2VO>>
{

    @Autowired
    SurveyV2Transformer surveyTransformer;


    @Override
    public SurveysAndReviewsVO transformApiRequestToDomainObject( List<SurveyGetV2VO> a, Object... objects )
        throws InvalidInputException
    {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public List<SurveyGetV2VO> transformDomainObjectToApiResponse( SurveysAndReviewsVO d, Object... objects )
    {

        List<SurveyGetV2VO> surveyVOs = new ArrayList<SurveyGetV2VO>();

        for ( Entry<SurveyDetails, SurveyPreInitiation> entry : d.getInitiatedSurveys().entrySet() ) {
            SurveyGetV2VO surveyVO = surveyTransformer.transformDomainObjectToApiResponse( entry.getKey(), entry.getValue() );
            surveyVOs.add( surveyVO );
        }

        for ( SurveyPreInitiation surveyPreInitiation : d.getPreInitiatedSurveys() ) {
            SurveyGetV2VO surveyVO = surveyTransformer.transformDomainObjectToApiResponse( null, surveyPreInitiation );
            surveyVOs.add( surveyVO );
        }

        return surveyVOs;
    }


}
