package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class SEOUrlEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String location;
	private String vertical;
	private String state;
	private Double count;
	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getVertical() {
		return vertical;
	}
	public void setVertical(String vertical) {
		this.vertical = vertical;
	}
	public Double getCount() {
		return count;
	}
	public void setCount(Double count) {
		this.count = count;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
}
