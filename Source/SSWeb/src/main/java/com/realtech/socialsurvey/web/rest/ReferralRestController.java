package com.realtech.socialsurvey.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.web.common.ErrorResponse;

/**
 * Rest controller for referral activities
 */
@RestController
@RequestMapping("referral")
public class ReferralRestController
{
	private static final Logger LOG = LoggerFactory.getLogger(ReferralRestController.class);

	@Autowired
	private UserManagementService userManagementService;

	@ExceptionHandler({ InvalidInputException.class, UserAlreadyExistsException.class })
	public ErrorResponse errorResponse(Exception exception) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
		errorResponse.setErrMessage(exception.getMessage());
		return errorResponse;
	}

	@RequestMapping(value = "/register", method = RequestMethod.GET, produces = "application/json")
	public String sendRegistrationMailUsingReferralCode(@RequestParam(value = "firstName") String firstName,
			@RequestParam(value = "lastName", required = false) String lastName, @RequestParam(value = "emailAddress") String emailAddress,
			@RequestParam(value = "referralcode") String referralCode) throws InvalidInputException, UserAlreadyExistsException, NonFatalException {
		LOG.info("Sending invitation mail through referral code");
		userManagementService.validateAndInviteCorporateToRegister(firstName, lastName, emailAddress, false, referralCode);
		return "success";
	}

}
