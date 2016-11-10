package com.realtech.socialsurvey.core.dao.impl;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.dao.CompanyHiddenNotificationDao;
import com.realtech.socialsurvey.core.entities.CompanyHiddenNotification;


@Component ( "CompanyHiddenNotification")
public class CompanyHiddenNotificationDaoImpl extends GenericDaoImpl<CompanyHiddenNotification, Long>
    implements CompanyHiddenNotificationDao
{
    private static final Logger LOG = LoggerFactory.getLogger( CompanyHiddenNotificationDaoImpl.class );


    @Override
    public void processByIdAndStatus( CompanyHiddenNotification companyHiddenNotification, int status )
    {
        LOG.info( "Processing company hidden modification with id " + companyHiddenNotification.getCompanyHiddenNotificationId()
            + " and status " + status );
        Query query = getSession().getNamedQuery( "CompanyHiddenNotification.processByIdAndStatus" );
        query.setParameter( 0, companyHiddenNotification.getCompanyHiddenNotificationId() );
        query.setParameter( 1, status );
        query.executeUpdate();
        LOG.info(
            "Processed the company hidden modification record " + companyHiddenNotification.getCompanyHiddenNotificationId() );
    }


}
