package com.realtech.socialsurvey.core.services.searchengine;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.LOSearchEngine;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SurveyStats;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.ZipCodeLookup;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.vo.AddressGeoLocationVO;
import com.realtech.socialsurvey.core.vo.AdvancedSearchVO;
import com.realtech.socialsurvey.core.vo.LOSearchRankingVO;

public interface SearchEngineManagementServices {

	  /**
     * @param companyId
     */
    public void updateHierarchyAddressForCompany( long entityId );


	
	/**
	 * @param userId
	 * @param regionId
	 * @param branchId
	 */
	public void updateAddressForAgentWhileAddingIndividual(long userId, long regionId, long branchId);


	/**
	 * @param userId
	 */
	public void updateAddressForAgentWhilePrimaryChange(long userId);


	/**
	 * @param userId
	 */
	public void updateAddressForAgentId(long userId);


	/**
	 * @param entityType
	 * @param entityId
	 * @param contactDetails
	 * @throws InvalidInputException 
	 */
	public void updateAddressForAgents(String entityType, long entityId, ContactDetailsSettings contactDetails) throws InvalidInputException;


	/**
	 * @param contactDetails
	 * @return
	 */
	public AddressGeoLocationVO fetchLatLng(AddressGeoLocationVO addGeoLoc);


	/**
	 * 
	 */
	public void updatelocForUsersWithLatLngNotUpdated();


	/**
	 * @param longitude
	 * @param latitude
	 * @param maxDistanceInMiles
	 * @return
	 * @throws InvalidInputException 
	 */
	List<OrganizationUnitSettings> nearestToLoc(double longitude, double latitude, double maxDistanceInMiles,String entityType) throws InvalidInputException;


	/**
	 * @return
	 */
	LOSearchEngine getLoSearchSettings();

	/**
	 * 
	 * @param entityType
	 * @return
	 * @throws InvalidInputException
	 */
	String getCollectionForEntity(String entityType) throws InvalidInputException;
	
	/**
	 * 
	 * @param entityType
	 * @param entityId
	 * @return
	 * @throws InvalidInputException
	 */
	public SurveyStats getSurveyStatsByEntityId(String entityType, long entityId) throws InvalidInputException;

	/**
	 * 
	 * @param entityType
	 * @param entityId
	 * @param surveyStats
	 * @throws InvalidInputException
	 */
	void updateSurveyStatsByEntityId(String entityType, long entityId, SurveyStats surveyStats)
			throws InvalidInputException;



	/**
	 * @param advancedSearchVO
	 * @return
	 */
	List<OrganizationUnitSettings> getSearchResults(AdvancedSearchVO advancedSearchVO);


	/**
	 * @param profileFilter
	 * @return
	 */
	String getCollectionFromProfile(String profileFilter);


	/**
	 * @param searchString
	 * @param startIndex
	 * @param batchSize
	 * @param onlyUsFilter
	 * @return
	 */
	List<ZipCodeLookup> getSuggestionForNearMe(String searchString, int startIndex, int batchSize, boolean onlyUsFilter);


	long getSearchResultsCount(AdvancedSearchVO advancedSearchVO);

	boolean updateSurveyStatsForAllEntities() throws InvalidInputException;


	/**
	 * @param contactDetailsSettings
	 * @return
	 * @throws InvalidInputException
	 */
	GeoJsonPoint getGeoLocForSettings(ContactDetailsSettings contactDetailsSettings) throws InvalidInputException;



	/**
	 * @param zipcode
	 * @param startIndex
	 * @param batchSize
	 * @param onlyUsFilter
	 * @return
	 */
	List<ZipCodeLookup> getSuggestionForZipcode(String zipcode, int startIndex, int batchSize, boolean onlyUsFilter);

	public boolean updateCompanyIdForAllEntities() throws InvalidInputException;

	/**
	 * 
	 */
	void retryFailedSurveyProcessor();
}
