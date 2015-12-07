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
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

@Component("branch")
public class BranchDaoImpl extends GenericDaoImpl<Branch, Long> implements BranchDao {

	private static final Logger LOG = LoggerFactory.getLogger(BranchDaoImpl.class);

	/*
	 * Method to delete all the users of a company.
	 */
	@Override
	public void deleteBranchesByCompanyId(long companyId) {
		LOG.info("Method to delete all the branches by company id,deleteBranchesByCompanyId() started.");
		try {
			Query query = getSession().createQuery("delete from Branch where company.companyId=?");
			query.setParameter(0, companyId);
			query.executeUpdate();
		}
		catch (HibernateException hibernateException) {
			LOG.error("Exception caught in deleteBranchesByCompanyId() ", hibernateException);
			throw new DatabaseException("Exception caught in deleteBranchesByCompanyId() ", hibernateException);
		}
		LOG.info("Method to delete all the branches by company id, deleteBranchesByCompanyId() finished.");
	}


    @SuppressWarnings ( "unchecked")
    @Override
    public List<Branch> getBranchForBranchIds( Set<Long> branchIds ) throws InvalidInputException
    {
        if ( branchIds == null || branchIds.isEmpty() )
            throw new InvalidInputException( "Branch ids passed cannot be null or empty" );
        LOG.info( "Method to get all the branches for branches ids,getBranchForBranchIds() started." );
        Criteria criteria = getSession().createCriteria( Branch.class );
        criteria.add( Restrictions.in( CommonConstants.BRANCH_ID_COLUMN, branchIds ) );
        LOG.info( "Method to get all the branches for branches ids, getBranchForBranchIds() finished." );
        return criteria.list();
    }
}
// JIRA SS-42 By RM-05 EOC
