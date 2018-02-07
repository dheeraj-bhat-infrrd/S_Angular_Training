/**
 * 
 */
package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.BranchRankingReportMonth;

/**
 * @author Subhrajit
 *
 */
public interface BranchRankingReportMonthDao extends GenericReportingDao<BranchRankingReportMonth, String> {

	List<BranchRankingReportMonth> getBranchRankingForMonth(long companyId, int month, int year);

}
