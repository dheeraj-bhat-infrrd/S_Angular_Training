package com.realtech.socialsurvey.core.exception;

public class HierarchyAlreadyExistsException extends NonFatalException
{

    public HierarchyAlreadyExistsException() {
        super();
    }

    public HierarchyAlreadyExistsException(String message) {
        super(message);
    }

    public HierarchyAlreadyExistsException(String message, Throwable thrw) {
        super(message, thrw);
    }

    public HierarchyAlreadyExistsException(String message, String errorCode) {
        super(message,errorCode);
    }

    public HierarchyAlreadyExistsException(String message, String errorCode, Throwable thrw) {
        super(message, errorCode, thrw);
    }
}
