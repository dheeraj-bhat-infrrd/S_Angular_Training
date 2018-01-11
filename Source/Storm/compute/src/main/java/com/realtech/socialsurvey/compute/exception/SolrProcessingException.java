package com.realtech.socialsurvey.compute.exception;

/**
 * Created by nishit on 03/01/18.
 */
public class SolrProcessingException extends FatalException
{

    private static final long serialVersionUID = 1L;

    public SolrProcessingException()
    {
        super();
    }


    public SolrProcessingException( String message )
    {
        super( message );
    }


    public SolrProcessingException( String message, Throwable thrw )
    {
        super( message, thrw );
    }
}
