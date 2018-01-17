package com.realtech.socialsurvey.compute.exception;

public class FileUploadUpdationException extends FatalException {

    public FileUploadUpdationException()
    {
        super();
    }


    public FileUploadUpdationException( String message, Throwable thrw )
    {
        super( message, thrw );
    }


    public FileUploadUpdationException( String message )
    {
        super( message );
    }

}
