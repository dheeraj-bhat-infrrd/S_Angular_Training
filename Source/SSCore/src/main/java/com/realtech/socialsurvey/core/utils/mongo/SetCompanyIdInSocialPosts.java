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
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;


public class SetCompanyIdInSocialPosts
{
    public static final Logger LOG = LoggerFactory.getLogger( SetCompanyIdInSocialPosts.class );

    @Autowired
    private SocialPostDao socialPostDao;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private BatchTrackerService batchTrackerService;


    /**
     * Method to set company IDs in Social Posts
     */
    public void setCompanyIdInSocialPosts()
    {
        LOG.info( "Started Method setCompanyIdInSocialPost()" );

        try {

            // update last start time
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_SET_COMPANY_ID_IN_SOCIAL_POSTS,
                CommonConstants.BATCH_NAME_SET_COMPANY_ID_IN_SOCIAL_POSTS );
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

            //updating last run time for batch in database
            batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_SET_COMPANY_ID_IN_SOCIAL_POSTS );
        } catch ( Exception e ) {
            LOG.error( "Error in ImageUploader", e );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType(
                    CommonConstants.BATCH_TYPE_SET_COMPANY_ID_IN_SOCIAL_POSTS, e.getMessage() );
                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError(
                    CommonConstants.BATCH_NAME_SET_COMPANY_ID_IN_SOCIAL_POSTS, System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in ImageUploader " );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report excption mail to admin " );
            }
        }
    }
}
