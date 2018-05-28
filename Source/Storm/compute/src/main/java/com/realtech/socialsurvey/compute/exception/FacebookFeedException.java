package com.realtech.socialsurvey.compute.exception;

/**
 * Exception for Facebook Rate limiting
 * code : 17 for 
 * @author manish
 *
 */
public class FacebookFeedException extends FatalException
{

    private static final long serialVersionUID = 1L;

    private final int facebookErrorCode;


    public FacebookFeedException( int facebookErrorCode )
    {
        super();
        this.facebookErrorCode = facebookErrorCode;
    }


    public FacebookFeedException( int facebookErrorCode, String message, Throwable thrw )
    {
        super( message, thrw );
        this.facebookErrorCode = facebookErrorCode;
    }


    public FacebookFeedException( int facebookErrorCode, String message )
    {
        super( message );
        this.facebookErrorCode = facebookErrorCode;
    }


    public int getFacebookErrorCode()
    {
        return facebookErrorCode;
    }
}
