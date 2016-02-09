package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.ExternalAPICallDetails;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


public interface ExternalApiCallDetailsDao
{

    /**
     * Method to insert external api call details into mongo
     * @param callDetails
     * @throws InvalidInputException
     */
    public void insertApiCallDetails( ExternalAPICallDetails callDetails ) throws InvalidInputException;

}
