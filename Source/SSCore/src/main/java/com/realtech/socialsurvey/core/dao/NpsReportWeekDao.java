package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.NpsReportWeek;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

public interface NpsReportWeekDao extends GenericReportingDao<NpsReportWeek, String>
{
    List<NpsReportWeek> fetchNpsReportWeek(long companyId, int week, int year)throws InvalidInputException;
}
