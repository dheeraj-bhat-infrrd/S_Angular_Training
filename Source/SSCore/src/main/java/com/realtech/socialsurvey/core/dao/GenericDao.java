package com.realtech.socialsurvey.core.dao;

import java.io.Serializable;
import java.util.List;

//JIRA: SS-8: By RM05: BOC

/*This is the base Dao which needs to be implemented by each GenericDao.
 * It contains all the basic CRUD methods required by every Dao. 
 */

	public interface GenericDao<T, ID extends Serializable> {  
		  
	    public T findById(ID id);
	    public List<T> findAll();
	    public T saveOrUpdate(T entity);  
	    public T save(T entity);
	    public void delete(T entity);  
	    public void flush(); 
	
}

//JIRA: SS-8: By RM05: EOC