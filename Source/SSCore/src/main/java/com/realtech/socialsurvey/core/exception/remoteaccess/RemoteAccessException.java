package com.realtech.socialsurvey.core.exception.remoteaccess;

import com.realtech.socialsurvey.core.exception.NonFatalException;


/**
 * Exception class for handling remote access across the application
 */
public class RemoteAccessException extends NonFatalException
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    public RemoteAccessException()
    {
        super();
    }


    public RemoteAccessException( String message )
    {
        super( message );
    }


    public RemoteAccessException( String message, Throwable thrw )
    {
        super( message, thrw );
    }


    public RemoteAccessException( String message, String errorCode )
    {
        super( message, errorCode );
    }


    public RemoteAccessException( String message, String errorCode, Throwable thrw )
    {
        super( message, errorCode, thrw );
    }
}
