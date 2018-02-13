/**
 * 
 */
package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.BranchRankingReportYear;

/**
 * @author Subhrajit
 *
 */
public interface BranchRankingReportYearDao extends GenericReportingDao<BranchRankingReportYear, String> {

	List<BranchRankingReportYear> getBranchRankingForYear(long companyId, int year);

}
