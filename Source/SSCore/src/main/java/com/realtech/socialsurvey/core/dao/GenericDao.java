package com.realtech.socialsurvey.core.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.hibernate.criterion.Criterion;

// JIRA: SS-8: By RM05: BOC

/*
 * This is the base Dao which needs to be implemented by each GenericDao. It contains all the basic
 * CRUD methods required by every Dao.
 */

public interface GenericDao<T, ID extends Serializable> {

	public List<T> findAll(Class<T> entityClass);

	public T saveOrUpdate(T entity);

	public T save(T entity);

	public void update(T entity);

	public void delete(T entity);

	public List<T> findByExample(T exampleInstance, String[] excludeProperty);

	public void flush();

	public void clear();

	public List<T> findByCriteria(Criterion... criterion);

	public T findById(Class<T> table, ID id);

	public List<T> findByKeyValue(Class<T> dataClass, Map<String, Object> queries);

	public List<T> findByColumn(Class<T> dataClass, String column, Object value);

	public List<T> findByColumnForMultipleValues(Class<T> dataClass, String column, List<?> values);

	public long findNumberOfRows(Class<T> dataClass);

	public long findNumberOfRowsByKeyValue(Class<T> dataClass, Map<String, Object> queries);

	public void merge(T entity);
	
	public List<T> findAllActive(Class<T> entityClass);

}

// JIRA: SS-8: By RM05: EOC