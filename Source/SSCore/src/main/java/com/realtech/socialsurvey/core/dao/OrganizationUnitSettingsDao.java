package com.realtech.socialsurvey.core.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.realtech.socialsurvey.core.entities.AgentRankingReport;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.FeedIngestionEntity;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.ProfileImageUrlData;
import com.realtech.socialsurvey.core.entities.ProfileUrlEntity;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;


/**
 * Gets the organization settings
 */
public interface OrganizationUnitSettingsDao
{

    /**
     * Inserts an organization settings
     * 
     * @param organizationUnitSettings
     * @param collectionName
     */
    public void insertOrganizationUnitSettings( OrganizationUnitSettings organizationUnitSettings, String collectionName );


    /**
     * Inserts agent settings
     * 
     * @param agentSettings
     */
    public void insertAgentSettings( AgentSettings agentSettings );


    /**
     * Fetches the organization unit settings for the given identifier and collection name
     * 
     * @param identifier
     * @param collectionName
     * @return
     */
    public OrganizationUnitSettings fetchOrganizationUnitSettingsById( long identifier, String collectionName );


    /**
     * Returns the agent settings
     * 
     * @param identitifier
     * @return
     */
    public AgentSettings fetchAgentSettingsById( long identifier );


    /**
     * Gets multiple user settings
     * 
     * @param identifiers
     * @return List<AgentSettings>
     * @throws InvalidInputException
     */
    public List<AgentSettings> fetchMultipleAgentSettingsById( List<Long> identifiers ) throws InvalidInputException;


    /**
     * Updates a particular element in the collection
     * 
     * @param keyToUpdate
     * @param updatedRecord
     * @param unitSettings
     * @param collectionName
     */
    public void updateParticularKeyOrganizationUnitSettings( String keyToUpdate, Object updatedRecord,
        OrganizationUnitSettings unitSettings, String collectionName );


    /**
     * Updates a particular key organization unit by id
     * @param keyToUpdate
     * @param updatedRecord
     * @param iden
     * @param collectionName
     */
    public void updateParticularKeyOrganizationUnitSettingsByIden( String keyToUpdate, Object updatedRecord, long iden,
        String collectionName );


    /**
     * Updates a particular element in the collection
     * 
     * @param keyToUpdate
     * @param updatedRecord
     * @param agentSettings
     */
    public void updateParticularKeyAgentSettings( String keyToUpdate, Object updatedRecord, AgentSettings agentSettings );


    /**
     * Fetchs the list of names of logos being used.
     * 
     * @return
     */
    public List<String> fetchLogoList();


    /**
     * Updates a particular key of organization unit settings based on criteria specified
     * 
     * @param keyToUpdate
     * @param updatedRecord
     * @param criteriaKey
     * @param criteriaValue
     * @param collectionName
     */
    public void updateKeyOrganizationUnitSettingsByCriteria( String keyToUpdate, Object updatedRecord, String criteriaKey,
        Object criteriaValue, String collectionName );


    /**
     * Method to fetch organization unit settings on the basis of profile name
     * 
     * @param profileName
     * @param collectionName
     * @return
     */
    public OrganizationUnitSettings fetchOrganizationUnitSettingsByProfileName( String profileName, String collectionName );


    /**
     *  Method to fetch organization unit settings on the basis of profile url
     * @param profileUrl
     * @param collectionName
     * @return
     */
    public OrganizationUnitSettings fetchOrganizationUnitSettingsByProfileUrl( String profileUrl, String collectionName );


    /**
     * Gets a list of SiteMapEntry object for SEO
     * @param collectionName
     * @return
     */
    public List<ProfileUrlEntity> fetchSEOOptimizedOrganizationUnitSettings( String collectionName, int skipCount,
        int numOfRecords, List<Long> excludedEntityIds );


    /**
     * Gets the count of records for SEO Optimization
     * @param collectionName
     * @return
     */
    public long fetchSEOOptimizedOrganizationUnitCount( String collectionName, List<Long> excludedEntityIds );


    public void updateCompletedSurveyCountForAgent( long agentId, int incrementCount );


    /**
     * Fetch list of social media tokens for particular collection
     * @param collectionName
     * @param skipCount
     * @param numOfRecords
     * @return
     */
    public List<FeedIngestionEntity> fetchSocialMediaTokens( String collectionName, int skipCount, int numOfRecords );


    public void removeOganizationUnitSettings( List<Long> agentIds, String agentSettingsCollection );


    public List<OrganizationUnitSettings> fetchOrganizationUnitSettingsForMultipleIds( Set<Long> identifiers,
        String collectionName );


    public Map<Long, OrganizationUnitSettings> getSettingsMapWithLinkedinImageUrl( String collectionName, String matchUrl );


    public void setAgentDetails( Map<Long, AgentRankingReport> agentsReport );


    public OrganizationUnitSettings removeKeyInOrganizationSettings( OrganizationUnitSettings unitSettings, String keyToUpdate,
        String collectionName );


    public List<OrganizationUnitSettings> getCompanyListByVerticalName( String verticalName );


    public List<OrganizationUnitSettings> getCompanyList();


    public List<OrganizationUnitSettings> getCompanyListByKey( String searchKey );


    public OrganizationUnitSettings fetchOrganizationUnitSettingsByUniqueIdentifier( String uniqueIdentifier,
        String collectionName );


    public List<OrganizationUnitSettings> getCompanyListByIds( Set<Long> companyIds );


    /**
     * Gets a list of organisation unit settings with give crm info details from a given collection
     * @param source
     * @param collectionName
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    public List<OrganizationUnitSettings> getOrganizationUnitListWithCRMSource( String source, String collectionName )
        throws InvalidInputException, NoRecordsFetchedException;


    void updateKeyOrganizationUnitSettingsByInCriteria( String keyToUpdate, Object updatedRecord, String criteriaKey,
        List<Object> criteriaValue, String collectionName );


    List<ProfileImageUrlData> fetchProfileImageUrlsForEntityList( String entityType, HashSet<Long> entityList )
        throws InvalidInputException;


    /**
     * Gets a collection of unprocessed images
     * @param collectionName
     * @param imageType
     * @return
     * @throws InvalidInputException
     */
    public Map<Long, String> getCollectionListOfUnprocessedImages( String collectionName, String imageType )
        throws InvalidInputException;


    void updateImageForOrganizationUnitSetting( long iden, String imgFileName, String imgThumbnailFileName, String rectangularThumbnailFileName, String collectionName, String imageType,
        boolean flagValue, boolean isThumbnail ) throws InvalidInputException;


    public List<OrganizationUnitSettings> getCompanyListForEncompass( String state, String encompassVersion )
        throws InvalidInputException, NoRecordsFetchedException;


    public void updateAgentSettingsForUserRestoration( String newProfileName, AgentSettings agentSettings,
        boolean restoreSocial, boolean isVerified ) throws InvalidInputException;


    public List<OrganizationUnitSettings> fetchUnitSettingsConnectedToZillow( String collectionName, List<Long> identifiers );


    public ContactDetailsSettings fetchAgentContactDetailByEncryptedId( String userEncryptedId );


    public List<AgentSettings> getAllAgentSettings();


    /**
     * Gets social media tokens from the collection for given id
     * @param collectionName
     * @param iden
     * @return
     */
    public SocialMediaTokens fetchSocialMediaTokens( String collectionName, long iden );


    public List<OrganizationUnitSettings> fetchUnitSettingsForSocialMediaTokens( String collectionName );


    public List<Long> fetchEntityIdsWithHiddenAttribute( String collection );
}
