package com.realtech.socialsurvey.core.services.organizationmanagement;

import java.util.List;

import com.realtech.socialsurvey.core.entities.SurveyDetails;


/**
 * @author manish
 *
 */
public interface DeleteDataTrackerService
{
    /**
     * Method to put entry in delete_data_tracker table for deleted survey details
     * @param documentsToBeDeleted
     */
    public void writeToDeleteTrackerForSurveyDetails( List<SurveyDetails> documentsToBeDeleted );

    /**
     * Method to put entry in delete_data_tracker for deleted user profile
     * @param userProfileId
     */
    public void writeToDeleteTrackerForUserProfile( long userProfileId );
}
