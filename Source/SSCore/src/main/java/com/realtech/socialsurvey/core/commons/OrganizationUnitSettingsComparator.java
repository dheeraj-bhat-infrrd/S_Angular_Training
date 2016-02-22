package com.realtech.socialsurvey.core.commons;

import java.util.Comparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;


/**
 * Compares OrganizationUnitSettings based on the name
 * */
public class OrganizationUnitSettingsComparator implements Comparator<OrganizationUnitSettings> {

    private static final Logger LOG = LoggerFactory.getLogger( OrganizationUnitSettingsComparator.class );


    @Override
    public int compare( OrganizationUnitSettings organizationUnitSettings1, OrganizationUnitSettings organizationUnitSettings2 ) {
        LOG.info( "Comparing OrganizationUnitSettings based on name" );
        return organizationUnitSettings1.getContact_details().getName()
            .compareToIgnoreCase( organizationUnitSettings2.getContact_details().getName() );
    }
}
