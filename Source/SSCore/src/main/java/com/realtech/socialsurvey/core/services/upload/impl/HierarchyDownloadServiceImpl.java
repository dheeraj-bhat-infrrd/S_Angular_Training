package com.realtech.socialsurvey.core.services.upload.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.HierarchyUploadDao;
import com.realtech.socialsurvey.core.dao.RegionDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.BranchUploadVO;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.HierarchyUpload;
import com.realtech.socialsurvey.core.entities.Licenses;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.RegionUploadVO;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.UserUploadVO;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.upload.HierarchyDownloadService;


@Component
public class HierarchyDownloadServiceImpl implements HierarchyDownloadService
{
    private static Logger LOG = LoggerFactory.getLogger( HierarchyDownloadServiceImpl.class );

    @Autowired
    private HierarchyUploadDao hierarchyUploadDao;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    private static int BATCH_SIZE = 50;

    @Autowired
    private RegionDao regionDao;

    @Resource
    @Qualifier ( "branch")
    private BranchDao branchDao;

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private UserDao userDao;


    /**
     * Method to update company hierarchy structure in mongo
     * @param company
     * @return
     * @throws InvalidInputException
     */
    @Override
    public HierarchyUpload fetchUpdatedHierarchyStructure( Company company ) throws InvalidInputException
    {
        LOG.info( "Method updateHierarchyStructure started for company : " + company.getCompany() );
        /* 
         * 1. fetch from mongo (oldHierarchyStructure)
         * 2. If empty go to step 5
         * 3. create current hierarchy upload object that reflects the current hierarchy structure.
         * 4. compare and update oldHierarchyStructure and currentHierarchyStructure)
         */
        HierarchyUpload oldHierarchyUpload = hierarchyUploadDao.getHierarchyUploadByCompany( company.getCompanyId() );
        // Generate new hierarchyupload
        HierarchyUpload currentHierarchyUpload = generateCurrentHierarchyStructure( company, oldHierarchyUpload );
        if ( oldHierarchyUpload != null ) {
            currentHierarchyUpload = aggregateHierarchyStructure( oldHierarchyUpload, currentHierarchyUpload );
        }
        hierarchyUploadDao.saveHierarchyUploadObject( currentHierarchyUpload );
        LOG.info( "Method updateHierarchyStructure finished for company : " + company.getCompany() );
        return currentHierarchyUpload;
    }


    /**
     * Method to aggregate hierarchy structure
     * @param oldHierarchyUpload
     * @param currentHierarchyUpload
     * @return
     * @throws InvalidInputException 
     */
    public HierarchyUpload aggregateHierarchyStructure( HierarchyUpload oldHierarchyUpload,
        HierarchyUpload currentHierarchyUpload ) throws InvalidInputException
    {
        LOG.info( "Method to aggregate hierarchy structure started" );
        if ( oldHierarchyUpload == null ) {
            throw new InvalidInputException( "OldHierarchyUpload object is empty" );
        }
        if ( currentHierarchyUpload == null ) {
            throw new InvalidInputException( "CurrentHierarchyUpload object is empty" );
        }

        HierarchyUpload newHierarchyUpload = oldHierarchyUpload;

        //First aggregate the region, branch and user source mappings
        aggregateSourceMaps( oldHierarchyUpload, currentHierarchyUpload, newHierarchyUpload );

        //Compare and aggregate regions
        aggregateRegionsStructure( oldHierarchyUpload.getRegions(), currentHierarchyUpload.getRegions(), newHierarchyUpload );

        //Compare and aggregate branches
        aggregateBranchesStructure( oldHierarchyUpload.getBranches(), currentHierarchyUpload.getBranches(), newHierarchyUpload );


        //Compare and aggregate users
        aggregateUsersStructure( oldHierarchyUpload.getUsers(), currentHierarchyUpload.getUsers(), newHierarchyUpload );

        LOG.info( "Method to aggregate hierarchy structure finished" );
        return newHierarchyUpload;
    }


    public void aggregateSourceMaps( HierarchyUpload oldHierarchyUpload, HierarchyUpload currentHierarchyUpload,
        HierarchyUpload newHierarchyUpload )
    {
        //Regions
        Map<String, Long> oldMap = oldHierarchyUpload.getRegionSourceMapping();
        Map<String, Long> newMap = newHierarchyUpload.getRegionSourceMapping();
        newMap.putAll( oldMap );
        newHierarchyUpload.setRegionSourceMapping( newMap );

        //Branches
        oldMap = oldHierarchyUpload.getBranchSourceMapping();
        newMap = newHierarchyUpload.getBranchSourceMapping();
        newMap.putAll( oldMap );
        newHierarchyUpload.setBranchSourceMapping( newMap );

        //Users
        oldMap = oldHierarchyUpload.getUserSourceMapping();
        newMap = newHierarchyUpload.getUserSourceMapping();
        newMap.putAll( oldMap );
        newHierarchyUpload.setUserSourceMapping( newMap );
    }


