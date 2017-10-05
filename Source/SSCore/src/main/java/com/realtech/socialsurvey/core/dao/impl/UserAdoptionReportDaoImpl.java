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


    @SuppressWarnings ( "unchecked")
    @Override
    public List<UserAdoptionReport> fetchUserAdoptionById(Long entityId  , String entityType)
    {
        LOG.info( "method to fetch user adoption report based on Id and type,fetchUserAdoptionById() started" );
        Criteria criteria = getSession().createCriteria( UserAdoptionReport.class );
        try {
            if(entityType.equals( CommonConstants.COMPANY_ID_COLUMN )){
                criteria.add( Restrictions.eq( CommonConstants.COMPANY_ID_COLUMN, entityId ) );
            }else if(entityType.equals( CommonConstants.REGION_ID_COLUMN )){
                criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, entityId ) );
            }else if(entityType.equals( CommonConstants.BRANCH_ID_COLUMN )){
                criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, entityId ) );
            }
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserAdoptionById() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserAdoptionById() ", hibernateException );
        }

        LOG.info( "method to fetch user adoption report based on Id and type, fetchUserAdoptionById() finished." );
        return (List<UserAdoptionReport>) criteria.list();
        
    }
    
}
