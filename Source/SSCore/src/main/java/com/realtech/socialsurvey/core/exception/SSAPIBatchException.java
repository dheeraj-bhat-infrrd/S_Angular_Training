package com.realtech.socialsurvey.core.exception;

public class SSAPIBatchException extends FatalException
{

    /**
     * default serial version id
     */
    private static final long serialVersionUID = 2015020702364453334L;

    public SSAPIBatchException() {
        super();
    }

    public SSAPIBatchException(String message) {
        super(message);
    }

    public SSAPIBatchException(String message, Throwable thrw) {
        super(message, thrw);
    }
}
