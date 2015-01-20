/**
 * JIRA:SS-62 BY RM 02 BOC
 */
package com.realtech.socialsurvey.core.services.search;

import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.Region;
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
	 * @return
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	public String searchRegions(String regionPattern, Company company) throws InvalidInputException, SolrException;

	/**
	 * Method to perform search of branches from solr based on the input pattern and company
	 * 
	 * @param branchPattern
	 * @param company
	 * @return
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	public String searchBranches(String branchPattern, Company company) throws InvalidInputException, SolrException;

	/**
	 * Method to add a region to solr
	 * @param region
	 * @throws SolrException
	 */
	public void addOrUpdateRegionToSolr(Region region) throws SolrException;

	/**
	 * Method to add a branch to solr
	 * @param branch
	 * @throws SolrException
	 */
	public void addOrUpdateBranchToSolr(Branch branch) throws SolrException;
}
// JIRA:SS-62 BY RM 02 EOC