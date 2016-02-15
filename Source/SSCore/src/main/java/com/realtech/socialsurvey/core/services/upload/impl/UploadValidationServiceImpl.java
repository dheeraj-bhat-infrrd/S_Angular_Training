package com.realtech.socialsurvey.core.services.upload.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.entities.BranchUploadVO;
import com.realtech.socialsurvey.core.entities.HierarchyUpload;
import com.realtech.socialsurvey.core.entities.RegionUploadVO;
import com.realtech.socialsurvey.core.entities.UploadValidation;
import com.realtech.socialsurvey.core.entities.UserUploadVO;
import com.realtech.socialsurvey.core.services.upload.UploadValidationService;


@Component
public class UploadValidationServiceImpl implements UploadValidationService
{

    static Logger LOG = LoggerFactory.getLogger( UploadValidationServiceImpl.class );


    @Override
    public void validateHeirarchyUpload( UploadValidation validationObject )
    {
        validateRegions( validationObject );
        validateBranches( validationObject );
        validateUsers( validationObject );
    }


    void validateRegions( UploadValidation validationObject )
    {
        List<String> regionValidationErrors = new ArrayList<String>();
        for ( RegionUploadVO uploadedRegion : validationObject.getUpload().getRegions() ) {
            validateRegionForErrors( uploadedRegion, regionValidationErrors );
        }
        validationObject.setRegionValidationErrors( regionValidationErrors );
    }


    void validateBranches( UploadValidation validationObject )
    {
        List<String> branchValidationErrors = new ArrayList<String>();
        List<String> branchValidationWarnings = new ArrayList<String>();
        for ( BranchUploadVO uploadedBranch : validationObject.getUpload().getBranches() ) {
            validateBranchForErrors( uploadedBranch, branchValidationErrors, validationObject.getUpload() );
            validateBranchForWarnings( uploadedBranch, branchValidationWarnings );
        }
        validationObject.setBranchValidationErrors( branchValidationErrors );
        validationObject.setBranchValidationWarnings( branchValidationWarnings );
    }


    void validateUsers( UploadValidation validationObject )
    {
        List<String> userValidationErrors = new ArrayList<String>();
        List<String> userValidationWarnings = new ArrayList<String>();
        for ( UserUploadVO uploadeduser : validationObject.getUpload().getUsers() ) {
            validateUserForErrors( uploadeduser, userValidationErrors, validationObject.getUpload() );
            validateUserForWarnings( uploadeduser, userValidationWarnings );
        }
        validationObject.setUserValidationErrors( userValidationErrors );
        validationObject.setUserValidationWarnings( userValidationWarnings );
    }


    void validateRegionForErrors( RegionUploadVO uploadedRegion, List<String> regionValidationErrors )
    {
        boolean errorRecord = false;
        if ( uploadedRegion.getSourceRegionId() == null || uploadedRegion.getSourceRegionId().isEmpty() ) {
            LOG.error( "Source Region Id at row: " + uploadedRegion.getRowNum() + " is not provided" );
            regionValidationErrors.add( "Source Id at row: " + uploadedRegion.getRowNum() + " is not provided" );
            errorRecord = true;
        }
        if ( uploadedRegion.getRegionName() == null || uploadedRegion.getRegionName().isEmpty() ) {
            LOG.error( "Region name at row: " + uploadedRegion.getRowNum() + " is not provided" );
            regionValidationErrors.add( "Region name at row: " + uploadedRegion.getRowNum() + " is not provided" );
            errorRecord = true;
        }

        if ( errorRecord ) {
            uploadedRegion.setErrorRecord( true );
        }
    }


