package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.SurveyStatsReportCompany;
import com.realtech.socialsurvey.core.entities.SurveyStatsReportUser;

public interface SurveyStatsReportUserDao extends GenericReportingDao<SurveyStatsReportUser, String>
{

    List<SurveyStatsReportUser> fetchUserSurveyStatsById( Long userId, String startTrxMonth, String endTrxMonth );
}
