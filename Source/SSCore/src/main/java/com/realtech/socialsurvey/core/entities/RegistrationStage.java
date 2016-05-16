package com.realtech.socialsurvey.core.entities;

public enum RegistrationStage
{
    INITIATE_REGISTRATION( "INIT", "Initiated account registration" ),
    LINKEDIN_SETUP( "LIN", "LinkedIn setup" ),
    USER_PROFESSIONAL_PROFILE( "UPP", "User professional profile" ),
    USER_PROFESSIONAL_DETAILS( "UPD", "User professional details" ),
    USER_COMPANY_PROFILE( "UCP", "User company profile" ),
    USER_COMPANY_DETAILS( "UCD", "User company details" );

    private String code;
    private String description;


    RegistrationStage( String code, String description )
    {
        this.code = code;
        this.description = description;
    }


    public String getCode()
    {
        return code;
    }


    public void setCode( String code )
    {
        this.code = code;
    }


    public String getDescription()
    {
        return description;
    }


    public void setDescription( String description )
    {
        this.description = description;
    }
}
