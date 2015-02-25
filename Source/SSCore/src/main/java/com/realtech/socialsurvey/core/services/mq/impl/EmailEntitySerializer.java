package com.realtech.socialsurvey.core.services.mq.impl;

import kafka.serializer.Decoder;
import kafka.serializer.Encoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realtech.socialsurvey.core.entities.EmailEntity;

/**
 * Custom serializer for Email Entity
 *
 */
public class EmailEntitySerializer implements Encoder<EmailEntity>, Decoder<EmailEntity> {

	@Override
	public EmailEntity fromBytes(byte[] arg0) {
		// TODO Auto-generated method stub
		ObjectMapper mapper = new ObjectMapper();
		return null;
	}

	@Override
	public byte[] toBytes(EmailEntity arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
