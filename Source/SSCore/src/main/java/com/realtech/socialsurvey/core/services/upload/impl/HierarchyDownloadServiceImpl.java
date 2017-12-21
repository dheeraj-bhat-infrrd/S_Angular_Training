package com.realtech.socialsurvey.core.services.upload.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.HashBiMap;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.HierarchyUploadDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.RegionDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.BranchUploadVO;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.HierarchyUpload;
import com.realtech.socialsurvey.core.entities.HierarchyUploadAggregate;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.RegionUploadVO;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.UserUploadVO;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.upload.HierarchyDownloadService;


@Component
public class HierarchyDownloadServiceImpl implements HierarchyDownloadService
{
    private static Logger LOG = LoggerFactory.getLogger( HierarchyDownloadServiceImpl.class );

    @Autowired
    private HierarchyUploadDao hierarchyUploadDao;

    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;

    private static int BATCH_SIZE = 50;

    @Autowired
    private RegionDao regionDao;

    @Resource
    @Qualifier ( "branch")
    private BranchDao branchDao;

    @Autowired
    private UserDao userDao;

    @Value ( "${MASK_EMAIL_ADDRESS}")
    private String maskEmail;

    @Autowired
    private Utils utils;

    @Autowired
    private UserProfileDao userProfileDao;

    private static char TYPE_USER = 'U';
    private static char TYPE_BRANCH = 'O';
    private static char TYPE_REGION = 'R';


    /**
     * Method to update company hierarchy structure in mongoDB
     * @param company
     * @return HierarchyUploadAggregate
     * @throws InvalidInputException
     */
    @Override
    @Transactional
    public HierarchyUploadAggregate fetchUpdatedHierarchyUploadStructure( Company company ) throws InvalidInputException
    {
        LOG.debug( "Method fetchUpdatedHierarchyUploadStructure started for company : " + company.getCompany() );

        // updating of hierarchy upload is done to retain the old source Id values and field histories
        // while generating new ones for new entities added for the company from the UI 

        /* 
         * 1. fetch from mongoDB Hierarchy upload collection (oldHierarchyStructure)
         * 2. build the current hierarchy structure 
         * 3. update the current entities with sourceId, histories and other necessary data from the old hierarchy structure
         * 4. remove the old structure from mongoDB and insert the new HierarchyUpload structure
         */

        // step 1
        HierarchyUpload oldHierarchyUpload = hierarchyUploadDao.getHierarchyUploadByCompany( company.getCompanyId() );

        // steps 2 and 3
        HierarchyUploadAggregate hierarchyUploadAggregate = generateUpdatedHierarchyStructure( company, oldHierarchyUpload );

        // step 4
        hierarchyUploadDao.reinsertHierarchyUploadObjectForACompany( hierarchyUploadAggregate.getHierarchyUpload() );

        LOG.debug( "Method fetchUpdatedHierarchyUploadStructure finished for company : " + company.getCompany() );
        return hierarchyUploadAggregate;
    }


