package com.realtech.socialsurvey.core.entities;

public class Phone
{
    private String countryCode;
    private String number;
    private String extension;
    private String countryAbbr;
    private String formattedPhoneNumber;


    public String getFormattedPhoneNumber()
    {
        return formattedPhoneNumber;
    }


    public void setFormattedPhoneNumber( String formattedPhoneNumber )
    {
        this.formattedPhoneNumber = formattedPhoneNumber;
    }


    public String getCountryAbbr()
    {
        return countryAbbr;
    }


    public void setCountryAbbr( String countryAbbr )
    {
        this.countryAbbr = countryAbbr;
    }


    public String getCountryCode()
    {
        return countryCode;
    }


    public void setCountryCode( String countryCode )
    {
        this.countryCode = countryCode;
    }


    public String getNumber()
    {
        return number;
    }


    public void setNumber( String number )
    {
        this.number = number;
    }


    public String getExtension()
    {
        return extension;
    }


    public void setExtension( String extension )
    {
        this.extension = extension;
    }
}
