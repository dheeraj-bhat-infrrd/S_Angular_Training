package com.realtech.socialsurvey.core.dao.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericReportingDao;
import com.realtech.socialsurvey.core.exception.DatabaseException;

@Component ( "genericReporting")
@Transactional(value = "transactionManagerForReporting")
public class GenericReportingDaoImpl<T, ID extends Serializable> implements GenericReportingDao<T, ID>
{
    private static final Logger LOG = LoggerFactory.getLogger( GenericDaoImpl.class );
    private Class<T> persistentClass;
    @Autowired
    private SessionFactory sessionFactoryForReporting;


    protected Session getSession()
    {
        Session session;
        try {
            session = sessionFactoryForReporting.getCurrentSession();
        } catch ( HibernateException hibernateException ) {
            LOG.error( "HibernateException caught while getting session. ", hibernateException );
            throw new DatabaseException( "HibernateException caught while getting session. ", hibernateException );
        }
        return session;
    }


    public Class<T> getPersistentClass()
    {
        return persistentClass;
    }


    public void setPersistentClass( Class<T> persistentClass )
    {
        this.persistentClass = persistentClass;
    }


    @Override
    @Transactional("transactionManagerForReporting")
    public T saveOrUpdate( T entity )
    {
        try {
            getSession().saveOrUpdate( entity );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "HibernateException caught in saveOrUpdate().", hibernateException );
            throw new DatabaseException( "HibernateException caught in saveOrUpdate().", hibernateException );
        }
        return entity;
    }


    @Override
    @Transactional(value = "transactionManagerForReporting")
    public T save( T entity )
    {
        try {
            getSession().save( entity );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "HibernateException caught in save().", hibernateException );
            throw new DatabaseException( "HibernateException caught in save().", hibernateException );
        }
        return entity;
    }


    @Override
    @Transactional("transactionManagerForReporting")
    public void update( T entity )
    {
        try {
            getSession().update( entity );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "HibernateException caught in update().", hibernateException );
            throw new DatabaseException( "HibernateException caught in update().", hibernateException );
        }
    }


    @Override
    @SuppressWarnings ( "unchecked")
    public T findById( Class<T> entityClass, ID id )
    {
        T entity;
        try {
            entity = (T) getSession().get( entityClass, id );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "HibernateException caught in findById(). ", hibernateException );
            throw new DatabaseException( "HibernateException caught in findById(). ", hibernateException );
        }
        return entity;
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public List<T> findAll( Class<T> entityClass )
    {
        try {
            final Criteria crit = getSession().createCriteria( entityClass );
            return crit.list();
        } catch ( HibernateException hibernateException ) {
            LOG.error( "HibernateException caught in findAll().", hibernateException );
            throw new DatabaseException( "HibernateException caught in findAll().", hibernateException );
        }
    }


    @Override
    @SuppressWarnings ( "unchecked")
    public List<T> findByExample( T exampleInstance, String[] excludeProperty )
    {
        Criteria crit = getSession().createCriteria( getPersistentClass() );
        try {
            Example example = Example.create( exampleInstance );
            for ( String exclude : excludeProperty ) {
                example.excludeProperty( exclude );
            }
            crit.add( example );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "HibernateException caught in findByCriteria().", hibernateException );
            throw new DatabaseException( "HibernateException caught in findByCriteria().", hibernateException );
        }
        return crit.list();
    }

    @Override
    public void flush()
    {
        try {
            getSession().flush();
        } catch ( HibernateException hibernateException ) {
            LOG.error( "HibernateException caught in flush().", hibernateException );
            throw new DatabaseException( "HibernateException caught in flush().", hibernateException );
        }
    }


    @Override
    public void clear()
    {
        try {
            getSession().clear();
        } catch ( HibernateException hibernateException ) {
            LOG.error( "HibernateException caught in clear().", hibernateException );
            throw new DatabaseException( "HibernateException caught in clear().", hibernateException );
        }
    }


    @Override
    @SuppressWarnings ( "unchecked")
    public List<T> findByCriteria( Class<T> dataClass, Criterion... criterion )
    {
        Criteria crit = getSession().createCriteria( dataClass );
        try {
            for ( Criterion c : criterion ) {
                crit.add( c );
            }
        } catch ( HibernateException hibernateException ) {
            LOG.error( "HibernateException caught in findByCriteria().", hibernateException );
            throw new DatabaseException( "HibernateException caught in findByCriteria().", hibernateException );
        }
        return crit.list();
    }


    @Override
    @SuppressWarnings ( "unchecked")
    public List<T> findByKeyValue( Class<T> dataClass, Map<String, Object> queries )
    {
        Criteria criteria = getSession().createCriteria( dataClass );
        try {
            for ( Entry<String, Object> query : queries.entrySet() ) {
                criteria.add( Restrictions.eq( query.getKey(), query.getValue() ) );
            }
        } catch ( HibernateException hibernateException ) {
            LOG.error( "HibernateException caught in findByKeyValue().", hibernateException );
            throw new DatabaseException( "HibernateException caught in findByKeyValue().", hibernateException );
        }
        return criteria.list();
    }
    
    @Override
    @SuppressWarnings ( "unchecked")
    public List<T> findByColumn( Class<T> dataClass, String column, Object value )
    {
        Criteria criteria = getSession().createCriteria( dataClass );
        try {
            criteria.add( Restrictions.eq( column, value ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "HibernateException caught in findByColumn().", hibernateException );
            throw new DatabaseException( "HibernateException caught in findByColumn().", hibernateException );
        }
        return criteria.list();
    }


    @Override
    @SuppressWarnings ( "unchecked")
    public List<T> findByColumnForMultipleValues( Class<T> dataClass, String column, List<?> values )
    {
        Criteria criteria = getSession().createCriteria( dataClass );
        try {
            criteria.add( Restrictions.in( column, values ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "HibernateException caught in findByColumnForMultipleValues().", hibernateException );
            throw new DatabaseException( "HibernateException caught in findByColumnForMultipleValues().", hibernateException );
        }
        return criteria.list();
    }


    @Override
    public long findNumberOfRows( Class<T> dataClass )
    {
        try {
            return (long) getSession().createCriteria( dataClass ).setProjection( Projections.rowCount() ).uniqueResult();
        } catch ( HibernateException hibernateException ) {
            LOG.error( "HibernateException caught in findNumberOfRows().", hibernateException );
            throw new DatabaseException( "HibernateException caught in findNumberOfRows().", hibernateException );
        }
    }


    @Override
    public long findNumberOfRowsByKeyValue( Class<T> dataClass, Map<String, Object> queries )
    {
        Criteria criteria = getSession().createCriteria( dataClass );
        try {
            for ( Entry<String, Object> query : queries.entrySet() ) {
                criteria.add( Restrictions.eq( query.getKey(), query.getValue() ) );
            }
        } catch ( HibernateException hibernateException ) {
            LOG.error( "HibernateException caught in findByKeyValue().", hibernateException );
            throw new DatabaseException( "HibernateException caught in findByKeyValue().", hibernateException );
        }
        return (long) criteria.setProjection( Projections.rowCount() ).uniqueResult();
    }




    @SuppressWarnings ( "unchecked")
    @Override
    public List<T> findAllActive( Class<T> entityClass )
    {
        try {
            Criteria crit = getSession().createCriteria( entityClass );
            crit.add( Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ) );
            return crit.list();
        } catch ( HibernateException hibernateException ) {
            LOG.error( "HibernateException caught in findAllActive().", hibernateException );
            throw new DatabaseException( "HibernateException caught in findAllActive().", hibernateException );
        }
    }


    @Override
    @SuppressWarnings ( "unchecked")
    public List<T> findByKeyValueAscending( Class<T> dataClass, Map<String, Object> queries, String ascendingColumn )
    {
        Criteria criteria = getSession().createCriteria( dataClass );
        try {
            for ( Entry<String, Object> query : queries.entrySet() ) {
                criteria.add( Restrictions.eq( query.getKey(), query.getValue() ) );
            }
            criteria.addOrder( Order.asc( ascendingColumn ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "HibernateException caught in findByKeyValue().", hibernateException );
            throw new DatabaseException( "HibernateException caught in findByKeyValue().", hibernateException );
        }
        return criteria.list();
    }


    @Override
    public List<T> findByKeyValueAscendingWithAlias( Class<T> dataClass, Map<String, Object> queries, String ascendingColumn,
        String alias )
    {
        return findByKeyValueAscendingWithAlias( dataClass, queries, Arrays.asList( new String[] { ascendingColumn } ), alias );
    }


    @Override
    @SuppressWarnings ( "unchecked")
    public List<T> findByKeyValueAscendingWithAlias( Class<T> dataClass, Map<String, Object> queries,
        List<String> ascendingColumns, String alias )
    {
        Criteria criteria = getSession().createCriteria( dataClass );
        try {
            for ( Entry<String, Object> query : queries.entrySet() ) {
                criteria.add( Restrictions.eq( query.getKey(), query.getValue() ) );
            }
            criteria.createAlias( alias, "alias" );
            if ( ascendingColumns != null && !ascendingColumns.isEmpty() ) {
                for ( String ascendingColumn : ascendingColumns ) {
                    criteria.addOrder( Order.asc( "alias." + ascendingColumn ) );
                }
            }
        } catch ( HibernateException hibernateException ) {
            LOG.error( "HibernateException caught in findByKeyValueAscendingWithAlias().", hibernateException );
            throw new DatabaseException( "HibernateException caught in findByKeyValueAscendingWithAlias().", hibernateException );
        }
        return criteria.list();
    }


    @Override
    public List<T> findProjectionsByKeyValue( Class<T> dataClass, List<String> columnNames, Map<String, Object> queries )
    {
        return findProjectionsByKeyValue( dataClass, columnNames, queries, null );
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public List<T> findProjectionsByKeyValue( Class<T> dataClass, List<String> columnNames, Map<String, Object> queries,
        String orderColumnName )
    {
        Criteria crit = null;
        try {
            crit = getSession().createCriteria( dataClass );
            ProjectionList projections = Projections.projectionList();
            for ( String columnName : columnNames ) {
                projections.add( Projections.property( columnName ).as( columnName ) );
            }
            crit.setProjection( projections );
            for ( Entry<String, Object> query : queries.entrySet() ) {
                crit.add( Restrictions.eq( query.getKey(), query.getValue() ) );
            }
            if ( orderColumnName != null && !orderColumnName.isEmpty() )
                crit.addOrder( Order.asc( orderColumnName ) );
        } catch ( HibernateException e ) {
            LOG.error( "HibernateException caught in findProjectionsByKeyValue(). Reason: " + e.getMessage(), e );
            throw new DatabaseException( "HibernateException caught in findProjectionsByKeyValue().", e );
        }
        return crit.setResultTransformer( Transformers.aliasToBean( dataClass ) ).list();
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public List<T> findProjectionsAscOrderByKeyValue( Class<T> dataClass, List<String> columnNames,
        Map<String, Object> queries, String columnToOrder )
    {
        Criteria crit = null;
        try {
            crit = getSession().createCriteria( dataClass );
            ProjectionList projections = Projections.projectionList();
            for ( String columnName : columnNames ) {
                projections.add( Projections.property( columnName ).as( columnName ) );
            }
            crit.setProjection( projections );
            for ( Entry<String, Object> query : queries.entrySet() ) {
                crit.add( Restrictions.eq( query.getKey(), query.getValue() ) );
            }
            crit.addOrder( Order.asc( columnToOrder ) );
        } catch ( HibernateException e ) {
            LOG.error( "HibernateException caught in findProjectionsByKeyValue(). Reason: " + e.getMessage(), e );
            throw new DatabaseException( "HibernateException caught in findProjectionsByKeyValue().", e );
        }
        return crit.setResultTransformer( Transformers.aliasToBean( dataClass ) ).list();
    }


    @Override
	public void saveAll(List<T> entityList) {
		for(T entity : entityList) {
			save(entity);
		}
		
	}

	@Override
	public void deleteAll(List<T> entityList) {
		for(T entity : entityList) {
			delete(entity);
		}
		
	}

	@Override
	public void delete(T entity) {
		try {
            getSession().delete(entity);
        } catch ( HibernateException hibernateException ) {
            LOG.error( "HibernateException caught in delete().", hibernateException );
            throw new DatabaseException( "HibernateException caught in delete().", hibernateException );
        }
	}
	
	@Override
    @SuppressWarnings ( "unchecked")
    public List<T> findByKeyValueInBatch(Class<T> dataClass, Map<String, Object> queries, int startIndex, int batchSize )
    {
        Criteria criteria = getSession().createCriteria( dataClass );
        try {
            for ( Entry<String, Object> query : queries.entrySet() ) {
                criteria.add( Restrictions.eq( query.getKey(), query.getValue() ) );
            }
            criteria.setFirstResult(startIndex);
            criteria.setFetchSize(batchSize);
        } catch ( HibernateException hibernateException ) {
            LOG.error( "HibernateException caught in findByKeyValue().", hibernateException );
            throw new DatabaseException( "HibernateException caught in findByKeyValue().", hibernateException );
        }
        return criteria.list();
    }

}
