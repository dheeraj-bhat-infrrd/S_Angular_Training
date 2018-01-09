package com.realtech.socialsurvey.compute.exception;

/**
 * Exception while processing messages to be queued to kafka
 * @author nishit
 *
 */
public class QueueingMessageProcessingException extends FatalException
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    public QueueingMessageProcessingException()
    {
        super();
    }


    public QueueingMessageProcessingException( String message, Throwable thrw )
    {
        super( message, thrw );
    }


    public QueueingMessageProcessingException( String message )
    {
        super( message );
    }


}
