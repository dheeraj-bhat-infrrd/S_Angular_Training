package com.realtech.socialsurvey.core.entities;

public class SearchedUser
{
    private long userId;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private int status;
    /**
     * @return the userId
     */
    public long getUserId()
    {
        return userId;
    }
    /**
     * @param userId the userId to set
     */
    public void setUserId( long userId )
    {
        this.userId = userId;
    }
    /**
     * @return the firstName
     */
    public String getFirstName()
    {
        return firstName;
    }
    /**
     * @param firstName the firstName to set
     */
    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }
    /**
     * @return the lastName
     */
    public String getLastName()
    {
        return lastName;
    }
    /**
     * @param lastName the lastName to set
     */
    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }
    /**
     * @return the emailAddress
     */
    public String getEmailAddress()
    {
        return emailAddress;
    }
    /**
     * @param emailAddress the emailAddress to set
     */
    public void setEmailAddress( String emailAddress )
    {
        this.emailAddress = emailAddress;
    }
    /**
     * @return the status
     */
    public int getStatus()
    {
        return status;
    }
    /**
     * @param status the status to set
     */
    public void setStatus( int status )
    {
        this.status = status;
    }
}
