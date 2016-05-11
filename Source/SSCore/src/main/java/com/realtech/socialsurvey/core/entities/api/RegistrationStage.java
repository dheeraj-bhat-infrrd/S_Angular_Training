package com.realtech.socialsurvey.core.entities.api;

public enum RegistrationStage
{
    INITIATE_REGISTRATION( "INIT", "Initiated Account Registration" );

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
