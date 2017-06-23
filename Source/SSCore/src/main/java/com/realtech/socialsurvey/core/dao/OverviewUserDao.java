package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.OverviewUser;

public interface OverviewUserDao extends GenericDao<OverviewUser, String>
{
    public String getOverviewUserId( Long id );
    
    public OverviewUser findOverviewUser( Class<OverviewUser> entityClass, String id );



}
