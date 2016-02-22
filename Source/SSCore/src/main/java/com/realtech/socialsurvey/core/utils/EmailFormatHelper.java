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

import com.realtech.socialsurvey.core.exception.InvalidInputException;

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
	
	public String getCustomerDisplayNameForEmail(String custFirstName, String custLastName) throws InvalidInputException {
	    LOG.debug( "method getCustomerDisplayNameForEmail started for first name : " + custFirstName + " and last name : " + custLastName );
		 String customerName = custFirstName;
		 if(custFirstName == null || custFirstName.isEmpty()){
	            throw new InvalidInputException("Invalid parameter: passed parameter custFirstName is null or empty");
	        }
        if(custLastName != null && !custLastName.isEmpty()){
        	customerName += " " + custLastName;
        }
        
        String[] custNameArray = customerName.split(" ");
        String custDisplayName = custNameArray[0];
        if(custNameArray.length > 1){
        	if(custNameArray[1] != null && custNameArray[1].length() >= 1){
        		custDisplayName += " " + custNameArray[1].substring(0, 1) + ".";
        	}
        }
        return WordUtils.capitalize(custDisplayName);
	}


    public String replaceLegends( boolean isSubject, String content, String baseUrl, String logoUrl, String link,
        String custFirstName, String custLastName, String agentName, String agentSignature, String recipientMailId,
        String senderEmail, String companyName, String initiatedDate, String currentYear, String fullAddress, String links )
        throws InvalidInputException
    {
        LOG.info( "Method to replace legends with values called, replaceLegends() started");
        if ( content == null || content.isEmpty() ) {
            LOG.error( "Content passed in replaceLegends is null or empty" );
            throw new InvalidInputException( "Content passed in replaceLegends is null or empty" );
        }

        String customerName = getCustomerDisplayNameForEmail( custFirstName, custLastName );

        content = content.replaceAll( "\\[BaseUrl\\]", "" + baseUrl );
        content = content.replaceAll( "\\[AppBaseUrl\\]", "" + baseUrl );
        content = content.replaceAll( "\\[LogoUrl\\]", "" + logoUrl );
        content = content.replaceAll( "\\[Link\\]", "" + link );
        content = content.replaceAll( "\\[Links\\]", "" + links );
        content = content.replaceAll( "\\[Name\\]", "" + customerName );
        if ( !isSubject ) {
            content = content.replaceFirst( "\\[FirstName\\]", WordUtils.capitalize( custFirstName ) );
        }

        content = content.replaceAll( "\\[FirstName\\]", "" + custFirstName );
        content = content.replaceAll( "\\[AgentName\\]", "" + agentName );
        content = content.replaceAll( "\\[AgentSignature\\]", "" + agentSignature );
        content = content.replaceAll( "\\[RecipientEmail\\]", "" + recipientMailId );
        content = content.replaceAll( "\\[SenderEmail\\]", "" + senderEmail );
        content = content.replaceAll( "\\[CompanyName\\]", "" + companyName );
        content = content.replaceAll( "\\[InitiatedDate\\]", "" + initiatedDate );
        content = content.replaceAll( "\\[CurrentYear\\]", "" + currentYear );
        content = content.replaceAll( "\\[FullAddress\\]", "" + fullAddress );
        content = content.replaceAll( "null", "" );
        LOG.info( "Method to replace legends with values called, replaceLegends() ended");
        return content;
    }
}