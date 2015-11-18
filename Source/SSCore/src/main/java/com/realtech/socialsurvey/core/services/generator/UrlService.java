package com.realtech.socialsurvey.core.services.generator;

import com.realtech.socialsurvey.core.exception.InvalidInputException;

public interface UrlService
{
    public String shortenUrl(String url) throws InvalidInputException;

    public String retrieveCompleteUrlForID( String encryptedIDStr ) throws InvalidInputException;

}
