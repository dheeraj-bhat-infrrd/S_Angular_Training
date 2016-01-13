package com.realtech.socialsurvey.core.entities;

public class EncompassCrmInfo extends CRMInfo
{

    private String crm_username;
    private String crm_password;
    private String url;
    private String crm_fieldId;


    public String getCrm_username()
    {
        return crm_username;
    }


    public void setCrm_username( String crm_username )
    {
        this.crm_username = crm_username;
    }


    public String getCrm_password()
    {
        return crm_password;
    }


    public void setCrm_password( String crm_password )
    {
        this.crm_password = crm_password;
    }


    public String getUrl()
    {
        return url;
    }


    public void setUrl( String url )
    {
        this.url = url;
    }


    public String getCrm_fieldId()
    {
        return crm_fieldId;
    }


    public void setCrm_fieldId( String crm_fieldId )
    {
        this.crm_fieldId = crm_fieldId;
    }


    @Override
    public String toString()
    {
        return "crm_username: " + crm_username;
    }

}