    /**
     * Method to aggregate Users structure
     * @param oldUsers
     * @param currentUsers
     * @param newHierarchyUpload
     */
    public void aggregateUsersStructure( List<UserUploadVO> oldUsers, List<UserUploadVO> currentUsers,
        HierarchyUpload newHierarchyUpload )
    {
        LOG.info( "Method to aggregate users structure started" );
        List<UserUploadVO> newUsers = new ArrayList<UserUploadVO>();

        Map<Long, UserUploadVO> oldUsersMap = new HashMap<Long, UserUploadVO>();
        //Get map from UserUploadVO
        for ( UserUploadVO userUploadVO : oldUsers ) {
            oldUsersMap.put( userUploadVO.getUserId(), userUploadVO );
        }

        /*
         * Things to check
         * 1. user addition
         * 2. user deletion
         */


        Map<Long, String> revMap = new HashMap<Long, String>();
        for ( String key : newHierarchyUpload.getUserSourceMapping().keySet() ) {
            revMap.put( newHierarchyUpload.getUserSourceMapping().get( key ), key );
        }

        Map<String, Long> regionMapping = newHierarchyUpload.getRegionSourceMapping();

        Map<Long, String> mappedRegion = new HashMap<Long, String>();
        for ( String key : regionMapping.keySet() ) {
            mappedRegion.put( regionMapping.get( key ), key );
        }

        Map<String, Long> branchMapping = newHierarchyUpload.getBranchSourceMapping();

        Map<Long, String> mappedBranch = new HashMap<Long, String>();
        for ( String key : branchMapping.keySet() ) {
            mappedBranch.put( branchMapping.get( key ), key );
        }

        Map<String, Long> userMapping = newHierarchyUpload.getUserSourceMapping();

        //Iterate through new list
        for ( UserUploadVO currentUser : currentUsers ) {
            UserUploadVO oldUser = oldUsersMap.get( currentUser.getUserId() );
            //If oldUser does not exist, then the currentUser is a new user
            if ( oldUser == null ) {
                currentUser.setSourceUserIdGenerated( true );
                newUsers.add( currentUser );
                //Add new users' sourceIds in the hierarchyupload
                userMapping.put( revMap.get( currentUser.getUserId() ), currentUser.getUserId() );
            } else {
                //Superimpose current on old and store in new list
                UserUploadVO amalgamatedUser = oldUser;
                //set sourceRegionId
                amalgamatedUser.setSourceRegionId( mappedRegion.get( amalgamatedUser.getRegionId() ) );

                //set sourceBranchId
                amalgamatedUser.setSourceBranchId( mappedBranch.get( amalgamatedUser.getBranchId() ) );

                amalgamatedUser.setFirstName( currentUser.getFirstName() );
                amalgamatedUser.setLastName( currentUser.getLastName() );
                amalgamatedUser.setTitle( currentUser.getTitle() );
                amalgamatedUser.setBranchId( currentUser.getBranchId() );
                amalgamatedUser.setRegionId( currentUser.getRegionId() );
                amalgamatedUser.setAgent( currentUser.isAgent() );
                amalgamatedUser.setEmailId( currentUser.getEmailId() );
                amalgamatedUser.setBelongsToCompany( currentUser.isBelongsToCompany() );
                amalgamatedUser.setBranchAdmin( currentUser.isBranchAdmin() );
                amalgamatedUser.setPhoneNumber( currentUser.getPhoneNumber() );
                amalgamatedUser.setWebsiteUrl( currentUser.getWebsiteUrl() );
                amalgamatedUser.setLicense( currentUser.getLicense() );
                amalgamatedUser.setLegalDisclaimer( currentUser.getLegalDisclaimer() );
                amalgamatedUser.setAboutMeDescription( currentUser.getAboutMeDescription() );
                amalgamatedUser.setUserPhotoUrl( currentUser.getUserPhotoUrl() );
                amalgamatedUser.setAssignedBranches( currentUser.getAssignedBranches() );
                amalgamatedUser.setAssignedBranchesAdmin( currentUser.getAssignedBranchesAdmin() );
                amalgamatedUser.setAssignedRegions( currentUser.getAssignedRegions() );
                amalgamatedUser.setAssignedRegionsAdmin( currentUser.getAssignedRegionsAdmin() );
                newUsers.add( amalgamatedUser );

                //Delete object entry from oldUsers
                oldUsers.remove( oldUser );
            }
        }

        if ( !( oldUsers.isEmpty() ) ) {
            //The remaining users are the ones that have been deleted
            //don't add these to the new list
            //Remove deleted users' sourceIds in the hierarchyupload map
            for ( UserUploadVO userUploadVO : oldUsers ) {
                userMapping.remove( userUploadVO.getSourceUserId() );
            }
            LOG.warn( "Some users have been deleted recently" );
        }

        newHierarchyUpload.setUserSourceMapping( userMapping );

        newHierarchyUpload.setUsers( newUsers );
        LOG.info( "Method to aggregate users structure finished" );
    }


    /**
     * Method to aggregate branches structure
     * @param oldBranches
     * @param currentBranches
     * @return
     */
    public void aggregateBranchesStructure( List<BranchUploadVO> oldBranches, List<BranchUploadVO> currentBranches,
        HierarchyUpload newHierarchyUpload )
    {
        LOG.info( "Method to aggregate branches structure started" );
        List<BranchUploadVO> newBranches = new ArrayList<BranchUploadVO>();

        Map<Long, BranchUploadVO> oldBranchesMap = new HashMap<Long, BranchUploadVO>();
        //Get map from BranchUploadVO
        for ( BranchUploadVO branchUploadVO : oldBranches ) {
            oldBranchesMap.put( branchUploadVO.getRegionId(), branchUploadVO );
        }

        Map<Long, String> revMap = new HashMap<Long, String>();
        for ( String key : newHierarchyUpload.getBranchSourceMapping().keySet() ) {
            revMap.put( newHierarchyUpload.getBranchSourceMapping().get( key ), key );
        }


        /*
         * Things to check
         * 1. branch addition
         * 2. branch deletion
         */

        Map<String, Long> regionMapping = newHierarchyUpload.getRegionSourceMapping();
        Map<Long, String> mappedRegion = new HashMap<Long, String>();
        for ( String key : regionMapping.keySet() ) {
            mappedRegion.put( regionMapping.get( key ), key );
        }

        Map<String, Long> branchMapping = newHierarchyUpload.getBranchSourceMapping();

        //Iterate through new list
        for ( BranchUploadVO currentBranch : currentBranches ) {
            BranchUploadVO oldBranch = oldBranchesMap.get( currentBranch.getBranchId() );
            //If oldBranch does not exist, then the currentBranch is a newly added branch
            if ( oldBranch == null ) {
                currentBranch.setSourceBranchIdGenerated( true );
                newBranches.add( currentBranch );
                //Add new branches' sourceIds in the hierarchyupload
                branchMapping.put( revMap.get( currentBranch.getBranchId() ), currentBranch.getBranchId() );

            } else {
                //Superimpose current on old and store in new list
                BranchUploadVO amalgamatedBranch = oldBranch;
                amalgamatedBranch.setRegionId( currentBranch.getRegionId() );
                amalgamatedBranch.setBranchName( currentBranch.getBranchName() );
                amalgamatedBranch.setBranchAddress1( currentBranch.getBranchAddress1() );
                amalgamatedBranch.setBranchAddress2( currentBranch.getBranchAddress2() );
                amalgamatedBranch.setBranchCountry( currentBranch.getBranchCountry() );
                amalgamatedBranch.setBranchCountryCode( currentBranch.getBranchCountryCode() );
                amalgamatedBranch.setBranchCity( currentBranch.getBranchCity() );
                amalgamatedBranch.setBranchZipcode( currentBranch.getBranchZipcode() );

                //set sourceRegionId
                amalgamatedBranch.setSourceRegionId( mappedRegion.get( amalgamatedBranch.getRegionId() ) );

                newBranches.add( amalgamatedBranch );

                //Delete object entry from oldRegions
                oldBranches.remove( oldBranch );
            }
        }

        if ( !( oldBranches.isEmpty() ) ) {
            //The remaining branches are the ones that have been deleted
            //don't add these to the new list
            //Remove deleted branches' sourceIds in the hierarchyupload map
            for ( BranchUploadVO branchUploadVO : oldBranches ) {
                regionMapping.remove( branchUploadVO.getSourceBranchId() );
            }
            LOG.warn( "Some branches have been deleted recently" );
        }

        newHierarchyUpload.setBranches( newBranches );
        newHierarchyUpload.setBranchSourceMapping( branchMapping );
        LOG.info( "Method to aggregate branches structure finished" );
    }


