package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.CompanyUserReportDao;
import com.realtech.socialsurvey.core.dao.GenericReportingDao;
import com.realtech.socialsurvey.core.entities.CompanyUserReport;
import com.realtech.socialsurvey.core.entities.UserAdoptionReport;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class CompanyUserReportImpl extends GenericReportingDaoImpl<CompanyUserReport, String> implements CompanyUserReportDao
{

    private static final Logger LOG = LoggerFactory.getLogger( CompanyUserReportImpl.class );
    
    @Override
    public List<CompanyUserReport> fetchCompanyUserReportByCompanyId(Long companyId)
    {
        LOG.info( "method to fetch company user report based on companyId,fetchCompanyUserReportByCompanyId() started" );
        Criteria criteria = getSession().createCriteria( CompanyUserReport.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, companyId ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserAdoptionByCompanyId() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchCompanyUserReportByCompanyId() ", hibernateException );
        }

        LOG.info( "method to fetch company user report based on companyId, fetchCompanyUserReportByCompanyId() finished." );
        return (List<CompanyUserReport>) criteria.list();
        
    }
}
