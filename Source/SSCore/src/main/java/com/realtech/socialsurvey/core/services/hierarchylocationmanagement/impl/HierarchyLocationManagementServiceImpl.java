package com.realtech.socialsurvey.core.services.hierarchylocationmanagement.impl;


import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.HierarchyRelocationTarget;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.enums.HierarchyType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.hierarchylocationmanagement.HierarchyLocationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.searchengine.SearchEngineManagementServices;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;

@Component
public class HierarchyLocationManagementServiceImpl implements HierarchyLocationManagementService
{
    private static final Logger LOG = LoggerFactory.getLogger( HierarchyLocationManagementServiceImpl.class );

    @Autowired
	private OrganizationUnitSettingsDao organizationUnitSettingsDao;
    
    @Autowired
    OrganizationManagementService organizationManagementService;

    @Autowired
    UserManagementService userManagementService;

    @Autowired
    SolrSearchService solrSearchService;

    @Autowired
    SurveyHandler surveyHandler;

    @Autowired
    private SocialManagementService socialManagementService;
    
    @Autowired
    private SearchEngineManagementServices searchEngineManagement;


    /* method to relocate region to another company 
     * does'nt apply to the same company
     * does'nt apply to default region
     * parameter ( region, target location ) 
    */
    @Transactional
    @Override
    public void relocateRegion( Region regionToBeRelocated, HierarchyRelocationTarget targetLocation )
        throws InvalidInputException, SolrException
    {
        LOG.info( "Method HierarchyLocationManagementService.relocateRegion() started" );

        parseParametersForRegionRelocation( regionToBeRelocated, targetLocation );

        //updating  region details with company of the target location effectively relocates the region to the target company
        regionToBeRelocated.setCompany( targetLocation.getTargetCompany() );

        //saving changes in MySQL database
        userManagementService.updateRegion( regionToBeRelocated );

        //updating region in solr
        solrSearchService.addOrUpdateRegionToSolr( regionToBeRelocated );

        //updating mongoDB and solr for social posts and social connections 
        socialManagementService.processSocialPostsAndSocialConnectionsForRegionDuringRelocation( regionToBeRelocated,
            targetLocation );

        //updating surveys solely meant for the region
       // surveyHandler.processSurveyDetailsForRegionDuringRelocation( regionToBeRelocated, targetLocation );

        //updating the branches in the given region
        targetLocation.setTargetRegion( regionToBeRelocated );
        for ( Branch branch : regionToBeRelocated.getBranches() ) {
            relocateBranch( branch, targetLocation );
        }

        LOG.info( "Method HierarchyLocationManagementService.relocateRegion() finished" );
    }


    /* method to relocate branch to another region 
     * does'nt apply to the same region
     * does'nt apply to default branch
     * parameter ( branch, target location ) 
    */
    @Transactional
    @Override
    public void relocateBranch( Branch branchToBeRelocated, HierarchyRelocationTarget targetLocation )
        throws SolrException, InvalidInputException
    {
        LOG.info( "Method HierarchyLocationManagementService.relocateBranch() started" );

        parseParametersForBranchRelocation( branchToBeRelocated, targetLocation );

        switch ( targetLocation.getHierarchyType() ) {
            case BRANCH: {
                //Relocating the branch to a different region in another company              
                branchToBeRelocated.setRegion( targetLocation.getTargetRegion() );
            }
            case REGION: {
                //updating the branch details under the region which is being moved
                branchToBeRelocated.setCompany( targetLocation.getTargetCompany() );
                userManagementService.updateBranch( branchToBeRelocated );
                break;
            }
            default: {
                LOG.error( "Relocation target is Invalid" );
                throw new InvalidInputException(
                    "Method HierarchyLocationManagementService.relocateBranch(): Invalid HierarchyRelocationTarget" );
            }
        }

        //updating branch in solr
        solrSearchService.addOrUpdateBranchToSolr( branchToBeRelocated );

        //updating mongoDB and solr for social posts and social connections 
        socialManagementService.processSocialPostsAndSocialConnectionsForBranchDuringRelocation( branchToBeRelocated,
            targetLocation );

        //updating surveys solely meant for the branch
        ///surveyHandler.processSurveyDetailsForBranchDuringRelocation( branchToBeRelocated, targetLocation );

        //updating the users in the given branch
        targetLocation.setTargetBranch( branchToBeRelocated );
        for ( User user : organizationManagementService.getUsersUnderBranch( branchToBeRelocated ) ) {
            relocateUser( user, targetLocation );
        }


        LOG.info( "Method HierarchyLocationManagementService.relocateBranch() finished" );
    }


