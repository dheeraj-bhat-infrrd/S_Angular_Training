package com.realtech.socialsurvey.core.entities;

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

@Entity
@Table(name = "FILE_UPLOAD")
@NamedQuery(name = "FileUpload.findAll", query = "SELECT f FROM FileUpload f")
public class FileUpload {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "FILE_UPLOAD_ID")
	private long fileUploadId;
	@Column(name = "ADMIN_USER_ID")
	private long adminUserId;
	@Column(name = "FILE_NAME")
	private String fileName;
	@Column(name = "UPLOAD_TYPE")
	private int uploadType;
	@Column(name = "STATUS")
	private int status;
	@Column(name = "CREATED_ON")
	private Timestamp createdOn;
	@Column(name = "MODIFIED_ON")
	private Timestamp modifiedOn;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private Company company;

	public long getFileUploadId() {
		return fileUploadId;
	}

	public void setFileUploadId(long fileUploadId) {
		this.fileUploadId = fileUploadId;
	}

	public long getAdminUserId() {
		return adminUserId;
	}

	public void setAdminUserId(long adminUserId) {
		this.adminUserId = adminUserId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getUploadType() {
		return uploadType;
	}

	public void setUploadType(int uploadType) {
		this.uploadType = uploadType;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public Timestamp getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Timestamp modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}