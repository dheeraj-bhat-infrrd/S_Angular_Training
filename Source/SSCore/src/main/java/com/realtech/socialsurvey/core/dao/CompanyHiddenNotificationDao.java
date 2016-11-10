package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.CompanyHiddenNotification;


public interface CompanyHiddenNotificationDao extends GenericDao<CompanyHiddenNotification, Long>
{
    public void processByIdAndStatus( CompanyHiddenNotification companyHiddenNotification, int status );
}