    /* method to relocate user a hierarchy
     * parameter ( user, target location ) 
    */
    @Transactional
    @Override
    public void relocateUser( User userToBeRelocated, HierarchyRelocationTarget targetLocation )
        throws SolrException, InvalidInputException
    {
        LOG.info( "Method HierarchyLocationManagementService.relocateUser() started" );

        parseParametersForUserRelocation( userToBeRelocated, targetLocation );

        List<UserProfile> userProfiles = userToBeRelocated.getUserProfiles();
        long curentBranchId = 0l;
        long curentRegionId = 0l;
        long curentCompanyId = 0l;
        
        
        //check if user is assigned to valid company
        LicenseDetail licenseDetail = null;
        if(userToBeRelocated.getCompany() != null && ! userToBeRelocated.getCompany().getLicenseDetails().isEmpty()) {
        		licenseDetail = userToBeRelocated.getCompany().getLicenseDetails().get(CommonConstants.INITIAL_INDEX);
        }else {
        	 	throw new InvalidInputException("Can't move user. User is assigned to an invalid company.");
        }
        
        //check, if user is an individual account
        if(licenseDetail.getAccountsMaster().getAccountsMasterId() == CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL) {
        		userToBeRelocated.setIsOwner(CommonConstants.IS_NOT_OWNER);
        		userToBeRelocated.setIsForcePassword(CommonConstants.NO);
        		ListIterator<UserProfile> userProfileItr = userProfiles.listIterator();
                while ( userProfileItr.hasNext() ) {
                    UserProfile userProfile = userProfileItr.next();
        			if(userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID) {
        				curentBranchId = userProfile.getBranchId();
        				curentRegionId = userProfile.getRegionId();
        				curentCompanyId = userProfile.getCompany().getCompanyId();
        			}else {
        				//userProfiles.remove(userProfile);
        			    //remove from iterator instead of list modifies the underlying list 
        				userProfileItr.remove();
        			}
        		}
        		
        }else {
        		Set<Long> existingBranchAssignments = new HashSet<Long>();
            for ( UserProfile userProfile : userProfiles ) {
                if(existingBranchAssignments.contains( userProfile.getBranchId() )){
                    throw new InvalidInputException("User " + userToBeRelocated.getUserId()  + " has assignments in different branches");
                }else{
                    existingBranchAssignments.add(  userProfile.getBranchId()  );
                }
                
                if(userProfile.getIsPrimary() == CommonConstants.IS_PRIMARY_TRUE){
                    curentBranchId = userProfile.getBranchId();
                    curentRegionId = userProfile.getRegionId();
                    curentCompanyId = userProfile.getCompany().getCompanyId();
                }
            }
        }
        
        
        
        
        //updating user Profile table for a user during relocation

        switch ( targetLocation.getHierarchyType() ) {
            case USER: {
                for ( UserProfile userProfile : userProfiles ) {
                    userProfile.setBranchId( targetLocation.getTargetBranch().getBranchId() );
                    userProfile.setRegionId( targetLocation.getTargetRegion().getRegionId() );
                    userProfile.setCompany( targetLocation.getTargetCompany() );
                    userProfile.setModifiedOn(new Timestamp(System.currentTimeMillis()));
                }
                break;
            }
            case BRANCH: {
                for ( UserProfile userProfile : userProfiles ) {
                		userProfile.setModifiedOn(new Timestamp(System.currentTimeMillis()));
                    if ( userProfile.getBranchId() == targetLocation.getTargetBranch().getBranchId() ) {
                        if ( !userToBeRelocated.isCompanyAdmin() ) {
                            userProfile.setRegionId( targetLocation.getTargetRegion().getRegionId() );
                            userProfile.setCompany( targetLocation.getTargetCompany() );
                        } else {
                            userProfiles.remove( userProfile );
                            userManagementService.removeUserProfile( userProfile.getUserProfileId() );
                        }
                    } else {
                        if ( !userToBeRelocated.isCompanyAdmin() ) {
                            userProfiles.remove( userProfile );
                            userManagementService.removeUserProfile( userProfile.getUserProfileId() );
                        }
                    }
                }
                break;
            }
            case REGION: {
                for ( UserProfile userProfile : userProfiles ) {
                		userProfile.setModifiedOn(new Timestamp(System.currentTimeMillis()));
                    if ( userProfile.getRegionId() == targetLocation.getTargetRegion().getRegionId() ) {
                        if ( !userToBeRelocated.isCompanyAdmin() ) {
                            userProfile.setCompany( targetLocation.getTargetCompany() );
                        } else {
                            userProfiles.remove( userProfile );
                            userManagementService.removeUserProfile( userProfile.getUserProfileId() );
                        }
                    } else {
                        if ( !userToBeRelocated.isCompanyAdmin() ) {
                            userProfiles.remove( userProfile );
                            userManagementService.removeUserProfile( userProfile.getUserProfileId() );
                        }
                    }
                }
                break;
            }
            default: {
                LOG.error( "Invalid Hierarchy Type" );
                throw new InvalidInputException(
                    "Method HierarchyLocationManagementService.relocateUser(): Invalid Hierarchy Type" );
            }
        }


        //update UserProfile table in MySQL
        userToBeRelocated.setUserProfiles( userProfiles );
        for ( UserProfile userProfile : userProfiles ) {
            userManagementService.updateUserProfileObject( userProfile );
        }

        //update Other user details in MySQL
        organizationManagementService.updateCompanyIdInMySQLForUser( userToBeRelocated, targetLocation.getTargetCompany() );
        userManagementService.updatePrimaryProfileOfUser( userToBeRelocated );

        //updating user in solr
        solrSearchService.addUserToSolr( userToBeRelocated );
        
        //update user in mongo
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettingsByIden("companyId", targetLocation.getTargetCompany().getCompanyId(), userToBeRelocated.getUserId(), CommonConstants.AGENT_ID_COLUMN);


        //updating  survey details  after relocation
        if(targetLocation.getSurveyRelcation() == 1){
            //move survey along with user
            surveyHandler.moveAllSurveysAlongWithUser( userToBeRelocated.getUserId(), targetLocation.getTargetBranch().getBranchId() , targetLocation.getTargetRegion().getRegionId() , targetLocation.getTargetCompany().getCompanyId() );            
        } if(targetLocation.getSurveyRelcation() == 2){
            //disconnect survey from user
            surveyHandler.disconnectAllSurveysFromWithUser( userToBeRelocated.getUserId() );
        }else if(targetLocation.getSurveyRelcation() == 3){
            //move all survey along with user
            surveyHandler.moveAllSurveysAlongWithUser( userToBeRelocated.getUserId(), targetLocation.getTargetBranch().getBranchId() , targetLocation.getTargetRegion().getRegionId() , targetLocation.getTargetCompany().getCompanyId() );            
            //copy completed survey to old branch again
            surveyHandler.copyAllSurveysAlongWithUser( userToBeRelocated.getUserId(), curentBranchId , curentRegionId , curentCompanyId );            
        }

        //updating mongoDB and solr for social posts and social connections 
        socialManagementService
            .processSocialPostsAndSocialConnectionsForUserAfterRelocation( userToBeRelocated, targetLocation );
        
        //update latest hierarchies address
        searchEngineManagement.updateAddressForAgentWhilePrimaryChange(userToBeRelocated.getUserId());

        LOG.info( "Method HierarchyLocationManagementService.relocateUser() finished" );

    }


