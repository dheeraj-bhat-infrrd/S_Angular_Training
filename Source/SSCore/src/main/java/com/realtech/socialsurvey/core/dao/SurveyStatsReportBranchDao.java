package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.SurveyStatsReportBranch;

public interface SurveyStatsReportBranchDao extends GenericReportingDao<SurveyStatsReportBranch, String>
{

    List<SurveyStatsReportBranch> fetchSurveyStatsById( Long entityId, String entityType );

    List<SurveyStatsReportBranch> fetchBranchSurveyStatsById( Long branchId, String startTrxMonth, String endTrxMonth );

    SurveyStatsReportBranch fetchBranchSurveyStats( long branchId, int month, int year );

}
