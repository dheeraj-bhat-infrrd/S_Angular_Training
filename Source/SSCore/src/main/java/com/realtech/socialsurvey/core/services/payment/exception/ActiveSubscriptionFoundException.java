package com.realtech.socialsurvey.core.services.payment.exception;

import com.realtech.socialsurvey.core.exception.NonFatalException;


public class ActiveSubscriptionFoundException extends NonFatalException
{

    /**
     * default serial version id
     */
    private static final long serialVersionUID = 2015020702364453334L;

    public ActiveSubscriptionFoundException() {
        super();
    }

    public ActiveSubscriptionFoundException(String message) {
        super(message);
    }

    public ActiveSubscriptionFoundException(String message, Throwable thrw) {
        super(message, thrw);
    }

    public ActiveSubscriptionFoundException(String message, String errorCode) {
        super(message,errorCode);
    }

    public ActiveSubscriptionFoundException(String message, String errorCode, Throwable thrw) {
        super(message, errorCode, thrw);
    }

}
