package com.realtech.socialsurvey.core.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.RegionDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

@Component("region")
public class RegionDaoImpl extends GenericDaoImpl<Region, Long> implements RegionDao {

	private static final Logger LOG = LoggerFactory.getLogger(RegionDaoImpl.class);

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;


    /*
     * Method to delete all the users of a company.
     */
	@Override
	public void deleteRegionsByCompanyId(long companyId) {
		LOG.info("Method to delete all the regions by company id,deleteRegionsByCompanyId() started.");
		try {
			Query query = getSession().createQuery("delete from Region where company.companyId=?");
			query.setParameter(0, companyId);
			query.executeUpdate();
		}
		catch (HibernateException hibernateException) {
			LOG.error("Exception caught in deleteRegionsByCompanyId() ", hibernateException);
			throw new DatabaseException("Exception caught in deleteRegionsByCompanyId() ", hibernateException);
		}
		LOG.info("Method to delete all the regions by company id, deleteRegionsByCompanyId() finished.");
	}


    @SuppressWarnings ( "unchecked")
    @Override
    public List<Region> getRegionForRegionIds( Set<Long> regionIds ) throws InvalidInputException
    {
        if ( regionIds == null || regionIds.isEmpty() )
            throw new InvalidInputException( "Region ids passed cannot be null or empty" );
        LOG.info( "Method to get all the regions for region ids,deleteRegionsByCompanyId() started." );
        Criteria criteria = getSession().createCriteria( Region.class );
        criteria.add( Restrictions.in( CommonConstants.REGION_ID_COLUMN, regionIds ) );
        LOG.info( "Method to get all the regions for region ids, deleteRegionsByCompanyId() finished." );
        return criteria.list();
    }


    /**
     * Method to fetch Region ids under a company
     * */
    @SuppressWarnings ( "unchecked")
    @Override
    public List<Region> getRegionsForCompany( long companyId, int start, int batch ) throws InvalidInputException
    {
        if ( companyId <= 0 )
            throw new InvalidInputException( "Invalid company id passed in getRegionsForCompany method" );
        LOG.debug( "Method to get all regions for a company id : " + companyId + ",getRegionsForCompany() started." );
        Criteria criteria = getSession().createCriteria( Region.class );
        criteria.add( Restrictions.eq( CommonConstants.COMPANY_COLUMN, companyDao.findById( Company.class, companyId ) ) );
        criteria.add( Restrictions.eq( CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.NO ) );
        criteria.add( Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ) );
        criteria.addOrder( Order.asc( "region" ) );
        if ( start > 0 )
            criteria.setFirstResult( start );
        if ( batch > 0 )
            criteria.setFetchSize( batch );

        List<Region> regionList = criteria.list();
        Set<Long> regionIdList = new LinkedHashSet<Long>();
        Map<Long, Region> regionIdObjMap = new TreeMap<>();
        if ( regionList != null && regionList.size() > 0 ) {
            for ( Region region : regionList ) {
                regionIdList.add( region.getRegionId() );
                regionIdObjMap.put( region.getRegionId(), region );
            }
        }

        List<Region> finalRegionList = new ArrayList<>();
        if ( regionIdList.size() > 0 ) {
            LOG.debug( "Fetching region settings for the fetched regions" );
            List<OrganizationUnitSettings> regionSettingList = organizationUnitSettingsDao
                .fetchOrganizationUnitSettingsForMultipleIds( regionIdList,
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
            LOG.debug( "Adding address, state city and zipcode info into regions" );
            if ( regionSettingList != null && regionSettingList.size() > 0 ) {
                for ( OrganizationUnitSettings regionSettings : regionSettingList ) {
                    if ( regionIdObjMap.get( regionSettings.getIden() ) != null ) {
                        Region region = regionIdObjMap.get( regionSettings.getIden() );
                        region.setAddress1( regionSettings.getContact_details().getAddress1() );
                        region.setAddress2( regionSettings.getContact_details().getAddress2() );
                        region.setCountry( regionSettings.getContact_details().getCountry() );
                        region.setCountryCode( regionSettings.getContact_details().getCountryCode() );
                        region.setCity( regionSettings.getContact_details().getCity() );
                        region.setState( regionSettings.getContact_details().getState() );
                        region.setZipcode( regionSettings.getContact_details().getZipcode() );

                        finalRegionList.add( region );
                    }
                }
            }
        }
        LOG.debug( "Method to get all regions for a company id : " + companyId + ",getRegionsForCompany() finished." );
        if ( finalRegionList.size() > 0 )
            return finalRegionList;
        else
            return regionList;

    }


