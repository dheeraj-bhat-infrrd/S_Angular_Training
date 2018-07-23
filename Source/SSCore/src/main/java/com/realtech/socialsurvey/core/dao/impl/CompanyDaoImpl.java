package com.realtech.socialsurvey.core.dao.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.entities.BillingReportData;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


@Component ( "company")
public class CompanyDaoImpl extends GenericDaoImpl<Company, Long> implements CompanyDao
{
    private static final Logger LOG = LoggerFactory.getLogger( CompanyDaoImpl.class );
    private static final String activeUsersInCompany = "select subquery_Data.COMPANY_ID, C.COMPANY, subquery_Data.USER_ID, subquery_Data.FIRST_NAME,"
        + "subquery_Data.LAST_NAME, subquery_Data.LOGIN_NAME,subquery_Data.REGION_ID,subquery_Data.BRANCH_ID,subquery_Data.REGION, "
        + "subquery_Data.BRANCH, group_concat(distinct outer_up.PROFILES_MASTER_ID) as PROFILES_MASTER_ID From USER_PROFILE outer_up JOIN"
        + " COMPANY C ON C.BILLING_MODE='I' JOIN "
        + "(Select U.USER_ID as USER_ID,U.FIRST_NAME as FIRST_NAME,U.LAST_NAME as LAST_NAME, U.LOGIN_NAME as LOGIN_NAME, "
        + "R.REGION_ID as REGION_ID, B.BRANCH_ID as BRANCH_ID, R.REGION as REGION, B.BRANCH as BRANCH, U.COMPANY_ID as COMPANY_ID "
        + "FROM USERS U JOIN (select UP.USER_ID,UP.REGION_ID,UP.BRANCH_ID FROM USER_PROFILE UP "
        + "where UP.STATUS IN (1,2) and UP.IS_PRIMARY=1 ) subquery_UP ON subquery_UP.USER_ID=U.USER_ID JOIN REGION R ON "
        + "R.REGION_ID = subquery_UP.REGION_ID JOIN BRANCH B ON B.BRANCH_ID = subquery_UP.BRANCH_ID JOIN "
        + "LICENSE_DETAILS L ON L.COMPANY_ID = U.COMPANY_ID where L.ACCOUNTS_MASTER_ID = 4 ) as  subquery_Data ON "
        + "outer_up.USER_ID = subquery_Data.USER_ID where C.COMPANY_ID = subquery_Data.COMPANY_ID "
        + "group by outer_up.USER_ID order by subquery_Data.COMPANY_ID, outer_up.REGION_ID, outer_up.BRANCH_ID";


    private static final String activeUsersInGivenCompany = "select subquery_Data.COMPANY_ID, C.COMPANY, subquery_Data.USER_ID, subquery_Data.FIRST_NAME,"
        + "subquery_Data.LAST_NAME, subquery_Data.LOGIN_NAME,subquery_Data.REGION_ID,subquery_Data.BRANCH_ID,subquery_Data.REGION, "
        + "subquery_Data.BRANCH, group_concat(distinct outer_up.PROFILES_MASTER_ID) as PROFILES_MASTER_ID From USER_PROFILE outer_up JOIN"
        + " COMPANY C ON C.BILLING_MODE='I' JOIN "
        + "(Select U.USER_ID as USER_ID,U.FIRST_NAME as FIRST_NAME,U.LAST_NAME as LAST_NAME, U.LOGIN_NAME as LOGIN_NAME, "
        + "R.REGION_ID as REGION_ID, B.BRANCH_ID as BRANCH_ID, R.REGION as REGION, B.BRANCH as BRANCH, U.COMPANY_ID as COMPANY_ID "
        + "FROM USERS U JOIN (select UP.USER_ID,UP.REGION_ID,UP.BRANCH_ID FROM USER_PROFILE UP "
        + "where UP.STATUS IN (1,2) and UP.IS_PRIMARY=1 ) subquery_UP ON subquery_UP.USER_ID=U.USER_ID JOIN REGION R ON "
        + "R.REGION_ID = subquery_UP.REGION_ID JOIN BRANCH B ON B.BRANCH_ID = subquery_UP.BRANCH_ID JOIN "
        + "LICENSE_DETAILS L ON L.COMPANY_ID = U.COMPANY_ID where L.ACCOUNTS_MASTER_ID = 4 AND L.COMPANY_ID=:companyId ) as  subquery_Data ON "
        + "outer_up.USER_ID = subquery_Data.USER_ID where C.COMPANY_ID = subquery_Data.COMPANY_ID "
        + "group by outer_up.USER_ID order by subquery_Data.COMPANY_ID, outer_up.REGION_ID, outer_up.BRANCH_ID";

