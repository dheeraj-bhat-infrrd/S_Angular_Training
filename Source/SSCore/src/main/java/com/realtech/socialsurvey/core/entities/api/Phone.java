package com.realtech.socialsurvey.core.entities.api;

/**
 * @author Shipra Goyal, RareMile
 *
 */
public class Phone
{
    private String countryCode;
    private String number;
    private String extension;


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
