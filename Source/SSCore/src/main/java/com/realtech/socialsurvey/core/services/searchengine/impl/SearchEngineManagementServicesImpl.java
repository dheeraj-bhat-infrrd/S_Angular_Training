package com.realtech.socialsurvey.core.services.searchengine.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Component;

import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.MongoApplicationSettingsDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.RegionDao;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.dao.SurveyPreInitiationDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.entities.ApplicationSettings;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.LOSearchEngine;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.SurveyStats;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.ZipCodeLookup;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.factories.ApplicationSettingsInstanceProvider;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.searchengine.SearchEngineManagementServices;
import com.realtech.socialsurvey.core.utils.google.GeoCodingApiUtils;
import com.realtech.socialsurvey.core.vo.AddressGeoLocationVO;
import com.realtech.socialsurvey.core.vo.AdvancedSearchVO;

@Component
public class SearchEngineManagementServicesImpl implements SearchEngineManagementServices {

	private static final Logger LOG = LoggerFactory.getLogger(SearchEngineManagementServicesImpl.class);

	@Autowired
	private UserProfileDao userProfileDao;

	@Autowired
	private OrganizationUnitSettingsDao organizationUnitSettingsDao;
	
	@Autowired
	private SurveyPreInitiationDao surveyPreInitiationDao; 

	@Resource
	@Qualifier("branch")
	private BranchDao branchDao;

	@Autowired
	private RegionDao regionDao;

	@Autowired
	private UserDao userDao;
	
	@Autowired
    private GeoCodingApiUtils geoUtils;
    
	@Autowired
	ApplicationSettingsInstanceProvider provider;
	
    @Value("${GOOGLE_GEO_API_KEY}")
	private String googleGeoApi;
    
    @Autowired
    private MongoApplicationSettingsDao applicationDaoSetting;
    
    @Autowired
    private SurveyDetailsDao surveyDetailsDao;
    
    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private UserManagementService userManagementService;
	
    
    
    /*
	 * Update contactDetails.address if doesn't exist in the hierarchy excluding the
	 * default branches and regions also updating the setStatus in mysql
	 */
	@Override
	public void updateHierarchyAddressForCompany(long companyId) {
		LOG.debug("Funciton to populate the lower hierarchies with address for companyId:{}", companyId);
		// create a function to fetch all non default regions
		Map<String, Map<Long, List<Long>>> hierarchy = userProfileDao.getUserListForhierarchy(companyId);
		hierarchyAddressIterator(hierarchy);
	}

	// iterate through hierarchy
	public void hierarchyAddressIterator(Map<String, Map<Long, List<Long>>> hierarchy) {
		LOG.debug("the method hierarchyAddressIterator has started");
		if (hierarchy.containsKey(CommonConstants.BRANCH_NAME_COLUMN)
				&& !hierarchy.get(CommonConstants.BRANCH_NAME_COLUMN).isEmpty()) {
			updateAddresses(hierarchy.get(CommonConstants.BRANCH_NAME_COLUMN), CommonConstants.BRANCH_ID_COLUMN,
					CommonConstants.BRANCH_SETTINGS_COLLECTION, CommonConstants.AGENT_SETTINGS_COLLECTION);
		}
		if (hierarchy.containsKey(CommonConstants.REGION_NAME_COLUMN)
				&& !hierarchy.get(CommonConstants.REGION_NAME_COLUMN).isEmpty()) {
			updateAddresses(hierarchy.get(CommonConstants.REGION_NAME_COLUMN), CommonConstants.REGION_ID_COLUMN,
					CommonConstants.REGION_SETTINGS_COLLECTION, CommonConstants.AGENT_SETTINGS_COLLECTION);
		}
		if (hierarchy.containsKey(CommonConstants.COMPANY_NAME)
				&& !hierarchy.get(CommonConstants.COMPANY_NAME).isEmpty()) {
			updateAddresses(hierarchy.get(CommonConstants.COMPANY_NAME), CommonConstants.COMPANY_ID_COLUMN,
					CommonConstants.COMPANY_SETTINGS_COLLECTION, CommonConstants.AGENT_SETTINGS_COLLECTION);
		}
		LOG.debug("the method hierarchyAddressIterator has finished");
	}

	// get higher hierarchies address
	// update multiple with the lower hierarchy
	public void updateAddresses(Map<Long, List<Long>> agentMap, String entityType, String getAddFromCollection,
			String updateAddForCollection) {
		LOG.debug("the method updateAddresses was called");
		for (Long entityId : agentMap.keySet()) {
			try {
			// fetch address, create AddressGeoLocationVO
			AddressGeoLocationVO addGeoLoc = organizationUnitSettingsDao.fetchAddressForId(entityId, entityType,
					getAddFromCollection);
			// hit google geoCoding api and update location of higher hierarchy , update
			// AddressGeoLocationVO
			fetchLatLng(addGeoLoc);
			organizationUnitSettingsDao.updateLocation(addGeoLoc.getLatitude(), addGeoLoc.getLongitude(), entityId,
					getAddFromCollection);
			organizationUnitSettingsDao.updateAddressForLowerHierarchy(updateAddForCollection, addGeoLoc,
					agentMap.get(entityId));
			}catch ( Exception exception ) {
	            LOG.error( "Exception caught in updateAddresses() for EntityID {} of the type {} ", entityId, entityType);
	        }
			
		}
	}

