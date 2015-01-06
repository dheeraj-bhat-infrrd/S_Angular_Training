package com.realtech.socialsurvey.core.services.search.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.utils.SolrSearchUtils;

// JIRA:SS-62 BY RM 02
/**
 * Implementation class for solr search services
 */
@Component
public class SolrSearchServiceImpl implements SolrSearchService {

	private static final Logger LOG = LoggerFactory.getLogger(SolrSearchServiceImpl.class);

	@Value("${SOLR_REGION_SEARCH_URL}")
	private String solrRegionSearchUrl;

	@Value("${SOLR_BRANCH_SEARCH_URL}")
	private String solrBranchSearchUrl;

	@Autowired
	private SolrSearchUtils solrSearchUtils;

	/**
	 * Method to perform search of regions from solr based on the input pattern and user
	 * 
	 * @throws InvalidInputException
	 */
	@Override
	public String searchRegions(String regionPattern, Company company) throws InvalidInputException {
		LOG.info("Method searchRegions called for regionPattern :" + regionPattern);
		if (regionPattern == null || regionPattern.isEmpty()) {
			throw new InvalidInputException("Region pattern is null or empty while searching for region");
		}
		if (company == null) {
			throw new InvalidInputException("company is null or empty while searching for region");
		}
		LOG.info("Method searchRegions called for regionPattern : " + regionPattern + " and company : " + company);
		String regionResult = null;
		InputStream response = null;
		try {
			regionPattern = regionPattern + "*";

			String solrSearchQuery = solrRegionSearchUrl + "search?wt=json&indent=true&fq=companyId:" + company.getCompanyId() + "&q=regionName:"
					+ URLEncoder.encode(regionPattern, "ISO-8859-1") + "&fq=status:" + CommonConstants.STATUS_ACTIVE;
			LOG.debug("Searching region: " + solrSearchQuery);

			response = new URL(solrSearchQuery).openStream();
			regionResult = solrSearchUtils.getStringFromInputStream(response);
		}
		catch (UnsupportedEncodingException e) {
			LOG.error("UnsupportedEncodingException while performing region search");
			throw new FatalException("Exception while performing search. Reason : " + e.getMessage(), e);
		}
		catch (MalformedURLException e) {
			LOG.error("MalformedURLException while performing region search", e);
			throw new FatalException("Exception while performing search. Reason : " + e.getMessage(), e);
		}
		catch (IOException e) {
			LOG.error("IOException while performing region search", e);
			throw new FatalException("Exception while performing search. Reason : " + e.getMessage(), e);
		}
		finally {
			try {
				response.close();
			}
			catch (IOException e) {
				LOG.error("IOException while closing response", e);
				throw new FatalException("Exception closing response while performing search. Reason : " + e.getMessage(), e);
			}
		}

		LOG.info("Method searchRegions finished for regionPattern :" + regionPattern + " returning : " + regionResult);
		return regionResult;
	}

	/**
	 * Method to perform search of branches from solr based on the input pattern and company
	 * 
	 * @throws InvalidInputException
	 */
	@Override
	public String searchBranches(String branchPattern, Company company) throws InvalidInputException {
		LOG.info("Method searchBranches called for branchPattern :" + branchPattern);
		if (branchPattern == null || branchPattern.isEmpty()) {
			throw new InvalidInputException("Branch pattern is null or empty while searching for branch");
		}
		if (company == null) {
			throw new InvalidInputException("Company is null or empty while searching for branch");
		}
		LOG.info("Method searchBranches called for branchPattern : " + branchPattern + " and company : " + company);
		String branchResult = null;
		InputStream response = null;
		try {
			branchPattern = branchPattern + "*";

			String solrSearchQuery = solrBranchSearchUrl + "search?wt=json&indent=true&fq=companyId:" + company.getCompanyId() + "&q=branchName:"
					+ URLEncoder.encode(branchPattern, "ISO-8859-1") + "&fq=status:" + CommonConstants.STATUS_ACTIVE;
			LOG.debug("Searching branch: " + solrSearchQuery);

			response = new URL(solrSearchQuery).openStream();
			branchResult = solrSearchUtils.getStringFromInputStream(response);
		}
		catch (UnsupportedEncodingException e) {
			LOG.error("UnsupportedEncodingException while performing branch search");
			throw new FatalException("Exception while performing search. Reason : " + e.getMessage(), e);
		}
		catch (MalformedURLException e) {
			LOG.error("MalformedURLException while performing branch search", e);
			throw new FatalException("Exception while performing search. Reason : " + e.getMessage(), e);
		}
		catch (IOException e) {
			LOG.error("IOException while performing branch search", e);
			throw new FatalException("Exception while performing search. Reason : " + e.getMessage(), e);
		}
		finally {
			try {
				response.close();
			}
			catch (IOException e) {
				LOG.error("IOException while closing response", e);
				throw new FatalException("Exception closing response while performing search. Reason : " + e.getMessage(), e);
			}
		}
		LOG.info("Method searchBranches finished for branchPattern :" + branchPattern);
		return branchResult;
	}

}
