package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.UserRankingThisYearBranchDao;
import com.realtech.socialsurvey.core.entities.UserRankingThisYearBranch;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserRankingThisYearBranchDaoImpl extends GenericReportingDaoImpl<UserRankingThisYearBranch, String> implements UserRankingThisYearBranchDao{

	private static final Logger LOG = LoggerFactory.getLogger( UserRankingThisYearBranchDaoImpl.class );
	
	@Override
	public List<UserRankingThisYearBranch> fetchUserRankingForThisYearBranch(Long branchId, int year) {
		LOG.info( "method to fetch user ranking branch list for this year, fetchUserRankingForThisYearBranch() started" );
        Criteria criteria = getSession().createCriteria( UserRankingThisYearBranch.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
            criteria.add( Restrictions.eq( CommonConstants.THIS_YEAR, year ) );            
            }
        catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserRankingForThisYearBranch() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserRankingForThisYearBranch() ", hibernateException );
        }

        LOG.info( "method to fetch user ranking branch list for this year, fetchUserRankingForThisYearBranch() finished." );
        return (List<UserRankingThisYearBranch>) criteria.list();
	}
	
}
