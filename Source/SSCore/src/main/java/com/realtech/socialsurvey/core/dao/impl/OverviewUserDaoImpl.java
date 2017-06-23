package com.realtech.socialsurvey.core.dao.impl;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.dao.OverviewUserDao;
import com.realtech.socialsurvey.core.entities.OverviewUser;

@Component
public class OverviewUserDaoImpl extends GenericDaoImpl<OverviewUser, String>implements OverviewUserDao
{

    private static final Logger LOG = LoggerFactory.getLogger(OverviewUserDaoImpl.class);

    @Override
    @Transactional
    public String getOverviewUserId( Long userId )
    {
        LOG.info("Method to get OverviewUserId from UserId, getOverviewUserId() started." );

        Query query = getSession().createSQLQuery( "SELECT overview_user_id FROM overview_user WHERE user_id = :userId " );
        query.setParameter( "userId", userId  );
        String OverviewUserId = (String) query.uniqueResult();
        
        LOG.info( "Method to get OverviewUserId from UserId, getOverviewUserId() finished." );
        return OverviewUserId;
        
        
    }
    @Override
    @Transactional
    public OverviewUser findOverviewUser( Class<OverviewUser> entityClass, String overviewUserId )
    {
        return super.findById( entityClass, overviewUserId );
    }
}
