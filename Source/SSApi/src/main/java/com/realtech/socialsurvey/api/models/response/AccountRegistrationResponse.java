package com.realtech.socialsurvey.api.models.response;

public class AccountRegistrationResponse
{
    private int userId;
    private int companyId;


    public int getUserId()
    {
        return userId;
    }


    public void setUserId( int userId )
    {
        this.userId = userId;
    }


    public int getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( int companyId )
    {
        this.companyId = companyId;
    }
}
