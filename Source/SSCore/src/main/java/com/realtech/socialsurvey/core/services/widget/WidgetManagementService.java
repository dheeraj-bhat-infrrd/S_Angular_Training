package com.realtech.socialsurvey.core.services.widget;


import java.util.List;

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


    public List<String> getListOfAvailableSources( String profileLevel, long iden ) throws InvalidInputException;


    /**
     * @param unitSettings
     * @param isLocked
     * @return
     */
    public WidgetConfiguration updateLockSettingsLog( WidgetConfiguration widgetConfiguration, Boolean isLocked );


    /**
     * @param profileLevel
     * @param profileId
     * @param unitSettings
     * @param lockFlag
     * @param isLocked
     * @return
     */
    public OrganizationUnitSettings updateLowerHeirarchyLock( String profileLevel, long profileId,
        OrganizationUnitSettings unitSettings, int lockFlag, boolean isLocked );


    /**
     * @param profileLevel
     * @param isLocked
     * @return
     */
    public int createLockFlag( String profileLevel, boolean isLocked );


    public boolean hasLockedLowerHierarchy( WidgetConfiguration widgetConfiguration );


    public void saveConfigurationInMongo( String entityType, long entityId, WidgetConfiguration widgetConfiguration )
        throws InvalidInputException;

}
