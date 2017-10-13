package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.Digest;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


public interface DigestDao extends GenericReportingDao<Digest, String>
{

    List<Digest> fetchDigestDataForNMonthsInAYear( long companyId, int startMonth, int endMonth, int year )
        throws InvalidInputException;
}
