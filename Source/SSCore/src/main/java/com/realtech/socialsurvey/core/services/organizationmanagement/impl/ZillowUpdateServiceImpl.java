package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import java.sql.Timestamp;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.RegionDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ZillowUpdateService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;


@Component
public class ZillowUpdateServiceImpl implements ZillowUpdateService
{
    private static final Logger LOG = LoggerFactory.getLogger( ZillowUpdateServiceImpl.class );

    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private RegionDao regionDao;

    @Resource
    @Qualifier ( "branch")
    private BranchDao branchDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ProfileManagementService profileManagementService;

    @Autowired
    private SolrSearchService solrSearchService;

    @Async
    @Override
    public void updateZillowReviewCountAndAverage( String collectionName, long iden, double zillowReviewCount,
        double zillowAverage )
    {
        if ( collectionName == null || collectionName.isEmpty() ) {
            LOG.error( "Collection name passed cannot be null or empty" );
            return;
        }
        if ( iden <= 0l ) {
            LOG.error( "Invalid iden passed as argument" );
            return;
        }
        LOG.info( "Updating the zillow review count and average in collection : " + collectionName );
        try {
            // update MySQL with zillow review score and average
            switch ( collectionName ) {
                case MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION:
                    Company company = companyDao.findById( Company.class, iden );
                    if ( company == null ) {
                        LOG.error( "Could not find company information for id : " + iden );
                        return;
                    }

                    company.setZillowAverageScore( zillowAverage );
                    company.setZillowReviewCount( new Double( zillowReviewCount ).intValue() );
                    company.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );

                    LOG.info( "Updating zillow average and review count for company id : " + iden );
                    companyDao.update( company );
                    break;
                case MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION:
                    Region region = regionDao.findById( Region.class, iden );
                    if ( region == null ) {
                        LOG.error( "Could not find region information for id : " + iden );
                        return;
                    }

                    region.setZillowAverageScore( zillowAverage );
                    region.setZillowReviewCount( new Double( zillowReviewCount ).intValue() );
                    region.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );

                    LOG.info( "Updating zillow average and review count for region id : " + iden );
                    regionDao.update( region );
                    break;
                case MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION:
                    Branch branch = branchDao.findById( Branch.class, iden );
                    if ( branch == null ) {
                        LOG.error( "Could not find branch information for id : " + iden );
                        return;
                    }

                    branch.setZillowAverageScore( zillowAverage );
                    branch.setZillowReviewCount( new Double( zillowReviewCount ).intValue() );
                    branch.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );

                    LOG.info( "Updating zillow average and review count for branch id : " + iden );
                    branchDao.update( branch );
                    break;
                case MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION:
                    User user = userDao.findById( User.class, iden );
                    if ( user == null ) {
                        LOG.error( "Could not find company information for id : " + iden );
                        return;
                    }

                    user.setZillowAverageScore( zillowAverage );
                    user.setZillowReviewCount( new Double( zillowReviewCount ).intValue() );
                    user.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );

                    LOG.info( "Updating zillow average and review count for user id : " + iden );
                    userDao.update( user );

                    // updating solr review count for agent
                    long reviewCount = 0;
                    AgentSettings agentSettings = organizationUnitSettingsDao.fetchAgentSettingsById( iden );
                    if ( agentSettings != null && agentSettings.getSurvey_settings() != null
                        && agentSettings.getSurvey_settings().getAuto_post_score() > 0 ) {
                        reviewCount = profileManagementService.getReviewsCount( iden, 0d, new Double( agentSettings
                            .getSurvey_settings().getAuto_post_score() ).doubleValue(),
                            CommonConstants.PROFILE_LEVEL_INDIVIDUAL, false, true, true, new Long( zillowReviewCount + "" )
                                .longValue() );
                    }
                    if ( reviewCount > 0 ) {
                        solrSearchService.editUserInSolr( iden, CommonConstants.REVIEW_COUNT_SOLR, reviewCount + "" );
                    }
                    break;
                default:
                    LOG.error( "Invalid collection name specified for updating zillow average and collection" );
                    return;
            }
        } catch ( Exception e ) {
            LOG.error( "Exception occurred while updating zillow review count and average. Reason : " + e );
            return;
        }
        LOG.info( "Updated the zillow review count and average in collection : " + collectionName );
    }
}