    private static final String companyDetailByBillingMode = "select C.COMPANY_ID ,  C.COMPANY  , C.STATUS , C.BILLING_MODE , C.CREATED_ON ,  "
        + " L.SUBSCRIPTION_ID  , L.LICENSE_START_DATE , L.PAYMENT_RETRIES  from " + "COMPANY C JOIN LICENSE_DETAILS L "
        + "ON L.COMPANY_ID = C.COMPANY_ID where L.PAYMENT_MODE= 'A'";


    @Autowired
    SessionFactory sessionFactory;


    @Override
    @SuppressWarnings ( "unchecked")
    public List<Company> searchBetweenTimeIntervals( Timestamp lowerTime, Timestamp higherTime )
    {
        LOG.debug( "Inside method searchBetweenTimeIntervals" );
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria( Company.class );
        criteria.add( Restrictions.ge( "createdOn", lowerTime ) );
        if ( higherTime != null ) {
            criteria.add( Restrictions.le( "createdOn", higherTime ) );
        }
        return criteria.list();
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public List<Object[]> searchCompaniesByName( String namePattern )
    {
        String queryString = "select c.company_id,c.company,c.status from COMPANY c "
            + "inner join (select u.company_id,count(u.user_id) as user_count "
            + "from USERS u where u.status in (1,2) group by u.company_id) a "
            + "on c.company_id = a.company_id and c.status=1 and a.user_count > 2 "
            + "and c.company like :namePattern";
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery( queryString );
        query.setParameter( "namePattern", namePattern+"%" );
        List<Object[]> result = query.list();
        return result;
        /*Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria( Company.class );
        criteria.add( Restrictions.like( "company", namePattern, MatchMode.START ) );
        criteria.add( Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ) );
        return criteria.list();*/
    }


    /**
     * 
     * This method will return the list of company ids
     * 
     */
    
    @SuppressWarnings ( "unchecked")
    @Override
    public List<Long> searchCompaniesByNameAndKeyValue( String namePattern, int accountType, int status,
        boolean inCompleteCompany , Timestamp startDate )
    {
        Criteria criteria = getSession().createCriteria( Company.class );
        if ( namePattern != null && namePattern != "" ) {
            Disjunction or = Restrictions.disjunction();
            or.add( Restrictions.ilike( "company", namePattern, MatchMode.START ) );
            or.add( Restrictions.ilike( "company", "% " + namePattern + "%" ) );
            criteria.add( or );
        }
        
        if(startDate != null)
            criteria.add( Restrictions.ge( "createdOn", startDate ) );

        if ( inCompleteCompany ) {
            criteria.add( Restrictions.sqlRestriction( "COMPANY_ID NOT in (select ld.COMPANY_ID from LICENSE_DETAILS ld)" ) );
            criteria.add( Restrictions.ne( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_COMPANY_DELETED ) );
            
        } else {
            if ( status > -1 ) {
                criteria.add( Restrictions.eq( CommonConstants.STATUS_COLUMN, status ) );
            }
            if ( status == CommonConstants.STATUS_ACTIVE ) {
                criteria.add( Restrictions.sqlRestriction( "COMPANY_ID in (select ld.COMPANY_ID from LICENSE_DETAILS ld)" ) );
            }
            if ( accountType > -1 ) {
                criteria.add( Restrictions
                    .sqlRestriction( "COMPANY_ID in (select ld.COMPANY_ID from LICENSE_DETAILS ld where ACCOUNTS_MASTER_ID="
                        + accountType + ")" ) );
            }
        }
        criteria.setProjection(Projections.property("companyId"));
        //criteria.addOrder( Order.asc( "company" ) );
        return criteria.list();
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public List<Company> getCompaniesByDateRange( Timestamp startTime, Timestamp endTime )
    {
        Criteria criteria = getSession().createCriteria( Company.class );
        criteria.add( Restrictions.ne( CommonConstants.CREATED_BY, "ADMIN" ) );
        criteria.addOrder( Order.desc( CommonConstants.CREATED_ON ) );
        if ( startTime != null )
            criteria.add( Restrictions.ge( CommonConstants.CREATED_ON, startTime ) );
        if ( endTime != null )
            criteria.add( Restrictions.le( CommonConstants.CREATED_ON, endTime ) );
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


    /**
     * Method to fetch all users in each company for billing report
     * @param companyId
     * @return
     */
    @SuppressWarnings ( "unchecked")
    @Override
    public List<BillingReportData> getAllUsersInCompanysForBillingReport( int startIndex, int batchSize )
    {
        LOG.info( "Method getAllUsersInCompanyForBillingReport started" );
        Query query = getSession().createSQLQuery( activeUsersInCompany );
        if ( startIndex > -1 ) {
            query.setFirstResult( startIndex );
        }
        if ( batchSize > -1 ) {
            query.setMaxResults( batchSize );
        }
        LOG.debug( "QUERY : " + query.getQueryString() );
        List<Object[]> rows = (List<Object[]>) query.list();
        if ( rows == null || rows.isEmpty() ) {
            LOG.debug( "Cound not find any more users in company having billing mode Invoice and of type enterprise" );
            return null;
        }

        //Parse rows into BilllingReportData
        List<BillingReportData> billingReportData = new ArrayList<BillingReportData>();
        for ( Object[] row : rows ) {
            BillingReportData reportRow = new BillingReportData();
            reportRow.setCompanyId( Long.parseLong( String.valueOf( row[0] ) ) );
            reportRow.setCompany( String.valueOf( row[1] ) );
            reportRow.setUserId( Long.parseLong( String.valueOf( row[2] ) ) );
            reportRow.setFirstName( String.valueOf( row[3] ) );
            reportRow.setLastName( String.valueOf( row[4] ) );
            reportRow.setLoginName( String.valueOf( row[5] ) );
            reportRow.setRegionId( Long.parseLong( String.valueOf( row[6] ) ) );
            reportRow.setBranchId( Long.parseLong( String.valueOf( row[7] ) ) );
            reportRow.setRegion( String.valueOf( row[8] ) );
            reportRow.setBranch( String.valueOf( row[9] ) );
            List<Long> profilesMasterIds = new ArrayList<Long>();
            String[] profilesMastersStr = String.valueOf( row[10] ).split( "," );
            for ( String pmId : profilesMastersStr ) {
                long profilesMasterId = Long.parseLong( String.valueOf( pmId ) );
                if ( !profilesMasterIds.contains( profilesMasterId ) )
                    profilesMasterIds.add( profilesMasterId );
            }
            reportRow.setProfilesMasterIds( profilesMasterIds );

            billingReportData.add( reportRow );
        }
        LOG.info( "Method getAllUsersInCompanyForBillingReport finished" );
        return billingReportData;
    }


    /**
     * Method to fetch all users in given companies for billing report
     * @param companyId
     * @return
     */
    @SuppressWarnings ( "unchecked")
    @Override
    public List<BillingReportData> getAllUsersInGivenCompaniesForBillingReport( int startIndex, int batchSize, Long companyId )
    {
        LOG.info( "Method getAllUsersInGivenCompaniesForBillingReport started for company : " + companyId );
        Query query = getSession().createSQLQuery( activeUsersInGivenCompany );
        query.setParameter( "companyId", companyId );
        if ( startIndex > -1 ) {
            query.setFirstResult( startIndex );
        }
        if ( batchSize > -1 ) {
            query.setMaxResults( batchSize );
        }
        LOG.debug( "QUERY : " + query.getQueryString() );
        List<Object[]> rows = (List<Object[]>) query.list();
        if ( rows == null || rows.isEmpty() ) {
            LOG.debug( "Cound not find any more users in company : " + companyId );
            return null;
        }

        //Parse rows into BilllingReportData
        List<BillingReportData> billingReportData = new ArrayList<BillingReportData>();
        for ( Object[] row : rows ) {
            BillingReportData reportRow = new BillingReportData();
            reportRow.setCompanyId( Long.parseLong( String.valueOf( row[0] ) ) );
            reportRow.setCompany( String.valueOf( row[1] ) );
            reportRow.setUserId( Long.parseLong( String.valueOf( row[2] ) ) );
            reportRow.setFirstName( String.valueOf( row[3] ) );
            reportRow.setLastName( String.valueOf( row[4] ) );
            reportRow.setLoginName( String.valueOf( row[5] ) );
            reportRow.setRegionId( Long.parseLong( String.valueOf( row[6] ) ) );
            reportRow.setBranchId( Long.parseLong( String.valueOf( row[7] ) ) );
            reportRow.setRegion( String.valueOf( row[8] ) );
            reportRow.setBranch( String.valueOf( row[9] ) );
            List<Long> profilesMasterIds = new ArrayList<Long>();
            String[] profilesMastersStr = String.valueOf( row[10] ).split( "," );
            for ( String pmId : profilesMastersStr ) {
                long profilesMasterId = Long.parseLong( String.valueOf( pmId ) );
                if ( !profilesMasterIds.contains( profilesMasterId ) )
                    profilesMasterIds.add( profilesMasterId );
            }
            reportRow.setProfilesMasterIds( profilesMasterIds );

            billingReportData.add( reportRow );
        }
        LOG.info( "Method getAllUsersInGivenCompaniesForBillingReport finished" );
        return billingReportData;
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public List<Company> getCompaniesWithExpiredInvoice()
    {
        LOG.debug( "method getCompaniesWithExpiredInvoice started " );
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria( Company.class, "company" );
        criteria.createAlias( "company.licenseDetails", "licenseDetail" );
        criteria.add( Restrictions.eq( "licenseDetail.paymentMode", CommonConstants.BILLING_MODE_INVOICE ) );
        criteria.add( Restrictions.eq( "licenseDetail.accountsMaster.accountsMasterId",
            CommonConstants.ACCOUNTS_MASTER_ENTERPRISE ) );
        criteria.add( Restrictions.le( "licenseDetail.nextInvoiceBillingDate", new Date( System.currentTimeMillis() ) ) );
        criteria.setResultTransformer( CriteriaSpecification.DISTINCT_ROOT_ENTITY );
        List<Company> companies = criteria.list();
        LOG.debug( "method getCompaniesWithExpiredInvoice started ended" );
        return companies;

    }


    @Override
    public Company getCompanyByBraintreeSubscriptionId( String subscriptionId )
    {
        LOG.debug( "method getCompaniesWithExpiredInvoice started " );
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria( Company.class, "company" );
        criteria.createAlias( "company.licenseDetails", "licenseDetail" );
        criteria.add( Restrictions.eq( "licenseDetail.paymentMode", CommonConstants.BILLING_MODE_AUTO ) );
        criteria.add( Restrictions.le( "licenseDetail.subscriptionId", subscriptionId ) );
        criteria.setResultTransformer( CriteriaSpecification.DISTINCT_ROOT_ENTITY );
        criteria.setMaxResults( 1 );
        Company company = (Company) criteria.uniqueResult();
        LOG.debug( "method getCompaniesWithExpiredInvoice started ended" );
        return company;

    }


    @SuppressWarnings ( "unchecked")
    @Override
    public List<Company> getAllInvoicedActiveCompanies()
    {
        LOG.debug( "method getAllInvoicedActiveCompanies started " );
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria( Company.class, "company" );
        criteria.add( Restrictions.eq( "status", CommonConstants.STATUS_ACTIVE ) );
        criteria.createAlias( "company.licenseDetails", "licenseDetail" );
        criteria.add( Restrictions.eq( "licenseDetail.paymentMode", CommonConstants.BILLING_MODE_INVOICE ) );
        criteria.setResultTransformer( CriteriaSpecification.DISTINCT_ROOT_ENTITY );
        List<Company> companies = criteria.list();
        LOG.debug( "method getAllInvoicedActiveCompanies started ended" );
        return companies;

    }


    @SuppressWarnings ( "unchecked")
    @Override
    public List<Company> getCompaniesByBillingModeAuto()
    {
        LOG.debug( "method getCompaniesByBillingModeAuto started " );
        Query query = getSession().createSQLQuery( companyDetailByBillingMode );
        LOG.debug( "QUERY : " + query.getQueryString() );
        List<Object[]> rows = (List<Object[]>) query.list();
        List<Company> companies = new ArrayList<Company>();

        for ( Object[] row : rows ) {
            Company company = new Company();
            company.setCompanyId( Long.parseLong( String.valueOf( row[0] ) ) );
            company.setCompany( String.valueOf( row[1] ) );
            company.setStatus( Integer.parseInt( String.valueOf( row[2] ) ) );
            company.setBillingMode( String.valueOf( row[3] ) );
            company.setCreatedOn( Timestamp.valueOf( String.valueOf( row[4] ) ) );

            LicenseDetail licenseDetail = new LicenseDetail();
            licenseDetail.setSubscriptionId( String.valueOf( row[5] ) );
            if(String.valueOf( row[6] ) != null)
                licenseDetail.setLicenseStartDate( Timestamp.valueOf( String.valueOf( row[6] ) ) );
            licenseDetail.setPaymentRetries( Integer.parseInt( String.valueOf( row[7] ) )  );

            List<LicenseDetail> licenseDetails = new ArrayList<LicenseDetail>();
            licenseDetails.add( licenseDetail );
            company.setLicenseDetails( licenseDetails );

            companies.add( company );
        }

        return companies;
    }
    
    
    
    @SuppressWarnings ( "unchecked")
    @Override
    public Map<Long , Company> getCompaniesByIds(Set<Long> ids)
    {
        LOG.debug( "method getCompaniesById started " );
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria( Company.class, "company" );
        criteria.add( Restrictions.in( "companyId", ids ) );
        List<Company> companies = criteria.list();
        
        Map<Long , Company> cpmanyiesById = new HashMap<Long , Company>();
        for(Company company : companies){
            cpmanyiesById.put( company.getCompanyId(), company );
        }
        
        LOG.debug( "method getAllInvoicedActiveCompanies started ended" );
        return cpmanyiesById;

    }
    
    @SuppressWarnings ( "unchecked")
    @Override
    public List<Company> getCompaniesByStatusAndAccountMasterId(int status , int accountMasterId){
        LOG.info( "method getCompaniesByStatusAndAccountType started for status %s and accountType %s " );
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( Company.class , "company");
        criteria.add( Restrictions.eq( CommonConstants.STATUS_COLUMN, status ) );
        criteria.createAlias( "company.licenseDetails", "licenseDetail" );
        criteria.add( Restrictions.eq( "licenseDetail.accountsMaster.accountsMasterId", accountMasterId ) );
        List<Company> companies = criteria.list();
        

        LOG.info( "method getCompaniesByStatusAndAccountType finished for status %s and accountType %s " );
        return companies;
    }
    
    
    @SuppressWarnings ( "unchecked")
    @Override
    public List<Company> getCompanyListByIds(Set<Long> companyIds)
    {
        LOG.debug( "method getCompanyListByIds started " );
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria( Company.class, "company" );
        criteria.add( Restrictions.in( "companyId", companyIds ) );
        List<Company> companies = criteria.list();
        
        LOG.debug( "method getCompanyListByIds started ended" );
        return companies;

    }

    @SuppressWarnings ( "unchecked")
	@Override
	public List<Long> filterIdsByStatus(List<Long> companies, List<Integer> status) {
        LOG.debug( "method getCompanyListByIds started " );
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria( Company.class, "company" );
        criteria.add( Restrictions.in( "companyId", companies ) );
        criteria.add( Restrictions.in( "status", status ) );
        criteria.setProjection(Projections.property("companyId"));
        List<Long> filteredCompanies = criteria.list();
        
        LOG.debug( "method getCompanyListByIds started ended" );
        return filteredCompanies;
		
	}
}