    /**
     * Method to aggregate region structure
     * @param oldRegions
     * @param currentRegions
     * @return
     */
    public void aggregateRegionsStructure( List<RegionUploadVO> oldRegions, List<RegionUploadVO> currentRegions,
        HierarchyUpload newHierarchyUpload )
    {
        LOG.info( "Method to aggregate regions structure started" );
        List<RegionUploadVO> newRegions = new ArrayList<RegionUploadVO>();

        Map<Long, RegionUploadVO> oldRegionsMap = new HashMap<Long, RegionUploadVO>();
        //Get map from RegionUploadVO
        for ( RegionUploadVO regionUploadVO : oldRegions ) {
            oldRegionsMap.put( regionUploadVO.getRegionId(), regionUploadVO );
        }

        Map<Long, String> revMap = new HashMap<Long, String>();
        for ( String key : newHierarchyUpload.getRegionSourceMapping().keySet() ) {
            revMap.put( newHierarchyUpload.getRegionSourceMapping().get( key ), key );
        }

        /*
         * Things to check
         * 1. region addition
         * 2. region deletion
         */

        Map<String, Long> regionMapping = newHierarchyUpload.getRegionSourceMapping();

        //Iterate through new list
        for ( RegionUploadVO currentRegion : currentRegions ) {
            RegionUploadVO oldRegion = oldRegionsMap.get( currentRegion.getRegionId() );
            //If oldRegion does not exist, then the currentRegion is a newly added region
            if ( oldRegion == null ) {
                currentRegion.setSourceRegionIdGenerated( true );
                newRegions.add( currentRegion );
                //Add new regions' sourceIds in the hierarchyupload map
                regionMapping.put( revMap.get( currentRegion.getRegionId() ), currentRegion.getRegionId() );

            } else {
                //Superimpose current on old and store in new list
                RegionUploadVO amalgamatedRegion = oldRegion;
                amalgamatedRegion.setRegionName( currentRegion.getRegionName() );
                amalgamatedRegion.setRegionAddress1( currentRegion.getRegionAddress1() );
                amalgamatedRegion.setRegionAddress2( currentRegion.getRegionAddress2() );
                amalgamatedRegion.setRegionCountry( currentRegion.getRegionCountry() );
                amalgamatedRegion.setRegionCountryCode( currentRegion.getRegionCountryCode() );
                amalgamatedRegion.setRegionCity( currentRegion.getRegionCity() );
                amalgamatedRegion.setRegionState( currentRegion.getRegionState() );
                amalgamatedRegion.setRegionZipcode( currentRegion.getRegionZipcode() );
                newRegions.add( amalgamatedRegion );

                //Delete object entry from oldRegions
                oldRegions.remove( oldRegion );
            }
        }

        if ( !( oldRegions.isEmpty() ) ) {
            //The remaining regions are the ones that have been deleted
            //don't add these to the new list
            //Remove deleted regions' sourceIds in the hierarchyupload map
            for ( RegionUploadVO regionUploadVO : oldRegions ) {
                regionMapping.remove( regionUploadVO.getSourceRegionId() );
            }
            LOG.warn( "Some regions have been deleted recently" );
        }

        newHierarchyUpload.setRegions( newRegions );
        newHierarchyUpload.setRegionSourceMapping( regionMapping );
        LOG.info( "Method to aggregate regions structure finished" );
    }


    public String generateSourceId( String entityType )
    {
        return entityType + String.valueOf( System.currentTimeMillis() );
    }


    /**
     * Method to generate current hierarchy structure for a company
     * @param company
     * @return
     * @throws InvalidInputException 
     */
    public HierarchyUpload generateCurrentHierarchyStructure( Company company, HierarchyUpload oldHierarchyUpload )
        throws InvalidInputException
    {
        LOG.info( "Method to generate current hierarchy structure for company : " + company.getCompany() + " started" );

        HierarchyUpload hierarchyUpload = new HierarchyUpload();

        //Set company Id
        hierarchyUpload.setCompanyId( company.getCompanyId() );

        //Set RegionVOs
        List<RegionUploadVO> regions = generateRegionUploadVOsForCompany( company, oldHierarchyUpload, hierarchyUpload );
        hierarchyUpload.setRegions( regions );

        //Set BranchVOs
        List<BranchUploadVO> branches = generateBranchUploadVOsForCompany( company, oldHierarchyUpload, hierarchyUpload );
        hierarchyUpload.setBranches( branches );

        //Set UserVOs

        //Generate maps of regionVOs and branchVOs
        Map<Long, RegionUploadVO> regionMap = new HashMap<Long, RegionUploadVO>();
        for ( RegionUploadVO regionUploadVO : regions ) {
            regionMap.put( regionUploadVO.getRegionId(), regionUploadVO );
        }

        Map<Long, BranchUploadVO> branchMap = new HashMap<Long, BranchUploadVO>();
        for ( BranchUploadVO branchUploadVO : branches ) {
            branchMap.put( branchUploadVO.getBranchId(), branchUploadVO );
        }

        List<UserUploadVO> users = generateUserUploadVOsForCompany( company, regionMap, branchMap, oldHierarchyUpload,
            hierarchyUpload );
        hierarchyUpload.setUsers( users );

        LOG.info( "Method to generate current hierarchy structure for company : " + company.getCompany() + " finished" );
        return hierarchyUpload;
    }


