package com.realtech.socialsurvey.core.vo;

import java.io.Serializable;


/**
 * @author Lavanya
 */


/**
 * This class is used as a VO for user
 */
public class UserVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private long userId;
    private String fullName;
    private String emailId;

    public UserVo()
    {
    }

    public long getUserId()
    {
        return userId;
    }


    public void setUserId( long userId )
    {
        this.userId = userId;
    }
    

    public String getFullName()
    {
        return fullName;
    }


    public void setFullName( String fullName )
    {
        this.fullName = fullName;
    }


    public String getEmailId()
    {
        return emailId;
    }


    public void setEmailId( String emailId )
    {
        this.emailId = emailId;
    }

    @Override public String toString()
    {
        return "UserVo{" + "userId=" + userId + ", fullName='" + fullName + '\'' + ", emailId='" + emailId + '\'' + '}';
    }
}
