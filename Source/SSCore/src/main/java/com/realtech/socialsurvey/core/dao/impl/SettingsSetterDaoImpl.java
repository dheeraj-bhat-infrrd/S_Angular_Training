package com.realtech.socialsurvey.core.dao.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.dao.SettingsSetterDao;
import com.realtech.socialsurvey.core.entities.SettingsDetails;
import com.realtech.socialsurvey.core.exception.DatabaseException;


@Component ( "settingsSetter")
public class SettingsSetterDaoImpl extends GenericDaoImpl<SettingsDetails, Long> implements SettingsSetterDao
{
    private static final Logger LOG = LoggerFactory.getLogger( SettingsSetterDaoImpl.class );

    @Autowired
    SessionFactory sessionFactory;


    @SuppressWarnings ( "unchecked")
    @Override
    public List<SettingsDetails> getScoresById( long companyId, long regionId, long branchId )
    {
        LOG.info( "Method getScoresById started" );

        List<SettingsDetails> scoreListObject = new ArrayList<SettingsDetails>();
        List<Object[]> scoreList = new ArrayList<Object[]>();
        String hql = null;
        Query query = null;
        try {

            if ( regionId == 0 && branchId == 0 ) {
                hql = "SELECT c.settingsSetStatus, c.settingsLockStatus From Company c FETCH ALL PROPERTIES WHERE c.companyId =?";
            } else if ( branchId == 0 ) {
                hql = "SELECT c.settingsSetStatus, c.settingsLockStatus, r.settingsSetStatus, r.settingsLockStatus  From Company c,Region r FETCH ALL PROPERTIES WHERE c.companyId =? AND r.regionId = ? ";
            } else {
                hql = "SELECT c.settingsSetStatus, c.settingsLockStatus, r.settingsSetStatus, r.settingsLockStatus, b.settingsSetStatus, b.settingsLockStatus  From Company c,Region r, Branch b FETCH ALL PROPERTIES WHERE c.companyId =? AND r.regionId = ? AND b.branchId = ? ";
            }

            query = getSession().createQuery( hql );

            query.setParameter( 0, companyId );
            if ( regionId != 0 ) {
                query.setParameter( 1, regionId );
            }
            if ( branchId != 0 ) {
                query.setParameter( 2, branchId );
            }
            scoreList = query.list();
            for ( Object[] row : scoreList ) {
                SettingsDetails companySettingsDetails = new SettingsDetails();
                companySettingsDetails.setSetSettingsHolder( new BigInteger( String.valueOf( row[0] ) ) );
                companySettingsDetails.setLockSettingsHolder( Long.valueOf( String.valueOf( row[1] ) ) );
                SettingsDetails regionSettingsDetails = null;
                if ( row.length > 2 && row.length <= 4 ) {
                    regionSettingsDetails = new SettingsDetails();
                    regionSettingsDetails.setSetSettingsHolder( new BigInteger( String.valueOf( row[2] ) ) );
                    regionSettingsDetails.setLockSettingsHolder( Long.valueOf( String.valueOf( row[3] ) ) );
                }
                SettingsDetails branchSettingsDetails = null;
                if ( row.length > 4 ) {
                    regionSettingsDetails = new SettingsDetails();
                    regionSettingsDetails.setSetSettingsHolder( new BigInteger( String.valueOf( row[2] ) ) );
                    regionSettingsDetails.setLockSettingsHolder( Long.valueOf( String.valueOf( row[3] ) ) );
                    branchSettingsDetails = new SettingsDetails();
                    branchSettingsDetails.setSetSettingsHolder( new BigInteger( String.valueOf( row[4] ) ) );
                    branchSettingsDetails.setLockSettingsHolder( Long.valueOf( String.valueOf( row[5] ) ) );
                }
                scoreListObject.add( companySettingsDetails );
                if ( branchSettingsDetails != null ) {
                    scoreListObject.add( branchSettingsDetails );
                }
                if ( regionSettingsDetails != null ) {
                    scoreListObject.add( regionSettingsDetails );
                }

            }


        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in getScoresById() ", hibernateException );
            throw new DatabaseException( "Exception caught in method getScoresById ", hibernateException );
        }
        LOG.info( "Method getScoresById finished." );

        return scoreListObject;
    }

}
