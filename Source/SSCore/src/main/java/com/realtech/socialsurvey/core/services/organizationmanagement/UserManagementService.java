package com.realtech.socialsurvey.core.services.organizationmanagement;

import java.util.List;
import java.util.Map;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;

// JIRA SS-34 BY RM02 BOC
/**
 * Interface with methods defined to manage user
 */
public interface UserManagementService {

	public ProfilesMaster getProfilesMasterById(int profileId) throws InvalidInputException;

	public User assignBranchAdmin(User user, long branchId, long userId) throws InvalidInputException;

	public User assignRegionAdmin(User user, long regionId, long userId) throws InvalidInputException;

	public void updateUserStatus(long userId, int status) throws InvalidInputException;

	// JIRA SS-42 BY RM02 BOC

	public void inviteUserToRegister(User admin, String firstName, String lastName, String emailId) throws InvalidInputException,
			UserAlreadyExistsException, UndeliveredEmailException;

	public void removeExistingUser(User admin, long userIdToBeDeactivated) throws InvalidInputException;

	public void unassignBranchAdmin(User admin, long branchId, long userIdToRemove) throws InvalidInputException;

	public void unassignRegionAdmin(User admin, long regionId, long userIdToRemove) throws InvalidInputException;

	public User getUserByEmailId(User admin, String emailId) throws InvalidInputException, NoRecordsFetchedException;

	public List<User> getUsersBySimilarEmailId(User admin, String emailId) throws InvalidInputException;

	public boolean isUserAdditionAllowed(User user) throws NoRecordsFetchedException;

	public User getUserByUserId(long userId);

	public List<Branch> getBranchesAssignedToUser(User user) throws NoRecordsFetchedException;

	public List<User> getUsersForCompany(User user) throws InvalidInputException, NoRecordsFetchedException;
	
	public void assignUserToBranch(User admin, long userId, long branchId) throws InvalidInputException;

	public void updateUser(User admin, long userIdToUpdate, boolean isActive) throws InvalidInputException;
	
	// JIRA SS-42 BY RM05 EOC

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
// JIRA SS-34 BY RM02 BOC
