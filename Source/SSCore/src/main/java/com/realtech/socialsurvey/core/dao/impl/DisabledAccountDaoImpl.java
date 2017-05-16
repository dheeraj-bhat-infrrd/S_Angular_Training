package com.realtech.socialsurvey.core.dao.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.DisabledAccountDao;
import com.realtech.socialsurvey.core.entities.DisabledAccount;
import com.realtech.socialsurvey.core.exception.DatabaseException;


/*
 * This class contains methods specific to DisabledAccount entity.
 */
@Component ( "disabledAccount")
public class DisabledAccountDaoImpl extends GenericDaoImpl<DisabledAccount, Long> implements DisabledAccountDao
{

    /*
     * Method to disable accounts whose DisableDate (Last date of billing cycle) has passed.
     * Returns list of all the accounts disabled.
     */
    @SuppressWarnings ( "unchecked")
    @Override
    public List<DisabledAccount> getAccountsToDisable( Date maxDisableDate )
    {
        try {
            Date currentDate = new Date();
            /*Query query = getSession()
                .createQuery( "update DisabledAccount set status=?, modifiedOn=? where disableDate<? and status=? and isForceDelete=false" );
            query.setParameter( 0, CommonConstants.DISABLED_ACCOUNT_PROCESSED );
            query.setParameter( 1, currentDate );
            query.setParameter( 2, maxDisableDate );
            query.setParameter( 3, CommonConstants.STATUS_ACTIVE );
            query.executeUpdate();*/
            Criteria criteria = getSession().createCriteria( DisabledAccount.class );
            criteria.add(Restrictions.eq( CommonConstants.IS_FORCE_DELETE_COLUMN, false ));
            criteria.add( Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ) );
            criteria.add( Restrictions.le( CommonConstants.ACCOUNT_DISABLE_DATE_COLUMN, currentDate ) );

            
            return criteria.list();
        } catch ( HibernateException e ) {
            throw new DatabaseException( "HibernateException caught in disableAccounts(). Nested exception is ", e );
        }
    }


    /*
     * Method to get accounts which were disabled before grace span.
     */
    @SuppressWarnings ( "unchecked")
    @Override
    public List<DisabledAccount> getAccountsForPurge( int graceSpan )
    {
        try {
                        
            Criteria criteria = getSession().createCriteria( DisabledAccount.class );
            Date maxDateForPurge = getNdaysBackDate( graceSpan );
            Criterion criterion = Restrictions.and( 
                Restrictions.lt( CommonConstants.ACCOUNT_DISABLE_DATE_COLUMN, maxDateForPurge ),
                Restrictions.eq( CommonConstants.IS_FORCE_DELETE_COLUMN, true ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ));
            criteria.add(criterion);
            return criteria.list();
        } catch ( HibernateException e ) {
            throw new DatabaseException( "HibernateException caught in getAccountsForPurge(). Nested exception is ", e );
        }
    }


    private Date getNdaysBackDate( int noOfDays )
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DATE, noOfDays * ( -1 ) );
        Date startDate = calendar.getTime();
        return startDate;
    }
}