    /**
     * Method to generate UserUploadVOs for a company
     * @param company
     * @return
     * @throws InvalidInputException
     */
    public List<UserUploadVO> generateUserUploadVOsForCompany( Company company, Map<Long, RegionUploadVO> regionMap,
        Map<Long, BranchUploadVO> branchMap, HierarchyUpload oldHierarchyUpload, HierarchyUpload currentHierarchyUpload )
        throws InvalidInputException
    {
        LOG.info( "Method to generate user upload VOs for company : " + company.getCompany() + " started" );
        List<UserUploadVO> userVOs = new ArrayList<UserUploadVO>();

        Map<Long, String> oldSourceMap = null;
        if ( oldHierarchyUpload != null && oldHierarchyUpload.getUserSourceMapping() != null ) {
            oldSourceMap = new HashMap<Long, String>();
            for ( String key : oldHierarchyUpload.getUserSourceMapping().keySet() ) {
                oldSourceMap.put( oldHierarchyUpload.getUserSourceMapping().get( key ), key );
            }
        }
        Map<String, Long> newSourceMap = currentHierarchyUpload.getUserSourceMapping();
        if ( newSourceMap == null ) {
            newSourceMap = new HashMap<String, Long>();
        }

        int start = 0;
        List<User> batchUserList = new ArrayList<User>();
        do {
            batchUserList = userDao.getUsersForCompany( company, start, BATCH_SIZE );
            if ( batchUserList != null && batchUserList.size() > 0 ) {
                for ( User user : batchUserList ) {
                    UserUploadVO userUploadVO = generateUserUploadVOForUser( user, regionMap, branchMap, oldSourceMap,
                        newSourceMap );
                    userVOs.add( userUploadVO );
                }
            }
            start += BATCH_SIZE;
        } while ( batchUserList != null && batchUserList.size() > 0 );
        currentHierarchyUpload.setUserSourceMapping( newSourceMap );

        LOG.info( "Method to generate user upload VOs for company : " + company.getCompany() + " finished" );
        return userVOs;
    }


    /**
     * Method to get user upload VO for user
     * @param user
     * @return
     * @throws InvalidInputException
     */
    public UserUploadVO generateUserUploadVOForUser( User user, Map<Long, RegionUploadVO> regionMap,
        Map<Long, BranchUploadVO> branchMap, Map<Long, String> oldSourceMap, Map<String, Long> newSourceMap )
        throws InvalidInputException
    {
        if ( user == null ) {
            throw new InvalidInputException( "User is null" );
        }
        LOG.info( "Method to get user upload VO for user : " + user.getUsername() + " started" );

        //Get userSettings
        AgentSettings agentSettings;
        try {
            agentSettings = organizationManagementService.getAgentSettings( user.getUserId() );
        } catch ( NoRecordsFetchedException e ) {
            throw new InvalidInputException( "Agent Setting null for userId : " + user.getUserId() );
        }

        UserUploadVO userUploadVO = new UserUploadVO();
        userUploadVO.setUserId( user.getUserId() );
        userUploadVO.setFirstName( user.getFirstName() );
        if ( user.getLastName() != null && !( user.getLastName().isEmpty() ) ) {
            userUploadVO.setLastName( user.getLastName() );
        }

        userUploadVO.setBelongsToCompany( true );

        //Get list of branchIds, list of regionIds and isAgent
        List<UserProfile> userProfiles = user.getUserProfiles();
        List<String> assignedBranchSourceIds = new ArrayList<String>();
        List<String> assignedRegionSourceIds = new ArrayList<String>();
        List<String> assignedBranchesAdmin = new ArrayList<String>();
        List<String> assignedRegionsAdmin = new ArrayList<String>();

        if ( oldSourceMap == null || oldSourceMap.isEmpty() || !( oldSourceMap.containsKey( userUploadVO.getUserId() ) ) ) {
            String sourceId = generateSourceId( CommonConstants.USER_COLUMN );
            userUploadVO.setSourceUserId( sourceId );
            newSourceMap.put( sourceId, userUploadVO.getUserId() );
        } else {
            userUploadVO.setSourceUserId( oldSourceMap.get( userUploadVO.getUserId() ) );
        }
        for ( UserProfile userProfile : userProfiles ) {
            if ( userProfile.getStatus() == CommonConstants.STATUS_ACTIVE ) {
                if ( branchMap.containsKey( userProfile.getBranchId() ) ) {
                    //Add sourceId to list instead
                    assignedBranchSourceIds.add( branchMap.get( userProfile.getBranchId() ).getSourceBranchId() );


                    if ( userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID ) {
                        userUploadVO.setBranchAdmin( true );
                        assignedBranchesAdmin.add( branchMap.get( userProfile.getBranchId() ).getSourceBranchId() );
                    }
                }
                if ( regionMap.containsKey( userProfile.getRegionId() ) ) {
                    //Add sourceID to list
                    assignedRegionSourceIds.add( regionMap.get( userProfile.getRegionId() ).getSourceRegionId() );


                    if ( userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID ) {
                        userUploadVO.setRegionAdmin( true );
                        assignedRegionsAdmin.add( regionMap.get( userProfile.getRegionId() ).getSourceRegionId() );
                    }
                }
                if ( userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
                    userUploadVO.setAgent( true );
                }
            }
        }


        if ( agentSettings.getContact_details() != null ) {
            if ( agentSettings.getContact_details().getTitle() != null
                && !( agentSettings.getContact_details().getTitle().isEmpty() ) ) {
                userUploadVO.setTitle( agentSettings.getContact_details().getTitle() );
            }
            if ( agentSettings.getContact_details().getMail_ids() != null
                && agentSettings.getContact_details().getMail_ids().getWork() != null
                && !( agentSettings.getContact_details().getMail_ids().getWork().isEmpty() ) ) {
                userUploadVO.setEmailId( agentSettings.getContact_details().getMail_ids().getWork() );
            }
            if ( agentSettings.getContact_details().getContact_numbers() != null
                && agentSettings.getContact_details().getContact_numbers().getWork() != null
                && !( agentSettings.getContact_details().getContact_numbers().getWork().isEmpty() ) ) {
                userUploadVO.setPhoneNumber( agentSettings.getContact_details().getContact_numbers().getWork() );
            }
            if ( agentSettings.getContact_details().getWeb_addresses() != null
                && agentSettings.getContact_details().getWeb_addresses().getWork() != null
                && !( agentSettings.getContact_details().getWeb_addresses().getWork().isEmpty() ) ) {
                userUploadVO.setWebsiteUrl( agentSettings.getContact_details().getWeb_addresses().getWork() );
            }
            if ( agentSettings.getContact_details().getAbout_me() != null
                && !( agentSettings.getContact_details().getAbout_me().isEmpty() ) ) {
                userUploadVO.setAboutMeDescription( agentSettings.getContact_details().getAbout_me() );
            }
        }
        if ( agentSettings.getLicenses() != null ) {
            Licenses licenses = agentSettings.getLicenses();
            List<String> authorizedInList = licenses.getAuthorized_in();
            String authorizedIns = "";
            if ( authorizedInList != null && authorizedInList.size() > 0 ) {
                for ( String authorizedIn : authorizedInList ) {
                    authorizedIns += authorizedIn + ",";
                }
            }
            userUploadVO.setLicense( authorizedIns );
        }
        if ( agentSettings.getDisclaimer() != null && !( agentSettings.getDisclaimer().isEmpty() ) ) {
            userUploadVO.setLegalDisclaimer( agentSettings.getDisclaimer() );
        }
        if ( agentSettings.getProfileImageUrl() != null && !( agentSettings.getProfileImageUrl().isEmpty() ) ) {
            userUploadVO.setUserPhotoUrl( agentSettings.getProfileImageUrl() );
        }
        userUploadVO.setAssignedBranches( assignedBranchSourceIds );
        userUploadVO.setAssignedRegions( assignedRegionSourceIds );
        userUploadVO.setAssignedBranchesAdmin( assignedBranchesAdmin );
        userUploadVO.setAssignedRegionsAdmin( assignedRegionsAdmin );

        LOG.info( "Method to get user upload VO for user : " + user.getUsername() + " finished" );
        return userUploadVO;
    }


