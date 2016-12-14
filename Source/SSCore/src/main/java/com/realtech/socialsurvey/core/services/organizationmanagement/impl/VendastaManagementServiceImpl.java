package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.VendastaProductSettings;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.VendastaManagementService;


@DependsOn ( "generic")
@Component
public class VendastaManagementServiceImpl implements VendastaManagementService
{
    private static final Logger LOG = LoggerFactory.getLogger( VendastaManagementServiceImpl.class );

    @Autowired
    OrganizationUnitSettingsDao organizationUnitSettingsDao;


    @Override
    public boolean updateVendastaAccess( String collectionName, OrganizationUnitSettings unitSettings )
        throws InvalidInputException
    {
        if ( unitSettings == null ) {
            throw new InvalidInputException( "Unit settings cannot be null." );
        }

        LOG.debug( "Updating unitSettings: " + unitSettings + " with vendasta Access: " + unitSettings.isVendastaAccessible() );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings( CommonConstants.VENDASTA_ACCESS,
            unitSettings.isVendastaAccessible(), unitSettings, collectionName );
        LOG.debug( "Updated the record successfully" );

        return true;
    }


    @Override
    public boolean updateVendastaRMSettings( String collectionName, OrganizationUnitSettings unitSettings,
        VendastaProductSettings vendastaReputationManagementSettings ) throws InvalidInputException
    {
        if ( unitSettings == null ) {
            throw new InvalidInputException( "OrganizationUnitSettings cannot be null." );
        }

        LOG.debug( "Updating collectionName: " + unitSettings + " with vendastaReputationManagementSettings: "
            + vendastaReputationManagementSettings );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_VENDASTA_RM_SETTINGS, vendastaReputationManagementSettings, unitSettings,
            collectionName );
        LOG.debug( "Updated the record successfully" );

        return true;
    }
}
