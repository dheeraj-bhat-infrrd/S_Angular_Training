package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.UserAdoptionReportDao;
import com.realtech.socialsurvey.core.entities.UserAdoptionReport;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class UserAdoptionReportDaoImpl extends GenericReportingDaoImpl<UserAdoptionReport, String>implements UserAdoptionReportDao
{
    private static final Logger LOG = LoggerFactory.getLogger( UserAdoptionReportDaoImpl.class );


    @Override
    public List<UserAdoptionReport> fetchUserAdoptionByCompanyId(Long companyId)
    {
        LOG.info( "method to fetch user adoption report based on companyId,fetchUserAdoptionByCompanyId() started" );
        Criteria criteria = getSession().createCriteria( UserAdoptionReport.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserAdoptionByCompanyId() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserAdoptionByCompanyId() ", hibernateException );
        }

        LOG.info( "method to fetch branch based on companyId, fetchUserAdoptionByCompanyId() finished." );
        return (List<UserAdoptionReport>) criteria.list();
        
    }
}
