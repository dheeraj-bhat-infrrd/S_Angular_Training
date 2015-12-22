package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;
import java.util.Set;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.RegionDao;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

@Component("region")
public class RegionDaoImpl extends GenericDaoImpl<Region, Long> implements RegionDao {

	private static final Logger LOG = LoggerFactory.getLogger(RegionDaoImpl.class);

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
    public List<Long> getRegionIdsForCompanyId( long companyId ) throws InvalidInputException
    {
        if ( companyId <= 0 )
            throw new InvalidInputException( "Invalid company id passed in getRegionIdsForCompanyId method" );
        LOG.info( "Method to get all the regions ids for a company id : " + companyId + ",getRegionIdsForCompanyId() started." );
        Criteria criteria = getSession().createCriteria( Region.class );
        criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
        LOG.info( "Method to get all the regions ids for a company id : " + companyId + ",getRegionIdsForCompanyId() finished." );
        return criteria.list();
    }
}
// JIRA SS-42 By RM-05 EOC
