package com.realtech.socialsurvey.core.entities;

public enum RegistrationStage
{
    INITIATE_REGISTRATION( "INIT", "Initiated account registration", 1 ),
    LINKEDIN_SETUP( "LIN", "LinkedIn setup", 2 ),
    USER_PROFESSIONAL_PROFILE( "UPP", "User professional profile", 3 ),
    USER_COMPANY_PROFILE( "UCP", "User company profile", 4 ),
    PAYMENT( "PAY", "Regsitration payment Completed", 5 );

    private String code;
    private String description;
    private int displayOrder;


    RegistrationStage( String code, String description, int displayOrder )
    {
        this.code = code;
        this.description = description;
        this.displayOrder = displayOrder;
    }


    public int getDisplayOrder()
    {
        return displayOrder;
    }


    public void setDisplayOrder( int displayOrder )
    {
        this.displayOrder = displayOrder;
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
