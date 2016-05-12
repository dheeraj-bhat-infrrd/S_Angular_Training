package com.realtech.socialsurvey.core.entities;

import java.util.List;

import com.realtech.socialsurvey.core.entities.api.Phone;


/**
 * Settings object for contact numbers of a profile
 */
public class ContactNumberSettings
{

    private String work;
    private String personal;
    private String fax;
    private List<MiscValues> others;
    private Phone phone1;
    private Phone phone2;


    public Phone getPhone1()
    {
        return phone1;
    }


    public void setPhone1( Phone phone1 )
    {
        this.phone1 = phone1;
    }


    public Phone getPhone2()
    {
        return phone2;
    }


    public void setPhone2( Phone phone2 )
    {
        this.phone2 = phone2;
    }


    public String getWork()
    {
        return work;
    }


    public void setWork( String work )
    {
        this.work = work;
    }


    public String getPersonal()
    {
        return personal;
    }


    public void setPersonal( String personal )
    {
        this.personal = personal;
    }


    public String getFax()
    {
        return fax;
    }


    public void setFax( String fax )
    {
        this.fax = fax;
    }


    public List<MiscValues> getOthers()
    {
        return others;
    }


    public void setOthers( List<MiscValues> others )
    {
        this.others = others;
    }


    @Override
    public String toString()
    {
        return "work: " + work + "\t personal: " + personal + "\t others: " + ( others != null ? others.toString() : "null" );
    }

}
