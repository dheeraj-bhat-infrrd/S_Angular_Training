package com.realtech.socialsurvey.core.services.search.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.LukeRequest;
import org.apache.solr.client.solrj.response.LukeResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.BranchFromSearch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.RegionFromSearch;
import com.realtech.socialsurvey.core.entities.SocialPost;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserFromSearch;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;


// JIRA:SS-62 BY RM 02
/**
 * Implementation class for solr search services
 */
@Component
public class SolrSearchServiceImpl implements SolrSearchService
{

    private static final Logger LOG = LoggerFactory.getLogger( SolrSearchServiceImpl.class );
    private static final String SOLR_EDIT_REPLACE = "set";

    @Value ( "${SOLR_REGION_URL}")
    private String solrRegionUrl;

    @Value ( "${SOLR_BRANCH_URL}")
    private String solrBranchUrl;

    @Value ( "${SOLR_USER_URL}")
    private String solrUserUrl;

    @Value ( "${SOLR_SOCIAL_POST_URL}")
    private String solrSocialPostUrl;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;

    @Autowired
    private ProfileManagementService profileManagementService;

    @Autowired
    private BatchTrackerService batchTrackerService;

    @Autowired
    private OrganizationManagementService organizationManagementService;


    /**
     * Method to perform search of regions from solr based on input pattern , company and regionIds
     * if provided
     */
    @Override
    public String searchRegions( String regionPattern, Company company, Set<Long> regionIds, int start, int rows )
        throws InvalidInputException, SolrException
    {
        LOG.info( "Method searchRegions called for regionPattern :" + regionPattern );
        if ( regionPattern == null ) {
            throw new InvalidInputException( "Region pattern is null while searching for region" );
        }
        if ( company == null ) {
            throw new InvalidInputException( "company is null or empty while searching for region" );
        }
        LOG.info( "Method searchRegions called for regionPattern : " + regionPattern + " and company : " + company );
        String regionResult = null;
        QueryResponse response = null;
        try {
            regionPattern = regionPattern + "*";

            SolrServer solrServer = new HttpSolrServer( solrRegionUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery( CommonConstants.REGION_NAME_SOLR + ":" + regionPattern );
            solrQuery.addFilterQuery( CommonConstants.COMPANY_ID_SOLR + ":" + company.getCompanyId(),
                CommonConstants.STATUS_COLUMN + ":" + CommonConstants.STATUS_ACTIVE,
                CommonConstants.IS_DEFAULT_BY_SYSTEM_SOLR + ":" + CommonConstants.NO );

            if ( regionIds != null && !regionIds.isEmpty() ) {
                String regionIdsStr = getSpaceSeparatedStringFromIds( regionIds );
                solrQuery.addFilterQuery( CommonConstants.REGION_ID_SOLR + ":(" + regionIdsStr + ")" );
            }

            solrQuery.setStart( start );
            if ( rows > 0 ) {
                solrQuery.setRows( rows );
            }
            solrQuery.addSort( CommonConstants.REGION_NAME_SOLR, ORDER.asc );

            LOG.debug( "Querying solr for searching regions" );
            response = solrServer.query( solrQuery );
            SolrDocumentList results = response.getResults();
            Collection<RegionFromSearch> regions = getRegionsFromSolrDocuments( results );
            regionResult = new Gson().toJson( regions );
            LOG.debug( "Region search result is : " + regionResult );

        } catch ( SolrServerException e ) {
            LOG.error( "UnsupportedEncodingException while performing region search" );
            throw new SolrException( "Exception while performing search. Reason : " + e.getMessage(), e );
        }

        LOG.info( "Method searchRegions finished for regionPattern :" + regionPattern + " returning : " + regionResult );
        return regionResult;
    }


    /**
     * Method to perform search of regions from solr based on input pattern , company and regionIds
     * if provided
     */
    @Override
    public long getRegionsCount( String regionPattern, Company company, Set<Long> regionIds )
        throws InvalidInputException, SolrException
    {
        LOG.info( "Method searchRegions called for regionPattern :" + regionPattern );
        if ( regionPattern == null ) {
            throw new InvalidInputException( "Region pattern is null while searching for region" );
        }
        if ( company == null ) {
            throw new InvalidInputException( "company is null or empty while searching for region" );
        }
        LOG.info( "Method searchRegions called for regionPattern : " + regionPattern + " and company : " + company );
        String regionResult = null;
        QueryResponse response = null;
        try {
            regionPattern = regionPattern + "*";

            SolrServer solrServer = new HttpSolrServer( solrRegionUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery( CommonConstants.REGION_NAME_SOLR + ":" + regionPattern );
            solrQuery.addFilterQuery( CommonConstants.COMPANY_ID_SOLR + ":" + company.getCompanyId(),
                CommonConstants.STATUS_COLUMN + ":" + CommonConstants.STATUS_ACTIVE,
                CommonConstants.IS_DEFAULT_BY_SYSTEM_SOLR + ":" + CommonConstants.NO );

            if ( regionIds != null && !regionIds.isEmpty() ) {
                String regionIdsStr = getSpaceSeparatedStringFromIds( regionIds );
                solrQuery.addFilterQuery( CommonConstants.REGION_ID_SOLR + ":(" + regionIdsStr + ")" );
            }
            LOG.debug( "Querying solr for searching regions" );
            response = solrServer.query( solrQuery );
            LOG.debug( "Region search result is : " + regionResult );

        } catch ( SolrServerException e ) {
            LOG.error( "UnsupportedEncodingException while performing region search" );
            throw new SolrException( "Exception while performing search. Reason : " + e.getMessage(), e );
        }

        LOG.info( "Method searchRegions finished for regionPattern :" + regionPattern + " returning : " + regionResult );
        return response.getResults().getNumFound();
    }


    /**
     * Method to perform search of branches from solr based on the input pattern, company and
     * branchIds
     */
    @Override
    public String searchBranches( String branchPattern, Company company, String idColumnName, Set<Long> branchIds, int start,
        int rows ) throws InvalidInputException, SolrException
    {
        LOG.info( "Method searchBranches called for branchPattern :" + branchPattern + " idColumnName:" + idColumnName );
        if ( branchPattern == null ) {
            throw new InvalidInputException( "Branch pattern is null while searching for branch" );
        }
        if ( company == null ) {
            throw new InvalidInputException( "Company is null while searching for branch" );
        }
        LOG.info( "Method searchBranches called for branchPattern : " + branchPattern + " and company : " + company );
        String branchResult = null;
        QueryResponse response = null;
        try {

            branchPattern = branchPattern + "*";

            SolrServer solrServer = new HttpSolrServer( solrBranchUrl );
            SolrQuery query = new SolrQuery();
            query.setQuery( CommonConstants.BRANCH_NAME_SOLR + ":" + branchPattern );
            query.addFilterQuery( CommonConstants.COMPANY_ID_SOLR + ":" + company.getCompanyId(),
                CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE,
                CommonConstants.IS_DEFAULT_BY_SYSTEM_SOLR + ":" + CommonConstants.NO );
            query.setStart( start );
            if ( branchIds != null && !branchIds.isEmpty() ) {
                if ( idColumnName == null || idColumnName.isEmpty() ) {
                    throw new InvalidInputException( "column name is not specified in search branches" );
                }
                String idsStr = getSpaceSeparatedStringFromIds( branchIds );
                query.addFilterQuery( idColumnName + ":(" + idsStr + ")" );
            }
            if ( rows > 0 ) {
                query.setRows( rows );
            }

            LOG.debug( "Querying solr for searching branches" );
            response = solrServer.query( query );
            SolrDocumentList documentList = response.getResults();

            Collection<BranchFromSearch> branches = getBranchesFromSolrDocuments( documentList );

            branchResult = new Gson().toJson( branches );

            LOG.debug( "Results obtained from solr :" + branchResult );
        } catch ( SolrServerException e ) {
            LOG.error( "SolrServerException while performing branch search" );
            throw new SolrException( "Exception while performing search. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method searchBranches finished for branchPattern :" + branchPattern );
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
    public String searchBranchesByRegion( long regionId, int start, int rows ) throws InvalidInputException, SolrException
    {
        LOG.info( "Method searchBranchesByRegion() to search branches in a region started" );

        if ( regionId <= 0l ) {
            throw new InvalidInputException( "Invalid parameter passed : region id is invalid" );
        }

        String branchResult = null;
        QueryResponse response = null;
        try {

            SolrServer solrServer = new HttpSolrServer( solrBranchUrl );
            SolrQuery query = new SolrQuery();
            query.setQuery( CommonConstants.REGION_ID_SOLR + ":" + regionId );
            query.addFilterQuery( CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE,
                CommonConstants.IS_DEFAULT_BY_SYSTEM_SOLR + ":" + CommonConstants.NO );
            query.setStart( start );

            if ( rows > 0 ) {
                query.setRows( rows );
            }
            query.addSort( CommonConstants.BRANCH_NAME_SOLR, ORDER.asc );

            LOG.debug( "Querying solr for searching branches" );
            response = solrServer.query( query );
            SolrDocumentList documentList = response.getResults();

            Collection<BranchFromSearch> branches = getBranchesFromSolrDocuments( documentList );

            branchResult = new Gson().toJson( branches );

            LOG.debug( "Results obtained from solr :" + branchResult );
        } catch ( SolrServerException e ) {
            LOG.error( "SolrServerException while performing branch search" );
            throw new SolrException( "Exception while performing search. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method searchBranchesByRegion() to search branches in a region finished" );
        return branchResult;
    }


    /**
     * Method to perform count of branches from solr based on the region id.
     * 
     * @param regionId
     * @param start
     * @param rows
     * @return list of branches
     * @throws InvalidInputException
     * @throws SolrException
     */
    @Override
    public long getBranchCountByRegion( long regionId ) throws InvalidInputException, SolrException
    {
        LOG.info( "Method searchBranchesByRegion() to search branches in a region started" );

        if ( regionId <= 0l ) {
            throw new InvalidInputException( "Invalid parameter passed : region id is invalid" );
        }

        String branchResult = null;
        QueryResponse response = null;
        try {

            SolrServer solrServer = new HttpSolrServer( solrBranchUrl );
            SolrQuery query = new SolrQuery();
            query.setQuery( CommonConstants.REGION_ID_SOLR + ":" + regionId );
            query.addFilterQuery( CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE,
                CommonConstants.IS_DEFAULT_BY_SYSTEM_SOLR + ":" + CommonConstants.NO );

            LOG.debug( "Querying solr for counting branches" );
            response = solrServer.query( query );
            LOG.debug( "Results obtained from solr :" + branchResult );
        } catch ( SolrServerException e ) {
            LOG.error( "SolrServerException while performing branch search" );
            throw new SolrException( "Exception while performing search. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method searchBranchesByRegion() to search branches in a region finished" );
        return response.getResults().getNumFound();
    }


    /**
     * Method to add region into solr
     * @throws InvalidInputException 
     */
    @Override
    public void addOrUpdateRegionToSolr( Region region ) throws SolrException, InvalidInputException
    {
        LOG.info( "Method to add or update region to solr called for region : " + region );

        if ( region == null ) {
            throw new InvalidInputException( "Invalid parameter is passed : region parameter is null" );
        }

        SolrServer solrServer;
        try {

            solrServer = new HttpSolrServer( solrRegionUrl );
            SolrInputDocument document = getSolrDocumentFromRegion( region );
            UpdateResponse response = solrServer.add( document );
            LOG.debug( "response is while adding/updating region is : " + response );
            solrServer.commit();
        } catch ( MalformedURLException e ) {
            LOG.error( "Exception while adding/updating regions to solr. Reason : " + e.getMessage(), e );
            throw new SolrException( "Exception while adding/updating regions to solr. Reason : " + e.getMessage(), e );
        } catch ( SolrServerException | IOException e ) {
            LOG.error( "Exception while adding/updating regions to solr. Reason : " + e.getMessage(), e );
            throw new SolrException( "Exception while adding/updating regions to solr. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method to add or update region to solr finshed for region : " + region );

    }


    /**
     * Method to add branch into solr
     * @throws InvalidInputException 
     */
    @Override
    public void addOrUpdateBranchToSolr( Branch branch ) throws SolrException, InvalidInputException
    {
        LOG.info( "Method to add/update branch to solr called for branch : " + branch );

        if ( branch == null ) {
            throw new InvalidInputException( "Invalid parameter is passed : branch parameter is null" );
        }

        SolrServer solrServer;
        try {
            solrServer = new HttpSolrServer( solrBranchUrl );
            SolrInputDocument document = getSolrDocumentFromBranch( branch );

            UpdateResponse response = solrServer.add( document );
            LOG.debug( "response while adding/updating branch is : " + response );
            solrServer.commit();
        } catch ( MalformedURLException e ) {
            LOG.error( "Exception while adding/updating branch to solr. Reason : " + e.getMessage(), e );
            throw new SolrException( "Exception while adding/updating branch to solr. Reason : " + e.getMessage(), e );
        } catch ( SolrServerException | IOException e ) {
            LOG.error( "Exception while adding/updating branch to solr. Reason : " + e.getMessage(), e );
            throw new SolrException( "Exception while adding/updating branch to solr. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method to add/update branch to solr finshed for branch : " + branch );
    }


    /**
     * Method to get solr input document from branch
     * 
     * @param branch
     * @return
     */
    private SolrInputDocument getSolrDocumentFromBranch( Branch branch )
    {
        LOG.debug( "Method getSolrDocumentFromBranch called for branch " + branch );

        SolrInputDocument document = new SolrInputDocument();
        document.addField( CommonConstants.REGION_ID_SOLR, branch.getRegion().getRegionId() );
        document.addField( CommonConstants.REGION_NAME_SOLR, branch.getRegion().getRegion() );
        document.addField( CommonConstants.BRANCH_ID_SOLR, branch.getBranchId() );
        document.addField( CommonConstants.BRANCH_NAME_SOLR, branch.getBranch() );
        document.addField( CommonConstants.COMPANY_ID_SOLR, branch.getCompany().getCompanyId() );
        document.addField( CommonConstants.IS_DEFAULT_BY_SYSTEM_SOLR, branch.getIsDefaultBySystem() );
        document.addField( CommonConstants.STATUS_SOLR, branch.getStatus() );
        document.addField( CommonConstants.ADDRESS1_SOLR, branch.getAddress1() );
        document.addField( CommonConstants.ADDRESS2_SOLR, branch.getAddress2() );

        LOG.debug( "Method getSolrDocumentFromBranch finished for branch " + branch );
        return document;
    }


    /**
     * Method to get solr input document from social post
     * @param socialPost
     * @return
     */
    private SolrInputDocument getSolrDocumentFromSocialPost( SocialPost socialPost )
    {
        LOG.debug( "Method getSolrDocumentFromSocialPost called for social post " + socialPost );

        SolrInputDocument document = new SolrInputDocument();

        document.addField( CommonConstants.ID_SOLR, socialPost.get_id() );
        document.addField( CommonConstants.SOURCE_SOLR, socialPost.getSource() );
        document.addField( CommonConstants.COMPANY_ID_SOLR, socialPost.getCompanyId() );
        document.addField( CommonConstants.REGION_ID_SOLR, socialPost.getRegionId() );
        document.addField( CommonConstants.BRANCH_ID_SOLR, socialPost.getBranchId() );
        document.addField( CommonConstants.USER_ID_SOLR, socialPost.getAgentId() );
        document.addField( CommonConstants.TIME_IN_MILLIS_SOLR, socialPost.getTimeInMillis() );
        document.addField( CommonConstants.POST_ID_SOLR, socialPost.getPostId() );
        document.addField( CommonConstants.POST_TEXT_SOLR, socialPost.getPostText() );
        document.addField( CommonConstants.POSTED_BY_SOLR, socialPost.getPostedBy() );
        document.addField( CommonConstants.POST_URL_SOLR, socialPost.getPostUrl() );

        LOG.debug( "Method getSolrDocumentFromSocialPost finished for social post " + socialPost );
        return document;
    }


    /**
     * Method to get solr document from a region
     * 
     * @param region
     * @return
     */
    private SolrInputDocument getSolrDocumentFromRegion( Region region )
    {
        LOG.debug( "Method getSolrDocumentFromRegion called for region " + region );

        SolrInputDocument document = new SolrInputDocument();
        document.addField( CommonConstants.REGION_ID_SOLR, region.getRegionId() );
        document.addField( CommonConstants.REGION_NAME_SOLR, region.getRegion() );
        document.addField( CommonConstants.COMPANY_ID_SOLR, region.getCompany().getCompanyId() );
        document.addField( CommonConstants.IS_DEFAULT_BY_SYSTEM_SOLR, region.getIsDefaultBySystem() );
        document.addField( CommonConstants.STATUS_SOLR, region.getStatus() );
        document.addField( CommonConstants.ADDRESS1_SOLR, region.getAddress1() );
        document.addField( CommonConstants.ADDRESS2_SOLR, region.getAddress2() );

        LOG.debug( "Method getSolrDocumentFromRegion finished for region " + region );
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
    public String searchUsersByLoginNameAndCompany( String loginNamePattern, Company company )
        throws InvalidInputException, SolrException
    {
        LOG.info( "Method searchUsers called for userNamePattern :" + loginNamePattern );
        if ( loginNamePattern == null || loginNamePattern.isEmpty() ) {
            throw new InvalidInputException( "Username pattern is null or empty while searching for Users" );
        }
        if ( company == null ) {
            throw new InvalidInputException( "company is null or empty while searching for users" );
        }
        LOG.info( "Method searchUsers() called for userNamePattern : " + loginNamePattern + " and company : " + company );
        String usersResult = null;
        QueryResponse response = null;
        try {
            loginNamePattern = loginNamePattern + "*";

            SolrServer solrServer = new HttpSolrServer( solrUserUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery( CommonConstants.USER_LOGIN_NAME_SOLR + ":" + loginNamePattern );
            solrQuery.addFilterQuery( CommonConstants.COMPANY_ID_SOLR + ":" + company.getCompanyId(),
                CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE );

            LOG.debug( "Querying solr for searching users" );
            response = solrServer.query( solrQuery );
            SolrDocumentList results = response.getResults();

            usersResult = new Gson().toJson( getUsersFromSolrDocuments( results ) );
            LOG.debug( "User search result is : " + usersResult );

        } catch ( SolrServerException e ) {
            LOG.error( "UnsupportedEncodingException while performing User search" );
            throw new SolrException( "Exception while performing search for user. Reason : " + e.getMessage(), e );
        }

        LOG.info( "Method searchUsers finished for username pattern :" + loginNamePattern + " returning : " + usersResult );
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
    public SolrDocumentList searchUsersByLoginNameOrName( String pattern, long companyId, int startIndex, int batchSize )
        throws InvalidInputException, SolrException, MalformedURLException
    {
        LOG.info( "Method searchUsersByLoginNameOrName called for pattern :" + pattern );
        if ( pattern == null ) {
            throw new InvalidInputException( "Pattern is null or empty while searching for Users" );
        }

        if ( companyId <= 0l ) {
            throw new InvalidInputException( "Invalid company id while searching for Users" );
        }

        LOG.info( "Method searchUsersByLoginNameOrName() called for parameter : " + pattern );
        SolrDocumentList results;
        QueryResponse response = null;
        pattern = pattern + "*";
        try {
            SolrServer solrServer = new HttpSolrServer( solrUserUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery( "displayName:" + pattern + " OR " + CommonConstants.USER_FIRST_NAME_SOLR + ":" + pattern
                + " OR " + CommonConstants.USER_LAST_NAME_SOLR + ":" + pattern + " OR " + CommonConstants.USER_LOGIN_NAME_SOLR
                + ":" + pattern );
            solrQuery.addFilterQuery( "companyId:" + companyId );
            solrQuery.addFilterQuery( CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE + " OR "
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_NOT_VERIFIED + " OR " + CommonConstants.STATUS_SOLR
                + ":" + CommonConstants.STATUS_TEMPORARILY_INACTIVE );
            solrQuery.addSort( CommonConstants.USER_DISPLAY_NAME_SOLR, ORDER.asc );
            LOG.debug( "Querying solr for searching users" );
            if ( startIndex > -1 ) {
                solrQuery.setStart( startIndex );
            }
            if ( batchSize > 0 ) {
                solrQuery.setRows( batchSize );
            }

            response = solrServer.query( solrQuery );

            // Change to get all matching records if batch size is not defined
            if ( batchSize <= 0 ) {
                int rows = (int) response.getResults().getNumFound();
                solrQuery.setRows( rows );
                response = solrServer.query( solrQuery );
            }

            results = response.getResults();
        } catch ( SolrServerException e ) {
            LOG.error( "SolrServerException while performing User search" );
            throw new SolrException( "Exception while performing search for user. Reason : " + e.getMessage(), e );
        }

        LOG.info( "Method searchUsersByLoginNameOrName finished for pattern :" + pattern );
        return results;
    }


    /**
     * Method to search for users given their first and/or last name
     */
    @Override
    public SolrDocumentList searchUsersByFirstOrLastName( String patternFirst, String patternLast, int startIndex, int noOfRows,
        String companyProfileName ) throws InvalidInputException, SolrException, MalformedURLException
    {
        LOG.info( "Method searchUsersByFirstOrLastName() called for pattern :" + patternFirst + ", " + patternLast );
        if ( patternFirst == null && patternLast == null ) {
            throw new InvalidInputException( "Pattern is null or empty while searching for Users" );
        }

        QueryResponse response = null;
        try {
            SolrQuery solrQuery = new SolrQuery();

            String[] fields = { CommonConstants.USER_ID_SOLR, CommonConstants.USER_DISPLAY_NAME_SOLR,
                CommonConstants.TITLE_SOLR, CommonConstants.ABOUT_ME_SOLR, CommonConstants.PROFILE_IMAGE_URL_SOLR,
                CommonConstants.PROFILE_URL_SOLR, CommonConstants.REVIEW_COUNT_SOLR };
            solrQuery.setFields( fields );
            List<SortClause> sortList = new ArrayList<SolrQuery.SortClause>();
            sortList.add( new SortClause( CommonConstants.REVIEW_COUNT_SOLR, ORDER.desc ) );
            sortList.add( new SortClause( CommonConstants.IS_PROFILE_IMAGE_SET_SOLR, ORDER.desc ) );
            solrQuery.setSorts( sortList );
            //solrQuery.setSort( CommonConstants.REVIEW_COUNT_SOLR, ORDER.desc );
            //solrQuery.setSort( CommonConstants.IS_PROFILE_IMAGE_SET_SOLR, ORDER.desc );
            String query = "";
            String firstName = "";
            String lastName = "";
            String displayName = null;
            if ( !patternFirst.equals( "" ) && !patternLast.equals( "" ) ) {
                query = CommonConstants.USER_FIRST_NAME_SOLR + ":" + patternFirst + "*" + " OR "
                    + CommonConstants.USER_LAST_NAME_SOLR + ":" + patternLast + "*";
                firstName = patternFirst;
                lastName = patternLast;
                displayName = firstName.trim() + " " + lastName.trim();
            } else if ( !patternFirst.equals( "" ) && patternLast.equals( "" ) ) {
                query = CommonConstants.USER_FIRST_NAME_SOLR + ":" + patternFirst + "*";
                firstName = patternFirst;
                displayName = firstName.trim();
            } else if ( patternFirst.equals( "" ) && !patternLast.equals( "" ) ) {
                query = CommonConstants.USER_LAST_NAME_SOLR + ":" + patternLast + "*";
                lastName = patternLast;
                displayName = lastName.trim();
            }

            //search display name 
            if ( displayName != null && !displayName.isEmpty() ) {
                query = query + " OR " + generateSubQueryToSearch( CommonConstants.USER_DISPLAY_NAME_SOLR, displayName );
            }
            solrQuery.setQuery( query );
            solrQuery.addFilterQuery( CommonConstants.IS_AGENT_SOLR + ":" + CommonConstants.IS_AGENT_TRUE_SOLR );
            solrQuery.addFilterQuery( "-" + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_INACTIVE );
            solrQuery.addFilterQuery( "-" + CommonConstants.USER_IS_HIDDEN_FROM_SEARCH_SOLR + ":" + true );
            solrQuery.setStart( startIndex );
            solrQuery.setRows( noOfRows );

            if ( companyProfileName != null ) {
                OrganizationUnitSettings companyProfile = null;
                try {
                    companyProfile = profileManagementService.getCompanyProfileByProfileName( companyProfileName );
                } catch ( ProfileNotFoundException e ) {
                    LOG.error( "Company profile not found with profile name: " + companyProfileName );
                }
                if ( companyProfile != null ) {
                    solrQuery.addFilterQuery( CommonConstants.COMPANY_ID_SOLR + ":" + companyProfile.getIden() );
                }
            }

            LOG.debug( "Querying solr for searching users" );
            SolrServer solrServer = new HttpSolrServer( solrUserUrl );
            response = solrServer.query( solrQuery );
        } catch ( SolrServerException e ) {
            LOG.error( "SolrServerException while performing User search" );
            throw new SolrException( "Exception while performing search for user. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method searchUsersByFirstOrLastName() called for parameter : " + patternFirst + ", " + patternLast
            + " returning : " + response.getResults() );
        return response.getResults();
    }


    private String generateSubQueryToSearch( String columnName, String srchString )
    {
        String query = "";
        if ( srchString.contains( " " ) ) {
            String[] srchStringArray = srchString.split( " " );
            for ( int i = 0; i < srchStringArray.length; i++ ) {
                String curSrchString = ( srchStringArray[i] ).trim() + "*";
                if ( i != 0 )
                    query = query + " OR ";
                query = query + columnName + ":" + curSrchString + " OR " + columnName + ":" + "*\\ " + curSrchString;
            }
        } else {
            srchString = srchString.trim() + "*";
            query = query + columnName + ":" + srchString + " OR " + columnName + ":" + "*\\ " + srchString;
        }

        return query;
    }


    private SolrQuery fixQuery( SolrQuery query, String searchColumn, String searchKey )
    {
        if ( !searchKey.contains( " " ) ) {
            query.addFilterQuery( searchColumn + ":" + searchKey );
            return query;
        }
        if ( searchColumn.equalsIgnoreCase( CommonConstants.USER_DISPLAY_NAME_SOLR ) ) {
            searchKey = searchKey.replace( " ", "\\ " );
            query.addFilterQuery( searchColumn + ":" + searchKey );
            return query;
        }
        /*
        Example:
        Office first second third
        q=branchName:Office\ first\ second
        fq=branchName:third*
        So the last word should be part of fq
        the remaining should be part of q, with whitespaces escaped
         */
        //Take the last word and put that as part of fq
        String lastWord = searchKey.substring( searchKey.lastIndexOf( ' ' ), searchKey.length() );
        //Put this as part of the query
        String queryStr = searchKey.substring( 0, searchKey.lastIndexOf( ' ' ) ).replace( " ", "\\ " );

        query.addFilterQuery( searchColumn + ":" + queryStr );
        query.addFilterQuery( searchColumn + ":" + lastWord );
        return query;
    }


    /**
     * Method to perform search of Users from solr based on the input pattern for user and company.
     * 
     * @throws InvalidInputException
     * @throws SolrException
     * @throws MalformedURLException
     */
    @Override
    public SolrDocumentList searchUsersByCompany( long companyId, int startIndex, int noOfRows )
        throws InvalidInputException, SolrException
    {
        if ( companyId <= 0l ) {
            throw new InvalidInputException( "companyId is null or empty while searching for Users" );
        }
        LOG.info( "Method searchUsersByCompanyId() called for company id : " + companyId );

        SolrDocumentList results = null;
        try {
            SolrServer solrServer = new HttpSolrServer( solrUserUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery( CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE + " OR "
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_NOT_VERIFIED + " OR " + CommonConstants.STATUS_SOLR
                + ":" + CommonConstants.STATUS_TEMPORARILY_INACTIVE );
            solrQuery.addFilterQuery( CommonConstants.COMPANY_ID_SOLR + ":" + companyId );
            solrQuery.setStart( startIndex );
            solrQuery.setRows( noOfRows );
            solrQuery.addSort( CommonConstants.USER_DISPLAY_NAME_SOLR, ORDER.asc );
            LOG.debug( "Querying solr for searching users" );

            results = solrServer.query( solrQuery ).getResults();
        } catch ( SolrServerException e ) {
            LOG.error( "SolrServerException while performing User search" );
            throw new SolrException( "Exception while performing search for user. Reason : " + e.getMessage(), e );
        }

        LOG.info( "Method searchUsersByCompanyId() finished for company id : " + companyId );
        return results;
    }


    /**
     * Method to find the number of users in a given company
     */
    @Override
    public long countUsersByCompany( long companyId, int startIndex, int noOfRows )
        throws InvalidInputException, SolrException, MalformedURLException
    {
        LOG.info( "Method countUsersByCompany() called for company id : {}", companyId );
        if ( companyId <= 0l ) {
            LOG.warn( "Pattern is null or empty while searching for Users" );
            throw new InvalidInputException( "Pattern is null or empty while searching for Users" );
        }

        long resultsCount = 0l;
        QueryResponse response = null;
        try {
            SolrServer solrServer = new HttpSolrServer( solrUserUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery( CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE + " OR "
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_NOT_VERIFIED + " OR " + CommonConstants.STATUS_SOLR
                + ":" + CommonConstants.STATUS_TEMPORARILY_INACTIVE );
            solrQuery.addFilterQuery( CommonConstants.COMPANY_ID_SOLR + ":" + companyId );
            solrQuery.setStart( startIndex );
            solrQuery.setRows( noOfRows );
            response = solrServer.query( solrQuery );

            resultsCount = response.getResults().getNumFound();
            LOG.debug( "User search result count is : {}", resultsCount );
        } catch ( SolrServerException e ) {
            LOG.warn( "SolrServerException while performing User search" );
            throw new SolrException( "Exception while performing search for user. Reason : " + e.getMessage(), e );
        }

        LOG.info( "Method countUsersByCompany() finished for company id : {}" , companyId );
        return resultsCount;
    }


    /**
     * Method to add User into solr
     * @throws InvalidInputException 
     */
    @Override
    public void addUserToSolr( User user ) throws SolrException, InvalidInputException
    {
        if ( user == null ) {
            throw new InvalidInputException( "Invalid parameter passed : user pararmeter is null" );
        }


        LOG.info( "Method to add user to solr called for user : " + user.getFirstName() );
        SolrServer solrServer;
        UpdateResponse response = null;
        try {
            solrServer = new HttpSolrServer( solrUserUrl );
            SolrInputDocument document = new SolrInputDocument();
            document = getSolrInputDocumentFromUser( user, document );
            response = solrServer.add( document );
            LOG.debug( "response while adding user is: " + response );
            solrServer.commit();
        } catch ( MalformedURLException e ) {
            LOG.error( "Exception while adding user to solr. Reason : " + e.getMessage(), e );
            throw new SolrException( "Exception while adding user to solr. Reason : " + e.getMessage(), e );
        } catch ( SolrServerException | IOException e ) {
            LOG.error( "Exception while adding user to solr. Reason : " + e.getMessage(), e );
            throw new SolrException( "Exception while adding user to solr. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method to add user to solr finshed for user : " + user );
    }


    /**
     * Method to generate a SolrInputDocument given a User object
     * 
     * @param user
     * @param document
     * @return
     */
    private SolrInputDocument getSolrInputDocumentFromUser( User user, SolrInputDocument document )
    {
        if ( user.getLastName() != null && !( user.getLastName().equals( "" ) ) ) {
            user.setLastName( user.getLastName().trim() );
        }
        if ( user.getFirstName() != null && !( user.getFirstName().equals( "" ) ) ) {
            user.setFirstName( user.getFirstName().trim() );
        }

        document.addField( CommonConstants.USER_ID_SOLR, user.getUserId() );
        document.addField( CommonConstants.USER_FIRST_NAME_SOLR, user.getFirstName() );
        document.addField( CommonConstants.USER_LAST_NAME_SOLR, user.getLastName() );
        document.addField( CommonConstants.USER_EMAIL_ID_SOLR, user.getEmailId() );
        document.addField( CommonConstants.USER_LOGIN_NAME_COLUMN, user.getEmailId() );
        document.addField( CommonConstants.USER_IS_OWNER_SOLR, user.getIsOwner() );
        document.addField( CommonConstants.REVIEW_COUNT_SOLR, 0 );

        String displayName = user.getFirstName();
        if ( user.getLastName() != null ) {
            displayName = displayName + " " + user.getLastName();
        }
        document.addField( CommonConstants.USER_DISPLAY_NAME_SOLR, displayName );

        /**
         * add/update profile url and profile name in solr only when they are not null
         */
        if ( user.getProfileName() != null && !user.getProfileName().isEmpty() ) {
            document.addField( CommonConstants.PROFILE_NAME_SOLR, user.getProfileName() );
        }
        if ( user.getProfileUrl() != null && !user.getProfileUrl().isEmpty() ) {
            document.addField( CommonConstants.PROFILE_URL_SOLR, user.getProfileUrl() );
        }
        Long companyId = user.getCompany().getCompanyId();
        if ( user.getCompany() != null ) {
            document.addField( CommonConstants.COMPANY_ID_SOLR, companyId );
            if ( organizationUnitSettingsDao
                .fetchOrganizationUnitSettingsById( companyId, CommonConstants.COMPANY_SETTINGS_COLLECTION )
                .isHiddenSection() ) {
                document.addField( CommonConstants.USER_IS_HIDDEN_FROM_SEARCH_SOLR, true );
            } else {
                document.addField( CommonConstants.USER_IS_HIDDEN_FROM_SEARCH_SOLR, false );
            }
        }
        document.addField( CommonConstants.STATUS_SOLR, user.getStatus() );
        Set<Long> branches = new HashSet<Long>();
        Set<Long> regions = new HashSet<Long>();
        boolean isAgent = false;
        boolean isBranchAdmin = false;
        boolean isRegionAdmin = false;
        if ( user.getUserProfiles() != null )
            for ( UserProfile userProfile : user.getUserProfiles() ) {
                if ( userProfile.getStatus() != CommonConstants.STATUS_ACTIVE ) {
                    continue;
                }
                if ( userProfile.getRegionId() != 0 ) {
                    regions.add( userProfile.getRegionId() );
                }
                if ( userProfile.getBranchId() != 0 ) {
                    branches.add( userProfile.getBranchId() );
                }
                if ( userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID )
                    isAgent = true;
                if ( userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID )
                    isBranchAdmin = true;
                if ( userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID )
                    isRegionAdmin = true;
            }
        document.addField( CommonConstants.BRANCHES_SOLR, branches );
        document.addField( CommonConstants.REGIONS_SOLR, regions );
        document.addField( CommonConstants.IS_AGENT_SOLR, ( user.isAgent() ? user.isAgent() : isAgent ) );
        document.addField( CommonConstants.IS_BRANCH_ADMIN_SOLR,
            ( user.isBranchAdmin() ? user.isBranchAdmin() : isBranchAdmin ) );
        document.addField( CommonConstants.IS_REGION_ADMIN_SOLR,
            ( user.isRegionAdmin() ? user.isRegionAdmin() : isRegionAdmin ) );
        try {
            AgentSettings agentSettings = userManagementService.getUserSettings( user.getUserId() );
            //Set profileImageUrl fields if present
            if ( agentSettings.getProfileImageUrl() != null && !( agentSettings.getProfileImageUrl().isEmpty() ) ) {
                document.addField( CommonConstants.IS_PROFILE_IMAGE_SET_SOLR, true );
                document.addField( CommonConstants.PROFILE_IMAGE_URL_SOLR, agentSettings.getProfileImageUrl() );
                if ( agentSettings.getProfileImageUrlThumbnail() != null
                    && !( agentSettings.getProfileImageUrlThumbnail().isEmpty() ) ) {
                    document.addField( CommonConstants.PROFILE_IMAGE_THUMBNAIL_COLUMN,
                        agentSettings.getProfileImageUrlThumbnail() );
                } else {
                    document.addField( CommonConstants.PROFILE_IMAGE_THUMBNAIL_COLUMN, agentSettings.getProfileImageUrl() );
                }

            } else {
                document.addField( CommonConstants.IS_PROFILE_IMAGE_SET_SOLR, false );
            }
        } catch ( InvalidInputException e ) {
            LOG.info( "No agentSettings found for userId :" + user.getUserId() );
            document.addField( CommonConstants.IS_PROFILE_IMAGE_SET_SOLR, false );
        }

        return document;
    }


    /*
     * Method to remove a user from Solr
     */
    @Override
    public void removeUserFromSolr( long userIdToRemove ) throws SolrException, InvalidInputException
    {
        LOG.info( "Method removeUserFromSolr() to remove user id {} from solr started.", userIdToRemove );
        if ( userIdToRemove <= 0l ) {
            throw new InvalidInputException( "Invalid input pareameter : passed user id is not valid" );
        }

        try {
            SolrServer solrServer = new HttpSolrServer( solrUserUrl );
            solrServer.deleteById( String.valueOf( userIdToRemove ) );
            solrServer.commit();
        } catch ( SolrServerException | IOException e ) {
            LOG.error( "Exception while removing user from solr. Reason : " + e.getMessage(), e );
            throw new SolrException( "Exception while removing user from solr. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method removeUserFromSolr() to remove user id {} from solr finished successfully.", userIdToRemove );
    }


    /*
     * Method to fetch display name of a user from solr based upon user id provided.
     */
    @Override
    public String getUserDisplayNameById( long userId ) throws InvalidInputException, NoRecordsFetchedException, SolrException
    {
        LOG.info( "Method to fetch user from solr based upon user id, searchUserById() started." );

        if ( userId <= 0l ) {
            throw new InvalidInputException( "Invalid input pareameter : passed user id is not valid" );
        }

        SolrDocument solrDocument = getUserByUniqueId( userId );
        if ( solrDocument == null || solrDocument.isEmpty() ) {
            throw new NoRecordsFetchedException( "No document found in solr for userId:" + userId );
        }
        String displayName = solrDocument.get( CommonConstants.USER_DISPLAY_NAME_SOLR ).toString();
        LOG.info( "Method to fetch user from solr based upon user id, searchUserById() finished." );
        return displayName;
    }


    /**
     * Method to fetch user based on the userid provided
     */
    @Override
    public SolrDocument getUserByUniqueId( long userId ) throws InvalidInputException, SolrException
    {
        LOG.info( "Method getUserByUniqueId called for userId:" + userId );
        if ( userId <= 0l ) {
            throw new InvalidInputException( "userId is invalid for getting user from solr" );
        }
        SolrDocument solrDocument = null;
        QueryResponse response = null;
        SolrServer solrServer = new HttpSolrServer( solrUserUrl );
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery( CommonConstants.USER_ID_SOLR + ":" + userId );

        LOG.debug( "Querying solr for searching users" );
        try {
            response = solrServer.query( solrQuery );
            SolrDocumentList results = response.getResults();

            if ( results != null && !results.isEmpty() ) {
                solrDocument = results.get( CommonConstants.INITIAL_INDEX );
            } else {
                LOG.debug( "No user present in solr for the userId:" + userId );
            }
        } catch ( SolrServerException e ) {
            throw new SolrException( "SolrServerException caught in getUserByUniqueId().", e );
        }
        LOG.info( "Method getUserByUniqueId executed succesfully. Returning :" + solrDocument );
        return solrDocument;

    }


    /**
     * Method to edit User in solr
     * @throws InvalidInputException 
     */
    @Override
    public void editUserInSolr( long userId, String key, String value ) throws SolrException, InvalidInputException
    {
        LOG.info( "Method to edit user in solr called for user : " + userId );

        if ( userId <= 0l ) {
            throw new InvalidInputException( "userId is invalid for edit user in solr" );
        }

        try {
            // Setting values to Map with instruction
            Map<String, String> editKeyValues = new HashMap<String, String>();
            editKeyValues.put( SOLR_EDIT_REPLACE, value );

            // Adding fields to be updated
            SolrInputDocument document = new SolrInputDocument();
            document.setField( CommonConstants.USER_ID_SOLR, userId );
            document.setField( key, editKeyValues );

            SolrServer solrServer = new HttpSolrServer( solrUserUrl );
            solrServer.add( document );
            solrServer.commit();
        } catch ( SolrServerException | IOException e ) {
            LOG.error( "Exception while editing user in solr. Reason : " + e.getMessage(), e );
            throw new SolrException( "Exception while adding regions to solr. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method to edit user in solr finished for user : " + userId );
    }
    

    @Override
    public void editUsersInSolr( List<Long> userIds, String key, String value ) throws SolrException, InvalidInputException
    {
        for ( Long userId : userIds ) {
            editUserInSolr( userId, key, value );
        }
    }


    @Override
    public void editUserInSolrWithMultipleValues( long userId, Map<String, Object> map )
        throws SolrException, InvalidInputException
    {
        LOG.info( "Method to edit user in solr called for user : " + userId );

        if ( userId <= 0l ) {
            throw new InvalidInputException( "userId is invalid for edit user in solr" );
        }
        if ( map == null ) {
            throw new InvalidInputException( "empty map massed for edit user in solr" );
        }

        try {
            // Setting values to Map with instruction
            Map<String, Object> editKeyValues = null;

            // Adding fields to be updated
            SolrInputDocument document = new SolrInputDocument();
            document.setField( CommonConstants.USER_ID_SOLR, userId );
            for ( Entry<String, Object> e : map.entrySet() ) {
                editKeyValues = new HashMap<String, Object>();
                editKeyValues.put( SOLR_EDIT_REPLACE, e.getValue() );
                document.setField( e.getKey(), editKeyValues );
            }
            SolrServer solrServer = new HttpSolrServer( solrUserUrl );
            solrServer.add( document );
            solrServer.commit();
        } catch ( SolrServerException | IOException e ) {
            LOG.error( "Exception while editing user in solr. Reason : " + e.getMessage(), e );
            throw new SolrException( "Exception while adding regions to solr. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method to edit user in solr finished for user : " + userId );
    }


    @Override
    public SolrDocumentList getUserIdsByIden( long iden, String idenFieldName, boolean isAgent, int startIndex, int noOfRows )
        throws InvalidInputException, SolrException
    {
        LOG.info( "Method getUserIdsByIden called for iden :" + iden + "idenFieldName:" + idenFieldName + " startIndex:"
            + startIndex + " noOfrows:" + noOfRows );
        if ( iden <= 0l ) {
            throw new InvalidInputException( "iden is not set in getUserIdsByIden" );
        }
        if ( idenFieldName == null || idenFieldName.isEmpty() ) {
            throw new InvalidInputException( "idenFieldName is null or empty in getUserIdsByIden" );
        }

        QueryResponse response = null;
        SolrDocumentList results = null;
        try {
            SolrServer solrServer = new HttpSolrServer( solrUserUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery( CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE + " OR "
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_NOT_VERIFIED + " OR " + CommonConstants.STATUS_SOLR
                + ":" + CommonConstants.STATUS_TEMPORARILY_INACTIVE );
            solrQuery.addFilterQuery( idenFieldName + ":" + iden );
            if ( isAgent ) {
                solrQuery.addFilterQuery( CommonConstants.IS_AGENT_SOLR + ":" + isAgent );
            }
            if ( startIndex > -1 ) {
                solrQuery.setStart( startIndex );
            }
            if ( noOfRows > -1 ) {
                solrQuery.setRows( noOfRows );
            }

            LOG.debug( "Querying solr for searching users" );
            response = solrServer.query( solrQuery );
            results = response.getResults();
            LOG.debug( "User search result is : " + results );
        } catch ( SolrServerException e ) {
            LOG.error( "SolrServerException in getUserIdsByIden.Reason:" + e.getMessage(), e );
            throw new SolrException( "Exception while performing search for user. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method getUserIdsByIden finished for iden : " + iden );
        return results;
    }


    @Override
    public Collection<UserFromSearch> searchUsersByIden( long iden, String idenFieldName, boolean isAgent, int startIndex,
        int noOfRows ) throws InvalidInputException, SolrException
    {
        LOG.info( "Method searchUsersByIden called for iden :" + iden + "idenFieldName:" + idenFieldName + " startIndex:"
            + startIndex + " noOfrows:" + noOfRows );
        if ( iden <= 0l ) {
            throw new InvalidInputException( "iden is not set in searchUsersByIden" );
        }
        if ( idenFieldName == null || idenFieldName.isEmpty() ) {
            throw new InvalidInputException( "idenFieldName is null or empty in searchUsersByIden" );
        }

        QueryResponse response = null;
        SolrDocumentList results = null;
        try {
            SolrServer solrServer = new HttpSolrServer( solrUserUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery( CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE + " OR "
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_NOT_VERIFIED + " OR " + CommonConstants.STATUS_SOLR
                + ":" + CommonConstants.STATUS_TEMPORARILY_INACTIVE );
            solrQuery.addFilterQuery( idenFieldName + ":" + iden );
            if ( isAgent ) {
                solrQuery.addFilterQuery( CommonConstants.IS_AGENT_SOLR + ":" + isAgent );
            }
            if ( startIndex > -1 ) {
                solrQuery.setStart( startIndex );
            }
            if ( noOfRows > -1 ) {
                solrQuery.setRows( noOfRows );
            }
            solrQuery.addSort( CommonConstants.USER_DISPLAY_NAME_SOLR, ORDER.asc );

            LOG.debug( "Querying solr for searching users" );
            response = solrServer.query( solrQuery );
            results = response.getResults();
            LOG.debug( "User search result is : " + results );
        } catch ( SolrServerException e ) {
            LOG.error( "SolrServerException in searchUsersByIden.Reason:" + e.getMessage(), e );
            throw new SolrException( "Exception while performing search for user. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method searchUsersByIden finished for iden : " + iden );
        return getUsersFromSolrDocuments( results );
    }


    @Override
    public long getUsersCountByIden( long iden, String idenFieldName, boolean isAgent )
        throws InvalidInputException, SolrException
    {
        LOG.info( "Method getUsersCountByIden called for iden :" + iden + "idenFieldName:" + idenFieldName );
        if ( iden <= 0l ) {
            throw new InvalidInputException( "iden is not set in searchUsersByIden" );
        }
        if ( idenFieldName == null || idenFieldName.isEmpty() ) {
            throw new InvalidInputException( "idenFieldName is null or empty in searchUsersByIden" );
        }

        QueryResponse response = null;
        SolrDocumentList results = null;
        try {
            SolrServer solrServer = new HttpSolrServer( solrUserUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery( CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE + " OR "
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_NOT_VERIFIED + " OR " + CommonConstants.STATUS_SOLR
                + ":" + CommonConstants.STATUS_TEMPORARILY_INACTIVE );
            solrQuery.addFilterQuery( idenFieldName + ":" + iden );
            if ( isAgent ) {
                solrQuery.addFilterQuery( CommonConstants.IS_AGENT_SOLR + ":" + isAgent );
            }

            LOG.debug( "Querying solr for searching users" );
            response = solrServer.query( solrQuery );
            results = response.getResults();
            LOG.debug( "User search result is : " + results );
        } catch ( SolrServerException e ) {
            LOG.error( "SolrServerException in getUsersCountByIden().Reason:" + e.getMessage(), e );
            throw new SolrException( "Exception while performing search count for user. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method getUsersCountByIden finished for iden : " + iden );
        return results.getNumFound();
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
    public String searchRegionById( long regionId ) throws InvalidInputException, SolrException
    {
        LOG.info( "Method searchRegionById called for regionId :" + regionId );
        if ( regionId <= 0l ) {
            throw new InvalidInputException( "Region id is null while searching for region" );
        }
        String regionName = null;
        QueryResponse response = null;
        try {
            SolrServer solrServer = new HttpSolrServer( solrRegionUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery( CommonConstants.REGION_ID_SOLR + ":" + regionId );
            solrQuery.addFilterQuery( CommonConstants.STATUS_COLUMN + ":" + CommonConstants.STATUS_ACTIVE );
            response = solrServer.query( solrQuery );
            SolrDocumentList results = response.getResults();
            if ( results.size() != 0 ) {
                regionName = (String) results.get( CommonConstants.INITIAL_INDEX )
                    .getFieldValue( CommonConstants.REGION_NAME_SOLR );
            }
        } catch ( SolrServerException e ) {
            LOG.error( "UnsupportedEncodingException while performing region search" );
            throw new SolrException( "Exception while performing search. Reason : " + e.getMessage(), e );
        }
        LOG.debug( "Region search result is : " + regionName );
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
    public String searchBranchNameById( long branchId ) throws InvalidInputException, SolrException
    {
        LOG.info( "Method searchBrancNameById called for branchId :" + branchId );
        if ( branchId <= 0l ) {
            throw new InvalidInputException( "Branch id is null while searching for Branch" );
        }
        String branchName = null;
        QueryResponse response = null;
        try {
            SolrServer solrServer = new HttpSolrServer( solrBranchUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery( CommonConstants.BRANCH_ID_SOLR + ":" + branchId );
            solrQuery.addFilterQuery( CommonConstants.STATUS_COLUMN + ":" + CommonConstants.STATUS_ACTIVE );
            response = solrServer.query( solrQuery );
            SolrDocumentList results = response.getResults();
            if ( results.size() != 0 )
                branchName = (String) results.get( CommonConstants.INITIAL_INDEX )
                    .getFieldValue( CommonConstants.BRANCH_NAME_SOLR );
        } catch ( SolrServerException e ) {
            LOG.error( "UnsupportedEncodingException while performing branch search" );
            throw new SolrException( "Exception while performing search. Reason : " + e.getMessage(), e );
        }
        LOG.debug( "Branch search result is : " + branchName );
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
    public List<SolrDocument> searchBranchRegionOrAgentByName( String searchColumn, String searchKey, String columnName,
        long id ) throws InvalidInputException, SolrException
    {
        LOG.info(
            "Method searchBranchRegionOrAgentByNameAndCompany() to search regions, branches, agent in a company started" );

        if ( searchColumn == null || searchColumn.isEmpty() ) {
            throw new InvalidInputException( "Invalid input parameter : passed searchColumn is null or invalid" );
        }
        if ( columnName == null || columnName.isEmpty() ) {
            throw new InvalidInputException( "Invalid input parameter : passed columnName is null or invalid" );
        }

        List<SolrDocument> results = new ArrayList<SolrDocument>();
        QueryResponse response = null;
        searchKey = searchKey.trim() + "*";

        SolrQuery query = new SolrQuery();
        try {
            SolrServer solrServer;
            switch ( searchColumn ) {
                case CommonConstants.REGION_NAME_SOLR:
                    solrServer = new HttpSolrServer( solrRegionUrl );
                    break;
                case CommonConstants.BRANCH_NAME_SOLR:
                    solrServer = new HttpSolrServer( solrBranchUrl );
                    break;
                case CommonConstants.USER_DISPLAY_NAME_SOLR:
                    solrServer = new HttpSolrServer( solrUserUrl );
                    if ( columnName.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                        columnName = "regions";
                    } else if ( columnName.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                        columnName = "branches";
                    }
                    query.addFilterQuery( "isAgent" + ":" + true );
                    break;
                default:
                    solrServer = new HttpSolrServer( solrRegionUrl );
            }

            // Check if proper id is passed
            if ( id > -1 ) {
                query.setQuery( columnName + ":" + id );
            } else {
                query.setQuery( "*:*" );
            }

            if ( !searchColumn.equalsIgnoreCase( CommonConstants.USER_DISPLAY_NAME_SOLR ) )
                query.addFilterQuery(
                    CommonConstants.IS_DEFAULT_BY_SYSTEM_SOLR + ":" + CommonConstants.IS_DEFAULT_BY_SYSTEM_NO );

            //query.addFilterQuery( searchColumn + ":" + searchKey );
            fixQuery( query, searchColumn, searchKey );
            query.addFilterQuery( "-" + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_INACTIVE );

            LOG.debug( "Querying solr for searching " + searchColumn );
            response = solrServer.query( query );
            // Change to get all matching records.
            int rows = (int) response.getResults().getNumFound();
            query.setRows( rows );
            response = solrServer.query( query );
            SolrDocumentList documentList = response.getResults();
            for ( SolrDocument doc : documentList ) {
                results.add( doc );
            }

            LOG.debug( "Results obtained from solr :" + results );
        } catch ( SolrServerException e ) {
            LOG.error( "SolrServerException while performing region, branch or agent search" );
            throw new SolrException( "Exception while performing search. Reason : " + e.getMessage(), e );
        }

        LOG.info(
            "Method searchBranchRegionOrAgentByNameAndCompany() to search regions, branches, agent in a company finished" );
        return results;
    }


    /*
     * (non-Javadoc)
     * @see com.realtech.socialsurvey.core.services.search.SolrSearchService#searchBranchRegionOrAgentByNameForAdmin(java.lang.String, java.lang.String, java.lang.String, long)
     */
    @Override
    public List<SolrDocument> searchBranchRegionOrAgentByNameForAdmin( String searchColumn, String searchKey )
        throws InvalidInputException, SolrException
    {
        LOG.info(
            "Method searchBranchRegionOrAgentByNameAndCompany() to search regions, branches, agent in a company started" );

        if ( searchColumn == null || searchColumn.isEmpty() ) {
            throw new InvalidInputException( "Invalid input parameter : passed searchColumn is null or invalid" );
        }

        List<SolrDocument> results = new ArrayList<SolrDocument>();
        QueryResponse response = null;

        SolrQuery query = new SolrQuery();
        try {
            SolrServer solrServer;
            switch ( searchColumn ) {
                case CommonConstants.REGION_NAME_SOLR:
                    solrServer = new HttpSolrServer( solrRegionUrl );
                    break;
                case CommonConstants.BRANCH_NAME_SOLR:
                    solrServer = new HttpSolrServer( solrBranchUrl );
                    break;
                case CommonConstants.USER_DISPLAY_NAME_SOLR:
                    solrServer = new HttpSolrServer( solrUserUrl );
                    break;
                default:
                    solrServer = new HttpSolrServer( solrRegionUrl );
            }

            query.setQuery( "*:*" );

            //search for login name also in case of user
            if ( !searchColumn.equalsIgnoreCase( CommonConstants.USER_DISPLAY_NAME_SOLR ) ) {
                query.addFilterQuery(
                    CommonConstants.IS_DEFAULT_BY_SYSTEM_SOLR + ":" + CommonConstants.IS_DEFAULT_BY_SYSTEM_NO );
                /*query.addFilterQuery( searchColumn + ":" + searchKey );*/
                String serchKeyQuery = "";
                if ( searchKey != null && !searchKey.isEmpty() ) {
                    serchKeyQuery = generateSubQueryToSearch( searchColumn, searchKey );
                }
                query.addFilterQuery( serchKeyQuery );
            } else {
                String serchKeyQuery = "";
                if ( searchKey != null && !searchKey.isEmpty() ) {
                    serchKeyQuery = serchKeyQuery
                        + generateSubQueryToSearch( CommonConstants.USER_DISPLAY_NAME_SOLR, searchKey );
                    serchKeyQuery = serchKeyQuery + " OR "
                        + generateSubQueryToSearch( CommonConstants.USER_LOGIN_NAME_SOLR, searchKey );
                }
                query.addFilterQuery( serchKeyQuery );
                query.addSort( CommonConstants.USER_DISPLAY_NAME_SOLR, ORDER.asc );

            }

            //filter for only active user
            query.addFilterQuery( CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE + " OR "
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_NOT_VERIFIED + " OR " + CommonConstants.STATUS_SOLR
                + ":" + CommonConstants.STATUS_TEMPORARILY_INACTIVE );


            LOG.debug( "Querying solr for searching " + searchKey );
            response = solrServer.query( query );
            // Change to get all matching records.
            int rows = (int) response.getResults().getNumFound();
            query.setRows( rows );
            response = solrServer.query( query );
            SolrDocumentList documentList = response.getResults();
            for ( SolrDocument doc : documentList ) {
                results.add( doc );
            }

            LOG.debug( "Results obtained from solr :" + results );
        } catch ( SolrServerException e ) {
            LOG.error( "SolrServerException while performing region, branch or agent search" );
            throw new SolrException( "Exception while performing search. Reason : " + e.getMessage(), e );
        }

        LOG.info(
            "Method searchBranchRegionOrAgentByNameAndCompany() to search regions, branches, agent in a company finished" );
        return results;
    }


    @Override
    public String fetchRegionsByCompany( long companyId, int size )
        throws InvalidInputException, SolrException, MalformedURLException
    {
        if ( companyId <= 0l ) {
            throw new InvalidInputException( "company id is null or empty while searching for Regions" );
        }
        LOG.info( "Method fetchRegionsByCompany() called for company id : " + companyId );
        String regionsResult = null;
        QueryResponse response = null;
        try {
            SolrServer solrServer = new HttpSolrServer( solrRegionUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery( CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE );
            solrQuery.addFilterQuery( CommonConstants.COMPANY_ID_SOLR + ":" + companyId );

            if ( size > -1 ) {
                solrQuery.setRows( size );
            }

            LOG.debug( "Querying solr for searching regions" );
            response = solrServer.query( solrQuery );
            SolrDocumentList results = response.getResults();
            Collection<RegionFromSearch> regions = getRegionsFromSolrDocuments( results );
            regionsResult = new Gson().toJson( regions );
        } catch ( SolrServerException e ) {
            LOG.error( "SolrServerException while performing Regions search" );
            throw new SolrException( "Exception while performing search for Regions. Reason : " + e.getMessage(), e );
        }

        LOG.info( "Method fetchRegionsByCompany() finished for company id : " + companyId );
        return regionsResult;
    }


    /**
     * Method to fetch social posts from solr given the entity
     */
    @Override
    public SolrDocumentList fetchSocialPostsByEntity( String entityType, long entityId, int startIndex, int noOfRows )
        throws InvalidInputException, SolrException, MalformedURLException
    {
        if ( entityId <= 0l ) {
            throw new InvalidInputException( "Pattern is null or empty while fetching social posts" );
        }
        if ( entityType == null || entityType.isEmpty() ) {
            throw new InvalidInputException( "Invalid input parameter : passed entityType is null or invalid" );
        }

        LOG.info( "Method fetchSocialPostsByEntity() called for entity id : " + entityId + " and entity type : " + entityType );
        SolrDocumentList results = null;
        try {
            SolrServer solrServer = new HttpSolrServer( solrSocialPostUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery( entityType + ":" + entityId );
            solrQuery.setStart( startIndex );
            solrQuery.setRows( noOfRows );
            solrQuery.addSort( CommonConstants.TIME_IN_MILLIS_SOLR, ORDER.desc );

            LOG.debug( "Querying solr for searching social posts" );
            results = solrServer.query( solrQuery ).getResults();
        } catch ( SolrServerException e ) {
            LOG.error( "SolrServerException while fetching social posts" );
            throw new SolrException( "Exception while fetching social posts. Reason : " + e.getMessage(), e );
        }

        LOG.info(
            "Method fetchSocialPostsByEntity() finished for entity id : " + entityId + " and entity type : " + entityType );
        return results;
    }


    /**
     * Method to search social posts based on the post text
     */
    @Override
    public SolrDocumentList searchPostText( String entityType, long entityId, int startIndex, int noOfRows, String searchQuery )
        throws InvalidInputException, SolrException, MalformedURLException
    {
        if ( entityId <= 0l ) {
            LOG.warn( "Pattern is null or empty while fetching social posts" );
            throw new InvalidInputException( "Pattern is null or empty while fetching social posts" );
        }
        if ( entityType == null || entityType.isEmpty() ) {
            LOG.warn( "Invalid input parameter : passed entityType is null or invalid" );
            throw new InvalidInputException( "Invalid input parameter : passed entityType is null or invalid" );
        }
        if ( searchQuery == null ) {
            LOG.warn( "Invalid input parameter : passed searchQuery is null" );
            throw new InvalidInputException( "Invalid input parameter : passed searchQuery is null" );
        }

        LOG.info( "Method searchPostText() called for entity id : {} and entity type : {}", entityId, entityType );
        SolrDocumentList results = null;
        try {
            SolrServer solrServer = new HttpSolrServer( solrSocialPostUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery( entityType + ":" + entityId + " AND " + CommonConstants.POST_TEXT_SOLR + ":" + "*" + searchQuery
                + "*" + " AND " + CommonConstants.POST_TEXT_SOLR + ":[\"\" TO *]" );
            solrQuery.setStart( startIndex );
            solrQuery.setRows( noOfRows );
            solrQuery.addSort( CommonConstants.TIME_IN_MILLIS_SOLR, ORDER.desc );
            if(LOG.isDebugEnabled())
                LOG.debug( "Solr Search Query : {}", solrQuery.getQuery() );
            LOG.debug( "Querying solr for searching social posts" );
            results = solrServer.query( solrQuery ).getResults();
            if(LOG.isDebugEnabled())
                LOG.debug( "Number of matches found : {}", results.getNumFound() );
        } catch ( SolrServerException e ) {
            LOG.warn( "SolrServerException while fetching social posts" );
            throw new SolrException( "Exception while fetching social posts. Reason : " , e );
        }

        LOG.info( "Method searchPostText() finished for entity id : {} and entity type : {}" ,entityId, entityType );
        return results;
    }


    @Override
    public String fetchBranchesByCompany( long companyId, int size )
        throws InvalidInputException, SolrException, MalformedURLException
    {
        if ( companyId <= 0l ) {
            throw new InvalidInputException( "companyId is null or empty while searching for Branches" );
        }
        LOG.info( "Method fetchBranchesByCompany() called for company id : " + companyId );
        String branchesResult = null;
        QueryResponse response = null;
        try {
            SolrServer solrServer = new HttpSolrServer( solrBranchUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery( CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE );
            solrQuery.addFilterQuery( CommonConstants.COMPANY_ID_SOLR + ":" + companyId );
            if ( size > 0 )
                solrQuery.setRows( size );

            LOG.debug( "Querying solr for searching branches" );
            response = solrServer.query( solrQuery );
            SolrDocumentList results = response.getResults();
            branchesResult = new Gson().toJson( getBranchesFromSolrDocuments( results ) );
        } catch ( SolrServerException e ) {
            LOG.error( "SolrServerException while performing Branches search" );
            throw new SolrException( "Exception while performing search for Branches. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method fetchBranchesByCompany() finished for company id : " + companyId );
        return branchesResult;
    }


    @Override
    public Long fetchBranchCountByCompany( long companyId ) throws InvalidInputException, SolrException, MalformedURLException
    {
        if ( companyId <= 0l ) {
            throw new InvalidInputException( "companyId is invalid while searching for Branches" );
        }
        LOG.info( "Method fetchBranchCountByCompany() called for company id : " + companyId );
        QueryResponse response = null;
        long resultsCount = 0;
        try {
            SolrServer solrServer = new HttpSolrServer( solrBranchUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery( CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE );
            solrQuery.addFilterQuery( CommonConstants.COMPANY_ID_SOLR + ":" + companyId );

            LOG.debug( "Querying solr for searching branches" );
            response = solrServer.query( solrQuery );
            resultsCount = response.getResults().getNumFound();
        } catch ( SolrServerException e ) {
            LOG.error( "SolrServerException while performing Branches search" );
            throw new SolrException( "Exception while performing search for Branches count. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method fetchBranchCountByCompany() finished for company id : " + companyId );
        return resultsCount;
    }


    @Override
    public Long fetchRegionCountByCompany( long companyId ) throws InvalidInputException, SolrException, MalformedURLException
    {
        if ( companyId <= 0l ) {
            throw new InvalidInputException( "companyId is null or empty while searching for Regions" );
        }
        LOG.info( "Method fetchRegionCountByCompany() called for company id : " + companyId );
        QueryResponse response = null;
        long resultsCount = 0;
        try {
            SolrServer solrServer = new HttpSolrServer( solrRegionUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery( CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE );
            solrQuery.addFilterQuery( CommonConstants.COMPANY_ID_SOLR + ":" + companyId );

            LOG.debug( "Querying solr for searching Regions" );
            response = solrServer.query( solrQuery );
            resultsCount = response.getResults().getNumFound();
        } catch ( SolrServerException e ) {
            LOG.error( "SolrServerException while performing Regions search" );
            throw new SolrException( "Exception while performing search for Regions count. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method fetchRegionCountByCompany() finished for company id : " + companyId );
        return resultsCount;
    }


    /**
     * Method to get space separated ids from set of ids
     * 
     * @param ids
     * @return
     */
    private String getSpaceSeparatedStringFromIds( Set<Long> ids )
    {
        LOG.debug( "Method getSpaceSeparatedStringFromIds called for ids:" + ids );
        StringBuilder idsSb = new StringBuilder();
        int count = 0;
        if ( ids != null && !ids.isEmpty() ) {
            for ( Long id : ids ) {
                if ( count != 0 ) {
                    idsSb.append( " " );
                }
                idsSb.append( id );
                count++;
            }
        }
        LOG.debug( "Method getSpaceSeparatedStringFromIds executed successfully. Returning:" + idsSb.toString() );
        return idsSb.toString();
    }


    /**
     * Method to search for the users based on branches specified
     */
    @Override
    public String searchUsersByBranches( Set<Long> branchIds, int start, int rows ) throws InvalidInputException, SolrException
    {
        if ( branchIds == null || branchIds.isEmpty() ) {
            throw new InvalidInputException( "branchIds are null in searchUsersByBranches" );
        }
        LOG.info( "Method searchUsersByBranches called for branchIds:" + branchIds + " start:" + start + " rows:" + rows );
        String usersResult = null;
        QueryResponse response = null;
        try {
            SolrServer solrServer = new HttpSolrServer( solrUserUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery( CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE + " OR "
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_NOT_VERIFIED + " OR " + CommonConstants.STATUS_SOLR
                + ":" + CommonConstants.STATUS_TEMPORARILY_INACTIVE );
            String branchIdsStr = getSpaceSeparatedStringFromIds( branchIds );
            solrQuery.addFilterQuery( CommonConstants.BRANCHES_SOLR + ":(" + branchIdsStr + ")" );

            solrQuery.setStart( start );
            if ( rows > 0 ) {
                solrQuery.setRows( rows );
            }
            solrQuery.addSort( CommonConstants.USER_DISPLAY_NAME_SOLR, ORDER.asc );

            LOG.debug( "Querying solr for searching users under the branches" );
            response = solrServer.query( solrQuery );
            SolrDocumentList results = response.getResults();
            usersResult = new Gson().toJson( getUsersFromSolrDocuments( results ) );
            LOG.debug( "Users search result is : " + usersResult );
        } catch ( SolrServerException e ) {
            throw new SolrException( "Exception while performing search for users by branches. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method searchUsersByBranches executed successfully" );
        return usersResult;
    }


    /**
     * Method to search for the users based on branches specified
     */
    @Override
    public long getUsersCountByBranches( Set<Long> branchIds ) throws InvalidInputException, SolrException
    {
        if ( branchIds == null || branchIds.isEmpty() ) {
            throw new InvalidInputException( "branchIds are null in getUsersCountByBranches" );
        }
        LOG.info( "Method getUsersCountByBranches called for branchIds:" + branchIds );
        String usersResult = null;
        QueryResponse response = null;
        try {
            SolrServer solrServer = new HttpSolrServer( solrUserUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery( CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE + " OR "
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_NOT_VERIFIED + " OR " + CommonConstants.STATUS_SOLR
                + ":" + CommonConstants.STATUS_TEMPORARILY_INACTIVE );
            String branchIdsStr = getSpaceSeparatedStringFromIds( branchIds );
            solrQuery.addFilterQuery( CommonConstants.BRANCHES_SOLR + ":(" + branchIdsStr + ")" );

            LOG.debug( "Querying solr for counting users under the branches" );
            response = solrServer.query( solrQuery );
            LOG.debug( "Users search result is : " + usersResult );
        } catch ( SolrServerException e ) {
            throw new SolrException( "Exception while performing search for users by branches. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method getUsersCountByBranches executed successfully" );
        return response.getResults().getNumFound();
    }


    @Override
    public void addRegionsToSolr( List<Region> regions ) throws SolrException, InvalidInputException
    {
        LOG.info( "Method to add regions to solr called" );

        if ( regions == null || regions.size() <= 0 ) {
            throw new InvalidInputException( "Invalid parameter passed : passed region list is null or empty" );
        }

        SolrServer solrServer;

        try {
            solrServer = new HttpSolrServer( solrRegionUrl );

            List<SolrInputDocument> documents = new ArrayList<SolrInputDocument>();
            SolrInputDocument document;
            for ( Region region : regions ) {
                document = getSolrDocumentFromRegion( region );

                // fetch RegionSettings from mongo
                OrganizationUnitSettings regionSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(
                    region.getRegionId(), MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );

                // update address
                if ( regionSettings.getContact_details() != null
                    && regionSettings.getContact_details().getAddress1() != null ) {
                    document.addField( CommonConstants.ADDRESS1_SOLR, regionSettings.getContact_details().getAddress1() );
                }
                if ( regionSettings.getContact_details() != null
                    && regionSettings.getContact_details().getAddress2() != null ) {
                    document.addField( CommonConstants.ADDRESS2_SOLR, regionSettings.getContact_details().getAddress2() );
                }

                documents.add( document );
            }

            UpdateResponse response = solrServer.add( documents );
            solrServer.commit();
            LOG.debug( "response while adding regions is : " + response );
        } catch ( MalformedURLException e ) {
            LOG.error( "Exception while adding regions to solr. Reason : " + e.getMessage(), e );
            throw new SolrException( "Exception while adding regions to solr. Reason : " + e.getMessage(), e );
        } catch ( SolrServerException | IOException e ) {
            LOG.error( "Exception while adding regions to solr. Reason : " + e.getMessage(), e );
            throw new SolrException( "Exception while adding regions to solr. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method to add regions to solr finshed" );
    }


    @Override
    public void addBranchesToSolr( List<Branch> branches ) throws SolrException, InvalidInputException
    {
        LOG.info( "Method to add branches to solr called" );

        if ( branches == null || branches.size() <= 0 ) {
            throw new InvalidInputException( "Invalid parameter passed : passed branche list is null or empty" );
        }

        SolrServer solrServer;

        try {
            solrServer = new HttpSolrServer( solrBranchUrl );

            List<SolrInputDocument> documents = new ArrayList<SolrInputDocument>();
            SolrInputDocument document;
            for ( Branch branch : branches ) {
                document = getSolrDocumentFromBranch( branch );

                // fetch BranchSettings from mongo
                OrganizationUnitSettings branchSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(
                    branch.getBranchId(), MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );

                // update address
                if ( branchSettings.getContact_details() != null
                    && branchSettings.getContact_details().getAddress1() != null ) {
                    document.addField( CommonConstants.ADDRESS1_SOLR, branchSettings.getContact_details().getAddress1() );
                }
                if ( branchSettings.getContact_details() != null
                    && branchSettings.getContact_details().getAddress2() != null ) {
                    document.addField( CommonConstants.ADDRESS2_SOLR, branchSettings.getContact_details().getAddress2() );
                }

                documents.add( document );
            }

            UpdateResponse response = solrServer.add( documents );
            solrServer.commit();
            LOG.debug( "response while adding branches is : " + response );
        } catch ( MalformedURLException e ) {
            LOG.error( "Exception while adding branches to solr. Reason : " + e.getMessage(), e );
            throw new SolrException( "Exception while adding branches to solr. Reason : " + e.getMessage(), e );
        } catch ( SolrServerException | IOException e ) {
            LOG.error( "Exception while adding branches to solr. Reason : " + e.getMessage(), e );
            throw new SolrException( "Exception while adding branches to solr. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method to add branches to solr finshed" );
    }


    /**
     * Method to index a list of social posts in Solr
     * @throws InvalidInputException 
     */
    @Override
    public void addSocialPostsToSolr( List<SocialPost> socialPosts ) throws SolrException, InvalidInputException
    {
        LOG.info( "Method to add social posts to solr called" );

        if ( socialPosts == null || socialPosts.size() <= 0 ) {
            throw new InvalidInputException( "Invalid parameter passed : passed socialPost list is null or empty" );
        }

        SolrServer solrServer;

        try {
            solrServer = new HttpSolrServer( solrSocialPostUrl );

            List<SolrInputDocument> documents = new ArrayList<SolrInputDocument>();
            SolrInputDocument document;

            for ( SocialPost post : socialPosts ) {
                document = getSolrDocumentFromSocialPost( post );
                documents.add( document );
            }
            UpdateResponse response = solrServer.add( documents );
            solrServer.commit();
            LOG.debug( "response while adding social posts is : " + response );
        } catch ( MalformedURLException e ) {
            LOG.error( "Exception while adding social posts to solr. Reason : " + e.getMessage(), e );
            throw new SolrException( "Exception while adding social posts to solr. Reason : " + e.getMessage(), e );
        } catch ( SolrServerException | IOException e ) {
            LOG.error( "Exception while adding social posts to solr. Reason : " + e.getMessage(), e );
            throw new SolrException( "Exception while adding social posts to solr. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method to add social posts to solr finshed" );
    }


    @Override
    public void addUsersToSolr( List<User> users ) throws SolrException, InvalidInputException
    {
        LOG.info( "Method to add users to solr called" );

        if ( users == null || users.size() <= 0 ) {
            throw new InvalidInputException( "Invalid parameter passed : passed user list is null or empty" );
        }

        SolrServer solrServer;

        try {
            solrServer = new HttpSolrServer( solrUserUrl );

            List<SolrInputDocument> documents = new ArrayList<SolrInputDocument>();
            SolrInputDocument document;
            for ( User user : users ) {
                // update profiles of user
                userManagementService.setProfilesOfUser( user );

                document = new SolrInputDocument();
                document = getSolrInputDocumentFromUser( user, document );

                // fetch AgentSettings from mongo
                AgentSettings agentSettings = organizationUnitSettingsDao.fetchAgentSettingsById( user.getUserId() );

                // update profileUrl
                if ( agentSettings.getContact_details() != null && agentSettings.getContact_details().getAbout_me() != null ) {
                    document.addField( CommonConstants.ABOUT_ME_SOLR, agentSettings.getContact_details().getAbout_me() );
                }
                // update profileName
                if ( agentSettings.getProfileName() != null ) {
                    document.addField( CommonConstants.PROFILE_NAME_SOLR, agentSettings.getProfileName() );
                }
                // update profileUrl
                if ( agentSettings.getProfileUrl() != null ) {
                    document.addField( CommonConstants.PROFILE_URL_SOLR, agentSettings.getProfileUrl() );
                }
                // update profileImageUrl
                if ( agentSettings.getProfileImageUrl() != null ) {
                    document.addField( CommonConstants.PROFILE_IMAGE_URL_SOLR, agentSettings.getProfileImageUrl() );
                    document.addField( CommonConstants.IS_PROFILE_IMAGE_SET_SOLR, true );
                }
                if ( agentSettings.getProfileImageUrlThumbnail() != null ) {
                    document.addField( CommonConstants.PROFILE_IMAGE_THUMBNAIL_COLUMN,
                        agentSettings.getProfileImageUrlThumbnail() );
                }

                documents.add( document );
            }

            UpdateResponse response = solrServer.add( documents );
            solrServer.commit();
            LOG.debug( "response while adding users is: " + response );
        } catch ( MalformedURLException e ) {
            LOG.error( "Exception while adding users to solr. Reason : " + e.getMessage(), e );
            throw new SolrException( "Exception while adding users to solr. Reason : " + e.getMessage(), e );
        } catch ( SolrServerException | IOException e ) {
            LOG.error( "Exception while adding users to solr. Reason : " + e.getMessage(), e );
            throw new SolrException( "Exception while adding users to solr. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method to add users to solr finshed" );
    }


    @Override
    public void updateCompletedSurveyCountForUserInSolr( long agentId, int incrementCount )
        throws SolrException, NoRecordsFetchedException, InvalidInputException
    {
        LOG.info( "Method to increase completed survey count updateCompletedSurveyCountForUserInSolr() finished." );

        if ( agentId <= 0l ) {
            throw new InvalidInputException( "Invalid parameter passed : passed agentId is invalid" );
        }

        SolrServer solrServer;
        solrServer = new HttpSolrServer( solrUserUrl );
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery( CommonConstants.USER_ID_SOLR + ":" + agentId );
        LOG.debug( "Querying solr for searching user" );
        QueryResponse response;
        try {
            response = solrServer.query( solrQuery );
            if ( response.getResults() == null || response.getResults().size() <= 0 ) {
                throw new NoRecordsFetchedException( "No user found in solr with agentId : " + agentId );
            }
            SolrDocumentList results = response.getResults();
            SolrDocument document = results.get( CommonConstants.INITIAL_INDEX );
            Long value = Long.parseLong( document.getFieldValue( CommonConstants.REVIEW_COUNT_SOLR ).toString() )
                + incrementCount;

            Map<String, Long> editKeyValues = new HashMap<String, Long>();
            editKeyValues.put( SOLR_EDIT_REPLACE, value );

            // Adding fields to be updated
            SolrInputDocument inputDoc = new SolrInputDocument();
            inputDoc.setField( CommonConstants.USER_ID_SOLR, agentId );
            inputDoc.setField( CommonConstants.REVIEW_COUNT_SOLR, editKeyValues );

            solrServer.add( inputDoc );
            solrServer.commit();
        } catch ( SolrServerException | IOException e ) {
            LOG.error( "Exception while editing user in solr. Reason : " + e.getMessage(), e );
            throw new SolrException( "Exception while adding regions to solr. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method to increase completed survey count updateCompletedSurveyCountForUserInSolr() finished." );
    }


    @Override
    public void updateCompletedSurveyCountForMultipleUserInSolr( Map<Long, Integer> usersReviewCount )
        throws SolrException, InvalidInputException
    {
        LOG.info( "Method to increase completed survey count updateCompletedSurveyCountForUserInSolr() finished." );

        if ( usersReviewCount == null || usersReviewCount.isEmpty() ) {
            throw new InvalidInputException( "Invalid parameter passed : passed usersReviewCount map is null or empty" );
        }

        SolrServer solrServer;
        solrServer = new HttpSolrServer( solrUserUrl );

        List<SolrInputDocument> inputDocList = new ArrayList<SolrInputDocument>();
        Map<String, Integer> editKeyValues;
        SolrInputDocument inputDoc;

        for ( Map.Entry<Long, Integer> entry : usersReviewCount.entrySet() ) {
            editKeyValues = new HashMap<String, Integer>();
            editKeyValues.put( SOLR_EDIT_REPLACE, entry.getValue() );
            // Adding fields to be updated
            inputDoc = new SolrInputDocument();
            inputDoc.setField( CommonConstants.USER_ID_SOLR, entry.getKey() );
            inputDoc.setField( CommonConstants.REVIEW_COUNT_SOLR, editKeyValues );
            inputDocList.add( inputDoc );
        }

        try {
            if ( inputDocList.size() > 0 ) {
                solrServer.add( inputDocList );
                solrServer.commit();
            }
        } catch ( SolrServerException | IOException e ) {
            LOG.error( "Exception while editing user in solr. Reason : " + e.getMessage(), e );
            throw new SolrException( "Exception while adding regions to solr. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method to increase completed survey count updateCompletedSurveyCountForUserInSolr() finished." );
    }


    @Override
    public Map<String, String> getCompanyAdmin( long companyId ) throws SolrException, InvalidInputException
    {
        LOG.info( "Method getEmailIdOfCompanyAdmin() started" );

        if ( companyId <= 0l ) {
            throw new InvalidInputException( "Invalid parameter passed : passed companyId is invalid" );
        }

        try {
            SolrServer solrServer;
            solrServer = new HttpSolrServer( solrUserUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery( CommonConstants.COMPANY_ID_SOLR + ":" + companyId );
            solrQuery.addFilterQuery( "isOwner" + ":" + "1" );
            QueryResponse response = solrServer.query( solrQuery );
            SolrDocumentList results = response.getResults();
            LOG.info( "Method getEmailIdOfCompanyAdmin() finished" );
            if ( results != null && !results.isEmpty() ) {
                SolrDocument solrDocument = results.get( CommonConstants.INITIAL_INDEX );
                if ( solrDocument != null ) {
                    Map<String, String> companyAdmin = new HashMap<>();
                    companyAdmin.put( "displayName", (String) solrDocument.get( "displayName" ) );
                    companyAdmin.put( "loginName", (String) solrDocument.get( "loginName" ) );
                    companyAdmin.put( "emailId", (String) solrDocument.get( "emailId" ) );
                    return companyAdmin;
                }
            }
        } catch ( SolrServerException e ) {
            LOG.error( "SolrServerException caught in getEmailIdOfCompanyAdmin(). Nested exception is ", e );
            throw new SolrException( "SolrServerException caught in getEmailIdOfCompanyAdmin(). Nested exception is", e );
        }
        return null;
    }


    /**
     * Method to perform search of User Ids from solr based on the company.
     * 
     * @throws InvalidInputException
     * @throws SolrException
     * @throws MalformedURLException
     */
    @Override
    public List<Long> searchUserIdsByCompany( long companyId ) throws InvalidInputException, SolrException
    {
        if ( companyId <= 0l ) {
            throw new InvalidInputException( "Pattern is null or empty while searching for Users" );
        }
        LOG.info( "Method searchUsersByCompanyId() called for company id : " + companyId );
        String usersResult = null;
        QueryResponse response = null;
        List<Long> userIds = new ArrayList<>();
        try {
            SolrServer solrServer = new HttpSolrServer( solrUserUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setRows( 1000000000 );
            solrQuery.setQuery( CommonConstants.COMPANY_ID_SOLR + ":" + companyId );
            LOG.debug( "Querying solr for searching users" );
            response = solrServer.query( solrQuery );
            SolrDocumentList results = response.getResults();
            if ( results != null ) {
                for ( SolrDocument user : results ) {
                    userIds.add( (Long) user.get( CommonConstants.USER_ID_SOLR ) );
                }
            }
            LOG.debug( "User search result is : " + usersResult );
        } catch ( SolrServerException e ) {
            LOG.error( "SolrServerException while performing User search" );
            throw new SolrException( "Exception while performing search for user. Reason : " + e.getMessage(), e );
        }

        LOG.info( "Method searchUsersByCompanyId() finished for company id : " + companyId );
        return userIds;
    }


    /*
     * Method to remove all the users from Solr based upon the list of ids provided.
     */
    @Override
    public void removeUsersFromSolr( List<Long> agentIds ) throws SolrException, InvalidInputException
    {

        if ( agentIds == null || agentIds.isEmpty() ) {
            throw new InvalidInputException( "Invalid parameter passed : passed agentIds list is null or empty" );
        }

        SolrServer solrServer = new HttpSolrServer( solrUserUrl );
        if ( agentIds != null && !agentIds.isEmpty() ) {
            String agentIdsStr = getSpaceSeparatedStringFromIds( new HashSet<>( agentIds ) );
            String solrQuery = CommonConstants.USER_ID_SOLR + ":(" + agentIdsStr + ")";
            try {
                solrServer.deleteByQuery( solrQuery );
                solrServer.commit();
            } catch ( SolrServerException | IOException e ) {
                LOG.error( "SolrServerException while deleting Users" );
                throw new SolrException( "Exception while removing multiple users. Reason : " + e.getMessage(), e );
            }
        }
    }


    /**
     * Method to perform search of Branch Ids from solr based on the company.
     * 
     * @throws InvalidInputException
     * @throws SolrException
     * @throws MalformedURLException
     */
    @Override
    public List<Long> searchBranchIdsByCompany( long companyId ) throws InvalidInputException, SolrException
    {
        if ( companyId <= 0l ) {
            throw new InvalidInputException( "Company ID is null while searching for branches." );
        }
        LOG.info( "Method searchBranchIdsByCompany() called for company id : " + companyId );
        QueryResponse response = null;
        List<Long> branchIds = new ArrayList<>();
        try {
            SolrServer solrServer = new HttpSolrServer( solrBranchUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery( CommonConstants.COMPANY_ID_SOLR + ":" + companyId );
            solrQuery.setRows( 1000000000 );
            LOG.debug( "Querying solr for searching branches" );
            response = solrServer.query( solrQuery );
            SolrDocumentList results = response.getResults();
            if ( results != null ) {
                for ( SolrDocument user : results ) {
                    branchIds.add( (Long) user.get( CommonConstants.BRANCH_ID_SOLR ) );
                }
            }
            LOG.debug( "Branches search result is : " + branchIds );
        } catch ( SolrServerException e ) {
            LOG.error( "SolrServerException while performing branch search" );
            throw new SolrException( "Exception while performing search for branches. Reason : " + e.getMessage(), e );
        }

        LOG.info( "Method searchBranchIdsByCompany() finished for company id : " + companyId );
        return branchIds;
    }


    @Override
    public void removeBranchesFromSolr( List<Long> branchIds ) throws SolrException, InvalidInputException
    {

        if ( branchIds == null || branchIds.isEmpty() ) {
            throw new InvalidInputException( "Invalid parameter passed : passed branchId list is null or empty" );
        }

        SolrServer solrServer = new HttpSolrServer( solrBranchUrl );
        if ( branchIds != null && !branchIds.isEmpty() ) {
            String branchIdsStr = getSpaceSeparatedStringFromIds( new HashSet<>( branchIds ) );
            String solrQuery = CommonConstants.BRANCH_ID_SOLR + ":(" + branchIdsStr + ")";
            try {
                solrServer.deleteByQuery( solrQuery );
                solrServer.commit();
            } catch ( SolrServerException | IOException e ) {
                LOG.error( "SolrServerException while deleting Branches" );
                throw new SolrException( "Exception while removing multiple branches. Reason : " + e.getMessage(), e );
            }
        }
    }


    /**
     * Method to perform search of Region Ids from solr based on the company.
     * 
     * @throws InvalidInputException
     * @throws SolrException
     * @throws MalformedURLException
     */
    @Override
    public List<Long> searchRegionIdsByCompany( long companyId ) throws InvalidInputException, SolrException
    {
        if ( companyId <= 0l ) {
            throw new InvalidInputException( "Company ID is null while searching for regions." );
        }
        LOG.info( "Method searchBranchIdsByCompany() called for company id : " + companyId );
        QueryResponse response = null;
        List<Long> regionIds = new ArrayList<>();
        try {
            SolrServer solrServer = new HttpSolrServer( solrRegionUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setRows( 1000000000 );
            solrQuery.setQuery( CommonConstants.COMPANY_ID_SOLR + ":" + companyId );
            LOG.debug( "Querying solr for searching regions" );
            response = solrServer.query( solrQuery );
            SolrDocumentList results = response.getResults();
            if ( results != null ) {
                for ( SolrDocument user : results ) {
                    regionIds.add( (Long) user.get( CommonConstants.REGION_ID_SOLR ) );
                }
            }
            LOG.debug( "Regions search result is : " + regionIds );
        } catch ( SolrServerException e ) {
            LOG.error( "SolrServerException while performing region search" );
            throw new SolrException( "Exception while performing search for regions. Reason : " + e.getMessage(), e );
        }

        LOG.info( "Method searchRegionIdsByCompany() finished for company id : " + companyId );
        return regionIds;
    }


    @Override
    public void removeRegionsFromSolr( List<Long> regionIds ) throws SolrException, InvalidInputException
    {

        if ( regionIds == null || regionIds.isEmpty() ) {
            throw new InvalidInputException( "Invalid parameter passed : passed regionId list is null or empty" );
        }

        SolrServer solrServer = new HttpSolrServer( solrRegionUrl );
        if ( regionIds != null && !regionIds.isEmpty() ) {
            String regionIdsStr = getSpaceSeparatedStringFromIds( new HashSet<>( regionIds ) );
            String solrQuery = CommonConstants.REGION_ID_SOLR + ":(" + regionIdsStr + ")";
            try {
                solrServer.deleteByQuery( solrQuery );
                solrServer.commit();
            } catch ( SolrServerException | IOException e ) {
                LOG.error( "SolrServerException while deleting Regions" );
                throw new SolrException( "Exception while removing multiple regions. Reason : " + e.getMessage(), e );
            }
        }
    }


    @Override
    public void removeSocialPostsFromSolr( String entityType, long entityId, String source ) throws SolrException
    {
        LOG.info( "Method to remove social posts from Solr started for entityType : " + entityType + " entityId : " + entityId
            + " and source : " + source );
        if ( entityType.equalsIgnoreCase( CommonConstants.AGENT_ID_COLUMN ) )
            entityType = CommonConstants.USER_ID_SOLR;
        SolrServer solrServer = new HttpSolrServer( solrSocialPostUrl );
        String solrQuery = entityType + ":" + entityId;
        solrQuery += " AND " + CommonConstants.SOURCE_SOLR + ":" + source;
        switch ( entityType ) {
            case CommonConstants.COMPANY_ID_COLUMN:
                solrQuery += " AND " + CommonConstants.REGION_ID_COLUMN + ":\"-1\"";
            case CommonConstants.REGION_ID_COLUMN:
                solrQuery += " AND " + CommonConstants.BRANCH_ID_COLUMN + ":\"-1\"";
            case CommonConstants.BRANCH_ID_COLUMN:
                solrQuery += " AND " + CommonConstants.USER_ID_SOLR + ":\"-1\"";
                break;
        }
        try {
            solrServer.deleteByQuery( solrQuery );
            solrServer.commit();
        } catch ( SolrServerException | IOException e ) {
            LOG.error( "SolrServerException while deleting social posts" );
            throw new SolrException( "Exception while removing social posts. Reason : " + e.getMessage(), e );
        }

        LOG.info(
            "Method to remove social posts from Solr finished for entityType : " + entityType + " entityId : " + entityId );
    }


    private Collection<BranchFromSearch> getBranchesFromSolrDocuments( SolrDocumentList documentList )
    {
        Map<Long, BranchFromSearch> matchedBranches = new LinkedHashMap<>();
        for ( SolrDocument document : documentList ) {
            BranchFromSearch branch = new BranchFromSearch();

            branch.setCompanyId( Long.parseLong( document.get( CommonConstants.COMPANY_ID_SOLR ).toString() ) );
            branch.setBranchName( document.get( CommonConstants.BRANCH_NAME_SOLR ).toString() );
            branch.setRegionId( Long.parseLong( document.get( CommonConstants.REGION_ID_SOLR ).toString() ) );
            branch.setRegionName( document.get( CommonConstants.REGION_NAME_SOLR ).toString() );
            branch.setStatus( Integer.parseInt( document.get( CommonConstants.STATUS_SOLR ).toString() ) );
            branch
                .setIsDefaultBySystem( Long.parseLong( document.get( CommonConstants.IS_DEFAULT_BY_SYSTEM_SOLR ).toString() ) );

            matchedBranches.put( Long.parseLong( document.get( CommonConstants.BRANCH_ID_SOLR ).toString() ), branch );
        }
        List<OrganizationUnitSettings> branchSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsForMultipleIds(
            matchedBranches.keySet(), MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
        for ( OrganizationUnitSettings setting : branchSettings ) {
            BranchFromSearch branch = matchedBranches.get( setting.getIden() );
            branch.setAddress1( setting.getContact_details().getAddress1() );
            branch.setAddress2( setting.getContact_details().getAddress2() );
            branch.setBranchId( setting.getIden() );
            branch.setCity( setting.getContact_details().getCity() );
            branch.setCountry( setting.getContact_details().getCountry() );
            branch.setCountryCode( setting.getContact_details().getCountryCode() );
            branch.setState( setting.getContact_details().getState() );
            branch.setZipcode( setting.getContact_details().getZipcode() );
        }

        return matchedBranches.values();
    }


    private Collection<RegionFromSearch> getRegionsFromSolrDocuments( SolrDocumentList documentList )
    {
        Map<Long, RegionFromSearch> matchedRegions = new LinkedHashMap<>();
        for ( SolrDocument document : documentList ) {
            RegionFromSearch region = new RegionFromSearch();

            region.setCompanyId( Long.parseLong( document.get( CommonConstants.COMPANY_ID_SOLR ).toString() ) );
            region.setRegionId( Long.parseLong( document.get( CommonConstants.REGION_ID_SOLR ).toString() ) );
            region.setRegionName( document.get( CommonConstants.REGION_NAME_SOLR ).toString() );
            region.setStatus( Integer.parseInt( document.get( CommonConstants.STATUS_SOLR ).toString() ) );
            region
                .setIsDefaultBySystem( Long.parseLong( document.get( CommonConstants.IS_DEFAULT_BY_SYSTEM_SOLR ).toString() ) );

            matchedRegions.put( Long.parseLong( document.get( CommonConstants.REGION_ID_SOLR ).toString() ), region );
        }
        List<OrganizationUnitSettings> branchSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsForMultipleIds(
            matchedRegions.keySet(), MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
        for ( OrganizationUnitSettings setting : branchSettings ) {
            RegionFromSearch region = matchedRegions.get( setting.getIden() );
            region.setAddress1( setting.getContact_details().getAddress1() );
            region.setAddress2( setting.getContact_details().getAddress2() );
            region.setCity( setting.getContact_details().getCity() );
            region.setCountry( setting.getContact_details().getCountry() );
            region.setCountryCode( setting.getContact_details().getCountryCode() );
            region.setState( setting.getContact_details().getState() );
            region.setZipcode( setting.getContact_details().getZipcode() );
        }

        return matchedRegions.values();
    }


    /**
     * Method to get a list of social posts given the Solr document.
     * @throws InvalidInputException 
     */
    @Override
    public List<SocialPost> getSocialPostsFromSolrDocuments( SolrDocumentList documentList ) throws InvalidInputException
    {
        LOG.info( "Method getSocialPostsFromSolrDocuments() started" );

        if ( documentList == null ) {
            LOG.warn( "Invalid parameter passed : passed parameter documentList is null" );
            throw new InvalidInputException( "Invalid parameter passed : passed parameter documentList is null" );
        }

        List<SocialPost> matchedSocialPosts = new ArrayList<SocialPost>();
        for ( SolrDocument document : documentList ) {
            SocialPost post = new SocialPost();
            if ( document.get( CommonConstants.SOURCE_SOLR ) != null
                && !( document.get( CommonConstants.SOURCE_SOLR ).toString().isEmpty() ) ) {
                post.setSource( document.get( CommonConstants.SOURCE_SOLR ).toString() );
            } else {
                post.setSource( "" );
            }
            if ( document.get( CommonConstants.COMPANY_ID_SOLR ) != null
                && !( document.get( CommonConstants.COMPANY_ID_SOLR ).toString().isEmpty() ) ) {
                post.setCompanyId( Long.parseLong( document.get( CommonConstants.COMPANY_ID_SOLR ).toString() ) );
            }
            if ( document.get( CommonConstants.REGION_ID_SOLR ) != null
                && !( document.get( CommonConstants.REGION_ID_SOLR ).toString().isEmpty() ) ) {
                post.setRegionId( Long.parseLong( document.get( CommonConstants.REGION_ID_SOLR ).toString() ) );
            }
            if ( document.get( CommonConstants.BRANCH_ID_SOLR ) != null
                && !( document.get( CommonConstants.BRANCH_ID_SOLR ).toString().isEmpty() ) ) {
                post.setBranchId( Long.parseLong( document.get( CommonConstants.BRANCH_ID_SOLR ).toString() ) );
            }
            if ( document.get( CommonConstants.USER_ID_SOLR ) != null
                && !( document.get( CommonConstants.USER_ID_SOLR ).toString().isEmpty() ) ) {
                post.setAgentId( Long.parseLong( document.get( CommonConstants.USER_ID_SOLR ).toString() ) );
            }
            if ( document.get( CommonConstants.TIME_IN_MILLIS_SOLR ) != null
                && !( document.get( CommonConstants.TIME_IN_MILLIS_SOLR ).toString().isEmpty() ) ) {
                post.setTimeInMillis( Long.parseLong( document.get( CommonConstants.TIME_IN_MILLIS_SOLR ).toString() ) );
            }
            if ( document.get( CommonConstants.POST_ID_SOLR ) != null
                && !( document.get( CommonConstants.POST_ID_SOLR ).toString().isEmpty() ) ) {
                post.setPostId( document.get( CommonConstants.POST_ID_SOLR ).toString() );
            }
            if ( document.get( CommonConstants.POST_TEXT_SOLR ) != null
                && !( document.get( CommonConstants.POST_TEXT_SOLR ).toString().isEmpty() ) ) {
                post.setPostText( document.get( CommonConstants.POST_TEXT_SOLR ).toString() );
            }

            if ( document.get( CommonConstants.POST_URL_SOLR ) != null ) {
                post.setPostUrl( document.get( CommonConstants.POST_URL_SOLR ).toString() );
            }
            if ( document.get( CommonConstants.POSTED_BY_SOLR ) != null
                && !( document.get( CommonConstants.POSTED_BY_SOLR ).toString().isEmpty() ) ) {
                post.setPostedBy( document.get( CommonConstants.POSTED_BY_SOLR ).toString() );
            }
            if ( document.get( CommonConstants.ID_SOLR ) != null
                && !( document.get( CommonConstants.ID_SOLR ).toString().isEmpty() ) ) {
                post.set_id( document.get( CommonConstants.ID_SOLR ).toString() );
            }
            matchedSocialPosts.add( post );
        }
        LOG.info( "Method getSocialPostsFromSolrDocuments() finished" );
        return matchedSocialPosts;
    }


    @SuppressWarnings ( "unchecked")
    public Collection<UserFromSearch> getUsersFromSolrDocuments( SolrDocumentList documentList ) throws InvalidInputException
    {
        if ( documentList == null ) {
            throw new InvalidInputException( "Invalid parameter passed : passed parameter documentList is null" );
        }

        Map<Long, UserFromSearch> matchedUsers = new LinkedHashMap<>();
        for ( SolrDocument document : documentList ) {
            UserFromSearch user = new UserFromSearch();

            user.setCompanyId( Long.parseLong( document.get( CommonConstants.COMPANY_ID_SOLR ).toString() ) );
            user.setAgent( Boolean.parseBoolean( document.get( CommonConstants.IS_AGENT_SOLR ).toString() ) );
            user.setStatus( Integer.parseInt( document.get( CommonConstants.STATUS_SOLR ).toString() ) );
            user.setIsOwner( Integer.parseInt( document.get( CommonConstants.IS_OWNER_COLUMN ).toString() ) );
            user.setBranchAdmin( Boolean.parseBoolean( document.get( CommonConstants.IS_BRANCH_ADMIN_SOLR ).toString() ) );
            user.setRegionAdmin( Boolean.parseBoolean( document.get( CommonConstants.IS_REGION_ADMIN_SOLR ).toString() ) );
            user.setRegions( (List<Long>) document.get( CommonConstants.REGIONS_SOLR ) );
            user.setBranches( (List<Long>) document.get( CommonConstants.BRANCHES_SOLR ) );
            user.setAgentIds( (List<Long>) document.get( "agentIds" ) );
            user.setProfileImageSet(
                Boolean.parseBoolean( document.get( CommonConstants.IS_PROFILE_IMAGE_SET_SOLR ).toString() ) );
            matchedUsers.put( Long.parseLong( document.get( CommonConstants.USER_ID_SOLR ).toString() ), user );
        }

        List<AgentSettings> agentSettings = organizationUnitSettingsDao
            .fetchMultipleAgentSettingsById( new ArrayList<Long>( matchedUsers.keySet() ) );
        for ( AgentSettings setting : agentSettings ) {
            UserFromSearch user = matchedUsers.get( setting.getIden() );
            user.setUserId( setting.getIden() );
            user.setEmailId( setting.getContact_details().getMail_ids().getWork() );
            user.setFirstName( setting.getContact_details().getFirstName() );
            user.setDisplayName( setting.getContact_details().getName() );
            user.setLastName( setting.getContact_details().getLastName() );
            user.setLoginName( setting.getContact_details().getMail_ids().getWork() );
            user.setTitle( setting.getContact_details().getTitle() );
            user.setAboutMe( setting.getContact_details().getAbout_me() );
            user.setProfileImageUrl( setting.getProfileImageUrl() );
            user.setProfileImageThumbnail( setting.getProfileImageUrlThumbnail() );
            user.setProfileName( setting.getProfileName() );
            user.setProfileUrl( setting.getProfileUrl() );
            user.setReviewCount( setting.getReviewCount() );
        }

        return matchedUsers.values();
    }


    /**
     * 
     * @param documentList
     * @return
     * @throws InvalidInputException 
     */
    @Override
    public List<UserFromSearch> getUsersWithMetaDataFromSolrDocuments( SolrDocumentList documentList )
        throws InvalidInputException
    {
        if ( documentList == null ) {
            throw new InvalidInputException( "Invalid parameter passed : passed parameter documentList is null" );
        }

        LOG.debug( "method getUsersWithMetaDataFromSolrDocuments started" );
        List<UserFromSearch> userList = new ArrayList<UserFromSearch>();
        for ( SolrDocument document : documentList ) {
            UserFromSearch user = new UserFromSearch();

            user.setUserId( Long.parseLong( document.get( CommonConstants.USER_ID_SOLR ).toString() ) );
            user.setAgent( Boolean.parseBoolean( document.get( CommonConstants.IS_AGENT_SOLR ).toString() ) );
            user.setStatus( Integer.parseInt( document.get( CommonConstants.STATUS_SOLR ).toString() ) );
            user.setFirstName( document.get( CommonConstants.USER_FIRST_NAME_SOLR ).toString() );
            if ( document.get( CommonConstants.USER_LAST_NAME_SOLR ) != null )
                user.setLastName( document.get( CommonConstants.USER_LAST_NAME_SOLR ).toString() );
            if ( document.get( CommonConstants.USER_DISPLAY_NAME_SOLR ) != null )
                user.setDisplayName( document.get( CommonConstants.USER_DISPLAY_NAME_SOLR ).toString() );
            userList.add( user );
        }
        LOG.debug( "method getUsersWithMetaDataFromSolrDocuments ended" );
        return userList;
    }


    /**
     * Method to get last build time for social posts in Solr(Social Monitor)
     * @return
     * @throws SolrException 
     * @throws SolrServerException 
     */
    @Override
    public Date getLastBuildTimeForSocialPosts() throws SolrException
    {
        LOG.info( "Method getLastBuildTimeForSocialPosts() started" );
        LukeResponse response = null;
        Date lastBuildTime = null;
        //Luke request handler gives the last commitTimeMSec
        SolrServer solrServer = new HttpSolrServer( solrSocialPostUrl );
        LukeRequest luke = new LukeRequest();
        luke.setShowSchema( false );
        try {
            response = luke.process( solrServer );
            if ( response != null ) {
                //Get the lastModified field from the luke response
                lastBuildTime = (Date) response.getIndexInfo().get( CommonConstants.LUKE_LAST_MODIFIED );
                if ( lastBuildTime != null ) {
                    LOG.debug( "Last Build Time : " + lastBuildTime );
                } else {
                    throw new SolrException( "The lastModified field is empty" );
                }
            } else {
                throw new SolrException( "The response is empty" );
            }
        } catch ( SolrServerException | IOException e ) {
            LOG.error( "Error getting response from LukeRequest" );
            throw new SolrException( "Error getting response from LukeRequest" );
        }
        LOG.info( "Method getLastBuildTimeForSocialPosts() finished" );
        return lastBuildTime;
    }


    @Override
    public SolrDocumentList searchUsersByLoginNameOrNameUnderAdmin( String pattern, User admin, UserFromSearch adminFromSearch,
        int startIndex, int batchSize ) throws InvalidInputException, SolrException, MalformedURLException
    {

        LOG.info( "Method searchUsersByLoginNameOrNameUnderAdmin called for pattern :" + pattern );
        if ( pattern == null ) {
            throw new InvalidInputException( "Pattern is null or empty while searching for Users" );
        }
        if ( admin == null ) {
            throw new InvalidInputException( "admin is null or empty while searching for Users" );
        }
        if ( adminFromSearch == null ) {
            throw new InvalidInputException( "adminFromSearch is null or empty while searching for Users" );
        }

        SolrDocumentList results;
        QueryResponse response = null;
        /*String namePattern = pattern + "*";
        namePattern = namePattern.replace(" ", "\\ ");
        String middlePattern = "*\\ " + namePattern;*/
        try {
            SolrServer solrServer = new HttpSolrServer( solrUserUrl );
            SolrQuery solrQuery = new SolrQuery();
            /*solrQuery.setQuery( CommonConstants.USER_DISPLAY_NAME_SOLR + ":" + namePattern + " OR "
                + CommonConstants.USER_DISPLAY_NAME_SOLR + ":" + middlePattern + " OR " + CommonConstants.USER_FIRST_NAME_SOLR
                + ":" + namePattern + " OR " + CommonConstants.USER_FIRST_NAME_SOLR + ":" + middlePattern + " OR "
                + CommonConstants.USER_LAST_NAME_SOLR + ":" + namePattern + " OR " + CommonConstants.USER_LAST_NAME_SOLR + ":"
                + middlePattern + " OR " + CommonConstants.USER_LOGIN_NAME_SOLR + ":" + namePattern + " OR "
                + CommonConstants.USER_LOGIN_NAME_SOLR + ":" + middlePattern );*/

            String query = "";


            //search display name 
            if ( pattern != null && !pattern.isEmpty() ) {
                query = query + generateSubQueryToSearch( CommonConstants.USER_DISPLAY_NAME_SOLR, pattern );
                query = query + " OR " + generateSubQueryToSearch( CommonConstants.USER_LOGIN_NAME_SOLR, pattern );
            } else {
                query = "*:*";
            }

            solrQuery.setQuery( query );

            solrQuery.addFilterQuery( CommonConstants.COMPANY_ID_SOLR + ":" + adminFromSearch.getCompanyId() );
            if ( !admin.isCompanyAdmin() ) {
                if ( admin.isRegionAdmin() ) {
                    solrQuery.addFilterQuery(
                        CommonConstants.REGIONS_SOLR + ":" + getSolrSearchArrayStr( adminFromSearch.getRegions() ) );
                } else if ( admin.isBranchAdmin() ) {
                    solrQuery.addFilterQuery(
                        CommonConstants.REGIONS_SOLR + ":" + getSolrSearchArrayStr( adminFromSearch.getRegions() ) );
                    solrQuery.addFilterQuery(
                        CommonConstants.BRANCHES_SOLR + ":" + getSolrSearchArrayStr( adminFromSearch.getBranches() ) );
                }
            }
            solrQuery.addFilterQuery( CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE + " OR "
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_NOT_VERIFIED + " OR " + CommonConstants.STATUS_SOLR
                + ":" + CommonConstants.STATUS_TEMPORARILY_INACTIVE );
            solrQuery.addSort( CommonConstants.USER_DISPLAY_NAME_SOLR, ORDER.asc );
            solrQuery.addField( CommonConstants.USER_ID_SOLR );
            LOG.debug( "Querying solr for searching users" );
            if ( startIndex > -1 ) {
                solrQuery.setStart( startIndex );
            }
            if ( batchSize > 0 ) {
                solrQuery.setRows( batchSize );
            }

            LOG.info( "Running Solr query : " + solrQuery.getQuery() );
            response = solrServer.query( solrQuery );
            results = response.getResults();
        } catch ( SolrServerException e ) {
            LOG.error( "SolrServerException while performing User search" );
            throw new SolrException( "Exception while performing search for user. Reason : " + e.getMessage(), e );
        }

        LOG.info(
            "Method searchUsersByLoginNameOrNameUnderAdmin finished for pattern :" + pattern + " returning : " + results );
        return results;

    }


    @Override
    public Set<Long> getUserIdsFromSolrDocumentList( SolrDocumentList userIdList ) throws InvalidInputException
    {
        if ( userIdList == null ) {
            throw new InvalidInputException( "Invalid parameter passed : passed parameter documentList is null" );
        }

        Set<Long> userIds = new LinkedHashSet<Long>();
        for ( SolrDocument userId : userIdList ) {
            userIds.add( Long.parseLong( userId.get( CommonConstants.USER_ID_SOLR ).toString() ) );
        }
        return userIds;
    }


    private String getSolrSearchArrayStr( List<Long> ids )
    {
        String searchArrStr = "";
        for ( Long id : ids ) {
            if ( id != CommonConstants.DEFAULT_COMPANY_ID && id != CommonConstants.DEFAULT_REGION_ID )
                searchArrStr += id + " ";
        }
        return "(" + searchArrStr.trim() + ")";
    }


    /**
     * Method to get all users in solr
     * 
     * @param startIndex
     * @param batchSize
     * @return
     * @throws SolrException
     */
    @Override
    public SolrDocumentList getAllUsers( int startIndex, int batchSize ) throws SolrException
    {
        SolrDocumentList results;
        QueryResponse response = null;
        SolrServer solrServer = new HttpSolrServer( solrUserUrl );
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery( "*:*" );
        if ( startIndex > -1 ) {
            solrQuery.setStart( startIndex );
        }
        if ( batchSize > 0 ) {
            solrQuery.setRows( batchSize );
        }
        LOG.info( "Running Solr query : " + solrQuery.getQuery() );
        try {
            response = solrServer.query( solrQuery );
            results = response.getResults();
        } catch ( SolrServerException e ) {
            LOG.error( "SolrServerException while performing User search" );
            throw new SolrException( "Exception while performing search for user. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method getAllUsers finished" );
        return results;
    }


    /**
     * Method to set isProfileImageSet field for multiple users
     * 
     * @param isProfileSetMap
     * @throws InvalidInputException
     * @throws SolrException
     */
    @Override
    public void updateIsProfileImageSetFieldForMultipleUsers( Map<Long, Boolean> isProfileSetMap )
        throws InvalidInputException, SolrException
    {
        if ( isProfileSetMap == null || isProfileSetMap.isEmpty() ) {
            throw new InvalidInputException( "Invalid parameter passed : passed isProfileSetMap map is null or empty" );
        }

        SolrServer solrServer = new HttpSolrServer( solrUserUrl );

        List<SolrInputDocument> inputDocList = new ArrayList<SolrInputDocument>();
        Map<String, Boolean> editKeyValues;
        SolrInputDocument inputDoc;

        for ( Entry<Long, Boolean> entry : isProfileSetMap.entrySet() ) {
            editKeyValues = new HashMap<String, Boolean>();
            editKeyValues.put( SOLR_EDIT_REPLACE, entry.getValue() );
            // Adding fields to be updated
            inputDoc = new SolrInputDocument();
            inputDoc.setField( CommonConstants.USER_ID_SOLR, entry.getKey() );
            inputDoc.setField( CommonConstants.IS_PROFILE_IMAGE_SET_SOLR, editKeyValues );
            inputDocList.add( inputDoc );
        }

        try {
            if ( inputDocList.size() > 0 ) {
                solrServer.add( inputDocList );
                solrServer.commit();
            }
        } catch ( SolrServerException | IOException e ) {
            LOG.error( "Exception while editing user in solr. Reason : " + e.getMessage(), e );
            throw new SolrException( "Exception while updating user to solr. Reason : " + e.getMessage(), e );
        }
    }


    /**
     * Method to update regions for multiple users started
     * @param regionsMap
     * @throws InvalidInputException
     * @throws SolrException 
     */
    @Override
    public void updateRegionsForMultipleUsers( Map<Long, List<Long>> regionsMap ) throws InvalidInputException, SolrException
    {
        LOG.info( "Method to regions for multiple users started." );
        if ( regionsMap == null ) {
            throw new InvalidInputException( "RegionsMap is null" );
        }
        //There's no update needed.
        if ( regionsMap.isEmpty() ) {
            return;
        }

        SolrServer solrServer = new HttpSolrServer( solrUserUrl );

        List<SolrInputDocument> inputDocList = new ArrayList<SolrInputDocument>();
        Map<String, List<Long>> editKeyValues;
        SolrInputDocument inputDoc;

        for ( Entry<Long, List<Long>> entry : regionsMap.entrySet() ) {
            editKeyValues = new HashMap<String, List<Long>>();
            editKeyValues.put( SOLR_EDIT_REPLACE, entry.getValue() );
            // Adding fields to be updated
            inputDoc = new SolrInputDocument();
            inputDoc.setField( CommonConstants.USER_ID_SOLR, entry.getKey() );
            inputDoc.setField( CommonConstants.REGIONS_SOLR, editKeyValues );
            inputDocList.add( inputDoc );
        }

        try {
            if ( inputDocList.size() > 0 ) {
                solrServer.add( inputDocList );
                solrServer.commit();
            }
        } catch ( SolrServerException | IOException e ) {
            LOG.error( "Exception while editing user in solr. Reason : " + e.getMessage(), e );
            throw new SolrException( "Exception while updating user to solr. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method to regions for multiple users finished." );
    }


    /**
     * Method to remove social post from solr
     * 
     * JIRA SS-1329
     * 
     * @param postMongoId
     * @throws SolrException
     * @throws InvalidInputException
     */
    @Override
    public void removeSocialPostFromSolr( String postMongoId ) throws SolrException, InvalidInputException
    {
        LOG.info( "Method removeSocialPostFromSolr() to remove social post from solr started.", postMongoId );
        if ( postMongoId == null || postMongoId.isEmpty() ) {
            throw new InvalidInputException( "Invalid input pareameter : passed postMongoId is not valid" );
        }

        try {
            SolrServer solrServer = new HttpSolrServer( solrSocialPostUrl );
            solrServer.deleteById( postMongoId );
            solrServer.commit();
        } catch ( SolrServerException | IOException e ) {
            LOG.error( "Exception while removing social post from solr. Reason : " + e.getMessage(), e );
            throw new SolrException( "Exception while removing social post from solr. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method removeUserFromSolr() to remove social post {} from solr finished successfully.", postMongoId );
    }


    /**
     * Method to find all the users in a branch
     * @param branchId
     * @return
     * @throws SolrException
     */
    @Override
    public SolrDocumentList findUsersInBranch( long branchId, int startIndex, int batchSize ) throws SolrException
    {
        LOG.info( "Method to find all users in branch " + branchId + " started." );
        SolrDocumentList results;
        QueryResponse response = null;
        SolrServer solrServer = new HttpSolrServer( solrUserUrl );
        SolrQuery query = new SolrQuery( CommonConstants.BRANCHES_SOLR + ":" + branchId );
        if ( startIndex > -1 ) {
            query.setStart( startIndex );
        }
        if ( batchSize > 0 ) {
            query.setRows( batchSize );
        }
        LOG.info( "Running Solr query : " + query.getQuery() );
        try {
            response = solrServer.query( query );
            results = response.getResults();
        } catch ( SolrServerException e ) {
            LOG.error( "SolrServerException while finding all the users in branch " + branchId );
            throw new SolrException(
                "Exception while finding all the users in branchId " + branchId + ". Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method to find all users in branch " + branchId + " finished." );
        return results;
    }


    /**
     * Method to update review count of user in solr
     * @throws InvalidInputException
     * @throws SolrException
     * */
    @Override
    public void updateReviewCountOfUserInSolr( User user ) throws InvalidInputException, SolrException
    {
        if ( user == null )
            throw new InvalidInputException( "User passed cannot be null in updateReviewCountOfUserInSolr()" );
        LOG.info(
            "Method to update solr review count for user : " + user.getUserId() + ", updateReviewCountOfUserInSolr() started" );
        LOG.info( "Fetching review count of user from mongo for user id : " + user.getUserId() );
        long reviewCount = profileManagementService.getReviewsCount( user.getUserId(), -1, -1,
            CommonConstants.PROFILE_LEVEL_INDIVIDUAL, false, false );
        LOG.info( "Fetched review count of user from mongo for user id : " + user.getUserId() );
        if ( user.getIsZillowConnected() == CommonConstants.STATUS_ACTIVE && user.getZillowReviewCount() > 0 ) {
            LOG.info( "Adding zillow review count to review count as user is connected to zillow" );
            reviewCount += user.getZillowReviewCount();
        }
        LOG.info( "Updating review count of user in solr for user id : " + user.getUserId() );
        editUserInSolr( user.getUserId(), CommonConstants.REVIEW_COUNT_SOLR, String.valueOf( reviewCount ) );
        LOG.info( "Updated review count of user in solr for user id : " + user.getUserId() );
        LOG.info(
            "Method to update solr review count for user : " + user.getUserId() + ", updateReviewCountOfUserInSolr() ended" );
    }


    @Override
    public void removeRegionFromSolr( long regionIdToRemove ) throws SolrException, InvalidInputException
    {
        LOG.info( "Method removeRegionFromSolr() to remove region id {} from solr started.", regionIdToRemove );
        if ( regionIdToRemove <= 0l ) {
            throw new InvalidInputException( "Invalid input pareameter : passed region id is not valid" );
        }

        try {
            SolrServer solrServer = new HttpSolrServer( solrRegionUrl );
            solrServer.deleteById( String.valueOf( regionIdToRemove ) );
            solrServer.commit();
        } catch ( SolrServerException | IOException e ) {
            LOG.error( "Exception while removing region from solr. Reason : " + e.getMessage(), e );
            throw new SolrException( "Exception while removing region from solr. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method removeRegionFromSolr() to remove region id {} from solr finished successfully.", regionIdToRemove );
    }


    @Override
    public void removeBranchFromSolr( long branchIdToRemove ) throws SolrException, InvalidInputException
    {
        LOG.info( "Method removeBranchFromSolr() to remove branch id {} from solr started.", branchIdToRemove );
        if ( branchIdToRemove <= 0l ) {
            throw new InvalidInputException( "Invalid input pareameter : passed branch id is not valid" );
        }

        try {
            SolrServer solrServer = new HttpSolrServer( solrBranchUrl );
            solrServer.deleteById( String.valueOf( branchIdToRemove ) );
            solrServer.commit();
        } catch ( SolrServerException | IOException e ) {
            LOG.error( "Exception while removing branch from solr. Reason : " + e.getMessage(), e );
            throw new SolrException( "Exception while removing branch from solr. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method removeBranchFromSolr() to remove branch id {} from solr finished successfully.", branchIdToRemove );
    }


    @Override
    public void solrReviewCountUpdater()
    {
        try {
            //getting last run end time of batch and update last start time
            long lastRunEndTime = batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_REVIEW_COUNT_UPDATER, CommonConstants.BATCH_NAME_REVIEW_COUNT_UPDATER );
            //get user id list for them review count will be updated
            List<Long> userIdList = batchTrackerService.getUserIdListToBeUpdated( lastRunEndTime );
            //getting no of reviews for the agents
            Map<Long, Integer> agentsReviewCount;
            try {
                agentsReviewCount = batchTrackerService.getReviewCountForAgents( userIdList );
            } catch ( ParseException e ) {
                LOG.error( "Error while parsing the data fetched from mongo for survey count", e );
                throw e;
            }
            if ( agentsReviewCount != null && !agentsReviewCount.isEmpty() )
                updateCompletedSurveyCountForMultipleUserInSolr( agentsReviewCount );


            //updating last run time for batch in database
            batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_REVIEW_COUNT_UPDATER );
        } catch ( Exception e ) {
            LOG.error( "Error in solr review count updater", e );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType( CommonConstants.BATCH_TYPE_REVIEW_COUNT_UPDATER,
                    e.getMessage() );
                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError( CommonConstants.BATCH_NAME_REVIEW_COUNT_UPDATER,
                    System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in batch tracker " );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report excption mail to admin " );
            }
        }
    }


    @Override
    @Transactional
    public void showOrHideUsersOfCompanyInSolr( Long companyId , Boolean hidden)
    {
        LOG.info( "Adding Hidden boolean for users in solr for hidden company" );
        int startIndex = 0;
        int batchSize = 500;
        List<Long> userIds;

        if ( companyId != null ) {
            try {
                do {
                    userIds = organizationManagementService.getAgentIdsUnderCompany( companyId, startIndex, batchSize );
                    // updating all users in the company in solr   
                    if(hidden){
                        this.editUsersInSolr( userIds, CommonConstants.USER_IS_HIDDEN_FROM_SEARCH_SOLR, "true" ); 
                    }
                    else{
                        this.editUsersInSolr( userIds, CommonConstants.USER_IS_HIDDEN_FROM_SEARCH_SOLR, "false" );
                    }
                    startIndex += batchSize;
                } while ( userIds != null && userIds.size() == batchSize );
            } catch ( Exception exception ) {
                LOG.error( "error while hiding users of the company: " + companyId + exception.getMessage() );
            }
        }
        LOG.info( "Added Hidden boolean for users in solr for hidden company" );
    }
}