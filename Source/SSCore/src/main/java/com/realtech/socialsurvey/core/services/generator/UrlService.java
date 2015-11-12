package com.realtech.socialsurvey.core.services.generator;

public interface UrlService
{
    public String shortenUrl(String url);

    public String retrieveCompleteUrlForID( String encryptedIDStr );

}
