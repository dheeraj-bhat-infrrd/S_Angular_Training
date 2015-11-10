/**
 * JIRA:SS-62 BY RM 02 BOC
 */
package com.realtech.socialsurvey.core.services.search;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.SocialPost;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserFromSearch;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;


/**
 * Holds method to perform search from solr
 */
public interface SolrSearchService
{

    /**
     * Method to perform search of regions from solr based on input pattern , company and regionIds
     * if provided
     * 
     * @param regionPattern
     * @param company
     * @param start
     * @param rows
     * @param regionIds
     * @return
     * @throws InvalidInputException
     * @throws SolrException
     */
    public String searchRegions( String regionPattern, Company company, Set<Long> regionIds, int start, int rows )
        throws InvalidInputException, SolrException;


    /**
     * Method to perform search of branches from solr based on the input pattern, company and
     * ids(ids could be either regionIds or branchIds and idColumnName is set accordingly )
     * 
     * @param branchPattern
     * @param company
     * @param idColumnName
     * @param ids
     * @param start
     * @param rows
     * @return
     * @throws InvalidInputException
     * @throws SolrException
     */
    public String searchBranches( String branchPattern, Company company, String idColumnName, Set<Long> ids, int start, int rows )
        throws InvalidInputException, SolrException;


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
    public String searchBranchesByRegion( long regionId, int start, int rows ) throws InvalidInputException, SolrException;


    /**
     * Method to add a region to solr
     * 
     * @param region
     * @throws SolrException
     */
    public void addOrUpdateRegionToSolr( Region region ) throws SolrException;


    /**
     * Method to add a branch to solr
     * 
     * @param branch
     * @throws SolrException
     */
    public void addOrUpdateBranchToSolr( Branch branch ) throws SolrException;


    public String searchUsersByLoginNameAndCompany( String userNamePattern, Company company ) throws InvalidInputException,
        SolrException, MalformedURLException;


    public void addUserToSolr( User user ) throws SolrException;


    public SolrDocumentList searchUsersByLoginNameOrName( String pattern, long companyId, int startIndex, int batchSize ) throws InvalidInputException, SolrException,
        MalformedURLException;


    /**
     * Method to perform search of Users from solr based on the input pattern for firstname and last
     * name
     * 
     * @throws InvalidInputException
     * @throws SolrException
     * @throws MalformedURLException
     * @throws UnsupportedEncodingException
     */
    public SolrDocumentList searchUsersByFirstOrLastName( String patternFirst, String patternLast, int startIndex, int noOfRows )
        throws InvalidInputException, SolrException, MalformedURLException;


    public SolrDocumentList searchUsersByCompany( long companyId, int startIndex, int noOfRows ) throws InvalidInputException,
        SolrException, MalformedURLException;


    public long countUsersByCompany( long companyId, int startIndex, int noOfRows ) throws InvalidInputException,
        SolrException, MalformedURLException;


    public String fetchRegionsByCompany( long companyId, int size ) throws InvalidInputException, SolrException,
        MalformedURLException;


    public String fetchBranchesByCompany( long companyId, int size ) throws InvalidInputException, SolrException,
        MalformedURLException;


    
    public void removeUserFromSolr( long userIdToRemove ) throws SolrException;


    /**
     * Method to fetch display name of a user from solr based upon user id provided
     * 
     * @param userId
     * @return
     * @throws NoRecordsFetchedException
     * @throws InvalidInputException
     * @throws SolrException
     */
    public String getUserDisplayNameById( long userId ) throws NoRecordsFetchedException, InvalidInputException, SolrException;


    /**
     * Method to fetch user based on the userid provided
     * 
     * @param userId
     * @return
     * @throws InvalidInputException
     * @throws SolrException
     */
    public SolrDocument getUserByUniqueId( long userId ) throws InvalidInputException, SolrException;


    public void editUserInSolr( long userId, String key, String value ) throws SolrException;


    public void editUserInSolrWithMultipleValues( long userId, Map<String, Object> map ) throws SolrException;


    /**
     * Method to search for the users based on the iden specified, iden could be branchId/regionId
     * or companyId
     * 
     * @param iden
     * @param idenFieldName
     * @param isAgent
     * @param startIndex
     * @param noOfRows
     * @return
     * @throws InvalidInputException
     * @throws SolrException
     */
    public Collection<UserFromSearch> searchUsersByIden( long iden, String idenFieldName, boolean isAgent, int startIndex,
        int noOfRows ) throws InvalidInputException, SolrException;