    private void parseParametersForRegionRelocation( Region regionToBeRelocated, HierarchyRelocationTarget targetLocation )
        throws InvalidInputException
    {
        checkForNullValues( regionToBeRelocated, targetLocation );
        checkForDefaultValues( regionToBeRelocated, targetLocation );

        if ( targetLocation.getTargetCompany().getCompanyId() == regionToBeRelocated.getCompany().getCompanyId() ) {
            LOG.error( " Cannot relocate a region within the company" );
            throw new InvalidInputException(
                "parseParametersForRegionRelocation: Cannot relocate a region within the company" );
        }

    }


    private void parseParametersForBranchRelocation( Branch branchToBeRelocated, HierarchyRelocationTarget targetLocation )
        throws InvalidInputException
    {
        checkForNullValues( branchToBeRelocated, targetLocation );
        checkForDefaultValues( branchToBeRelocated, targetLocation );

        if ( targetLocation.getTargetCompany().getCompanyId() == branchToBeRelocated.getCompany().getCompanyId() ) {
            LOG.error( " Cannot relocate a Branch within the company" );
            throw new InvalidInputException(
                "parseParametersForBranchRelocation: Cannot relocate a Branch within the company" );
        } else if ( targetLocation.getTargetRegion().getCompany().getCompanyId() == branchToBeRelocated.getCompany()
            .getCompanyId() ) {
            LOG.error( "Can't relocate branch to a region within the company" );
            throw new InvalidInputException(
                "parseParametersForBranchRelocation: Can't relocate branch to a region within the company" );
        } else if ( targetLocation.getTargetCompany().getCompanyId() != targetLocation.getTargetRegion().getCompany()
            .getCompanyId() ) {
            LOG.error( "target Region is not under the target company" );
            throw new InvalidInputException(
                "parseParametersForBranchRelocation: target Region is not under the target company" );
        }
    }


