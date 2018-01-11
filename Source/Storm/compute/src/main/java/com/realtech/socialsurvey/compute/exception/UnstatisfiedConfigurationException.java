package com.realtech.socialsurvey.compute.exception;

public class UnstatisfiedConfigurationException extends FatalException
{

    private static final long serialVersionUID = 1L;


    public UnstatisfiedConfigurationException()
    {
        super();
    }


    public UnstatisfiedConfigurationException( String message, Throwable thrw )
    {
        super( message, thrw );
    }


    public UnstatisfiedConfigurationException( String message )
    {
        super( message );
    }


}
