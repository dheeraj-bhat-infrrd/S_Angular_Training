package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;

@Entity ( "failed_messages")
public class FailedSms extends FailedMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Embedded
	private SmsInfo smsEntity;

	public SmsInfo getSmsEntity() {
		return smsEntity;
	}

	public void setSmsEntity(SmsInfo smsEntity) {
		this.smsEntity = smsEntity;
	}

}
