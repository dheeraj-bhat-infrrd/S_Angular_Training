package com.realtech.socialsurvey.compute.exception;

/**
 * Exception for api integration
 * @author nishit
 *
 */
public class APIIntegrationException extends FatalException
{

    private static final long serialVersionUID = 1L;


    public APIIntegrationException()
    {
        super();
    }


    public APIIntegrationException( String message, Throwable thrw )
    {
        super( message, thrw );
    }


    public APIIntegrationException( String message )
    {
        super( message );
    }


}
