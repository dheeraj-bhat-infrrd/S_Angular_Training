/**
 * 
 */
package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.CompanyDetailsReport;

/**
 * @author Subhrajit
 *
 */
public interface CompanyDetailsReportDao extends GenericReportingDao<CompanyDetailsReport, String> {

	/**
	 * Method to get the company details report data for specific start index
	 * and batch size.
	 * @param startIndex
	 * @param batchSize
	 * @return
	 */
	public List<CompanyDetailsReport> getCompanyDetailsReportData(int startIndex, int batchSize);

}