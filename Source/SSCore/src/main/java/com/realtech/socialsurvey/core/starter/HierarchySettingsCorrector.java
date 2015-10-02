package com.realtech.socialsurvey.core.starter;

import java.util.List;
import java.util.Set;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;


/**
 * One time class to synchronize the hierarchy settings
 */
public class HierarchySettingsCorrector extends QuartzJobBean
{

    private static final Logger LOG = LoggerFactory.getLogger( HierarchySettingsCorrector.class );

    private OrganizationManagementService organizationManagementService;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        // get a list of all the companies and find all the values set
        Set<Company> companyList = organizationManagementService.getAllCompanies();
        LOG.debug( "Got " + companyList.size() + " companies" );
        for ( Company company : companyList ) {
            OrganizationUnitSettings companySetting = null;
            try {
                companySetting = organizationManagementService.getCompanySettings( company.getCompanyId() );
            } catch ( InvalidInputException e1 ) {
                LOG.error( "Exception caught ", e1 );
            }
            if ( companySetting != null ) {
                processCompany( companySetting );
                try {
                    List<Region> regions = company.getRegions();
                    for ( Region region : regions ) {
                        // get region settings
                        OrganizationUnitSettings regionSetting = organizationManagementService.getRegionSettings( region
                            .getRegionId() );
                        //  processRegion( regionSetting, region );
                    }

                } catch ( InvalidInputException e ) {
                    LOG.error( "Could not get regions for company profile " + companySetting.getProfileName(), e );
                }
            }
        }
    }


    private void processRegion( OrganizationUnitSettings regionSetting, Region region )
    {
        LOG.debug( "Processing region " + region.getRegion() );
        long setterValue = 0l;
        LOG.debug( "Getting details of region: " + region.getRegion() );
        if ( regionSetting.getLogo() != null ) {
            LOG.debug( "Logo is set" );
            setterValue += SettingsForApplication.LOGO.getOrder() * 2;
        } else {
            LOG.debug( "Logo is not set" );
        }
        if ( regionSetting.getContact_details() != null ) {
            if ( regionSetting.getContact_details().getAddress() != null ) {
                LOG.debug( "Address is set" );
                setterValue += SettingsForApplication.ADDRESS.getOrder() * 2;
            } else {
                LOG.debug( "Address is not set" );
            }
            if ( regionSetting.getContact_details().getContact_numbers() != null ) {
                LOG.debug( "Contact number is set" );
                setterValue += SettingsForApplication.PHONE.getOrder() * 2;
            } else {
                LOG.debug( "Contact number is not set" );
            }
            // skipping location
            if ( regionSetting.getContact_details().getWeb_addresses() != null ) {
                LOG.debug( "Web address is set" );
                setterValue += SettingsForApplication.WEB_ADDRESS_WORK.getOrder() * 2;
            } else {
                LOG.debug( "Web address is not set" );
            }
            if ( regionSetting.getContact_details().getAbout_me() != null ) {
                LOG.debug( "About me is set" );
                setterValue += SettingsForApplication.ABOUT_ME.getOrder() * 2;
            } else {
                LOG.debug( "About me is not set" );
            }
            if ( regionSetting.getContact_details().getMail_ids() != null
                && regionSetting.getContact_details().getMail_ids().getWork() != null ) {
                LOG.debug( "Work email id is set" );
                setterValue += SettingsForApplication.EMAIL_ID_WORK.getOrder() * 2;
            } else {
                LOG.debug( "Work email id is not set" );
            }
        }
        if ( regionSetting.getSocialMediaTokens() != null ) {
            if ( regionSetting.getSocialMediaTokens().getFacebookToken() != null ) {
                LOG.debug( "Facebook is set" );
                setterValue += SettingsForApplication.FACEBOOK.getOrder() * 2;
            } else {
                LOG.debug( "Facebook is not set" );
            }
            if ( regionSetting.getSocialMediaTokens().getTwitterToken() != null ) {
                LOG.debug( "Twitter is set" );
                setterValue += SettingsForApplication.TWITTER.getOrder() * 2;
            } else {
                LOG.debug( "Twitter is not set" );
            }
            if ( regionSetting.getSocialMediaTokens().getLinkedInToken() != null ) {
                LOG.debug( "Linkedin is set" );
                setterValue += SettingsForApplication.LINKED_IN.getOrder() * 2;
            } else {
                LOG.debug( "Linkedin is not set" );
            }
            if ( regionSetting.getSocialMediaTokens().getGoogleToken() != null ) {
                LOG.debug( "Google+ is set" );
                setterValue += SettingsForApplication.GOOGLE_PLUS.getOrder() * 2;
            } else {
                LOG.debug( "Google+ is not set" );
            }
            if ( regionSetting.getSocialMediaTokens().getYelpToken() != null ) {
                LOG.debug( "Yelp is set" );
                setterValue += SettingsForApplication.YELP.getOrder() * 2;
            } else {
                LOG.debug( "Yelp is not set" );
            }
            if ( regionSetting.getSocialMediaTokens().getZillowToken() != null ) {
                LOG.debug( "Zillow is set" );
                setterValue += SettingsForApplication.ZILLOW.getOrder() * 2;
            } else {
                LOG.debug( "Zillow is not set" );
            }
            if ( regionSetting.getSocialMediaTokens().getRealtorToken() != null ) {
                LOG.debug( "Realtor is set" );
                setterValue += SettingsForApplication.REALTOR.getOrder() * 2;
            } else {
                LOG.debug( "Realtor is not set" );
            }
            if ( regionSetting.getSocialMediaTokens().getLendingTreeToken() != null ) {
                LOG.debug( "Lending tree is set" );
                setterValue += SettingsForApplication.LENDING_TREE.getOrder() * 2;
            } else {
                LOG.debug( "Lending tree is not set" );
            }
        }
        LOG.debug( "Final Settings setter value : " + setterValue );
        region.setSettingsSetStatus( String.valueOf( setterValue ) );
        // update the values to company
        organizationManagementService.updateRegion( region );
        // get list of branches for region
        try {
            List<Branch> branches = region.getBranches();
            for ( Branch branch : branches ) {
                try {
                    OrganizationUnitSettings branchSetting = organizationManagementService.getBranchSettingsDefault( branch
                        .getBranchId() );
                    processBranch( branchSetting, branch );
                } catch ( NoRecordsFetchedException e ) {
                    LOG.error( "Could not get branches setting for " + branch.getBranch(), e );
                }
            }
        } catch ( InvalidInputException e ) {
            LOG.error( "Could not get branches for region " + region.getRegionId(), e );
        }
    }


    private void processBranch( OrganizationUnitSettings branchSetting, Branch branch )
    {
        LOG.debug( "Updating details for branch " + branch.getBranch() );
        long setterValue = 0l;
        LOG.debug( "Getting details of branch: " + branch.getRegion() );
        if ( branchSetting.getLogo() != null ) {
            LOG.debug( "Logo is set" );
            setterValue += SettingsForApplication.LOGO.getOrder() * 4;
        } else {
            LOG.debug( "Logo is not set" );
        }
        if ( branchSetting.getContact_details() != null ) {
            if ( branchSetting.getContact_details().getAddress() != null ) {
                LOG.debug( "Address is set" );
                setterValue += SettingsForApplication.ADDRESS.getOrder() * 4;
            } else {
                LOG.debug( "Address is not set" );
            }
            if ( branchSetting.getContact_details().getContact_numbers() != null ) {
                LOG.debug( "Contact number is set" );
                setterValue += SettingsForApplication.PHONE.getOrder() * 4;
            } else {
                LOG.debug( "Contact number is not set" );
            }
            // skipping location
            if ( branchSetting.getContact_details().getWeb_addresses() != null ) {
                LOG.debug( "Web address is set" );
                setterValue += SettingsForApplication.WEB_ADDRESS_WORK.getOrder() * 4;
            } else {
                LOG.debug( "Web address is not set" );
            }
            if ( branchSetting.getContact_details().getAbout_me() != null ) {
                LOG.debug( "About me is set" );
                setterValue += SettingsForApplication.ABOUT_ME.getOrder() * 4;
            } else {
                LOG.debug( "About me is not set" );
            }
            if ( branchSetting.getContact_details().getMail_ids() != null
                && branchSetting.getContact_details().getMail_ids().getWork() != null ) {
                LOG.debug( "Work email id is set" );
                setterValue += SettingsForApplication.EMAIL_ID_WORK.getOrder() * 4;
            } else {
                LOG.debug( "Work email id is not set" );
            }
        }
        if ( branchSetting.getSocialMediaTokens() != null ) {
            if ( branchSetting.getSocialMediaTokens().getFacebookToken() != null ) {
                LOG.debug( "Facebook is set" );
                setterValue += SettingsForApplication.FACEBOOK.getOrder() * 4;
            } else {
                LOG.debug( "Facebook is not set" );
            }
            if ( branchSetting.getSocialMediaTokens().getTwitterToken() != null ) {
                LOG.debug( "Twitter is set" );
                setterValue += SettingsForApplication.TWITTER.getOrder() * 4;
            } else {
                LOG.debug( "Twitter is not set" );
            }
            if ( branchSetting.getSocialMediaTokens().getLinkedInToken() != null ) {
                LOG.debug( "Linkedin is set" );
                setterValue += SettingsForApplication.LINKED_IN.getOrder() * 4;
            } else {
                LOG.debug( "Linkedin is not set" );
            }
            if ( branchSetting.getSocialMediaTokens().getGoogleToken() != null ) {
                LOG.debug( "Google+ is set" );
                setterValue += SettingsForApplication.GOOGLE_PLUS.getOrder() * 4;
            } else {
                LOG.debug( "Google+ is not set" );
            }
            if ( branchSetting.getSocialMediaTokens().getYelpToken() != null ) {
                LOG.debug( "Yelp is set" );
                setterValue += SettingsForApplication.YELP.getOrder() * 4;
            } else {
                LOG.debug( "Yelp is not set" );
            }
            if ( branchSetting.getSocialMediaTokens().getZillowToken() != null ) {
                LOG.debug( "Zillow is set" );
                setterValue += SettingsForApplication.ZILLOW.getOrder() * 4;
            } else {
                LOG.debug( "Zillow is not set" );
            }
            if ( branchSetting.getSocialMediaTokens().getRealtorToken() != null ) {
                LOG.debug( "Realtor is set" );
                setterValue += SettingsForApplication.REALTOR.getOrder() * 4;
            } else {
                LOG.debug( "Realtor is not set" );
            }
            if ( branchSetting.getSocialMediaTokens().getLendingTreeToken() != null ) {
                LOG.debug( "Lending tree is set" );
                setterValue += SettingsForApplication.LENDING_TREE.getOrder() * 4;
            } else {
                LOG.debug( "Lending tree is not set" );
            }
        }
        LOG.debug( "Final Settings setter value : " + setterValue );
        branch.setSettingsSetStatus( String.valueOf( setterValue ) );
        // update the values to company
        organizationManagementService.updateBranch( branch );
    }


    private void processCompany( OrganizationUnitSettings companySetting )
    {
        long setterValue = 0l;
        /* String lockValue = "0";*/
        // get a the company id and get the company from SQL
        LOG.debug( "Getting details of company: " + companySetting.getIden() );
        Company company = organizationManagementService.getCompanyById( companySetting.getIden() );
        LOG.debug( "Checking for all the values that can be set for " + company.getCompany() );
        if ( companySetting.getLogo() != null ) {
            LOG.debug( "Logo is set" );
            setterValue += SettingsForApplication.LOGO.getOrder() * 1;
            // lock the logo
            /* lockValue = "1";*/
        } else {
            LOG.debug( "Logo is not set" );
        }
        if ( companySetting.getContact_details() != null ) {
            if ( companySetting.getContact_details().getAddress() != null ) {
                LOG.debug( "Address is set" );
                setterValue += SettingsForApplication.ADDRESS.getOrder() * 1;
            } else {
                LOG.debug( "Address is not set" );
            }
            if ( companySetting.getContact_details().getContact_numbers() != null ) {
                LOG.debug( "Contact number is set" );
                setterValue += SettingsForApplication.PHONE.getOrder() * 1;
            } else {
                LOG.debug( "Contact number is not set" );
            }
            // skipping location
            if ( companySetting.getContact_details().getWeb_addresses() != null ) {
                LOG.debug( "Web address is set" );
                setterValue += SettingsForApplication.WEB_ADDRESS_WORK.getOrder() * 1;
            } else {
                LOG.debug( "Web address is not set" );
            }
            if ( companySetting.getContact_details().getAbout_me() != null ) {
                LOG.debug( "About me is set" );
                setterValue += SettingsForApplication.ABOUT_ME.getOrder() * 1;
            } else {
                LOG.debug( "About me is not set" );
            }
            if ( companySetting.getContact_details().getMail_ids() != null
                && companySetting.getContact_details().getMail_ids().getWork() != null ) {
                LOG.debug( "Work email id is set" );
                setterValue += SettingsForApplication.EMAIL_ID_WORK.getOrder() * 1;
            } else {
                LOG.debug( "Work email id is not set" );
            }
        }
        if ( companySetting.getSocialMediaTokens() != null ) {
            if ( companySetting.getSocialMediaTokens().getFacebookToken() != null ) {
                LOG.debug( "Facebook is set" );
                setterValue += SettingsForApplication.FACEBOOK.getOrder() * 1;
            } else {
                LOG.debug( "Facebook is not set" );
            }
            if ( companySetting.getSocialMediaTokens().getTwitterToken() != null ) {
                LOG.debug( "Twitter is set" );
                setterValue += SettingsForApplication.TWITTER.getOrder() * 1;
            } else {
                LOG.debug( "Twitter is not set" );
            }
            if ( companySetting.getSocialMediaTokens().getLinkedInToken() != null ) {
                LOG.debug( "Linkedin is set" );
                setterValue += SettingsForApplication.LINKED_IN.getOrder() * 1;
            } else {
                LOG.debug( "Linkedin is not set" );
            }
            if ( companySetting.getSocialMediaTokens().getGoogleToken() != null ) {
                LOG.debug( "Google+ is set" );
                setterValue += SettingsForApplication.GOOGLE_PLUS.getOrder() * 1;
            } else {
                LOG.debug( "Google+ is not set" );
            }
            if ( companySetting.getSocialMediaTokens().getYelpToken() != null ) {
                LOG.debug( "Yelp is set" );
                setterValue += SettingsForApplication.YELP.getOrder() * 1;
            } else {
                LOG.debug( "Yelp is not set" );
            }
            if ( companySetting.getSocialMediaTokens().getZillowToken() != null ) {
                LOG.debug( "Zillow is set" );
                setterValue += SettingsForApplication.ZILLOW.getOrder() * 1;
            } else {
                LOG.debug( "Zillow is not set" );
            }
            if ( companySetting.getSocialMediaTokens().getRealtorToken() != null ) {
                LOG.debug( "Realtor is set" );
                setterValue += SettingsForApplication.REALTOR.getOrder() * 1;
            } else {
                LOG.debug( "Realtor is not set" );
            }
            if ( companySetting.getSocialMediaTokens().getLendingTreeToken() != null ) {
                LOG.debug( "Lending tree is set" );
                setterValue += SettingsForApplication.LENDING_TREE.getOrder() * 1;
            } else {
                LOG.debug( "Lending tree is not set" );
            }
        }
        if ( companySetting.getSurvey_settings() != null ) {
            if ( companySetting.getSurvey_settings().getShow_survey_above_score() > 0 ) {
                setterValue += SettingsForApplication.MIN_SCORE.getOrder() * 1;
            }

            setterValue += SettingsForApplication.AUTO_POST_ENABLED.getOrder() * 1;

        }
        LOG.debug( "Final Settings setter value : " + setterValue );
        /*    LOG.debug( "Final Settings locker value : " + lockValue );*/
        company.setSettingsSetStatus( String.valueOf( setterValue ) );
        /* company.setSettingsLockStatus( lockValue );*/
        // update the values to company
        organizationManagementService.updateCompany( company );
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        organizationManagementService = (OrganizationManagementService) jobMap.get( "organizationManagementService" );
    }
}
