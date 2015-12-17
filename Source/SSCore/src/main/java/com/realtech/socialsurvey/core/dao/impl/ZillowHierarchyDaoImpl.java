package com.realtech.socialsurvey.core.dao.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.ZillowHierarchyDao;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


@Component
public class ZillowHierarchyDaoImpl implements ZillowHierarchyDao
{
    private static final Logger LOG = LoggerFactory.getLogger( ZillowHierarchyDaoImpl.class );
    private String fetchIdsUnderBranchQuery = "select distinct CASE WHEN br.IS_ZILLOW_CONNECTED = :isZillowConnected THEN br.BRANCH_ID ELSE -1 END AS BRANCH_ID , br.ZILLOW_REVIEW_COUNT as BRANCH_ZILLOW_COUNT, br.ZILLOW_AVERAGE_SCORE as BRANCH_ZILLOW_AVERAGE, CASE WHEN us.IS_ZILLOW_CONNECTED = :isZillowConnected THEN us_pro.USER_ID ELSE -1 END AS USER_ID , us.ZILLOW_REVIEW_COUNT  as INDIVIDUAL_ZILLOW_COUNT, us.ZILLOW_AVERAGE_SCORE as INDIVIDUAL_ZILLOW_AVERAGE from BRANCH as br LEFT join USER_PROFILE as us_pro ON br.BRANCH_ID = us_pro.BRANCH_ID and us_pro.PROFILES_MASTER_ID = :agentProfileMasterId and us_pro.STATUS != :status LEFT join USERS as us on us_pro.USER_ID = us.USER_ID where br.BRANCH_ID = :branchId and ( br.IS_ZILLOW_CONNECTED = :isZillowConnected OR us.IS_ZILLOW_CONNECTED = :isZillowConnected  )";
    private String fetchIdsUnderRegionQuery = "select distinct CASE WHEN reg.IS_ZILLOW_CONNECTED = :isZillowConnected THEN reg.REGION_ID ELSE -1 END AS REGION_ID, reg.ZILLOW_REVIEW_COUNT as REGION_ZILLOW_COUNT , reg.ZILLOW_AVERAGE_SCORE as REGION_ZILLOW_AVERAGE , CASE WHEN br.IS_ZILLOW_CONNECTED = :isZillowConnected THEN br.BRANCH_ID ELSE -1 END AS BRANCH_ID , br.ZILLOW_REVIEW_COUNT as BRANCH_ZILLOW_COUNT , br.ZILLOW_AVERAGE_SCORE as BRANCH_ZILLOW_AVERAGE , CASE WHEN us.IS_ZILLOW_CONNECTED = :isZillowConnected THEN us_pro.USER_ID ELSE -1 END AS USER_ID , us.ZILLOW_REVIEW_COUNT as INDIVIDUAL_ZILLOW_COUNT , us.ZILLOW_AVERAGE_SCORE as INDIVIDUAL_ZILLOW_AVERAGE from REGION as reg LEFT join BRANCH as br ON reg.REGION_ID = br.REGION_ID and br.STATUS != :status LEFT join USER_PROFILE as us_pro ON br.BRANCH_ID = us_pro.BRANCH_ID and us_pro.PROFILES_MASTER_ID = :agentProfileMasterId and us_pro.STATUS != :status LEFT join USERS as us on us_pro.USER_ID = us.USER_ID where reg.REGION_ID = :regionId and (reg.IS_ZILLOW_CONNECTED = :isZillowConnected OR br.IS_ZILLOW_CONNECTED = :isZillowConnected OR us.IS_ZILLOW_CONNECTED = :isZillowConnected )";
    private String fetchIdsUnderCompanyQuery = "select distinct CASE WHEN co.IS_ZILLOW_CONNECTED = :isZillowConnected THEN co.COMPANY_ID ELSE -1 END AS COMPANY_ID , co.ZILLOW_REVIEW_COUNT as COMPANY_ZILLOW_COUNT , co.ZILLOW_AVERAGE_SCORE as COMPANY_ZILLOW_AVERAGE , CASE WHEN reg.IS_ZILLOW_CONNECTED = :isZillowConnected THEN reg.REGION_ID ELSE -1 END AS REGION_ID , reg.ZILLOW_REVIEW_COUNT as REGION_ZILLOW_COUNT , reg.ZILLOW_AVERAGE_SCORE as REGION_ZILLOW_AVERAGE , CASE WHEN br.IS_ZILLOW_CONNECTED = :isZillowConnected THEN br.BRANCH_ID ELSE -1 END AS BRANCH_ID , br.ZILLOW_REVIEW_COUNT  as BRANCH_ZILLOW_COUNT , br.ZILLOW_AVERAGE_SCORE as BRANCH_ZILLOW_AVERAGE , CASE WHEN us.IS_ZILLOW_CONNECTED = :isZillowConnected THEN us_pro.USER_ID ELSE -1 END AS USER_ID , us.ZILLOW_REVIEW_COUNT as INDIVIDUAL_ZILLOW_COUNT , us.ZILLOW_AVERAGE_SCORE as INDIVIDUAL_ZILLOW_AVERAGE from COMPANY as co LEFT join REGION as reg ON co.COMPANY_ID = reg.COMPANY_ID and reg.STATUS != :status LEFT join BRANCH as br ON reg.REGION_ID = br.REGION_ID and br.STATUS != :status LEFT join USER_PROFILE as us_pro ON br.BRANCH_ID = us_pro.BRANCH_ID and us_pro.PROFILES_MASTER_ID = :agentProfileMasterId and us_pro.STATUS != :status LEFT join USERS as us on us_pro.USER_ID = us.USER_ID where co.COMPANY_ID = :companyId and (co.IS_ZILLOW_CONNECTED = :isZillowConnected OR reg.IS_ZILLOW_CONNECTED = :isZillowConnected OR br.IS_ZILLOW_CONNECTED = :isZillowConnected OR us.IS_ZILLOW_CONNECTED = :isZillowConnected)";

