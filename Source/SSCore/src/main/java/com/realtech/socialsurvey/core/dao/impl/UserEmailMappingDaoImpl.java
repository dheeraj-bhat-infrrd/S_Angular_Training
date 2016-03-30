package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.UserEmailMappingDao;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserEmailMapping;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


/**
 * 
 * @author rohit
 *
 */
@Component("userEmailMapping")
public class UserEmailMappingDaoImpl extends GenericDaoImpl<UserEmailMapping, Long> implements UserEmailMappingDao
{
    private static final Logger LOG = LoggerFactory.getLogger( UserEmailMapping.class );


    
    
    
    @SuppressWarnings ( "unchecked")
    @Override
    public List<UserEmailMapping> getAciveUserEmailMappingForUser( User user ) throws InvalidInputException
    {
        if ( user == null )
            throw new InvalidInputException( "user passed in getAciveUserEmailMappingForUser() cannot be null" );
        
        LOG.info( "Method getAciveUserEmailMappingForUser called to fetch list of emails of user : " + user.getFirstName() );
        Criteria criteria = getSession().createCriteria( UserEmailMapping.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.USER_COLUMN, user ) );
            criteria.add( Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ) );

        } catch ( HibernateException hibernateException ) {
            throw new DatabaseException( "Exception caught in getUsersForCompany() ", hibernateException );
        }
        LOG.info( "Method getAciveUserEmailMappingForUser finished to fetch list of emails of user : " + user.getFirstName() );
        return (List<UserEmailMapping>) criteria.list();
    }

}
