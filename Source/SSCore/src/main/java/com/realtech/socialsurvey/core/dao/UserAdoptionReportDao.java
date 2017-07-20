package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.UserAdoptionReport;

public interface UserAdoptionReportDao extends GenericReportingDao<UserAdoptionReport, String>
{

    List<UserAdoptionReport> fetchUserAdoptionById( Long entityId, String entityType );

}
