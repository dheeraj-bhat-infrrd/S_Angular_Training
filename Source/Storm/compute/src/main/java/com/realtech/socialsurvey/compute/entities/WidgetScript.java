package com.realtech.socialsurvey.compute.entities;

public class WidgetScript
{
    private String profileName;
    private String name;
    private String scriptPAF;
    private String scriptCc;
    private String scriptJi;
    
    public String getProfileName()
    {
        return profileName;
    }
    public void setProfileName( String profileName )
    {
        this.profileName = profileName;
    }
    public String getName()
    {
        return name;
    }
    public void setName( String name )
    {
        this.name = name;
    }
    public String getScriptPAF()
    {
        return scriptPAF;
    }
    public void setScriptPAF( String scriptPAF )
    {
        this.scriptPAF = scriptPAF;
    }
    public String getScriptCc()
    {
        return scriptCc;
    }
    public void setScriptCc( String scriptCc )
    {
        this.scriptCc = scriptCc;
    }
    public String getScriptJi()
    {
        return scriptJi;
    }
    public void setScriptJi( String scriptJi )
    {
        this.scriptJi = scriptJi;
    }
    @Override
    public String toString()
    {
        return "WidgetScript [profileName=" + profileName + ", name=" + name + ", scriptPAF=" + scriptPAF + ", scriptCc="
            + scriptCc + ", scriptJi=" + scriptJi + "]";
    }

}