    private void parseParametersForUserRelocation( User userToBeRelocated, HierarchyRelocationTarget targetLocation )
        throws InvalidInputException
    {
        checkForNullValues( userToBeRelocated, targetLocation );
        checkForDefaultValues( userToBeRelocated, targetLocation );

        if ( targetLocation.getTargetCompany().getCompanyId() == userToBeRelocated.getCompany().getCompanyId() ) {
            LOG.error( " Cannot relocate a User within the company" );
            throw new InvalidInputException( "Cannot relocate a User within the company" );
        } else if ( targetLocation.getTargetRegion().getCompany().getCompanyId() == userToBeRelocated.getCompany()
            .getCompanyId() ) {
            LOG.error( "Can't relocate User to a region within the company" );
            throw new InvalidInputException("Can't relocate User to a region within the company" );
        } else if ( targetLocation.getTargetBranch().getCompany().getCompanyId() == userToBeRelocated.getCompany()
            .getCompanyId() ) {
            LOG.error( "Can't relocate User to a Branch within the company" );
            throw new InvalidInputException("Can't relocate User to a Branch within the company" );
        } else if ( targetLocation.getTargetCompany().getCompanyId() != targetLocation.getTargetRegion().getCompany()
            .getCompanyId() ) {
            LOG.error( "target Region is not under the target company" );
            throw new InvalidInputException("target Region is not under the target company" );
        } else if ( targetLocation.getTargetRegion().getCompany().getCompanyId() != targetLocation.getTargetBranch()
            .getCompany().getCompanyId() ) {
            LOG.error( "target Branch is not under the target Region" );
            throw new InvalidInputException( "target Branch is not under the target Region" );
        }
    }


    private void checkForNullValues( Object hierarchyToBeRelocated, HierarchyRelocationTarget targetLocation )
        throws InvalidInputException
    {
        LOG.info( "Method checkForNullValues started" );
        if ( targetLocation == null ) {
            LOG.error( "Method checkForNullValues: targetLocation is null" );
            throw new InvalidInputException( "Method checkForNullValues: targetLocation is null" );
        } else if ( targetLocation.getHierarchyType() == null ) {
            LOG.error( "Method checkForNullValues: target Hierarchy type is null" );
            throw new InvalidInputException( "Method checkForNullValues: target Hierarchy type is null" );
        }
        switch ( targetLocation.getHierarchyType() ) {

            case REGION: {
                if ( !( hierarchyToBeRelocated instanceof Region ) ) {
                    LOG.error( "Invalid Region Object" );
                    throw new InvalidInputException(
                        "Method checkForNullValues(): Relocating Region...... Invalid Region Object" );
                }
                if ( targetLocation.getTargetCompany() == null ) {
                    LOG.error( "Target Company Object is null" );
                    throw new InvalidInputException(
                        "Method checkForNullValues():Relocating Region...... Target Company Object is null" );
                }
                break;
            }
            case BRANCH: {
                if ( !( hierarchyToBeRelocated instanceof Branch ) ) {
                    LOG.error( "Invalid Branch Object" );
                    throw new InvalidInputException(
                        "Method checkForNullValues(): Relocating Branch...... Invalid Branch Object" );
                }
                if ( targetLocation.getTargetRegion() == null ) {
                    LOG.error( "Target Region Object is null" );
                    throw new InvalidInputException(
                        "Method checkForNullValues():Relocating Branch...... Target Region Object is null" );
                }
                if ( targetLocation.getTargetCompany() == null ) {
                    LOG.error( "Target Company Object is null" );
                    throw new InvalidInputException(
                        "Method checkForNullValues():Relocating Branch...... Target Company Object is null" );
                }
                break;
            }
            case USER: {
                if ( !( hierarchyToBeRelocated instanceof User ) ) {
                    LOG.error( "Invalid User Object" );
                    throw new InvalidInputException( "Method checkForNullValues(): Relocating User...... Invalid User Object" );
                }
                if ( targetLocation.getTargetBranch() == null ) {
                    LOG.error( "Target Branch Object is null" );
                    throw new InvalidInputException(
                        "Method checkForNullValues():Relocating User...... Target Branch Object is null" );
                }
                if ( targetLocation.getTargetRegion() == null ) {
                    LOG.error( "Target Region Object is null" );
                    throw new InvalidInputException(
                        "Method checkForNullValues():Relocating User...... Target Region Object is null" );
                }
                if ( targetLocation.getTargetCompany() == null ) {
                    LOG.error( "Target Company Object is null" );
                    throw new InvalidInputException(
                        "Method checkForNullValues(): Relocating User...... Target Company Object is null" );
                }
                break;
            }
            default: {
                LOG.error( "Invalid Hierarchy Type" );
                throw new InvalidInputException(
                    "Method HierarchyLocationManagementService.relocateUser(): Invalid Hierarchy Type" );
            }
        }
        LOG.info( "Method checkForNullValues finished" );
    }


