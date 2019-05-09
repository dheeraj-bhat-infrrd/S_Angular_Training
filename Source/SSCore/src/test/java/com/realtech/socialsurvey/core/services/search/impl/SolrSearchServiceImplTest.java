package com.realtech.socialsurvey.core.services.search.impl;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.SocialPost;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserFromSearch;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;


public class SolrSearchServiceImplTest
{
    @InjectMocks
    private SolrSearchServiceImpl solrSearchServiceImpl;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {}


    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks( this );
    }


    @After
    public void tearDown() throws Exception
    {}


    @Test ( expected = InvalidInputException.class)
    public void testSearchRegionsForNullRegionPattern() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.searchRegions( null, new Company(), new HashSet<Long>(), 0, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchRegionsForNullCompany() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.searchRegions( "test", null, new HashSet<Long>(), 0, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetRegionsCountForNullRegionPattern() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.getRegionsCount( null, new Company(), new HashSet<Long>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetRegionsCountForNullCompany() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.getRegionsCount( "test", null, new HashSet<Long>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchBranchesForNullBranchPattern() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.searchBranches( null, new Company(), null, null, 0, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchBranchesForNullCompany() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.searchBranches( "test", null, null, null, 0, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchBranchesByRegionForInvaliRegionId() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.searchBranchesByRegion( 0l, 0, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBranchCountByRegionForInvaliRegionId() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.getBranchCountByRegion( 0l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddOrUpdateRegionToSolrForNullRegion() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.addOrUpdateRegionToSolr( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddOrUpdateBranchToSolrForNullBranch() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.addOrUpdateBranchToSolr( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchUsersByLoginNameAndCompanyForNullLoginNamePattern() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.searchUsersByLoginNameAndCompany( null, new Company() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchUsersByLoginNameAndCompanyForEmptyLoginNamePattern() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.searchUsersByLoginNameAndCompany( "", new Company() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchUsersByLoginNameAndCompanyForNullCompany() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.searchUsersByLoginNameAndCompany( "test", null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchUsersByLoginNameOrNameForNullPattern() throws InvalidInputException, SolrException,
        MalformedURLException
    {
        solrSearchServiceImpl.searchUsersByLoginNameOrName( null, 1, 0, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchUsersByLoginNameOrNameForInvalidCompanyId() throws InvalidInputException, SolrException,
        MalformedURLException
    {
        solrSearchServiceImpl.searchUsersByLoginNameOrName( "test", 0l, 0, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchUsersByLoginNameOrNameForNullFirstAndLastName() throws InvalidInputException, SolrException,
        MalformedURLException
    {
        solrSearchServiceImpl.searchUsersByFirstOrLastName( null, null, 0, 5, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchUsersByCompanyForInvalidCompanyId() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.searchUsersByCompany( 0l, 0, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testCountUsersByCompanyForInvalidCompanyId() throws InvalidInputException, SolrException, MalformedURLException
    {
        solrSearchServiceImpl.countUsersByCompany( 0l, 0, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddUserToSolrForNullUser() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.addUserToSolr( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testRemoveUserFromSolrForInvalidUserId() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.removeUserFromSolr( 0l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUserDisplayNameByIdForInvalidUserId() throws InvalidInputException, SolrException,
        NoRecordsFetchedException
    {
        solrSearchServiceImpl.getUserDisplayNameById( 0l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUserByUniqueIdForInvalidUserId() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.getUserByUniqueId( 0l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testEditUserInSolrForInvalidUserId() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.editUserInSolr( 0l, "test", "test1" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testEditUserInSolrWithMultipleValuesForInvalidUserId() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.editUserInSolrWithMultipleValues( 0l, new HashMap<String, Object>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testEditUserInSolrWithMultipleValuesForNullInput() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.editUserInSolrWithMultipleValues( 5, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUserIdsByIdenForInvalidIden() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.getUserIdsByIden( 0l, "test", true, 0, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUserIdsByIdenForNullIdenName() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.getUserIdsByIden( 2, null, true, 0, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUserIdsByIdenForEmptyIdenName() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.getUserIdsByIden( 2, "", true, 0, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchUsersByIdenForInvalidIden() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.searchUsersByIden( 0l, "test", true, 0, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchUsersByIdenForNullIdenName() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.searchUsersByIden( 2, null, true, 0, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchUsersByIdenForEmptyIdenName() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.searchUsersByIden( 2, "", true, 0, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUserCountByIdenForInvalidIden() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.getUsersCountByIden( 0l, "test", true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUserCountByIdenForNullIdenName() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.getUsersCountByIden( 2, null, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUserCountByIdenForEmptyIdenName() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.getUsersCountByIden( 2, "", true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchRegionByIdForInvalidRegionId() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.searchRegionById( 0l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchBranchNameByIdForInvalidBranchId() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.searchBranchNameById( 0l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchBranchRegionOrAgentByNameForNullSearchColumn() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.searchBranchRegionOrAgentByName( null, "test", "test1", 2 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchBranchRegionOrAgentByNameForEmptySearchColumn() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.searchBranchRegionOrAgentByName( "", "test", "test1", 2 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchBranchRegionOrAgentByNameForNullColumnName() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.searchBranchRegionOrAgentByName( "test", "test1", null, 2 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchBranchRegionOrAgentByNameForEmptyColumnName() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.searchBranchRegionOrAgentByName( "test", "test1", "", 2 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testFetchRegionsByCompanyForInvalidCompanyId() throws InvalidInputException, SolrException,
        MalformedURLException
    {
        solrSearchServiceImpl.fetchRegionsByCompany( 0l, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testFetchSocialPostsByEntityForNullEntityType() throws InvalidInputException, SolrException,
        MalformedURLException
    {
        solrSearchServiceImpl.fetchSocialPostsByEntity( null, 2, 0, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testFetchSocialPostsByEntityForEmptyEntityType() throws InvalidInputException, SolrException,
        MalformedURLException
    {
        solrSearchServiceImpl.fetchSocialPostsByEntity( "", 2, 0, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testFetchSocialPostsByEntityForInvalidEntityId() throws InvalidInputException, SolrException,
        MalformedURLException
    {
        solrSearchServiceImpl.fetchSocialPostsByEntity( "test", 0l, 0, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchPostTextForNullEntityType() throws InvalidInputException, SolrException, MalformedURLException
    {
        solrSearchServiceImpl.searchPostText( null, 2, 0, 5, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchPostTextForEmptyEntityType() throws InvalidInputException, SolrException, MalformedURLException
    {
        solrSearchServiceImpl.searchPostText( "", 2, 0, 5, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchPostTextForInvalidEntityId() throws InvalidInputException, SolrException, MalformedURLException
    {
        solrSearchServiceImpl.searchPostText( "test", 0l, 0, 5, "test1" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchPostTextForNullSearchQuery() throws InvalidInputException, SolrException, MalformedURLException
    {
        solrSearchServiceImpl.searchPostText( "test", 2, 0, 5, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testFetchBranchesByCompanyForInvalidCompanyId() throws InvalidInputException, SolrException,
        MalformedURLException
    {
        solrSearchServiceImpl.fetchBranchesByCompany( 0l, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testFetchBranchCountByCompanyForInvalidCompanyId() throws InvalidInputException, SolrException,
        MalformedURLException
    {
        solrSearchServiceImpl.fetchBranchCountByCompany( 0l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testFetchRegionCountByCompanyForInvalidCompanyId() throws InvalidInputException, SolrException,
        MalformedURLException
    {
        solrSearchServiceImpl.fetchRegionCountByCompany( 0l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testsearchUsersByBranchesForNullBranchIdSet() throws InvalidInputException, SolrException,
        MalformedURLException
    {
        solrSearchServiceImpl.searchUsersByBranches( null, 0, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testsearchUsersByBranchesForEmptyBranchIdSet() throws InvalidInputException, SolrException,
        MalformedURLException
    {
        solrSearchServiceImpl.searchUsersByBranches( new HashSet<Long>(), 0, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUsersCountByBranchesForNullBranchIdSet() throws InvalidInputException, SolrException,
        MalformedURLException
    {
        solrSearchServiceImpl.getUsersCountByBranches( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUsersCountByBranchesForEmptyBranchIdSet() throws InvalidInputException, SolrException,
        MalformedURLException
    {
        solrSearchServiceImpl.getUsersCountByBranches( new HashSet<Long>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddRegionsToSolrForNullList() throws InvalidInputException, SolrException, MalformedURLException
    {
        solrSearchServiceImpl.addRegionsToSolr( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddRegionsToSolrForEmptyList() throws InvalidInputException, SolrException, MalformedURLException
    {
        solrSearchServiceImpl.addRegionsToSolr( new ArrayList<Region>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddBranchesToSolrForNullList() throws InvalidInputException, SolrException, MalformedURLException
    {
        solrSearchServiceImpl.addBranchesToSolr( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddBranchesToSolrForEmptyList() throws InvalidInputException, SolrException, MalformedURLException
    {
        solrSearchServiceImpl.addBranchesToSolr( new ArrayList<Branch>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddSocialPostsToSolrForNullList() throws InvalidInputException, SolrException, MalformedURLException
    {
        solrSearchServiceImpl.addSocialPostsToSolr( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddSocialPostsToSolrForEmptyList() throws InvalidInputException, SolrException, MalformedURLException
    {
        solrSearchServiceImpl.addSocialPostsToSolr( new ArrayList<SocialPost>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddUsersToSolrForNullList() throws InvalidInputException, SolrException, MalformedURLException
    {
        solrSearchServiceImpl.addUsersToSolr( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddUsersToSolrForEmptyList() throws InvalidInputException, SolrException, MalformedURLException
    {
        solrSearchServiceImpl.addUsersToSolr( new ArrayList<User>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateCompletedSurveyCountForUserInSolrForInvalidAgentId() throws InvalidInputException, SolrException,
        NoRecordsFetchedException
    {
        solrSearchServiceImpl.updateCompletedSurveyCountForUserInSolr( 0l, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetCompanyAdminForInvalidCompanyId() throws InvalidInputException, SolrException, NoRecordsFetchedException
    {
        solrSearchServiceImpl.getCompanyAdmin( 0l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchUserIdsByCompanyForInvalidCompanyId() throws InvalidInputException, SolrException,
        NoRecordsFetchedException
    {
        solrSearchServiceImpl.searchUserIdsByCompany( 0l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testRemoveUsersFromSolrForNullList() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.removeUsersFromSolr( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testRemoveUsersFromSolrForEmptyList() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.removeUsersFromSolr( new ArrayList<Long>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchBranchIdsByCompanyForInvalidCompanyId() throws InvalidInputException, SolrException,
        NoRecordsFetchedException
    {
        solrSearchServiceImpl.searchBranchIdsByCompany( 0l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testRemoveBranchesFromSolrForNullList() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.removeBranchesFromSolr( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testRemoveBranchesFromSolrForEmptyList() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.removeBranchesFromSolr( new ArrayList<Long>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchRegionIdsByCompanyForInvalidCompanyId() throws InvalidInputException, SolrException,
        NoRecordsFetchedException
    {
        solrSearchServiceImpl.searchRegionIdsByCompany( 0l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testRemoveRegionsFromSolrForNullList() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.removeRegionsFromSolr( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testRemoveRegionsFromSolrForEmptyList() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.removeRegionsFromSolr( new ArrayList<Long>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetSocialPostsFromSolrDocumentsForNullDocumetList() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.getSocialPostsFromSolrDocuments( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUsersFromSolrDocumentsForNullDocumetList() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.getUsersFromSolrDocuments( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetUsersWithMetaDataFromSolrDocumentsForNullDocumetList() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.getUsersWithMetaDataFromSolrDocuments( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchUsersByLoginNameOrNameUnderAdminForNullPattern() throws InvalidInputException, SolrException,
        MalformedURLException
    {
        solrSearchServiceImpl.searchUsersByLoginNameOrNameUnderAdmin( null, new User(), new UserFromSearch(), 0, 5,"default" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchUsersByLoginNameOrNameUnderAdminForNullUser() throws InvalidInputException, SolrException,
        MalformedURLException
    {
        solrSearchServiceImpl.searchUsersByLoginNameOrNameUnderAdmin( "test", null, new UserFromSearch(), 0, 5,"default" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSearchUsersByLoginNameOrNameUnderAdminForNullUserFromSearch() throws InvalidInputException, SolrException,
        MalformedURLException
    {
        solrSearchServiceImpl.searchUsersByLoginNameOrNameUnderAdmin( "test", new User(), null, 0, 5,"default" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testgetUserIdsFromSolrDocumentListForNullDocumetList() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.getUserIdsFromSolrDocumentList( null );
    }


    //Tests for updateIsProfileImageSetFieldForMultipleUsers
    @Test ( expected = InvalidInputException.class)
    public void updateIsProfileImageSetFieldForMultipleUsersTestIsProfileSetMapNull() throws InvalidInputException,
        SolrException
    {
        solrSearchServiceImpl.updateIsProfileImageSetFieldForMultipleUsers( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void updateIsProfileImageSetFieldForMultipleUsersTestIsProfileSetMapEmpty() throws InvalidInputException,
        SolrException
    {
        solrSearchServiceImpl.updateIsProfileImageSetFieldForMultipleUsers( new HashMap<Long, Boolean>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void removeSocialPostFromSolrTestIDNull() throws SolrException, InvalidInputException
    {
        solrSearchServiceImpl.removeSocialPostFromSolr( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void removeSocialPostFromSolrTestIDEmpty() throws SolrException, InvalidInputException
    {
        solrSearchServiceImpl.removeSocialPostFromSolr( "" );
    }


    @Test ( expected = InvalidInputException.class)
    public void updateRegionsForMultipleUsersTestRegionsMapIsNull() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.updateRegionsForMultipleUsers( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateReviewCountOfUserInSolrWithNullUser() throws InvalidInputException, SolrException
    {
        solrSearchServiceImpl.updateReviewCountOfUserInSolr( null );
    }
}
