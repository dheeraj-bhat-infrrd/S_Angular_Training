package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the REMOVED_USER database table.
 * 
 */
@Entity
@Table(name="REMOVED_USER")
@NamedQuery(name="RemovedUser.findAll", query="SELECT r FROM RemovedUser r")
public class RemovedUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="REMOVED_USER_ID")
	private int removedUserId;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_ON")
	private Timestamp createdOn;

	//bi-directional many-to-one association to User
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="USER_ID")
	private User user;

	//bi-directional many-to-one association to Company
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="COMPANY_ID")
	private Company company;

	public RemovedUser() {
	}

	public int getRemovedUserId() {
		return this.removedUserId;
	}

	public void setRemovedUserId(int removedUserId) {
		this.removedUserId = removedUserId;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}