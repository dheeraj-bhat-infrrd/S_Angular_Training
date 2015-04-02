package com.realtech.socialsurvey.core.services.search.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.noggit.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.utils.SolrSearchUtils;

// JIRA:SS-62 BY RM 02
/**
 * Implementation class for solr search services
 */
@Component
public class SolrSearchServiceImpl implements SolrSearchService {

	private static final Logger LOG = LoggerFactory.getLogger(SolrSearchServiceImpl.class);
	private static final String SOLR_EDIT_REPLACE = "set";

	@Value("${SOLR_REGION_URL}")
	private String solrRegionUrl;

	@Value("${SOLR_BRANCH_URL}")
	private String solrBranchUrl;

	@Value("${SOLR_USER_URL}")
	private String solrUserUrl;

	@Autowired
	private SolrSearchUtils solrSearchUtils;

	/**
	 * Method to perform search of regions from solr based on input pattern , company and regionIds
	 * if provided
	 */
	@Override
	public String searchRegions(String regionPattern, Company company, Set<Long> regionIds, int start, int rows) throws InvalidInputException,
			SolrException {
		LOG.info("Method searchRegions called for regionPattern :" + regionPattern);
		if (regionPattern == null) {
			throw new InvalidInputException("Region pattern is null while searching for region");
		}
		if (company == null) {
			throw new InvalidInputException("company is null or empty while searching for region");
		}
		LOG.info("Method searchRegions called for regionPattern : " + regionPattern + " and company : " + company);
		String regionResult = null;
		QueryResponse response = null;
		try {
			regionPattern = regionPattern + "*";

			SolrServer solrServer = new HttpSolrServer(solrRegionUrl);
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setQuery(CommonConstants.REGION_NAME_SOLR + ":" + regionPattern);
			solrQuery.addFilterQuery(CommonConstants.COMPANY_ID_SOLR + ":" + company.getCompanyId(), CommonConstants.STATUS_COLUMN + ":"
					+ CommonConstants.STATUS_ACTIVE, CommonConstants.IS_DEFAULT_BY_SYSTEM_SOLR + ":" + CommonConstants.NO);

			if (regionIds != null && !regionIds.isEmpty()) {
				String regionIdsStr = getSpaceSeparatedStringFromIds(regionIds);
				solrQuery.addFilterQuery(CommonConstants.REGION_ID_SOLR + ":(" + regionIdsStr + ")");
			}

			solrQuery.setStart(start);
			if (rows > 0) {
				solrQuery.setRows(rows);
			}

			LOG.debug("Querying solr for searching regions");
			response = solrServer.query(solrQuery);
			SolrDocumentList results = response.getResults();
			regionResult = JSONUtil.toJSON(results);
			LOG.debug("Region search result is : " + regionResult);

		}
		catch (SolrServerException e) {
			LOG.error("UnsupportedEncodingException while performing region search");
			throw new SolrException("Exception while performing search. Reason : " + e.getMessage(), e);
		}

		LOG.info("Method searchRegions finished for regionPattern :" + regionPattern + " returning : " + regionResult);
		return regionResult;
	}

	/**
	 * Method to perform search of branches from solr based on the input pattern, company and
	 * branchIds
	 */
	@Override
	public String searchBranches(String branchPattern, Company company, String idColumnName, Set<Long> branchIds, int start, int rows)
			throws InvalidInputException, SolrException {
		LOG.info("Method searchBranches called for branchPattern :" + branchPattern + " idColumnName:" + idColumnName);
		if (branchPattern == null) {
			throw new InvalidInputException("Branch pattern is null while searching for branch");
		}
		if (company == null) {
			throw new InvalidInputException("Company is null while searching for branch");
		}
		LOG.info("Method searchBranches called for branchPattern : " + branchPattern + " and company : " + company);
		String branchResult = null;
		QueryResponse response = null;
		try {

			branchPattern = branchPattern + "*";

			SolrServer solrServer = new HttpSolrServer(solrBranchUrl);
			SolrQuery query = new SolrQuery();
			query.setQuery(CommonConstants.BRANCH_NAME_SOLR + ":" + branchPattern);
			query.addFilterQuery(CommonConstants.COMPANY_ID_SOLR + ":" + company.getCompanyId(), CommonConstants.STATUS_SOLR + ":"
					+ CommonConstants.STATUS_ACTIVE, CommonConstants.IS_DEFAULT_BY_SYSTEM_SOLR + ":" + CommonConstants.NO);
			query.setStart(start);
			if (branchIds != null && !branchIds.isEmpty()) {
				if (idColumnName == null || idColumnName.isEmpty()) {
					throw new InvalidInputException("column name is not specified in search branches");
				}
				String idsStr = getSpaceSeparatedStringFromIds(branchIds);
				query.addFilterQuery(idColumnName + ":(" + idsStr + ")");
			}
			if (rows > 0) {
				query.setRows(rows);
			}

			LOG.debug("Querying solr for searching branches");
			response = solrServer.query(query);
			SolrDocumentList documentList = response.getResults();
			branchResult = JSONUtil.toJSON(documentList);

			LOG.debug("Results obtained from solr :" + branchResult);
		}
		catch (SolrServerException e) {
			LOG.error("SolrServerException while performing branch search");
			throw new SolrException("Exception while performing search. Reason : " + e.getMessage(), e);
		}
		LOG.info("Method searchBranches finished for branchPattern :" + branchPattern);
		return branchResult;
	}

