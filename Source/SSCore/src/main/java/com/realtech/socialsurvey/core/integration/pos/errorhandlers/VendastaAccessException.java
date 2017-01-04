package com.realtech.socialsurvey.core.integration.pos.errorhandlers;

import com.realtech.socialsurvey.core.exception.FatalException;


public class VendastaAccessException extends FatalException
{

    /**
     * default serial version id
     */
    private static final long serialVersionUID = 2015020702364453334L;


    public VendastaAccessException()
    {
        super();
    }


    public VendastaAccessException( String message )
    {
        super( message );
    }


    public VendastaAccessException( String message, Throwable thrw )
    {
        super( message, thrw );
    }
}