package com.realtech.socialsurvey.core.services.widget.impl;

import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.widget.WidgetConfigurationRequest;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.widget.WidgetManagementService;


@Component
public class ProcessWidgetOverrideAndLock
{

    private static final Logger LOG = LoggerFactory.getLogger( ProcessWidgetOverrideAndLock.class );

    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private WidgetManagementService widgetManagementService;

    @Resource
    @Qualifier ( "branch")
    private BranchDao branchDao;

    @Autowired
    private UserProfileDao userProfileDao;


    @Async
    public void processOverrideAndLock( long entityId, String entityType, long userId,
        WidgetConfigurationRequest widgetConfigurationRequest, boolean applyLock, boolean lockLowerHierarchy, boolean override )
    {
        LOG.info( "method processOverrideAndLock() called" );
        List<OrganizationUnitSettings> regionSettingsList = null;
        List<OrganizationUnitSettings> branchSettingsList = null;
        List<OrganizationUnitSettings> agentSettingsList = null;


        String companyOverrideTag = "COMPANY OVERRIDE - ";
        String regionOverrideTag = "REGION OVERRIDE - ";
        String branchOverrideTag = "BRANCH OVERRIDE - ";

        if ( CommonConstants.COMPANY_ID_COLUMN.equals( entityType ) ) {

            try {
                regionSettingsList = organizationUnitSettingsDao.fetchOrganizationUnitSettingsForMultipleIds(
                    new HashSet<>( organizationManagementService.getRegionIdsUnderCompany( entityId, -1, -1 ) ),
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );


                branchSettingsList = organizationUnitSettingsDao.fetchOrganizationUnitSettingsForMultipleIds(
                    new HashSet<>( organizationManagementService.getBranchIdsUnderCompany( entityId, -1, -1 ) ),
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );

                agentSettingsList = organizationUnitSettingsDao.fetchOrganizationUnitSettingsForMultipleIds(
                    new HashSet<>( organizationManagementService.getAgentIdsUnderCompany( entityId, -1, -1 ) ),
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

            } catch ( InvalidInputException e ) {
                LOG.warn( "Unable to get hierarchy list" );
                return;
            }

            widgetConfigurationRequest.setRequestMessage( companyOverrideTag + widgetConfigurationRequest.getRequestMessage() );


        } else if ( CommonConstants.REGION_ID_COLUMN.equals( entityType ) ) {

            try {
                branchSettingsList = organizationUnitSettingsDao.fetchOrganizationUnitSettingsForMultipleIds(
                    new HashSet<>( branchDao.getBranchIdsOfRegion( entityId, -1, -1, -1 ) ),
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );

                agentSettingsList = organizationUnitSettingsDao.fetchOrganizationUnitSettingsForMultipleIds(
                    userProfileDao.findUserIdsByRegion( entityId ),
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

                widgetConfigurationRequest
                    .setRequestMessage( regionOverrideTag + widgetConfigurationRequest.getRequestMessage() );

            } catch ( InvalidInputException e ) {
                LOG.warn( "Unable to get hierarchy list" );
                return;
            }

        } else if ( CommonConstants.BRANCH_ID_COLUMN.equals( entityType ) ) {

            agentSettingsList = organizationUnitSettingsDao.fetchOrganizationUnitSettingsForMultipleIds(
                userProfileDao.findUserIdsByBranch( entityId ), MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

            widgetConfigurationRequest.setRequestMessage( branchOverrideTag + widgetConfigurationRequest.getRequestMessage() );

        }

        else {
            LOG.debug( "Unable to lock override: invalid higher hierarchy" );
            return;
        }


        widgetConfigurationRequest.setOverrideLowerHierarchy( CommonConstants.WIDGET_DEFAULT_OVERRIDE_LOWER_HIERARCHY );

        int lockFlag = widgetManagementService.createLockFlag( entityType, lockLowerHierarchy );

        // override Lower hierarchy
        if ( regionSettingsList != null && !regionSettingsList.isEmpty() ) {
            for ( OrganizationUnitSettings settings : regionSettingsList ) {

                try {

                    widgetConfigurationRequest.setLockLowerHierarchy(
                        widgetManagementService.hasLockedLowerHierarchy( settings.getWidgetConfiguration() ) ? "true"
                            : "false" );

                    if ( applyLock ) {
                        widgetManagementService.updateLowerHeirarchyLock( entityType, entityId, settings, lockFlag,
                            lockLowerHierarchy );
                        widgetManagementService.saveConfigurationInMongo( CommonConstants.REGION_ID_COLUMN, settings.getIden(),
                            settings.getWidgetConfiguration() );

                    }

                    if ( lockLowerHierarchy || override ) {
                        widgetManagementService.saveWidgetConfigurationForEntity( settings.getIden(),
                            CommonConstants.REGION_ID_COLUMN, userId, widgetConfigurationRequest );
                    }

                } catch ( InvalidInputException e ) {
                    LOG.warn( "Unable to process region list for widget override/lock", e );
                }
            }
        }

        if ( branchSettingsList != null && !branchSettingsList.isEmpty() ) {
            for ( OrganizationUnitSettings settings : branchSettingsList ) {

                try {
                    widgetConfigurationRequest.setLockLowerHierarchy(
                        widgetManagementService.hasLockedLowerHierarchy( settings.getWidgetConfiguration() ) ? "true"
                            : "false" );

                    if ( applyLock ) {
                        widgetManagementService.updateLowerHeirarchyLock( entityType, entityId, settings, lockFlag,
                            lockLowerHierarchy );
                        widgetManagementService.saveConfigurationInMongo( CommonConstants.BRANCH_ID_COLUMN, settings.getIden(),
                            settings.getWidgetConfiguration() );
                    }

                    if ( lockLowerHierarchy || override ) {
                        widgetManagementService.saveWidgetConfigurationForEntity( settings.getIden(),
                            CommonConstants.BRANCH_ID_COLUMN, userId, widgetConfigurationRequest );
                    }

                } catch ( InvalidInputException e ) {
                    LOG.warn( "Unable to process branch list for widget override/lock", e );
                }
            }
        }

        if ( agentSettingsList != null && !agentSettingsList.isEmpty() ) {
            for ( OrganizationUnitSettings settings : agentSettingsList ) {

                try {
                    widgetConfigurationRequest.setLockLowerHierarchy(
                        widgetManagementService.hasLockedLowerHierarchy( settings.getWidgetConfiguration() ) ? "true"
                            : "false" );

                    if ( applyLock ) {
                        widgetManagementService.updateLowerHeirarchyLock( entityType, entityId, settings, lockFlag,
                            lockLowerHierarchy );
                        widgetManagementService.saveConfigurationInMongo( CommonConstants.AGENT_ID_COLUMN, settings.getIden(),
                            settings.getWidgetConfiguration() );
                    }

                    if ( lockLowerHierarchy || override ) {
                        widgetManagementService.saveWidgetConfigurationForEntity( settings.getIden(),
                            CommonConstants.AGENT_ID_COLUMN, userId, widgetConfigurationRequest );

                    }

                } catch ( InvalidInputException e ) {
                    LOG.warn( "Unable to process agent list for widget override/lock", e );
                }
            }
        }

    }


}
