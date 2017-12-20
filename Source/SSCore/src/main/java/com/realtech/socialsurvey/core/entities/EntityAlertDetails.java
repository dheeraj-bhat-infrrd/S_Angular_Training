package com.realtech.socialsurvey.core.entities;

import java.util.List;

public class EntityAlertDetails
{
    private boolean isErrorAlert;
    
    private boolean isWarningAlert;

    private List<String> currentErrorAlerts;
    
    private List<String> currentWarningAlerts;

    
    public boolean isErrorAlert()
    {
        return isErrorAlert;
    }

    public void setErrorAlert( boolean isErrorAlert )
    {
        this.isErrorAlert = isErrorAlert;
    }

    public boolean isWarningAlert()
    {
        return isWarningAlert;
    }

    public void setWarningAlert( boolean isWarningAlert )
    {
        this.isWarningAlert = isWarningAlert;
    }

    public List<String> getCurrentErrorAlerts()
    {
        return currentErrorAlerts;
    }

    public void setCurrentErrorAlerts( List<String> currentErrorAlerts )
    {
        this.currentErrorAlerts = currentErrorAlerts;
    }

    public List<String> getCurrentWarningAlerts()
    {
        return currentWarningAlerts;
    }

    public void setCurrentWarningAlerts( List<String> currentWarningAlerts )
    {
        this.currentWarningAlerts = currentWarningAlerts;
    }
    
}