    private void checkForDefaultValues( Object hierarchyToBeRelocated, HierarchyRelocationTarget targetLocation )
        throws InvalidInputException
    {
        LOG.info( "Method checkForDefaultValues started" );
        switch ( targetLocation.getHierarchyType() ) {

            case REGION: {
                if ( targetLocation.getTargetCompany().getCompanyId() == CommonConstants.DEFAULT_COMPANY_ID ) {
                    LOG.error( "Target Company is default" );
                    throw new InvalidInputException(
                        "Method checkForDefaultValues():Relocating Region...... can't move to a default company" );
                }
                if ( ( (Region) hierarchyToBeRelocated ).getIsDefaultBySystem() == CommonConstants.ONE ) {
                    LOG.error( "Can't move a default region" );
                    throw new InvalidInputException(
                        "Method checkForDefaultValues():Relocating Region...... can't move a default region" );

                }
                break;
            }
            case BRANCH: {
                if ( targetLocation.getTargetRegion().getIsDefaultBySystem() == CommonConstants.ONE ) {
                    LOG.error( "Target Region is default" );
                    throw new InvalidInputException(
                        "Method checkForDefaultValues():Relocating Branch...... can't move to a default region" );
                } else if ( targetLocation.getTargetCompany().getCompanyId() == CommonConstants.DEFAULT_COMPANY_ID ) {
                    LOG.error( "Target Company is default" );
                    throw new InvalidInputException(
                        "Method checkForDefaultValues():Relocating Branch...... can't move to a default company" );
                }

                if ( ( (Branch) hierarchyToBeRelocated ).getIsDefaultBySystem() == CommonConstants.ONE ) {
                    LOG.error( "Can't move a default branch" );
                    throw new InvalidInputException(
                        "Method checkForDefaultValues():Relocating Region...... can't move a default branch" );

                }
                break;
            }
            case USER: {
               if ( targetLocation.getTargetCompany().getCompanyId() == CommonConstants.DEFAULT_COMPANY_ID ) {
                    LOG.error( "Target Company is default" );
                    throw new InvalidInputException(
                        "Method checkForDefaultValues():Relocating User...... can't move to a default company" );
                }
                break;
            }
            default: {
                LOG.error( "Invalid Hierarchy Type" );
                throw new InvalidInputException(
                    "Method HierarchyLocationManagementService.relocateUser(): Invalid Hierarchy Type" );
            }
        }
        LOG.info( "Method checkForDefaultValues finished" );
    }


