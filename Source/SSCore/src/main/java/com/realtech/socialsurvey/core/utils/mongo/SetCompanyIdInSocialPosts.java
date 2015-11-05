package com.realtech.socialsurvey.core.utils.mongo;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.SocialPostDao;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.SocialPostCompanyIdMapping;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;


public class SetCompanyIdInSocialPosts
{
    public static final Logger LOG = LoggerFactory.getLogger( SetCompanyIdInSocialPosts.class );

    @Autowired
    private SocialPostDao socialPostDao;

    @Autowired
    private OrganizationManagementService organizationManagementService;


    /**
     * Method to set company IDs in Social Posts
     */
    public void setCompanyIdInSocialPosts()
    {
        LOG.info( "Started Method setCompanyIdInSocialPost()" );

        //Get all regions in MySQL
        List<Region> regions = organizationManagementService.getAllRegions();
        //Get all branches in MySQL
        List<Branch> branches = organizationManagementService.getAllBranches();
        //Get all users in MySQL
        List<User> users = organizationManagementService.getAllUsers();

        List<SocialPostCompanyIdMapping> socialPostCompanyIdMappings = new ArrayList<SocialPostCompanyIdMapping>();

        //fetch company ids for regions
        for ( Region region : regions ) {
            SocialPostCompanyIdMapping socialPostCompanyIdMapping = new SocialPostCompanyIdMapping();
            socialPostCompanyIdMapping.setEntityType( CommonConstants.REGION_ID_COLUMN );
            socialPostCompanyIdMapping.setEntityId( region.getRegionId() );
            socialPostCompanyIdMapping.setCompanyId( region.getCompany().getCompanyId() );
            socialPostCompanyIdMappings.add( socialPostCompanyIdMapping );
        }

        //fetch company ids for branches
        for ( Branch branch : branches ) {
            SocialPostCompanyIdMapping socialPostCompanyIdMapping = new SocialPostCompanyIdMapping();
            socialPostCompanyIdMapping.setEntityType( CommonConstants.BRANCH_ID_COLUMN );
            socialPostCompanyIdMapping.setEntityId( branch.getBranchId() );
            socialPostCompanyIdMapping.setCompanyId( branch.getCompany().getCompanyId() );
            socialPostCompanyIdMappings.add( socialPostCompanyIdMapping );
        }

        //fetch company ids for users
        for ( User user : users ) {
            SocialPostCompanyIdMapping socialPostCompanyIdMapping = new SocialPostCompanyIdMapping();
            socialPostCompanyIdMapping.setEntityType( CommonConstants.AGENT_ID_COLUMN );
            socialPostCompanyIdMapping.setEntityId( user.getUserId() );
            socialPostCompanyIdMapping.setCompanyId( user.getCompany().getCompanyId() );
            socialPostCompanyIdMappings.add( socialPostCompanyIdMapping );
        }

        //Set company IDs in mongo

        try {
            socialPostDao.updateCompanyIdForSocialPosts( socialPostCompanyIdMappings );
        } catch ( InvalidInputException e ) {
            LOG.error( "Error occured while updating companyIds for entities in social posts. Reason", e );
        }
    }
}
