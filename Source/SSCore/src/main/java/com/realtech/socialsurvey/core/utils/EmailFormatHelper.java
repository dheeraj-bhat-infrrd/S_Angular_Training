package com.realtech.socialsurvey.core.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.WordUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailFormatHelper {
	private static final Logger LOG = LoggerFactory.getLogger(EmailFormatHelper.class);
	
	private static final String PARAM_PATTERN_REGEX = "\\[(.*?)\\]";
	private static final String PARAM_PATTERN = "%s";
	private static final String PARAM_OPEN = "[";
	private static final String PARAM_CLOSE = "]";

	public String buildAgentSignature(String agentName, String agentPhone, String agentTitle, String companyName) {
		LOG.info("Formatting Individual Signature for email");
		StringBuilder agentDetail = new StringBuilder();
		if (agentName != null && !agentName.isEmpty()) {
            agentDetail.append(agentName).append("<br />");
        }
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

	public String replaceEmailBodyWithParams(String mailBody, List<String> paramOrder) {
		LOG.info("Replacing Default String with Email Params");
		if (paramOrder != null && !paramOrder.isEmpty()) {
			for (String replacementArg : paramOrder) {
				mailBody = mailBody.replaceFirst(PARAM_PATTERN, PARAM_OPEN + replacementArg + PARAM_CLOSE);
			}
		}
		return mailBody;
	}

	public String replaceEmailBodyParamsWithDefaultValue(String mailBody, List<String> paramOrder) {
		LOG.info("Replacing Email Params with Default String");
		Pattern pattern = Pattern.compile(PARAM_PATTERN_REGEX);
		Matcher matcher = pattern.matcher(mailBody);
		while (matcher.find()) {
			paramOrder.add(matcher.group(1));
		}
		mailBody = mailBody.replaceAll(PARAM_PATTERN_REGEX, PARAM_PATTERN);
		return mailBody;
	}
	
	/**
	 * Converts email html format to txt format
	 * @param htmlFormat
	 */
	public String getEmailTextFormat(String htmlFormat){
		LOG.debug("Converting html to text format");
		String textFormat = null;
		if(htmlFormat != null && !htmlFormat.isEmpty()){
			Document document = Jsoup.parse(htmlFormat);
			textFormat = document.body().text();
		}
		return textFormat;
	}
	
	public String getCustomerDisplayNameForEmail(String custFirstName, String custLastName) {
		 String customerName = custFirstName;
        if(custLastName != null && custLastName != ""){
        	customerName += " " + custLastName;
        }
        
        String[] custNameArray = customerName.split(" ");
        String custDisplayName = custNameArray[0];
        if(custDisplayName.length() > 1){
        	custDisplayName += " " + custNameArray[1].substring(0, 1);
        }
        return WordUtils.capitalize(custDisplayName);
	}
}