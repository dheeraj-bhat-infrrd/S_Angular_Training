package com.realtech.socialsurvey.api.transformers;

import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.api.models.CompanyProfile;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyCompositeEntity;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.ContactNumberSettings;
import com.realtech.socialsurvey.core.entities.Country;
import com.realtech.socialsurvey.core.entities.Location;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;


@Component
public class CompanyProfileTransformer implements Transformer<CompanyProfile, CompanyCompositeEntity, CompanyProfile>
{
    public CompanyCompositeEntity transformApiRequestToDomainObject( CompanyProfile request, Object... objects )
    {
        CompanyCompositeEntity companyProfile = new CompanyCompositeEntity();
        Company company = null;
        OrganizationUnitSettings unitSettings = null;
        if ( request != null ) {

            if ( objects[0] != null && objects[0] instanceof Company ) {
                company = (Company) objects[0];
                company.setCompany( request.getCompanyName() );
                company.setVerticalsMaster( request.getIndustry() );
                companyProfile.setCompany( company );
            }

            if ( objects[1] != null && objects[1] instanceof OrganizationUnitSettings ) {
                unitSettings = (OrganizationUnitSettings) objects[1];
                ContactDetailsSettings contactDetails = unitSettings.getContact_details();
                if ( contactDetails == null ) {
                    contactDetails = new ContactDetailsSettings();
                }
                contactDetails.setName( request.getCompanyName() );
                contactDetails.setAddress( request.getAddress() );
                contactDetails.setAddress1( request.getAddress() );
                contactDetails.setCity( request.getCity() );
                contactDetails.setState( request.getState() );
                contactDetails.setZipcode( request.getZip() );
                if ( request.getLocation() != null && request.getLocation().getCountry() != null ) {
                    contactDetails.setCountry( request.getLocation().getName() );
                    contactDetails.setCountryCode( request.getLocation().getCountry().getCode() );
                }

                if ( contactDetails.getContact_numbers() == null ) {
                    contactDetails.setContact_numbers( new ContactNumberSettings() );
                }
                contactDetails.getContact_numbers().setPhone1( request.getOfficePhone() );

                unitSettings.setContact_details( contactDetails );
                unitSettings.setLogo( request.getCompanyLogo() );
                unitSettings.setLogoThumbnail( request.getCompanyLogo() );
                unitSettings.setLogoImageProcessed( false );
                unitSettings.setVertical( request.getIndustry().getVerticalName() );
                companyProfile.setCompanySettings( unitSettings );
            }
        }
        return companyProfile;
    }


    public CompanyProfile transformDomainObjectToApiResponse( CompanyCompositeEntity companyProfile )
    {
        CompanyProfile response = new CompanyProfile();
        if ( companyProfile != null ) {
            if ( companyProfile.getCompany() != null ) {
                response.setCompanyName( companyProfile.getCompany().getCompany() );
                response.setIndustry( companyProfile.getCompany().getVerticalsMaster() );
            }

            if ( companyProfile.getCompanySettings() != null ) {
                response.setCompanyLogo( companyProfile.getCompanySettings().getLogo() );
                if ( companyProfile.getCompanySettings().getContact_details() != null ) {
                    response.setAddress( companyProfile.getCompanySettings().getContact_details().getAddress() );
                    response.setCity( companyProfile.getCompanySettings().getContact_details().getCity() );
                    response.setState( companyProfile.getCompanySettings().getContact_details().getState() );
                    response.setZip( companyProfile.getCompanySettings().getContact_details().getZipcode() );
                    if ( companyProfile.getCompanySettings().getContact_details().getContact_numbers() != null ) {
                        response.setOfficePhone(
                            companyProfile.getCompanySettings().getContact_details().getContact_numbers().getPhone1() );
                    }
                    Location location = new Location();
                    Country country = new Country();
                    country.setCode( companyProfile.getCompanySettings().getContact_details().getCountryCode() );
                    location.setCountry( country );
                    location.setName( companyProfile.getCompanySettings().getContact_details().getCountry() );
                    response.setLocation( location );
                }
            }
        }
        return response;
    }
}