    /**
     * Method to generate the updated company hierarchy structure using the old one in mongoDB
     * @param company
     * @param oldHierarchyUpload
     * @param doGenerateSourceIdMaps 
     * @return
     * @throws InvalidInputException 
     */
    public HierarchyUploadAggregate generateUpdatedHierarchyStructure( Company company, HierarchyUpload oldHierarchyUpload )
        throws InvalidInputException
    {
        LOG.debug( "Method generateUpdatedHierarchyStructure started for company : " + company.getCompany() );

        // in order to build the updated hierarchy Upload( HU ) object,
        // create a new HU object, add all the regions, branches and users for the company present currently active.
        // for all the entities present in the old hierarchy structure if sourceId, field histories, etc are found,
        // then update the newly added entity with these values while adding the entities to the new HU object.

        /* 
         * 1. create new HU object and set the company ID for the HU
         * 2. parse all the old hierarchy entities and build the HierarchyUploadAggregate object
         * 3. while parsing new entities, region, branch or user ( in that order ),
         *      a. check if the old hierarchy upload has data on the entity
         *      b. update the new entity with mapping and history present in the old data
         * 4. set all the data generated to the new HU object   
         */


        // creating new instances of HierarchyUpload and HierarchyUploadAggregate objects
        HierarchyUpload newHierarchyUpload = new HierarchyUpload();
        HierarchyUploadAggregate hierarchyAggregate = new HierarchyUploadAggregate();

        // setting the company identifier for the aggregate object
        hierarchyAggregate.setCompany( company );

        if ( oldHierarchyUpload != null ) {

            // creating region, branches and user maps for old hierarchy upload data : BEGIN
            if ( oldHierarchyUpload.getRegions() != null && !oldHierarchyUpload.getRegions().isEmpty() ) {
                hierarchyAggregate.setOldRegionUploadVOMap( new HashMap<Long, RegionUploadVO>() );
                hierarchyAggregate.setOldRegionSourceMapping( HashBiMap.create( new HashMap<String, Long>() ) );
                for ( RegionUploadVO region : oldHierarchyUpload.getRegions() ) {
                    hierarchyAggregate.getOldRegionUploadVOMap().put( region.getRegionId(), region );
                    hierarchyAggregate.getOldRegionSourceMapping().forcePut( region.getSourceRegionId(), region.getRegionId() );
                }
            }

            if ( oldHierarchyUpload.getBranches() != null && !oldHierarchyUpload.getBranches().isEmpty() ) {
                hierarchyAggregate.setOldBranchUploadVOMap( new HashMap<Long, BranchUploadVO>() );
                hierarchyAggregate.setOldBranchSourceMapping( HashBiMap.create( new HashMap<String, Long>() ) );
                for ( BranchUploadVO branch : oldHierarchyUpload.getBranches() ) {
                    hierarchyAggregate.getOldBranchUploadVOMap().put( branch.getBranchId(), branch );
                    hierarchyAggregate.getOldBranchSourceMapping().forcePut( branch.getSourceBranchId(), branch.getBranchId() );
                }
            }

            if ( oldHierarchyUpload.getUsers() != null && !oldHierarchyUpload.getUsers().isEmpty() ) {
                hierarchyAggregate.setOldUserUploadVOMap( new HashMap<Long, UserUploadVO>() );
                hierarchyAggregate.setOldUserSourceMapping( HashBiMap.create( new HashMap<String, Long>() ) );
                for ( UserUploadVO user : oldHierarchyUpload.getUsers() ) {
                    hierarchyAggregate.getOldUserUploadVOMap().put( user.getUserId(), user );
                    hierarchyAggregate.getOldUserSourceMapping().forcePut( user.getSourceUserId(), user.getUserId() );
                }
            }
            // creating region, branches and user maps for old hierarchy upload data : END
        }


        // building and adding hierarchy data using old hierarchy object to the aggregate object : BEGIN
        buildAndAddRegionsAndRegionSourceMappingForCompany( hierarchyAggregate );
        buildAndAddBranchesAndBranchSourceMappingForCompany( hierarchyAggregate );
        buildAndAddUsersAndUserSourceMappingForCompany( hierarchyAggregate );
        // building and adding hierarchy data using old hierarchy object to the aggregate object : END


        // set the new hierarchy data produced : BEGIN
        // setting company identifier for the new upload object
        newHierarchyUpload.setCompanyId( company.getCompanyId() );

        // setting region, branch, user value objects  
        if ( hierarchyAggregate.getNewRegionUploadVOMap() != null ) {
            newHierarchyUpload
                .setRegions( new ArrayList<RegionUploadVO>( hierarchyAggregate.getNewRegionUploadVOMap().values() ) );
        }

        if ( hierarchyAggregate.getNewBranchUploadVOMap() != null ) {
            newHierarchyUpload
                .setBranches( new ArrayList<BranchUploadVO>( hierarchyAggregate.getNewBranchUploadVOMap().values() ) );
        }

        if ( hierarchyAggregate.getNewUserUploadVOMap() != null ) {
            newHierarchyUpload.setUsers( new ArrayList<UserUploadVO>( hierarchyAggregate.getNewUserUploadVOMap().values() ) );
        }

        // set the new hierarchy data produced : END

        // add the new upload reference
        hierarchyAggregate.setHierarchyUpload( newHierarchyUpload );

        LOG.debug( "Method generateUpdatedHierarchyStructure finished for company : " + company.getCompany() );
        return hierarchyAggregate;
    }


    /**
     *  method to build and add the necessary mappings and value objects for regions
     * @param hierarchyAggregate
     * @throws InvalidInputException
     */
    private void buildAndAddRegionsAndRegionSourceMappingForCompany( HierarchyUploadAggregate hierarchyAggregate )
        throws InvalidInputException
    {
        LOG.debug( "Method buildAndAddRegionsAndRegionSourceMappingForCompany started for company with ID: "
            + hierarchyAggregate.getCompany().getCompanyId() );

        int start = 0;
        List<Long> batchRegionIdList = null;

        // build and add hierarchy data in batches for the non-default regions currently active in the company
        // NOTE: data is obtained from mongoDB only ( REGION SETTINGS - Collection )
        do {
            batchRegionIdList = regionDao.getRegionIdsUnderCompany( hierarchyAggregate.getCompany().getCompanyId(), start,
                BATCH_SIZE );

            // utilizing region setting list and building the region mappings and Value Object list
            if ( batchRegionIdList != null && batchRegionIdList.size() > 0 ) {

                buildAndAddRegionsAndRegionSourceMappingInBatch(
                    organizationUnitSettingsDao.fetchOrganizationUnitSettingsForMultipleIds(
                        new HashSet<Long>( batchRegionIdList ), CommonConstants.REGION_SETTINGS_COLLECTION ),
                    hierarchyAggregate );
            }

            start += BATCH_SIZE;
        } while ( batchRegionIdList != null && batchRegionIdList.size() == BATCH_SIZE );

        LOG.debug( "Method buildAndAddRegionsAndRegionSourceMappingForCompany finished for company with ID: "
            + hierarchyAggregate.getCompany().getCompanyId() );
    }


