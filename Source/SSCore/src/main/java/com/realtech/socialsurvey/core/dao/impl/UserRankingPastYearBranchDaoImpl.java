package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.UserRankingPastYearBranchDao;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearBranch;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingPastYearBranchDaoImpl extends GenericReportingDaoImpl<UserRankingPastYearBranch, String> implements UserRankingPastYearBranchDao{
	
	private static final Logger LOG = LoggerFactory.getLogger( UserRankingPastYearBranchDaoImpl.class );

	@Override
	public List<UserRankingPastYearBranch> fetchUserRankingForPastYearBranch(Long branchId, int year) {
		LOG.info( "method to fetch user ranking branch list for past year, fetchUserRankingForPastYearBranch() started" );
        Criteria criteria = getSession().createCriteria( UserRankingPastYearBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
            criteria.add( Restrictions.eq( CommonConstants.LEADERBOARD_YEAR, year ) );            
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingForPastYearBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingForPastYearBranch() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking branch list for past year, fetchUserRankingForPastYearBranch() finished." );
        return (List<UserRankingPastYearBranch>) criteria.list();
	}

}
