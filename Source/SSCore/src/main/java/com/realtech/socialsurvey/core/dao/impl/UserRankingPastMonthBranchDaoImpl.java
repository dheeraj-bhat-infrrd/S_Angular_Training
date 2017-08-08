package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.UserRankingPastMonthBranchDao;
import com.realtech.socialsurvey.core.entities.UserRankingPastMonthBranch;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingPastMonthBranchDaoImpl extends GenericReportingDaoImpl<UserRankingPastMonthBranch, String> implements UserRankingPastMonthBranchDao{

	private static final Logger LOG = LoggerFactory.getLogger( UserRankingPastMonthBranchDaoImpl.class );
	
	@Override
	public List<UserRankingPastMonthBranch> fetchUserRankingForPastMonthBranch(Long branchId, int month, int year) {
		LOG.info( "method to fetch user ranking branch list for past month, fetchUserRankingForPastMonthBranch() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastMonthBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_MONTH, month ) ); 
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_YEAR, year ) );            
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingForPastMonthBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingForPastMonthBranch() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking branch list for past month, fetchUserRankingForPastMonthBranch() finished." );
        return (List<UserRankingPastMonthBranch>) criteria.list();
	}

}
