package com.realtech.socialsurvey.core.services.reportingmanagement;

import java.util.List;

import com.realtech.socialsurvey.core.entities.OverviewUser;

public interface OverviewManagement
{
    public OverviewUser fetchOverviewDetails(long entityId , String entityType);
}