	@Override
	public void updateAddressForAgentWhileAddingIndividual(long userId, long regionId, long branchId) {
		LOG.debug("the method updateAddressForAgentWhileAddingIndividual has started");
		User user = userDao.findById(User.class, userId);
		// create get user profile
		List<UserProfile> userProfiles = user.getUserProfiles();
		if (userProfiles == null || userProfiles.isEmpty()) {
			userProfiles = userProfileDao.getUserProfiles(user.getUserId());
		}
		for (UserProfile userProfile : userProfiles) {
			if (userProfile.getIsPrimary() == CommonConstants.IS_PRIMARY_TRUE && userProfile.getProfilesMaster()
					.getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID) {
				// create contact details settings
				AddressGeoLocationVO addGeoLoc = new AddressGeoLocationVO();
				if (branchId > 0l) {
					addGeoLoc = organizationUnitSettingsDao.fetchAddressForId(userProfile.getBranchId(),
							CommonConstants.BRANCH_ID_COLUMN, CommonConstants.BRANCH_SETTINGS_COLLECTION);
				} else if (regionId > 0l) {
					addGeoLoc = organizationUnitSettingsDao.fetchAddressForId(userProfile.getRegionId(),
							CommonConstants.REGION_ID_COLUMN, CommonConstants.REGION_SETTINGS_COLLECTION);
				} else {
					addGeoLoc = organizationUnitSettingsDao.fetchAddressForId(userProfile.getCompany().getCompanyId(),
							CommonConstants.COMPANY_ID_COLUMN, CommonConstants.COMPANY_SETTINGS_COLLECTION);
				}
				List<Long> userList = new ArrayList<>();
				userList.add(user.getUserId());
				organizationUnitSettingsDao.updateAddressForLowerHierarchy(CommonConstants.AGENT_SETTINGS_COLLECTION,
						addGeoLoc, userList);
			}
		}
		LOG.debug("the method updateAddressForAgentWhileAddingIndividual has finished");
	}

	@Override
	public void updateAddressForAgentWhilePrimaryChange(long userId) {
		LOG.debug("the method updateAddressForAgentWhilePrimaryChange has started");
		User user = userDao.findById(User.class, userId);
		// create get user profile
		List<UserProfile> userProfiles = user.getUserProfiles();
		if (userProfiles == null || userProfiles.isEmpty()) {
			userProfiles = userProfileDao.getUserProfiles(user.getUserId());
		}
		for (UserProfile userProfile : userProfiles) {
			if (userProfile.getIsPrimary() == CommonConstants.IS_PRIMARY_TRUE
					&& userProfile.getProfilesMaster()
							.getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID
					&& userProfile.getStatus() == CommonConstants.STATUS_ACTIVE) {
				// create contact details settings
				AddressGeoLocationVO addGeoVO = new AddressGeoLocationVO();
				try {
					branchDao.checkIfBranchIsDefault(userProfile.getBranchId());
					addGeoVO = organizationUnitSettingsDao.fetchAddressForId(userProfile.getBranchId(),
							CommonConstants.BRANCH_ID_COLUMN, CommonConstants.BRANCH_SETTINGS_COLLECTION);
				} catch (Exception defaultBranchEx) {
					try {
						regionDao.checkIfRegionIsDefault(userProfile.getRegionId());
						addGeoVO = organizationUnitSettingsDao.fetchAddressForId(userProfile.getRegionId(),
								CommonConstants.REGION_ID_COLUMN, CommonConstants.REGION_SETTINGS_COLLECTION);
					} catch (Exception defaultRegionEx) {
						addGeoVO = organizationUnitSettingsDao.fetchAddressForId(
								userProfile.getCompany().getCompanyId(), CommonConstants.COMPANY_ID_COLUMN,
								CommonConstants.COMPANY_SETTINGS_COLLECTION);
					}
				}
				organizationUnitSettingsDao.updateAgentAddress(CommonConstants.AGENT_SETTINGS_COLLECTION, addGeoVO,
						user.getUserId());
			}
		}
		LOG.debug("the method updateAddressForAgentWhilePrimaryChange has finished");
	}

	@Override
	public void updateAddressForAgentId(long userId) {
		LOG.debug("the method updateAddressForAgentId for userId:{} is called", userId);
		updateAddressForAgentWhilePrimaryChange(userId);
	}

	// update address of agents linked to the entity as primary
	// and enter the contact details
	@Override
	public void updateAddressForAgents(String entityType, long entityId, ContactDetailsSettings contactDetails) throws InvalidInputException {
		LOG.debug("the method updateAddressForAgents for entityType:{} , entityId:{} has started", entityType,
				entityId);
		// Create address geoloc
		AddressGeoLocationVO addGeoLoc = organizationUnitSettingsDao.createAddressGeoLocationVo(contactDetails, null);
		// update location for higher hierarchy
		fetchLatLng(addGeoLoc);
		organizationUnitSettingsDao.updateLocation(addGeoLoc.getLatitude(), addGeoLoc.getLongitude(), entityId,
				getCollectionForEntity(entityType));
		if (!entityType.equals(CommonConstants.AGENT_SETTINGS_COLLECTION)) {
			List<Long> userList = userProfileDao.findPrimaryUserProfile(entityType, entityId);
			if (userList != null && !userList.isEmpty()) {
				organizationUnitSettingsDao.updateAddressForLowerHierarchy(CommonConstants.AGENT_SETTINGS_COLLECTION,
						addGeoLoc, userList);
			}
		}
	}

	@Override
	public AddressGeoLocationVO fetchLatLng(AddressGeoLocationVO addGeoLoc) {
		LatLng location = new LatLng();
		try {
			location = geoUtils.getGoogleApiResultsLocation(googleGeoApi, createGoogleAddressFormat(addGeoLoc));
			if(location != null) {
				addGeoLoc.setLatitude(location.lat);
				addGeoLoc.setLongitude(location.lng);
			}else {
				addGeoLoc.setLatitude(0);
				addGeoLoc.setLongitude(0);
			}
		} catch (ApiException | InterruptedException | IOException exception) {
			LOG.error("Exception caught while hitting google geocoding api" + exception.getMessage());
		}
		return addGeoLoc;

	}

