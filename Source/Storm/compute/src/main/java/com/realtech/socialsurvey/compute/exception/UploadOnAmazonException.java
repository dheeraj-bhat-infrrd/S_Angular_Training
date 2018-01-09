package com.realtech.socialsurvey.compute.exception;

public class UploadOnAmazonException extends FatalException{

    public UploadOnAmazonException()
    {
        super();
    }


    public UploadOnAmazonException( String message, Throwable thrw )
    {
        super( message, thrw );
    }


    public UploadOnAmazonException( String message )
    {
        super( message );
    }
}
