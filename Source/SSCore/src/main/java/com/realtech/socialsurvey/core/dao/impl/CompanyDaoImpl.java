package com.realtech.socialsurvey.core.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.entities.BillingReportData;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

@Component("company")
public class CompanyDaoImpl extends GenericDaoImpl<Company, Long> implements CompanyDao {
	private static final Logger LOG = LoggerFactory.getLogger(CompanyDaoImpl.class);
    private static final String activeUsersInCompany = "select distinct subquery_Data.USER_ID,subquery_Data.FIRST_NAME,"
        + "subquery_Data.LAST_NAME, subquery_Data.LOGIN_NAME,subquery_Data.REGION_ID,subquery_Data.BRANCH_ID,subquery_Data.REGION, "
        + "subquery_Data.BRANCH, group_concat(distinct outer_up.PROFILES_MASTER_ID) as PROFILES_MASTER_ID From USER_PROFILE outer_up"
        + " JOIN(Select U.USER_ID as USER_ID,U.FIRST_NAME as FIRST_NAME,U.LAST_NAME  as LAST_NAME,U.LOGIN_NAME as LOGIN_NAME,"
        + " R.REGION_ID as REGION_ID, B.BRANCH_ID as BRANCH_ID, R.REGION as REGION, B.BRANCH as BRANCH FROM USERS U JOIN  "
        + "(select UP.USER_ID,UP.REGION_ID,UP.BRANCH_ID FROM USER_PROFILE UP where  UP.COMPANY_ID=? and UP.STATUS IN (1,2) and UP.IS_PRIMARY=1 )"
        + " subquery_UP  ON subquery_UP.USER_ID=U.USER_ID JOIN REGION R ON R.REGION_ID= subquery_UP.REGION_ID JOIN  "
        + "BRANCH B ON B.BRANCH_ID=subquery_UP.BRANCH_ID ) as  subquery_Data ON outer_up.USER_ID = subquery_Data.USER_ID  "
        + "group by outer_up.USER_ID order by outer_up.REGION_ID asc, outer_up.BRANCH_ID";

	@Autowired
	SessionFactory sessionFactory;