	public String createGoogleAddressFormat(AddressGeoLocationVO addGeoLoc) {
		String appendAddress = "";
		if (addGeoLoc.getAddress() != null)
			appendAddress = addGeoLoc.getAddress().concat(",");
		if (addGeoLoc.getAddress1() != null)
			appendAddress = appendAddress.concat(addGeoLoc.getAddress1()).concat(",");
		if (addGeoLoc.getAddress2() != null)
			appendAddress = appendAddress.concat(addGeoLoc.getAddress2()).concat(",");
		if (addGeoLoc.getCity() != null)
			appendAddress = appendAddress.concat(addGeoLoc.getCity()).concat(",");
		if (addGeoLoc.getCountry() != null)
			appendAddress = appendAddress.concat(addGeoLoc.getCountry());
		return appendAddress;
	}

	//one time method to update user's (company admin , region admin ,branch admin and users) who have no latlng updates
	@Override
	public void updatelocForUsersWithLatLngNotUpdated() {
		LOG.debug("fetch agents with their own address and update lat and long");
		//fetch companies with no agent's associated with and update their lat and long
		List<OrganizationUnitSettings> compContacts = organizationUnitSettingsDao
				.fetchUsersWithOwnAddress(CommonConstants.COMPANY_SETTINGS_COLLECTION);
		updateLatLongForUserList(compContacts, CommonConstants.COMPANY_SETTINGS_COLLECTION);

		// fetch companies with no agent's associated with and update their lat and long
		List<OrganizationUnitSettings> regionContacts = organizationUnitSettingsDao
				.fetchUsersWithOwnAddress(CommonConstants.REGION_SETTINGS_COLLECTION);
		updateLatLongForUserList(regionContacts, CommonConstants.REGION_SETTINGS_COLLECTION);
		
		// fetch companies with no agent's associated with and update their lat and long
		List<OrganizationUnitSettings> branchContacts = organizationUnitSettingsDao
				.fetchUsersWithOwnAddress(CommonConstants.BRANCH_SETTINGS_COLLECTION);
		updateLatLongForUserList(branchContacts, CommonConstants.BRANCH_SETTINGS_COLLECTION);
		
		// fetch companies with no agent's associated with and update their lat and long
		List<OrganizationUnitSettings> userContacts = organizationUnitSettingsDao
				.fetchUsersWithOwnAddress(CommonConstants.AGENT_SETTINGS_COLLECTION);
		updateLatLongForUserList(userContacts, CommonConstants.AGENT_SETTINGS_COLLECTION);
		
	}
	
	public void updateLatLongForUserList(List<OrganizationUnitSettings> userContacts, String collectionName) {
		// for each user fetch lat long and update
		for(OrganizationUnitSettings userContact : userContacts) {
			AddressGeoLocationVO addGeo = organizationUnitSettingsDao.createAddressGeoLocationVo(userContact.getContact_details(), null);
			fetchLatLng(addGeo);
			organizationUnitSettingsDao.updateLocation(addGeo.getLatitude(), addGeo.getLongitude(), userContact.getIden(), collectionName);
		}
	}
	
	@Override
	public List<OrganizationUnitSettings> nearestToLoc(double longitude,double latitude,double maxDistanceInMiles,String entityType) throws InvalidInputException{
		LOG.debug("fetch agents from given lat:{},lng:{} for a distance of:{} miles",latitude,longitude,maxDistanceInMiles);
		return organizationUnitSettingsDao.nearestToLoc(longitude, latitude, maxDistanceInMiles,getCollectionForEntity(entityType));
	}
	
	// create a function to call application settings for lo search
	@Override
	public LOSearchEngine getLoSearchSettings() 
	{	
		ApplicationSettings applicationSettings = provider.getApplicationSettings();
		LOSearchEngine loSearchEngine =  applicationSettings.getLoSearchEngine();
		return loSearchEngine;
	}
	
	@Override
	public String getCollectionForEntity(String entityType) throws InvalidInputException {
		LOG.debug("function to get collection for given entityType:{}",entityType);
		switch ( entityType ) {
        case CommonConstants.COMPANY_ID_COLUMN:
            return CommonConstants.COMPANY_SETTINGS_COLLECTION;
        case CommonConstants.REGION_ID_COLUMN:
            return CommonConstants.REGION_SETTINGS_COLLECTION;
        case CommonConstants.BRANCH_ID_COLUMN:
           return CommonConstants.BRANCH_SETTINGS_COLLECTION;
        case CommonConstants.AGENT_ID_COLUMN:
            return CommonConstants.AGENT_SETTINGS_COLLECTION;
        default:
           throw new InvalidInputException("Passing wrong entityType");
		}
	}
	
	
	
