package com.realtech.socialsurvey.core.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Criterion;


public interface GenericReportingDao<T, ID extends Serializable>{

    public List<T> findAll(Class<T> entityClass);
    
    public T saveOrUpdate(T entity);

    public T save(T entity);

    public void update(T entity);

    public List<T> findByExample(T exampleInstance, String[] excludeProperty);

    public void flush();

    public void clear();

    public List<T> findByCriteria(Class<T> dataClass,Criterion... criterion);

    public T findById(Class<T> table, ID id);

    public List<T> findByKeyValue(Class<T> dataClass, Map<String, Object> queries);

    public List<T> findByColumn(Class<T> dataClass, String column, Object value);

    public List<T> findByColumnForMultipleValues(Class<T> dataClass, String column, List<?> values);

    public long findNumberOfRows(Class<T> dataClass);

    public long findNumberOfRowsByKeyValue(Class<T> dataClass, Map<String, Object> queries);

    public List<T> findAllActive(Class<T> entityClass);
    
    public List<T> findProjectionsByKeyValue(Class<T> dataClass,List<String> columnNames,Map<String, Object> queries);

    public List<T> findByKeyValueAscending(Class<T> dataClass, Map<String, Object> queries, String ascendingColumn);

    public List<T> findByKeyValueAscendingWithAlias(Class<T> dataClass,
            Map<String, Object> queries, String ascendingColumn, String alias);

    public List<T> findProjectionsAscOrderByKeyValue(Class<T> dataClass, List<String> columnNames, Map<String, Object> queries, String columnToOrder);

    public List<T> findProjectionsByKeyValue( Class<T> dataClass, List<String> columnNames, Map<String, Object> queries,
        String orderColumnName );

    public List<T> findByKeyValueAscendingWithAlias( Class<T> dataClass, Map<String, Object> queries, List<String> ascendingColumns,
        String alias );

    public void saveAll(List<T> entityList);

	public void deleteAll(List<T> entityList);
	public void delete(T entity);
	
	List<T> findByKeyValueInBatch(Class<T> dataClass, Map<String, Object> queries, int startIndex, int batchSize);

    List<T> executeNativeQuery(Class<T> dataClass, Map<String, Object> queries, String nativeQuery);
}