    /**
     * Method to generate BranchUploadVOs for a company
     * @param company
     * @return
     * @throws InvalidInputException
     */
    public List<BranchUploadVO> generateBranchUploadVOsForCompany( Company company, HierarchyUpload oldHierarchyUpload,
        HierarchyUpload currentHierarchyUpload ) throws InvalidInputException
    {

        LOG.info( "Method to generate branch upload VOs for company : " + company.getCompany() + " started" );
        List<BranchUploadVO> branchVOs = new ArrayList<BranchUploadVO>();

        Map<Long, String> oldSourceMap = null;
        if ( oldHierarchyUpload != null && oldHierarchyUpload.getBranchSourceMapping() != null ) {
            oldSourceMap = new HashMap<Long, String>();
            for ( String key : oldHierarchyUpload.getBranchSourceMapping().keySet() ) {
                oldSourceMap.put( oldHierarchyUpload.getBranchSourceMapping().get( key ), key );
            }
        }
        Map<String, Long> newSourceMap = currentHierarchyUpload.getBranchSourceMapping();
        if ( newSourceMap == null ) {
            newSourceMap = new HashMap<String, Long>();
        }


        int start = 0;
        List<Branch> batchBranchList = new ArrayList<Branch>();
        do {
            batchBranchList = branchDao.getBranchesForCompany( company.getCompanyId(), CommonConstants.NO, start, BATCH_SIZE );
            if ( batchBranchList != null && batchBranchList.size() > 0 )
                for ( Branch branch : batchBranchList ) {
                    if ( branch.getIsDefaultBySystem() == CommonConstants.IS_DEFAULT_BY_SYSTEM_YES ) {
                        continue;
                    }
                    BranchUploadVO branchUploadVO = generateBranchUploadVOForBranch( branch, oldSourceMap, newSourceMap );
                    branchVOs.add( branchUploadVO );
                }
            start += BATCH_SIZE;
        } while ( batchBranchList != null && batchBranchList.size() == BATCH_SIZE );
        currentHierarchyUpload.setBranchSourceMapping( newSourceMap );

        LOG.info( "Method to generate branch upload VOs for company : " + company.getCompany() + " finished" );
        return branchVOs;
    }


    /**
     * Method to get BranchUploadVO for branch
     * @param branch
     * @return
     * @throws InvalidInputException
     */
    public BranchUploadVO generateBranchUploadVOForBranch( Branch branch, Map<Long, String> oldSourceMap,
        Map<String, Long> newSourceMap ) throws InvalidInputException
    {
        if ( branch == null ) {
            throw new InvalidInputException( "Branch is null" );
        }
        LOG.info( "Method to get branch upload VO for branch : " + branch.getBranch() + " started" );

        //Get branchSettings
        OrganizationUnitSettings branchSettings;
        try {
            branchSettings = organizationManagementService.getBranchSettingsDefault( branch.getBranchId() );
        } catch ( NoRecordsFetchedException e ) {
            throw new InvalidInputException( "Branch settings is null for branch : " + branch.getBranchId() );
        }

        BranchUploadVO branchUploadVO = new BranchUploadVO();

        branchUploadVO.setBranchId( branch.getBranchId() );
        branchUploadVO.setRegionId( branch.getRegion().getRegionId() );
        branchUploadVO.setBranchName( branch.getBranch() );
        branchUploadVO.setAssignedRegionName( branch.getRegion().getRegion() );

        if ( branchSettings.getContact_details() != null ) {
            if ( branchSettings.getContact_details().getAddress1() != null
                && !( branchSettings.getContact_details().getAddress1().isEmpty() ) ) {
                branchUploadVO.setBranchAddress1( branchSettings.getContact_details().getAddress1() );
            }
            if ( branchSettings.getContact_details().getAddress2() != null
                && !( branchSettings.getContact_details().getAddress2().isEmpty() ) ) {
                branchUploadVO.setBranchAddress2( branchSettings.getContact_details().getAddress2() );
            }
            if ( branchSettings.getContact_details().getCountry() != null
                && !( branchSettings.getContact_details().getCountry().isEmpty() ) ) {
                branchUploadVO.setBranchCountry( branchSettings.getContact_details().getCountry() );
            }
            if ( branchSettings.getContact_details().getCountryCode() != null
                && !( branchSettings.getContact_details().getCountryCode().isEmpty() ) ) {
                branchUploadVO.setBranchCountryCode( branchSettings.getContact_details().getCountryCode() );
            }
            if ( branchSettings.getContact_details().getState() != null
                && !( branchSettings.getContact_details().getState().isEmpty() ) ) {
                branchUploadVO.setBranchState( branchSettings.getContact_details().getState() );
            }
            if ( branchSettings.getContact_details().getCity() != null
                && !( branchSettings.getContact_details().getCity().isEmpty() ) ) {
                branchUploadVO.setBranchCity( branchSettings.getContact_details().getCity() );
            }
            if ( branchSettings.getContact_details().getZipcode() != null
                && !( branchSettings.getContact_details().getZipcode().isEmpty() ) ) {
                branchUploadVO.setBranchZipcode( branchSettings.getContact_details().getZipcode() );
            }
            if ( oldSourceMap == null || oldSourceMap.isEmpty() || !( oldSourceMap.containsKey( branchUploadVO.getBranchId() ) ) ) {
                String sourceId = generateSourceId( CommonConstants.BRANCH_NAME_COLUMN );
                branchUploadVO.setSourceBranchId( sourceId );
                newSourceMap.put( sourceId, branchUploadVO.getBranchId() );
            } else {
                branchUploadVO.setSourceBranchId( oldSourceMap.get( branchUploadVO.getBranchId() ) );
            }
        }


        LOG.info( "Method to get branch upload VO for branch : " + branch.getBranch() + " finished" );
        return branchUploadVO;
    }


