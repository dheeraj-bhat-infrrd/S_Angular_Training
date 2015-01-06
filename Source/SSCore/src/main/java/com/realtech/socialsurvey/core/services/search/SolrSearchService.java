/**
 * JIRA:SS-62 BY RM 02 BOC
 */
package com.realtech.socialsurvey.core.services.search;

import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

/**
 * Holds method to perform search from solr
 */
public interface SolrSearchService {

	/**
	 * Method to perform search of regions from solr based on the input pattern and company
	 * 
	 * @param regionPattern
	 * @param company
	 * @return
	 * @throws InvalidInputException
	 */
	public String searchRegions(String regionPattern, Company company) throws InvalidInputException;

	/**
	 * Method to perform search of branches from solr based on the input pattern and company
	 * 
	 * @param branchPattern
	 * @param company
	 * @return
	 * @throws InvalidInputException
	 */
	public String searchBranches(String branchPattern, Company company) throws InvalidInputException;
}
// JIRA:SS-62 BY RM 02 EOC