package com.realtech.socialsurvey.core.vo;

public class EncompassAlertMailsVO
{
    private Long companyId;
    private String alertMails;
    
    public Long getCompanyId()
    {
        return companyId;
    }
    
    public void setCompanyId( Long companyId )
    {
        this.companyId = companyId;
    }
    
    public String getAlertMails()
    {
        return alertMails;
    }
    
    public void setAlertMails( String alertMails )
    {
        this.alertMails = alertMails;
    }

    @Override
    public String toString()
    {
        return "EncompassAlertMailsVO [companyId=" + companyId + ", alertMails=" + alertMails + "]";
    }
    
}