    @Autowired
    SessionFactory sessionFactory;


    /**
     * Method to fetch all individual ids under a branch
     * @param branchId
     * @throws InvalidInputException
     * */
    @Override
    public Map<String, Set<Long>> getIdsUnderBranchConnectedToZillow( long branchId ) throws InvalidInputException
    {
        if ( branchId <= 0l ) {
            LOG.info( "Invalid branch id passed in getIdsUnderBranchConnectedToZillow" );
            throw new InvalidInputException( "Invalid branch id passed in getIdsUnderBranchConnectedToZillow" );
        }

        LOG.info( "Method fetch all ids under a branch connected to zillow, getIdsUnderBranchConnectedToZillow started" );
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery( fetchIdsUnderBranchQuery );
        query.setParameter( "isZillowConnected", CommonConstants.YES );
        query.setParameter( "agentProfileMasterId", CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID );
        query.setParameter( "branchId", branchId );
        query.setParameter( "status", CommonConstants.STATUS_INACTIVE );

        LOG.info( "Fetching ids under a branch conncected to zillow for branch id : " + branchId );
        List<Object[]> rows = (List<Object[]>) query.list();
        LOG.info( "Fetched ids under a branch conncected to zillow for branch id : " + branchId );

        Set<Long> zillowConnectedIndividualIds = new HashSet<Long>();

        if ( rows == null || rows.isEmpty() ) {
            LOG.error( "No records found for branch id : " + branchId );
            return null;
        }

        LOG.info( "Parsing the information recieved for branch id : " + branchId );
        for ( Object[] row : rows ) {
            // row 3 - agent id
            Long zillowConnectedIndividualId = new Long( String.valueOf( row[3] ) );
            if ( zillowConnectedIndividualId != -1 )
                zillowConnectedIndividualIds.add( zillowConnectedIndividualId );
        }
        LOG.info( "Parsed the information recieved for branch id : " + branchId );
        Map<String, Set<Long>> hierarchyIdsMap = new HashMap<String, Set<Long>>();
        if ( !zillowConnectedIndividualIds.isEmpty() )
            hierarchyIdsMap.put( CommonConstants.PROFILE_TYPE_INDIVIDUAL, zillowConnectedIndividualIds );
        LOG.info( "Method fetch all ids under a branch connected to zillow, getIdsUnderBranchConnectedToZillow finished" );
        return hierarchyIdsMap;
    }