    /**
     * method to build and add the necessary mappings and value objects for branches
     * @param hierarchyAggregate
     * @throws InvalidInputException
     */
    private void buildAndAddBranchesAndBranchSourceMappingForCompany( HierarchyUploadAggregate hierarchyAggregate )
        throws InvalidInputException
    {
        LOG.debug( "Method buildAndAddBranchesAndBranchSourceMappingForCompany started for company with ID: "
            + hierarchyAggregate.getCompany().getCompanyId() );

        int start = 0;
        List<Branch> batchBranchesList = null;

        // build and add hierarchy data in batches for the non-default branches currently active in the company
        // NOTE: data is obtained from MySQL database ( ss_user ) and mongoDB ( BRANCH SETTINGS - Collection )
        do {

            // amalgamated data from both MySQL and MongoDB
            batchBranchesList = branchDao.getBranchesForCompany( hierarchyAggregate.getCompany().getCompanyId(),
                CommonConstants.NO, start, BATCH_SIZE );

            // utilizing branch object list and building the branch mappings and Value Object list
            if ( batchBranchesList != null && batchBranchesList.size() > 0 ) {

                buildAndAddBranchesAndBranchSourceMappingInBatch( batchBranchesList, hierarchyAggregate );
            }

            start += BATCH_SIZE;
        } while ( batchBranchesList != null && batchBranchesList.size() == BATCH_SIZE );


        LOG.debug( "Method buildAndAddBranchesAndBranchSourceMappingForCompany finished for company with ID: "
            + hierarchyAggregate.getCompany().getCompanyId() );
    }


    /**
     * method to build and add the necessary mappings and value objects for users
     * @param hierarchyAggregate
     * @throws InvalidInputException
     */
    private void buildAndAddUsersAndUserSourceMappingForCompany( HierarchyUploadAggregate hierarchyAggregate )
        throws InvalidInputException
    {
        LOG.debug( "Method buildAndAddUsersAndUserSourceMappingForCompany started for company with ID: "
            + hierarchyAggregate.getCompany().getCompanyId() );

        int start = 0;
        List<User> batchUsersList = null;

        // build and add hierarchy data in batches for the users in the company
        // NOTE: data is obtained from MySQL database ( ss_user ) and mongoDB ( AGENT SETTINGS - Collection )

        /* Data Set needed
         * 1. User object from USERS table
         * 2. UserProfile object from USER_PROFILE table
         * 3. Agent Settings Documents from mongoDB
         * */

        do {

            List<Long> batchUserIds = null;
            Map<Long, List<UserProfile>> batchUserAndProfileMap = null;
            batchUsersList = userDao.getUsersForCompany( hierarchyAggregate.getCompany(), start, BATCH_SIZE );

            if ( batchUsersList != null && batchUsersList.size() > 0 ) {

                // creating batch user List of identifiers
                batchUserIds = new ArrayList<>();
                for ( User user : batchUsersList )
                    batchUserIds.add( user.getUserId() );

                // obtaining user profile map for the current batch of users using custom HQL query
                batchUserAndProfileMap = userProfileDao.getUserProfilesForUsers( batchUserIds );

                // obtaining agent settings map for the current batch of users
                Map<Long, OrganizationUnitSettings> agentSettingsMap = buildOrganizationUnitSettingsIDMap( batchUserIds );

                // utilizing user object list, user profile map, agent setting map and building the user mappings and Value Object list
                buildAndAddUsersAndUserSourceMappingInBatch( batchUsersList, batchUserAndProfileMap, agentSettingsMap,
                    hierarchyAggregate );

            }

            start += BATCH_SIZE;
        } while ( batchUsersList != null && batchUsersList.size() == BATCH_SIZE );

        LOG.debug( "Method buildAndAddUsersAndUserSourceMappingForCompany finished for company with ID: "
            + hierarchyAggregate.getCompany().getCompanyId() );
    }


