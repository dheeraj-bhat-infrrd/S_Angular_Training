package com.realtech.socialsurvey.stream.common;

import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.stream.messages.SmsInfo;

@Component
public class CommonUtil {

	public SmsInfo buildSmsEntity( String smsSid,  String smsStatus, String errorCode, String errorMessage) {
		SmsInfo smsInfo = new SmsInfo();
		smsInfo.setTwilioSmsId( smsSid );
		smsInfo.setSmsStatus( smsStatus );
		smsInfo.setErrorCode( errorCode );
		smsInfo.setErrorMessage( errorMessage );
		return smsInfo;
	}
}