    void validateBranchForErrors( BranchUploadVO uploadedBranch, List<String> branchValidationErrors, HierarchyUpload upload )
    {
        boolean errorRecord = false;
        if ( uploadedBranch.getSourceBranchId() == null || uploadedBranch.getSourceBranchId().isEmpty() ) {
            LOG.error( "Source Id at row: " + uploadedBranch.getRowNum() + " is not provided" );
            branchValidationErrors.add( "Source Id at row: " + uploadedBranch.getRowNum() + " is not provided" );
            errorRecord = true;
        }
        if ( uploadedBranch.getBranchName() == null || uploadedBranch.getBranchName().isEmpty() ) {
            LOG.error( "Office name at row: " + uploadedBranch.getRowNum() + " is not provided" );
            branchValidationErrors.add( "Office name at row: " + uploadedBranch.getRowNum() + " is not provided" );
            errorRecord = true;
        }
        if ( !uploadedBranch.isAddressSet() ) {
            LOG.error( "Office address at row: " + uploadedBranch.getRowNum() + " is not provided" );
            branchValidationErrors.add( "Office address at row: " + uploadedBranch.getRowNum() + " is not provided" );
            errorRecord = true;
        }
        if ( uploadedBranch.getBranchCity() == null || uploadedBranch.getBranchCity().isEmpty() ) {
            LOG.error( "Office city at row: " + uploadedBranch.getRowNum() + " is not provided" );
            branchValidationErrors.add( "Office city at row: " + uploadedBranch.getRowNum() + " is not provided" );
            errorRecord = true;
        }
        if ( uploadedBranch.getSourceRegionId() != null && !uploadedBranch.getSourceRegionId().isEmpty()
            && !isSourceRegionIdMappedToRegion( uploadedBranch.getSourceRegionId(), upload ) ) {
            LOG.error( "The region id at row: " + uploadedBranch.getRowNum() + " is not valid" );
            branchValidationErrors.add( "The region id at row: " + uploadedBranch.getRowNum() + " is not valid" );
            errorRecord = true;
        }

        if ( errorRecord ) {
            uploadedBranch.setErrorRecord( true );
        }
    }


    void validateBranchForWarnings( BranchUploadVO uploadedBranch, List<String> branchValidationWarnings )
    {
        boolean isWarningRecord = false;
        if ( uploadedBranch.getSourceRegionId() == null || uploadedBranch.getSourceRegionId().isEmpty() ) {
            LOG.error( "Office region Id at " + uploadedBranch.getRowNum() + " is not linked to any region" );
            branchValidationWarnings.add( "Office at " + uploadedBranch.getRowNum() + " is not linked to any region" );
            isWarningRecord = true;
        }
        if ( isWarningRecord ) {
            uploadedBranch.setWarningRecord( true );
        }
    }


    void validateUserForErrors( UserUploadVO uploadedUser, List<String> userValidationErrors, HierarchyUpload upload )
    {
        boolean errorRecord = false;
        if ( uploadedUser.getSourceUserId() == null || uploadedUser.getSourceUserId().isEmpty() ) {
            LOG.error( "Source user Id at row: " + uploadedUser.getRowNum() + " is not provided" );
            userValidationErrors.add( "Source user Id at row: " + uploadedUser.getRowNum() + " is not provided" );
            errorRecord = true;
        }
        if ( uploadedUser.getFirstName() == null || uploadedUser.getFirstName().isEmpty() ) {
            LOG.error( "First name at row: " + uploadedUser.getRowNum() + " is not provided" );
            userValidationErrors.add( "First name at row: " + uploadedUser.getRowNum() + " is not provided" );
            errorRecord = true;
        }
        if ( uploadedUser.getEmailId() == null || uploadedUser.getEmailId().isEmpty() ) {
            LOG.error( "Email Id at row: " + uploadedUser.getRowNum() + " is not provided" );
            userValidationErrors.add( "Email Id at row: " + uploadedUser.getRowNum() + " is not provided" );
            errorRecord = true;
        }
        if ( uploadedUser.getSourceBranchId() != null && !uploadedUser.getSourceBranchId().isEmpty()
            && !isSourceBranchIdMappedToBranch( uploadedUser.getSourceBranchId(), upload ) ) {
            LOG.error( "The branch id at row: " + uploadedUser.getRowNum() + " is not valid" );
            userValidationErrors.add( "The branchId at row: " + uploadedUser.getRowNum() + " is not valid" );
            errorRecord = true;
        }
        if ( uploadedUser.getSourceRegionId() != null && !uploadedUser.getSourceRegionId().isEmpty()
            && !isSourceRegionIdMappedToRegion( uploadedUser.getSourceRegionId(), upload ) ) {
            LOG.error( "The region id at row: " + uploadedUser.getRowNum() + " is not valid" );
            userValidationErrors.add( "The region id at row: " + uploadedUser.getRowNum() + " is not valid" );
            errorRecord = true;
        }
        if ( uploadedUser.getAssignedBrachesAdmin() != null && !uploadedUser.getAssignedBrachesAdmin().isEmpty() ) {
            if ( isSourceBranchIdMappedToBranch( uploadedUser.getAssignedBrachesAdmin(), upload ) ) {
                uploadedUser.setBranchAdmin( true );
            } else {
                LOG.error( "The admin branch id at row: " + uploadedUser.getRowNum() + " is not valid" );
                userValidationErrors.add( "The admin branch id at row: " + uploadedUser.getRowNum() + " is not valid" );
                errorRecord = true;
            }
        }
        if ( uploadedUser.getAssignedRegionsAdmin() != null && !uploadedUser.getAssignedRegionsAdmin().isEmpty() ) {
            if ( isSourceRegionIdMappedToRegion( uploadedUser.getAssignedRegionsAdmin(), upload ) ) {
                uploadedUser.setRegionAdmin( true );
            } else {
                LOG.error( "The admin region id at row: " + uploadedUser.getRowNum() + " is not valid" );
                userValidationErrors.add( "The admin region id at row: " + uploadedUser.getRowNum() + " is not valid" );
                errorRecord = true;
            }
        }
        if ( errorRecord ) {
            uploadedUser.setErrorRecord( true );
        }
    }