    /**
     * method to add mapping and VO for regions
     * @param regionSettingsList
     * @param hierarchyAggregate
     * @throws InvalidInputException
     */
    private void buildAndAddRegionsAndRegionSourceMappingInBatch( List<OrganizationUnitSettings> regionSettingsList,
        HierarchyUploadAggregate hierarchyAggregate ) throws InvalidInputException
    {
        LOG.debug( "Method buildAndAddRegionsAndRegionSourceMappingInBatch started" );

        if ( regionSettingsList == null || regionSettingsList.isEmpty() ) {
            throw new InvalidInputException( "region settings list for the current batch is non-existent" );
        }


        // setting up the new mapping and VO containers
        if ( hierarchyAggregate.getNewRegionSourceMapping() == null ) {
            hierarchyAggregate.setNewRegionSourceMapping( HashBiMap.create( new HashMap<String, Long>() ) );
        }

        if ( hierarchyAggregate.getNewRegionUploadVOMap() == null ) {
            hierarchyAggregate.setNewRegionUploadVOMap( new HashMap<Long, RegionUploadVO>() );
        }

        hierarchyAggregate.setRegionUploadVOMap( new HashMap<String, RegionUploadVO>() );
        hierarchyAggregate.setRegionNameMap( new HashMap<String, String>() );

        // processing all the regions in the current batch
        for ( OrganizationUnitSettings regionSettings : regionSettingsList ) {

            String sourceId = "";
            RegionUploadVO oldRegionVO = null;
            RegionUploadVO regionUploadVO = new RegionUploadVO();

            // initializing old region VO
            if ( hierarchyAggregate.getOldRegionUploadVOMap() != null
                && hierarchyAggregate.getOldRegionUploadVOMap().containsKey( regionSettings.getIden() ) ) {
                oldRegionVO = hierarchyAggregate.getOldRegionUploadVOMap().get( regionSettings.getIden() );
            }

            // processing source id for the current region
            // if the old hierarchy data has a source id then retain it else generate new
            if ( hierarchyAggregate.getOldRegionSourceMapping() != null
                && hierarchyAggregate.getOldRegionSourceMapping().containsValue( regionSettings.getIden() ) ) {
                sourceId = hierarchyAggregate.getOldRegionSourceMapping().inverse().get( regionSettings.getIden() );
            } else {
                sourceId = generateSourceId( TYPE_REGION, regionSettings.getIden() );
            }


            // fill up the region VO
            regionUploadVO.setSourceRegionId( sourceId );
            regionUploadVO.setRegionId( regionSettings.getIden() );

            if ( regionSettings.getContact_details() != null ) {

                regionUploadVO.setRegionName( StringUtils.defaultString( regionSettings.getContact_details().getName() ) );
                regionUploadVO
                    .setRegionAddress1( StringUtils.defaultString( regionSettings.getContact_details().getAddress1() ) );
                regionUploadVO
                    .setRegionAddress2( StringUtils.defaultString( regionSettings.getContact_details().getAddress2() ) );
                regionUploadVO
                    .setRegionCountry( StringUtils.defaultString( regionSettings.getContact_details().getCountry() ) );
                regionUploadVO
                    .setRegionCountryCode( StringUtils.defaultString( regionSettings.getContact_details().getCountryCode() ) );
                regionUploadVO
                    .setRegionCountryCode( StringUtils.defaultString( regionSettings.getContact_details().getCountryCode() ) );
                regionUploadVO.setRegionState( StringUtils.defaultString( regionSettings.getContact_details().getState() ) );
                regionUploadVO.setRegionCity( StringUtils.defaultString( regionSettings.getContact_details().getCity() ) );
                regionUploadVO
                    .setRegionZipcode( StringUtils.defaultString( regionSettings.getContact_details().getZipcode() ) );
            }


            // load the histories into the new VO from the old VO
            if ( oldRegionVO != null ) {
                regionUploadVO.setRegionNameHistory(
                    oldRegionVO.getRegionNameHistory() != null ? oldRegionVO.getRegionNameHistory() : null );
                regionUploadVO.setRegionAddress1History(
                    oldRegionVO.getRegionAddress1History() != null ? oldRegionVO.getRegionAddress1History() : null );
                regionUploadVO.setRegionAddress2History(
                    oldRegionVO.getRegionAddress2History() != null ? oldRegionVO.getRegionAddress2History() : null );
                regionUploadVO.setRegionCountryHistory(
                    oldRegionVO.getRegionCountryHistory() != null ? oldRegionVO.getRegionCountryHistory() : null );
                regionUploadVO.setRegionCountryCodeHistory(
                    oldRegionVO.getRegionCountryCodeHistory() != null ? oldRegionVO.getRegionCountryCodeHistory() : null );
                regionUploadVO.setRegionStateHistory(
                    oldRegionVO.getRegionStateHistory() != null ? oldRegionVO.getRegionStateHistory() : null );
                regionUploadVO.setRegionCityHistory(
                    oldRegionVO.getRegionCityHistory() != null ? oldRegionVO.getRegionCityHistory() : null );
                regionUploadVO.setRegionZipcodeHistory(
                    oldRegionVO.getRegionZipcodeHistory() != null ? oldRegionVO.getRegionZipcodeHistory() : null );
            }

            // finally add the mapping and new VO to the hierarchy containers
            // add to SourceId Map
            hierarchyAggregate.getRegionUploadVOMap().put( regionUploadVO.getSourceRegionId(), regionUploadVO );

            // name map
            hierarchyAggregate.getRegionNameMap().put( regionUploadVO.getSourceRegionId(), regionUploadVO.getRegionName() );

            // add to internal Id Map
            hierarchyAggregate.getNewRegionSourceMapping().put( regionUploadVO.getSourceRegionId(),
                regionUploadVO.getRegionId() );

            // internal id map
            hierarchyAggregate.getNewRegionUploadVOMap().put( regionUploadVO.getRegionId(), regionUploadVO );

        }


        LOG.debug( "Method buildAndAddRegionsAndRegionSourceMappingInBatch finished" );
    }