	@Override
	@SuppressWarnings("unchecked")
	public List<Company> searchBetweenTimeIntervals(Timestamp lowerTime, Timestamp higherTime) {
		LOG.debug("Inside method searchBetweenTimeIntervals");
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Company.class);
		criteria.add(Restrictions.ge("createdOn", lowerTime));
		if (higherTime != null) {
			criteria.add(Restrictions.le("createdOn", higherTime));
		}
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Company> searchCompaniesByName(String namePattern) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Company.class);
		criteria.add(Restrictions.like("company", namePattern, MatchMode.START));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Company> searchCompaniesByNameAndKeyValue(String namePattern, int accountType, int status) {
		Criteria criteria = getSession().createCriteria(Company.class);
		criteria.add(Restrictions.ilike("company", namePattern, MatchMode.START));
		if (status > -1) {
			criteria.add(Restrictions.eq(CommonConstants.STATUS_COLUMN, status));
		}
		if (accountType > -1) {
			criteria.add(Restrictions.sqlRestriction("COMPANY_ID in (select ld.COMPANY_ID from LICENSE_DETAILS ld where ACCOUNTS_MASTER_ID=" + accountType + ")"));
		}
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Company> getCompaniesByDateRange(Timestamp startTime, Timestamp endTime) {
		Criteria criteria = getSession().createCriteria(Company.class);
		criteria.add(Restrictions.ne(CommonConstants.CREATED_BY, "ADMIN"));
		criteria.addOrder(Order.desc(CommonConstants.CREATED_ON));
		if (startTime != null)
			criteria.add(Restrictions.ge(CommonConstants.CREATED_ON, startTime));
		if (endTime != null)
			criteria.add(Restrictions.le(CommonConstants.CREATED_ON, endTime));
		return criteria.list();
	}


    @SuppressWarnings ( "unchecked")
    @Override
    public List<Object[]> getAllUsersAndAdminsUnderACompanyGroupedByBranches( long companyId ) throws InvalidInputException
    {
        if ( companyId <= 0 ) {
            LOG.error( "Invalid company id passed in getAllUsersAndAdminsUnderACompanyGroupedByBranches" );
            throw new InvalidInputException( "Invalid company id passed in getAllUsersAndAdminsUnderACompanyGroupedByBranches" );
        }

        LOG.info( "Method to get all users and admins under a company grouped by branches,getAllUsersAndAdminsUnderACompanyGroupedByBranches() started." );

        List<Integer> statuses = new ArrayList<Integer>();
        statuses.add( CommonConstants.STATUS_ACTIVE );
        statuses.add( CommonConstants.STATUS_NOT_VERIFIED );

        String queryString = "SELECT B.BRANCH_ID, C.COMPANY, R.REGION, B.BRANCH, COUNT(DISTINCT U.USER_ID) FROM USERS U, BRANCH B, USER_PROFILE UP, REGION R, COMPANY C WHERE UP.USER_ID = U.USER_ID AND B.BRANCH_ID = UP.BRANCH_ID AND B.REGION_ID = R.REGION_ID AND C.COMPANY_ID = UP.COMPANY_ID AND U.STATUS IN (:statuses) AND UP.COMPANY_ID = :companyId GROUP BY B.BRANCH_ID";

        Query query = getSession().createSQLQuery( queryString );
        query.setParameterList( "statuses", statuses );
        query.setParameter( "companyId", companyId );
        LOG.info( "Querying database to fetch the users and admins under a company grouped by branches" );
        List<Object[]> rows = (List<Object[]>) query.list();
        LOG.info( "Querying database to fetch the users and admins under a company grouped by branches done" );
        LOG.info( "Method to get all users and admins under a company grouped by branches,getAllUsersAndAdminsUnderACompanyGroupedByBranches() finished." );
        return rows;
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public Map<Long, Integer> getAllActiveUsersAndAdminsUnderACompanyGroupedByBranches( long companyId )
        throws InvalidInputException
    {
        if ( companyId <= 0 ) {
            LOG.error( "Invalid company id passed in getAllActiveUsersAndAdminsUnderACompanyGroupedByBranches()" );
            throw new InvalidInputException(
                "Invalid company id passed in getAllActiveUsersAndAdminsUnderACompanyGroupedByBranches()" );
        }
        LOG.info( "Method to get all active users and admins under a company grouped by branches,getAllActiveUsersAndAdminsUnderACompanyGroupedByBranches() started." );
        String queryString = "SELECT B.BRANCH_ID, COUNT(DISTINCT U.USER_ID) FROM USERS U, BRANCH B, USER_PROFILE UP, REGION R, COMPANY C WHERE UP.USER_ID = U.USER_ID AND B.BRANCH_ID = UP.BRANCH_ID AND B.REGION_ID = R.REGION_ID AND C.COMPANY_ID = UP.COMPANY_ID AND U.STATUS = :status AND UP.COMPANY_ID = :companyId GROUP BY B.BRANCH_ID";

        Query query = getSession().createSQLQuery( queryString );
        query.setParameter( "status", CommonConstants.STATUS_ACTIVE );
        query.setParameter( "companyId", companyId );
        LOG.info( "Querying database to fetch active users and admins under a company grouped by branches" );
        List<Object[]> rows = (List<Object[]>) query.list();
        LOG.info( "Querying database to fetch active users and admins under a company grouped by branches done" );

        if ( rows == null || rows.isEmpty() ) {
            LOG.error( "Cound not find any active users for the company Id and status active" );
            return null;
        }

        LOG.info( "Parsing the branchids and user count returned by the query" );
        Map<Long, Integer> branchIdUserCountMap = new LinkedHashMap<Long, Integer>();
        for ( Object[] row : rows ) {
            if ( row[0] != null && row[1] != null ) {
                try {
                    long branchId = Long.parseLong( String.valueOf( row[0] ) );
                    int userCount = Integer.parseInt( String.valueOf( row[1] ) );
                    branchIdUserCountMap.put( branchId, userCount );
                } catch ( NumberFormatException nfe ) {
                    // ignore and proceed
                }
            }
        }
        LOG.info( "Parsing the branchids and user count returned by the query done" );
        LOG.info( "Method to get all acitve users and admins under a company grouped by branches,getAllActiveUsersAndAdminsUnderACompanyGroupedByBranches() finished." );
        return branchIdUserCountMap;
    }
    
    
    /**
     * Method to fetch all users in company for billing report
     * @param companyId
     * @return
     */
    @SuppressWarnings ( "unchecked")
    @Override
    public List<BillingReportData> getAllUsersInCompanyForBillingReport( long companyId ){
        LOG.info( "Method getAllUsersInCompanyForBillingReport started for company ID : " + companyId );
        Query query = getSession().createSQLQuery( activeUsersInCompany );
        query.setParameter( 0, companyId );
        LOG.debug( "QUERY : " + query.getQueryString() );
        List<Object[]> rows = (List<Object[]>) query.list();
        if ( rows == null || rows.isEmpty() ) {
            LOG.error( "Cound not find any active users for the company Id and status active" );
            return null;
        }
        
        //Parse rows into BilllingReportData
        List<BillingReportData> billingReportData = new ArrayList<BillingReportData>();
        for ( Object[] row : rows ) {
            BillingReportData reportRow = new BillingReportData();
            reportRow.setUserId( Long.parseLong( String.valueOf( row[0] ) ) );
            reportRow.setFirstName( String.valueOf( row[1] ) );
            reportRow.setLastName( String.valueOf( row[2] ) );
            reportRow.setLoginName( String.valueOf( row[3] ) );
            reportRow.setRegionId( Long.parseLong( String.valueOf( row[4] ) ) );
            reportRow.setBranchId( Long.parseLong( String.valueOf( row[5] ) ) );
            reportRow.setRegion( String.valueOf( row[6] ) );
            reportRow.setBranch( String.valueOf( row[7] ) );
            List<Long> profilesMasterIds = new ArrayList<Long>();
            String[] profilesMastersStr = String.valueOf( row[8] ).split( "," );
            for ( String pmId : profilesMastersStr ) {
                long profilesMasterId = Long.parseLong( String.valueOf( pmId ) );
                if ( !profilesMasterIds.contains( profilesMasterId ) )
                    profilesMasterIds.add( profilesMasterId );
            }
            reportRow.setProfilesMasterIds( profilesMasterIds );
            
            billingReportData.add( reportRow );
        }
        LOG.info( "Method getAllUsersInCompanyForBillingReport finished for company ID : " + companyId );
        return billingReportData;
    }
}
// JIRA SS-42 By RM-05 EOC