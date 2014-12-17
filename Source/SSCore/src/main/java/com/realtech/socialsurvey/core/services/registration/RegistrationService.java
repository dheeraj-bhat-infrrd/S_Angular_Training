package com.realtech.socialsurvey.core.services.registration;

import java.util.Map;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;

/**
 * Holds methods to register a user or a company to the application
 */
public interface RegistrationService {

	/**
	 * Sends invitation to corporate to register
	 * 
	 * @throws NonFatalException
	 */
	public void inviteCorporateToRegister(String firstName, String lastName, String emailId) throws InvalidInputException, UndeliveredEmailException,
			NonFatalException;

	public Map<String, String> validateRegistrationUrl(String encryptedUrlParameter) throws InvalidInputException;

	public User addCorporateAdminAndUpdateStage(String firstName, String lastName, String emailId, String password, boolean isDirectRegistration)
			throws InvalidInputException, UserAlreadyExistsException, UndeliveredEmailException;

	public void updateProfileCompletionStage(User user, int profilesMasterId, String profileCompletionStage) throws InvalidInputException;

	public void verifyAccount(String encryptedUrlParams) throws InvalidInputException;

}