    /**
     * method to add mapping and VO for branches
     * @param batchBranchesList
     * @param hierarchyAggregate
     * @throws InvalidInputException
     */
    private void buildAndAddBranchesAndBranchSourceMappingInBatch( List<Branch> batchBranchesList,
        HierarchyUploadAggregate hierarchyAggregate ) throws InvalidInputException
    {
        LOG.debug( "Method buildAndAddBranchesAndBranchSourceMappingInBatch started" );
        if ( batchBranchesList == null ) {
            throw new InvalidInputException( "branch list for the current batch is non-existent" );
        }


        // setting up the new mapping and VO containers
        if ( hierarchyAggregate.getNewBranchSourceMapping() == null ) {
            hierarchyAggregate.setNewBranchSourceMapping( HashBiMap.create( new HashMap<String, Long>() ) );
        }

        if ( hierarchyAggregate.getNewBranchUploadVOMap() == null ) {
            hierarchyAggregate.setNewBranchUploadVOMap( new HashMap<Long, BranchUploadVO>() );
        }

        hierarchyAggregate.setBranchUploadVOMap( new HashMap<String, BranchUploadVO>() );
        hierarchyAggregate.setBranchNameMap( new HashMap<String, String>() );

        // processing all the branches in the current batch
        for ( Branch branch : batchBranchesList ) {

            String sourceId = "";
            BranchUploadVO oldBranchVO = null;
            BranchUploadVO branchUploadVO = new BranchUploadVO();


            // processing source id for the current branch
            // if the old hierarchy data has a source id then retain it else generate new
            if ( hierarchyAggregate.getOldBranchSourceMapping() != null
                && hierarchyAggregate.getOldBranchSourceMapping().containsValue( branch.getBranchId() ) ) {
                sourceId = hierarchyAggregate.getOldBranchSourceMapping().inverse().get( branch.getBranchId() );
            } else {
                sourceId = generateSourceId( TYPE_BRANCH, branch.getBranchId() );
            }


            // initializing old branch VO
            if ( hierarchyAggregate.getOldBranchUploadVOMap() != null
                && hierarchyAggregate.getOldBranchUploadVOMap().containsKey( branch.getBranchId() ) ) {
                oldBranchVO = hierarchyAggregate.getOldBranchUploadVOMap().get( branch.getBranchId() );
            }


            // fill up the branch VO
            branchUploadVO.setSourceBranchId( sourceId );
            branchUploadVO.setBranchId( branch.getBranchId() );
            branchUploadVO.setRegionId( branch.getRegion().getRegionId() );
            branchUploadVO.setBranchName( StringUtils.defaultString( branch.getBranch() ) );
            branchUploadVO.setBranchAddress1( StringUtils.defaultString( branch.getAddress1() ) );
            branchUploadVO.setBranchAddress2( StringUtils.defaultString( branch.getAddress2() ) );
            branchUploadVO.setBranchCountry( StringUtils.defaultString( branch.getCountry() ) );
            branchUploadVO.setBranchCountryCode( StringUtils.defaultString( branch.getCountryCode() ) );
            branchUploadVO.setBranchState( StringUtils.defaultString( branch.getState() ) );
            branchUploadVO.setBranchCity( StringUtils.defaultString( branch.getCity() ) );
            branchUploadVO.setBranchZipcode( StringUtils.defaultString( branch.getZipcode() ) );

            if ( hierarchyAggregate.getNewRegionUploadVOMap() != null
                && hierarchyAggregate.getNewRegionUploadVOMap().containsKey( branchUploadVO.getRegionId() ) ) {
                branchUploadVO.setSourceRegionId(
                    hierarchyAggregate.getNewRegionUploadVOMap().get( branchUploadVO.getRegionId() ).getSourceRegionId() );
            }


            // load the histories into the new VO from the old VO
            if ( oldBranchVO != null ) {
                branchUploadVO
                    .setRegionIdHistory( oldBranchVO.getRegionIdHistory() != null ? oldBranchVO.getRegionIdHistory() : null );
                branchUploadVO.setBranchNameHistory(
                    oldBranchVO.getBranchNameHistory() != null ? oldBranchVO.getBranchNameHistory() : null );
                branchUploadVO.setBranchAddress1History(
                    oldBranchVO.getBranchAddress1History() != null ? oldBranchVO.getBranchAddress1History() : null );
                branchUploadVO.setBranchAddress2History(
                    oldBranchVO.getBranchAddress2History() != null ? oldBranchVO.getBranchAddress2History() : null );
                branchUploadVO.setBranchCountryHistory(
                    oldBranchVO.getBranchCountryHistory() != null ? oldBranchVO.getBranchCountryHistory() : null );
                branchUploadVO.setBranchCountryCodeHistory(
                    oldBranchVO.getBranchCountryCodeHistory() != null ? oldBranchVO.getBranchCountryCodeHistory() : null );
                branchUploadVO.setBranchStateHistory(
                    oldBranchVO.getBranchStateHistory() != null ? oldBranchVO.getBranchStateHistory() : null );
                branchUploadVO.setBranchCityHistory(
                    oldBranchVO.getBranchCityHistory() != null ? oldBranchVO.getBranchCityHistory() : null );
                branchUploadVO.setBranchZipcodeHistory(
                    oldBranchVO.getBranchZipcodeHistory() != null ? oldBranchVO.getBranchZipcodeHistory() : null );
                branchUploadVO.setSourceRegionIdHistory(
                    oldBranchVO.getSourceRegionIdHistory() != null ? oldBranchVO.getSourceRegionIdHistory() : null );
            }

            // finally add the mapping and new VO to the new hierarchy containers
            // add to soruceId Maps
            hierarchyAggregate.getBranchUploadVOMap().put( branchUploadVO.getSourceBranchId(), branchUploadVO );

            // name map
            hierarchyAggregate.getBranchNameMap().put( branchUploadVO.getSourceBranchId(), branchUploadVO.getBranchName() );

            // add to internal and source Id Map
            hierarchyAggregate.getNewBranchSourceMapping().put( branchUploadVO.getSourceBranchId(),
                branchUploadVO.getBranchId() );

            // internal id map
            hierarchyAggregate.getNewBranchUploadVOMap().put( branchUploadVO.getBranchId(), branchUploadVO );

        }

        LOG.debug( "Method buildAndAddBranchesAndBranchSourceMappingInBatch finished" );

    }


