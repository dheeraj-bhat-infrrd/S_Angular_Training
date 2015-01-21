/**
 * JIRA:SS-62 BY RM 02 BOC
 */
package com.realtech.socialsurvey.core.services.search;

import java.net.MalformedURLException;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;

/**
 * Holds method to perform search from solr
 */
public interface SolrSearchService {

	/**
	 * Method to perform search of regions from solr based on the input pattern and company
	 * 
	 * @param regionPattern
	 * @param company
	 * @param start
	 * @param rows
	 * @return
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	public String searchRegions(String regionPattern, Company company, int start, int rows) throws InvalidInputException, SolrException;

	/**
	 * Method to perform search of branches from solr based on the input pattern and company
	 * 
	 * @param branchPattern
	 * @param company
	 * @param start
	 * @param rows
	 * @return
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	public String searchBranches(String branchPattern, Company company, int start, int rows) throws InvalidInputException, SolrException;

	/**
	 * Method to add a region to solr
	 * 
	 * @param region
	 * @throws SolrException
	 */
	public void addOrUpdateRegionToSolr(Region region) throws SolrException;

	/**
	 * Method to add a branch to solr
	 * 
	 * @param branch
	 * @throws SolrException
	 */
	public void addOrUpdateBranchToSolr(Branch branch) throws SolrException;

	public String searchUsersByLoginNameAndCompany(String userNamePattern, Company company) throws InvalidInputException, SolrException,
			MalformedURLException;

	public void addUserToSolr(User user) throws SolrException;

	public String searchUsersByLoginNameOrName(String pattern, long companyId) throws InvalidInputException, SolrException, MalformedURLException;

	public String searchUsersByCompany(long companyId, int startIndex, int noOfRows) throws InvalidInputException, SolrException,
			MalformedURLException;

	public void removeUserFromSolr(long userIdToRemove) throws SolrException;
}
// JIRA:SS-62 BY RM 02 EOC