    /**
     * Method to fetch all region ids under company
     * @param companyId
     * @throws InvalidInputException
     * */
    @SuppressWarnings ( "unchecked")
    @Override
    @Transactional
    public List<Long> getRegionIdsUnderCompany( long companyId, int start, int batchSize ) throws InvalidInputException
    {
        if ( companyId <= 0 ) {
            throw new InvalidInputException( "Invalid company id passed in getRegionIdsUnderCompany method" );
        }
        LOG.info( "Method to get all region ids under company id : " + companyId + ",getRegionIdsUnderCompany() started." );
        
        List<Long> regionIds = new ArrayList<>();
        regionIds = getRegionIdList(CommonConstants.COMPANY_ID_COLUMN , companyId , start , batchSize , CommonConstants.NO , true);
        
        /* Criteria criteria = null;
        try {
            criteria = getSession().createCriteria( Region.class );
            criteria.setProjection( Projections.property( CommonConstants.REGION_ID_COLUMN ).as(
                CommonConstants.REGION_ID_COLUMN ) );
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_COLUMN, companyDao.findById( Company.class, companyId ) ) );
            criteria.add( Restrictions.eq( CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.NO ) );
            criteria.add( Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ) );
            if ( start > 0 )
                criteria.setFirstResult( start );
            if ( batchSize > 0 )
                criteria.setMaxResults( batchSize );
        } catch ( HibernateException e ) {
            LOG.error( "HibernateException caught in getRegionIdsUnderCompany(). Reason: " + e.getMessage(), e );
            throw new DatabaseException( "HibernateException caught in getRegionIdsUnderCompany().", e );
        }*/
        LOG.info( "Method to get all region ids under company id : " + companyId + ",getRegionIdsUnderCompany() ended." );
        return regionIds;
    }
    