	/**
	 * 
	 */
	@Override
	public SurveyStats getSurveyStatsByEntityId(String entityType , long entityId) throws InvalidInputException
	{
		SurveyStats surveyStats = organizationUnitSettingsDao.getSurveyStats(entityId, getCollectionForEntity(entityType));
		
		if(entityType.equalsIgnoreCase(CommonConstants.COMPANY_ID_COLUMN) && surveyStats != null) {
			return surveyStats;
		}
		
		if(surveyStats == null) {
			surveyStats = new SurveyStats();
		}
		//get latest incomplete survey count from mysql survey pre initiation
		int[] status = {CommonConstants.SURVEY_STATUS_PRE_INITIATED , CommonConstants.SURVEY_STATUS_INITIATED};
		long incompleteSurveyCount = surveyPreInitiationDao.getIncompleteSurveyCount(0, entityId, status, null, null, null);
		surveyStats.setIncompleteSurveyCount(incompleteSurveyCount);
		
		//get gateway response count from mongo
		long gatewayResponseCountGreat = 0;
		long gatewayResponseCountUnpleasant = 0;
		Map<String,Long> gatewayResponseCount = surveyDetailsDao.getSurveyCountForGatewayResponses(entityType, entityId);
		if (gatewayResponseCount != null && gatewayResponseCount.get(CommonConstants.SURVEY_MOOD_GREAT) != null)
			gatewayResponseCountGreat = gatewayResponseCount.get(CommonConstants.SURVEY_MOOD_GREAT);
		if (gatewayResponseCount != null && gatewayResponseCount.get(CommonConstants.SURVEY_MOOD_UNPLEASANT) != null)
			gatewayResponseCountUnpleasant = gatewayResponseCount.get(CommonConstants.SURVEY_MOOD_UNPLEASANT);
		
		surveyStats.setGatewayResponseGreat(gatewayResponseCountGreat);
		surveyStats.setGatewayResponseUnpleasant(gatewayResponseCountUnpleasant);

		
		//calculate and update total completed survey count
		surveyStats.setSurveyCount(surveyDetailsDao.getCompletedSurveyCount(entityType, entityId, null, null, true, true));
		
		//recent survey counts
		Calendar cal = Calendar.getInstance();
		ApplicationSettings applicationSettings = provider.getApplicationSettings();
		cal.add(Calendar.DATE, - applicationSettings.getLoSearchEngine().getRecentSurveyDays());
		surveyStats.setRecentSurveyCount(surveyDetailsDao.getCompletedSurveyCount(entityType, entityId, new Timestamp(cal.getTimeInMillis()), null, true, true));	
		
		//get avg score for entity
		surveyStats.setAvgScore(surveyDetailsDao.getAvgScoreForEntity(entityType, entityId));
		
		return surveyStats;
	}
	
	/**
	 * 
	 */
	@Override
	public void updateSurveyStatsByEntityId(String entityType , long entityId, SurveyStats surveyStats) throws InvalidInputException
	{
		 organizationUnitSettingsDao.updateSurveyStats(entityId, getCollectionForEntity(entityType), surveyStats);

	}
	
	//sevice to fetch data based on search filter
	@Override
	public List<OrganizationUnitSettings> getSearchResults(AdvancedSearchVO advancedSearchVO){
		long companyId = 0l;
		//get company id if we need to search only with in company
		if( !StringUtils.isEmpty(advancedSearchVO.getCompanyProfileName()) ) {
			companyId = organizationManagementService.getCompanyByProfileName(advancedSearchVO.getCompanyProfileName());
		}		
		// check if name search give two pattern's for first name and last name
 		if (advancedSearchVO.getFindBasedOn() != null && !advancedSearchVO.getFindBasedOn().isEmpty()) {
 			long startIndex = advancedSearchVO.getStartIndex();
 			long batchSize = advancedSearchVO.getBatchSize();
 			// search with pattern ^
 			long firstNameCount = organizationUnitSettingsDao.getSearchResultsForCriteriaCount(advancedSearchVO,
 					getCollectionFromProfile(advancedSearchVO.getProfileFilter()), null, companyId, "^");
 			long fullDataCount = startIndex + batchSize;
 			if(firstNameCount > startIndex ) {
 				if(firstNameCount >= fullDataCount)
 					return organizationUnitSettingsDao.getSearchResultsForCriteria(advancedSearchVO, getCollectionFromProfile(advancedSearchVO.getProfileFilter()), null, companyId, "^");
 				else  {
 					//initialise to get complete list
 					List<OrganizationUnitSettings> organisationUnitSettings= new ArrayList<>();
 					//if first name doesn't have full batch size break it
 					long fetchFromFirstCount = firstNameCount - advancedSearchVO.getStartIndex();
 					advancedSearchVO.setBatchSize(fetchFromFirstCount);
 					organisationUnitSettings.addAll(organizationUnitSettingsDao.getSearchResultsForCriteria(advancedSearchVO, getCollectionFromProfile(advancedSearchVO.getProfileFilter()), null, companyId, "^"));
 					//find left out count
 					long fetchLastNameCount = batchSize - fetchFromFirstCount;
 					advancedSearchVO.setBatchSize(fetchLastNameCount);
 					advancedSearchVO.setStartIndex(0);
 					organisationUnitSettings.addAll(organizationUnitSettingsDao.getSearchResultsForCriteria(advancedSearchVO, getCollectionFromProfile(advancedSearchVO.getProfileFilter()), null, companyId, " "));	
 					return organisationUnitSettings;
 				}
 			}else {
 				//find the exact start index for last name 
 				long startIndexForLast = startIndex - firstNameCount;
 				advancedSearchVO.setStartIndex(startIndexForLast);
 				return organizationUnitSettingsDao.getSearchResultsForCriteria(advancedSearchVO, getCollectionFromProfile(advancedSearchVO.getProfileFilter()), null, companyId, " ");
 			}
 
  		}
 		return organizationUnitSettingsDao.getSearchResultsForCriteria(advancedSearchVO, getCollectionFromProfile(advancedSearchVO.getProfileFilter()), null, companyId, null);		
 	}	 		
	
	
	@Override
	public String getCollectionFromProfile(String profileFilter) {
		switch (profileFilter) {
		case CommonConstants.SEARCH_ENGINE_PROFILE_COMPANIES:
			return CommonConstants.COMPANY_SETTINGS_COLLECTION;
		case CommonConstants.SEARCH_ENGINE_PROFILE_LOAN_OFFICERS:
			return CommonConstants.BRANCH_SETTINGS_COLLECTION;
		case CommonConstants.SEARCH_ENGINE_PROFILE_PROFESSIONAL:
			return CommonConstants.AGENT_SETTINGS_COLLECTION;
		default:
			return CommonConstants.AGENT_SETTINGS_COLLECTION;
		}
	}
	
