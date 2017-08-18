package com.realtech.socialsurvey.core.enums;

public enum HierarchyType
{

    COMPANY( "Company" ), REGION( "Region" ), BRANCH( "Branch" ), USER( "User" );

    private String value;


    private HierarchyType( String value )
    {
        this.value = value;
    }


    public String getValue()
    {
        return value;
    }

}