	/**
	 * Method to perform search of branches from solr based on the region id.
	 * 
	 * @param regionId
	 * @param start
	 * @param rows
	 * @return list of branches
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	public String searchBranchesByRegion(long regionId, int start, int rows) throws InvalidInputException, SolrException {
		LOG.info("Method searchBranchesByRegion() to search branches in a region started");
		String branchResult = null;
		QueryResponse response = null;
		try {

			SolrServer solrServer = new HttpSolrServer(solrBranchUrl);
			SolrQuery query = new SolrQuery();
			query.setQuery(CommonConstants.REGION_ID_SOLR + ":" + regionId);
			query.addFilterQuery(CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE, CommonConstants.IS_DEFAULT_BY_SYSTEM_SOLR + ":"
					+ CommonConstants.NO);
			query.setStart(start);

			if (rows > 0) {
				query.setRows(rows);
			}

			LOG.debug("Querying solr for searching branches");
			response = solrServer.query(query);
			SolrDocumentList documentList = response.getResults();
			branchResult = JSONUtil.toJSON(documentList);

			LOG.debug("Results obtained from solr :" + branchResult);
		}
		catch (SolrServerException e) {
			LOG.error("SolrServerException while performing branch search");
			throw new SolrException("Exception while performing search. Reason : " + e.getMessage(), e);
		}
		LOG.info("Method searchBranchesByRegion() to search branches in a region finished");
		return branchResult;
	}

	/**
	 * Method to add region into solr
	 */
	@Override
	public void addOrUpdateRegionToSolr(Region region) throws SolrException {
		LOG.info("Method to add or update region to solr called for region : " + region);
		SolrServer solrServer;
		try {

			solrServer = new HttpSolrServer(solrRegionUrl);
			SolrInputDocument document = getSolrDocumentFromRegion(region);
			UpdateResponse response = solrServer.add(document);
			LOG.debug("response is while adding/updating region is : " + response);
			solrServer.commit();
		}
		catch (MalformedURLException e) {
			LOG.error("Exception while adding/updating regions to solr. Reason : " + e.getMessage(), e);
			throw new SolrException("Exception while adding/updating regions to solr. Reason : " + e.getMessage(), e);
		}
		catch (SolrServerException | IOException e) {
			LOG.error("Exception while adding/updating regions to solr. Reason : " + e.getMessage(), e);
			throw new SolrException("Exception while adding/updating regions to solr. Reason : " + e.getMessage(), e);
		}
		LOG.info("Method to add or update region to solr finshed for region : " + region);

	}

	/**
	 * Method to add branch into solr
	 */
	@Override
	public void addOrUpdateBranchToSolr(Branch branch) throws SolrException {
		LOG.info("Method to add/update branch to solr called for branch : " + branch);
		SolrServer solrServer;
		try {
			solrServer = new HttpSolrServer(solrBranchUrl);
			SolrInputDocument document = getSolrDocumentFromBranch(branch);

			UpdateResponse response = solrServer.add(document);
			LOG.debug("response while adding/updating branch is : " + response);
			solrServer.commit();
		}
		catch (MalformedURLException e) {
			LOG.error("Exception while adding/updating branch to solr. Reason : " + e.getMessage(), e);
			throw new SolrException("Exception while adding/updating branch to solr. Reason : " + e.getMessage(), e);
		}
		catch (SolrServerException | IOException e) {
			LOG.error("Exception while adding/updating branch to solr. Reason : " + e.getMessage(), e);
			throw new SolrException("Exception while adding/updating branch to solr. Reason : " + e.getMessage(), e);
		}
		LOG.info("Method to add/update branch to solr finshed for branch : " + branch);
	}

	/**
	 * Method to get solr input document from branch
	 * 
	 * @param branch
	 * @return
	 */
	private SolrInputDocument getSolrDocumentFromBranch(Branch branch) {
		LOG.debug("Method getSolrDocumentFromBranch called for branch " + branch);

		SolrInputDocument document = new SolrInputDocument();
		document.addField(CommonConstants.REGION_ID_SOLR, branch.getRegion().getRegionId());
		document.addField(CommonConstants.REGION_NAME_SOLR, branch.getRegion().getRegion());
		document.addField(CommonConstants.BRANCH_ID_SOLR, branch.getBranchId());
		document.addField(CommonConstants.BRANCH_NAME_SOLR, branch.getBranch());
		document.addField(CommonConstants.COMPANY_ID_SOLR, branch.getCompany().getCompanyId());
		document.addField(CommonConstants.IS_DEFAULT_BY_SYSTEM_SOLR, branch.getIsDefaultBySystem());
		document.addField(CommonConstants.STATUS_SOLR, branch.getStatus());

		String address = branch.getAddress1();
		if (address != null && branch.getAddress2() != null) {
			address = address + " " + branch.getAddress2();
		}
		document.addField(CommonConstants.BRANCH_ADDRESS_SOLR, address);

		LOG.debug("Method getSolrDocumentFromBranch finished for branch " + branch);
		return document;
	}

