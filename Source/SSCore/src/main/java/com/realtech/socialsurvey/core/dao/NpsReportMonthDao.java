package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.NpsReportMonth;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


public interface NpsReportMonthDao extends GenericReportingDao<NpsReportMonth, String>
{
    List<NpsReportMonth> fetchNpsReportMonth( long companyId, int month, int year ) throws InvalidInputException;
}
