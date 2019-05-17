package com.realtech.socialsurvey.core.services.generator.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.services.generator.RebrandlyUrlGenerator;
import com.realtech.socialsurvey.core.vo.RebrandlyVO;
import com.realtech.socialsurvey.core.vo.SocialSurveyDomainVO;

@Component
@DependsOn("rebrandlyRestTemplate")
public class RebrandlyUrlGeneratorImpl implements RebrandlyUrlGenerator {

	private static final Logger LOG = LoggerFactory.getLogger(RebrandlyUrlGeneratorImpl.class);
	
	@Autowired
	@Qualifier( "rebrandlyRestTemplate" )
	RestTemplate rebrandlyRestTemplate;
	
	@Value ( "${REBRANDLY_API_KEY}" )
	private String rebrandlyApiKey;
	
	@Value ( "${REBRANDLY_ENDPOINT_URL}" )
	private String rebrandlyEndpointUrl;
	
	@Value ( "${SURVEY_URL_DOMAIN_NAME}" )
	private String domainName;
	
	@Override
	public RebrandlyVO getShortenedUrl(String surveyUrl) {
		
		LOG.debug( "RebrandlyUrlGeneratorImpl :getShortenedUrl() started for surveyUrl :  {}", surveyUrl );
		RebrandlyVO rebrandlyVO = new RebrandlyVO();
		try {
		    
			rebrandlyVO.setDestination( surveyUrl );
		    SocialSurveyDomainVO domain = new SocialSurveyDomainVO();
		    domain.setFullName( domainName );
		    rebrandlyVO.setDomain( domain );
		    
		    HttpHeaders headers = new HttpHeaders();
		    headers.add( CommonConstants.HEADER_API_KEY, rebrandlyApiKey );
		    HttpEntity<RebrandlyVO> entity = new HttpEntity<>( rebrandlyVO, headers );
		     
		    ResponseEntity<RebrandlyVO> response = rebrandlyRestTemplate.exchange(rebrandlyEndpointUrl, HttpMethod.POST, entity, RebrandlyVO.class);
		    
		    if( response == null || !response.getStatusCode().equals( HttpStatus.OK ) ||
		    		response.getBody() == null || StringUtils.isEmpty( response.getBody().getShortUrl() ) ) {
		    	
		    	rebrandlyVO.setRebrandlyErrorCode( ( response != null ? response.getStatusCodeValue() : null ) );
		    	if( response == null || response.getBody() == null ) {
		    		
		    		rebrandlyVO.setRebrandlyErrorMessage( "Response or response body returned by Rebrand.ly is null" );
		    	}
		    	else if( !response.getStatusCode().equals( HttpStatus.OK ) ) {
		    		
		    		rebrandlyVO.setRebrandlyErrorMessage( "Http Status code returned by Rebrand.ly is " + response.getStatusCodeValue() );
		    	}
		    	else {
		    		
		    		rebrandlyVO.setRebrandlyErrorMessage( "Short url returned by Rebrand.ly is null or empty" );
		    	}
		    	return rebrandlyVO;
		    }
		    return response.getBody();
		}
		catch( Exception exception ) {
			
			LOG.error( "Exception occured while calling Rebrand.ly {}", exception.getMessage() );
			rebrandlyVO.setRebrandlyErrorMessage( "Exception occured while calling Rebrand.ly " + exception );
		}
		LOG.debug( "RebrandlyUrlGeneratorImpl :getShortenedUrl() finished for shortenedUrl :  {}", rebrandlyVO.getShortUrl() );
		return rebrandlyVO;
	}
}