	/**
	 * Method to get solr document from a region
	 * 
	 * @param region
	 * @return
	 */
	private SolrInputDocument getSolrDocumentFromRegion(Region region) {
		LOG.debug("Method getSolrDocumentFromRegion called for region " + region);

		SolrInputDocument document = new SolrInputDocument();
		document.addField(CommonConstants.REGION_ID_SOLR, region.getRegionId());
		document.addField(CommonConstants.REGION_NAME_SOLR, region.getRegion());
		document.addField(CommonConstants.COMPANY_ID_SOLR, region.getCompany().getCompanyId());
		document.addField(CommonConstants.IS_DEFAULT_BY_SYSTEM_SOLR, region.getIsDefaultBySystem());
		document.addField(CommonConstants.STATUS_SOLR, region.getStatus());
		String address = region.getAddress1();
		if (address != null && region.getAddress2() != null) {
			address = address + " " + region.getAddress2();
		}
		document.addField(CommonConstants.REGION_ADDRESS_SOLR, address);

		LOG.debug("Method getSolrDocumentFromRegion finished for region " + region);
		return document;
	}

	/**
	 * Method to perform search of Users from solr based on the input pattern for user and company.
	 * 
	 * @throws InvalidInputException
	 * @throws SolrException
	 * @throws MalformedURLException
	 */
	@Override
	public String searchUsersByLoginNameAndCompany(String loginNamePattern, Company company) throws InvalidInputException, SolrException,
			MalformedURLException {
		LOG.info("Method searchUsers called for userNamePattern :" + loginNamePattern);
		if (loginNamePattern == null || loginNamePattern.isEmpty()) {
			throw new InvalidInputException("Username pattern is null or empty while searching for Users");
		}
		if (company == null) {
			throw new InvalidInputException("company is null or empty while searching for users");
		}
		LOG.info("Method searchUsers() called for userNamePattern : " + loginNamePattern + " and company : " + company);
		String usersResult = null;
		QueryResponse response = null;
		try {
			loginNamePattern = loginNamePattern + "*";

			SolrServer solrServer = new HttpSolrServer(solrUserUrl);
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setQuery(CommonConstants.USER_LOGIN_NAME_SOLR + ":" + loginNamePattern);
			solrQuery.addFilterQuery(CommonConstants.COMPANY_ID_SOLR + ":" + company.getCompanyId(), CommonConstants.STATUS_SOLR + ":"
					+ CommonConstants.STATUS_ACTIVE);

			LOG.debug("Querying solr for searching users");
			response = solrServer.query(solrQuery);
			SolrDocumentList results = response.getResults();
			usersResult = JSONUtil.toJSON(results);
			LOG.debug("User search result is : " + usersResult);

		}
		catch (SolrServerException e) {
			LOG.error("UnsupportedEncodingException while performing User search");
			throw new SolrException("Exception while performing search for user. Reason : " + e.getMessage(), e);
		}

		LOG.info("Method searchUsers finished for username pattern :" + loginNamePattern + " returning : " + usersResult);
		return usersResult;
	}

	/**
	 * Method to perform search of Users from solr based on the input pattern for user and company.
	 * 
	 * @throws InvalidInputException
	 * @throws SolrException
	 * @throws MalformedURLException
	 * @throws UnsupportedEncodingException
	 */
	@Override
	public String searchUsersByLoginNameOrName(String pattern, long companyId) throws InvalidInputException, SolrException, MalformedURLException {
		LOG.info("Method searchUsersByLoginNameOrName called for pattern :" + pattern);
		if (pattern == null) {
			throw new InvalidInputException("Pattern is null or empty while searching for Users");
		}
		LOG.info("Method searchUsersByLoginNameOrName() called for parameter : " + pattern);
		String usersResult = null;
		QueryResponse response = null;
		pattern = pattern + "*";
		try {
			SolrServer solrServer = new HttpSolrServer(solrUserUrl);
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setQuery("displayName:" + pattern + " OR " + CommonConstants.USER_FIRST_NAME_SOLR + ":" + pattern + " OR "
					+ CommonConstants.USER_LAST_NAME_SOLR + ":" + pattern + " OR " + CommonConstants.USER_LOGIN_NAME_SOLR + ":" + pattern);
			solrQuery.addFilterQuery("companyId:" + companyId);
			solrQuery.addFilterQuery(CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE + " OR " + CommonConstants.STATUS_SOLR + ":"
					+ CommonConstants.STATUS_NOT_VERIFIED + " OR " + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_TEMPORARILY_INACTIVE);
			solrQuery.addSort(CommonConstants.USER_DISPLAY_NAME_SOLR, ORDER.asc);
			LOG.debug("Querying solr for searching users");
			response = solrServer.query(solrQuery);
			SolrDocumentList results = response.getResults();
			usersResult = JSONUtil.toJSON(results);
			LOG.debug("User search result is : " + usersResult);
		}
		catch (SolrServerException e) {
			LOG.error("SolrServerException while performing User search");
			throw new SolrException("Exception while performing search for user. Reason : " + e.getMessage(), e);
		}

		LOG.info("Method searchUsersByLoginNameOrName finished for pattern :" + pattern + " returning : " + usersResult);
		return usersResult;
	}

