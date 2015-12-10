package com.realtech.socialsurvey.core.services.organizationmanagement;

public interface ZillowUpdateService
{
    /**
     * Method to update zillow review count and zillow average
     * */
    public void updateZillowReviewCountAndAverage( String collectionName, long iden, double zillowReviewCount,
        double zillowAverage );
}
