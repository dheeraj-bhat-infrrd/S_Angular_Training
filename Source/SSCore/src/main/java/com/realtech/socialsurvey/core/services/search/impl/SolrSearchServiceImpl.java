package com.realtech.socialsurvey.core.services.search.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import org.apache.noggit.JSONUtil;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
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
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.utils.SolrSearchUtils;

// JIRA:SS-62 BY RM 02
/**
 * Implementation class for solr search services
 */
@SuppressWarnings("deprecation")
@Component
public class SolrSearchServiceImpl implements SolrSearchService {

	private static final Logger LOG = LoggerFactory.getLogger(SolrSearchServiceImpl.class);

	@Value("${SOLR_REGION_URL}")
	private String solrRegionUrl;

	@Value("${SOLR_BRANCH_URL}")
	private String solrBranchUrl;

	@Value("${SOLR_USER_URL}")
	private String solrUserUrl;

	@Autowired
	private SolrSearchUtils solrSearchUtils;

	/**
	 * Method to perform search of regions from solr based on the input pattern and user
	 * 
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	@Override
	public String searchRegions(String regionPattern, Company company) throws InvalidInputException, SolrException {
		LOG.info("Method searchRegions called for regionPattern :" + regionPattern);
		if (regionPattern == null || regionPattern.isEmpty()) {
			throw new InvalidInputException("Region pattern is null or empty while searching for region");
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
					+ CommonConstants.STATUS_ACTIVE);

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
	 * Method to perform search of branches from solr based on the input pattern and company
	 * 
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	@Override
	public String searchBranches(String branchPattern, Company company) throws InvalidInputException, SolrException {
		LOG.info("Method searchBranches called for branchPattern :" + branchPattern);
		if (branchPattern == null || branchPattern.isEmpty()) {
			throw new InvalidInputException("Branch pattern is null or empty while searching for branch");
		}
		if (company == null) {
			throw new InvalidInputException("Company is null or empty while searching for branch");
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
					+ CommonConstants.STATUS_ACTIVE);

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
	 * Method to add region into solr
	 */
	@Override
	public void addOrUpdateRegionToSolr(Region region) throws SolrException {
		LOG.info("Method to add or update region to solr called for region : " + region);
		SolrServer solrServer;
		try {

			// TODO change the solr instance and do not use deprecated class
			solrServer = new CommonsHttpSolrServer(solrRegionUrl);
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
			solrServer = new CommonsHttpSolrServer(solrBranchUrl);
			// TODO remove deprecated class
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

			SolrServer solrServer = new CommonsHttpSolrServer(solrUserUrl);
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setQuery("loginName:" + loginNamePattern);
			solrQuery.addFilterQuery("companyId:" + company.getCompanyId(), "status:" + CommonConstants.STATUS_ACTIVE);

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
	 */
	@Override
	public String searchUsersByLoginNameOrName(String pattern, long companyId) throws InvalidInputException, SolrException, MalformedURLException {
		LOG.info("Method searchUsersByLoginNameOrName called for pattern :" + pattern);
		if (pattern == null || pattern.isEmpty()) {
			throw new InvalidInputException("Pattern is null or empty while searching for Users");
		}
		LOG.info("Method searchUsersByLoginNameOrName() called for parameter : " + pattern);
		String usersResult = null;
		QueryResponse response = null;
		try {
			pattern = pattern + "*";

			SolrServer solrServer = new CommonsHttpSolrServer(solrUserUrl);
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setQuery("loginName:" + pattern + " OR firstName:" + pattern + " OR lastName:" + pattern);
			solrQuery.addFilterQuery("companyId:" + companyId, "status:" + CommonConstants.STATUS_ACTIVE);
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

	/**
	 * Method to add User into solr
	 */
	@Override
	public void addUserToSolr(User user) throws SolrException {
		LOG.info("Method to add user to solr called for user : " + user.getFirstName());
		SolrServer solrServer;
		UpdateResponse response = null;
		try {
			// TODO change the solr instance and do not use deprecated class
			solrServer = new CommonsHttpSolrServer(solrUserUrl);
			SolrInputDocument document = new SolrInputDocument();
			document.addField(CommonConstants.USER_ID_SOLR, user.getUserId());

			document.addField(CommonConstants.USER_FIRST_NAME_SOLR, user.getFirstName());
			document.addField(CommonConstants.USER_LAST_NAME_SOLR, user.getLastName());
			document.addField(CommonConstants.USER_EMAIL_ID_SOLR, user.getEmailId());
			document.addField(CommonConstants.USER_IS_OWNER_SOLR, user.getIsOwner());
			document.addField(CommonConstants.COMPANY_ID_SOLR, user.getCompany().getCompanyId());
			document.addField(CommonConstants.STATUS_SOLR, user.getStatus());

			if (user.getUserProfiles() != null)
				for (UserProfile userProfile : user.getUserProfiles()) {
					document.addField(CommonConstants.BRANCH_ID_SOLR, userProfile.getBranchId());
					document.addField(CommonConstants.REGION_ID_SOLR, userProfile.getRegionId());
					response = solrServer.add(document);
				}
			LOG.debug("response while adding region is {}." + response);
			solrServer.commit();
		}
		catch (MalformedURLException e) {
			LOG.error("Exception while adding regions to solr. Reason : " + e.getMessage(), e);
			throw new SolrException("Exception while adding regions to solr. Reason : " + e.getMessage(), e);
		}
		catch (SolrServerException | IOException e) {
			LOG.error("Exception while adding regions to solr. Reason : " + e.getMessage(), e);
			throw new SolrException("Exception while adding regions to solr. Reason : " + e.getMessage(), e);
		}
		LOG.info("Method to add region to solr finshed for region : " + user);
	}
	
	/*public static void main(String[] args) throws MalformedURLException {
		SolrSearchServiceImpl solrSearchServiceImpl = new SolrSearchServiceImpl();
		solrSearchServiceImpl.solrUserUrl = "http://localhost:8983/solr/ss-users/";
		Company company = new Company();
		company.setCompanyId(2);
		User user = new User();
		try {
			user.setUserId(11);
			System.out.println(solrSearchServiceImpl.searchUsersByLoginNameOrName("ritwik"));
		}
		catch (InvalidInputException | SolrException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}
