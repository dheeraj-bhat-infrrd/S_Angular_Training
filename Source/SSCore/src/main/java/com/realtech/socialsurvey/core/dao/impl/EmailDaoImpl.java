package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.dao.EmailDao;
import com.realtech.socialsurvey.core.entities.EmailObject;


@Component
public class EmailDaoImpl extends GenericDaoImpl<EmailObject, Long> implements EmailDao
{

    @Autowired
    SessionFactory sessionFactory;


    @Transactional
    @Override
    public void saveEmailObjectInDB( EmailObject emailObject )
    {
        Session session = sessionFactory.getCurrentSession();
        session.save( emailObject );
    }


    @Transactional ( readOnly = true)
    @Override
    public List<EmailObject> findAllEmails()
    {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria( EmailObject.class );
        return (List<EmailObject>) criteria.list();

    }


    @Transactional
    @Override
    public void deleteEmail( EmailObject emailObject )
    {
        Session session = sessionFactory.getCurrentSession();
        session.delete( emailObject );

    }

}
