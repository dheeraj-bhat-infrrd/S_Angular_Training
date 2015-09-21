package com.realtech.socialsurvey.core.starter;

import java.util.List;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;

/**
 * One time class to synchronize the hierarchy settings
 */
public class HierarchySettingsCorrector extends QuartzJobBean {

	private static final Logger LOG = LoggerFactory.getLogger(HierarchySettingsCorrector.class);
	
	private OrganizationManagementService organizationManagementService;

	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		// get a list of all the companies and find all the values set
		List<OrganizationUnitSettings> companySettings = organizationManagementService.getCompaniesByKeyValueFromMongo("", -1,
				CommonConstants.STATUS_ACTIVE);
		LOG.debug("Got "+companySettings.size()+" companies");
		for(OrganizationUnitSettings companySetting: companySettings){
			long setterValue = 0l;
			String lockValue = "0";
			// get a the company id and get the company from SQL
			LOG.debug("Getting details of company: "+companySetting.getIden());
			Company company = organizationManagementService.getCompanyById(companySetting.getIden());
			LOG.debug("Checking for all the values that can be set for "+company.getCompany());
			if(companySetting.getLogo() != null){
				LOG.debug("Logo is set");
				setterValue += SettingsForApplication.LOGO.getOrder()*1;
				// lock the logo
				lockValue = "1";
			}else{
				LOG.debug("Logo is not set");
			}
			if(companySetting.getContact_details() != null){
				if(companySetting.getContact_details().getAddress() != null){
					LOG.debug("Address is set");
					setterValue += SettingsForApplication.ADDRESS.getOrder()*1;
				}else{
					LOG.debug("Address is not set");
				}
				if(companySetting.getContact_details().getContact_numbers() != null){
					LOG.debug("Contact number is set");
					setterValue += SettingsForApplication.PHONE.getOrder()*1;
				}else{
					LOG.debug("Contact number is not set");
				}
				// skipping location
				if(companySetting.getContact_details().getWeb_addresses() != null){
					LOG.debug("Web address is set");
					setterValue += SettingsForApplication.WEB_ADDRESS_WORK.getOrder()*1;
				}else{
					LOG.debug("Web address is not set");
				}
				if(companySetting.getContact_details().getAbout_me() != null){
					LOG.debug("About me is set");
					setterValue += SettingsForApplication.ABOUT_ME.getOrder()*1;
				}else{
					LOG.debug("About me is not set");
				}
				if(companySetting.getContact_details().getMail_ids() != null && companySetting.getContact_details().getMail_ids().getWork() != null){
					LOG.debug("Work email id is set");
					setterValue += SettingsForApplication.EMAIL_ID_WORK.getOrder()*1;
				}else{
					LOG.debug("Work email id is not set");
				}
			}
			if(companySetting.getSocialMediaTokens() != null){
				if(companySetting.getSocialMediaTokens().getFacebookToken() != null){
					LOG.debug("Facebook is set");
					setterValue += SettingsForApplication.FACEBOOK.getOrder()*1;
				}else{
					LOG.debug("Facebook is not set");
				}
				if(companySetting.getSocialMediaTokens().getTwitterToken() != null){
					LOG.debug("Twitter is set");
					setterValue += SettingsForApplication.TWITTER.getOrder()*1;
				}else{
					LOG.debug("Twitter is not set");
				}
				if(companySetting.getSocialMediaTokens().getLinkedInToken() != null){
					LOG.debug("Linkedin is set");
					setterValue += SettingsForApplication.LINKED_IN.getOrder()*1;
				}else{
					LOG.debug("Linkedin is not set");
				}
				if(companySetting.getSocialMediaTokens().getGoogleToken() != null){
					LOG.debug("Google+ is set");
					setterValue += SettingsForApplication.GOOGLE_PLUS.getOrder()*1;
				}else{
					LOG.debug("Google+ is not set");
				}
				if(companySetting.getSocialMediaTokens().getYelpToken() != null){
					LOG.debug("Yelp is set");
					setterValue += SettingsForApplication.YELP.getOrder()*1;
				}else{
					LOG.debug("Yelp is not set");
				}
				if(companySetting.getSocialMediaTokens().getZillowToken() != null){
					LOG.debug("Zillow is set");
					setterValue += SettingsForApplication.ZILLOW.getOrder()*1;
				}else{
					LOG.debug("Zillow is not set");
				}
				if(companySetting.getSocialMediaTokens().getRealtorToken() != null){
					LOG.debug("Realtor is set");
					setterValue += SettingsForApplication.REALTOR.getOrder()*1;
				}else{
					LOG.debug("Realtor is not set");
				}
				if(companySetting.getSocialMediaTokens().getLendingTreeToken() != null){
					LOG.debug("Lending tree is set");
					setterValue += SettingsForApplication.LENDING_TREE.getOrder()*1;
				}else{
					LOG.debug("Lending tree is not set");
				}
			}
			LOG.debug("Final Settings setter value : "+setterValue);
			LOG.debug("Final Settings locker value : "+lockValue);
			company.setSettingsSetStatus(String.valueOf(setterValue));
			company.setSettingsLockStatus(lockValue);
			// update the values to company
			organizationManagementService.updateCompany(company);
		}
	}
}
