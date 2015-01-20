package com.realtech.socialsurvey.repositories.mongo;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Component;
import com.mongodb.BasicDBObject;
import com.realtech.socialsurvey.entities.mongo.Person;

@Component
public class RepositoryPerson {
	@Autowired
	MongoTemplate mongoTemplate;

	
	// Simple find query to fetch all results.
	public void logAllPeople() {
		List<Person> results = mongoTemplate.findAll(Person.class);
		System.out.println(results);
	}

	// Simple find query by criteria.
	public void log41YearsOldPerson() {
		List<Person> results = mongoTemplate.find(new BasicQuery(new BasicDBObject("age", 51)), Person.class);
		for (Person p : results) {
			System.out.println(p.getAge());
			System.out.println(p.getName());
			System.out.println(p.getAddress());
			System.out.println(p.getPersonId());
		}
	}

	// To perform aggregation operations on a collection. 
	public void aggregate() {
		
		// mongoTemplate.aggregate(aggregation, collectionName, outputType)
	}

	
	// Insert a new row into the collection.
	// It will create a new collection if their is no collection with the specified name.
	public void insert() {
		double age = Math.ceil(Math.random() * 100);
		Person p = new Person("Ritwik", (int) age);
		mongoTemplate.insert(p);
	}

	// Create a new collection.
	public void createPersonCollection() {
		if (!mongoTemplate.collectionExists(Person.class)) {
			mongoTemplate.createCollection(Person.class);
		}
	}

	// Drop an existing collection.
	public void dropPersonCollection() {
		if (mongoTemplate.collectionExists(Person.class)) {

			mongoTemplate.dropCollection(Person.class);
		}
	}

}