	@Override
	public List<SolrDocument> searchUsersByFirstOrLastName(String patternFirst, String patternLast) throws InvalidInputException, SolrException,
			MalformedURLException {
		LOG.info("Method searchUsersByFirstOrLastName() called for pattern :" + patternFirst + ", " + patternLast);
		if (patternFirst == null && patternLast == null) {
			throw new InvalidInputException("Pattern is null or empty while searching for Users");
		}

		List<SolrDocument> users = new ArrayList<SolrDocument>();
		QueryResponse response = null;
		try {
			SolrQuery solrQuery = new SolrQuery();
			String[] fields = { CommonConstants.USER_FIRST_NAME_SOLR, CommonConstants.USER_LAST_NAME_SOLR, CommonConstants.USER_DISPLAY_NAME_SOLR,
					CommonConstants.USER_EMAIL_ID_SOLR };
			solrQuery.setFields(fields);

			String query = "";
			if (!patternFirst.equals("") && !patternLast.equals("")) {
				query = CommonConstants.USER_FIRST_NAME_SOLR + ":" + patternFirst + "*" + " OR " + CommonConstants.USER_LAST_NAME_SOLR + ":"
						+ patternLast + "*";
			}
			else if (!patternFirst.equals("") && patternLast.equals("")) {
				query = CommonConstants.USER_FIRST_NAME_SOLR + ":" + patternFirst + "*";
			}
			else if (patternFirst.equals("") && !patternLast.equals("")) {
				query = CommonConstants.USER_LAST_NAME_SOLR + ":" + patternLast + "*";
			}
			solrQuery.setQuery(query);

			solrQuery.addFilterQuery(CommonConstants.IS_AGENT_SOLR + ":" + CommonConstants.IS_AGENT_TRUE_SOLR);

			LOG.debug("Querying solr for searching users");
			SolrServer solrServer = new HttpSolrServer(solrUserUrl);
			response = solrServer.query(solrQuery);
			SolrDocumentList results = response.getResults();
			for (SolrDocument solrDocument : results) {
				users.add(solrDocument);
			}
			LOG.debug("User search result size is : " + users.size());
		}
		catch (SolrServerException e) {
			LOG.error("SolrServerException while performing User search");
			throw new SolrException("Exception while performing search for user. Reason : " + e.getMessage(), e);
		}
		LOG.info("Method searchUsersByFirstOrLastName() called for parameter : " + patternFirst + ", " + patternLast + " returning : " + users);
		return users;
	}

	@Override
	public SolrDocumentList searchUsersByFirstOrLastName(String patternFirst, String patternLast, int startIndex, int noOfRows)
			throws InvalidInputException, SolrException, MalformedURLException {
		LOG.info("Method searchUsersByFirstOrLastName() called for pattern :" + patternFirst + ", " + patternLast);
		if (patternFirst == null && patternLast == null) {
			throw new InvalidInputException("Pattern is null or empty while searching for Users");
		}

		QueryResponse response = null;
		try {
			SolrQuery solrQuery = new SolrQuery();

			String[] fields = { CommonConstants.USER_ID_SOLR, CommonConstants.USER_DISPLAY_NAME_SOLR, CommonConstants.TITLE_SOLR,
					CommonConstants.ABOUT_ME_SOLR, CommonConstants.PROFILE_IMAGE_URL_SOLR, CommonConstants.PROFILE_URL_SOLR };
			solrQuery.setFields(fields);

			String query = "";
			if (!patternFirst.equals("") && !patternLast.equals("")) {
				query = CommonConstants.USER_FIRST_NAME_SOLR + ":" + patternFirst + "*" + " OR " + CommonConstants.USER_LAST_NAME_SOLR + ":"
						+ patternLast + "*";
			}
			else if (!patternFirst.equals("") && patternLast.equals("")) {
				query = CommonConstants.USER_FIRST_NAME_SOLR + ":" + patternFirst + "*";
			}
			else if (patternFirst.equals("") && !patternLast.equals("")) {
				query = CommonConstants.USER_LAST_NAME_SOLR + ":" + patternLast + "*";
			}
			solrQuery.setQuery(query);
			solrQuery.addFilterQuery(CommonConstants.IS_AGENT_SOLR + ":" + CommonConstants.IS_AGENT_TRUE_SOLR);
			solrQuery.setStart(startIndex);
			solrQuery.setRows(noOfRows);

			LOG.debug("Querying solr for searching users");
			SolrServer solrServer = new HttpSolrServer(solrUserUrl);
			response = solrServer.query(solrQuery);
		}
		catch (SolrServerException e) {
			LOG.error("SolrServerException while performing User search");
			throw new SolrException("Exception while performing search for user. Reason : " + e.getMessage(), e);
		}
		LOG.info("Method searchUsersByFirstOrLastName() called for parameter : " + patternFirst + ", " + patternLast + " returning : "
				+ response.getResults());
		return response.getResults();
	}

