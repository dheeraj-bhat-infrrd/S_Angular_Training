package com.realtech.socialsurvey.compute.exception;

/**
 * Typically, a critical exception, which occurs when the application cannot
 * recover from the cause that affected this. This is base exception which needs
 * to be extended by all the similar exception scenarios.
 * 
 */
public class FatalException extends RuntimeException
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    public FatalException()
    {
        super();
    }


    public FatalException( String message )
    {
        super( message );
    }


    public FatalException( String message, Throwable thrw )
    {
        super( message, thrw );
    }

}

