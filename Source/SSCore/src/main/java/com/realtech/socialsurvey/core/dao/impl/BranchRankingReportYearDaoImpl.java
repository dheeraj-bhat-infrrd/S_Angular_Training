/**
 * 
 */
package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.BranchRankingReportYearDao;
import com.realtech.socialsurvey.core.entities.BranchRankingReportYear;

/**
 * @author Subhrajit
 *
 */
@Repository
public class BranchRankingReportYearDaoImpl extends GenericReportingDaoImpl<BranchRankingReportYear, String>
		implements BranchRankingReportYearDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<BranchRankingReportYear> getBranchRankingForYear(long companyId, int year) {
		Criteria criteria = getSession().createCriteria(BranchRankingReportYear.class);
		criteria.add(Restrictions.eq(CommonConstants.COMPANY_ID_COLUMN, companyId));
		criteria.add(Restrictions.eq(CommonConstants.AGGREGATE_BY_YEAR, year));
		criteria.addOrder(Order.asc("rankInCompany"));
		
		return (List<BranchRankingReportYear>)criteria.list();
	}


}
