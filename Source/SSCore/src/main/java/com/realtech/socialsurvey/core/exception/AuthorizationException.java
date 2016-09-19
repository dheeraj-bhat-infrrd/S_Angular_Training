package com.realtech.socialsurvey.core.exception;

public class AuthorizationException extends NonFatalException {

    /**
     * default serial version id
     */
    private static final long serialVersionUID = 2015020702364453334L;

    public AuthorizationException() {
        super();
    }

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(String message, Throwable thrw) {
        super(message, thrw);
    }
    
    public AuthorizationException(String message, String errorCode) {
        super(message,errorCode);
    }

    public AuthorizationException(String message, String errorCode, Throwable thrw) {
        super(message, errorCode, thrw);
    }
}
