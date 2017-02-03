package com.realtech.socialsurvey.core.services.organizationmanagement;

import java.util.HashMap;
import java.util.List;

import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

public interface ZillowUpdateService
{
    /**
     * Method to update zillow review count and zillow average
     * */
    public void updateZillowReviewCountAndAverage( String collectionName, long iden, double zillowReviewCount,
        double zillowAverage );


    public void pushZillowReviews( List<SurveyDetails> surveyDetailsList, String collectionName,
        OrganizationUnitSettings profileSettings, long companyId ) throws InvalidInputException;
}