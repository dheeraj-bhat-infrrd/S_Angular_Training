package com.realtech.socialsurvey.core.exception;

/**
 * JIRA:SS-117 by RM02 
 * Base Exception class for handling exceptions of rest services
 */
public class BaseRestException extends RuntimeException {

	/**
	 * serial version id
	 */
	private static final long serialVersionUID = 7660565310950488351L;

	protected String message;
	protected String debugMessage;
	protected ErrorCode errorCode;
	protected int httpCode;
	protected Throwable thrw;
	
	public BaseRestException()
    {
        super();
    }

    public BaseRestException( Throwable thrw )
    {
        super(thrw);
        this.thrw = thrw;
    }

    public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDebugMessage() {
		return debugMessage;
	}

	public void setDebugMessage(String debugMessage) {
		this.debugMessage = debugMessage;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}
	
	public Throwable getThrw()
    {
        return thrw;
    }

    public void setThrw( Throwable thrw )
    {
        this.thrw = thrw;
    }

    /**
	 * Transforms the exception to rest error object that could be sent as a response
	 * 
	 * @param httpStatus
	 * @return resterrorresponse
	 */
	public RestErrorResponse transformException(int httpStatus) {
		RestErrorResponse restError = new RestErrorResponse();
		restError.setHttpStatus(httpStatus);
		restError.setServiceId(errorCode.getServiceId());
		restError.setErrorCode(errorCode.getErrorCode());
		restError.setMessage(errorCode.getMessage());
		restError.setDebugMessage(debugMessage);
		//restError.setThrw( thrw );
		return restError;
	}

}