    /**
     * Method to get the total score and total review count of branch including individuals under a branch
     * @param branchId
     * @throws InvalidInputException
     * */
    @Override
    public Map<String, Long> getZillowReviewCountAndTotalScoreForAllUnderBranch( long branchId ) throws InvalidInputException
    {
        if ( branchId <= 0l ) {
            LOG.info( "Invalid branch id passed in getZillowAverageAndTotalScoreForAllUnderBranch" );
            throw new InvalidInputException( "Invalid branch id passed in getZillowAverageAndTotalScoreForAllUnderBranch" );
        }

        LOG.info( "Method to calculate zillow review count and total score for branch id : " + branchId
            + ", getZillowAverageAndTotalScoreForAllUnderBranch started" );
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery( fetchIdsUnderBranchQuery );
        query.setParameter( "isZillowConnected", CommonConstants.YES );
        query.setParameter( "agentProfileMasterId", CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID );
        query.setParameter( "branchId", branchId );
        query.setParameter( "status", CommonConstants.STATUS_INACTIVE );

        LOG.info( "Fetching ids under a branch conncected to zillow for branch id : " + branchId );
        List<Object[]> rows = (List<Object[]>) query.list();
        LOG.info( "Fetched ids under a branch conncected to zillow for branch id : " + branchId );

        if ( rows == null || rows.isEmpty() ) {
            LOG.error( "No records found for branch id : " + branchId );
            return null;
        }

        long totalReviewCount = 0;
        long totalScore = 0;

        LOG.info( "Parsing the information recieved for branch id : " + branchId );
        for ( Object[] row : rows ) {
            Long zillowConnectedBranchId = new Long( String.valueOf( row[0] ) );
            Long zillowConnectedIndividualId = new Long( String.valueOf( row[3] ) );
            if ( zillowConnectedIndividualId != -1 ) {
                long reviewCount = new Long( String.valueOf( row[4] ) );
                totalReviewCount += reviewCount;
                totalScore += new Double( String.valueOf( row[5] ) ) * reviewCount;
            } else if ( zillowConnectedBranchId != -1 ) {
                long reviewCount = new Long( String.valueOf( row[1] ) );
                totalReviewCount += reviewCount;
                totalScore += new Double( String.valueOf( row[2] ) ) * reviewCount;
            }
        }
        LOG.info( "Parsed the information recieved for branch id : " + branchId );
        Map<String, Long> zillowTotalScoreAndAverageMap = new HashMap<String, Long>();
        zillowTotalScoreAndAverageMap.put( CommonConstants.ZILLOW_REVIEW_COUNT_COLUMN, totalReviewCount );
        zillowTotalScoreAndAverageMap.put( CommonConstants.ZILLOW_TOTAL_SCORE, totalScore );
        LOG.info( "Method to calculate zillow review count and total score for branch id : " + branchId
            + ", getZillowAverageAndTotalScoreForAllUnderBranch finished" );
        return zillowTotalScoreAndAverageMap;
    }


    /**
     * Method to fetch all branches and individual ids under a region
     * @param regionId
     * @throws InvalidInputException
     * */
    @Override
    public Map<String, Set<Long>> getIdsUnderRegionConnectedToZillow( long regionId ) throws InvalidInputException
    {
        if ( regionId <= 0l ) {
            LOG.info( "Invalid region id passed in getIdsUnderRegionConnectedToZillow" );
            throw new InvalidInputException( "Invalid region id passed in getIdsUnderRegionConnectedToZillow" );
        }

        LOG.info( "Method fetch all ids under a region connected to zillow, getIdsUnderRegionConnectedToZillow started" );
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery( fetchIdsUnderRegionQuery );
        query.setParameter( "isZillowConnected", CommonConstants.YES );
        query.setParameter( "agentProfileMasterId", CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID );
        query.setParameter( "regionId", regionId );
        query.setParameter( "status", CommonConstants.STATUS_INACTIVE );

        LOG.info( "Fetching ids under a region conncected to zillow for region id : " + regionId );
        List<Object[]> rows = (List<Object[]>) query.list();
        LOG.info( "Fetched ids under a region conncected to zillow for region id : " + regionId );

        Set<Long> zillowConnectedIndividualIds = new HashSet<Long>();
        Set<Long> zillowConnectedBranchIds = new HashSet<Long>();

        if ( rows == null || rows.isEmpty() ) {
            LOG.error( "No records found for region id : " + regionId );
            return null;
        }

        LOG.info( "Parsing the information recieved for region id : " + regionId );
        for ( Object[] row : rows ) {
            // row 3 - agent id
            Long zillowConnectedIndividualId = new Long( String.valueOf( row[6] ) );
            Long zillowConnectedBranchId = new Long( String.valueOf( row[3] ) );
            if ( zillowConnectedIndividualId != -1 )
                zillowConnectedIndividualIds.add( zillowConnectedIndividualId );
            if ( zillowConnectedBranchId != -1 )
                zillowConnectedBranchIds.add( zillowConnectedBranchId );
        }
        LOG.info( "Parsed the information recieved for region id : " + regionId );
        Map<String, Set<Long>> hierarchyIdsMap = new HashMap<String, Set<Long>>();
        if ( !zillowConnectedIndividualIds.isEmpty() )
            hierarchyIdsMap.put( CommonConstants.PROFILE_TYPE_INDIVIDUAL, zillowConnectedIndividualIds );
        if ( !zillowConnectedBranchIds.isEmpty() )
            hierarchyIdsMap.put( CommonConstants.PROFILE_TYPE_BRANCH, zillowConnectedBranchIds );
        LOG.info( "Method fetch all ids under a region connected to zillow, getIdsUnderRegionConnectedToZillow finished" );
        return hierarchyIdsMap;
    }


