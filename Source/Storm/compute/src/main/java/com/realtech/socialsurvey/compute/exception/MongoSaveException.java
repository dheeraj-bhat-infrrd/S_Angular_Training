package com.realtech.socialsurvey.compute.exception;

public class MongoSaveException extends FatalException {

    public MongoSaveException()
    {
        super();
    }


    public MongoSaveException( String message, Throwable thrw )
    {
        super( message, thrw );
    }


    public MongoSaveException( String message )
    {
        super( message );
    }
}
