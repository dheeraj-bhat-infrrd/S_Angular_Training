package com.realtech.socialsurvey.compute.topology.bolts.mailsender.exception;

import com.realtech.socialsurvey.compute.exception.FatalException;


/**
 * Should be thrown when mail processing is failed temporarily. The mail message that throws this exception should be retried.
 * @author nishit
 *
 */
public class TemporaryMailProcessingException extends FatalException
{

    private static final long serialVersionUID = 1L;


    public TemporaryMailProcessingException()
    {
        super();
    }


    public TemporaryMailProcessingException( String message, Throwable thrw )
    {
        super( message, thrw );
    }


    public TemporaryMailProcessingException( String message )
    {
        super( message );
    }


}
