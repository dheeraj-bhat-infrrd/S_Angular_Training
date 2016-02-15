package com.realtech.socialsurvey.core.services.upload.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.HierarchyUpload;
import com.realtech.socialsurvey.core.entities.LongUploadHistory;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.RegionUploadVO;
import com.realtech.socialsurvey.core.entities.StringUploadHistory;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.RegionAdditionException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.upload.HierarchyStructureUploadService;


@Component
public class HierarchyStructureUploadServiceImpl implements HierarchyStructureUploadService
{

    private static Logger LOG = LoggerFactory.getLogger( HierarchyStructureUploadServiceImpl.class );

    @Autowired
    private OrganizationManagementService organizationManagementService;


    @Override
    public void uploadHierarchy( HierarchyUpload upload, Company company, User user ) throws InvalidInputException
    {
        // the upload object should have the current value as well the changes made by the user in the sheet/ UI
        if ( upload == null ) {
            LOG.error( "No upload object to upload." );
            throw new InvalidInputException( "No upload object to upload." );
        }
        if ( company == null ) {
            LOG.error( "No company object to upload." );
            throw new InvalidInputException( "No company object to upload." );
        }
        if ( user == null ) {
            LOG.error( "Invalid user details to upload." );
            throw new InvalidInputException( "Invalid user details to upload." );
        }
        if ( !user.isCompanyAdmin() ) {
            LOG.error( "User is not authorized to upload hierarchy." );
            throw new InvalidInputException( "User is not authorized to upload hierarchy." );
        }
        LOG.info( "Uploading hierarchy for company " + upload.getCompanyId() );
        // start with addition and modification of each unit starting from the highest hierarchy and then deletion starting from the lowest hierarchy
        // uploading new regions
        uploadRegions( upload, user, company );
    }


    private void uploadRegions( HierarchyUpload upload, User user, Company company )
    {
        LOG.debug( "Uploading new regions." );
        List<RegionUploadVO> regionsToBeUploaded = upload.getRegions();
        if ( regionsToBeUploaded != null && !regionsToBeUploaded.isEmpty() ) {
            Region region = null;
            for ( RegionUploadVO regionUpload : regionsToBeUploaded ) {

                // create the region. add the field to history for all fields as its new region and map source id to the id mapping list
                try {
                    if ( regionUpload.isRegionAdded() ) {
                        region = createRegion( user, regionUpload );
                        regionUpload.setRegionId( region.getRegionId() );
                    } else {
                        //TODO: process modified records
                    }
                    // map the history records
                    mapRegionModificationHistory( regionUpload, region );
                    // map the id mapping
                    upload.getRegionSourceMapping().put( regionUpload.getSourceRegionId(), region.getRegionId() );
                } catch ( InvalidInputException | SolrException e ) {
                    // TODO: Add error records
                    e.printStackTrace();
                }
            }
        }
    }


