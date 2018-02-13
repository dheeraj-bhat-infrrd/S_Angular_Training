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
import com.realtech.socialsurvey.core.dao.BranchRankingReportMonthDao;
import com.realtech.socialsurvey.core.entities.BranchRankingReportMonth;

/**
 * @author Subhrajit
 *
 */
@Repository
public class BranchRankingReportMonthDaoImpl extends GenericReportingDaoImpl<BranchRankingReportMonth, String> implements BranchRankingReportMonthDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<BranchRankingReportMonth> getBranchRankingForMonth(long companyId, int month, int year) {
		Criteria criteria = getSession().createCriteria(BranchRankingReportMonth.class);
		criteria.add(Restrictions.eq(CommonConstants.COMPANY_ID_COLUMN, companyId));
		criteria.add(Restrictions.eq(CommonConstants.AGGREGATE_BY_MONTH, month));
		criteria.add(Restrictions.eq(CommonConstants.AGGREGATE_BY_YEAR, year));
		criteria.addOrder(Order.asc("rankInCompany"));
		
		return (List<BranchRankingReportMonth>)criteria.list();
		

	}

}