    /**
     * Method to get the total score and total review count of region including branches and individuals under a region
     * @param regionId
     * @throws InvalidInputException
     * */
    @Override
    public Map<String, Long> getZillowReviewCountAndTotalScoreForAllUnderRegion( long regionId ) throws InvalidInputException
    {
        if ( regionId <= 0l ) {
            LOG.info( "Invalid region id passed in getZillowReviewCountAndTotalScoreForAllUnderRegion" );
            throw new InvalidInputException( "Invalid region id passed in getZillowReviewCountAndTotalScoreForAllUnderRegion" );
        }

        LOG.info( "Method to calculate zillow review count and total score for region id : " + regionId
            + ", getZillowReviewCountAndTotalScoreForAllUnderRegion started" );
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery( fetchIdsUnderRegionQuery );
        query.setParameter( "isZillowConnected", CommonConstants.YES );
        query.setParameter( "agentProfileMasterId", CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID );
        query.setParameter( "regionId", regionId );
        query.setParameter( "status", CommonConstants.STATUS_INACTIVE );

        LOG.info( "Fetching ids under a region conncected to zillow for region id : " + regionId );
        List<Object[]> rows = (List<Object[]>) query.list();
        LOG.info( "Fetched ids under a region conncected to zillow for region id : " + regionId );

        if ( rows == null || rows.isEmpty() ) {
            LOG.error( "No records found for region id : " + regionId );
            return null;
        }

        Set<Long> zillowConnectedIndividualIds = new HashSet<Long>();
        Set<Long> zillowConnectedBranchIds = new HashSet<Long>();
        Set<Long> zillowConnectedRegionIds = new HashSet<Long>();
        long totalReviewCount = 0;
        long totalScore = 0;

        LOG.info( "Parsing the information recieved for region id : " + regionId );
        for ( Object[] row : rows ) {
            Long zillowConnectedRegionId = new Long( String.valueOf( row[0] ) );
            Long zillowConnectedBranchId = new Long( String.valueOf( row[3] ) );
            Long zillowConnectedIndividualId = new Long( String.valueOf( row[6] ) );
            if ( zillowConnectedIndividualId != -1 ) {
                if ( !zillowConnectedIndividualIds.contains( zillowConnectedIndividualId ) ) {
                    long reviewCount = new Long( String.valueOf( row[7] ) );
                    totalReviewCount += reviewCount;
                    totalScore += new Double( String.valueOf( row[8] ) ) * reviewCount;
                    zillowConnectedIndividualIds.add( zillowConnectedIndividualId );
                }
            } else if ( zillowConnectedBranchId != -1 ) {
                if ( !zillowConnectedBranchIds.contains( zillowConnectedBranchId ) ) {
                    long reviewCount = new Long( String.valueOf( row[4] ) );
                    totalReviewCount += reviewCount;
                    totalScore += new Double( String.valueOf( row[5] ) ) * reviewCount;
                    zillowConnectedBranchIds.add( zillowConnectedBranchId );
                }
            } else if ( zillowConnectedRegionId != -1 ) {
                if ( !zillowConnectedRegionIds.contains( zillowConnectedRegionId ) ) {
                    long reviewCount = new Long( String.valueOf( row[1] ) );
                    totalReviewCount += reviewCount;
                    totalScore += new Double( String.valueOf( row[2] ) ) * reviewCount;
                    zillowConnectedRegionIds.add( zillowConnectedRegionId );
                }
            }
        }
        LOG.info( "Parsed the information recieved for region id : " + regionId );
        Map<String, Long> zillowTotalScoreAndAverageMap = new HashMap<String, Long>();
        zillowTotalScoreAndAverageMap.put( CommonConstants.ZILLOW_REVIEW_COUNT_COLUMN, totalReviewCount );
        zillowTotalScoreAndAverageMap.put( CommonConstants.ZILLOW_TOTAL_SCORE, totalScore );
        LOG.info( "Method to calculate zillow review count and total score for region id : " + regionId
            + ", getZillowReviewCountAndTotalScoreForAllUnderRegion finished" );
        return zillowTotalScoreAndAverageMap;
    }


