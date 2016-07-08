package com.realtech.socialsurvey.web.api.exception;

import com.realtech.socialsurvey.core.exception.FatalException;


public class SSAPIBadRequestException extends FatalException
{
    /**
     * default serial version id
     */
    private static final long serialVersionUID = 2015020702364453334L;

    public SSAPIBadRequestException() {
        super();
    }

    public SSAPIBadRequestException(String message) {
        super(message);
    }

}