    @Override
    public void generateEntitiesAndStartRelocationForRegion( long regionId, long targetCompanyId )
        throws InvalidInputException, SolrException
    {
        LOG.info( "Method generateEntitiesAndStartRelocationForRegion started" );
        Company targetCompany = userManagementService.getCompanyById( targetCompanyId );
        Region regionToBeRelocated = userManagementService.getRegionById( regionId );

        //check for valid values
        if ( regionToBeRelocated == null ) {
            LOG.error( "Region does'nt exist" );
            throw new InvalidInputException( "generateEntitiesAndStartRelocationForRegion: Region does'nt exist" );
        } else if ( targetCompany == null ) {
            LOG.error( "Target company does'nt exist" );
            throw new InvalidInputException( "generateEntitiesAndStartRelocationForRegion: Target company does'nt exist" );
        }

        //generate the entities needed for region relocation
        HierarchyRelocationTarget targetLocation = new HierarchyRelocationTarget();
        targetLocation.setHierarchyType( HierarchyType.REGION );
        targetLocation.setTargetCompany( targetCompany );

        //start region relocation
        relocateRegion( regionToBeRelocated, targetLocation );

        LOG.info( "Method generateEntitiesAndStartRelocationForRegion finished" );

    }


    @Override
    public void generateEntitiesAndStartRelocationForBranch( long branchId, long targetCompanyId, long targetRegionId )
        throws InvalidInputException, SolrException
    {
        LOG.info( "Method generateEntitiesAndStartRelocationForBranch started" );
        Branch branchToBeRelocated = userManagementService.getBranchById( branchId );
        Company targetCompany = userManagementService.getCompanyById( targetCompanyId );
        Region targetRegion = userManagementService.getRegionById( targetRegionId );


        //check for valid values
        if ( branchToBeRelocated == null ) {
            LOG.error( "Branch does'nt exist" );
            throw new InvalidInputException( "generateEntitiesAndStartRelocationForBranch: Branch does'nt exist" );
        } else if ( targetCompany == null ) {
            LOG.error( "Target company does'nt exist" );
            throw new InvalidInputException( "generateEntitiesAndStartRelocationForBranch: Target company does'nt exist" );
        } else if ( targetRegion == null ) {
            LOG.error( "Target region does'nt exist" );
            throw new InvalidInputException( "generateEntitiesAndStartRelocationForBranch: Target region does'nt exist" );
        }

        //generate the entities needed for branch relocation
        HierarchyRelocationTarget targetLocation = new HierarchyRelocationTarget();
        targetLocation.setHierarchyType( HierarchyType.BRANCH );
        targetLocation.setTargetCompany( targetCompany );
        targetLocation.setTargetRegion( targetRegion );

        //start branch relocation
        relocateBranch( branchToBeRelocated, targetLocation );

        LOG.info( "Method generateEntitiesAndStartRelocationForBranch finished" );
    }


    @Override
    public void generateEntitiesAndStartRelocationForUser( long userId,  long targetBranchId , int surveyRelcation ) throws InvalidInputException, SolrException
    {
        LOG.info( "Method generateEntitiesAndStartRelocationForUser started" );
        User userToBeRelocated = userManagementService.getUserByUserId( userId );
        Branch targetBranch = userManagementService.getBranchById( targetBranchId );
       
        //check for valid values
        if ( userToBeRelocated == null ) {
            LOG.error( "User does'nt exist" );
            throw new InvalidInputException( "generateEntitiesAndStartRelocationForUser: User does'nt exist" );
        }  
        if ( targetBranch == null ) {
            LOG.error( "Target branch does'nt exist" );
            throw new InvalidInputException( "generateEntitiesAndStartRelocationForUser: Target branch does'nt exist" );
        }

        Region targetRegion = targetBranch.getRegion();
        Company targetCompany = targetRegion.getCompany();
        
        
        //generate the entities needed for user relocation
        HierarchyRelocationTarget targetLocation = new HierarchyRelocationTarget();
        targetLocation.setHierarchyType( HierarchyType.USER );
        targetLocation.setTargetCompany( targetCompany );
        targetLocation.setTargetRegion( targetRegion );
        targetLocation.setTargetBranch( targetBranch );
        targetLocation.setSurveyRelcation( surveyRelcation );

        //start user relocation
        relocateUser( userToBeRelocated, targetLocation );
        
        //update latest hierarchies address
        searchEngineManagement.updateAddressForAgentWhilePrimaryChange(userToBeRelocated.getUserId());

        LOG.info( "Method generateEntitiesAndStartRelocationForUser finished" );
    }
}
