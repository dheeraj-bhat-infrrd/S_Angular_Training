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
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

@Component("company")
public class CompanyDaoImpl extends GenericDaoImpl<Company, Long> implements CompanyDao {
	private static final Logger LOG = LoggerFactory.getLogger(CompanyDaoImpl.class);

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
    public List<Object[]> getUserAdoptionData( long companyId ) throws InvalidInputException
    {
        if ( companyId <= 0 ) {
            LOG.error( "Invalid company id passed in getUserAdoptionData" );
            throw new InvalidInputException( "Invalid company id passed in getUserAdoptionData" );
        }

        LOG.info( "Method to get user adoption data for company id : " + companyId + ",getUserAdoptionData() started." );

        List<Integer> statuses = new ArrayList<Integer>();
        statuses.add( CommonConstants.STATUS_ACTIVE );
        statuses.add( CommonConstants.STATUS_NOT_VERIFIED );

        String queryString = "SELECT C.COMPANY as Company,(CASE WHEN R.REGION = :defaultRegion THEN NULL ELSE R.REGION END) as Region,(CASE WHEN B.BRANCH = :defaultBranch THEN NULL ELSE B.BRANCH END) as Branch,COUNT(DISTINCT (CASE WHEN U.STATUS IN (:statuses) THEN U.USER_ID ELSE NULL END)) AS Invited,COUNT(DISTINCT (CASE WHEN U.STATUS IN (:status) THEN U.USER_ID ELSE NULL END)) AS Active,(CONCAT(ROUND(COUNT(DISTINCT (CASE WHEN U.STATUS IN (:status) THEN U.USER_ID ELSE NULL END)) / COUNT(DISTINCT (CASE WHEN U.STATUS IN (:statuses) THEN U.USER_ID ELSE NULL END)) * 100),'%')) as 'Adoption Rate' FROM USERS U, BRANCH B, USER_PROFILE UP, REGION R, COMPANY C WHERE UP.USER_ID = U.USER_ID AND B.BRANCH_ID = UP.BRANCH_ID AND B.REGION_ID = R.REGION_ID AND C.COMPANY_ID = UP.COMPANY_ID AND UP.COMPANY_ID = :companyId AND R.STATUS = :status AND B.STATUS = :status GROUP BY B.BRANCH_ID ORDER BY C.COMPANY, R.REGION, B.BRANCH";
        // TODO: remove this log
        LOG.info( queryString );
        Query query = getSession().createSQLQuery( queryString );
        query.setParameter( "defaultRegion", CommonConstants.DEFAULT_REGION_NAME );
        query.setParameter( "defaultBranch", CommonConstants.DEFAULT_BRANCH_NAME );
        query.setParameterList( "statuses", statuses );
        query.setParameter( "companyId", companyId );
        query.setParameter( "status", CommonConstants.STATUS_ACTIVE );
        LOG.info( "Querying database to fetch the user adoption data for company id : " + companyId );
        List<Object[]> rows = (List<Object[]>) query.list();
        LOG.info( "Querying database to fetch the user adoption data for company id : " + companyId + " finished" );
        LOG.info( "Method to get user adoption data for company id : " + companyId + ",getUserAdoptionData() finished." );
        return rows;
    }
}
// JIRA SS-42 By RM-05 EOC