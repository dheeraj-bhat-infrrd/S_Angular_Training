package com.realtech.socialsurvey.core.integration.stream;

import com.realtech.socialsurvey.core.exception.FatalException;

/**
 * Error when stream api connection fails
 * @author nishit
 *
 */
public class StreamApiConnectException extends FatalException
{

    private static final long serialVersionUID = 1L;

    public StreamApiConnectException()
    {
        super();
    }

    public StreamApiConnectException( String message, Throwable thrw )
    {
        super( message, thrw );
    }

    public StreamApiConnectException( String message )
    {
        super( message );
    }
    
    

}
