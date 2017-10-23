package com.realtech.socialsurvey.core.services.reportingmanagement.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OverviewBranchDao;
import com.realtech.socialsurvey.core.dao.OverviewBranchMonthDao;
import com.realtech.socialsurvey.core.dao.OverviewBranchYearDao;
import com.realtech.socialsurvey.core.dao.OverviewCompanyDao;
import com.realtech.socialsurvey.core.dao.OverviewCompanyMonthDao;
import com.realtech.socialsurvey.core.dao.OverviewCompanyYearDao;
import com.realtech.socialsurvey.core.dao.OverviewRegionDao;
import com.realtech.socialsurvey.core.dao.OverviewRegionMonthDao;
import com.realtech.socialsurvey.core.dao.OverviewRegionYearDao;
import com.realtech.socialsurvey.core.dao.OverviewUserDao;
import com.realtech.socialsurvey.core.dao.OverviewUserMonthDao;
import com.realtech.socialsurvey.core.dao.OverviewUserYearDao;
import com.realtech.socialsurvey.core.entities.OverviewBranch;
import com.realtech.socialsurvey.core.entities.OverviewBranchMonth;
import com.realtech.socialsurvey.core.entities.OverviewBranchYear;
import com.realtech.socialsurvey.core.entities.OverviewCompany;
import com.realtech.socialsurvey.core.entities.OverviewCompanyMonth;
import com.realtech.socialsurvey.core.entities.OverviewCompanyYear;
import com.realtech.socialsurvey.core.entities.OverviewRegion;
import com.realtech.socialsurvey.core.entities.OverviewRegionMonth;
import com.realtech.socialsurvey.core.entities.OverviewRegionYear;
import com.realtech.socialsurvey.core.entities.OverviewUser;
import com.realtech.socialsurvey.core.entities.OverviewUserMonth;
import com.realtech.socialsurvey.core.entities.OverviewUserYear;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.reportingmanagement.OverviewManagement;


@DependsOn ( "generic")
@Component
public class OverviewManagementImpl implements OverviewManagement
{
    private static final Logger LOG = LoggerFactory.getLogger( OverviewManagementImpl.class );

    private static final String PROCESSED = "Processed";
    private static final String COMPLETED = "Completed";
    private static final String COMPLETEPERCENTAGE = "CompletePercentage";
    private static final String INCOMPLETE = "Incomplete";
    private static final String INCOMPLETEPERCENTAGE = "IncompletePercentage";
    private static final String SOCIALPOSTS = "SocialPosts";
    private static final String ZILLOWREVIEWS = "ZillowReviews";
    private static final String UNPROCESSED = "Unprocessed";
    private static final String UNASSIGNED = "Unassigned";
    private static final String DUPLICATE = "Duplicate";
    private static final String CORRUPTED = "Corrupted";
    private static final String RATING = "Rating";
    private static final String TOTALREVIEW = "TotalReview";
    private static final String THIRDPARTY = "ThirdParty";
    private static final String SPSSCORE = "SpsScore";
    private static final String DETRACTORPERCENTAGE = "DetractorPercentage";
    private static final String PASSIVESPERCENTAGE = "PassivesPercentage";
    private static final String PROMOTERPERCENTAGE = "PromoterPercentage";

    @Autowired
    private OverviewUserDao overviewUserDao;

    @Autowired
    private OverviewBranchDao overviewBranchDao;

    @Autowired
    private OverviewRegionDao overviewRegionDao;

    @Autowired
    private OverviewCompanyDao overviewCompanyDao;

    @Autowired
    private OverviewUserMonthDao overviewUserMonthDao;

    @Autowired
    private OverviewBranchMonthDao overviewBranchMonthDao;

    @Autowired
    private OverviewRegionMonthDao overviewRegionMonthDao;

    @Autowired
    private OverviewCompanyMonthDao overviewCompanyMonthDao;

    @Autowired
    private OverviewUserYearDao overviewUserYearDao;

    @Autowired
    private OverviewBranchYearDao overviewBranchYearDao;