	/**
	 * Method to perform search of Users from solr based on the input pattern for user and company.
	 * 
	 * @throws InvalidInputException
	 * @throws SolrException
	 * @throws MalformedURLException
	 */
	@Override
	public String searchUsersByCompany(long companyId, int startIndex, int noOfRows) throws InvalidInputException, SolrException,
			MalformedURLException {
		if (companyId < 0) {
			throw new InvalidInputException("Pattern is null or empty while searching for Users");
		}
		LOG.info("Method searchUsersByCompanyId() called for company id : " + companyId);
		String usersResult = null;
		QueryResponse response = null;
		try {
			SolrServer solrServer = new HttpSolrServer(solrUserUrl);
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setQuery(CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE + " OR " + CommonConstants.STATUS_SOLR + ":"
					+ CommonConstants.STATUS_NOT_VERIFIED + " OR " + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_TEMPORARILY_INACTIVE);
			solrQuery.addFilterQuery(CommonConstants.COMPANY_ID_SOLR + ":" + companyId);
			solrQuery.setStart(startIndex);
			solrQuery.setRows(noOfRows);
			solrQuery.addSort(CommonConstants.USER_DISPLAY_NAME_SOLR, ORDER.asc);
			LOG.debug("Querying solr for searching users");
			response = solrServer.query(solrQuery);
			SolrDocumentList results = response.getResults();
			usersResult = JSONUtil.toJSON(results);
			LOG.debug("User search result is : " + usersResult);
		}
		catch (SolrServerException e) {
			LOG.error("SolrServerException while performing User search");
			throw new SolrException("Exception while performing search for user. Reason : " + e.getMessage(), e);
		}

		LOG.info("Method searchUsersByCompanyId() finished for company id : " + companyId);
		return usersResult;
	}

	@Override
	public long countUsersByCompany(long companyId, int startIndex, int noOfRows) throws InvalidInputException, SolrException, MalformedURLException {
		LOG.info("Method countUsersByCompany() called for company id : " + companyId);
		if (companyId < 0) {
			throw new InvalidInputException("Pattern is null or empty while searching for Users");
		}

		long resultsCount = 0l;
		QueryResponse response = null;
		try {
			SolrServer solrServer = new HttpSolrServer(solrUserUrl);
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setQuery(CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE + " OR " + CommonConstants.STATUS_SOLR + ":"
					+ CommonConstants.STATUS_NOT_VERIFIED + " OR " + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_TEMPORARILY_INACTIVE);
			solrQuery.addFilterQuery(CommonConstants.COMPANY_ID_SOLR + ":" + companyId);
			solrQuery.setStart(startIndex);
			solrQuery.setRows(noOfRows);
			response = solrServer.query(solrQuery);

			resultsCount = response.getResults().getNumFound();
			LOG.debug("User search result count is : " + resultsCount);
		}
		catch (SolrServerException e) {
			LOG.error("SolrServerException while performing User search");
			throw new SolrException("Exception while performing search for user. Reason : " + e.getMessage(), e);
		}

		LOG.info("Method countUsersByCompany() finished for company id : " + companyId);
		return resultsCount;
	}

	/**
	 * Method to add User into solr
	 */
	@Override
	public void addUserToSolr(User user) throws SolrException {
		LOG.info("Method to add user to solr called for user : " + user.getFirstName());
		SolrServer solrServer;
		UpdateResponse response = null;
		try {
			solrServer = new HttpSolrServer(solrUserUrl);
			SolrInputDocument document = new SolrInputDocument();
			document.addField(CommonConstants.USER_ID_SOLR, user.getUserId());
			document.addField(CommonConstants.USER_FIRST_NAME_SOLR, user.getFirstName());
			document.addField(CommonConstants.USER_LAST_NAME_SOLR, user.getLastName());
			document.addField(CommonConstants.USER_EMAIL_ID_SOLR, user.getEmailId());
			document.addField(CommonConstants.USER_LOGIN_NAME_COLUMN, user.getEmailId());
			document.addField(CommonConstants.USER_IS_OWNER_SOLR, user.getIsOwner());

			String displayName = user.getFirstName();
			if (user.getLastName() != null) {
				displayName = displayName + " " + user.getLastName();
			}
			document.addField(CommonConstants.USER_DISPLAY_NAME_SOLR, displayName);

			/**
			 * add/update profile url and profile name in solr only when they are not null
			 */
			if (user.getProfileName() != null && !user.getProfileName().isEmpty()) {
				document.addField(CommonConstants.PROFILE_NAME_SOLR, user.getProfileName());
			}
			if (user.getProfileUrl() != null && !user.getProfileUrl().isEmpty()) {
				document.addField(CommonConstants.PROFILE_URL_SOLR, user.getProfileUrl());
			}

			if (user.getCompany() != null) {
				document.addField(CommonConstants.COMPANY_ID_SOLR, user.getCompany().getCompanyId());
			}
			document.addField(CommonConstants.STATUS_SOLR, user.getStatus());
			Set<Long> branches = new HashSet<Long>();
			Set<Long> regions = new HashSet<Long>();
			if (user.getUserProfiles() != null)
				for (UserProfile userProfile : user.getUserProfiles()) {
					if (userProfile.getRegionId() != 0) {
						regions.add(userProfile.getRegionId());
					}
					if (userProfile.getBranchId() != 0) {
						branches.add(userProfile.getBranchId());
					}
				}
			document.addField(CommonConstants.BRANCHES_SOLR, branches);
			document.addField(CommonConstants.REGIONS_SOLR, regions);
			document.addField(CommonConstants.IS_AGENT_SOLR, user.isAgent());
			document.addField(CommonConstants.IS_BRANCH_ADMIN_SOLR, user.isBranchAdmin());
			document.addField(CommonConstants.IS_REGION_ADMIN_SOLR, user.isRegionAdmin());
			LOG.debug("response while adding user is: " + response);
			solrServer.add(document);
			solrServer.commit();
		}
		catch (MalformedURLException e) {
			LOG.error("Exception while adding user to solr. Reason : " + e.getMessage(), e);
			throw new SolrException("Exception while adding user to solr. Reason : " + e.getMessage(), e);
		}
		catch (SolrServerException | IOException e) {
			LOG.error("Exception while adding user to solr. Reason : " + e.getMessage(), e);
			throw new SolrException("Exception while adding user to solr. Reason : " + e.getMessage(), e);
		}
		LOG.info("Method to add user to solr finshed for user : " + user);
	}

