package com.realtech.socialsurvey.core.dao.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.exception.DatabaseException;

// JIRA: SS-8: By RM05: BOC

/**
 * This is the base Dao which needs to be extended by each Dao. It contains implementation for basic
 * CRUD methods required by every Dao.
 */
@Primary
@Component("generic")
public class GenericDaoImpl<T, ID extends Serializable> implements GenericDao<T, ID> {

	private static final Logger LOG = LoggerFactory.getLogger(GenericDaoImpl.class);
	private Class<T> persistentClass;
	@Autowired
	private SessionFactory sessionFactory;

	protected Session getSession() {
		Session session;
		try {
			session = sessionFactory.getCurrentSession();
		}
		catch (HibernateException hibernateException) {
			LOG.error("HibernateException caught while getting session. ", hibernateException);
			throw new DatabaseException("HibernateException caught while getting session. ", hibernateException);
		}
		return session;
	}

	public Class<T> getPersistentClass() {
		return persistentClass;
	}

	public void setPersistentClass(Class<T> persistentClass) {
		this.persistentClass = persistentClass;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T findById(Class<T> entityClass, ID id) {
		T entity;
		try {
			entity = (T) getSession().load(entityClass, id);
		}
		catch (HibernateException hibernateException) {
			LOG.error("HibernateException caught in findById(). ", hibernateException);
			throw new DatabaseException("HibernateException caught in findById(). ", hibernateException);
		}
		return entity;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findAll(Class<T> entityClass) {
		try {
			final Criteria crit = getSession().createCriteria(entityClass);
			return crit.list();
		}
		catch (HibernateException hibernateException) {
			LOG.error("HibernateException caught in findAll().", hibernateException);
			throw new DatabaseException("HibernateException caught in findAll().", hibernateException);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> findByExample(T exampleInstance, String[] excludeProperty) {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		try {
			Example example = Example.create(exampleInstance);
			for (String exclude : excludeProperty) {
				example.excludeProperty(exclude);
			}
			crit.add(example);
		}
		catch (HibernateException hibernateException) {
			LOG.error("HibernateException caught in findByCriteria().", hibernateException);
			throw new DatabaseException("HibernateException caught in findByCriteria().", hibernateException);
		}
		return crit.list();
	}

	@Override
	public T saveOrUpdate(T entity) {
		try {
			getSession().saveOrUpdate(entity);
		}
		catch (HibernateException hibernateException) {
			LOG.error("HibernateException caught in saveOrUpdate().", hibernateException);
			throw new DatabaseException("HibernateException caught in saveOrUpdate().", hibernateException);
		}
		return entity;
	}

	@Override
	public T save(T entity) {
		try {
			getSession().save(entity);
		}
		catch (HibernateException hibernateException) {
			LOG.error("HibernateException caught in save().", hibernateException);
			throw new DatabaseException("HibernateException caught in save().", hibernateException);
		}
		return entity;
	}

	@Override
	public void update(T entity) {
		try {
			getSession().update(entity);
		}
		catch (HibernateException hibernateException) {
			LOG.error("HibernateException caught in update().", hibernateException);
			throw new DatabaseException("HibernateException caught in update().", hibernateException);
		}
	}

	@Override
	public void delete(T entity) {
		try {
			getSession().delete(entity);
		}
		catch (HibernateException hibernateException) {
			LOG.error("HibernateException caught in delete(). ", hibernateException);
			throw new DatabaseException("HibernateException caught in delete(). ", hibernateException);
		}
	}

	@Override
	public void flush() {
		try {
			getSession().flush();
		}
		catch (HibernateException hibernateException) {
			LOG.error("HibernateException caught in flush().", hibernateException);
			throw new DatabaseException("HibernateException caught in flush().", hibernateException);
		}
	}

	@Override
	public void clear() {
		try {
			getSession().clear();
		}
		catch (HibernateException hibernateException) {
			LOG.error("HibernateException caught in clear().", hibernateException);
			throw new DatabaseException("HibernateException caught in clear().", hibernateException);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> findByCriteria(Criterion... criterion) {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		try {
			for (Criterion c : criterion) {
				crit.add(c);
			}
		}
		catch (HibernateException hibernateException) {
			LOG.error("HibernateException caught in findByCriteria().", hibernateException);
			throw new DatabaseException("HibernateException caught in findByCriteria().", hibernateException);
		}
		return crit.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> findByKeyValue(Class<T> dataClass, Map<String, Object> queries) {
		Criteria criteria = getSession().createCriteria(dataClass);
		try {
			for (Entry<String, Object> query : queries.entrySet()) {
				criteria.add(Restrictions.eq(query.getKey(), query.getValue()));
			}
		}
		catch (HibernateException hibernateException) {
			LOG.error("HibernateException caught in findByKeyValue().", hibernateException);
			throw new DatabaseException("HibernateException caught in findByKeyValue().", hibernateException);
		}
		return criteria.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> findByColumn(Class<T> dataClass, String column, Object value) {
		Criteria criteria = getSession().createCriteria(dataClass);
		try {
			criteria.add(Restrictions.eq(column, value));
		}
		catch (HibernateException hibernateException) {
			LOG.error("HibernateException caught in findByColumn().", hibernateException);
			throw new DatabaseException("HibernateException caught in findByColumn().", hibernateException);
		}
		return criteria.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> findByColumnForMultipleValues(Class<T> dataClass, String column, List<?> values) {
		Criteria criteria = getSession().createCriteria(dataClass);
		try {
			criteria.add(Restrictions.in(column, values));
		}
		catch (HibernateException hibernateException) {
			LOG.error("HibernateException caught in findByColumnForMultipleValues().", hibernateException);
			throw new DatabaseException("HibernateException caught in findByColumnForMultipleValues().", hibernateException);
		}
		return criteria.list();
	}

	@Override
	public long findNumberOfRows(Class<T> dataClass) {
		try {
			return (long) getSession().createCriteria(dataClass).setProjection(Projections.rowCount()).uniqueResult();
		}
		catch (HibernateException hibernateException) {
			LOG.error("HibernateException caught in findNumberOfRows().", hibernateException);
			throw new DatabaseException("HibernateException caught in findNumberOfRows().", hibernateException);
		}
	}

	@Override
	public long findNumberOfRowsByKeyValue(Class<T> dataClass, Map<String, Object> queries) {
		Criteria criteria = getSession().createCriteria(dataClass);
		try {
			for (Entry<String, Object> query : queries.entrySet()) {
				criteria.add(Restrictions.eq(query.getKey(), query.getValue()));
			}
		}
		catch (HibernateException hibernateException) {
			LOG.error("HibernateException caught in findByKeyValue().", hibernateException);
			throw new DatabaseException("HibernateException caught in findByKeyValue().", hibernateException);
		}
		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}

	@Override
	public void merge(T entity) {

		try {
			getSession().merge(entity);
		}
		catch (HibernateException hibernateException) {
			LOG.error("HibernateException caught in merge().", hibernateException);
			throw new DatabaseException("HibernateException caught in merge().", hibernateException);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<T> findAllActive(Class<T> entityClass) {
		try {
			Criteria crit = getSession().createCriteria(entityClass);
			crit.add(Restrictions.eq(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE));
			return crit.list();
		}
		catch (HibernateException hibernateException) {
			LOG.error("HibernateException caught in findAllActive().", hibernateException);
			throw new DatabaseException("HibernateException caught in findAllActive().", hibernateException);
		}
	}

}
// JIRA: SS-8: By RM05: EOC