package com.realtech.socialsurvey.compute.topology.bolts.monitor.exception;

import com.realtech.socialsurvey.compute.exception.FatalException;


public class FeedsProcessingException extends FatalException
{

    private static final long serialVersionUID = 1L;


    public FeedsProcessingException()
    {
        super();
    }


    public FeedsProcessingException( String message, Throwable thrw )
    {
        super( message, thrw );
    }


    public FeedsProcessingException( String message )
    {
        super( message );
    }


}