	/*
	 * Method to remove a user from Solr
	 */
	@Override
	public void removeUserFromSolr(long userIdToRemove) throws SolrException {
		LOG.info("Method removeUserFromSolr() to remove user id {} from solr started.", userIdToRemove);
		try {
			SolrServer solrServer = new HttpSolrServer(solrUserUrl);
			solrServer.deleteById(String.valueOf(userIdToRemove));
			solrServer.commit();
		}
		catch (SolrServerException | IOException e) {
			LOG.error("Exception while removing user from solr. Reason : " + e.getMessage(), e);
			throw new SolrException("Exception while removing user from solr. Reason : " + e.getMessage(), e);
		}
		LOG.info("Method removeUserFromSolr() to remove user id {} from solr finished successfully.", userIdToRemove);
	}

	/*
	 * Method to fetch display name of a user from solr based upon user id provided.
	 */
	@Override
	public String getUserDisplayNameById(long userId) throws InvalidInputException, NoRecordsFetchedException, SolrServerException {
		LOG.info("Method to fetch user from solr based upon user id, searchUserById() started.");
		SolrDocument solrDocument = getUserByUniqueId(userId);
		if (solrDocument == null || solrDocument.isEmpty()) {
			throw new NoRecordsFetchedException("No document found in solr for userId:" + userId);
		}
		String displayName = solrDocument.get(CommonConstants.USER_DISPLAY_NAME_SOLR).toString();
		LOG.info("Method to fetch user from solr based upon user id, searchUserById() finished.");
		return displayName;
	}

	/**
	 * Method to fetch user based on the userid provided
	 */
	@Override
	public SolrDocument getUserByUniqueId(long userId) throws InvalidInputException, SolrServerException {
		LOG.info("Method getUserByUniqueId called for userId:" + userId);
		if (userId <= 0l) {
			throw new InvalidInputException("userId is invalid for getting user from solr");
		}
		SolrDocument solrDocument = null;
		QueryResponse response = null;
		SolrServer solrServer = new HttpSolrServer(solrUserUrl);
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(CommonConstants.USER_ID_SOLR + ":" + userId);

		LOG.debug("Querying solr for searching users");
		response = solrServer.query(solrQuery);
		SolrDocumentList results = response.getResults();

		if (results != null && !results.isEmpty()) {
			solrDocument = results.get(CommonConstants.INITIAL_INDEX);
		}
		else {
			LOG.debug("No user present in solr for the userId:" + userId);
		}
		LOG.info("Method getUserByUniqueId executed succesfully. Returning :" + solrDocument);
		return solrDocument;

	}

	/**
	 * Method to edit User in solr
	 */
	@Override
	public void editUserInSolr(long userId, String key, String value) throws SolrException {
		LOG.info("Method to edit user in solr called for user : " + userId);

		try {
			// Setting values to Map with instruction
			Map<String, String> editKeyValues = new HashMap<String, String>();
			editKeyValues.put(SOLR_EDIT_REPLACE, value);

			// Adding fields to be updated
			SolrInputDocument document = new SolrInputDocument();
			document.setField(CommonConstants.USER_ID_SOLR, userId);
			document.setField(key, editKeyValues);

			SolrServer solrServer = new HttpSolrServer(solrUserUrl);
			solrServer.add(document);
			solrServer.commit();
		}
		catch (SolrServerException | IOException e) {
			LOG.error("Exception while editing user in solr. Reason : " + e.getMessage(), e);
			throw new SolrException("Exception while adding regions to solr. Reason : " + e.getMessage(), e);
		}
		LOG.info("Method to edit user in solr finished for user : " + userId);
	}

