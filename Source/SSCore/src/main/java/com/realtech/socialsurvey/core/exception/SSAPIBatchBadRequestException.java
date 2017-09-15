package com.realtech.socialsurvey.core.exception;

public class SSAPIBatchBadRequestException extends FatalException
{

    /**
     * default serial version id
     */
    private static final long serialVersionUID = 2015020702364453334L;

    public SSAPIBatchBadRequestException() {
        super();
    }

    public SSAPIBatchBadRequestException(String message) {
        super(message);
    }
}
