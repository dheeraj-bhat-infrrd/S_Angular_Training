package com.realtech.socialsurvey.core.services.upload;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.entities.BranchUploadVO;
import com.realtech.socialsurvey.core.entities.RegionUploadVO;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserUploadVO;
import com.realtech.socialsurvey.core.exception.BranchAdditionException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.RegionAdditionException;
import com.realtech.socialsurvey.core.exception.UserAdditionException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserAssignmentException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;

@Component
public interface CsvUploadService {

	/**
	 * Parses a csv and returns a map of lists of users,branches and regions
	 * @param fileName
	 * @return
	 */
	public Map<String, List<Object>> parseCsv(String fileName);

	/**
	 * Used to get the admin user while testing
	 * @return
	 */
	public User getUser(long userId);

	/**
	 * Creates a user and assigns him under the appropriate branch or region else company.
	 * @param adminUser
	 * @param user
	 * @throws InvalidInputException
	 * @throws UserAdditionException
	 * @throws NoRecordsFetchedException
	 * @throws SolrException
	 * @throws UserAssignmentException
	 */
	public void createUser(User adminUser, UserUploadVO user) throws InvalidInputException, UserAdditionException, NoRecordsFetchedException,
			SolrException, UserAssignmentException;

	/**
	 * Creates a branch and assigns it under the appropriate region or company
	 * @param adminUser
	 * @param branch
	 * @throws InvalidInputException
	 * @throws BranchAdditionException
	 * @throws SolrException
	 * @throws NoRecordsFetchedException
	 */
	public void createBranch(User adminUser, BranchUploadVO branch) throws InvalidInputException, BranchAdditionException, SolrException, NoRecordsFetchedException;

	/**
	 * Creates a region
	 * @param adminUser
	 * @param region
	 * @throws InvalidInputException
	 * @throws RegionAdditionException
	 * @throws SolrException
	 */
	public void createRegion(User adminUser, RegionUploadVO region) throws InvalidInputException, RegionAdditionException, SolrException;

	/**
	 * Takes a map of objects and creates them and returns list of errors if any
	 * @param uploadObjects
	 * @param adminUser
	 * @return
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 * @throws SolrException
	 * @throws UserAssignmentException
	 */
	public List<String> createAndReturnErrors(Map<String, List<Object>> uploadObjects, User adminUser) throws InvalidInputException,
			NoRecordsFetchedException, SolrException, UserAssignmentException;

}
