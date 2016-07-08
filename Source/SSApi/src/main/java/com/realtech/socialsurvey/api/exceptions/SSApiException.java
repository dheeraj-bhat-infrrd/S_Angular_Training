package com.realtech.socialsurvey.api.exceptions;

import com.realtech.socialsurvey.core.exception.NonFatalException;

public class SSApiException extends NonFatalException
{
    /**
     * default serial version id
     */
    private static final long serialVersionUID = 2015020702364453334L;

    public SSApiException() {
        super();
    }

    public SSApiException(String message) {
        super(message);
    }

    public SSApiException(String message, Throwable thrw) {
        super(message, thrw);
    }

    public SSApiException(String message, String errorCode) {
        super(message, errorCode);
    }

    public SSApiException(String message, String errorCode, Throwable thrw) {
        super(message, errorCode, thrw);
    }
}
