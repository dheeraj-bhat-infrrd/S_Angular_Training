package com.realtech.socialsurvey.core.facade.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserFromSearch;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.facade.HierarchyFacade;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.vo.BranchVO;
import com.realtech.socialsurvey.core.vo.CompanyNotRegisteredVO;
import com.realtech.socialsurvey.core.vo.HierarchyViewVO;
import com.realtech.socialsurvey.core.vo.RegionVO;
import com.realtech.socialsurvey.core.vo.UserFromSearchVO;


/**
 * @author manish
 *
 */
@Component
public class HierarchyFacadeImpl implements HierarchyFacade
{

    private static final Logger LOG = LoggerFactory.getLogger( HierarchyFacadeImpl.class );

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    SocialManagementService socialManagementService;


    /* (non-Javadoc)
     * @see com.realtech.socialsurvey.core.facade.HierarchyFacade#getCompanyHierarchyView(long)
     */
    @Override
    public HierarchyViewVO getCompanyHierarchyView( long companyId ) throws NonFatalException
    {
        LOG.info( "Inside companyHierarchyView() method" );
        int start = 0;
        HierarchyViewVO hierarchyView = new HierarchyViewVO();
        List<RegionVO> regionVOs = null;
        List<BranchVO> brancheVOs = null;
        List<UserFromSearchVO> userVOs = null;
        Company company = organizationManagementService.getCompanyById( companyId );

        try {
            LOG.debug( "fetching regions under company" );
            List<Region> regions = organizationManagementService.getRegionsForCompany( companyId );
            LOG.debug( "fetching branches under company" );
            List<Branch> branches = organizationManagementService.getBranchesUnderCompany( companyId );
            LOG.debug( "fetching users under company from solr" );
            List<UserFromSearch> users = organizationManagementService.getUsersUnderCompanyFromSolr( company, start );
            // Populating VOs for response
            brancheVOs = populateBranchVOList( branches );
            regionVOs = populateRegionVOList( regions );
            userVOs = populateUserVOList( users );

            // convert to vo and set to vo
            hierarchyView.setRegions( regionVOs );
            hierarchyView.setBranches( brancheVOs );
            hierarchyView.setUsers( userVOs );

        } catch ( NoRecordsFetchedException e ) {
            OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( companyId );
            if ( companySettings != null && companySettings.getContact_details() != null ) {
                ContactDetailsSettings companyContactDetail = companySettings.getContact_details();
                CompanyNotRegisteredVO companyNotRegisteredVO = new CompanyNotRegisteredVO();
                if ( companyContactDetail.getContact_numbers() != null ) {
                    companyNotRegisteredVO.setWorkContactNo( companyContactDetail.getContact_numbers().getWork() );
                }
                if ( companyContactDetail.getMail_ids() != null ) {
                    companyNotRegisteredVO.setWorkMailId( companyContactDetail.getMail_ids().getWork() );
                    // Get the user user name
                    if ( companyContactDetail.getMail_ids().getWork() != null
                        && !companyContactDetail.getMail_ids().getWork().isEmpty() ) {
                        User user = userManagementService.getUserByEmail( companyContactDetail.getMail_ids().getWork() );
                        companyNotRegisteredVO.setUserName(
                            user.getFirstName() + " " + ( user.getLastName() != null ? user.getLastName() : "" ) );
                    }
                }

                LOG.warn( "No records found for company branch or region. Reason: {}", e.getMessage() );
                companyNotRegisteredVO.setMessage("Company registration not complete");
                hierarchyView.setCompanyNotRegistered( companyNotRegisteredVO );
            }
        }
        return hierarchyView;
    }


    /**
     * Method to populate Region VO list.
     * @param regions
     * @return
     */
    private List<RegionVO> populateRegionVOList( List<Region> regions )
    {
        List<RegionVO> regionVOs = new ArrayList<>();
        if ( !CollectionUtils.isEmpty( regions ) ) {
            for ( Region region : regions ) {
                RegionVO regionVO = new RegionVO();
                regionVO.setRegion( region.getRegion() );
                if(region.getAddress1() != null)
                    regionVO.setAddress1( region.getAddress1() );
                else
                    regionVO.setAddress1("");
                if(region.getAddress2() != null)
                    regionVO.setAddress2( region.getAddress2() );
                else
                    regionVO.setAddress2("");
                regionVO.setRegionId( region.getRegionId() );
                regionVO.setRegionName( region.getRegionName() );
                regionVOs.add( regionVO );
            }
        }
        return regionVOs;
    }


    /**
     * Method to populate Branch VO list
     * @param branches
     * @return
     */
    private List<BranchVO> populateBranchVOList( List<Branch> branches )
    {
        List<BranchVO> branchVOs = new ArrayList<>();
        if ( !CollectionUtils.isEmpty( branches ) ) {
            for ( Branch branch : branches ) {
                BranchVO branchVO = new BranchVO();
                branchVO.setBranch( branch.getBranch() );
                if(branch.getAddress1() != null)
                    branchVO.setAddress1( branch.getAddress1());
                else
                    branchVO.setAddress1("");
                if(branch.getAddress2() != null)
                    branchVO.setAddress2( branch.getAddress2());
                else
                    branchVO.setAddress2("");
                branchVO.setBranchId( branch.getBranchId() );
                branchVOs.add( branchVO );
            }
        }
        return branchVOs;
    }


    /**
     * Method to populate User VO list
     * @param users
     * @return
     */
    private List<UserFromSearchVO> populateUserVOList( List<UserFromSearch> users )
    {
        List<UserFromSearchVO> userVOs = new ArrayList<>();
        if ( !CollectionUtils.isEmpty( users ) ) {
            for ( UserFromSearch user : users ) {
                UserFromSearchVO userVO = new UserFromSearchVO();
                userVO.setBranchAdmin( user.getIsBranchAdmin() );
                userVO.setAgent( user.getIsAgent() );
                userVO.setDisplayName( user.getDisplayName() );
                userVO.setEmailId( user.getEmailId() );
                userVO.setUserId( user.getUserId() );
                userVO.setOwner( user.getIsOwner() == 1 );
                userVO.setEmailId( user.getEmailId() );
                userVOs.add( userVO );
            }
        }
        return userVOs;
    }
}
