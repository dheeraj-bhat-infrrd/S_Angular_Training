package com.realtech.socialsurvey.core.vo;

import java.util.List;

import com.realtech.socialsurvey.core.entities.CompanySurveyStatusStats;
import com.realtech.socialsurvey.core.entities.EntityAlertDetails;

public class TransactionMonitorGraphDataVO
{
    private long companyId;
    
    private String companyName;
    
    private EntityAlertDetails entityAlertDetails;
    
    private List<CompanySurveyStatusStats> companySurveyStatusStatslist;

    public long getCompanyId()
    {
        return companyId;
    }

    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }

    public String getCompanyName()
    {
        return companyName;
    }

    public void setCompanyName( String companyName )
    {
        this.companyName = companyName;
    }

    public EntityAlertDetails getEntityAlertDetails()
    {
        return entityAlertDetails;
    }

    public void setEntityAlertDetails( EntityAlertDetails entityAlertDetails )
    {
        this.entityAlertDetails = entityAlertDetails;
    }
    
    public List<CompanySurveyStatusStats> getCompanySurveyStatusStatslist()
    {
        return companySurveyStatusStatslist;
    }

    public void setCompanySurveyStatusStatslist( List<CompanySurveyStatusStats> companySurveyStatusStatslist )
    {
        this.companySurveyStatusStatslist = companySurveyStatusStatslist;
    }

}
