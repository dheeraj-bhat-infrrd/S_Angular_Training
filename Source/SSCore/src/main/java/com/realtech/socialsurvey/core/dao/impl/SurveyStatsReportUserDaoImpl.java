package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.SurveyStatsReportUserDao;
import com.realtech.socialsurvey.core.entities.SurveyStatsReportCompany;
import com.realtech.socialsurvey.core.entities.SurveyStatsReportUser;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component
public class SurveyStatsReportUserDaoImpl extends GenericReportingDaoImpl<SurveyStatsReportUser, String>implements SurveyStatsReportUserDao
{

    private static final Logger LOG = LoggerFactory.getLogger( SurveyStatsReportUserDaoImpl.class );


    @SuppressWarnings ( "unchecked")
    @Override
    public List<SurveyStatsReportUser> fetchUserSurveyStatsById( Long userId )
    {
        LOG.info( "Method to fetch all the survey stats,fetchUserSurveyStatsById() started." );
        Criteria criteria = getSession().createCriteria( SurveyStatsReportUser.class );
        try {
            criteria.add( Restrictions.eq( "userId" , userId ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUserSurveyStatsById() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUserSurveyStatsById() ", hibernateException );
        }

        List<SurveyStatsReportUser> demo = (List<SurveyStatsReportUser>) criteria.list();
        LOG.info( "Method to fetch all the users by email id, fetchUserSurveyStatsById() finished."+demo+criteria );

        return (List<SurveyStatsReportUser>) criteria.list();
    }
}