    /**
     * Method to fetch all regions, branches and individual ids under a company
     * @param companyId
     * @throws InvalidInputException
     * */
    @Override
    public Map<String, Set<Long>> getIdsUnderCompanyConnectedToZillow( long companyId ) throws InvalidInputException
    {
        if ( companyId <= 0l ) {
            LOG.info( "Invalid company id passed in getIdsUnderCompanyConnectedToZillow" );
            throw new InvalidInputException( "Invalid company id passed in getIdsUnderCompanyConnectedToZillow" );
        }

        LOG.info( "Method fetch all ids under a company connected to zillow, getIdsUnderCompanyConnectedToZillow started" );
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery( fetchIdsUnderCompanyQuery );
        query.setParameter( "isZillowConnected", CommonConstants.YES );
        query.setParameter( "agentProfileMasterId", CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID );
        query.setParameter( "companyId", companyId );
        query.setParameter( "status", CommonConstants.STATUS_INACTIVE );

        LOG.info( "Fetching ids under a company conncected to zillow for company id : " + companyId );
        List<Object[]> rows = (List<Object[]>) query.list();
        LOG.info( "Fetched ids under a company conncected to zillow for company id : " + companyId );

        Set<Long> zillowConnectedIndividualIds = new HashSet<Long>();
        Set<Long> zillowConnectedBranchIds = new HashSet<Long>();
        Set<Long> zillowConnectedRegionIds = new HashSet<Long>();

        if ( rows == null || rows.isEmpty() ) {
            LOG.error( "No records found for company id : " + companyId );
            return null;
        }

        LOG.info( "Parsing the information recieved for company id : " + companyId );
        for ( Object[] row : rows ) {
            // row 3 - agent id
            Long zillowConnectedIndividualId = new Long( String.valueOf( row[9] ) );
            Long zillowConnectedBranchId = new Long( String.valueOf( row[6] ) );
            Long zillowConnectedRegionId = new Long( String.valueOf( row[3] ) );
            if ( zillowConnectedIndividualId != -1 )
                zillowConnectedIndividualIds.add( zillowConnectedIndividualId );
            if ( zillowConnectedBranchId != -1 )
                zillowConnectedBranchIds.add( zillowConnectedBranchId );
            if ( zillowConnectedRegionId != -1 )
                zillowConnectedRegionIds.add( zillowConnectedRegionId );
        }
        LOG.info( "Parsed the information recieved for company id : " + companyId );
        Map<String, Set<Long>> hierarchyIdsMap = new HashMap<String, Set<Long>>();
        if ( !zillowConnectedIndividualIds.isEmpty() )
            hierarchyIdsMap.put( CommonConstants.PROFILE_TYPE_INDIVIDUAL, zillowConnectedIndividualIds );
        if ( !zillowConnectedBranchIds.isEmpty() )
            hierarchyIdsMap.put( CommonConstants.PROFILE_TYPE_BRANCH, zillowConnectedBranchIds );
        if ( !zillowConnectedRegionIds.isEmpty() )
            hierarchyIdsMap.put( CommonConstants.PROFILE_TYPE_REGION, zillowConnectedRegionIds );
        LOG.info( "Method fetch all ids under a company connected to zillow, getIdsUnderCompanyConnectedToZillow started" );
        return hierarchyIdsMap;
    }


