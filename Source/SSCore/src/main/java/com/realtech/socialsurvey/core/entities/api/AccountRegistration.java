package com.realtech.socialsurvey.core.entities.api;

/**
 * @author Shipra Goyal, RareMile
 *
 */
public class AccountRegistration
{
    private String firstName;
    private String lastName;
    private String companyName;
    private String email;
    private Phone phone;
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


    public String getFirstName()
    {
        return firstName;
    }


    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }


    public String getLastName()
    {
        return lastName;
    }


    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }


    public String getCompanyName()
    {
        return companyName;
    }


    public void setCompanyName( String companyName )
    {
        this.companyName = companyName;
    }


    public String getEmail()
    {
        return email;
    }


    public void setEmail( String email )
    {
        this.email = email;
    }


    public Phone getPhone()
    {
        return phone;
    }


    public void setPhone( Phone phone )
    {
        this.phone = phone;
    }
}
