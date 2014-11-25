package com.realtech.socialsurvey.core.dao.impl;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.exception.DatabaseException;

//JIRA: SS-8: By RM05: BOC

/**
 * This is the base Dao which needs to be extended by each Dao. It contains
 * implementation for basic CRUD methods required by every Dao.
 */
@Component
public class GenericDaoImpl<T, ID extends Serializable> implements
		GenericDao<T, ID> {

	private Class<T> persistentClass;
	@Autowired
	private SessionFactory sessionFactory;

	protected Session getSession() {
		Session session;
		try {
			session = sessionFactory.getCurrentSession();
		} catch (HibernateException hibernateException) {
			throw new DatabaseException(
					"HibernateException caught while getting session. ",
					hibernateException);
		}
		return session;
	}

	public Class<T> getPersistentClass() {
		return persistentClass;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T findById(Class<T> entityClass, ID id) {
		T entity;
		try {
			entity = (T) getSession().load(entityClass, id);
		} catch (HibernateException hibernateException) {
			throw new DatabaseException(
					"HibernateException caught in findById(). ",
					hibernateException);
		}
		return entity;
	}

	@Override
	public List<T> findAll() {
		try {
			return findByCriteria();
		} catch (HibernateException hibernateException) {
			throw new DatabaseException(
					"HibernateException caught in findAll().",
					hibernateException);
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
		} catch (HibernateException hibernateException) {
			throw new DatabaseException(
					"HibernateException caught in findByCriteria().",
					hibernateException);
		}
		return crit.list();
	}

	@Override
	public T saveOrUpdate(T entity) {
		try {
			getSession().saveOrUpdate(entity);
		} catch (HibernateException hibernateException) {
			throw new DatabaseException(
					"HibernateException caught in saveOrUpdate().",
					hibernateException);
		}
		return entity;
	}

	@Override
	public T save(T entity) {
		try {
			getSession().save(entity);
		} catch (HibernateException hibernateException) {
			throw new DatabaseException("HibernateException caught in save().",
					hibernateException);
		}
		return entity;
	}

	@Override
	public void delete(T entity) {
		try {
			getSession().delete(entity);
		} catch (HibernateException hibernateException) {
			throw new DatabaseException(
					"HibernateException caught in delete(). ",
					hibernateException);
		}
	}

	@Override
	public void flush() {
		try {
			getSession().flush();
		} catch (HibernateException hibernateException) {
			throw new DatabaseException(
					"HibernateException caught in flush().", hibernateException);
		}
	}

	@Override
	public void clear() {
		try {
			getSession().clear();
		} catch (HibernateException hibernateException) {
			throw new DatabaseException(
					"HibernateException caught in clear().", hibernateException);
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
		} catch (HibernateException hibernateException) {
			throw new DatabaseException(
					"HibernateException caught in findByCriteria().",
					hibernateException);
		}
		return crit.list();
	}
}
// JIRA: SS-8: By RM05: EOC