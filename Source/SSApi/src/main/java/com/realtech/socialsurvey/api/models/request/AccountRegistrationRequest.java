package com.realtech.socialsurvey.api.models.request;

import com.realtech.socialsurvey.api.models.Phone;


public class AccountRegistrationRequest
{
    private String firstName;
    private String lastName;
    private String companyName;
    private String email;
    private Phone phone;


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