    @Autowired
    private OverviewRegionYearDao overviewRegionYearDao;

    @Autowired
    private OverviewCompanyYearDao overviewCompanyYearDao;


    @Override
    public OverviewUser fetchOverviewUserDetails( long entityId, String entityType ) throws NonFatalException
    {
        String overviewUserId = overviewUserDao.getOverviewUserId( entityId );
        OverviewUser overviewUser = null;
        if ( overviewUserId != null ) {
            overviewUser = overviewUserDao.findOverviewUser( OverviewUser.class, overviewUserId );
        }
        return overviewUser;
    }


    @Override
    public OverviewBranch fetchOverviewBranchDetails( long entityId, String entityType ) throws NonFatalException
    {


        String overviewBranchId = overviewBranchDao.getOverviewBranchId( entityId );
        OverviewBranch overviewBranch = null;
        if ( overviewBranchId != null ) {
            overviewBranch = overviewBranchDao.findOverviewBranch( OverviewBranch.class, overviewBranchId );
        }
        return overviewBranch;

    }


    @Override
    public OverviewRegion fetchOverviewRegionDetails( long entityId, String entityType ) throws NonFatalException
    {
        String overviewRegionId = overviewRegionDao.getOverviewRegionId( entityId );
        OverviewRegion overviewRegion = null;
        if ( overviewRegionId != null ) {
            overviewRegion = overviewRegionDao.findOverviewRegion( OverviewRegion.class, overviewRegionId );
        }
        return overviewRegion;
    }


    @Override
    public OverviewCompany fetchOverviewCompanyDetails( long entityId, String entityType ) throws NonFatalException
    {
        String overviewCompanyId = overviewCompanyDao.getOverviewCompanyId( entityId );
        OverviewCompany overviewCompany = null;
        if ( overviewCompanyId != null ) {
            overviewCompany = overviewCompanyDao.findOverviewCompany( OverviewCompany.class, overviewCompanyId );
        }

        return overviewCompany;
    }