    /**
     * Method to generate RegionUploadVOs for a company
     * @param company
     * @return
     * @throws InvalidInputException 
     */
    public List<RegionUploadVO> generateRegionUploadVOsForCompany( Company company, HierarchyUpload oldHierarchyUpload,
        HierarchyUpload currentHierarchyUpload ) throws InvalidInputException
    {
        LOG.info( "Method to generate region upload VOs for comapny : " + company.getCompany() + " started" );
        List<RegionUploadVO> regionVOs = new ArrayList<RegionUploadVO>();

        //if oldHierarchyUpload is not null, generate a revMap and send it in getRegionUploadVOForRegion

        int start = 0;
        List<Region> batchRegionList = new ArrayList<Region>();
        Map<Long, String> oldSourceMap = null;
        if ( oldHierarchyUpload != null && oldHierarchyUpload.getRegionSourceMapping() != null ) {
            oldSourceMap = new HashMap<Long, String>();
            for ( String key : oldHierarchyUpload.getRegionSourceMapping().keySet() ) {
                oldSourceMap.put( oldHierarchyUpload.getRegionSourceMapping().get( key ), key );
            }
        }
        Map<String, Long> newSourceMap = currentHierarchyUpload.getRegionSourceMapping();
        if ( newSourceMap == null ) {
            newSourceMap = new HashMap<String, Long>();
        }
        do {
            batchRegionList = regionDao.getRegionsForCompany( company.getCompanyId(), start, BATCH_SIZE );
            if ( batchRegionList != null && batchRegionList.size() > 0 )
                for ( Region region : batchRegionList ) {
                    if ( region.getIsDefaultBySystem() == CommonConstants.IS_DEFAULT_BY_SYSTEM_YES ) {
                        continue;
                    }
                    RegionUploadVO regionUploadVO = getRegionUploadVOForRegion( region, oldSourceMap, newSourceMap );
                    regionVOs.add( regionUploadVO );
                }
            start += BATCH_SIZE;
        } while ( batchRegionList != null && batchRegionList.size() == BATCH_SIZE );
        currentHierarchyUpload.setRegionSourceMapping( newSourceMap );

        LOG.info( "Method to generate region upload VOs for comapny : " + company.getCompany() + " finished" );
        return regionVOs;
    }


    /**
     * Method to get RegionUploadVO for a region
     * @param region
     * @return
     * @throws InvalidInputException 
     */
    public RegionUploadVO getRegionUploadVOForRegion( Region region, Map<Long, String> oldSourceMap,
        Map<String, Long> newSourceMap ) throws InvalidInputException
    {
        if ( region == null ) {
            throw new InvalidInputException( "Region is null" );
        }
        LOG.info( "Method to get region upload VO for region : " + region.getRegion() + " started" );

        //Get regionSettings
        OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( region.getRegionId() );
        if ( regionSettings == null ) {
            throw new InvalidInputException( "Region settings is null" );
        }


        RegionUploadVO regionUploadVO = new RegionUploadVO();
        regionUploadVO.setRegionId( region.getRegionId() );
        regionUploadVO.setRegionName( region.getRegion() );
        if ( regionSettings.getContact_details() != null ) {
            if ( regionSettings.getContact_details().getAddress1() != null
                && !( regionSettings.getContact_details().getAddress1().isEmpty() ) ) {
                regionUploadVO.setRegionAddress1( regionSettings.getContact_details().getAddress1() );
            }

            if ( regionSettings.getContact_details().getAddress2() != null
                && !( regionSettings.getContact_details().getAddress2().isEmpty() ) ) {
                regionUploadVO.setRegionAddress2( regionSettings.getContact_details().getAddress2() );
            }

            if ( regionSettings.getContact_details().getCountry() != null
                && !( regionSettings.getContact_details().getCountry().isEmpty() ) ) {
                regionUploadVO.setRegionCountry( regionSettings.getContact_details().getCountry() );
            }

            if ( regionSettings.getContact_details().getCountryCode() != null
                && !( regionSettings.getContact_details().getCountryCode().isEmpty() ) ) {
                regionUploadVO.setRegionCountryCode( regionSettings.getContact_details().getCountryCode() );
            }

            if ( regionSettings.getContact_details().getCountryCode() != null
                && !( regionSettings.getContact_details().getCountryCode().isEmpty() ) ) {
                regionUploadVO.setRegionCountryCode( regionSettings.getContact_details().getCountryCode() );
            }

            if ( regionSettings.getContact_details().getState() != null
                && !( regionSettings.getContact_details().getState().isEmpty() ) ) {
                regionUploadVO.setRegionState( regionSettings.getContact_details().getState() );
            }

            if ( regionSettings.getContact_details().getCity() != null
                && !( regionSettings.getContact_details().getCity().isEmpty() ) ) {
                regionUploadVO.setRegionCity( regionSettings.getContact_details().getCity() );
            }

            if ( regionSettings.getContact_details().getZipcode() != null
                && !( regionSettings.getContact_details().getZipcode().isEmpty() ) ) {
                regionUploadVO.setRegionZipcode( regionSettings.getContact_details().getZipcode() );
            }
            if ( oldSourceMap == null || oldSourceMap.isEmpty() || !( oldSourceMap.containsKey( regionUploadVO.getRegionId() ) ) ) {
                String sourceId = generateSourceId( CommonConstants.REGION_COLUMN );
                regionUploadVO.setSourceRegionId( sourceId );
                newSourceMap.put( sourceId, regionUploadVO.getRegionId() );
            } else {
                regionUploadVO.setSourceRegionId( oldSourceMap.get( regionUploadVO.getRegionId() ) );
            }


        }

        LOG.info( "Method to get region upload VO for region : " + region.getRegion() + " finished" );
        return regionUploadVO;
    }


