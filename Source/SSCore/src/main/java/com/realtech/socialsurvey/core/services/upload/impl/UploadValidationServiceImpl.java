package com.realtech.socialsurvey.core.services.upload.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    private static Logger LOG = LoggerFactory.getLogger( UploadValidationServiceImpl.class );

    @Value ( "${MASK_EMAIL_ADDRESS}")
    private String maskEmail;


    @Override
    public void validateHeirarchyUpload( UploadValidation validationObject )
    {
        validateRegions( validationObject );
        validateBranches( validationObject );
        validateUsers( validationObject );
        validateDeletedRegionsRecords( validationObject );
        validateDeletedBranchesRecords( validationObject );
    }


    void validateRegions( UploadValidation validationObject )
    {
        List<String> regionValidationErrors = new ArrayList<String>();
        for ( RegionUploadVO uploadedRegion : validationObject.getUpload().getRegions() ) {
            if ( !uploadedRegion.isDeletedRecord() ) {
                if ( uploadedRegion.getValidationErrors() != null ) {
                    uploadedRegion.getValidationErrors().clear();
                }
                validateRegionForErrors( uploadedRegion, regionValidationErrors );
            }
        }
        validationObject.setRegionValidationErrors( regionValidationErrors );
    }


    void validateBranches( UploadValidation validationObject )
    {
        List<String> branchValidationErrors = new ArrayList<String>();
        List<String> branchValidationWarnings = new ArrayList<String>();
        for ( BranchUploadVO uploadedBranch : validationObject.getUpload().getBranches() ) {
            if ( !uploadedBranch.isDeletedRecord() ) {
                if ( uploadedBranch.getValidationWarnings() != null ) {
                    uploadedBranch.getValidationWarnings().clear();
                }
                if ( uploadedBranch.getValidationErrors() != null ) {
                    uploadedBranch.getValidationErrors().clear();
                }
                validateBranchForErrors( uploadedBranch, branchValidationErrors, validationObject.getUpload() );
                validateBranchForWarnings( uploadedBranch, branchValidationWarnings );
            }
        }
        validationObject.setBranchValidationErrors( branchValidationErrors );
        validationObject.setBranchValidationWarnings( branchValidationWarnings );
    }


    void validateUsers( UploadValidation validationObject )
    {
        List<String> userValidationErrors = new ArrayList<String>();
        List<String> userValidationWarnings = new ArrayList<String>();
        for ( UserUploadVO uploadeduser : validationObject.getUpload().getUsers() ) {
            if ( !uploadeduser.isDeletedRecord() ) {
                if ( uploadeduser.getValidationErrors() != null ) {
                    uploadeduser.getValidationErrors().clear();
                }
                if ( uploadeduser.getValidationWarnings() != null ) {
                    uploadeduser.getValidationWarnings().clear();
                }
                validateUserForErrors( uploadeduser, userValidationErrors, validationObject.getUpload() );
                validateUserForWarnings( uploadeduser, userValidationWarnings );
            }
        }
        validationObject.setUserValidationErrors( userValidationErrors );
        validationObject.setUserValidationWarnings( userValidationWarnings );
    }


    private void validateDeletedRegionsRecords( UploadValidation validationObject )
    {
        Map<String, List<BranchUploadVO>> branchesLinkedToRegion = getBranchesLinkedToRegion(
            validationObject.getUpload().getBranches() );
        Map<String, List<UserUploadVO>> usersLinkedToRegion = getUsersLinkedToRegion( validationObject.getUpload().getUsers() );
        for ( RegionUploadVO region : validationObject.getUpload().getRegions() ) {
            if ( region.isDeletedRecord() ) {
                boolean errorRecordDueToActiveBranch = false;
                boolean errorRecordDueToActiveUser = false;
                if ( branchesLinkedToRegion.containsKey( region.getSourceRegionId() ) ) {
                    for ( BranchUploadVO branch : branchesLinkedToRegion.get( region.getSourceRegionId() ) ) {
                        if ( !branch.isDeletedRecord() ) {
                            errorRecordDueToActiveBranch = true;
                            break;
                        }
                    }
                }
                if ( usersLinkedToRegion.containsKey( region.getSourceRegionId() ) ) {
                    for ( UserUploadVO user : usersLinkedToRegion.get( region.getSourceRegionId() ) ) {
                        if ( !user.isDeletedRecord() ) {
                            errorRecordDueToActiveUser = true;
                            break;
                        }
                    }
                }
                if ( errorRecordDueToActiveBranch && errorRecordDueToActiveUser ) {
                    region.setErrorRecord( true );
                    LOG.error( "Region at row " + region.getRowNum()
                        + " cannot be deleted as it has active offices and users associated with it" );
                    validationObject.getRegionValidationErrors().add( "Region at row " + region.getRowNum()
                        + " cannot be deleted as it has active offices and users associated with it" );
                    region.getValidationErrors().add( "Region at row " + region.getRowNum()
                        + " cannot be deleted as it has active offices and users associated with it" );
                } else if ( errorRecordDueToActiveBranch && !errorRecordDueToActiveUser ) {
                    region.setErrorRecord( true );
                    LOG.error( "Region at row " + region.getRowNum()
                        + " cannot be deleted as it has active offices associated with it" );
                    validationObject.getRegionValidationErrors().add( "Region at row " + region.getRowNum()
                        + " cannot be deleted as it has active offices associated with it" );
                    region.getValidationErrors().add( "Region at row " + region.getRowNum()
                        + " cannot be deleted as it has active offices associated with it" );
                } else if ( !errorRecordDueToActiveBranch && errorRecordDueToActiveUser ) {
                    region.setErrorRecord( true );
                    LOG.error( "Region at row " + region.getRowNum()
                        + " cannot be deleted as it has active users associated with it" );
                    validationObject.getRegionValidationErrors().add( "Region at row " + region.getRowNum()
                        + " cannot be deleted as it has active users associated with it" );
                    region.getValidationErrors().add( "Region at row " + region.getRowNum()
                        + " cannot be deleted as it has active users associated with it" );
                } else {
                    validationObject.setNumberOfRegionsDeleted( validationObject.getNumberOfRegionsDeleted() + 1 );
                }
            }
        }
    }


    private void validateDeletedBranchesRecords( UploadValidation validationObject )
    {
        Map<String, List<UserUploadVO>> usersLinkedToBranch = getUsersLinkedToBranch( validationObject.getUpload().getUsers() );
        for ( BranchUploadVO branch : validationObject.getUpload().getBranches() ) {
            if ( branch.isDeletedRecord() ) {
                boolean errorRecordDueToActiveUser = false;
                if ( usersLinkedToBranch.containsKey( branch.getSourceBranchId() ) ) {
                    for ( UserUploadVO user : usersLinkedToBranch.get( branch.getSourceBranchId() ) ) {
                        if ( !user.isDeletedRecord() ) {
                            errorRecordDueToActiveUser = true;
                            break;
                        }
                    }
                }
                if ( errorRecordDueToActiveUser ) {
                    branch.setErrorRecord( true );
                    LOG.error( "Office at row " + branch.getRowNum()
                        + " cannot be deleted as it has active users associated with it" );
                    validationObject.getBranchValidationErrors().add( "Office at row " + branch.getRowNum()
                        + " cannot be deleted as it has active users associated with it" );
                    branch.getValidationErrors().add( "Office at row " + branch.getRowNum()
                        + " cannot be deleted as it has active users associated with it" );
                } else {
                    validationObject.setNumberOfBranchesDeleted( validationObject.getNumberOfBranchesDeleted() + 1 );
                }
            }
        }
    }


    private Map<String, List<UserUploadVO>> getUsersLinkedToBranch( List<UserUploadVO> uploadedUsers )
    {
        Map<String, List<UserUploadVO>> usersBySourceBranchId = new HashMap<String, List<UserUploadVO>>();
        for ( UserUploadVO user : uploadedUsers ) {
            addUserToMap( user.getSourceBranchId(), user, usersBySourceBranchId );
            if ( user.getAssignedBranchesAdmin() != null && !user.getAssignedBranchesAdmin().isEmpty() ) {
                for ( String adminBranch : user.getAssignedBranchesAdmin() ) {
                    if ( !adminBranch.equalsIgnoreCase( user.getSourceBranchId() ) ) {
                        addUserToMap( adminBranch, user, usersBySourceBranchId );
                    }
                }
            }
        }
        return usersBySourceBranchId;
    }


    private Map<String, List<UserUploadVO>> getUsersLinkedToRegion( List<UserUploadVO> uploadedUsers )
    {
        Map<String, List<UserUploadVO>> usersBySourceRegionId = new HashMap<String, List<UserUploadVO>>();
        for ( UserUploadVO user : uploadedUsers ) {
            addUserToMap( user.getSourceRegionId(), user, usersBySourceRegionId );
            if ( user.getAssignedRegionsAdmin() != null && !user.getAssignedRegionsAdmin().isEmpty() ) {
                for ( String adminRegion : user.getAssignedRegionsAdmin() ) {
                    if ( !adminRegion.equalsIgnoreCase( user.getSourceRegionId() ) ) {
                        addUserToMap( adminRegion, user, usersBySourceRegionId );
                    }
                }
            }
        }
        return usersBySourceRegionId;
    }


    private void addUserToMap( String key, UserUploadVO user, Map<String, List<UserUploadVO>> usersBySourceRegionId )
    {
        List<UserUploadVO> users = null;
        if ( key != null && !key.isEmpty() ) {
            if ( usersBySourceRegionId.get( key ) == null ) {
                users = new ArrayList<UserUploadVO>();
                users.add( user );
                usersBySourceRegionId.put( key, users );
            } else {
                usersBySourceRegionId.get( key ).add( user );
            }
        }
    }


    private Map<String, List<BranchUploadVO>> getBranchesLinkedToRegion( List<BranchUploadVO> uploadedBranches )
    {
        Map<String, List<BranchUploadVO>> branchesBySourceRegionId = new HashMap<String, List<BranchUploadVO>>();
        List<BranchUploadVO> branches = null;
        for ( BranchUploadVO branch : uploadedBranches ) {
            if ( branch.getSourceRegionId() != null && !branch.getSourceRegionId().isEmpty() ) {
                if ( branchesBySourceRegionId.get( branch.getSourceRegionId() ) == null ) {
                    branches = new ArrayList<BranchUploadVO>();
                    branches.add( branch );
                    branchesBySourceRegionId.put( branch.getSourceRegionId(), branches );
                } else {
                    branchesBySourceRegionId.get( branch.getSourceRegionId() ).add( branch );
                }
            }
        }
        return branchesBySourceRegionId;
    }


    private void validateRegionForErrors( RegionUploadVO uploadedRegion, List<String> regionValidationErrors )
    {
        boolean errorRecord = false;
        if ( uploadedRegion.getSourceRegionId() == null || uploadedRegion.getSourceRegionId().isEmpty() ) {
            LOG.error( "Region Id at row: " + uploadedRegion.getRowNum() + " is not provided" );
            regionValidationErrors.add( "Region Id at row: " + uploadedRegion.getRowNum() + " is not provided" );
            uploadedRegion.getValidationErrors().add( "Region Id at row: " + uploadedRegion.getRowNum() + " is not provided" );
            errorRecord = true;
        }
        if ( uploadedRegion.getRegionName() == null || uploadedRegion.getRegionName().isEmpty() ) {
            LOG.error( "Region name at row: " + uploadedRegion.getRowNum() + " is not provided" );
            regionValidationErrors.add( "Region name at row: " + uploadedRegion.getRowNum() + " is not provided" );
            uploadedRegion.getValidationErrors()
                .add( "Region name at row: " + uploadedRegion.getRowNum() + " is not provided" );
            errorRecord = true;
        }

        if ( errorRecord ) {
            uploadedRegion.setErrorRecord( true );
        }
    }


    private void validateBranchForErrors( BranchUploadVO uploadedBranch, List<String> branchValidationErrors,
        HierarchyUpload upload )
    {
        boolean errorRecord = false;
        if ( uploadedBranch.getSourceBranchId() == null || uploadedBranch.getSourceBranchId().isEmpty() ) {
            LOG.error( "Office Id at row: " + uploadedBranch.getRowNum() + " is not provided" );
            branchValidationErrors.add( "Office Id at row: " + uploadedBranch.getRowNum() + " is not provided" );
            uploadedBranch.getValidationErrors().add( "Office Id at row: " + uploadedBranch.getRowNum() + " is not provided" );
            errorRecord = true;
        }
        if ( uploadedBranch.getBranchName() == null || uploadedBranch.getBranchName().isEmpty() ) {
            LOG.error( "Office name at row: " + uploadedBranch.getRowNum() + " is not provided" );
            branchValidationErrors.add( "Office name at row: " + uploadedBranch.getRowNum() + " is not provided" );
            uploadedBranch.getValidationErrors()
                .add( "Office name at row: " + uploadedBranch.getRowNum() + " is not provided" );
            errorRecord = true;
        }
        if ( ( uploadedBranch.getBranchAddress1() == null || uploadedBranch.getBranchAddress1().isEmpty() )
            && ( uploadedBranch.getBranchAddress2() == null || uploadedBranch.getBranchAddress2().isEmpty() ) ) {
            LOG.error( "Office address at row: " + uploadedBranch.getRowNum() + " is not provided" );
            branchValidationErrors.add( "Office address at row: " + uploadedBranch.getRowNum() + " is not provided" );
            uploadedBranch.getValidationErrors()
                .add( "Office address at row: " + uploadedBranch.getRowNum() + " is not provided" );
            errorRecord = true;
        }
        if ( uploadedBranch.getBranchCity() == null || uploadedBranch.getBranchCity().isEmpty() ) {
            LOG.error( "Office city at row: " + uploadedBranch.getRowNum() + " is not provided" );
            branchValidationErrors.add( "Office city at row: " + uploadedBranch.getRowNum() + " is not provided" );
            uploadedBranch.getValidationErrors()
                .add( "Office city at row: " + uploadedBranch.getRowNum() + " is not provided" );
            errorRecord = true;
        }
        if ( uploadedBranch.getSourceRegionId() != null && !uploadedBranch.getSourceRegionId().isEmpty()
            && !isSourceRegionIdMappedToRegion( uploadedBranch.getSourceRegionId(), upload ) ) {
            LOG.error( "Region id at row: " + uploadedBranch.getRowNum() + " is not valid" );
            branchValidationErrors.add( "Region id at row: " + uploadedBranch.getRowNum() + " is not valid" );
            uploadedBranch.getValidationErrors().add( "Region id at row: " + uploadedBranch.getRowNum() + " is not valid" );
            errorRecord = true;
        }

        if ( errorRecord ) {
            uploadedBranch.setErrorRecord( true );
        }
    }


    private void validateBranchForWarnings( BranchUploadVO uploadedBranch, List<String> branchValidationWarnings )
    {
        boolean isWarningRecord = false;
        if ( uploadedBranch.getSourceRegionId() == null || uploadedBranch.getSourceRegionId().isEmpty() ) {
            LOG.error( "Office at row " + uploadedBranch.getRowNum() + " is not assigned to any region" );
            branchValidationWarnings.add( "Office at row " + uploadedBranch.getRowNum() + " is not assigned to any region" );
            uploadedBranch.getValidationWarnings()
                .add( "Office at row " + uploadedBranch.getRowNum() + " is not assigned to any region" );
            isWarningRecord = true;
        }
        if ( isWarningRecord ) {
            uploadedBranch.setWarningRecord( true );
        }
    }


    private void validateUserForErrors( UserUploadVO uploadedUser, List<String> userValidationErrors, HierarchyUpload upload )
    {
        boolean errorRecord = false;
        if ( uploadedUser.getSourceUserId() == null || uploadedUser.getSourceUserId().isEmpty() ) {
            LOG.error( "User Id at row: " + uploadedUser.getRowNum() + " is not provided" );
            userValidationErrors.add( "User Id at row: " + uploadedUser.getRowNum() + " is not provided" );
            uploadedUser.getValidationErrors().add( "User Id at row: " + uploadedUser.getRowNum() + " is not provided" );
            errorRecord = true;
        }
        if ( uploadedUser.getFirstName() == null || uploadedUser.getFirstName().isEmpty() ) {
            LOG.error( "First name at row: " + uploadedUser.getRowNum() + " is not provided" );
            userValidationErrors.add( "First name at row: " + uploadedUser.getRowNum() + " is not provided" );
            uploadedUser.getValidationErrors().add( "First name at row: " + uploadedUser.getRowNum() + " is not provided" );
            errorRecord = true;
        }
        if ( uploadedUser.getEmailId() == null || uploadedUser.getEmailId().isEmpty() ) {
            LOG.error( "Email Id at row: " + uploadedUser.getRowNum() + " is not provided" );
            userValidationErrors.add( "Email Id at row: " + uploadedUser.getRowNum() + " is not provided" );
            uploadedUser.getValidationErrors().add( "Email Id at row: " + uploadedUser.getRowNum() + " is not provided" );
            errorRecord = true;
        }

        if ( uploadedUser.getAssignedBranches() != null && !uploadedUser.getAssignedBranches().isEmpty()
            && !isSourceBranchIdMappedToBranch( uploadedUser.getAssignedBranches(), upload ) ) {
            LOG.error( "Office assignment(s) at row: " + uploadedUser.getRowNum() + " is not valid" );
            userValidationErrors.add( "Office assignment(s) at row: " + uploadedUser.getRowNum() + " is not valid" );
            uploadedUser.getValidationErrors()
                .add( "Office assignment(s) at row: " + uploadedUser.getRowNum() + " is not valid" );
            errorRecord = true;
        }

        if ( uploadedUser.getAssignedRegions() != null && !uploadedUser.getAssignedRegions().isEmpty()
            && !isSourceRegionIdMappedToRegion( uploadedUser.getAssignedRegions(), upload ) ) {
            LOG.error( "Region assignment(s) at row: " + uploadedUser.getRowNum() + " is not valid" );
            userValidationErrors.add( "Region assignment(s) at row: " + uploadedUser.getRowNum() + " is not valid" );
            uploadedUser.getValidationErrors()
                .add( "Region assignment(s) at row: " + uploadedUser.getRowNum() + " is not valid" );
            errorRecord = true;
        }

        if ( uploadedUser.getAssignedBranchesAdmin() != null && !uploadedUser.getAssignedBranchesAdmin().isEmpty() ) {
            if ( isSourceBranchIdMappedToBranch( uploadedUser.getAssignedBranchesAdmin(), upload ) ) {
                uploadedUser.setBranchAdmin( true );
            } else {
                LOG.error( "Office admin privilege(s) at row: " + uploadedUser.getRowNum() + " is not valid" );
                userValidationErrors.add( "Office admin privilege(s) at row: " + uploadedUser.getRowNum() + " is not valid" );
                uploadedUser.getValidationErrors()
                    .add( "Office admin privilege(s) at row: " + uploadedUser.getRowNum() + " is not valid" );
                errorRecord = true;
            }
        }
        if ( uploadedUser.getAssignedRegionsAdmin() != null && !uploadedUser.getAssignedRegionsAdmin().isEmpty() ) {
            if ( isSourceRegionIdMappedToRegion( uploadedUser.getAssignedRegionsAdmin(), upload ) ) {
                uploadedUser.setRegionAdmin( true );
            } else {
                LOG.error( "Region admin privilege(s) at row: " + uploadedUser.getRowNum() + " is not valid" );
                userValidationErrors.add( "Region admin privilege(s) at row: " + uploadedUser.getRowNum() + " is not valid" );
                uploadedUser.getValidationErrors()
                    .add( "Region admin privilege(s) at row: " + uploadedUser.getRowNum() + " is not valid" );
                errorRecord = true;
            }
        }
        if ( errorRecord ) {
            uploadedUser.setErrorRecord( true );
        }
    }


    private void validateUserForWarnings( UserUploadVO uploadedUser, List<String> userValidationWarnings )
    {
        boolean isWarningRecord = false;
        boolean isAssignedToBranch = true;
        boolean isAssignedToRegion = true;
        boolean isAssignedToBranchAdmin = true;
        boolean isAssignedToRegionAdmin = true;
        if ( uploadedUser.getAssignedRegions() == null || uploadedUser.getAssignedRegions().isEmpty() ) {
            isAssignedToRegion = false;
        }
        if ( uploadedUser.getAssignedBranches() == null || uploadedUser.getAssignedBranches().isEmpty() ) {
            isAssignedToBranch = false;
        }
        if ( uploadedUser.getAssignedBranchesAdmin() == null || uploadedUser.getAssignedBranchesAdmin().isEmpty() ) {
            isAssignedToBranchAdmin = false;
        }
        if ( uploadedUser.getAssignedRegionsAdmin() == null || uploadedUser.getAssignedRegionsAdmin().isEmpty() ) {
            isAssignedToRegionAdmin = false;
        }

        if ( !isAssignedToBranch && !isAssignedToRegion && !isAssignedToBranchAdmin && !isAssignedToRegionAdmin ) {
            LOG.error( "User at row " + uploadedUser.getRowNum()
                + " is not assigned to any region or branch. User will be assigned to the company." );
            userValidationWarnings.add( "User at row " + uploadedUser.getRowNum()
                + " is not assigned to any region or branch. User will be assigned to the company." );
            uploadedUser.getValidationWarnings().add( "User at row " + uploadedUser.getRowNum()
                + " is not assigned to any region or branch. User will be assigned to the company." );
            isWarningRecord = true;
        } else {
            if ( !isAssignedToBranch ) {
                LOG.error( "User at row " + uploadedUser.getRowNum() + " is not assigned to any office" );
                userValidationWarnings.add( "User at row " + uploadedUser.getRowNum() + " is not assigned to any office" );
                uploadedUser.getValidationWarnings()
                    .add( "User at row " + uploadedUser.getRowNum() + " is not assigned to any office" );
                isWarningRecord = true;
            }
            if ( !isAssignedToRegion ) {
                LOG.error( "User at row " + uploadedUser.getRowNum() + " is not assigned to any region" );
                userValidationWarnings.add( "User at row " + uploadedUser.getRowNum() + " is not assigned to any region" );
                uploadedUser.getValidationWarnings()
                    .add( "User at row " + uploadedUser.getRowNum() + " is not assigned to any region" );
                isWarningRecord = true;
            }
            if ( !isAssignedToBranchAdmin ) {
                LOG.error( "User at row " + uploadedUser.getRowNum() + " is not assigned to any admin office" );
                userValidationWarnings
                    .add( "User at row " + uploadedUser.getRowNum() + " is not assigned to any admin office" );
                uploadedUser.getValidationWarnings()
                    .add( "User at row " + uploadedUser.getRowNum() + " is not assigned to any admin office" );
                isWarningRecord = true;
            }
            if ( !isAssignedToRegionAdmin ) {
                LOG.error( "User at row " + uploadedUser.getRowNum() + " is not assigned to any admin region" );
                userValidationWarnings
                    .add( "User at row " + uploadedUser.getRowNum() + " is not assigned to any admin region" );
                uploadedUser.getValidationWarnings()
                    .add( "User at row " + uploadedUser.getRowNum() + " is not assigned to any admin region" );
                isWarningRecord = true;
            }
        }

        if ( isWarningRecord ) {
            uploadedUser.setWarningRecord( true );
        }
    }


    private boolean isSourceBranchIdMappedToBranch( String sourceBranchId, HierarchyUpload upload )
    {
        LOG.debug( "Checking if source branch id is present" );
        BranchUploadVO branchUploadVO = new BranchUploadVO();
        branchUploadVO.setSourceBranchId( sourceBranchId );
        if ( upload.getBranches() != null && !upload.getBranches().isEmpty() ) {
            return upload.getBranches().contains( branchUploadVO );
        }
        return false;
    }


    private boolean isSourceRegionIdMappedToRegion( String sourceRegionId, HierarchyUpload upload )
    {
        LOG.debug( "Checking if source region id is present" );
        RegionUploadVO regionUploadVO = new RegionUploadVO();
        regionUploadVO.setSourceRegionId( sourceRegionId );
        if ( upload.getRegions() != null && !upload.getRegions().isEmpty() ) {
            return upload.getRegions().contains( regionUploadVO );
        }
        return false;
    }


    private boolean isSourceBranchIdMappedToBranch( List<String> sourceBranchIds, HierarchyUpload upload )
    {
        boolean isValid = true;
        for ( String sourceBranchId : sourceBranchIds ) {
            if ( !isSourceBranchIdMappedToBranch( sourceBranchId, upload ) ) {
                isValid = false;
            }
        }
        return isValid;
    }


    private boolean isSourceRegionIdMappedToRegion( List<String> sourceRegionIds, HierarchyUpload upload )
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