	@Override
	public SolrDocumentList searchUsersByIden(long iden, String idenFieldName, boolean isAgent, int startIndex, int noOfRows)
			throws InvalidInputException, SolrException {
		LOG.info("Method searchUsersByIden called for iden :" + iden + "idenFieldName:" + idenFieldName + " startIndex:" + startIndex + " noOfrows:"
				+ noOfRows);
		if (iden <= 0l) {
			throw new InvalidInputException("iden is not set in searchUsersByIden");
		}
		if (idenFieldName == null || idenFieldName.isEmpty()) {
			throw new InvalidInputException("idenFieldName is null or empty in searchUsersByIden");
		}

		QueryResponse response = null;
		SolrDocumentList results = null;
		try {
			SolrServer solrServer = new HttpSolrServer(solrUserUrl);
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setQuery(CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE + " OR " + CommonConstants.STATUS_SOLR + ":"
					+ CommonConstants.STATUS_NOT_VERIFIED + " OR " + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_TEMPORARILY_INACTIVE);
			solrQuery.addFilterQuery(idenFieldName + ":" + iden);
			if (isAgent) {
				solrQuery.addFilterQuery(CommonConstants.IS_AGENT_SOLR + ":" + isAgent);
			}
			if (startIndex > -1) {
				solrQuery.setStart(startIndex);
			}
			if (noOfRows > -1) {
				solrQuery.setRows(noOfRows);
			}

			LOG.debug("Querying solr for searching users");
			response = solrServer.query(solrQuery);
			results = response.getResults();
			LOG.debug("User search result is : " + results);
		}
		catch (SolrServerException e) {
			LOG.error("SolrServerException in searchUsersByIden.Reason:" + e.getMessage(), e);
			throw new SolrException("Exception while performing search for user. Reason : " + e.getMessage(), e);
		}
		LOG.info("Method searchUsersByIden finished for iden : " + iden);
		return results;
	}

	/**
	 * Method to perform search of region from solr based on the input Region id
	 * 
	 * @param regionId
	 * @return
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	@Override
	public String searchRegionById(long regionId) throws InvalidInputException, SolrException {
		LOG.info("Method searchRegionById called for regionId :" + regionId);
		if (regionId < 0) {
			throw new InvalidInputException("Region id is null while searching for region");
		}
		String regionName = null;
		QueryResponse response = null;
		try {
			SolrServer solrServer = new HttpSolrServer(solrRegionUrl);
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setQuery(CommonConstants.REGION_ID_SOLR + ":" + regionId);
			solrQuery.addFilterQuery(CommonConstants.STATUS_COLUMN + ":" + CommonConstants.STATUS_ACTIVE);
			response = solrServer.query(solrQuery);
			SolrDocumentList results = response.getResults();
			if (results.size() != 0)
				regionName = (String) results.get(CommonConstants.INITIAL_INDEX).getFieldValue(CommonConstants.REGION_NAME_COLUMN);
		}
		catch (SolrServerException e) {
			LOG.error("UnsupportedEncodingException while performing region search");
			throw new SolrException("Exception while performing search. Reason : " + e.getMessage(), e);
		}
		LOG.debug("Region search result is : " + regionName);
		return regionName;
	}

	/**
	 * Method to perform search of branch name from solr based on the input branch id
	 * 
	 * @param branchId
	 * @return
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	@Override
	public String searchBranchNameById(long branchId) throws InvalidInputException, SolrException {
		LOG.info("Method searchBrancNameById called for branchId :" + branchId);
		if (branchId < 0) {
			throw new InvalidInputException("Branch id is null while searching for Branch");
		}
		String branchName = null;
		QueryResponse response = null;
		try {
			SolrServer solrServer = new HttpSolrServer(solrBranchUrl);
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setQuery(CommonConstants.BRANCH_ID_SOLR + ":" + branchId);
			solrQuery.addFilterQuery(CommonConstants.STATUS_COLUMN + ":" + CommonConstants.STATUS_ACTIVE);
			response = solrServer.query(solrQuery);
			SolrDocumentList results = response.getResults();
			if (results.size() != 0)
				branchName = (String) results.get(CommonConstants.INITIAL_INDEX).getFieldValue(CommonConstants.BRANCH_NAME_COLUMN);
		}
		catch (SolrServerException e) {
			LOG.error("UnsupportedEncodingException while performing branch search");
			throw new SolrException("Exception while performing search. Reason : " + e.getMessage(), e);
		}
		LOG.debug("Branch search result is : " + branchName);
		return branchName;
	}

	/**
	 * Method to perform search of region, branch or Agent name from solr based on the input pattern
	 * for a specific company
	 * 
	 * @param branchId
	 * @return
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	@Override
	public String searchBranchRegionOrAgentByName(String searchColumn, String searchKey, String columnName, long id) throws InvalidInputException,
			SolrException {
		LOG.info("Method searchBranchRegionOrAgentByNameAndCompany() to search regions, branches, agent in a company started");
		String result = null;
		QueryResponse response = null;
		searchKey = searchKey + "*";
		try {
			SolrServer solrServer;
			switch (searchColumn) {
				case CommonConstants.REGION_NAME_SOLR:
					solrServer = new HttpSolrServer(solrRegionUrl);
					break;
				case CommonConstants.BRANCH_NAME_SOLR:
					solrServer = new HttpSolrServer(solrBranchUrl);
					break;
				case CommonConstants.USER_DISPLAY_NAME_SOLR:
					solrServer = new HttpSolrServer(solrUserUrl);
					if (columnName.equals(CommonConstants.REGION_ID_COLUMN)) {
						columnName = "regions";
					}
					else if (columnName.equals(CommonConstants.BRANCH_ID_COLUMN)) {
						columnName = "branches";
					}
					break;
				default:
					solrServer = new HttpSolrServer(solrRegionUrl);
			}
			SolrQuery query = new SolrQuery();
			query.setQuery(columnName + ":" + id);

			query.addFilterQuery(searchColumn + ":" + searchKey);
			query.addFilterQuery(CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE);

			LOG.debug("Querying solr for searching " + searchColumn);
			response = solrServer.query(query);
			SolrDocumentList documentList = response.getResults();
			result = JSONUtil.toJSON(documentList);

			LOG.debug("Results obtained from solr :" + result);
		}
		catch (SolrServerException e) {
			LOG.error("SolrServerException while performing region, branch or agent search");
			throw new SolrException("Exception while performing search. Reason : " + e.getMessage(), e);
		}
		LOG.info("Method searchBranchRegionOrAgentByNameAndCompany() to search regions, branches, agent in a company finished");
		return result;
	}

	@Override
	public String fetchRegionsByCompany(long companyId) throws InvalidInputException, SolrException, MalformedURLException {
		if (companyId < 0) {
			throw new InvalidInputException("Pattern is null or empty while searching for Regions");
		}
		LOG.info("Method fetchRegionsByCompany() called for company id : " + companyId);
		String regionsResult = null;
		QueryResponse response = null;
		try {
			SolrServer solrServer = new HttpSolrServer(solrRegionUrl);
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setQuery(CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE);
			solrQuery.addFilterQuery(CommonConstants.COMPANY_ID_SOLR + ":" + companyId);

			LOG.debug("Querying solr for searching regions");
			response = solrServer.query(solrQuery);
			SolrDocumentList results = response.getResults();
			regionsResult = JSONUtil.toJSON(results);
		}
		catch (SolrServerException e) {
			LOG.error("SolrServerException while performing Regions search");
			throw new SolrException("Exception while performing search for Regions. Reason : " + e.getMessage(), e);
		}

		LOG.info("Method fetchRegionsByCompany() finished for company id : " + companyId);
		return regionsResult;
	}

	@Override
	public String fetchBranchesByCompany(long companyId) throws InvalidInputException, SolrException, MalformedURLException {
		if (companyId < 0) {
			throw new InvalidInputException("Pattern is null or empty while searching for Branches");
		}
		LOG.info("Method fetchBranchesByCompany() called for company id : " + companyId);
		String branchesResult = null;
		QueryResponse response = null;
		try {
			SolrServer solrServer = new HttpSolrServer(solrBranchUrl);
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setQuery(CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE);
			solrQuery.addFilterQuery(CommonConstants.COMPANY_ID_SOLR + ":" + companyId);

			LOG.debug("Querying solr for searching branches");
			response = solrServer.query(solrQuery);
			SolrDocumentList results = response.getResults();
			branchesResult = JSONUtil.toJSON(results);
		}
		catch (SolrServerException e) {
			LOG.error("SolrServerException while performing Branches search");
			throw new SolrException("Exception while performing search for Branches. Reason : " + e.getMessage(), e);
		}
		LOG.info("Method fetchBranchesByCompany() finished for company id : " + companyId);
		return branchesResult;
	}

	/**
	 * Method to get space separated ids from set of ids
	 * 
	 * @param ids
	 * @return
	 */
	private String getSpaceSeparatedStringFromIds(Set<Long> ids) {
		LOG.debug("Method getSpaceSeparatedStringFromIds called for ids:" + ids);
		StringBuilder idsSb = new StringBuilder();
		int count = 0;
		if (ids != null && !ids.isEmpty()) {
			for (Long id : ids) {
				if (count != 0) {
					idsSb.append(" ");
				}
				idsSb.append(id);
				count++;
			}
		}
		LOG.debug("Method getSpaceSeparatedStringFromIds executed successfully. Returning:" + idsSb.toString());
		return idsSb.toString();
	}

