package com.realtech.socialsurvey.core.vo;

/**
 * @author manish
 *
 */
public class CompanyNotRegisteredVO
{
    private String workContactNo;
    private String userName;
    private String workMailId;
    private String message;
    
    public String getWorkContactNo()
    {
        return workContactNo;
    }

    public void setWorkContactNo( String workContactNo )
    {
        this.workContactNo = workContactNo;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName( String userName )
    {
        this.userName = userName;
    }

    public String getWorkMailId()
    {
        return workMailId;
    }

    public void setWorkMailId( String workMailId )
    {
        this.workMailId = workMailId;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return "CompanyNotRegisteredVO [workContactNo=" + workContactNo + ", userName=" + userName + ", workMailId="
            + workMailId + ", message=" + message + "]";
    }
    
    
}
