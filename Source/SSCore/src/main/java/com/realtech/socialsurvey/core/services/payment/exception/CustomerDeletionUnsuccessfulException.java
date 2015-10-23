package com.realtech.socialsurvey.core.services.payment.exception;

import com.realtech.socialsurvey.core.exception.NonFatalException;

public class CustomerDeletionUnsuccessfulException extends NonFatalException
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public CustomerDeletionUnsuccessfulException() {
        super();
    }

    public CustomerDeletionUnsuccessfulException(String message) {
        super(message);
    }

    public CustomerDeletionUnsuccessfulException(String message, Throwable thrw) {
        super(message, thrw);
    }

    public CustomerDeletionUnsuccessfulException(String message, String errorCode) {
        super(message, errorCode);
    }

    public CustomerDeletionUnsuccessfulException(String message, String errorCode, Throwable thrw) {
        super(message, errorCode, thrw);
    }

}