	/**
	 * Method to search for the users based on branches specified
	 */
	@Override
	public String searchUsersByBranches(Set<Long> branchIds, int start, int rows) throws InvalidInputException, SolrException {
		if (branchIds == null || branchIds.isEmpty()) {
			throw new InvalidInputException("branchIds are null in searchUsersByBranches");
		}
		LOG.info("Method searchUsersByBranches called for branchIds:" + branchIds + " start:" + start + " rows:" + rows);
		String usersResult = null;
		QueryResponse response = null;
		try {
			SolrServer solrServer = new HttpSolrServer(solrUserUrl);
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setQuery(CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE + " OR " + CommonConstants.STATUS_SOLR + ":"
					+ CommonConstants.STATUS_NOT_VERIFIED + " OR " + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_TEMPORARILY_INACTIVE);
			String branchIdsStr = getSpaceSeparatedStringFromIds(branchIds);
			solrQuery.addFilterQuery(CommonConstants.BRANCHES_SOLR + ":(" + branchIdsStr + ")");

			solrQuery.setStart(start);
			if (rows > 0) {
				solrQuery.setRows(rows);
			}

			LOG.debug("Querying solr for searching users under the branches");
			response = solrServer.query(solrQuery);
			SolrDocumentList results = response.getResults();
			usersResult = JSONUtil.toJSON(results);
			LOG.debug("Users search result is : " + usersResult);
		}
		catch (SolrServerException e) {
			throw new SolrException("Exception while performing search for users by branches. Reason : " + e.getMessage(), e);
		}
		LOG.info("Method searchUsersByBranches executed successfully");
		return usersResult;
	}
}