    /**
     * method to add mapping and VO for users
     * @param batchUsersList
     * @param batchUserAndProfileMap
     * @param agentSettingsMap
     * @param hierarchyAggregate
     * @throws InvalidInputException
     */
    private void buildAndAddUsersAndUserSourceMappingInBatch( List<User> batchUsersList,
        Map<Long, List<UserProfile>> batchUserAndProfileMap, Map<Long, OrganizationUnitSettings> agentSettingsMap,
        HierarchyUploadAggregate hierarchyAggregate ) throws InvalidInputException
    {
        LOG.debug( "Method buildAndAddUsersAndUserSourceMappingInBatch started" );

        if ( batchUsersList == null ) {
            throw new InvalidInputException( "user list for the current batch is non-existent" );
        }


        // setting up the new mapping and VO containers
        if ( hierarchyAggregate.getNewUserSourceMapping() == null ) {
            hierarchyAggregate.setNewUserSourceMapping( HashBiMap.create( new HashMap<String, Long>() ) );
        }

        if ( hierarchyAggregate.getNewUserUploadVOMap() == null ) {
            hierarchyAggregate.setNewUserUploadVOMap( new HashMap<Long, UserUploadVO>() );
        }

        hierarchyAggregate.setUserUploadVOMap( new HashMap<String, UserUploadVO>() );


        // processing all the users in the current batch
        for ( User user : batchUsersList ) {

            String sourceId = "";
            UserUploadVO oldUserVO = null;
            UserUploadVO userUploadVO = new UserUploadVO();
            Set<String> assignedBranchSourceIds = new HashSet<String>();
            Set<String> assignedRegionSourceIds = new HashSet<String>();
            Set<String> assignedBranchesAdmin = new HashSet<String>();
            Set<String> assignedRegionsAdmin = new HashSet<String>();


            // obtaining old VO, list of user profiles and agent settings for the current user
            if ( hierarchyAggregate.getOldUserUploadVOMap() != null
                && hierarchyAggregate.getOldUserUploadVOMap().containsKey( user.getUserId() ) )
                oldUserVO = hierarchyAggregate.getOldUserUploadVOMap().get( user.getUserId() );

            List<UserProfile> userProfiles = batchUserAndProfileMap.get( user.getUserId() );
            OrganizationUnitSettings agentSettings = agentSettingsMap.get( user.getUserId() );


            // processing source id for the current user
            // if the old hierarchy data has a source id then retain it else generate new
            if ( hierarchyAggregate.getOldUserSourceMapping() != null
                && hierarchyAggregate.getOldUserSourceMapping().containsValue( user.getUserId() ) ) {
                sourceId = hierarchyAggregate.getOldUserSourceMapping().inverse().get( user.getUserId() );
            } else {
                sourceId = generateSourceId( TYPE_USER, user.getUserId() );
            }


            // populating the agent and administrator assignments lists for region and branches using the list of user profiles
            for ( UserProfile userProfile : userProfiles ) {
                if ( userProfile.getStatus() == CommonConstants.STATUS_ACTIVE ) {

                    if ( hierarchyAggregate.getNewBranchUploadVOMap() != null
                        && hierarchyAggregate.getNewBranchUploadVOMap().containsKey( userProfile.getBranchId() ) ) {

                        BranchUploadVO branchVO = hierarchyAggregate.getNewBranchUploadVOMap().get( userProfile.getBranchId() );

                        switch ( userProfile.getProfilesMaster().getProfileId() ) {

                            case CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID:
                                assignedBranchSourceIds.add( branchVO.getSourceBranchId() );
                                break;
                            case CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID:
                                assignedBranchesAdmin.add( branchVO.getSourceBranchId() );
                        }

                    } else if ( hierarchyAggregate.getNewRegionUploadVOMap() != null
                        && hierarchyAggregate.getNewRegionUploadVOMap().containsKey( userProfile.getRegionId() ) ) {

                        RegionUploadVO regionVO = hierarchyAggregate.getNewRegionUploadVOMap().get( userProfile.getRegionId() );

                        switch ( userProfile.getProfilesMaster().getProfileId() ) {

                            case CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID:
                                assignedRegionSourceIds.add( regionVO.getSourceRegionId() );
                                break;
                            case CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID:
                                assignedRegionsAdmin.add( regionVO.getSourceRegionId() );
                        }
                    }

                }
            }


            // fill up the user VO
            userUploadVO.setSourceUserId( sourceId );
            userUploadVO.setUserId( user.getUserId() );
            userUploadVO.setFirstName( StringUtils.defaultString( user.getFirstName() ) );
            userUploadVO.setLastName( StringUtils.defaultString( user.getLastName() ) );

            if ( agentSettings.getContact_details() != null ) {

                userUploadVO.setTitle( StringUtils.defaultString( agentSettings.getContact_details().getTitle() ) );
                userUploadVO.setEmailId( CommonConstants.YES_STRING.equals( maskEmail )
                    ? utils.unmaskEmailAddress( user.getEmailId() ) : user.getEmailId() );
                userUploadVO
                    .setAboutMeDescription( StringUtils.defaultString( agentSettings.getContact_details().getAbout_me() ) );


                if ( agentSettings.getContact_details().getContact_numbers() != null )
                    userUploadVO.setPhoneNumber(
                        StringUtils.defaultString( agentSettings.getContact_details().getContact_numbers().getWork() ) );

                if ( agentSettings.getContact_details().getWeb_addresses() != null )
                    userUploadVO.setWebsiteUrl(
                        StringUtils.defaultString( agentSettings.getContact_details().getWeb_addresses().getWork() ) );

            }

            if ( agentSettings.getLicenses() != null && agentSettings.getLicenses().getAuthorized_in() != null &&  agentSettings.getLicenses().getAuthorized_in().size() > 0 )
                userUploadVO.setLicense( StringUtils.defaultString( agentSettings.getLicenses().getAuthorized_in().get( 0 ) ) );
            else{
                userUploadVO.setLicense("");
            }

            userUploadVO.setLegalDisclaimer( StringUtils.defaultString( agentSettings.getDisclaimer() ) );
            userUploadVO.setUserPhotoUrl( StringUtils.defaultString( agentSettings.getProfileImageUrl() ) );
            userUploadVO.setAssignedBranches( assignedBranchSourceIds );
            userUploadVO.setAssignedRegions( assignedRegionSourceIds );
            userUploadVO.setAssignedBranchesAdmin( assignedBranchesAdmin );
            userUploadVO.setAssignedRegionsAdmin( assignedRegionsAdmin );
            userUploadVO.setUserVerified( user.getStatus() == CommonConstants.STATUS_NOT_VERIFIED ? false : true );


            // load the histories into the new VO from the old VO
            if ( oldUserVO != null ) {
                userUploadVO.setFirstNameHistory( oldUserVO.getFirstNameHistory() );
                userUploadVO.setLastNameHistory( oldUserVO.getLastNameHistory() );
                userUploadVO.setTitleHistory( oldUserVO.getTitleHistory() );
                userUploadVO.setEmailIdHistory( oldUserVO.getEmailIdHistory() );
                userUploadVO.setPhoneNumberHistory( oldUserVO.getPhoneNumberHistory() );
                userUploadVO.setWebsiteUrlHistory( oldUserVO.getWebsiteUrlHistory() );
                userUploadVO.setLicenseHistory( oldUserVO.getLicenseHistory() );
                userUploadVO.setLegalDisclaimerHistory( oldUserVO.getLegalDisclaimerHistory() );
                userUploadVO.setAboutMeDescriptionHistory( oldUserVO.getAboutMeDescriptionHistory() );
                userUploadVO.setUserPhotoUrlHistory( oldUserVO.getUserPhotoUrlHistory() );
                userUploadVO.setAssignedBranchesHistory( oldUserVO.getAssignedBranchesHistory() );
                userUploadVO.setAssignedBranchesAdminHistory( oldUserVO.getAssignedBranchesAdminHistory() );
                userUploadVO.setAssignedRegionsHistory( oldUserVO.getAssignedRegionsHistory() );
                userUploadVO.setAssignedRegionsAdminHistory( oldUserVO.getAssignedRegionsAdminHistory() );
            }

            // finally add the mapping and new VO to the new hierarchy containers                
            // add to soruceId Map
            hierarchyAggregate.getUserUploadVOMap().put( userUploadVO.getSourceUserId(), userUploadVO );

            // add to internal and source Id Map
            hierarchyAggregate.getNewUserSourceMapping().put( userUploadVO.getSourceUserId(), user.getUserId() );

            // internal Id map
            hierarchyAggregate.getNewUserUploadVOMap().put( userUploadVO.getUserId(), userUploadVO );

        }

        LOG.debug( "Method buildAndAddUsersAndUserSourceMappingInBatch finished" );

    }

