package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.SurveyTransactionReportBranch;

public interface SurveyTransactionReportBranchDao extends GenericReportingDao<SurveyTransactionReportBranchDao , String>
{

    List<SurveyTransactionReportBranch> fetchSurveyTransactionByBranchId( Long branchId, int startYear, int startMonth,
        int endYear, int endMonth );

}
