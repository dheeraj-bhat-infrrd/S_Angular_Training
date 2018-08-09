package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.UserRankingPastYearsBranchDao;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearBranch;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearsBranch;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingPastYearsBranchDaoImpl extends GenericReportingDaoImpl<UserRankingPastYearsBranch, String> implements UserRankingPastYearsBranchDao
{
    private static final Logger LOG = LoggerFactory.getLogger( UserRankingPastYearsBranchDaoImpl.class );

    @Override
    public List<UserRankingPastYearsBranch> fetchUserRankingForPastYearsBranch(Long branchId , int startIndex , int batchSize) {
        LOG.info( "method to fetch user ranking branch list for past years, fetchUserRankingForPastYearsBranch() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastYearsBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
            if ( startIndex > -1 ) {
                criteria.setFirstResult( startIndex );
            }
            if ( batchSize > -1 ) {
                criteria.setMaxResults( batchSize );
            }
            criteria.addOrder( Order.asc( CommonConstants.RANK ) );
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingForPastYearsBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingForPastYearsBranch() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking branch list for past years, fetchUserRankingForPastYearsBranch() finished." );
        return (List<UserRankingPastYearsBranch>) criteria.list();
    }

}
