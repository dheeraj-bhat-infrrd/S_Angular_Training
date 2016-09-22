package com.realtech.socialsurvey.core.vo;

import java.util.List;
import java.util.Map;

import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;

/**
 * 
 * @author rohit
 *
 */
public class SurveysAndReviewsVO
{

    private Map<SurveyDetails , SurveyPreInitiation> initiatedSurveys;
    private List<SurveyPreInitiation> preInitiatedSurveys;

    
    
    public Map<SurveyDetails, SurveyPreInitiation> getInitiatedSurveys()
    {
        return initiatedSurveys;
    }
    public void setInitiatedSurveys( Map<SurveyDetails, SurveyPreInitiation> initiatedSurveys )
    {
        this.initiatedSurveys = initiatedSurveys;
    }
    public List<SurveyPreInitiation> getPreInitiatedSurveys()
    {
        return preInitiatedSurveys;
    }
    public void setPreInitiatedSurveys( List<SurveyPreInitiation> preInitiatedSurveys )
    {
        this.preInitiatedSurveys = preInitiatedSurveys;
    }
}
