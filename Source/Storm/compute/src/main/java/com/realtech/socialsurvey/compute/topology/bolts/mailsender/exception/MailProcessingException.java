package com.realtech.socialsurvey.compute.topology.bolts.mailsender.exception;

import com.realtech.socialsurvey.compute.exception.FatalException;


public class MailProcessingException extends FatalException
{

    private static final long serialVersionUID = 1L;


    public MailProcessingException()
    {
        super();
    }


    public MailProcessingException( String message, Throwable thrw )
    {
        super( message, thrw );
    }


    public MailProcessingException( String message )
    {
        super( message );
    }


}