    private Map<Long, OrganizationUnitSettings> buildOrganizationUnitSettingsIDMap( List<Long> batchUserIds )
    {
        Map<Long, OrganizationUnitSettings> agentSettingsMap = new HashMap<>();
        for ( OrganizationUnitSettings unitSettings : organizationUnitSettingsDao.fetchOrganizationUnitSettingsForMultipleIds(
            new HashSet<>( batchUserIds ), CommonConstants.AGENT_SETTINGS_COLLECTION ) ) {
            agentSettingsMap.put( unitSettings.getIden(), unitSettings );
        }

        return agentSettingsMap;
    }


    public String generateSourceId( char entityType, long iden )
    {
        return entityType + Integer.toHexString( String.valueOf( System.currentTimeMillis() ).hashCode() )
            + Integer.toHexString( String.valueOf( iden ).hashCode() );
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
    @Transactional
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
                // col 15 - send email ( empty )
                userReportToPopulate.add( user.getSourceUserId() );
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
                    userReportToPopulate
                        .add( user.getAssignedBranchesAdmin().toString().replace( "[", "" ).replace( "]", "" ) );
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
        userReportToPopulate.add( CommonConstants.CHR_USERS_SEND_EMAIL );

        usersData.put( 1, userReportToPopulate );

        List<BranchUploadVO> branchList = hierarchyUpload.getBranches();
        // loop on branches to populate branches sheet
        if ( branchList != null && branchList.size() > 0 ) {
            for ( BranchUploadVO branch : branchList ) {
                // col 0 office id
                // col 1 office name
                // col 2 source region id
                // col 3 address 1
                // col 4 address 2
                // col 5 city
                // col 6 state
                // col 7 zip

                branchesReportToPopulate.add( branch.getSourceBranchId() );
                branchesReportToPopulate.add( branch.getBranchName() );
                branchesReportToPopulate.add( branch.getSourceRegionId() );
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
                regionsReportToPopulate.add( region.getSourceRegionId() );
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
