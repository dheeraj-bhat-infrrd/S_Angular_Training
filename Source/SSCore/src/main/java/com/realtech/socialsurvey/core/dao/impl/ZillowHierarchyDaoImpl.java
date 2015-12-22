package com.realtech.socialsurvey.core.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
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
    private static final String fetchIdsUnderBranchQuery = "select distinct CASE WHEN br.IS_ZILLOW_CONNECTED = :isZillowConnected THEN br.BRANCH_ID ELSE -1 END AS BRANCH_ID, br.ZILLOW_REVIEW_COUNT as BRANCH_ZILLOW_COUNT, br.ZILLOW_AVERAGE_SCORE as BRANCH_ZILLOW_AVERAGE, us.USER_ID AS USER_IDS, us.ZILLOW_REVIEW_COUNT AS INDIVIDUALS_REVIEW_COUNT, us.ZILLOW_AVERAGE_SCORE AS INDIVIDUALS_REVIEW_AVERAGE from BRANCH br LEFT JOIN (select us_pro.BRANCH_ID, group_concat(usInner.USER_ID) as USER_ID, group_concat(usInner.ZILLOW_REVIEW_COUNT) as ZILLOW_REVIEW_COUNT, group_concat(usInner.ZILLOW_AVERAGE_SCORE) as ZILLOW_AVERAGE_SCORE FROM USERS as usInner JOIN USER_PROFILE as us_pro ON us_pro.USER_ID = usInner.USER_ID and us_pro.PROFILES_MASTER_ID = :agentProfileMasterId and us_pro.STATUS != :status where (case when usInner.IS_ZILLOW_CONNECTED = :isZillowConnected THEN 1 = 1 ELSE 1 = 0 END) and us_pro.BRANCH_ID  = :branchId group by us_pro.BRANCH_ID) as us on br.BRANCH_ID = us.BRANCH_ID where br.BRANCH_ID = :branchId";
    private static final String fetchIdsUnderRegionQuery = "select distinct CASE WHEN reg.IS_ZILLOW_CONNECTED = :isZillowConnected THEN reg.REGION_ID ELSE -1 END AS REGION_ID , reg.ZILLOW_REVIEW_COUNT as REGION_ZILLOW_COUNT, reg.ZILLOW_AVERAGE_SCORE as REGION_ZILLOW_AVERAGE, br.BRANCH_ID AS BRANCH_IDS, br.ZILLOW_REVIEW_COUNT AS BRANCHES_REVIEW_COUNT, br.ZILLOW_AVERAGE_SCORE AS BRANCHES_REVIEW_AVERAGE, us.USER_ID AS USER_IDS, us.ZILLOW_REVIEW_COUNT AS INDIVIDUALS_REVIEW_COUNT, us.ZILLOW_AVERAGE_SCORE AS INDIVIDUALS_REVIEW_AVERAGE from REGION reg left JOIN (select brInner.REGION_ID, group_concat(brInner.BRANCH_ID) as BRANCH_ID, group_concat(brInner.ZILLOW_REVIEW_COUNT) as ZILLOW_REVIEW_COUNT, group_concat(brInner.ZILLOW_AVERAGE_SCORE) as ZILLOW_AVERAGE_SCORE FROM BRANCH as brInner where brInner.STATUS != :status and (case when brInner.IS_ZILLOW_CONNECTED = :isZillowConnected THEN 1 = 1 ELSE 1 = 0 END) and brInner.REGION_ID  = :regionId group by brInner.REGION_ID) as br on br.REGION_ID = reg.REGION_ID left JOIN (select us_pro.REGION_ID, Group_concat(usInner.USER_ID) as USER_ID, group_concat(usInner.ZILLOW_REVIEW_COUNT) as ZILLOW_REVIEW_COUNT, group_concat(usInner.ZILLOW_AVERAGE_SCORE) as ZILLOW_AVERAGE_SCORE FROM USERS as usInner JOIN USER_PROFILE as us_pro ON us_pro.USER_ID = usInner.USER_ID and us_pro.PROFILES_MASTER_ID = :agentProfileMasterId and us_pro.STATUS != :status where (case when usInner.IS_ZILLOW_CONNECTED = :isZillowConnected THEN 1 = 1 ELSE 1 = 0 END) and us_pro.REGION_ID  = :regionId group by us_pro.REGION_ID) as us on us.REGION_ID = reg.REGION_ID where reg.REGION_ID = :regionId";
    private static final String fetchIdsUnderCompanyQuery = "select distinct CASE WHEN co.IS_ZILLOW_CONNECTED = :isZillowConnected THEN co.COMPANY_ID ELSE -1 END AS COMPANY_ID,co.ZILLOW_REVIEW_COUNT as COMPANY_ZILLOW_COUNT, co.ZILLOW_AVERAGE_SCORE as COMPANY_ZILLOW_AVERAGE , reg.REGION_ID AS REGION_IDS , reg.ZILLOW_REVIEW_COUNT as REGIONS_ZILLOW_COUNT, reg.ZILLOW_AVERAGE_SCORE as REGIONS_ZILLOW_AVERAGE, br.BRANCH_ID AS BRANCH_IDS, br.ZILLOW_REVIEW_COUNT AS BRANCHES_REVIEW_COUNT, br.ZILLOW_AVERAGE_SCORE AS BRANCHES_REVIEW_AVERAGE, us.USER_ID AS USER_IDS, us.ZILLOW_REVIEW_COUNT AS INDIVIDUALS_REVIEW_COUNT, us.ZILLOW_AVERAGE_SCORE AS INDIVIDUALS_REVIEW_AVERAGE from COMPANY co left JOIN (select regInner.COMPANY_ID, group_concat(regInner.REGION_ID) as REGION_ID , group_concat(regInner.ZILLOW_REVIEW_COUNT) as ZILLOW_REVIEW_COUNT, group_concat(regInner.ZILLOW_AVERAGE_SCORE) as ZILLOW_AVERAGE_SCORE FROM REGION as regInner where regInner.STATUS != :status and (case when regInner.IS_ZILLOW_CONNECTED = :isZillowConnected THEN 1 = 1 ELSE 1 = 0 END) and regInner.COMPANY_ID  = :companyId group by regInner.COMPANY_ID)  as reg on reg.COMPANY_ID = co.COMPANY_ID left JOIN (select brInner.COMPANY_ID, group_concat(brInner.BRANCH_ID) as BRANCH_ID, group_concat(brInner.ZILLOW_REVIEW_COUNT) as ZILLOW_REVIEW_COUNT, group_concat(brInner.ZILLOW_AVERAGE_SCORE) as ZILLOW_AVERAGE_SCORE FROM BRANCH as brInner where brInner.STATUS != :status and (case when brInner.IS_ZILLOW_CONNECTED = :isZillowConnected THEN 1 = 1 ELSE 1 = 0 END) and brInner.COMPANY_ID  = :companyId group by brInner.COMPANY_ID)  as br on br.COMPANY_ID = co.COMPANY_ID left JOIN (select usInner.COMPANY_ID, group_concat(usInner.USER_ID) as USER_ID, group_concat(usInner.ZILLOW_REVIEW_COUNT) as ZILLOW_REVIEW_COUNT, group_concat(usInner.ZILLOW_AVERAGE_SCORE) as ZILLOW_AVERAGE_SCORE FROM USERS as usInner JOIN USER_PROFILE as us_pro ON us_pro.USER_ID = usInner.USER_ID and us_pro.PROFILES_MASTER_ID = :agentProfileMasterId and us_pro.STATUS != :status where (case when usInner.IS_ZILLOW_CONNECTED = :isZillowConnected THEN 1 = 1 ELSE 1 = 0 END) and us_pro.COMPANY_ID  = :companyId group by us_pro.COMPANY_ID) as us on us.COMPANY_ID = co.COMPANY_ID where co.COMPANY_ID = :companyId";
    private static final String fetchUserIdsUnderCompanyQuery = "SELECT DISTINCT(U.USER_ID) AS USER_ID FROM USERS U, USER_PROFILE UP  WHERE U.STATUS IN ( :statuses ) AND U.IS_ZILLOW_CONNECTED = :isZillowConnected AND U.COMPANY_ID = :companyId AND U.COMPANY_ID = UP.COMPANY_ID AND UP.PROFILES_MASTER_ID = :profilesMasterId AND UP.STATUS = :status ORDER BY U.USER_ID ASC";
    private static final String fetchUserIdsUnderRegionQuery = "SELECT DISTINCT(U.USER_ID) AS USER_ID FROM USERS U, USER_PROFILE UP WHERE U.STATUS IN (:statuses) AND U.IS_ZILLOW_CONNECTED=:isZillowConnected AND UP.STATUS = :status AND U.USER_ID = UP.USER_ID AND UP.REGION_ID=:regionId AND UP.PROFILES_MASTER_ID = :profileMasterId ORDER BY U.USER_ID ASC";
    private static final String fetchUserIdsUnderBranchQuery = "SELECT DISTINCT(U.USER_ID) AS USER_ID FROM USERS U, USER_PROFILE UP WHERE  U.STATUS IN ( :statuses ) AND  U.IS_ZILLOW_CONNECTED = :isZillowConnected AND UP.STATUS = :status AND U.USER_ID = UP.USER_ID AND UP.BRANCH_ID = :branchId AND UP.PROFILES_MASTER_ID = :profileMasterId ORDER BY U.USER_ID ASC";
    private static final String fetchRegionIdsUnderCompanyQuery = "SELECT REGION_ID FROM REGION WHERE COMPANY_ID = :companyId AND IS_ZILLOW_CONNECTED = :isZillowConnected AND STATUS != :status  ORDER BY REGION_ID ASC";
    private static final String fetchBranchIdsUnderCompanyQuery = "SELECT BRANCH_ID FROM BRANCH WHERE COMPANY_ID = :companyId AND IS_ZILLOW_CONNECTED = :isZillowConnected AND STATUS != :status ORDER BY BRANCH_ID ASC";
    private static final String fetchBranchIdsUnderRegionQuery = "SELECT BRANCH_ID FROM BRANCH WHERE REGION_ID = :regionId AND IS_ZILLOW_CONNECTED = :isZillowConnected AND STATUS != :status ORDER BY BRANCH_ID ASC";

    @Autowired
    SessionFactory sessionFactory;


    /**
     * Method to get the total score and total review count of branch including individuals under a branch
     * @param branchId
     * @throws InvalidInputException
     * */
    @SuppressWarnings ( "unchecked")
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
        List<Long> zillowConnectedIndividualIds = new ArrayList<Long>();

        LOG.info( "Parsing the information recieved for branch id : " + branchId );
        for ( Object[] row : rows ) {
            //  row [0] - branch_id
            //  row [1] - branch zillow count
            //  row [2] - branch zillow average
            //  row [3] - user_id
            //  row [4] - individuals review count
            //  row [5] - individuals review average
            Long zillowConnectedBranchId = new Long( String.valueOf( row[0] ) );
            if ( zillowConnectedBranchId != -1 ) {
                long reviewCount = new Long( String.valueOf( row[1] ) );
                totalReviewCount += reviewCount;
                totalScore += new Double( String.valueOf( row[2] ) ) * reviewCount;
            }
            String reviewCounts = String.valueOf( row[4] );
            String reviewAverages = String.valueOf( row[5] );
            String userIds = String.valueOf( row[3] );
            if ( reviewCounts.contains( "," ) && reviewAverages.contains( "," ) ) {
                String[] reviewCountArr = reviewCounts.split( "," );
                String[] reviewAveragesArr = reviewAverages.split( "," );
                String[] userIdsArr = userIds.split( "," );
                if ( reviewCountArr.length == reviewAveragesArr.length ) {
                    for ( int i = 0; i < reviewCountArr.length; i++ ) {
                        Long userId = Long.parseLong( userIdsArr[i] );
                        if ( !zillowConnectedIndividualIds.contains( userId ) ) {
                            long reviewCount = new Long( String.valueOf( reviewCountArr[i] ) );
                            totalReviewCount += reviewCount;
                            totalScore += new Double( String.valueOf( reviewAveragesArr[i] ) ) * reviewCount;
                            zillowConnectedIndividualIds.add( userId );
                        }
                    }
                }
            } else {
                if ( reviewCounts != null && !reviewCounts.equalsIgnoreCase( "null" ) ) {
                    long reviewCount = new Long( reviewCounts );
                    totalReviewCount += reviewCount;
                    totalScore += new Double( reviewAverages ) * reviewCount;
                }
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
     * Method to get the total score and total review count of region including branches and individuals under a region
     * @param regionId
     * @throws InvalidInputException
     * */
    @SuppressWarnings ( "unchecked")
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
        long totalReviewCount = 0;
        long totalScore = 0;

        LOG.info( "Parsing the information recieved for region id : " + regionId );
        for ( Object[] row : rows ) {
            //  row [0] - region_id
            //  row [1] - region zillow count
            //  row [2] - region zillow average
            //  row [3] - branch_id
            //  row [4] - branch zillow count
            //  row [5] - branch zillow average
            //  row [6] - user_id
            //  row [7] - individuals review count
            //  row [8] - individuals review average
            Long zillowConnectedRegionId = new Long( String.valueOf( row[0] ) );
            if ( zillowConnectedRegionId != -1 ) {
                long reviewCount = new Long( String.valueOf( row[1] ) );
                totalReviewCount += reviewCount;
                totalScore += new Double( String.valueOf( row[2] ) ) * reviewCount;
            }
            String branchReviewCounts = String.valueOf( row[4] );
            String branchReviewAverages = String.valueOf( row[5] );
            String branchIds = String.valueOf( row[3] );
            if ( branchReviewCounts.contains( "," ) && branchReviewAverages.contains( "," ) ) {
                String[] reviewCountArr = branchReviewCounts.split( "," );
                String[] reviewAveragesArr = branchReviewAverages.split( "," );
                String[] branchIdsArr = branchIds.split( "," );
                if ( reviewCountArr.length == reviewAveragesArr.length ) {
                    for ( int i = 0; i < reviewCountArr.length; i++ ) {
                        Long branchId = Long.parseLong( branchIdsArr[i] );
                        if ( !zillowConnectedBranchIds.contains( branchId ) ) {
                            long reviewCount = new Long( String.valueOf( reviewCountArr[i] ) );
                            totalReviewCount += reviewCount;
                            totalScore += new Double( String.valueOf( reviewAveragesArr[i] ) ) * reviewCount;
                            zillowConnectedBranchIds.add( branchId );
                        }
                    }
                }
            } else {
                if ( branchReviewCounts != null && !branchReviewCounts.equals( "null" ) ) {
                    long reviewCount = new Long( branchReviewCounts );
                    totalReviewCount += reviewCount;
                    totalScore += new Double( branchReviewAverages ) * reviewCount;
                }
            }
            String individualReviewCounts = String.valueOf( row[7] );
            String individualReviewAverages = String.valueOf( row[8] );
            String userIds = String.valueOf( row[6] );
            if ( individualReviewCounts.contains( "," ) && individualReviewAverages.contains( "," ) ) {
                String[] reviewCountArr = individualReviewCounts.split( "," );
                String[] reviewAveragesArr = individualReviewAverages.split( "," );
                String[] userIdsArr = userIds.split( "," );
                if ( reviewCountArr.length == reviewAveragesArr.length ) {
                    for ( int i = 0; i < reviewCountArr.length; i++ ) {
                        Long userId = Long.parseLong( userIdsArr[i] );
                        if ( !zillowConnectedIndividualIds.contains( userId ) ) {
                            long reviewCount = new Long( String.valueOf( reviewCountArr[i] ) );
                            totalReviewCount += reviewCount;
                            totalScore += new Double( String.valueOf( reviewAveragesArr[i] ) ) * reviewCount;
                            zillowConnectedIndividualIds.add( userId );
                        }
                    }
                }
            } else {
                if ( individualReviewCounts != null && !individualReviewCounts.equals( "null" ) ) {
                    long reviewCount = new Long( individualReviewCounts );
                    totalReviewCount += reviewCount;
                    totalScore += new Double( individualReviewAverages ) * reviewCount;
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
     * Method to get the total score and total review count of company including region, branches and individuals under a company
     * @param regionId
     * @throws InvalidInputException
     * */
    @SuppressWarnings ( "unchecked")
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
        long totalReviewCount = 0;
        long totalScore = 0;

        LOG.info( "Parsing the information recieved for company id : " + companyId );
        for ( Object[] row : rows ) {
            //  row [0] - region_id
            //  row [1] - region zillow count
            //  row [2] - region zillow average
            //  row [3] - region_id
            //  row [4] - region zillow count
            //  row [5] - region zillow average
            //  row [6] - branch_id
            //  row [7] - branch zillow count
            //  row [8] - branch zillow average
            //  row [9] - user_id
            //  row [10] - individuals review count
            //  row [11] - individuals review average
            Long zillowConnectedCompanyId = new Long( String.valueOf( row[0] ) );
            if ( zillowConnectedCompanyId != -1 ) {
                long reviewCount = new Long( String.valueOf( row[1] ) );
                totalReviewCount += reviewCount;
                totalScore += new Double( String.valueOf( row[2] ) ) * reviewCount;
            }

            String regionReviewCounts = String.valueOf( row[4] );
            String regionReviewAverages = String.valueOf( row[5] );
            String regionIds = String.valueOf( row[3] );
            if ( regionReviewCounts.contains( "," ) && regionReviewAverages.contains( "," ) ) {
                String[] reviewCountArr = regionReviewCounts.split( "," );
                String[] reviewAveragesArr = regionReviewAverages.split( "," );
                String[] regionIdsArr = regionIds.split( "," );
                if ( reviewCountArr.length == reviewAveragesArr.length ) {
                    for ( int i = 0; i < reviewCountArr.length; i++ ) {
                        Long regionId = Long.parseLong( regionIdsArr[i] );
                        if ( !zillowConnectedRegionIds.contains( regionId ) ) {
                            long reviewCount = new Long( String.valueOf( reviewCountArr[i] ) );
                            totalReviewCount += reviewCount;
                            totalScore += new Double( String.valueOf( reviewAveragesArr[i] ) ) * reviewCount;
                            zillowConnectedRegionIds.add( regionId );
                        }
                    }
                }
            } else {
                if ( regionReviewCounts != null && !regionReviewCounts.equalsIgnoreCase( "null" ) ) {
                    long reviewCount = new Long( regionReviewCounts );
                    totalReviewCount += reviewCount;
                    totalScore += new Double( regionReviewAverages ) * reviewCount;
                }
            }
            String branchReviewCounts = String.valueOf( row[7] );
            String branchReviewAverages = String.valueOf( row[8] );
            String branchIds = String.valueOf( row[6] );
            if ( branchReviewCounts.contains( "," ) && branchReviewAverages.contains( "," ) ) {
                String[] reviewCountArr = branchReviewCounts.split( "," );
                String[] reviewAveragesArr = branchReviewAverages.split( "," );
                String[] branchIdsArr = branchIds.split( "," );
                if ( reviewCountArr.length == reviewAveragesArr.length ) {
                    for ( int i = 0; i < reviewCountArr.length; i++ ) {
                        Long branchId = Long.parseLong( branchIdsArr[i] );
                        if ( !zillowConnectedBranchIds.contains( branchId ) ) {
                            long reviewCount = new Long( String.valueOf( reviewCountArr[i] ) );
                            totalReviewCount += reviewCount;
                            totalScore += new Double( String.valueOf( reviewAveragesArr[i] ) ) * reviewCount;
                            zillowConnectedBranchIds.add( branchId );
                        }
                    }
                }
            } else {
                if ( branchReviewCounts != null && !branchReviewCounts.equals( "null" ) ) {
                    long reviewCount = new Long( branchReviewCounts );
                    totalReviewCount += reviewCount;
                    totalScore += new Double( branchReviewAverages ) * reviewCount;
                }
            }
            String individualReviewCounts = String.valueOf( row[10] );
            String individualReviewAverages = String.valueOf( row[11] );
            String userIds = String.valueOf( row[9] );
            if ( individualReviewCounts.contains( "," ) && individualReviewAverages.contains( "," ) ) {
                String[] reviewCountArr = individualReviewCounts.split( "," );
                String[] reviewAveragesArr = individualReviewAverages.split( "," );
                String[] userIdsArr = userIds.split( "," );
                if ( reviewCountArr.length == reviewAveragesArr.length ) {
                    for ( int i = 0; i < reviewCountArr.length; i++ ) {
                        Long userId = Long.parseLong( userIdsArr[i] );
                        if ( !zillowConnectedIndividualIds.contains( userId ) ) {
                            long reviewCount = new Long( String.valueOf( reviewCountArr[i] ) );
                            totalReviewCount += reviewCount;
                            totalScore += new Double( String.valueOf( reviewAveragesArr[i] ) ) * reviewCount;
                            zillowConnectedIndividualIds.add( userId );
                        }
                    }
                }
            } else {
                if ( individualReviewCounts != null && !individualReviewCounts.equals( "null" )  ) {
                    long reviewCount = new Long( individualReviewCounts );
                    totalReviewCount += reviewCount;
                    totalScore += new Double( individualReviewAverages ) * reviewCount;
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


    /**
     * Method to get regions under company connected to zillow
     * @param companyId
     * @param startIndex
     * @param batchSize
     * @throws InvalidInputException
     * */
    @SuppressWarnings ( "unchecked")
    @Override
    public Set<Long> getRegionIdsUnderCompanyConnectedToZillow( long companyId, int startIndex, int batchSize )
        throws InvalidInputException
    {

        if ( companyId <= 0l ) {
            LOG.error( "Invalid companyId passed in getRegionIdsUnderCompanyConnectedToZillow" );
            throw new InvalidInputException( "Invalid companyId passed in getRegionIdsUnderCompanyConnectedToZillow" );
        }

        LOG.info( "Method called to fetch regions under company for company id : " + companyId + " in batch started" );
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery( fetchRegionIdsUnderCompanyQuery )
            .addScalar( "REGION_ID", StandardBasicTypes.LONG );

        query.setParameter( "isZillowConnected", CommonConstants.YES );
        query.setParameter( "companyId", companyId );
        query.setParameter( "status", CommonConstants.STATUS_INACTIVE );

        if ( startIndex > -1 )
            query.setFirstResult( startIndex );
        if ( batchSize > -1 )
            query.setMaxResults( batchSize );

        List<Long> ids = (List<Long>) query.list();
        Set<Long> regionIds = new HashSet<Long>();

        if ( ids != null && !ids.isEmpty() ) {
            for ( Long id : ids ) {
                if ( id > 0 ) {
                    regionIds.add( id );
                }
            }
        }

        LOG.info( "Method called to fetch regions under company for company id : " + companyId + " in batch ended" );
        return regionIds;
    }


    /**
     * Method to get branches under company connected to zillow
     * @param companyId
     * @param startIndex
     * @param batchSize
     * @throws InvalidInputException
     * */
    @SuppressWarnings ( "unchecked")
    @Override
    public Set<Long> getBranchIdsUnderCompanyConnectedToZillow( long companyId, int startIndex, int batchSize )
        throws InvalidInputException
    {

        if ( companyId <= 0l ) {
            LOG.error( "Invalid companyId passed in getBranchIdsUnderCompanyConnectedToZillow" );
            throw new InvalidInputException( "Invalid companyId passed in getBranchIdsUnderCompanyConnectedToZillow" );
        }

        LOG.info( "Method called to fetch branches under company for company id : " + companyId + " in batch started" );
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery( fetchBranchIdsUnderCompanyQuery )
            .addScalar( "BRANCH_ID", StandardBasicTypes.LONG );

        query.setParameter( "isZillowConnected", CommonConstants.YES );
        query.setParameter( "companyId", companyId );
        query.setParameter( "status", CommonConstants.STATUS_INACTIVE );

        if ( startIndex > -1 )
            query.setFirstResult( startIndex );
        if ( batchSize > -1 )
            query.setMaxResults( batchSize );

        List<Long> ids = (List<Long>) query.list();
        Set<Long> branchIds = new HashSet<Long>();

        if ( ids != null && !ids.isEmpty() ) {
            for ( Long id : ids ) {
                if ( id > 0 ) {
                    branchIds.add( id );
                }
            }
        }
        LOG.info( "Method called to fetch branches under company for company id : " + companyId + " in batch ended" );
        return branchIds;
    }


    /**
     * Method to branches under region connected to zillow
     * @param regionId
     * @param startIndex
     * @param batchSize
     * @throws InvalidInputException
     * */
    @SuppressWarnings ( "unchecked")
    @Override
    public Set<Long> getBranchIdsUnderRegionConnectedToZillow( long regionId, int startIndex, int batchSize )
        throws InvalidInputException
    {

        if ( regionId <= 0l ) {
            LOG.error( "Invalid regionId passed in getBranchIdsUnderRegionConnectedToZillow" );
            throw new InvalidInputException( "Invalid regionId passed in getBranchIdsUnderRegionConnectedToZillow" );
        }

        LOG.info( "Method called to fetch branches under region for region id : " + regionId + " in batch started" );
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery( fetchBranchIdsUnderRegionQuery ).addScalar( "BRANCH_ID", StandardBasicTypes.LONG );

        query.setParameter( "isZillowConnected", CommonConstants.YES );
        query.setParameter( "regionId", regionId );
        query.setParameter( "status", CommonConstants.STATUS_INACTIVE );

        if ( startIndex > -1 )
            query.setFirstResult( startIndex );
        if ( batchSize > -1 )
            query.setMaxResults( batchSize );

        List<Long> ids = (List<Long>) query.list();
        Set<Long> branchIds = new HashSet<Long>();

        if ( ids != null && !ids.isEmpty() ) {
            for ( Long id : ids ) {
                if ( id > 0 ) {
                    branchIds.add( id );
                }
            }
        }
        LOG.info( "Method called to fetch branches under region for region id : " + regionId + " in batch ended" );
        return branchIds;
    }


    /**
     * Method to get users under company connected to zillow
     * @param regionId
     * @param startIndex
     * @param batchSize
     * @throws InvalidInputException
     * */
    @SuppressWarnings ( "unchecked")
    @Override
    public Set<Long> getUserIdsUnderCompanyConnectedToZillow( long companyId, int startIndex, int batchSize )
        throws InvalidInputException
    {

        if ( companyId <= 0l ) {
            LOG.error( "Invalid companyId passed in getUserIdsUnderCompanyConnectedToZillow" );
            throw new InvalidInputException( "Invalid companyId passed in getUserIdsUnderCompanyConnectedToZillow" );
        }

        LOG.info( "Method called to fetch users under company for company id : " + companyId + " in batch started" );
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery( fetchUserIdsUnderCompanyQuery ).addScalar( "USER_ID", StandardBasicTypes.LONG );
        List<Integer> statusesList = new ArrayList<Integer>();
        statusesList.add( CommonConstants.STATUS_ACTIVE );
        statusesList.add( CommonConstants.STATUS_NOT_VERIFIED );

        query.setParameterList( "statuses", statusesList );
        query.setParameter( "isZillowConnected", CommonConstants.YES );
        query.setParameter( "companyId", companyId );
        query.setParameter( "status", CommonConstants.STATUS_ACTIVE );
        query.setParameter( "profilesMasterId", CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID );

        if ( startIndex > -1 )
            query.setFirstResult( startIndex );
        if ( batchSize > -1 )
            query.setMaxResults( batchSize );

        List<Long> ids = (List<Long>) query.list();
        Set<Long> userIds = new HashSet<Long>();

        if ( ids != null && !ids.isEmpty() ) {
            for ( Long id : ids ) {
                if ( id > 0 ) {
                    userIds.add( id );
                }
            }
        }
        LOG.info( "Method called to fetch users under company for company id : " + companyId + " in batch ended" );
        return userIds;
    }


    /**
     * Method to get users under region connected to zillow
     * @param regionId
     * @param startIndex
     * @param batchSize
     * @throws InvalidInputException
     * */
    @SuppressWarnings ( "unchecked")
    @Override
    public Set<Long> getUserIdsUnderRegionConnectedToZillow( long regionId, int startIndex, int batchSize )
        throws InvalidInputException
    {

        if ( regionId <= 0l ) {
            LOG.error( "Invalid regionId passed in getUserIdsUnderRegionConnectedToZillow" );
            throw new InvalidInputException( "Invalid regionId passed in getUserIdsUnderRegionConnectedToZillow" );
        }

        LOG.info( "Method called to fetch users under region for region id : " + regionId + " in batch started" );
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery( fetchUserIdsUnderRegionQuery ).addScalar( "USER_ID", StandardBasicTypes.LONG );
        List<Integer> statusesList = new ArrayList<Integer>();
        statusesList.add( CommonConstants.STATUS_ACTIVE );
        statusesList.add( CommonConstants.STATUS_NOT_VERIFIED );

        query.setParameterList( "statuses", statusesList );
        query.setParameter( "status", CommonConstants.STATUS_ACTIVE );
        query.setParameter( "isZillowConnected", CommonConstants.YES );
        query.setParameter( "regionId", regionId );
        query.setParameter( "profileMasterId", CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID );

        if ( startIndex > -1 )
            query.setFirstResult( startIndex );
        if ( batchSize > -1 )
            query.setMaxResults( batchSize );

        List<Long> ids = (List<Long>) query.list();

        Set<Long> userIds = new HashSet<Long>();

        if ( ids != null && !ids.isEmpty() ) {
            for ( Long id : ids ) {
                if ( id > 0 ) {
                    userIds.add( id );
                }
            }
        }
        LOG.info( "Method called to fetch users under region for region id : " + regionId + " in batch ended" );
        return userIds;
    }


    /**
     * Method to get users under branch connected to zillow
     * @param branchId
     * @param startIndex
     * @param batchSize
     * @throws InvalidInputException
     * */
    @SuppressWarnings ( "unchecked")
    @Override
    public Set<Long> getUserIdsUnderBranchConnectedToZillow( long branchId, int startIndex, int batchSize )
        throws InvalidInputException
    {

        if ( branchId <= 0l ) {
            LOG.error( "Invalid branchId passed in getUserIdsUnderBranchConnectedToZillow" );
            throw new InvalidInputException( "Invalid branchId passed in getUserIdsUnderBranchConnectedToZillow" );
        }

        LOG.info( "Method called to fetch users under branch for branch id : " + branchId + " in batch started" );
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery( fetchUserIdsUnderBranchQuery ).addScalar( "USER_ID", StandardBasicTypes.LONG );

        List<Integer> statusesList = new ArrayList<Integer>();
        statusesList.add( CommonConstants.STATUS_ACTIVE );
        statusesList.add( CommonConstants.STATUS_NOT_VERIFIED );

        query.setParameterList( "statuses", statusesList );
        query.setParameter( "status", CommonConstants.STATUS_ACTIVE );
        query.setParameter( "isZillowConnected", CommonConstants.YES );
        query.setParameter( "branchId", branchId );
        query.setParameter( "profileMasterId", CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID );

        if ( startIndex > -1 )
            query.setFirstResult( startIndex );
        if ( batchSize > -1 )
            query.setMaxResults( batchSize );

        List<Long> ids = (List<Long>) query.list();

        Set<Long> userIds = new HashSet<Long>();

        if ( ids != null && !ids.isEmpty() ) {
            for ( long id : ids ) {
                if ( id > 0 ) {
                    userIds.add( id );
                }
            }
        }
        LOG.info( "Method called to fetch users under branch for branch id : " + branchId + " in batch ended" );
        return userIds;
    }
}
