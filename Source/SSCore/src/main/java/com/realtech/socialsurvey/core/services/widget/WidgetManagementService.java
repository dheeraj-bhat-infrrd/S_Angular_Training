package com.realtech.socialsurvey.core.services.widget;


import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.widget.WidgetConfiguration;
import com.realtech.socialsurvey.core.entities.widget.WidgetConfigurationRequest;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


public interface WidgetManagementService
{

    public WidgetConfiguration saveWidgetConfigurationForEntity( long entityId, String entityType,
        long userId, WidgetConfigurationRequest widgetConfigurationRequest ) throws InvalidInputException;


    public WidgetConfiguration getWidgetConfigurationForEntity( long entityId, String entityType, boolean fillDefault )
        throws InvalidInputException;


    public WidgetConfiguration resetWidgetConfigurationForEntity( long entityId, String entityType, long userId, String requestMessage ) throws InvalidInputException;


    public WidgetConfiguration getDefaultWidgetConfiguration( OrganizationUnitSettings unitSettings, String unitSettingType );


}