    /**
     * Method to generate Hierarchy Download Report
     * @param hierarchyUpload
     * @param company
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    @Override
    public XSSFWorkbook generateHierarchyDownloadReport( HierarchyUpload hierarchyUpload, Company company )
        throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.info( "Method to generate hierarchy download report started" );

        // Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        // Create a blank sheet
        XSSFSheet userSheet = workbook.createSheet( "Users" );
        XSSFSheet branchSheet = workbook.createSheet( "Offices" );
        XSSFSheet regionSheet = workbook.createSheet( "Regions" );
        Integer usersCounter = 1;
        Integer branchesCounter = 1;
        Integer regionsCounter = 1;

        // This data needs to be written (List<Object>)
        Map<Integer, List<Object>> usersData = new TreeMap<>();
        Map<Integer, List<Object>> branchesData = new TreeMap<>();
        Map<Integer, List<Object>> regionsData = new TreeMap<>();
        List<Object> userReportToPopulate = new ArrayList<>();
        List<Object> branchesReportToPopulate = new ArrayList<>();
        List<Object> regionsReportToPopulate = new ArrayList<>();


        List<UserUploadVO> userList = hierarchyUpload.getUsers();
        if ( userList != null && userList.size() > 0 ) {
            for ( UserUploadVO user : userList ) {
                // col 0 - user id
                // col 1 -  firstname
                // col 2 - last name
                // col 3 - title
                // col 4 - source branch ids 
                // col 5 - source region ids
                // col x - public page - Yes if user is an agent (REMOVED)
                // col 6 - branch ids where he is admin
                // col 7 - region ids where he is admin
                // col 8 - email
                // col 9 - phone
                // col 10 - website
                // col 11 - license
                // col 12 - legal disclaimer
                // col 13 - photo - profile image url
                // col 14 - about me
                userReportToPopulate.add( user.getUserId() );
                userReportToPopulate.add( user.getFirstName() );
                if ( user.getLastName() != null && !user.getLastName().trim().equalsIgnoreCase( "" )
                    && !user.getLastName().trim().equalsIgnoreCase( "null" ) )
                    userReportToPopulate.add( user.getLastName() );
                else
                    userReportToPopulate.add( "" );
                if ( user.getTitle() != null && !( user.getTitle().isEmpty() ) ) {
                    userReportToPopulate.add( user.getTitle() );
                } else {
                    userReportToPopulate.add( "" );
                }

                if ( user.getAssignedBranches() != null && !( user.getAssignedBranches().isEmpty() ) ) {
                    userReportToPopulate.add( user.getAssignedBranches().toString().replace( "[", "" ).replace( "]", "" ) );
                } else {
                    userReportToPopulate.add( "" );
                }

                if ( user.getAssignedRegions() != null && !( user.getAssignedRegions().isEmpty() ) ) {
                    userReportToPopulate.add( user.getAssignedRegions().toString().replace( "[", "" ).replace( "]", "" ) );
                } else {
                    userReportToPopulate.add( "" );
                }

                if ( user.getAssignedBranchesAdmin() != null && !( user.getAssignedBranchesAdmin().isEmpty() ) ) {
                    userReportToPopulate.add( user.getAssignedBranchesAdmin().toString().replace( "[", "" ).replace( "]", "" ) );
                } else {
                    userReportToPopulate.add( "" );
                }

                if ( user.getAssignedRegionsAdmin() != null && !( user.getAssignedRegionsAdmin().isEmpty() ) ) {
                    userReportToPopulate.add( user.getAssignedRegionsAdmin().toString().replace( "[", "" ).replace( "]", "" ) );
                } else {
                    userReportToPopulate.add( "" );
                }
                
                userReportToPopulate.add( user.getEmailId() );

                if ( user.getPhoneNumber() != null && !( user.getPhoneNumber().isEmpty() ) ) {
                    userReportToPopulate.add( user.getPhoneNumber() );
                } else {
                    userReportToPopulate.add( "" );
                }

                if ( user.getWebsiteUrl() != null && !( user.getWebsiteUrl().isEmpty() ) ) {
                    userReportToPopulate.add( user.getWebsiteUrl() );
                } else {
                    userReportToPopulate.add( "" );
                }

                if ( user.getLicense() != null && !( user.getLicense().isEmpty() ) ) {
                    userReportToPopulate.add( user.getLicense() );
                } else {
                    userReportToPopulate.add( "" );
                }

                if ( user.getLegalDisclaimer() != null && !( user.getLegalDisclaimer().isEmpty() ) ) {
                    userReportToPopulate.add( user.getLegalDisclaimer() );
                } else {
                    userReportToPopulate.add( "" );
                }

                if ( user.getUserPhotoUrl() != null && !( user.getUserPhotoUrl().isEmpty() ) ) {
                    userReportToPopulate.add( user.getUserPhotoUrl() );
                } else {
                    userReportToPopulate.add( "" );
                }

                if ( user.getAboutMeDescription() != null && !( user.getAboutMeDescription().isEmpty() ) ) {
                    userReportToPopulate.add( user.getAboutMeDescription() );
                } else {
                    userReportToPopulate.add( "" );
                }

                usersData.put( ( ++usersCounter ), userReportToPopulate );
                userReportToPopulate = new ArrayList<>();
            }
        }

        // Setting up user sheet headers
        userReportToPopulate.add( CommonConstants.CHR_USERS_USER_ID );
        userReportToPopulate.add( CommonConstants.CHR_USERS_FIRST_NAME );
        userReportToPopulate.add( CommonConstants.CHR_USERS_LAST_NAME );
        userReportToPopulate.add( CommonConstants.CHR_USERS_TITLE );
        userReportToPopulate.add( CommonConstants.CHR_USERS_OFFICE_ASSIGNMENTS );
        userReportToPopulate.add( CommonConstants.CHR_USERS_REGION_ASSIGNMENTS );
        userReportToPopulate.add( CommonConstants.CHR_USERS_OFFICE_ADMIN_PRIVILEGE );
        userReportToPopulate.add( CommonConstants.CHR_USERS_REGION_ADMIN_PRIVILEGE );
        userReportToPopulate.add( CommonConstants.CHR_USERS_EMAIL );
        userReportToPopulate.add( CommonConstants.CHR_USERS_PHONE );
        userReportToPopulate.add( CommonConstants.CHR_USERS_WEBSITE );
        userReportToPopulate.add( CommonConstants.CHR_USERS_LICENSE );
        userReportToPopulate.add( CommonConstants.CHR_USERS_LEGAL_DISCLAIMER );
        userReportToPopulate.add( CommonConstants.CHR_USERS_PHOTO );
        userReportToPopulate.add( CommonConstants.CHR_USERS_ABOUT_ME_DESCRIPTION );

        usersData.put( 1, userReportToPopulate );

        List<BranchUploadVO> branchList = hierarchyUpload.getBranches();
        // loop on branches to populate branches sheet
        if ( branchList != null && branchList.size() > 0 ) {
            for ( BranchUploadVO branch : branchList ) {
                // col 0 office id
                // col 1 office name
                // col 2 region id
                // col 3 address 1
                // col 4 address 2
                // col 5 city
                // col 6 state
                // col 7 zip

                branchesReportToPopulate.add( branch.getBranchId() );
                branchesReportToPopulate.add( branch.getBranchName() );
                branchesReportToPopulate.add( branch.getRegionId() );
                branchesReportToPopulate.add( branch.getBranchAddress1() );
                if ( branch.getBranchAddress2() != null && !( branch.getBranchAddress2().isEmpty() ) ) {
                    branchesReportToPopulate.add( branch.getBranchAddress2() );
                } else {
                    branchesReportToPopulate.add( "" );
                }
                branchesReportToPopulate.add( branch.getBranchCity() );
                branchesReportToPopulate.add( branch.getBranchState() );
                branchesReportToPopulate.add( branch.getBranchZipcode() );

                branchesData.put( ( ++branchesCounter ), branchesReportToPopulate );
                branchesReportToPopulate = new ArrayList<>();
            }
        }

        // Setting up branch sheet headers
        branchesReportToPopulate.add( CommonConstants.CHR_BRANCH_BRANCH_ID );
        branchesReportToPopulate.add( CommonConstants.CHR_BRANCH_BRANCH_NAME );
        branchesReportToPopulate.add( CommonConstants.CHR_REGION_REGION_ID );
        branchesReportToPopulate.add( CommonConstants.CHR_ADDRESS_1 );
        branchesReportToPopulate.add( CommonConstants.CHR_ADDRESS_2 );
        branchesReportToPopulate.add( CommonConstants.CHR_CITY );
        branchesReportToPopulate.add( CommonConstants.CHR_STATE );
        branchesReportToPopulate.add( CommonConstants.CHR_ZIP );

        branchesData.put( 1, branchesReportToPopulate );

        List<RegionUploadVO> regionList = hierarchyUpload.getRegions();
        // loop on region to populate region sheet
        if ( regionList != null && regionList.size() > 0 ) {
            for ( RegionUploadVO region : regionList ) {
                // col 0 region id
                // col 1 region name
                // col 2 address 1
                // col 3 address 2
                // col 4 city
                // col 5 state
                // col 6 zip
                regionsReportToPopulate.add( region.getRegionId() );
                regionsReportToPopulate.add( region.getRegionName() );
                regionsReportToPopulate.add( region.getRegionAddress1() );
                if ( region.getRegionAddress2() != null && !( region.getRegionAddress2().isEmpty() ) ) {
                    regionsReportToPopulate.add( region.getRegionAddress2() );
                } else {
                    regionsReportToPopulate.add( "" );
                }
                regionsReportToPopulate.add( region.getRegionCity() );
                regionsReportToPopulate.add( region.getRegionState() );
                regionsReportToPopulate.add( region.getRegionZipcode() );

                regionsData.put( ( ++regionsCounter ), regionsReportToPopulate );
                regionsReportToPopulate = new ArrayList<>();
            }
        }

        // Setting up branch sheet headers
        regionsReportToPopulate.add( CommonConstants.CHR_REGION_REGION_ID );
        regionsReportToPopulate.add( CommonConstants.CHR_REGION_REGION_NAME );
        regionsReportToPopulate.add( CommonConstants.CHR_ADDRESS_1 );
        regionsReportToPopulate.add( CommonConstants.CHR_ADDRESS_2 );
        regionsReportToPopulate.add( CommonConstants.CHR_CITY );
        regionsReportToPopulate.add( CommonConstants.CHR_STATE );
        regionsReportToPopulate.add( CommonConstants.CHR_ZIP );

        regionsData.put( 1, regionsReportToPopulate );


        // Iterate over data and write to sheet
        Set<Integer> keyset = usersData.keySet();

        int rownum = 0;
        for ( Integer key : keyset ) {
            Row row = userSheet.createRow( rownum++ );
            List<Object> objArr = usersData.get( key );
            int cellnum = 0;
            for ( Object obj : objArr ) {
                Cell cell = row.createCell( cellnum++ );
                if ( obj instanceof String )
                    cell.setCellValue( (String) obj );
                if ( obj instanceof Long )
                    cell.setCellValue( String.valueOf( (Long) obj ) );
            }
        }
        // Iterate over data and write to sheet
        keyset = branchesData.keySet();

        rownum = 0;
        for ( Integer key : keyset ) {
            Row row = branchSheet.createRow( rownum++ );
            List<Object> objArr = branchesData.get( key );
            int cellnum = 0;
            for ( Object obj : objArr ) {
                Cell cell = row.createCell( cellnum++ );
                if ( obj instanceof String )
                    cell.setCellValue( (String) obj );
                if ( obj instanceof Long )
                    cell.setCellValue( String.valueOf( (Long) obj ) );
            }
        }
        // Iterate over data and write to sheet
        keyset = regionsData.keySet();

        rownum = 0;
        for ( Integer key : keyset ) {
            Row row = regionSheet.createRow( rownum++ );
            List<Object> objArr = regionsData.get( key );
            int cellnum = 0;
            for ( Object obj : objArr ) {
                Cell cell = row.createCell( cellnum++ );
                if ( obj instanceof String )
                    cell.setCellValue( (String) obj );
                if ( obj instanceof Long )
                    cell.setCellValue( String.valueOf( (Long) obj ) );
            }
        }
        LOG.info( "Method to generate hierarchy download report started" );
        return workbook;
    }
}
