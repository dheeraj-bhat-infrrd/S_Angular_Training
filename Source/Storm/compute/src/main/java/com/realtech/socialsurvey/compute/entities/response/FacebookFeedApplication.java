package com.realtech.socialsurvey.compute.entities.response;

import java.io.Serializable;

public class FacebookFeedApplication  implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private String category;
    private String name;
    private String namespace;
	private String id;
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
