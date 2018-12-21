package com.realtech.socialsurvey.core.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Iterator;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.RegionDao;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

@Component("branch")
public class BranchDaoImpl extends GenericDaoImpl<Branch, Long> implements BranchDao {

	private static final Logger LOG = LoggerFactory.getLogger(BranchDaoImpl.class);

    @Autowired
    private CompanyDao companyDao;
    
    @Autowired
    private RegionDao regionDao;

    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;


	/*
	 * Method to delete all the users of a company.
	 */
	@Override
	public void deleteBranchesByCompanyId(long companyId) {
		LOG.info("Method to delete all the branches by company id,deleteBranchesByCompanyId() started.");
		try {
			Query query = getSession().createQuery("delete from Branch where company.companyId=?");
			query.setParameter(0, companyId);
			query.executeUpdate();
		}
		catch (HibernateException hibernateException) {
			LOG.error("Exception caught in deleteBranchesByCompanyId() ", hibernateException);
			throw new DatabaseException("Exception caught in deleteBranchesByCompanyId() ", hibernateException);
		}
		LOG.info("Method to delete all the branches by company id, deleteBranchesByCompanyId() finished.");
	}


    @SuppressWarnings ( "unchecked")
    @Override
    public List<Branch> getBranchForBranchIds( Set<Long> branchIds ) throws InvalidInputException
    {
        if ( branchIds == null || branchIds.isEmpty() )
            throw new InvalidInputException( "Branch ids passed cannot be null or empty" );
        LOG.info( "Method to get all the branches for branches ids,getBranchForBranchIds() started." );
        Criteria criteria = getSession().createCriteria( Branch.class );
        criteria.add( Restrictions.in( CommonConstants.BRANCH_ID_COLUMN, branchIds ) );
        LOG.info( "Method to get all the branches for branches ids, getBranchForBranchIds() finished." );
        return criteria.list();
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public List<Branch> getBranchesForCompany( long companyId, int isDefault,int start, int batch ) throws InvalidInputException
    {
        if ( companyId <= 0 )
            throw new InvalidInputException( "Invalid company id is passed in getBranchesForCompany()" );
        LOG.debug( "Method to get all branches for company,getBranchesForCompany() started." );
        Criteria criteria = getSession().createCriteria( Branch.class );
        criteria.add( Restrictions.eq( CommonConstants.COMPANY_COLUMN, companyDao.findById( Company.class, companyId ) ) );
        criteria.add( Restrictions.eq( CommonConstants.IS_DEFAULT_BY_SYSTEM, isDefault ) );
        criteria.add( Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ) );
        criteria.addOrder(Order.asc("branch"));
        if ( start > 0 )
            criteria.setFirstResult( start );
        if ( batch > 0 )
            criteria.setFetchSize( batch );

        List<Branch> branchList = criteria.list();
        if ( isDefault == CommonConstants.NO ) {
            Set<Long> branchIdList = new LinkedHashSet<Long>();
            Map<Long, Branch> branchIdObjMap = new TreeMap<>();
            if ( branchList != null && branchList.size() > 0 ) {
                for ( Branch branch : branchList ) {
                    branchIdList.add( branch.getBranchId() );
                    branchIdObjMap.put( branch.getBranchId(), branch );
                }
            }

            List<Branch> finalBranchList = new ArrayList<>();
            if ( branchIdList.size() > 0 ) {
                LOG.debug( "Fetching branch settings for the fetched branches" );
                List<OrganizationUnitSettings> branchSettingList = organizationUnitSettingsDao
                    .fetchOrganizationUnitSettingsForMultipleIds( branchIdList,
                        MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
                LOG.debug( "Adding address, state city and zipcode info into branches" );
                if ( branchSettingList != null && branchSettingList.size() > 0 ) {
                    for ( OrganizationUnitSettings branchSettings : branchSettingList ) {
                        if ( branchIdObjMap.get( branchSettings.getIden() ) != null ) {
                            Branch branch = branchIdObjMap.get( branchSettings.getIden() );
                            branch.setAddress1( branchSettings.getContact_details().getAddress1() );
                            branch.setAddress2( branchSettings.getContact_details().getAddress2() );
                            branch.setCountry( branchSettings.getContact_details().getCountry() );
                            branch.setCountryCode( branchSettings.getContact_details().getCountryCode() );
                            branch.setCity( branchSettings.getContact_details().getCity() );
                            branch.setState( branchSettings.getContact_details().getState() );
                            branch.setZipcode( branchSettings.getContact_details().getZipcode() );

                            finalBranchList.add( branch );
                        }
                    }
                }
            }
            LOG.debug( "Method to get all branches for company,getBranchesForCompany() ended." );
            if ( finalBranchList.size() > 0 )
                return finalBranchList;
            else
                return branchList;
        } else
            return branchList;
    }
    
    
    @SuppressWarnings ( "unchecked")
    @Override
    public List<Branch> getBranchesForRegion( long regionId, int isDefault,int start, int batch ) throws InvalidInputException
    {
        if ( regionId <= 0 )
            throw new InvalidInputException( "Invalid company id is passed in getBranchesForCompany()" );
        LOG.debug( "Method to get all branches for company,getBranchesForCompany() started." );
        Criteria criteria = getSession().createCriteria( Branch.class );
        criteria.add( Restrictions.eq( CommonConstants.REGION_COLUMN, regionDao.findById( Region.class, regionId ) ) );
        
        if( isDefault == CommonConstants.YES || isDefault == CommonConstants.NO ) {
            criteria.add( Restrictions.eq( CommonConstants.IS_DEFAULT_BY_SYSTEM, isDefault ) );
        }
                criteria.add( Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ) );
        criteria.addOrder(Order.asc("branch"));
        if ( start > 0 )
            criteria.setFirstResult( start );
        if ( batch > 0 )
            criteria.setFetchSize( batch );

        List<Branch> branchList = criteria.list();
        if ( isDefault == CommonConstants.NO ) {
            Set<Long> branchIdList = new LinkedHashSet<Long>();
            Map<Long, Branch> branchIdObjMap = new TreeMap<>();
            if ( branchList != null && branchList.size() > 0 ) {
                for ( Branch branch : branchList ) {
                    branchIdList.add( branch.getBranchId() );
                    branchIdObjMap.put( branch.getBranchId(), branch );
                }
            }

            List<Branch> finalBranchList = new ArrayList<>();
            if ( branchIdList.size() > 0 ) {
                LOG.debug( "Fetching branch settings for the fetched branches" );
                List<OrganizationUnitSettings> branchSettingList = organizationUnitSettingsDao
                    .fetchOrganizationUnitSettingsForMultipleIds( branchIdList,
                        MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
                LOG.debug( "Adding address, state city and zipcode info into branches" );
                if ( branchSettingList != null && branchSettingList.size() > 0 ) {
                    for ( OrganizationUnitSettings branchSettings : branchSettingList ) {
                        if ( branchIdObjMap.get( branchSettings.getIden() ) != null ) {
                            Branch branch = branchIdObjMap.get( branchSettings.getIden() );
                            branch.setAddress1( branchSettings.getContact_details().getAddress1() );
                            branch.setAddress2( branchSettings.getContact_details().getAddress2() );
                            branch.setCity( branchSettings.getContact_details().getCity() );
                            branch.setState( branchSettings.getContact_details().getState() );
                            branch.setZipcode( branchSettings.getContact_details().getZipcode() );

                            finalBranchList.add( branch );
                        }
                    }
                }
            }
            LOG.debug( "Method to get all branches for company,getBranchesForCompany() ended." );
            if ( finalBranchList.size() > 0 )
                return finalBranchList;
            else
                return branchList;
        } else
            return branchList;
    }

    /**
     * Method to fetch the region and branch name.
     * @param regionId
     * @param branchId
     * @return map containing branch name and region name
     * @throws InvalidInputException
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> getBranchAndRegionName(long regionId, long branchId) throws InvalidInputException {
        Map<String, String> branchAndRegionNameMap = new HashMap<>();
        if(regionId < 0 || branchId < 0)
            throw new InvalidInputException(" Invalid branchId or regionId passed to getBranchAndRegionName method ");
        LOG.info("Method to get the region and branch name for region id: "+regionId+"branchId: "+branchId+" started");

        Query query = getSession().createSQLQuery(" SELECT BRANCH.BRANCH, REGION.REGION FROM BRANCH \n" +
                "INNER JOIN REGION ON BRANCH.REGION_ID = REGION.REGION_ID AND BRANCH.BRANCH_ID = :branchId \n" +
                "AND  REGION.REGION_ID = :regionId ");
        query.setParameter(CommonConstants.BRANCH_ID_COLUMN, branchId);
        query.setParameter(CommonConstants.REGION_ID_COLUMN, regionId);
        List<Object[]> rows = query.list();
        for(Object[] row: rows) {
            branchAndRegionNameMap.put(CommonConstants.BRANCH_NAME_COLUMN, row[0].toString());
            branchAndRegionNameMap.put(CommonConstants.REGION_COLUMN, row[1].toString());
        }
        LOG.info(branchAndRegionNameMap.toString());
        return branchAndRegionNameMap;
    }


    /**
     * Method to fetch all branch ids under company
     * @param companyId
     * @throws InvalidInputException
     * */
    @SuppressWarnings ( "unchecked")
    @Override
    @Transactional
    public List<Long> getBranchIdsUnderCompany( long companyId, int start, int batchSize ) throws InvalidInputException
    {
        if ( companyId <= 0 ) {
            throw new InvalidInputException( "Invalid company id passed in getBranchIdsUnderCompany method" );
        }
        LOG.info( "Method to get all branch ids under company id : " + companyId + ",getBranchIdsUnderCompany() started." );
        Criteria criteria = null;
        try {
            criteria = getSession().createCriteria( Branch.class );
            criteria.setProjection( Projections.property( CommonConstants.BRANCH_ID_COLUMN ).as(
                CommonConstants.BRANCH_ID_COLUMN ) );
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_COLUMN, companyDao.findById( Company.class, companyId ) ) );
            criteria.add( Restrictions.eq( CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.NO ) );
            criteria.add( Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ) );
            if ( start > 0 )
                criteria.setFirstResult( start );
            if ( batchSize > 0 )
                criteria.setMaxResults( batchSize );
        } catch ( HibernateException e ) {
            LOG.error( "HibernateException caught in getBranchIdsUnderCompany(). Reason: " + e.getMessage(), e );
            throw new DatabaseException( "HibernateException caught in getBranchIdsUnderCompany().", e );
        }
        LOG.info( "Method to get all branch ids under company id : " + companyId + ",getBranchIdsUnderCompany() ended." );
        return criteria.list();
    }


	@Override
	public long getRegionIdByBranchId(long branchId) {
		LOG.debug( "Method to fetch regionId from branchId getRegionIdByBranchId() started." );
		Query query = getSession().createSQLQuery( "SELECT region_id FROM BRANCH WHERE branch_id = :branchId " );
        query.setParameter( "branchId", branchId  );
        long regionId = (int) query.uniqueResult();
        LOG.debug( "Method to fetch regionId from branchId getRegionIdByBranchId() finished." );
        return regionId;
	}
    
	/**
	 * Method to get branch Ids of a company
	 * @param companyId
	 * @return
	 * @throws InvalidInputException
	 */
	@SuppressWarnings ( "unchecked")
    @Override
    @Transactional
    public List<Long> getBranchIdsOfCompany( long companyId ) throws InvalidInputException
    {
        if ( companyId <= 0 ) {
            throw new InvalidInputException( "Invalid company id passed in getBranchIdsUnderCompany method" );
        }
        LOG.info( "Method to get all branch ids under company id : " + companyId + ",getBranchIdsUnderCompany() started." );
        Criteria criteria = null;
        try {
            criteria = getSession().createCriteria( Branch.class );
            criteria.setProjection( Projections.property( CommonConstants.BRANCH_ID_COLUMN ).as(
                CommonConstants.BRANCH_ID_COLUMN ) );
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_COLUMN, companyDao.findById( Company.class, companyId ) ) );
            criteria.add( Restrictions.eq( CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.NO ) );
            criteria.add( Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ) );
        } catch ( HibernateException e ) {
            LOG.error( "HibernateException caught in getBranchIdsUnderCompany(). Reason: " + e.getMessage(), e );
            throw new DatabaseException( "HibernateException caught in getBranchIdsUnderCompany().", e );
        }
        LOG.info( "Method to get all branch ids under company id : " + companyId + ",getBranchIdsUnderCompany() ended." );
        return criteria.list();
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public Map<Long, Long> getCompanyIdsForBranchIds( List<Long> branchIds )
    {
        LOG.debug( "Inside method getCompanyIdsForBranchIds {}", branchIds );
        if ( branchIds.isEmpty() ) {
            return Collections.emptyMap();
        }

        try {
            Query query = getSession()
                .createQuery( "select b.branchId, b.company.companyId from Branch b where b.branchId in (:ids)" );
            query.setParameterList( "ids", branchIds );

            Map<Long, Long> branchCompanyIdsMap = new HashMap<>();

            List<Object> result = (List<Object>) query.list();
            Iterator<Object> itr = result.iterator();

            while ( itr.hasNext() ) {
                Object[] obj = (Object[]) itr.next();
                Long branchId = (Long) obj[0];
                Long companyId = (Long) obj[1];
                branchCompanyIdsMap.put( branchId, companyId );
                LOG.trace( "branchId {} and company Id {}", branchId, companyId );
            }

            return branchCompanyIdsMap;
        } catch ( HibernateException e ) {
            LOG.error( "HibernateException caught in getCompanyIdsForBranchIds(). Reason: " + e.getMessage(), e );
            throw new DatabaseException( "HibernateException caught in getCompanyIdsForBranchIds().", e );
        }
    }
    
    @SuppressWarnings ( "unchecked")
    @Override
    @Transactional
    public List<Long> getBranchIdsOfRegion( long regionId, int isDefault, int batch, int start ) throws InvalidInputException
    {
        if ( regionId <= 0 ) {
            throw new InvalidInputException( "Invalid region id passed in getBranchIdsOfRegion method" );
        }
        LOG.info( "Method to get all branch ids under region id : {},getBranchIdsOfRegion() started.", regionId );
        Criteria criteria = null;
        try {
            criteria = getSession().createCriteria( Branch.class );
            criteria.setProjection( Projections.property( CommonConstants.BRANCH_ID_COLUMN ).as(
                CommonConstants.BRANCH_ID_COLUMN ) );
            criteria.add( Restrictions.eq( CommonConstants.REGION_COLUMN, regionDao.findById( Region.class, regionId ) ) );

            if( isDefault == CommonConstants.YES || isDefault == CommonConstants.NO ) {
                criteria.add( Restrictions.eq( CommonConstants.IS_DEFAULT_BY_SYSTEM, isDefault ) );
            }
            
            criteria.add( Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ) );
            
            if ( start > 0 )
                criteria.setFirstResult( start );
            if ( batch > 0 )
                criteria.setFetchSize( batch );
            
        } catch ( HibernateException e ) {
            LOG.error( "HibernateException caught in getBranchIdsOfRegion(). Reason: " + e.getMessage(), e );
            throw new DatabaseException( "HibernateException caught in getBranchIdsOfRegion().", e );
        }
        LOG.info( "Method to get all branch ids under region id : {},getBranchIdsOfRegion() ended.", regionId );
        return criteria.list();
    }
    
    //check if branch is default 
    @Override
	public long checkIfBranchIsDefault(long branchId) {
		LOG.debug( "Method to check if branch is default checkIfBranchIsDefault() started." );
		Query query = getSession().createSQLQuery( "SELECT branch_id FROM BRANCH WHERE branch_id = :branchId AND IS_DEFAULT_BY_SYSTEM = 0 " );
        query.setParameter( "branchId", branchId  );
        LOG.debug( "Method to check if branch is default checkIfBranchIsDefault() finished." );
        return  (int) query.uniqueResult();
	}
    
    @Override
    public String getCompanyNameForBranchId(long branchId) {
    	LOG.debug("method to get companyName for branch with branchId : {}",branchId);
    	Branch branch = findById(Branch.class, branchId);
    	return branch.getCompany().getCompany();
    }
}
// JIRA SS-42 By RM-05 EOC
