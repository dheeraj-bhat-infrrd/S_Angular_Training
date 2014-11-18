package com.realtech.socialsurvey.core.dao.impl;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
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
        if (session == null)  
            session=sessionFactory.openSession();
        return session;  
    }  
  
    public Class<T> getPersistentClass() {  
        return persistentClass;  
    }  

    @SuppressWarnings("unchecked")
    public T findById(ID id) {  
        T entity;  
        entity = (T) getSession().load(getPersistentClass(), id);
  
        return entity;  
    }  
  
    public List<T> findAll() {  
        return findByCriteria();  
    }  
  
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
  
    public T saveOrUpdate(T entity) {  
        getSession().saveOrUpdate(entity);
        return entity;  
    }  
  
    @Transactional
    public T save(T entity) {
        getSession().save(entity);
        return entity;  
    } 
    
    public void delete(T entity) {
        getSession().delete(entity);  
    }  
  
    public void flush() {  
        getSession().flush();  
    }  
  
    public void clear() {  
        getSession().clear();  
    }  
  
    /** 
     * Use this inside subclasses as a convenience method.
     */  
    @SuppressWarnings("unchecked")  
    protected List<T> findByCriteria(Criterion... criterion) {  
        Criteria crit = getSession().createCriteria(getPersistentClass());  
        for (Criterion c : criterion) {  
            crit.add(c);  
        }  
        return crit.list();  
   }
}
//JIRA: SS-8: By RM05: EOC