package com.realtech.socialsurvey.core.dao.impl;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.dao.GenericDao;

//JIRA: SS-8: By RM05: BOC

/*This is the base Dao which needs to be extended by each Dao.
 * It contains implementation for basic CRUD methods required by every Dao.
 */
public class GenericDaoImpl<T, ID extends Serializable> implements GenericDao<T, ID> {

	private Class<T> persistentClass;  
	private SessionFactory sessionFactory; 
	private Session session;

	public void setSession(Session s) {  
		this.session = s;  
	}  

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Transactional
	protected Session getSession() {  
		try {
			if (session == null)  
				session=sessionFactory.openSession();
		} catch (HibernateException e) {
			throw e;
		}
		return session;  
	}  

	public Class<T> getPersistentClass() {  
		return persistentClass;  
	}  

	@SuppressWarnings("unchecked")
	public T findById(ID id) {  
		T entity;  
		try {
			entity = (T) getSession().load(getPersistentClass(), id);
		} catch (HibernateException e) {
			throw e;
		}
		return entity;  
	}  

	@Override
	public List<T> findAll() {  
		try{
			return findByCriteria();
		} catch (HibernateException e) {
			throw e;
		}
	}  
	
	@Override
	@SuppressWarnings("unchecked")  
	public List<T> findByExample(T exampleInstance, String[] excludeProperty) {  
		Criteria crit = getSession().createCriteria(getPersistentClass());  
		Example example =  Example.create(exampleInstance);  
		for (String exclude : excludeProperty) {  
			example.excludeProperty(exclude);  
		}  
		crit.add(example);  
		return crit.list();  
	}

	@Override
	public T saveOrUpdate(T entity) {  
		try{
			getSession().saveOrUpdate(entity);
		} catch (HibernateException e) {
			throw e;
		}
		return entity;  
	}  

	@Override
	@Transactional
	public T save(T entity) {
		try{
			getSession().save(entity);
		} catch (HibernateException e) {
			throw e;
		}
		return entity;  
	} 

	@Override
	public void delete(T entity) {
		try{
			getSession().delete(entity);
		} catch (HibernateException e) {
			throw e;
		}
	}  

	@Override
	public void flush() {  
		try{
			getSession().flush();
		} catch (HibernateException e) {
			throw e;
		}
	}  

	@Override
	public void clear() {  
		try{
			getSession().clear();
		} catch (HibernateException e) {
			throw e;
		}
	}

	@Override
	@SuppressWarnings("unchecked")  
	public List<T> findByCriteria(Criterion... criterion) {  
		Criteria crit = getSession().createCriteria(getPersistentClass());  
		for (Criterion c : criterion) {  
			crit.add(c);
		}  
		return crit.list();  
	}
}
//JIRA: SS-8: By RM05: EOC