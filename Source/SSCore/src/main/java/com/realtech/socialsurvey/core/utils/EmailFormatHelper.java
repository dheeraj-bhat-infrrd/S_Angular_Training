package com.realtech.socialsurvey.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.entities.MailContent;

@Component
public class EmailFormatHelper {
	private static final Logger LOG = LoggerFactory.getLogger(EmailFormatHelper.class);
	
	private static final String PARAM_PATTERN_REGEX = "\\[(.*?)\\]";
	private static final String PARAM_PATTERN = "%s";
	private static final String PARAM_OPEN = "[";
	private static final String PARAM_CLOSE = "]";

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
		LOG.info("Replacing Default String with Email Params");
		String mailBody = takeSurvey.getMail_body();
		if (takeSurvey.getParam_order() != null && !takeSurvey.getParam_order().isEmpty()) {
			for (String replacementArg : takeSurvey.getParam_order()) {
				mailBody = mailBody.replaceFirst(PARAM_PATTERN, PARAM_OPEN + replacementArg + PARAM_CLOSE);
			}
		}
		return mailBody;
	}

	public String replaceEmailBodyParamsWithDefaultValue(String mailBody) {
		LOG.info("Replacing Email Params with Default String");
		List<String> paramOrder = new ArrayList<String>();
		Pattern pattern = Pattern.compile(PARAM_PATTERN_REGEX);
		Matcher matcher = pattern.matcher(mailBody);
		while (matcher.find()) {
			paramOrder.add(matcher.group(1));
		}
		mailBody = mailBody.replaceAll(PARAM_PATTERN_REGEX, PARAM_PATTERN);
		return mailBody;
	}
}