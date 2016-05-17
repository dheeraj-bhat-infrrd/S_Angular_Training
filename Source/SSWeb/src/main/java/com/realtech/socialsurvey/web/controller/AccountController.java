package com.realtech.socialsurvey.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;

import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realtech.socialsurvey.web.api.SSApiIntegration;
import com.realtech.socialsurvey.web.api.builder.SSApiIntergrationBuilder;
import com.realtech.socialsurvey.web.api.entities.AccountRegistrationAPIRequest;
import com.realtech.socialsurvey.web.api.entities.CaptchaAPIRequest;
import com.realtech.socialsurvey.web.entities.CompanyProfile;
import com.realtech.socialsurvey.web.entities.PersonalProfile;
import com.realtech.socialsurvey.web.ui.entities.AccountRegistration;
import com.realtech.socialsurvey.web.util.RequestUtils;

/**
 * Typically used for account registration. The controller should not call
 * services directly but should call APIs
 */
@Controller
public class AccountController {
	private static final Logger LOG = LoggerFactory
			.getLogger(AccountController.class);

	@Autowired
	private SSApiIntergrationBuilder apiBuilder;

	@Autowired
	private RequestUtils requestUtils;

	// LinkedIn
	@Value("${LINKED_IN_API_KEY}")
	private String linkedInApiKey;

	@Value("${LINKED_IN_REDIRECT_URI}")
	private String linkedinRedirectUri;

	@Value("${LINKED_IN_AUTH_URI}")
	private String linkedinAuthUri;

	@Value("${LINKED_IN_SCOPE}")
	private String linkedinScope;

	@RequestMapping(value = "/registeraccount/initiateregistration", method = RequestMethod.POST)
	@ResponseBody
	public String initateAccountRegistration(
			@RequestBody AccountRegistration account, HttpServletRequest request) {
		LOG.info("Registering user");
		String responseString = null;
		SSApiIntegration api = apiBuilder.getIntegrationApi();

		// validate captcha
		CaptchaAPIRequest captchaRequest = new CaptchaAPIRequest();
		captchaRequest.setRemoteAddress(request.getRemoteAddr());
		captchaRequest.setCaptchaResponse(account.getCaptchaResponse());
		api.validateCaptcha(captchaRequest);

		// initiate registration
		AccountRegistrationAPIRequest accountRequest = new AccountRegistrationAPIRequest();
		accountRequest.setFirstName(account.getFirstName());
		accountRequest.setLastName(account.getLastName());
		accountRequest.setCompanyName(account.getCompanyName());
		accountRequest.setEmail(account.getEmail());
		accountRequest.setPhone(account.getPhone());
		Response response = api.initateRegistration(accountRequest);
		responseString = new String(
				((TypedByteArray) response.getBody()).getBytes());
		return responseString;
	}

	@RequestMapping(value = "/registeraccount/getuserprofile", method = RequestMethod.GET)
	@ResponseBody
	public String getUserProfile(@QueryParam("userId") String userId) {
		String responseString = null;
		SSApiIntegration api = apiBuilder.getIntegrationApi();
		Response response = api.getUserProfile(userId);
		responseString = new String(
				((TypedByteArray) response.getBody()).getBytes());
		return responseString;
	}

	@RequestMapping(value = "/registeraccount/updateuserprofile", method = RequestMethod.PUT)
	@ResponseBody
	public String updateUserProfile(@QueryParam("userId") String userId,
			@QueryParam("stage") String stage,
			@RequestBody PersonalProfile personalProfile) {
		String responseString = null;
		SSApiIntegration api = apiBuilder.getIntegrationApi();
		Response response = api.updateUserProfile(personalProfile, userId);
		responseString = new String(
				((TypedByteArray) response.getBody()).getBytes());
		if (response.getStatus() == HttpStatus.SC_OK) {
			response = api.updateUserProfileStage(userId, stage);
			responseString = new String(
					((TypedByteArray) response.getBody()).getBytes());
		}
		return responseString;
	}

	@RequestMapping(value = "/registeraccount/getcompanyprofile", method = RequestMethod.GET)
	@ResponseBody
	public String getCompanyProfile(@QueryParam("companyId") String companyId) {
		String responseString = null;
		SSApiIntegration api = apiBuilder.getIntegrationApi();
		Response response = api.getCompanyProfile(companyId);
		responseString = new String(
				((TypedByteArray) response.getBody()).getBytes());
		return responseString;
	}

	@RequestMapping(value = "/registeraccount/updatecompanyprofile", method = RequestMethod.PUT)
	@ResponseBody
	public String updateCompanyProfile(
			@QueryParam("companyId") String companyId,
			@QueryParam("stage") String stage,
			@RequestBody CompanyProfile companyProfile) {
		String responseString = null;
		SSApiIntegration api = apiBuilder.getIntegrationApi();
		Response response = api.updateCompanyProfile(companyProfile, companyId);
		responseString = new String(
				((TypedByteArray) response.getBody()).getBytes());
		if (response.getStatus() == HttpStatus.SC_OK) {
			response = api.updateCompanyProfileStage(companyId, stage);
			responseString = new String(
					((TypedByteArray) response.getBody()).getBytes());
		}
		return responseString;
	}

	@RequestMapping(value = "/registeraccount/getverticals", method = RequestMethod.GET)
	@ResponseBody
	public String getVerticals() {
		String responseString = null;
		SSApiIntegration api = apiBuilder.getIntegrationApi();
		Response response = api.getVerticals();
		responseString = new String(
				((TypedByteArray) response.getBody()).getBytes());
		return responseString;
	}

	@RequestMapping(value = "/registeraccount/getpaymentplans", method = RequestMethod.GET)
	@ResponseBody
	public String getPaymentPlans() {
		String responseString = null;
		SSApiIntegration api = apiBuilder.getIntegrationApi();
		Response response = api.getPaymentPlans();
		responseString = new String(
				((TypedByteArray) response.getBody()).getBytes());
		return responseString;
	}

	// TODO: To be moved from register account to more generic
	@RequestMapping(value = "/registeraccount/{organizationunit}/initlinkedinconnection", method = RequestMethod.POST)
	@ResponseBody
	public String initiateLinkedInConnection(@RequestBody String userId,
			@PathVariable("organizationunit") String organizationunit,
			HttpServletRequest request) throws JsonProcessingException {
		LOG.debug("Creating linkedin url");
		String serverBaseUrl = requestUtils.getRequestServerName(request);
		StringBuilder linkedInAuth = new StringBuilder(linkedinAuthUri)
				.append("?response_type=").append("code").append("&client_id=")
				.append(linkedInApiKey).append("&redirect_uri=")
				.append(serverBaseUrl).append(linkedinRedirectUri)
				.append("?unit=").append(organizationunit).append("&id=")
				.append(userId).append("&state=").append("SOCIALSURVEY")
				.append("&scope=").append(linkedinScope);
		ObjectMapper mapper = new ObjectMapper();
        String jsonStr = mapper.writeValueAsString(linkedInAuth.toString());
        return jsonStr;
	}

	// TODO: To be moved from register account to more generic
	@RequestMapping(value = "/registeraccount/connectlinkedin", method = RequestMethod.GET)
	@ResponseBody
	public String connectToLinkedIn(HttpServletRequest request) {
		LOG.info("Connecting to linkedin");
		return null;
	}
}