    /**
     * Method to get the total score and total review count of company including region, branches and individuals under a company
     * @param regionId
     * @throws InvalidInputException
     * */
    @Override
    public Map<String, Long> getZillowReviewCountAndTotalScoreForAllUnderCompany( long companyId ) throws InvalidInputException
    {
        if ( companyId <= 0l ) {
            LOG.info( "Invalid company id passed in getZillowReviewCountAndTotalScoreForAllUnderCompany" );
            throw new InvalidInputException( "Invalid company id passed in getZillowReviewCountAndTotalScoreForAllUnderCompany" );
        }

        LOG.info( "Method to calculate zillow review count and total score for company id : " + companyId
            + ", getZillowReviewCountAndTotalScoreForAllUnderCompany started" );
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery( fetchIdsUnderCompanyQuery );
        query.setParameter( "isZillowConnected", CommonConstants.YES );
        query.setParameter( "agentProfileMasterId", CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID );
        query.setParameter( "companyId", companyId );
        query.setParameter( "status", CommonConstants.STATUS_INACTIVE );

        LOG.info( "Fetching ids under a company conncected to zillow for company id : " + companyId );
        List<Object[]> rows = (List<Object[]>) query.list();
        LOG.info( "Fetched ids under a company conncected to zillow for company id : " + companyId );

        if ( rows == null || rows.isEmpty() ) {
            LOG.error( "No records found for company id : " + companyId );
            return null;
        }

        Set<Long> zillowConnectedIndividualIds = new HashSet<Long>();
        Set<Long> zillowConnectedBranchIds = new HashSet<Long>();
        Set<Long> zillowConnectedRegionIds = new HashSet<Long>();
        Set<Long> zillowConnectedCompanyIds = new HashSet<Long>();
        long totalReviewCount = 0;
        long totalScore = 0;

        LOG.info( "Parsing the information recieved for company id : " + companyId );
        for ( Object[] row : rows ) {
            Long zillowConnectedCompanyId = new Long( String.valueOf( row[0] ) );
            Long zillowConnectedRegionId = new Long( String.valueOf( row[3] ) );
            Long zillowConnectedBranchId = new Long( String.valueOf( row[6] ) );
            Long zillowConnectedIndividualId = new Long( String.valueOf( row[9] ) );
            if ( zillowConnectedIndividualId != -1 ) {
                if ( !zillowConnectedIndividualIds.contains( zillowConnectedIndividualId ) ) {
                    if(row[10] != null && row[11] != null) {
                    long reviewCount = new Long( String.valueOf( row[10] ) );
                    totalReviewCount += reviewCount;
                    totalScore += new Double( String.valueOf( row[11] ) ) * reviewCount;
                    zillowConnectedIndividualIds.add( zillowConnectedIndividualId );
                    }
                }
            } else if ( zillowConnectedBranchId != -1 ) {
                if ( !zillowConnectedBranchIds.contains( zillowConnectedBranchId ) ) {
                    long reviewCount = new Long( String.valueOf( row[7] ) );
                    totalReviewCount += reviewCount;
                    totalScore += new Double( String.valueOf( row[8] ) ) * reviewCount;
                    zillowConnectedBranchIds.add( zillowConnectedBranchId );
                }
            } else if ( zillowConnectedRegionId != -1 ) {
                if ( !zillowConnectedRegionIds.contains( zillowConnectedRegionId ) ) {
                    long reviewCount = new Long( String.valueOf( row[4] ) );
                    totalReviewCount += reviewCount;
                    totalScore += new Double( String.valueOf( row[5] ) ) * reviewCount;
                    zillowConnectedRegionIds.add( zillowConnectedRegionId );
                }
            } else if ( zillowConnectedCompanyId != -1 ) {
                if ( !zillowConnectedCompanyIds.contains( zillowConnectedCompanyId ) ) {
                    long reviewCount = new Long( String.valueOf( row[1] ) );
                    totalReviewCount += reviewCount;
                    totalScore += new Double( String.valueOf( row[2] ) ) * reviewCount;
                    zillowConnectedCompanyIds.add( zillowConnectedCompanyId );
                }
            }
        }
        LOG.info( "Parsed the information recieved for company id : " + companyId );
        Map<String, Long> zillowTotalScoreAndAverageMap = new HashMap<String, Long>();
        zillowTotalScoreAndAverageMap.put( CommonConstants.ZILLOW_REVIEW_COUNT_COLUMN, totalReviewCount );
        zillowTotalScoreAndAverageMap.put( CommonConstants.ZILLOW_TOTAL_SCORE, totalScore );
        LOG.info( "Method to calculate zillow review count and total score for company id : " + companyId
            + ", getZillowReviewCountAndTotalScoreForAllUnderCompany finished" );
        return zillowTotalScoreAndAverageMap;
    }
}
