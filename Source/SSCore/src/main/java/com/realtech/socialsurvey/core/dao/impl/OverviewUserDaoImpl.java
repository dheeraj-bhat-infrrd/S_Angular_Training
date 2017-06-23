package com.realtech.socialsurvey.core.dao.impl;

import java.math.BigInteger;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OverviewUserDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OverviewUser;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.reportingmanagement.OverviewManagement;

@Component
public class OverviewUserDaoImpl extends GenericDaoImpl<OverviewUser, String>implements OverviewUserDao
{

    private static final Logger LOG = LoggerFactory.getLogger(BranchDaoImpl.class);

    @Override
    @Transactional
    public String getOverviewUserId( Long id )
    {
        LOG.info("Method to get OverviewUserId from UserId, getOverviewUserId() started." );

        Query query = getSession().createSQLQuery( "SELECT overview_user_id FROM overview_user WHERE user_id = :userId " );
        query.setParameter( "userId", id  );
        String OverviewUserId = (String) query.uniqueResult();
        
        LOG.info(
            "Method to get OverviewUserId from UserId, getOverviewUserId() finished." );
        return OverviewUserId;
        
        
    }
    @Override
    @Transactional
    public OverviewUser findOverviewUser( Class<OverviewUser> entityClass, String id )
    {
        // TODO Auto-generated method stub
        return super.findById( entityClass, id );
    }
}
