package com.realtech.socialsurvey.entities.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Person {
	@Id
	private String personId;
	private String name;
	private String address;
	private int age;

	public Person(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(final String personId) {
		this.personId = personId;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(final int age) {
		this.age = age;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(final String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "Person [id=" + personId + ", name=" + name + ", age=" + age + ", Address=" + address + "]";
	}
}