    private RegionUploadVO mapRegionModificationHistory( RegionUploadVO regionUpload, Region region )
    {
        LOG.debug( "mapping region history" );
        Timestamp currentTimestamp = new Timestamp( System.currentTimeMillis() );
        // map region id history
        if ( regionUpload.isRegionAdded() || regionUpload.isRegionIdModified() ) {
            List<LongUploadHistory> regionIdHistoryList = regionUpload.getRegionIdHistory();
            if ( regionIdHistoryList == null ) {
                regionIdHistoryList = new ArrayList<LongUploadHistory>();
            }
            LongUploadHistory regionIdHistory = new LongUploadHistory();
            regionIdHistory.setValue( region.getRegionId() );
            regionIdHistory.setTime( currentTimestamp );
            regionIdHistoryList.add( regionIdHistory );
            regionUpload.setRegionIdHistory( regionIdHistoryList );
        }

        // map source region id history
        if ( regionUpload.isRegionAdded() || regionUpload.isSourceRegionIdModified() ) {
            List<StringUploadHistory> sourceIdHistoryList = regionUpload.getSourceRegionIdHistory();
            if ( sourceIdHistoryList == null ) {
                sourceIdHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory sourceIdHistory = new StringUploadHistory();
            sourceIdHistory.setValue( regionUpload.getSourceRegionId() );
            sourceIdHistory.setTime( currentTimestamp );
            sourceIdHistoryList.add( sourceIdHistory );
            regionUpload.setSourceRegionIdHistory( sourceIdHistoryList );
        }

        // map region name history
        if ( regionUpload.isRegionAdded() || regionUpload.isSourceRegionIdModified() ) {
            List<StringUploadHistory> regionNameHistoryList = null;
            if ( regionNameHistoryList == null ) {
                regionNameHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory regionNameHistory = new StringUploadHistory();
            regionNameHistory.setValue( regionUpload.getRegionName() );
            regionNameHistory.setTime( currentTimestamp );
            regionNameHistoryList.add( regionNameHistory );
            regionUpload.setRegionNameHistory( regionNameHistoryList );
        }

        // map region address 1 history
        if ( regionUpload.isRegionAdded() || regionUpload.isRegionAddress1Modified() ) {
            List<StringUploadHistory> regionAddress1HistoryList = null;
            if ( regionAddress1HistoryList == null ) {
                regionAddress1HistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory regionAddress1History = new StringUploadHistory();
            regionAddress1History.setValue( regionUpload.getRegionAddress1() );
            regionAddress1History.setTime( currentTimestamp );
            regionAddress1HistoryList.add( regionAddress1History );
            regionUpload.setRegionAddress1History( regionAddress1HistoryList );
        }

        // map region address 2 history
        if ( regionUpload.isRegionAdded() || regionUpload.isRegionAddress2Modified() ) {
            List<StringUploadHistory> regionAddress2HistoryList = null;
            if ( regionAddress2HistoryList == null ) {
                regionAddress2HistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory regionAddress2History = new StringUploadHistory();
            regionAddress2History.setValue( regionUpload.getRegionAddress2() );
            regionAddress2History.setTime( currentTimestamp );
            regionAddress2HistoryList.add( regionAddress2History );
            regionUpload.setRegionAddress2History( regionAddress2HistoryList );
        }

        // map city history
        if ( regionUpload.isRegionAdded() || regionUpload.isRegionCityModified() ) {
            List<StringUploadHistory> regionCityHistoryList = null;
            if ( regionCityHistoryList == null ) {
                regionCityHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory regionCityHistory = new StringUploadHistory();
            regionCityHistory.setValue( regionUpload.getRegionCity() );
            regionCityHistory.setTime( currentTimestamp );
            regionCityHistoryList.add( regionCityHistory );
            regionUpload.setRegionCityHistory( regionCityHistoryList );
        }

        // map state history
        if ( regionUpload.isRegionAdded() || regionUpload.isRegionStateModified() ) {
            List<StringUploadHistory> regionStateHistoryList = null;
            if ( regionStateHistoryList == null ) {
                regionStateHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory regionStateHistory = new StringUploadHistory();
            regionStateHistory.setValue( regionUpload.getRegionState() );
            regionStateHistory.setTime( currentTimestamp );
            regionStateHistoryList.add( regionStateHistory );
            regionUpload.setRegionStateHistory( regionStateHistoryList );
        }

        // map zip history
        if ( regionUpload.isRegionAdded() || regionUpload.isRegionZipcodeModified() ) {
            List<StringUploadHistory> regionZipCodeHistoryList = null;
            if ( regionZipCodeHistoryList == null ) {
                regionZipCodeHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory regionZipCodeHistory = new StringUploadHistory();
            regionZipCodeHistory.setValue( regionUpload.getRegionZipcode() );
            regionZipCodeHistory.setTime( currentTimestamp );
            regionZipCodeHistoryList.add( regionZipCodeHistory );
            regionUpload.setRegionZipcodeHistory( regionZipCodeHistoryList );
        }
        return regionUpload;
    }


    /**
     * Creates a region
     * 
     * @param adminUser
     * @param region
     * @throws InvalidInputException
     * @throws RegionAdditionException
     * @throws SolrException
     */
    @Transactional
    Region createRegion( User adminUser, RegionUploadVO region ) throws InvalidInputException, SolrException
    {
        Region newRegion = null;
        if ( adminUser == null ) {
            LOG.error( "admin user parameter is null!" );
            throw new InvalidInputException( "admin user parameter is null!" );
        }
        if ( region == null ) {
            LOG.error( "region parameter is null!" );
            throw new InvalidInputException( "region parameter is null!" );
        }
        LOG.info( "createRegion called to add region : " + region.getRegionName() );

        LOG.debug( "Adding region : " + region.getRegionName() );
        newRegion = organizationManagementService.addNewRegion( adminUser, region.getRegionName(), CommonConstants.NO,
            region.getRegionAddress1(), region.getRegionAddress2(), region.getRegionCountry(), region.getRegionCountryCode(),
            region.getRegionState(), region.getRegionCity(), region.getRegionZipcode() );
        organizationManagementService.addNewBranch( adminUser, newRegion.getRegionId(), CommonConstants.YES,
            CommonConstants.DEFAULT_BRANCH_NAME, null, null, null, null, null, null, null );
        return newRegion;
    }
}
