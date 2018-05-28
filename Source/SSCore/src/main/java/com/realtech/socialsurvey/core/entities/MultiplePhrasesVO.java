package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.util.List;


public class MultiplePhrasesVO implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private List<String> phrases;
    private MonitorType monitorType;


    public List<String> getPhrases()
    {
        return phrases;
    }


    public void setPhrases( List<String> phrases )
    {
        this.phrases = phrases;
    }


    public MonitorType getMonitorType()
    {
        return monitorType;
    }


    public void setMonitorType( MonitorType monitorType )
    {
        this.monitorType = monitorType;
    }


    @Override
    public String toString()
    {
        return "MultiplePhrasesVO [phrases=" + phrases + ", monitorType=" + monitorType + "]";
    }


}