    void validateUserForWarnings( UserUploadVO uploadedUser, List<String> userValidationWarnings )
    {
        boolean isWarningRecord = false;
        if ( uploadedUser.getSourceRegionId() == null || uploadedUser.getSourceRegionId().isEmpty() ) {
            LOG.error( "Region Id of user at " + uploadedUser.getRowNum() + " is not linked to any region" );
            userValidationWarnings.add( "Region Id of user at " + uploadedUser.getRowNum() + " is not linked to any region" );
            isWarningRecord = true;
        }
        if ( uploadedUser.getSourceBranchId() == null || uploadedUser.getSourceBranchId().isEmpty() ) {
            LOG.error( "Branch Id of user at " + uploadedUser.getRowNum() + " is not linked to any branch" );
            userValidationWarnings.add( "Branch Id of user at " + uploadedUser.getRowNum() + " is not linked to any branch" );
            isWarningRecord = true;
        }
        if ( uploadedUser.getAssignedBrachesAdmin() == null || uploadedUser.getAssignedBrachesAdmin().isEmpty() ) {
            LOG.error( "admin region Id of user at " + uploadedUser.getRowNum() + " is not linked to any region" );
            userValidationWarnings
                .add( "admin region Id of user at " + uploadedUser.getRowNum() + " is not linked to any region" );
            isWarningRecord = true;
        }
        if ( uploadedUser.getAssignedRegionsAdmin() == null || uploadedUser.getAssignedRegionsAdmin().isEmpty() ) {
            LOG.error( "admin branch id of user at " + uploadedUser.getRowNum() + " is not linked to any branch" );
            userValidationWarnings
                .add( "admin branch id of user at " + uploadedUser.getRowNum() + " is not linked to any branch" );
            isWarningRecord = true;
        }

        if ( isWarningRecord ) {
            uploadedUser.setWarningRecord( true );
        }
    }


    boolean isSourceBranchIdMappedToBranch( String sourceBranchId, HierarchyUpload upload )
    {
        LOG.debug( "Checking if source branch id is present" );
        BranchUploadVO branchUploadVO = new BranchUploadVO();
        branchUploadVO.setSourceBranchId( sourceBranchId );
        if ( upload.getBranches() != null && !upload.getBranches().isEmpty() ) {
            return upload.getBranches().contains( branchUploadVO );
        }
        return false;
    }


    boolean isSourceRegionIdMappedToRegion( String sourceRegionId, HierarchyUpload upload )
    {
        LOG.debug( "Checking if source region id is present" );
        RegionUploadVO regionUploadVO = new RegionUploadVO();
        regionUploadVO.setSourceRegionId( sourceRegionId );
        if ( upload.getRegions() != null && !upload.getRegions().isEmpty() ) {
            return upload.getRegions().contains( regionUploadVO );
        }
        return false;
    }


    boolean isSourceBranchIdMappedToBranch( List<String> sourceBranchIds, HierarchyUpload upload )
    {
        boolean isValid = true;
        for ( String sourceBranchId : sourceBranchIds ) {
            if ( !isSourceBranchIdMappedToBranch( sourceBranchId, upload ) ) {
                isValid = false;
            }
        }
        return isValid;
    }


    boolean isSourceRegionIdMappedToRegion( List<String> sourceRegionIds, HierarchyUpload upload )
    {
        boolean isValid = true;
        for ( String sourceRegionId : sourceRegionIds ) {
            if ( !isSourceRegionIdMappedToRegion( sourceRegionId, upload ) ) {
                isValid = false;
            }
        }
        return isValid;
    }
}