    /**
     * @param regionId
     * @return
     * @throws InvalidInputException
     * @throws SolrException
     */
    public String searchRegionById( long regionId ) throws InvalidInputException, SolrException;


    /**
     * @param branchId
     * @return
     * @throws InvalidInputException
     * @throws SolrException
     */
    public String searchBranchNameById( long branchId ) throws InvalidInputException, SolrException;


    /**
     * @param searchColumn
     * @param searchKey
     * @param columnName
     * @param id
     * @return
     * @throws InvalidInputException
     * @throws SolrException
     */
    public List<SolrDocument> searchBranchRegionOrAgentByName( String searchColumn, String searchKey, String columnName, long id )
        throws InvalidInputException, SolrException;


    /**
     * Method to search for the users based on branches specified
     * 
     * @param branchIds
     * @param start
     * @param rows
     * @return
     * @throws InvalidInputException
     * @throws SolrException
     */
    public String searchUsersByBranches( Set<Long> branchIds, int start, int rows ) throws InvalidInputException, SolrException;


    /**
     * Method to add multiple regions to solr
     * 
     * @param regions
     * @throws SolrException
     */
    public void addRegionsToSolr( List<Region> regions ) throws SolrException;


    /**
     * Method to add multiple branches to solr
     * 
     * @param branches
     * @throws SolrException
     */
    public void addBranchesToSolr( List<Branch> branches ) throws SolrException;


    /**
     * Method to add multiple users to solr
     * 
     * @param users
     * @throws SolrException
     */
    public void addUsersToSolr( List<User> users ) throws SolrException;


    public void updateCompletedSurveyCountForUserInSolr( long agentId, int incrementCount) throws SolrException, NoRecordsFetchedException;


    public Map<String, String> getCompanyAdmin( long companyId ) throws SolrException;


    public List<Long> searchUserIdsByCompany( long companyId ) throws InvalidInputException, SolrException;


    public void removeUsersFromSolr( List<Long> agentIds ) throws SolrException;


    public List<Long> searchBranchIdsByCompany( long companyId ) throws SolrException, InvalidInputException;


    public void removeBranchesFromSolr( List<Long> branchIds ) throws SolrException;


    public List<Long> searchRegionIdsByCompany( long companyId ) throws InvalidInputException, SolrException;


    public void removeRegionsFromSolr( List<Long> regionIds ) throws SolrException;


    public long getRegionsCount( String regionPattern, Company company, Set<Long> regionIds ) throws InvalidInputException,
        SolrException;


    public long getBranchCountByRegion( long regionId ) throws InvalidInputException, SolrException;


    public long getUsersCountByIden( long iden, String idenFieldName, boolean isAgent ) throws InvalidInputException,
        SolrException;


    public long getUsersCountByBranches( Set<Long> branchIds ) throws InvalidInputException, SolrException;


    public SolrDocumentList getUserIdsByIden( long iden, String idenFieldName, boolean isAgent, int startIndex, int noOfRows )
        throws InvalidInputException, SolrException;


    public Collection<UserFromSearch> getUsersFromSolrDocuments( SolrDocumentList documentList ) throws InvalidInputException;


    public Long fetchBranchCountByCompany( long companyId ) throws InvalidInputException, SolrException, MalformedURLException;


    public Long fetchRegionCountByCompany( long companyId ) throws InvalidInputException, SolrException, MalformedURLException;


    void addSocialPostsToSolr( List<SocialPost> socialPosts ) throws SolrException;


    Collection<SocialPost> getSocialPostsFromSolrDocuments( SolrDocumentList documentList );


    SolrDocumentList fetchSocialPostsByEntity( String entityType, long entityId, int startIndex, int noOfRows )
        throws InvalidInputException, SolrException, MalformedURLException;

    SolrDocumentList searchPostText( String entityType, long entityId, int startIndex, int noOfRows, String searchQuery )
        throws InvalidInputException, SolrException, MalformedURLException;


    Date getLastBuildTimeForSocialPosts() throws SolrException;
    
    
    public void updateCompletedSurveyCountForMultipleUserInSolr( Map<Long, Integer> usersReviewCount ) throws SolrException;


    public SolrDocumentList searchUsersByLoginNameOrNameUnderAdmin( String pattern, User admin, UserFromSearch adminFromSearch,
        int startIndex, int batchSize ) throws InvalidInputException, SolrException, MalformedURLException;


    public Set<Long> getUserIdsFromSolrDocumentList( SolrDocumentList userIdList );


    List<UserFromSearch> getUsersWithMetaDataFromSolrDocuments( SolrDocumentList documentList );
}
// JIRA:SS-62 BY RM 02 EOC