package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.ForwardMailDetails;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


public interface ForwardMailDetailsDao
{

    public void insertForwardMailDetails( ForwardMailDetails forwardMailDetails ) throws InvalidInputException;


    public boolean checkIfForwardMailDetailsExist( String senderMailId, String recipientMailId, String messageId ) throws InvalidInputException;
}