	@Override
	public List<ZipCodeLookup> getSuggestionForNearMe(String searchString, int startIndex, int batchSize, boolean onlyUsFilter) {
		try {
			Integer.parseInt(searchString);
			return userDao.getZipcodeSuggestion(searchString, startIndex, batchSize, onlyUsFilter);
		}catch(NumberFormatException exception) {
			if (searchString.contains(",")) {
				String[] commaSeparatedArr = searchString.split(",");
				if (commaSeparatedArr.length > 1) {
					searchString = commaSeparatedArr[0].trim() + "," + commaSeparatedArr[1].trim();
				} else {
					searchString = commaSeparatedArr[0].trim();
				}
			}
			return userDao.getCityAndCountySuggestion(searchString,startIndex,batchSize,onlyUsFilter);
		}
		
	}

	@Override
	public long getSearchResultsCount(AdvancedSearchVO advancedSearchVO) {
		long companyId = 0l;
		//get company id if we need to search only with in company
		if( !StringUtils.isEmpty(advancedSearchVO.getCompanyProfileName()) ) {
		    companyId = organizationManagementService.getCompanyByProfileName(advancedSearchVO.getCompanyProfileName());
		}
		//check if name search give two pattern's for first name and last name 
 		if(advancedSearchVO.getFindBasedOn() != null && !advancedSearchVO.getFindBasedOn().isEmpty()) {
 			//search with pattern ^
 			long firstNameCount = organizationUnitSettingsDao.getSearchResultsForCriteriaCount(advancedSearchVO, getCollectionFromProfile(advancedSearchVO.getProfileFilter()), null, companyId, "^");
 			long lastNameCount = organizationUnitSettingsDao.getSearchResultsForCriteriaCount(advancedSearchVO, getCollectionFromProfile(advancedSearchVO.getProfileFilter()), null, companyId, " ");
 			return firstNameCount + lastNameCount;
 		}
 		return organizationUnitSettingsDao.getSearchResultsForCriteriaCount(advancedSearchVO, getCollectionFromProfile(advancedSearchVO.getProfileFilter()), null, companyId, null);
		
	}
	
	@Override
	public boolean updateSurveyStatsForAllEntities() throws InvalidInputException
	{
		ApplicationSettings applicationSettings = provider.getApplicationSettings();
		LOSearchEngine loSearchEngine =  applicationSettings.getLoSearchEngine();
		
		LOG.info("LOSearchEngine details are ");
		LOG.info("DefaultOffset : " +  loSearchEngine.getDefaultOffset());
		LOG.info("CompletionRatio : " +  loSearchEngine.getCompletionRatio());
		LOG.info("SpsOffset : " +  loSearchEngine.getSpsOffset());
		LOG.info("SpsRatio : " +  loSearchEngine.getSpsRatio());
		LOG.info("RecentSurveyDays : " +  loSearchEngine.getRecentSurveyDays());
		
		int[] status = {CommonConstants.SURVEY_STATUS_PRE_INITIATED , CommonConstants.SURVEY_STATUS_INITIATED};
		
		this.updateSurveyStatsForAllAgents(loSearchEngine, status);
		this.updateSurveyStatsForAllBranches(loSearchEngine, status);
		this.updateSurveyStatsForAllCompanies(loSearchEngine, status);
		 
		 return true;
	}
	
	
	void updateSurveyStatsForAllBranches(LOSearchEngine loSearchEngine , int[] status) throws InvalidInputException
	{
		for ( Company company : organizationManagementService.getAllCompanies() ) {
			 int batch = 500;
			 int start = 0;
			 List<Branch> branches = null;
			 
			 while(branches == null || branches.size() == 500 ) {
				  branches = branchDao.getBranchesForCompany(company.getCompanyId(), 0, start, batch);
				  for(Branch branch : branches) {
					  try {
						LOG.info("Running method updateSurveyStatsForAllEntities for branch {}" , branch.getBranchId());	 
						 SurveyStats surveyStats = organizationUnitSettingsDao.getSurveyStats(branch.getBranchId(), getCollectionForEntity("branchId"));
						 	if(surveyStats == null)
						 		surveyStats = new SurveyStats();
							//get latest incomplete survey count from mysql survey pre initiation
						 	long incompleteSurveyCount = surveyPreInitiationDao.getIncompleteSurveyCount( 0l, 0l, status, null, null, userProfileDao.findUserIdsByBranch( branch.getBranchId() ) );
							surveyStats.setIncompleteSurveyCount(incompleteSurveyCount);
							
							//get gateway response count from mongo
							Map<String,Long> gatewayResponseCount = surveyDetailsDao.getSurveyCountForGatewayResponses("branchId", branch.getBranchId());
							if(gatewayResponseCount == null || gatewayResponseCount.isEmpty())
								gatewayResponseCount = new HashMap<>();
							long gatewayResponseCountGreat = 0l;
							if(gatewayResponseCount.get(CommonConstants.SURVEY_MOOD_GREAT) != null)
								gatewayResponseCountGreat = gatewayResponseCount.get(CommonConstants.SURVEY_MOOD_GREAT).longValue();
							long gatewayResponseCountUnpleasent = 0l;
							if(gatewayResponseCount.get(CommonConstants.SURVEY_MOOD_UNPLEASANT) != null)
								gatewayResponseCountUnpleasent = gatewayResponseCount.get(CommonConstants.SURVEY_MOOD_UNPLEASANT).longValue();
							surveyStats.setGatewayResponseGreat(gatewayResponseCountGreat);
							surveyStats.setGatewayResponseUnpleasant(gatewayResponseCountUnpleasent);					
							//calculate and update total completed survey count
							surveyStats.setSurveyCount(surveyDetailsDao.getCompletedSurveyCount("branchId", branch.getBranchId(), null, null, true, true));	
							//recent survey count 
							Calendar cal = Calendar.getInstance();
							cal.add(Calendar.DATE, - loSearchEngine.getRecentSurveyDays());
							surveyStats.setRecentSurveyCount(surveyDetailsDao.getCompletedSurveyCount("branchId", branch.getBranchId(), new Timestamp(cal.getTimeInMillis()), null, true, true));	
							//get avg score for entity
							surveyStats.setAvgScore(surveyDetailsDao.getAvgScoreForEntity("branchId", branch.getBranchId()));
							
							SurveyDetails latestSurveyDetails = surveyDetailsDao.getLatestCompletedSurveyForEntity("branchId", branch.getBranchId());
							if(latestSurveyDetails != null)
								surveyStats.setLatestReview(latestSurveyDetails.getReview());
							
							//calculate sps
							double spsScore = 0;
							if(surveyStats.getSurveyCount() > 0)
								spsScore = ( surveyStats.getGatewayResponseGreat() - surveyStats.getGatewayResponseUnpleasant() ) * 100 / surveyStats.getSurveyCount();
							surveyStats.setSpsScore(spsScore);
							
							double completionRate = 0;
							if(surveyStats.getSurveyCount() > 0)		
								completionRate = surveyStats.getSurveyCount() / (  surveyStats.getSurveyCount() +  surveyStats.getIncompleteSurveyCount()) * 100;
							
							double searchRankingScore  = 0l;
							surveyStats.setSearchRankingScore(searchRankingScore);
							if((surveyStats.getSurveyCount() > 0)) {
								LOG.info("Survey count is " + surveyStats.getSurveyCount());
								LOG.info("Survey avg score is " + surveyStats.getAvgScore());
								LOG.info("Survey def offset is " + loSearchEngine.getDefaultOffset());
								LOG.info("Survey completion ratio is " + loSearchEngine.getCompletionRatio());
								double searchRankingSurveyCountPart = ( (surveyStats.getSurveyCount() * surveyStats.getAvgScore() ) + loSearchEngine.getDefaultOffset() ) / (surveyStats.getSurveyCount() + 1) ;
								double searchRankingCompletionPart =  (completionRate * loSearchEngine.getCompletionRatio()) ;
								double searchRankingSPSPart = (spsScore - loSearchEngine.getSpsOffset() ) * loSearchEngine.getSpsRatio();
								searchRankingScore =  searchRankingSurveyCountPart + searchRankingCompletionPart  + searchRankingSPSPart;
								
								LOG.info("--searchRankingSurveyCountPart-- " + searchRankingSurveyCountPart );
								LOG.info("--searchRankingCompletionPart-- " + searchRankingCompletionPart );
								LOG.info("--searchRankingSPSPart-- " + searchRankingSPSPart );
								LOG.info("--searchRankingScore-- " + searchRankingScore );
								
								surveyStats.setSearchRankingScore(searchRankingScore);					
							}
							surveyStats.setSearchRankingScore(searchRankingScore);
							organizationUnitSettingsDao.updateSurveyStats(branch.getBranchId(), getCollectionForEntity("branchId"), surveyStats);
					 
						 
			 }catch(Exception e) {
				 LOG.error("ERROR" , e);
				 LOG.error("ERROR in updateSurveyStatsForAllEntities method for branch id {}" , branch.getBranchId() , e );
			 }
			 }
			 }
		 }
	}
	
