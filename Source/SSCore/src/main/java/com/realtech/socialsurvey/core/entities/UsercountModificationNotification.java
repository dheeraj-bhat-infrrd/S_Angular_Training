package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the USERCOUNT_MODIFICATION_NOTIFICATION database table.
 * 
 */
@Entity
@Table(name="USERCOUNT_MODIFICATION_NOTIFICATION")
@NamedQueries({
	@NamedQuery(name="UsercountModificationNotification.findAll", query="SELECT u FROM UsercountModificationNotification u"),
	@NamedQuery(name="UsercountModificationNotification.deleteByIdAndStatus", query="DELETE FROM UsercountModificationNotification WHERE usercountModificationNotificationId = ? AND status = ?")
})
public class UsercountModificationNotification implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="USERCOUNT_MODIFICATION_NOTIFICATION_ID")
	private int usercountModificationNotificationId;

	@Column(name="CREATED_ON")
	private Timestamp createdOn;

	@Column(name="MODIFIED_ON")
	private Timestamp modifiedOn;

	@Column(name="STATUS")
	private int status;

	//bi-directional many-to-one association to Company
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="COMPANY_ID")
	private Company company;

	public UsercountModificationNotification() {
	}

	public int getUsercountModificationNotificationId() {
		return this.usercountModificationNotificationId;
	}

	public void setUsercountModificationNotificationId(int usercountModificationNotificationId) {
		this.usercountModificationNotificationId = usercountModificationNotificationId;
	}

	public Timestamp getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public Timestamp getModifiedOn() {
		return this.modifiedOn;
	}

	public void setModifiedOn(Timestamp modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}