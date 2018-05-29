package com.realtech.socialsurvey.compute.enums;

/**
 * @author manish
 *
 */
public enum ProfileType
{
    COMPANY( "COMPANY_SETTINGS" ), REGION( "REGION_SETTINGS" ), BRANCH( "BRANCH_SETTINGS" ), AGENT( "AGENT_SETTINGS" );

    private String value;


    ProfileType( String value )
    {
        this.value = value;
    }


    public String getValue()
    {
        return value;
    }
}
