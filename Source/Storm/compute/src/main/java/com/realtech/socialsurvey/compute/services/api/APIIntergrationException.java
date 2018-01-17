package com.realtech.socialsurvey.compute.services.api;

import com.realtech.socialsurvey.compute.exception.FatalException;

/**
 * Exception for api integration
 * @author nishit
 *
 */
public class APIIntergrationException extends FatalException
{

    private static final long serialVersionUID = 1L;


    public APIIntergrationException()
    {
        super();
    }


    public APIIntergrationException( String message, Throwable thrw )
    {
        super( message, thrw );
    }


    public APIIntergrationException( String message )
    {
        super( message );
    }


}