    @Override
    @Transactional ( value = "transactionManagerForReporting")
    public Map<String, Object> fetchAllTimeOverview( long entityId, String entityType ) throws NonFatalException
    {
        LOG.info( "Method to fetchAllTimeOverview for entityId : {} , entityType : {} started", entityId,
            entityType );
        Map<String, Object> overviewMap = new HashMap<>();
        if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
            OverviewUser overviewUser = fetchOverviewUserDetails( entityId, entityType );
            if ( overviewUser != null ) {
                overviewMap.put( PROCESSED, overviewUser.getProcessed() );
                overviewMap.put( COMPLETED, overviewUser.getCompleted() );
                overviewMap.put( COMPLETEPERCENTAGE, overviewUser.getCompletedPercentage() );
                overviewMap.put( INCOMPLETE, overviewUser.getIncomplete() );
                overviewMap.put( INCOMPLETEPERCENTAGE, overviewUser.getIncompletePercentage() );
                overviewMap.put( SOCIALPOSTS, overviewUser.getSocialPosts() );
                overviewMap.put( ZILLOWREVIEWS, overviewUser.getZillowReviews() );
                overviewMap.put( UNPROCESSED, overviewUser.getUnprocessed() );
                overviewMap.put( UNASSIGNED, overviewUser.getUnassigned() );
                overviewMap.put( DUPLICATE, overviewUser.getTotalDuplicate() );
                overviewMap.put( CORRUPTED, overviewUser.getTotalCorrupted() );
                overviewMap.put( RATING, overviewUser.getRating() );
                overviewMap.put( TOTALREVIEW, overviewUser.getTotalReviews() );
                overviewMap.put( THIRDPARTY, overviewUser.getThirdParty() );
            }

        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            OverviewBranch overviewBranch = fetchOverviewBranchDetails( entityId, entityType );
            if ( overviewBranch != null ) {
                overviewMap.put( PROCESSED, overviewBranch.getProcessed() );
                overviewMap.put( COMPLETED, overviewBranch.getCompleted() );
                overviewMap.put( COMPLETEPERCENTAGE, overviewBranch.getCompletedPercentage() );
                overviewMap.put( INCOMPLETE, overviewBranch.getIncomplete() );
                overviewMap.put( INCOMPLETEPERCENTAGE, overviewBranch.getIncompletePercentage() );
                overviewMap.put( SOCIALPOSTS, overviewBranch.getSocialPosts() );
                overviewMap.put( ZILLOWREVIEWS, overviewBranch.getZillowReviews() );
                overviewMap.put( UNPROCESSED, overviewBranch.getUnprocessed() );
                overviewMap.put( UNASSIGNED, overviewBranch.getUnassigned() );
                overviewMap.put( DUPLICATE, overviewBranch.getTotalDuplicate() );
                overviewMap.put( CORRUPTED, overviewBranch.getTotalCorrupted() );
                overviewMap.put( RATING, overviewBranch.getRating() );
                overviewMap.put( TOTALREVIEW, overviewBranch.getTotalReviews() );
                overviewMap.put( THIRDPARTY, overviewBranch.getThirdParty() );
            }


        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            OverviewRegion overviewRegion = fetchOverviewRegionDetails( entityId, entityType );
            if ( overviewRegion != null ) {
                overviewMap.put( PROCESSED, overviewRegion.getProcessed() );
                overviewMap.put( COMPLETED, overviewRegion.getCompleted() );
                overviewMap.put( COMPLETEPERCENTAGE, overviewRegion.getCompletedPercentage() );
                overviewMap.put( INCOMPLETE, overviewRegion.getIncomplete() );
                overviewMap.put( INCOMPLETEPERCENTAGE, overviewRegion.getIncompletePercentage() );
                overviewMap.put( SOCIALPOSTS, overviewRegion.getSocialPosts() );
                overviewMap.put( ZILLOWREVIEWS, overviewRegion.getZillowReviews() );
                overviewMap.put( UNPROCESSED, overviewRegion.getUnprocessed() );
                overviewMap.put( UNASSIGNED, overviewRegion.getUnassigned() );
                overviewMap.put( DUPLICATE, overviewRegion.getTotalDuplicate() );
                overviewMap.put( CORRUPTED, overviewRegion.getTotalCorrupted() );
                overviewMap.put( RATING, overviewRegion.getRating() );
                overviewMap.put( TOTALREVIEW, overviewRegion.getTotalReviews() );
                overviewMap.put( THIRDPARTY, overviewRegion.getThirdParty() );
            }

        } else if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            OverviewCompany overviewCompany = fetchOverviewCompanyDetails( entityId, entityType );
            if ( overviewCompany != null ) {
                overviewMap.put( PROCESSED, overviewCompany.getProcessed() );
                overviewMap.put( COMPLETED, overviewCompany.getCompleted() );
                overviewMap.put( COMPLETEPERCENTAGE, overviewCompany.getCompletedPercentage() );
                overviewMap.put( INCOMPLETE, overviewCompany.getIncomplete() );
                overviewMap.put( INCOMPLETEPERCENTAGE, overviewCompany.getIncompletePercentage() );
                overviewMap.put( SOCIALPOSTS, overviewCompany.getSocialPosts() );
                overviewMap.put( ZILLOWREVIEWS, overviewCompany.getZillowReviews() );
                overviewMap.put( UNPROCESSED, overviewCompany.getUnprocessed() );
                overviewMap.put( UNASSIGNED, overviewCompany.getUnassigned() );
                overviewMap.put( DUPLICATE, overviewCompany.getTotalDuplicate() );
                overviewMap.put( CORRUPTED, overviewCompany.getTotalCorrupted() );
                overviewMap.put( RATING, overviewCompany.getRating() );
                overviewMap.put( TOTALREVIEW, overviewCompany.getTotalReviews() );
                overviewMap.put( THIRDPARTY, overviewCompany.getThirdParty() );

            }
        }
        LOG.info( "Method to fetchAllTimeOverview for entityId : {} , entityType : {} ended", entityId,
            entityType );

        return overviewMap;
    }


    @Override
    @Transactional ( value = "transactionManagerForReporting")
    public Map<String, Object> fetchSpsAllTime( long entityId, String entityType ) throws NonFatalException
    {
        LOG.info( "Method to fetchSpsAllTime for entityId : {} , entityType : {} started", entityId,
            entityType );
        Map<String, Object> overviewMap = new HashMap<>();
        if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
            OverviewUser overviewUser = fetchOverviewUserDetails( entityId, entityType );
            if ( overviewUser != null ) {
                overviewMap.put( SPSSCORE, overviewUser.getSpsScore() );
                overviewMap.put( DETRACTORPERCENTAGE, overviewUser.getDetractorPercentage() );
                overviewMap.put( PASSIVESPERCENTAGE, overviewUser.getPassivesPercentage() );
                overviewMap.put( PROMOTERPERCENTAGE, overviewUser.getPromoterPercentage() );
            }


        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            OverviewBranch overviewBranch = fetchOverviewBranchDetails( entityId, entityType );
            if ( overviewBranch != null ) {
                overviewMap.put( SPSSCORE, overviewBranch.getSpsScore() );
                overviewMap.put( DETRACTORPERCENTAGE, overviewBranch.getDetractorPercentage() );
                overviewMap.put( PASSIVESPERCENTAGE, overviewBranch.getPassivesPercentage() );
                overviewMap.put( PROMOTERPERCENTAGE, overviewBranch.getPromoterPercentage() );
            }


        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            OverviewRegion overviewRegion = fetchOverviewRegionDetails( entityId, entityType );
            if ( overviewRegion != null ) {
                overviewMap.put( SPSSCORE, overviewRegion.getSpsScore() );
                overviewMap.put( DETRACTORPERCENTAGE, overviewRegion.getDetractorPercentage() );
                overviewMap.put( PASSIVESPERCENTAGE, overviewRegion.getPassivesPercentage() );
                overviewMap.put( PROMOTERPERCENTAGE, overviewRegion.getPromoterPercentage() );
            }

        } else if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            OverviewCompany overviewCompany = fetchOverviewCompanyDetails( entityId, entityType );
            if ( overviewCompany != null ) {
                overviewMap.put( SPSSCORE, overviewCompany.getSpsScore() );
                overviewMap.put( DETRACTORPERCENTAGE, overviewCompany.getDetractorPercentage() );
                overviewMap.put( PASSIVESPERCENTAGE, overviewCompany.getPassivesPercentage() );
                overviewMap.put( PROMOTERPERCENTAGE, overviewCompany.getPromoterPercentage() );
            }

        }
        LOG.info( "Method to fetchSpsAllTime for entityId : {} , entityType : {} ended", entityId,
            entityType );
        return overviewMap;

    }

    @Override
    @Transactional ( value = "transactionManagerForReporting")
    public Map<String, Object> fetchOverviewDetailsBasedOnMonth( long entityId, String entityType, int month, int year )
        throws NonFatalException
    {
        LOG.info( "Method to fetchOverviewDetailsBasedOnMonth for entityId : {} , entityType : {} started", entityId,
            entityType );


        Map<String, Object> overviewMap = new HashMap<>();

        if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
            OverviewUserMonth overviewUserMonth = overviewUserMonthDao.fetchOverviewForUserBasedOnMonth( entityId, month,
                year );
            if ( overviewUserMonth != null ) {
                overviewMap.put( PROCESSED, overviewUserMonth.getProcessed() );
                overviewMap.put( COMPLETED, overviewUserMonth.getCompleted() );
                overviewMap.put( COMPLETEPERCENTAGE, overviewUserMonth.getCompletePercentage() );
                overviewMap.put( INCOMPLETE, overviewUserMonth.getIncomplete() );
                overviewMap.put( INCOMPLETEPERCENTAGE, overviewUserMonth.getIncompletePercentage() );
                overviewMap.put( SOCIALPOSTS, overviewUserMonth.getSocialPosts() );
                overviewMap.put( ZILLOWREVIEWS, overviewUserMonth.getZillowReviews() );
                overviewMap.put( UNPROCESSED, overviewUserMonth.getUnprocessed() );
                overviewMap.put( UNASSIGNED, overviewUserMonth.getUnassigned() );
                overviewMap.put( DUPLICATE, overviewUserMonth.getDuplicate() );
                overviewMap.put( CORRUPTED, overviewUserMonth.getCorrupted() );
                overviewMap.put( RATING, overviewUserMonth.getRating() );
                overviewMap.put( TOTALREVIEW, overviewUserMonth.getTotalReview() );
                overviewMap.put( THIRDPARTY, overviewUserMonth.getThirdParty() );

            }

        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            OverviewBranchMonth overviewBranchMonth = overviewBranchMonthDao.fetchOverviewForBranchBasedOnMonth( entityId,
                month, year );
            if ( overviewBranchMonth != null ) {
                overviewMap.put( PROCESSED, overviewBranchMonth.getProcessed() );
                overviewMap.put( COMPLETED, overviewBranchMonth.getCompleted() );
                overviewMap.put( COMPLETEPERCENTAGE, overviewBranchMonth.getCompletePercentage() );
                overviewMap.put( INCOMPLETE, overviewBranchMonth.getIncomplete() );
                overviewMap.put( INCOMPLETEPERCENTAGE, overviewBranchMonth.getIncompletePercentage() );
                overviewMap.put( SOCIALPOSTS, overviewBranchMonth.getSocialPosts() );
                overviewMap.put( ZILLOWREVIEWS, overviewBranchMonth.getZillowReviews() );
                overviewMap.put( UNPROCESSED, overviewBranchMonth.getUnprocessed() );
                overviewMap.put( UNASSIGNED, overviewBranchMonth.getUnassigned() );
                overviewMap.put( DUPLICATE, overviewBranchMonth.getDuplicate() );
                overviewMap.put( CORRUPTED, overviewBranchMonth.getCorrupted() );
                overviewMap.put( RATING, overviewBranchMonth.getRating() );
                overviewMap.put( TOTALREVIEW, overviewBranchMonth.getTotalReview() );
                overviewMap.put( THIRDPARTY, overviewBranchMonth.getThirdParty() );

            }

        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            OverviewRegionMonth overviewRegionMonth = overviewRegionMonthDao.fetchOverviewForRegionBasedOnMonth( entityId,
                month, year );
            if ( overviewRegionMonth != null ) {
                overviewMap.put( PROCESSED, overviewRegionMonth.getProcessed() );
                overviewMap.put( COMPLETED, overviewRegionMonth.getCompleted() );
                overviewMap.put( COMPLETEPERCENTAGE, overviewRegionMonth.getCompletePercentage() );
                overviewMap.put( INCOMPLETE, overviewRegionMonth.getIncomplete() );
                overviewMap.put( INCOMPLETEPERCENTAGE, overviewRegionMonth.getIncompletePercentage() );
                overviewMap.put( SOCIALPOSTS, overviewRegionMonth.getSocialPosts() );
                overviewMap.put( ZILLOWREVIEWS, overviewRegionMonth.getZillowReviews() );
                overviewMap.put( UNPROCESSED, overviewRegionMonth.getUnprocessed() );
                overviewMap.put( UNASSIGNED, overviewRegionMonth.getUnassigned() );
                overviewMap.put( DUPLICATE, overviewRegionMonth.getDuplicate() );
                overviewMap.put( CORRUPTED, overviewRegionMonth.getCorrupted() );
                overviewMap.put( RATING, overviewRegionMonth.getRating() );
                overviewMap.put( TOTALREVIEW, overviewRegionMonth.getTotalReview() );
                overviewMap.put( THIRDPARTY, overviewRegionMonth.getThirdParty() );

            }

        } else if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            OverviewCompanyMonth overviewCompanyMonth = overviewCompanyMonthDao.fetchOverviewForCompanyBasedOnMonth( entityId,
                month, year );
            if ( overviewCompanyMonth != null ) {
                overviewMap.put( PROCESSED, overviewCompanyMonth.getProcessed() );
                overviewMap.put( COMPLETED, overviewCompanyMonth.getCompleted() );
                overviewMap.put( COMPLETEPERCENTAGE, overviewCompanyMonth.getCompletePercentage() );
                overviewMap.put( INCOMPLETE, overviewCompanyMonth.getIncomplete() );
                overviewMap.put( INCOMPLETEPERCENTAGE, overviewCompanyMonth.getIncompletePercentage() );
                overviewMap.put( SOCIALPOSTS, overviewCompanyMonth.getSocialPosts() );
                overviewMap.put( ZILLOWREVIEWS, overviewCompanyMonth.getZillowReviews() );
                overviewMap.put( UNPROCESSED, overviewCompanyMonth.getUnprocessed() );
                overviewMap.put( UNASSIGNED, overviewCompanyMonth.getUnassigned() );
                overviewMap.put( DUPLICATE, overviewCompanyMonth.getDuplicate() );
                overviewMap.put( CORRUPTED, overviewCompanyMonth.getCorrupted() );
                overviewMap.put( RATING, overviewCompanyMonth.getRating() );
                overviewMap.put( TOTALREVIEW, overviewCompanyMonth.getTotalReview() );
                overviewMap.put( THIRDPARTY, overviewCompanyMonth.getThirdParty() );
            }

        }
        LOG.info( "Method to fetchOverviewDetailsBasedOnMonth for entityId : {} , entityType : {} ended", entityId,
            entityType );

        return overviewMap;
    }


    @Override
    @Transactional ( value = "transactionManagerForReporting")
    public Map<String, Object> fetchOverviewDetailsBasedOnYear( long entityId, String entityType, int year )
        throws NonFatalException
    {
        LOG.info( "Method to fetchOverviewDetailsBasedOnYear for entityId : {} , entityType : {} started", entityId,
            entityType );

        Map<String, Object> overviewMap = new HashMap<>();

        if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
            OverviewUserYear overviewUserYear = overviewUserYearDao.fetchOverviewForUserBasedOnYear( entityId, year );
            if ( overviewUserYear != null ) {
                overviewMap.put( PROCESSED, overviewUserYear.getProcessed() );
                overviewMap.put( COMPLETED, overviewUserYear.getCompleted() );
                overviewMap.put( COMPLETEPERCENTAGE, overviewUserYear.getCompletePercentage() );
                overviewMap.put( INCOMPLETE, overviewUserYear.getIncomplete() );
                overviewMap.put( INCOMPLETEPERCENTAGE, overviewUserYear.getIncompletePercentage() );
                overviewMap.put( SOCIALPOSTS, overviewUserYear.getSocialPosts() );
                overviewMap.put( ZILLOWREVIEWS, overviewUserYear.getZillowReviews() );
                overviewMap.put( UNPROCESSED, overviewUserYear.getUnprocessed() );
                overviewMap.put( UNASSIGNED, overviewUserYear.getUnassigned() );
                overviewMap.put( DUPLICATE, overviewUserYear.getDuplicate() );
                overviewMap.put( CORRUPTED, overviewUserYear.getCorrupted() );
                overviewMap.put( RATING, overviewUserYear.getRating() );
                overviewMap.put( TOTALREVIEW, overviewUserYear.getTotalReview() );
                overviewMap.put( THIRDPARTY, overviewUserYear.getThirdParty() );

            }

        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            OverviewBranchYear overviewBranchYear = overviewBranchYearDao.fetchOverviewForBranchBasedOnYear( entityId, year );
            if ( overviewBranchYear != null ) {
                overviewMap.put( PROCESSED, overviewBranchYear.getProcessed() );
                overviewMap.put( COMPLETED, overviewBranchYear.getCompleted() );
                overviewMap.put( COMPLETEPERCENTAGE, overviewBranchYear.getCompletePercentage() );
                overviewMap.put( INCOMPLETE, overviewBranchYear.getIncomplete() );
                overviewMap.put( INCOMPLETEPERCENTAGE, overviewBranchYear.getIncompletePercentage() );
                overviewMap.put( SOCIALPOSTS, overviewBranchYear.getSocialPosts() );
                overviewMap.put( ZILLOWREVIEWS, overviewBranchYear.getZillowReviews() );
                overviewMap.put( UNPROCESSED, overviewBranchYear.getUnprocessed() );
                overviewMap.put( UNASSIGNED, overviewBranchYear.getUnassigned() );
                overviewMap.put( DUPLICATE, overviewBranchYear.getDuplicate() );
                overviewMap.put( CORRUPTED, overviewBranchYear.getCorrupted() );
                overviewMap.put( RATING, overviewBranchYear.getRating() );
                overviewMap.put( TOTALREVIEW, overviewBranchYear.getTotalReview() );
                overviewMap.put( THIRDPARTY, overviewBranchYear.getThirdParty() );

            }

        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            OverviewRegionYear overviewRegionYear = overviewRegionYearDao.fetchOverviewForRegionBasedOnYear( entityId, year );
            if ( overviewRegionYear != null ) {
                overviewMap.put( PROCESSED, overviewRegionYear.getProcessed() );
                overviewMap.put( COMPLETED, overviewRegionYear.getCompleted() );
                overviewMap.put( COMPLETEPERCENTAGE, overviewRegionYear.getCompletePercentage() );
                overviewMap.put( INCOMPLETE, overviewRegionYear.getIncomplete() );
                overviewMap.put( INCOMPLETEPERCENTAGE, overviewRegionYear.getIncompletePercentage() );
                overviewMap.put( SOCIALPOSTS, overviewRegionYear.getSocialPosts() );
                overviewMap.put( ZILLOWREVIEWS, overviewRegionYear.getZillowReviews() );
                overviewMap.put( UNPROCESSED, overviewRegionYear.getUnprocessed() );
                overviewMap.put( UNASSIGNED, overviewRegionYear.getUnassigned() );
                overviewMap.put( DUPLICATE, overviewRegionYear.getDuplicate() );
                overviewMap.put( CORRUPTED, overviewRegionYear.getCorrupted() );
                overviewMap.put( RATING, overviewRegionYear.getRating() );
                overviewMap.put( TOTALREVIEW, overviewRegionYear.getTotalReview() );
                overviewMap.put( THIRDPARTY, overviewRegionYear.getThirdParty() );

            }

        } else if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            OverviewCompanyYear overviewCompanyYear = overviewCompanyYearDao.fetchOverviewForCompanyBasedOnYear( entityId,
                year );
            if ( overviewCompanyYear != null ) {
                overviewMap.put( PROCESSED, overviewCompanyYear.getProcessed() );
                overviewMap.put( COMPLETED, overviewCompanyYear.getCompleted() );
                overviewMap.put( COMPLETEPERCENTAGE, overviewCompanyYear.getCompletePercentage() );
                overviewMap.put( INCOMPLETE, overviewCompanyYear.getIncomplete() );
                overviewMap.put( INCOMPLETEPERCENTAGE, overviewCompanyYear.getIncompletePercentage() );
                overviewMap.put( SOCIALPOSTS, overviewCompanyYear.getSocialPosts() );
                overviewMap.put( ZILLOWREVIEWS, overviewCompanyYear.getZillowReviews() );
                overviewMap.put( UNPROCESSED, overviewCompanyYear.getUnprocessed() );
                overviewMap.put( UNASSIGNED, overviewCompanyYear.getUnassigned() );
                overviewMap.put( DUPLICATE, overviewCompanyYear.getDuplicate() );
                overviewMap.put( CORRUPTED, overviewCompanyYear.getCorrupted() );
                overviewMap.put( RATING, overviewCompanyYear.getRating() );
                overviewMap.put( TOTALREVIEW, overviewCompanyYear.getTotalReview() );
                overviewMap.put( THIRDPARTY, overviewCompanyYear.getThirdParty() );

            }

        }
        LOG.info( "Method to fetchOverviewDetailsBasedOnYear for entityId : {} , entityType : {} ended", entityId, entityType );
        return overviewMap;
    }
}
