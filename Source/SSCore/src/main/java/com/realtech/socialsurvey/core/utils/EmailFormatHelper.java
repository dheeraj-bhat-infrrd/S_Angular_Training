package com.realtech.socialsurvey.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.MailContent;

@Component
public class EmailFormatHelper {

	private static final Logger LOG = LoggerFactory.getLogger(EmailFormatHelper.class);

	public String buildAgentSignature(String agentPhone, String agentTitle, String companyName) {
		LOG.info("Formatting Individual Signature for email");
		StringBuilder agentDetail = new StringBuilder();
		if (agentPhone != null && !agentPhone.isEmpty()) {
			agentDetail.append(agentPhone).append("<br />");
		}
		if (agentTitle != null && !agentTitle.isEmpty()) {
			agentDetail.append(agentTitle).append("<br />");
		}
		if (companyName != null && !companyName.isEmpty()) {
			agentDetail.append(companyName).append("<br />");
		}
		return agentDetail.toString();
	}
	
	public String replaceEmailBodyWithParams(MailContent takeSurvey) {
		String mailBody = takeSurvey.getMail_body();
		if (takeSurvey.getParam_order() != null && !takeSurvey.getParam_order().isEmpty()) {
			for (String replacementArg : takeSurvey.getParam_order()) {
				mailBody = mailBody.replaceFirst(CommonConstants.PARAM_PATTERN, CommonConstants.PARAM_OPEN + replacementArg
						+ CommonConstants.PARAM_CLOSE);
			}
		}
		return mailBody;
	}
}