	void updateSurveyStatsForAllAgents(LOSearchEngine loSearchEngine , int[] status) throws InvalidInputException 
	{
		for ( Company company : organizationManagementService.getAllCompanies() ) {			 
		 int batch = 500;
		 int start = 0;
		 List<User> users = null;
		 while(users == null || users.size() == 500 ) {
			 users = userDao.getUsersForCompany(company, start, batch);
			 for(User user : users) {
				 try {
				LOG.info("Running method updateSurveyStatsForAllEntities for agent {}" , user.getUserId());	 
				 SurveyStats surveyStats = organizationUnitSettingsDao.getSurveyStats(user.getUserId(), getCollectionForEntity("agentId"));
				 	if(surveyStats == null)
				 		surveyStats = new SurveyStats();
					//get latest incomplete survey count from mysql survey pre initiation
					long incompleteSurveyCount = surveyPreInitiationDao.getIncompleteSurveyCount(0, user.getUserId(), status, null, null, null);
					surveyStats.setIncompleteSurveyCount(incompleteSurveyCount);
					
					//get gateway response count from mongo
					Map<String,Long> gatewayResponseCount = surveyDetailsDao.getSurveyCountForGatewayResponses("agentId", user.getUserId());
					if(gatewayResponseCount == null || gatewayResponseCount.isEmpty())
						gatewayResponseCount = new HashMap<>();
					long gatewayResponseCountGreat = 0l;
					if(gatewayResponseCount.get(CommonConstants.SURVEY_MOOD_GREAT) != null)
						gatewayResponseCountGreat = gatewayResponseCount.get(CommonConstants.SURVEY_MOOD_GREAT).longValue();
					long gatewayResponseCountUnpleasent = 0l;
					if(gatewayResponseCount.get(CommonConstants.SURVEY_MOOD_UNPLEASANT) != null)
						gatewayResponseCountUnpleasent = gatewayResponseCount.get(CommonConstants.SURVEY_MOOD_UNPLEASANT).longValue();
					surveyStats.setGatewayResponseGreat(gatewayResponseCountGreat);
					surveyStats.setGatewayResponseUnpleasant(gatewayResponseCountUnpleasent);					
					//calculate and update total completed survey count
					surveyStats.setSurveyCount(surveyDetailsDao.getCompletedSurveyCount("agentId", user.getUserId(), null, null, true, true));	
					//recent survey count 
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.DATE, - loSearchEngine.getRecentSurveyDays());
					surveyStats.setRecentSurveyCount(surveyDetailsDao.getCompletedSurveyCount("agentId", user.getUserId(), new Timestamp(cal.getTimeInMillis()), null, true, true));	
					//get avg score for entity
					surveyStats.setAvgScore(surveyDetailsDao.getAvgScoreForEntity("agentId", user.getUserId()));
					
					SurveyDetails latestSurveyDetails = surveyDetailsDao.getLatestCompletedSurveyForEntity("agentId", user.getUserId());
					if(latestSurveyDetails != null)
						surveyStats.setLatestReview(latestSurveyDetails.getReview());
					
					//calculate sps
					double spsScore = 0;
					if(surveyStats.getSurveyCount() > 0)
						spsScore = ( surveyStats.getGatewayResponseGreat() - surveyStats.getGatewayResponseUnpleasant() ) * 100 / surveyStats.getSurveyCount();
					surveyStats.setSpsScore(spsScore);
					
					double completionRate = 0;
					if(surveyStats.getSurveyCount() > 0)		
						completionRate = surveyStats.getSurveyCount() / (  surveyStats.getSurveyCount() +  surveyStats.getIncompleteSurveyCount()) * 100;
					
					double searchRankingScore  = 0l;
					surveyStats.setSearchRankingScore(searchRankingScore);
					if((surveyStats.getSurveyCount() > 0)) {
						LOG.info("Survey count is " + surveyStats.getSurveyCount());
						LOG.info("Survey avg score is " + surveyStats.getAvgScore());
						LOG.info("Survey def offset is " + loSearchEngine.getDefaultOffset());
						LOG.info("Survey completion ratio is " + loSearchEngine.getCompletionRatio());
						double searchRankingSurveyCountPart = ( (surveyStats.getSurveyCount() * surveyStats.getAvgScore() ) + loSearchEngine.getDefaultOffset() ) / (surveyStats.getSurveyCount() + 1) ;
						double searchRankingCompletionPart =  (completionRate * loSearchEngine.getCompletionRatio()) ;
						double searchRankingSPSPart = (spsScore - loSearchEngine.getSpsOffset() ) * loSearchEngine.getSpsRatio();
						searchRankingScore =  searchRankingSurveyCountPart + searchRankingCompletionPart  + searchRankingSPSPart;
						
						LOG.info("--searchRankingSurveyCountPart-- " + searchRankingSurveyCountPart );
						LOG.info("--searchRankingCompletionPart-- " + searchRankingCompletionPart );
						LOG.info("--searchRankingSPSPart-- " + searchRankingSPSPart );
						LOG.info("--searchRankingScore-- " + searchRankingScore );
						
						surveyStats.setSearchRankingScore(searchRankingScore);					
					}
					surveyStats.setSearchRankingScore(searchRankingScore);
					organizationUnitSettingsDao.updateSurveyStats(user.getUserId(), getCollectionForEntity("agentId"), surveyStats);
			 
				 }catch(Exception e) {
					 e.printStackTrace();
					 LOG.error("ERROR" , e);
					 LOG.error("ERROR in updateSurveyStatsForAllEntities method for agent id {}" , user.getUserId() , e );
				 }
			 }	 
			 
		 }
	 }
	}

	void updateSurveyStatsForAllCompanies(LOSearchEngine loSearchEngine , int[] status) {
		for ( Company company : organizationManagementService.getAllCompanies() ) {
			 try {
			 LOG.info("Running method updateSurveyStatsForAllEntities for company {}" , company.getCompanyId());
			 SurveyStats surveyStats = organizationUnitSettingsDao.getSurveyStats(company.getCompanyId(), getCollectionForEntity("companyId"));
			 if(surveyStats == null)
			 		surveyStats = new SurveyStats();
				//get latest incomplete survey count from mysql survey pre initiation
				long incompleteSurveyCount = surveyPreInitiationDao.getIncompleteSurveyCount( company.getCompanyId(), 0, status, null, null, null);
				surveyStats.setIncompleteSurveyCount(incompleteSurveyCount);
				
				//get gateway response count from mongo
				Map<String,Long> gatewayResponseCount = surveyDetailsDao.getSurveyCountForGatewayResponses("companyId", company.getCompanyId());
				if(gatewayResponseCount == null || gatewayResponseCount.isEmpty())
					gatewayResponseCount = new HashMap<>();
				long gatewayResponseCountGreat = 0l;
				if(gatewayResponseCount.get(CommonConstants.SURVEY_MOOD_GREAT) != null)
					gatewayResponseCountGreat = gatewayResponseCount.get(CommonConstants.SURVEY_MOOD_GREAT).longValue();
				long gatewayResponseCountUnpleasent = 0l;
				if(gatewayResponseCount.get(CommonConstants.SURVEY_MOOD_UNPLEASANT) != null)
					gatewayResponseCountUnpleasent = gatewayResponseCount.get(CommonConstants.SURVEY_MOOD_UNPLEASANT).longValue();
				surveyStats.setGatewayResponseGreat(gatewayResponseCountGreat);
				surveyStats.setGatewayResponseUnpleasant(gatewayResponseCountUnpleasent);					
				//calculate and update total completed survey count
				surveyStats.setSurveyCount(surveyDetailsDao.getCompletedSurveyCount("companyId", company.getCompanyId(), null, null, true, true));	
				//recent survey count 
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, - loSearchEngine.getRecentSurveyDays());
				surveyStats.setRecentSurveyCount(surveyDetailsDao.getCompletedSurveyCount("companyId", company.getCompanyId(), new Timestamp(cal.getTimeInMillis()), null, true, true));	
				//get avg score for entity
				surveyStats.setAvgScore(surveyDetailsDao.getAvgScoreForEntity("companyId", company.getCompanyId()));
				
				SurveyDetails latestSurveyDetails = surveyDetailsDao.getLatestCompletedSurveyForEntity("companyId", company.getCompanyId());
				if(latestSurveyDetails != null)
					surveyStats.setLatestReview(latestSurveyDetails.getReview());
				
				//calculate sps
				double spsScore = 0;
				if(surveyStats.getSurveyCount() > 0)
					spsScore = ( surveyStats.getGatewayResponseGreat() - surveyStats.getGatewayResponseUnpleasant() ) * 100 / surveyStats.getSurveyCount();
				surveyStats.setSpsScore(spsScore);
				
				double completionRate = 0;
				if(surveyStats.getSurveyCount() > 0)		
					completionRate = surveyStats.getSurveyCount() / (  surveyStats.getSurveyCount() +  surveyStats.getIncompleteSurveyCount()) * 100;
				
				double searchRankingScore  = 0l;
				surveyStats.setSearchRankingScore(searchRankingScore);
				if((surveyStats.getSurveyCount() > 0)) {
					LOG.info("Survey count is " + surveyStats.getSurveyCount());
					LOG.info("Survey avg score is " + surveyStats.getAvgScore());
					LOG.info("Survey def offset is " + loSearchEngine.getDefaultOffset());
					LOG.info("Survey completion ratio is " + loSearchEngine.getCompletionRatio());
					double searchRankingSurveyCountPart = ( (surveyStats.getSurveyCount() * surveyStats.getAvgScore() ) + loSearchEngine.getDefaultOffset() ) / (surveyStats.getSurveyCount() + 1) ;
					double searchRankingCompletionPart =  (completionRate * loSearchEngine.getCompletionRatio()) ;
					double searchRankingSPSPart = (spsScore - loSearchEngine.getSpsOffset() ) * loSearchEngine.getSpsRatio();
					searchRankingScore =  searchRankingSurveyCountPart + searchRankingCompletionPart  + searchRankingSPSPart;
					
					LOG.info("--searchRankingSurveyCountPart-- " + searchRankingSurveyCountPart );
					LOG.info("--searchRankingCompletionPart-- " + searchRankingCompletionPart );
					LOG.info("--searchRankingSPSPart-- " + searchRankingSPSPart );
					LOG.info("--searchRankingScore-- " + searchRankingScore );
					
					surveyStats.setSearchRankingScore(searchRankingScore);					
				}
				
				organizationUnitSettingsDao.updateSurveyStats(company.getCompanyId(), getCollectionForEntity("companyId"), surveyStats);
			 }catch(Exception e) {
				 LOG.error("ERROR" , e);
				 LOG.error("ERROR in updateSurveyStatsForAllEntities method for company id {}" , company.getCompanyId() , e );
			 }
		}
	}

	// update address of agents linked to the entity as primary
	// and enter the contact details
	@Override
	public GeoJsonPoint getGeoLocForSettings(ContactDetailsSettings contactDetailsSettings)
			throws InvalidInputException {
		LOG.debug("the method updateAddressForEntityType for entityType:{} , entityId:{} has started");
		// Create address geoloc
		AddressGeoLocationVO addGeoLoc = organizationUnitSettingsDao.createAddressGeoLocationVo(contactDetailsSettings,
				null);
		// update location for higher hierarchy
		fetchLatLng(addGeoLoc);
		// add location in organisation unit setting's and return
		return organizationUnitSettingsDao.createGeoJsonPoint(addGeoLoc.getLatitude(), addGeoLoc.getLongitude());
	}
	
	@Override
	public List<ZipCodeLookup> getSuggestionForZipcode(String zipcode, int startIndex, int batchSize, boolean onlyUsFilter) {
		return userDao.getZipcodeSuggestion(zipcode,startIndex,batchSize,onlyUsFilter);
	}

	@Override
	public boolean updateCompanyIdForAllEntities() throws InvalidInputException
	{
		LOG.info("Method updateCompanyIdForAllEntities started");
		for ( Company company : organizationManagementService.getAllCompanies() ) {
			LOG.info(" updating CompanyId For company {}" , company.getCompanyId());
			organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettingsByIden("companyId", company.getCompanyId(), company.getCompanyId(), getCollectionForEntity("companyId"));
		}
		
		
		for ( Company company : organizationManagementService.getAllCompanies() ) {			 
			 int batch = 500;
			 int start = 0;
			 List<User> users = null;
			 while(users == null || users.size() == 500 ) {
				 users = userDao.getUsersForCompany(company, start, batch);
				 for(User user : users) {
					 	LOG.info(" updating CompanyId For user {}" , user.getUserId());
						organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettingsByIden("companyId", company.getCompanyId(), user.getUserId(), getCollectionForEntity("agentId"));
						}
				 }
		} 
		
		for ( Company company : organizationManagementService.getAllCompanies() ) {
			 int batch = 500;
			 int start = 0;
			 List<Branch> branches = null;
			 
			 //non default branches
			 while(branches == null || branches.size() == 500 ) {
				  branches = branchDao.getBranchesForCompany(company.getCompanyId(), 0, start, batch);
				  for(Branch branch : branches) {
					  	LOG.info(" updating CompanyId For branch {}" , branch.getBranchId());
						organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettingsByIden("companyId", company.getCompanyId(), branch.getBranchId(), getCollectionForEntity("branchId"));
						}
				  }
			 
			 //for default branches
			 start = 0;
			 branches = null;
			 while(branches == null || branches.size() == 500 ) {
				  branches = branchDao.getBranchesForCompany(company.getCompanyId(), 1, start, batch);
				  for(Branch branch : branches) {
					  	LOG.info(" updating CompanyId For branch {}" , branch.getBranchId());
						organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettingsByIden("companyId", company.getCompanyId(), branch.getBranchId(), getCollectionForEntity("branchId"));
						}
				  }
		}	
		
		for ( Company company : organizationManagementService.getAllCompanies() ) {
			 int batch = 500;
			 int start = 0;
			 List<Region> regions = null;
			 
			 while(regions == null || regions.size() == 500 ) {
				 regions = regionDao.getRegionsForCompany(company.getCompanyId(), start, batch);
				  for(Region region : regions) {
					  	LOG.info(" updating CompanyId For branch {}" , region.getRegionId());
						organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettingsByIden("companyId", company.getCompanyId(), region.getRegionId(), getCollectionForEntity("regionId"));
						}
				  }
		}
		
		return true;
	}
	
}
