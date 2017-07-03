package com.realtech.socialsurvey.core.dao.impl;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.dao.OverviewRegionDao;
import com.realtech.socialsurvey.core.entities.OverviewRegion;

@Component
public class OverviewRegionDaoImpl extends GenericReportingDaoImpl<OverviewRegion, String> implements OverviewRegionDao
{

    private static final Logger LOG = LoggerFactory.getLogger(OverviewRegionDaoImpl.class);

    @Override
    public String getOverviewRegionId( Long regionId )
    {
        LOG.info("Method to get OverviewRegionId from RegionId, getOverviewRegionId() started." );

        Query query = getSession().createSQLQuery( "SELECT overview_region_id FROM overview_region WHERE region_id = :regionId " );
        query.setParameter( "regionId", regionId  );
        String OverviewRegionId = (String) query.uniqueResult();
        
        LOG.info( "Method to get OverviewRegionId from RegionId, getOverviewRegionId() finished." );
        return OverviewRegionId;
    }

    @Override
    public OverviewRegion findOverviewRegion( Class<OverviewRegion> entityClass, String overviewRegionId )throws IllegalArgumentException
    {
        return super.findById( entityClass, overviewRegionId );
    }

  
}
