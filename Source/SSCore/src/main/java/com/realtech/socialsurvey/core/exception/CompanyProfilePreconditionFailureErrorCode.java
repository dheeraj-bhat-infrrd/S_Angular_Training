package com.realtech.socialsurvey.core.exception;

import com.realtech.socialsurvey.core.commons.CommonConstants;

public class CompanyProfilePreconditionFailureErrorCode implements ErrorCode {

	private String message;

	public CompanyProfilePreconditionFailureErrorCode(String message) {
		this.message = message;
	}

	@Override
	public int getErrorCode() {
		return CommonConstants.ERROR_CODE_COMPANY_PROFILE_PRECONDITION_FAILURE;
	}

	@Override
	public int getServiceId() {
		return CommonConstants.SERVICE_CODE_COMPANY_PROFILE;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
