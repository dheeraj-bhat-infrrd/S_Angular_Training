package com.realtech.socialsurvey.core.services.generator;

import com.realtech.socialsurvey.core.exception.InvalidInputException;

public interface UrlService
{
    public String shortenUrl(String url, String uuid) throws InvalidInputException;

    public String retrieveCompleteUrlForID( String encryptedIDStr ) throws InvalidInputException;

    public void sendClickEvent( String uuid );
}
