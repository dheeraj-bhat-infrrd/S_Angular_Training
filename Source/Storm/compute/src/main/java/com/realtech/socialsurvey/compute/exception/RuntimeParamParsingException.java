package com.realtech.socialsurvey.compute.exception;

public class RuntimeParamParsingException extends FatalException
{

    private static final long serialVersionUID = 1L;


    public RuntimeParamParsingException()
    {
        super();
    }


    public RuntimeParamParsingException( String message, Throwable thrw )
    {
        super( message, thrw );
    }


    public RuntimeParamParsingException( String message )
    {
        super( message );
    }


}
