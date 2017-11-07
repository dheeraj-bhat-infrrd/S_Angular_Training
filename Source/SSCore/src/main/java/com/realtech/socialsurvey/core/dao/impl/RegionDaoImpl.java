package com.realtech.socialsurvey.core.dao.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
        Criteria criteria = null;
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
        }
        LOG.info( "Method to get all region ids under company id : " + companyId + ",getRegionIdsUnderCompany() ended." );
        return criteria.list();
    }

}
// JIRA SS-42 By RM-05 EOC
