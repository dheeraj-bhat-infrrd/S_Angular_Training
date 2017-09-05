package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.SurveyTransactionReportRegion;

public interface SurveyTransactionReportRegionDao extends GenericReportingDao<SurveyTransactionReportRegionDao, String>
{

    List<SurveyTransactionReportRegion> fetchSurveyTransactionByRegionId( Long regionId, int startYear, int startMonth,
        int endYear, int endMonth );

}
