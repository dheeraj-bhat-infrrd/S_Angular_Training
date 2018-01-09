package com.realtech.socialsurvey.core.integration.stream;

import com.realtech.socialsurvey.core.exception.FatalException;

public class StreamApiException extends FatalException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 268118632307906810L;


    public StreamApiException()
    {
        super();
    }


    public StreamApiException( String message )
    {
        super( message );
    }


    public StreamApiException( String message, Throwable thrw )
    {
        super( message, thrw );
    }

}
