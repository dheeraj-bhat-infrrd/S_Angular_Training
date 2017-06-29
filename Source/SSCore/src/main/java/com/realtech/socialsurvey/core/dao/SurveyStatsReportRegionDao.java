package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.SurveyStatsReportRegion;

public interface SurveyStatsReportRegionDao extends GenericReportingDao<SurveyStatsReportRegion, String>
{

    List<SurveyStatsReportRegion> fetchRegionSurveyStatsById( Long regionId );

}
