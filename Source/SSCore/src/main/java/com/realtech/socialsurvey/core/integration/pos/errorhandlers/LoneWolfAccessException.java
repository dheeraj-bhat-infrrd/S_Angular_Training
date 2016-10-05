package com.realtech.socialsurvey.core.integration.pos.errorhandlers;

import com.realtech.socialsurvey.core.exception.FatalException;


public class LoneWolfAccessException extends FatalException
{

    /**
     * default serial version id
     */
    private static final long serialVersionUID = 2015020702364453334L;


    public LoneWolfAccessException()
    {
        super();
    }


    public LoneWolfAccessException( String message )
    {
        super( message );
    }


    public LoneWolfAccessException( String message, Throwable thrw )
    {
        super( message, thrw );
    }
}