    /**
     * Method to get Region Ids of a company
     * @param companyId
     * @return
     * @throws InvalidInputException
     */
    @SuppressWarnings ( "unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<Long> getRegionIdsOfCompany( long companyId ) throws InvalidInputException
    {
        if ( companyId <= 0 ) {
            throw new InvalidInputException( "Invalid company id passed in getRegionIdsUnderCompany method" );
        }
        LOG.info( "Method to get all region ids under company id : " + companyId + ",getRegionIdsUnderCompany() started." );
        
        List<Long> regionIds = new ArrayList<>();
        regionIds = getRegionIdList(CommonConstants.COMPANY_ID_COLUMN , companyId , 0 , 0 , CommonConstants.NO , true);
        
        /*Criteria criteria = null;
        try {
            criteria = getSession().createCriteria( Region.class );
            criteria.setProjection( Projections.property( CommonConstants.REGION_ID_COLUMN ).as(
                CommonConstants.REGION_ID_COLUMN ) );
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_COLUMN, companyDao.findById( Company.class, companyId ) ) );
            criteria.add( Restrictions.eq( CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.NO ) );
            criteria.add( Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ) );
        } catch ( HibernateException e ) {
            LOG.error( "HibernateException caught in getRegionIdsUnderCompany(). Reason: " + e.getMessage(), e );
            throw new DatabaseException( "HibernateException caught in getRegionIdsUnderCompany().", e );
        }*/
        LOG.info( "Method to get all region ids under company id : " + companyId + ",getRegionIdsUnderCompany() ended." );
        return regionIds;
    }
    
    /**
     * Method to get Region Ids of a company
     * @param companyId
     * @return
     * @throws InvalidInputException
     */
    @SuppressWarnings ( "unchecked")
    @Override
    @Transactional
    public List<Long> getRegionIdsUnderCompany( long companyId ) throws InvalidInputException
    {
        if ( companyId <= 0 ) {
            throw new InvalidInputException( "Invalid company id passed in getRegionIdsUnderCompany method" );
        }
        LOG.info( "Method to get all region ids under company id : " + companyId + ",getRegionIdsUnderCompany() started." );
        Criteria criteria = null;
        try {
            criteria = getSession().createCriteria( Region.class );
            criteria.setProjection( Projections.property( CommonConstants.REGION_ID_COLUMN ).as(
                CommonConstants.REGION_ID_COLUMN ) );
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_COLUMN, companyDao.findById( Company.class, companyId ) ) );
            criteria.add( Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ) );
        } catch ( HibernateException e ) {
            LOG.error( "HibernateException caught in getRegionIdsUnderCompany(). Reason: " + e.getMessage(), e );
            throw new DatabaseException( "HibernateException caught in getRegionIdsUnderCompany().", e );
        }
        LOG.info( "Method to get all region ids under company id : " + companyId + ",getRegionIdsUnderCompany() ended." );
        return criteria.list();
    }
    

    @SuppressWarnings ( "unchecked")
    @Override
    public Map<Long, Long> getCompanyIdsForRegionIds( List<Long> regionIds )
    {
        LOG.debug( "Inside method getCompanyIdsForRegionIds {}", regionIds );
        if ( regionIds.isEmpty() ) {
            return Collections.emptyMap();
        }

        try {
            Query query = getSession()
                .createQuery( "select r.regionId, r.company.companyId from Region r where r.regionId in (:ids)" );
            query.setParameterList( "ids", regionIds );

            Map<Long, Long> regionCompanyIdsMap = new HashMap<>();

            List<Object> result = (List<Object>) query.list();
            Iterator<Object> itr = result.iterator();

            while ( itr.hasNext() ) {
                Object[] obj = (Object[]) itr.next();
                Long regionId = (Long) obj[0];
                Long companyId = (Long) obj[1];
                regionCompanyIdsMap.put( regionId, companyId );
                LOG.trace( "regionID {} and company Id {}", regionId, companyId );
            }
            return regionCompanyIdsMap;
        } catch ( HibernateException e ) {
            LOG.error( "HibernateException caught in getRegionIdsUnderCompany(). Reason: " + e.getMessage(), e );
            throw new DatabaseException( "HibernateException caught in getRegionIdsUnderCompany().", e );
        }
    }
    
    //check if REGION is default 
    @Override
	public long checkIfRegionIsDefault(long regionId) {
		LOG.debug( "Method to check if region is default checkIfRegionIsDefault() started." );
		Query query = getSession().createSQLQuery( "SELECT region_id FROM REGION WHERE region_id = :regionId AND IS_DEFAULT_BY_SYSTEM = 0 " );
        query.setParameter( "regionId", regionId  );
        LOG.debug( "Method to check if region is default checkIfRegionIsDefault() finished." );
        return  (int) query.uniqueResult();
	}
    

    public List<Long> getRegionIdList( String entityType, long entityId )
    {
        return getRegionIdList(entityType , entityId , 0 , 0 , 0 , false);
    }
    
    @SuppressWarnings ( "unchecked")
    private List<Long> getRegionIdList(String entityType , long entityId , int start , int batchSize , int isDefault , boolean useDefault)
    {
        LOG.info( "Getting all regionIds under {}: {}", entityType, entityId );
        Criteria criteria = null;
        
        try {
            criteria = getSession().createCriteria( Region.class );
            criteria.setProjection( Projections.property( CommonConstants.REGION_ID_COLUMN ).as(
                CommonConstants.REGION_ID_COLUMN ) );
            if(entityType.equals(CommonConstants.COMPANY_ID_COLUMN)) {
                criteria.add( Restrictions.eq( CommonConstants.COMPANY_COLUMN, companyDao.findById( Company.class, entityId ) ) );
            }
            else {
                return null;
            }
            
            if ( start > 0 )
                criteria.setFirstResult( start );
            if ( batchSize > 0 )
                criteria.setMaxResults( batchSize );
            
            if(useDefault) {
                criteria.add( Restrictions.eq( CommonConstants.IS_DEFAULT_BY_SYSTEM, isDefault ) ); 
            }
            
            criteria.add( Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ) );
        } catch ( HibernateException e ) {
            throw new DatabaseException( "HibernateException caught in getRegionIdList", e );
        }
        return criteria.list();
    }
}
// JIRA SS-42 By RM-05 EOC
