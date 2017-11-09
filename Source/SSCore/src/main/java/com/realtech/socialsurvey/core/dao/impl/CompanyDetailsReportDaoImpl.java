/**
 * 
 */
package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.CompanyDetailsReportDao;
import com.realtech.socialsurvey.core.entities.CompanyDetailsReport;

/**
 * @author Subhrajit
 *
 */
@Repository
public class CompanyDetailsReportDaoImpl extends GenericReportingDaoImpl<CompanyDetailsReport, String>
		implements CompanyDetailsReportDao {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.realtech.socialsurvey.core.dao.CompanyDetailsReportDao#
	 * getCompanyDetailsReportData(int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CompanyDetailsReport> getCompanyDetailsReportData(int startIndex, int batchSize) {
		Criteria criteria = getSession().createCriteria(CompanyDetailsReport.class);
		criteria.setFirstResult(startIndex);
		criteria.setMaxResults(batchSize);
		//criteria.addOrder(Order.asc(CommonConstants.COMPANY_COLUMN));
		List<CompanyDetailsReport> companyDetailsReportData =criteria.list(); 
		return companyDetailsReportData;
	}
}