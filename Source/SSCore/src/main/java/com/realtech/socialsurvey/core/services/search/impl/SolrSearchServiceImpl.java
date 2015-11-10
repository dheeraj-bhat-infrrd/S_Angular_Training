package com.realtech.socialsurvey.core.services.search.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
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
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.utils.SolrSearchUtils;


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
    private SolrSearchUtils solrSearchUtils;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;


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
                CommonConstants.STATUS_COLUMN + ":" + CommonConstants.STATUS_ACTIVE, CommonConstants.IS_DEFAULT_BY_SYSTEM_SOLR
                    + ":" + CommonConstants.NO );

            if ( regionIds != null && !regionIds.isEmpty() ) {
                String regionIdsStr = getSpaceSeparatedStringFromIds( regionIds );
                solrQuery.addFilterQuery( CommonConstants.REGION_ID_SOLR + ":(" + regionIdsStr + ")" );
            }

            solrQuery.setStart( start );
            if ( rows > 0 ) {
                solrQuery.setRows( rows );
            }

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
    public long getRegionsCount( String regionPattern, Company company, Set<Long> regionIds ) throws InvalidInputException,
        SolrException
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
                CommonConstants.STATUS_COLUMN + ":" + CommonConstants.STATUS_ACTIVE, CommonConstants.IS_DEFAULT_BY_SYSTEM_SOLR
                    + ":" + CommonConstants.NO );

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
            query.addFilterQuery( CommonConstants.COMPANY_ID_SOLR + ":" + company.getCompanyId(), CommonConstants.STATUS_SOLR
                + ":" + CommonConstants.STATUS_ACTIVE, CommonConstants.IS_DEFAULT_BY_SYSTEM_SOLR + ":" + CommonConstants.NO );
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
     */
    @Override
    public void addOrUpdateRegionToSolr( Region region ) throws SolrException
    {
        LOG.info( "Method to add or update region to solr called for region : " + region );
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
     */
    @Override
    public void addOrUpdateBranchToSolr( Branch branch ) throws SolrException
    {
        LOG.info( "Method to add/update branch to solr called for branch : " + branch );
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
    public String searchUsersByLoginNameAndCompany( String loginNamePattern, Company company ) throws InvalidInputException,
        SolrException, MalformedURLException
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
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_NOT_VERIFIED + " OR "
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_TEMPORARILY_INACTIVE );
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

        LOG.info( "Method searchUsersByLoginNameOrName finished for pattern :" + pattern + " returning : " + results );
        return results;
    }


    /**
     * Method to search for users given their first and/or last name
     */
    @Override
    public SolrDocumentList searchUsersByFirstOrLastName( String patternFirst, String patternLast, int startIndex, int noOfRows )
        throws InvalidInputException, SolrException, MalformedURLException
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
            solrQuery.setSort( CommonConstants.REVIEW_COUNT_SOLR, ORDER.desc );
            String query = "";
            if ( !patternFirst.equals( "" ) && !patternLast.equals( "" ) ) {
                query = CommonConstants.USER_FIRST_NAME_SOLR + ":" + patternFirst + "*" + " AND "
                    + CommonConstants.USER_LAST_NAME_SOLR + ":" + patternLast + "*";
            } else if ( !patternFirst.equals( "" ) && patternLast.equals( "" ) ) {
                query = CommonConstants.USER_FIRST_NAME_SOLR + ":" + patternFirst + "*";
            } else if ( patternFirst.equals( "" ) && !patternLast.equals( "" ) ) {
                query = CommonConstants.USER_LAST_NAME_SOLR + ":" + patternLast + "*";
            }
            solrQuery.setQuery( query );
            solrQuery.addFilterQuery( CommonConstants.IS_AGENT_SOLR + ":" + CommonConstants.IS_AGENT_TRUE_SOLR );
            solrQuery.addFilterQuery( "-" + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_INACTIVE );
            solrQuery.setStart( startIndex );
            solrQuery.setRows( noOfRows );

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


    /**
     * Method to perform search of Users from solr based on the input pattern for user and company.
     * 
     * @throws InvalidInputException
     * @throws SolrException
     * @throws MalformedURLException
     */
    @Override
    public SolrDocumentList searchUsersByCompany( long companyId, int startIndex, int noOfRows ) throws InvalidInputException,
        SolrException, MalformedURLException
    {
        if ( companyId < 0 ) {
            throw new InvalidInputException( "Pattern is null or empty while searching for Users" );
        }
        LOG.info( "Method searchUsersByCompanyId() called for company id : " + companyId );

        SolrDocumentList results = null;
        try {
            SolrServer solrServer = new HttpSolrServer( solrUserUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery( CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE + " OR "
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_NOT_VERIFIED + " OR "
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_TEMPORARILY_INACTIVE );
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
    public long countUsersByCompany( long companyId, int startIndex, int noOfRows ) throws InvalidInputException,
        SolrException, MalformedURLException
    {
        LOG.info( "Method countUsersByCompany() called for company id : " + companyId );
        if ( companyId < 0 ) {
            throw new InvalidInputException( "Pattern is null or empty while searching for Users" );
        }

        long resultsCount = 0l;
        QueryResponse response = null;
        try {
            SolrServer solrServer = new HttpSolrServer( solrUserUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery( CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE + " OR "
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_NOT_VERIFIED + " OR "
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_TEMPORARILY_INACTIVE );
            solrQuery.addFilterQuery( CommonConstants.COMPANY_ID_SOLR + ":" + companyId );
            solrQuery.setStart( startIndex );
            solrQuery.setRows( noOfRows );
            response = solrServer.query( solrQuery );

            resultsCount = response.getResults().getNumFound();
            LOG.debug( "User search result count is : " + resultsCount );
        } catch ( SolrServerException e ) {
            LOG.error( "SolrServerException while performing User search" );
            throw new SolrException( "Exception while performing search for user. Reason : " + e.getMessage(), e );
        }

        LOG.info( "Method countUsersByCompany() finished for company id : " + companyId );
        return resultsCount;
    }


    /**
     * Method to add User into solr
     */
    @Override
    public void addUserToSolr( User user ) throws SolrException
    {
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

        if ( user.getCompany() != null ) {
            document.addField( CommonConstants.COMPANY_ID_SOLR, user.getCompany().getCompanyId() );
        }
        document.addField( CommonConstants.STATUS_SOLR, user.getStatus() );
        Set<Long> branches = new HashSet<Long>();
        Set<Long> regions = new HashSet<Long>();
        if ( user.getUserProfiles() != null )
            for ( UserProfile userProfile : user.getUserProfiles() ) {
                if ( userProfile.getRegionId() != 0 ) {
                    regions.add( userProfile.getRegionId() );
                }
                if ( userProfile.getBranchId() != 0 ) {
                    branches.add( userProfile.getBranchId() );
                }
            }
        document.addField( CommonConstants.BRANCHES_SOLR, branches );
        document.addField( CommonConstants.REGIONS_SOLR, regions );
        document.addField( CommonConstants.IS_AGENT_SOLR, user.isAgent() );
        document.addField( CommonConstants.IS_BRANCH_ADMIN_SOLR, user.isBranchAdmin() );
        document.addField( CommonConstants.IS_REGION_ADMIN_SOLR, user.isRegionAdmin() );

        return document;
    }


    /*
     * Method to remove a user from Solr
     */
    @Override
    public void removeUserFromSolr( long userIdToRemove ) throws SolrException
    {
        LOG.info( "Method removeUserFromSolr() to remove user id {} from solr started.", userIdToRemove );
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
     */
    @Override
    public void editUserInSolr( long userId, String key, String value ) throws SolrException
    {
        LOG.info( "Method to edit user in solr called for user : " + userId );

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
    public void editUserInSolrWithMultipleValues( long userId, Map<String, Object> map ) throws SolrException
    {
        LOG.info( "Method to edit user in solr called for user : " + userId );

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
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_NOT_VERIFIED + " OR "
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_TEMPORARILY_INACTIVE );
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
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_NOT_VERIFIED + " OR "
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_TEMPORARILY_INACTIVE );
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
            LOG.error( "SolrServerException in searchUsersByIden.Reason:" + e.getMessage(), e );
            throw new SolrException( "Exception while performing search for user. Reason : " + e.getMessage(), e );
        }
        LOG.info( "Method searchUsersByIden finished for iden : " + iden );
        return getUsersFromSolrDocuments( results );
    }


    @Override
    public long getUsersCountByIden( long iden, String idenFieldName, boolean isAgent ) throws InvalidInputException,
        SolrException
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
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_NOT_VERIFIED + " OR "
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_TEMPORARILY_INACTIVE );
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
        if ( regionId < 0 ) {
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
                regionName = (String) results.get( CommonConstants.INITIAL_INDEX ).getFieldValue(
                    CommonConstants.REGION_NAME_SOLR );
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
        if ( branchId < 0 ) {
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
                branchName = (String) results.get( CommonConstants.INITIAL_INDEX ).getFieldValue(
                    CommonConstants.BRANCH_NAME_SOLR );
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
    public List<SolrDocument> searchBranchRegionOrAgentByName( String searchColumn, String searchKey, String columnName, long id )
        throws InvalidInputException, SolrException
    {
        LOG.info( "Method searchBranchRegionOrAgentByNameAndCompany() to search regions, branches, agent in a company started" );
        List<SolrDocument> results = new ArrayList<SolrDocument>();
        QueryResponse response = null;
        searchKey = searchKey + "*";

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
                query
                    .addFilterQuery( CommonConstants.IS_DEFAULT_BY_SYSTEM_SOLR + ":" + CommonConstants.IS_DEFAULT_BY_SYSTEM_NO );

            query.addFilterQuery( searchColumn + ":" + searchKey );
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

        LOG.info( "Method searchBranchRegionOrAgentByNameAndCompany() to search regions, branches, agent in a company finished" );
        return results;
    }


    @Override
    public String fetchRegionsByCompany( long companyId, int size ) throws InvalidInputException, SolrException,
        MalformedURLException
    {
        if ( companyId < 0 ) {
            throw new InvalidInputException( "Pattern is null or empty while searching for Regions" );
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
        if ( entityId < 0 ) {
            throw new InvalidInputException( "Pattern is null or empty while fetching social posts" );
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

        LOG.info( "Method fetchSocialPostsByEntity() finished for entity id : " + entityId + " and entity type : " + entityType );
        return results;
    }


    /**
     * Method to search social posts based on the post text
     */
    @Override
    public SolrDocumentList searchPostText( String entityType, long entityId, int startIndex, int noOfRows, String searchQuery )
        throws InvalidInputException, SolrException, MalformedURLException
    {
        if ( entityId < 0 ) {
            throw new InvalidInputException( "Pattern is null or empty while fetching social posts" );
        }
        LOG.info( "Method searchPostText() called for entity id : " + entityId + " and entity type : " + entityType );
        SolrDocumentList results = null;
        try {
            SolrServer solrServer = new HttpSolrServer( solrSocialPostUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery( entityType + ":" + entityId + " AND " + CommonConstants.POST_TEXT_SOLR + ":" + "*"
                + searchQuery + "*" );
            solrQuery.setStart( startIndex );
            solrQuery.setRows( noOfRows );
            solrQuery.addSort( CommonConstants.TIME_IN_MILLIS_SOLR, ORDER.desc );
            LOG.debug( "Solr Search Query : " + solrQuery.getQuery() );
            LOG.debug( "Querying solr for searching social posts" );
            results = solrServer.query( solrQuery ).getResults();
            LOG.debug( "Number of matches found : " + results.getNumFound() );
        } catch ( SolrServerException e ) {
            LOG.error( "SolrServerException while fetching social posts" );
            throw new SolrException( "Exception while fetching social posts. Reason : " + e.getMessage(), e );
        }

        LOG.info( "Method searchPostText() finished for entity id : " + entityId + " and entity type : " + entityType );
        return results;
    }


    @Override
    public String fetchBranchesByCompany( long companyId, int size ) throws InvalidInputException, SolrException,
        MalformedURLException
    {
        if ( companyId < 0 ) {
            throw new InvalidInputException( "Pattern is null or empty while searching for Branches" );
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
        if ( companyId < 0 ) {
            throw new InvalidInputException( "Pattern is null or empty while searching for Branches" );
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
        if ( companyId < 0 ) {
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
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_NOT_VERIFIED + " OR "
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_TEMPORARILY_INACTIVE );
            String branchIdsStr = getSpaceSeparatedStringFromIds( branchIds );
            solrQuery.addFilterQuery( CommonConstants.BRANCHES_SOLR + ":(" + branchIdsStr + ")" );

            solrQuery.setStart( start );
            if ( rows > 0 ) {
                solrQuery.setRows( rows );
            }

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
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_NOT_VERIFIED + " OR "
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_TEMPORARILY_INACTIVE );
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
    public void addRegionsToSolr( List<Region> regions ) throws SolrException
    {
        LOG.info( "Method to add regions to solr called" );
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
                if ( regionSettings.getContact_details() != null && regionSettings.getContact_details().getAddress1() != null ) {
                    document.addField( CommonConstants.ADDRESS1_SOLR, regionSettings.getContact_details().getAddress1() );
                }
                if ( regionSettings.getContact_details() != null && regionSettings.getContact_details().getAddress2() != null ) {
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
    public void addBranchesToSolr( List<Branch> branches ) throws SolrException
    {
        LOG.info( "Method to add branches to solr called" );
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
                if ( branchSettings.getContact_details() != null && branchSettings.getContact_details().getAddress1() != null ) {
                    document.addField( CommonConstants.ADDRESS1_SOLR, branchSettings.getContact_details().getAddress1() );
                }
                if ( branchSettings.getContact_details() != null && branchSettings.getContact_details().getAddress2() != null ) {
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
     */
    @Override
    public void addSocialPostsToSolr( List<SocialPost> socialPosts ) throws SolrException
    {
        LOG.info( "Method to add social posts to solr called" );
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
    public void addUsersToSolr( List<User> users ) throws SolrException
    {
        LOG.info( "Method to add users to solr called" );
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
    public void updateCompletedSurveyCountForUserInSolr( long agentId, int incrementCount ) throws SolrException,
        NoRecordsFetchedException
    {
        LOG.info( "Method to increase completed survey count updateCompletedSurveyCountForUserInSolr() finished." );
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
    public void updateCompletedSurveyCountForMultipleUserInSolr( Map<Long, Integer> usersReviewCount ) throws SolrException
    {
        LOG.info( "Method to increase completed survey count updateCompletedSurveyCountForUserInSolr() finished." );
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
    public Map<String, String> getCompanyAdmin( long companyId ) throws SolrException
    {
        LOG.info( "Method getEmailIdOfCompanyAdmin() started" );
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
        if ( companyId < 0 ) {
            throw new InvalidInputException( "Pattern is null or empty while searching for Users" );
        }
        LOG.info( "Method searchUsersByCompanyId() called for company id : " + companyId );
        String usersResult = null;
        QueryResponse response = null;
        List<Long> userIds = new ArrayList<>();
        try {
            SolrServer solrServer = new HttpSolrServer( solrUserUrl );
            SolrQuery solrQuery = new SolrQuery();
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
    public void removeUsersFromSolr( List<Long> agentIds ) throws SolrException
    {
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
        if ( companyId < 0 ) {
            throw new InvalidInputException( "Company ID is null while searching for branches." );
        }
        LOG.info( "Method searchBranchIdsByCompany() called for company id : " + companyId );
        QueryResponse response = null;
        List<Long> branchIds = new ArrayList<>();
        try {
            SolrServer solrServer = new HttpSolrServer( solrBranchUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery( CommonConstants.COMPANY_ID_SOLR + ":" + companyId );
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
    public void removeBranchesFromSolr( List<Long> branchIds ) throws SolrException
    {
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
        if ( companyId < 0 ) {
            throw new InvalidInputException( "Company ID is null while searching for regions." );
        }
        LOG.info( "Method searchBranchIdsByCompany() called for company id : " + companyId );
        QueryResponse response = null;
        List<Long> regionIds = new ArrayList<>();
        try {
            SolrServer solrServer = new HttpSolrServer( solrRegionUrl );
            SolrQuery solrQuery = new SolrQuery();
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
    public void removeRegionsFromSolr( List<Long> regionIds ) throws SolrException
    {
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


    private Collection<BranchFromSearch> getBranchesFromSolrDocuments( SolrDocumentList documentList )
    {
        Map<Long, BranchFromSearch> matchedBranches = new HashMap<>();
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
        List<OrganizationUnitSettings> branchSettings = organizationUnitSettingsDao
            .fetchOrganizationUnitSettingsForMultipleIds( matchedBranches.keySet(),
                MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
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
        Map<Long, RegionFromSearch> matchedRegions = new HashMap<>();
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
        List<OrganizationUnitSettings> branchSettings = organizationUnitSettingsDao
            .fetchOrganizationUnitSettingsForMultipleIds( matchedRegions.keySet(),
                MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
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
     */
    @Override
    public List<SocialPost> getSocialPostsFromSolrDocuments( SolrDocumentList documentList )
    {
        LOG.info( "Method getSocialPostsFromSolrDocuments() started" );
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

            matchedUsers.put( Long.parseLong( document.get( CommonConstants.USER_ID_SOLR ).toString() ), user );
        }

        List<AgentSettings> agentSettings = organizationUnitSettingsDao.fetchMultipleAgentSettingsById( new ArrayList<Long>(
            matchedUsers.keySet() ) );
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
     */
    @Override
    public List<UserFromSearch> getUsersWithMetaDataFromSolrDocuments( SolrDocumentList documentList )
    {
        LOG.debug( "method getUsersWithMetaDataFromSolrDocuments started" );
        List<UserFromSearch> userList = new ArrayList<UserFromSearch>();
        for ( SolrDocument document : documentList ) {
            UserFromSearch user = new UserFromSearch();
            
            user.setUserId( Long.parseLong(document.get( CommonConstants.USER_ID_SOLR ).toString() ) );
            user.setAgent( Boolean.parseBoolean( document.get( CommonConstants.IS_AGENT_SOLR ).toString() ) );
            user.setStatus( Integer.parseInt( document.get( CommonConstants.STATUS_SOLR ).toString() ) );
            user.setFirstName( document.get( CommonConstants.USER_FIRST_NAME_SOLR ).toString() );
            if(document.get( CommonConstants.USER_LAST_NAME_SOLR ) != null)
                user.setDisplayName( document.get( CommonConstants.USER_LAST_NAME_SOLR ).toString() );
            if(document.get( CommonConstants.USER_DISPLAY_NAME_SOLR ) != null)
                user.setLastName( document.get( CommonConstants.USER_DISPLAY_NAME_SOLR ).toString() );
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
        LOG.info( "Method searchUsersByLoginNameOrNameUnderAdmin() called for parameter : " + pattern );

        SolrDocumentList results;
        QueryResponse response = null;
        pattern = pattern + "*";
        try {
            SolrServer solrServer = new HttpSolrServer( solrUserUrl );
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery( "displayName:" + pattern + " OR " + CommonConstants.USER_FIRST_NAME_SOLR + ":" + pattern
                + " OR " + CommonConstants.USER_LAST_NAME_SOLR + ":" + pattern + " OR " + CommonConstants.USER_LOGIN_NAME_SOLR
                + ":" + pattern );
            solrQuery.addFilterQuery( CommonConstants.COMPANY_ID_SOLR + ":" + adminFromSearch.getCompanyId() );
            if ( !admin.isCompanyAdmin() ) {
                if ( admin.isRegionAdmin() ) {
                    solrQuery.addFilterQuery( CommonConstants.REGIONS_SOLR + ":"
                        + getSolrSearchArrayStr( adminFromSearch.getRegions() ) );
                } else if ( admin.isBranchAdmin() ) {
                    solrQuery.addFilterQuery( CommonConstants.REGIONS_SOLR + ":"
                        + getSolrSearchArrayStr( adminFromSearch.getRegions() ) );
                    solrQuery.addFilterQuery( CommonConstants.BRANCHES_SOLR + ":"
                        + getSolrSearchArrayStr( adminFromSearch.getBranches() ) );
                }
            }
            solrQuery.addFilterQuery( CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_ACTIVE + " OR "
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_NOT_VERIFIED + " OR "
                + CommonConstants.STATUS_SOLR + ":" + CommonConstants.STATUS_TEMPORARILY_INACTIVE );
            solrQuery.addSort( CommonConstants.USER_ID_SOLR, ORDER.asc );
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

        LOG.info( "Method searchUsersByLoginNameOrNameUnderAdmin finished for pattern :" + pattern + " returning : " + results );
        return results;

    }


    @Override
    public Set<Long> getUserIdsFromSolrDocumentList( SolrDocumentList userIdList )
    {
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
}