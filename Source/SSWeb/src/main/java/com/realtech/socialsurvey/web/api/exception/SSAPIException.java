package com.realtech.socialsurvey.web.api.exception;

import com.realtech.socialsurvey.core.exception.FatalException;


/**
 * API Exception
 */
public class SSAPIException extends FatalException
{

    /**
     * default serial version id
     */
    private static final long serialVersionUID = 2015020702364453334L;

    public SSAPIException() {
        super();
    }

    public SSAPIException(String message) {
        super(message);
    }

    public SSAPIException(String message, Throwable thrw) {
        super(message, thrw);
    }
}
