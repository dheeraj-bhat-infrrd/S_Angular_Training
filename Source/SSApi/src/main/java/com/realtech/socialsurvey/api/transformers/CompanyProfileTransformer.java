package com.realtech.socialsurvey.api.transformers;

import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.api.models.request.CompanyProfileRequest;
import com.realtech.socialsurvey.api.models.response.CompanyProfileResponse;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.entities.api.CompanyProfile;
import com.realtech.socialsurvey.core.entities.api.Country;
import com.realtech.socialsurvey.core.entities.api.Phone;


@Component
public class CompanyProfileTransformer implements Transformer<CompanyProfileRequest, CompanyProfile, CompanyProfileResponse>
{
    public CompanyProfile transformApiRequestToDomainObject( CompanyProfileRequest request )
    {
        CompanyProfile companyProfile = new CompanyProfile();
        if ( request != null ) {
            companyProfile.setAddress( request.getAddress() );
            companyProfile.setCity( request.getCity() );
            companyProfile.setCompanyLogo( request.getCompanyLogo() );
            companyProfile.setCompanyName( request.getCompanyName() );
            companyProfile.setState( request.getState() );
            companyProfile.setZip( request.getZip() );

            if ( request.getCountry() != null ) {
                Country country = new Country();
                country.setCountryCode( request.getCountry().getCountryCode() );
                country.setCountryName( request.getCountry().getCountryName() );
                companyProfile.setCountry( country );
            }

            if ( request.getIndustry() != null ) {
                VerticalsMaster industry = new VerticalsMaster();
                industry.setVerticalsMasterId( request.getIndustry().getId() );
                industry.setPriorityOrder( request.getIndustry().getPriorityOrder() );
                industry.setVerticalName( request.getIndustry().getVertical() );
                companyProfile.setIndustry( industry );
            }

            if ( request.getOfficePhone() != null ) {
                Phone officePhone = new Phone();
                officePhone.setCountryCode( request.getOfficePhone().getCountryCode() );
                officePhone.setExtension( request.getOfficePhone().getExtension() );
                officePhone.setNumber( request.getOfficePhone().getNumber() );
                companyProfile.setOfficePhone( officePhone );
            }
        }
        return companyProfile;
    }


    public CompanyProfileResponse transformDomainObjectToApiResponse( CompanyProfile companyProfile )
    {
        CompanyProfileResponse response = new CompanyProfileResponse();
        if ( companyProfile != null ) {
            response.setAddress( companyProfile.getAddress() );
            response.setCity( companyProfile.getCity() );
            response.setCompanyLogo( companyProfile.getCompanyLogo() );
            response.setCompanyName( companyProfile.getCompanyName() );
            response.setState( companyProfile.getState() );
            response.setZip( companyProfile.getZip() );

            if ( companyProfile.getCountry() != null ) {
                com.realtech.socialsurvey.api.models.Country country = new com.realtech.socialsurvey.api.models.Country();
                country.setCountryCode( companyProfile.getCountry().getCountryCode() );
                country.setCountryName( companyProfile.getCountry().getCountryName() );
                response.setCountry( country );
            }

            if ( companyProfile.getIndustry() != null ) {
                com.realtech.socialsurvey.api.models.Industry industry = new com.realtech.socialsurvey.api.models.Industry();
                industry.setId( companyProfile.getIndustry().getVerticalsMasterId() );
                industry.setPriorityOrder( companyProfile.getIndustry().getPriorityOrder() );
                industry.setVertical( companyProfile.getIndustry().getVerticalName() );
                response.setIndustry( industry );
            }

            if ( companyProfile.getOfficePhone() != null ) {
                com.realtech.socialsurvey.api.models.Phone officePhone = new com.realtech.socialsurvey.api.models.Phone();
                officePhone.setCountryCode( companyProfile.getOfficePhone().getCountryCode() );
                officePhone.setExtension( companyProfile.getOfficePhone().getExtension() );
                officePhone.setNumber( companyProfile.getOfficePhone().getNumber() );
                response.setOfficePhone( officePhone );
            }
        }
        return response;
    }
}
