package com.realtech.socialsurvey.api.transformers;

import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.api.models.VendastaRmAccountVO;
import com.realtech.socialsurvey.core.entities.VendastaRmAccount;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


@Component
public class VendastaRmAccountTransformer implements Transformer<VendastaRmAccountVO, VendastaRmAccount, VendastaRmAccountVO>
{

    @Override
    public VendastaRmAccount transformApiRequestToDomainObject( VendastaRmAccountVO accountVO, Object... objects )
        throws InvalidInputException
    {
        VendastaRmAccount vendastaRmAccount = new VendastaRmAccount();
        vendastaRmAccount.setEntityId( accountVO.getEntityId() );
        vendastaRmAccount.setEntityType( accountVO.getEntityType() );
        vendastaRmAccount.setAddress( accountVO.getAddress() );
        vendastaRmAccount.setCity( accountVO.getCity() );
        vendastaRmAccount.setCompanyName( accountVO.getCompanyName() );
        vendastaRmAccount.setCountry( accountVO.getCountry() );
        vendastaRmAccount.setZip( accountVO.getZip() );
        vendastaRmAccount.setState( accountVO.getState() );
        vendastaRmAccount.setAccountGroupId( accountVO.getAccountGroupId() );
        vendastaRmAccount.setAdminNotes( accountVO.getAdminNotes() );
        vendastaRmAccount.setAlternateEmail( accountVO.getAlternateEmail() );
        vendastaRmAccount.setBillingCode( accountVO.getBillingCode() );
        vendastaRmAccount.setBusinessCategory( accountVO.getBusinessCategory() );
        vendastaRmAccount.setCallTrackingNumber( accountVO.getCallTrackingNumber() );
        vendastaRmAccount.setCellNumber( accountVO.getCellNumber() );
        vendastaRmAccount.setCommonCompanyName( accountVO.getCommonCompanyName() );
        vendastaRmAccount.setCompetitor( accountVO.getCompetitor() );
        vendastaRmAccount.setCustomerIdentifier( accountVO.getCustomerIdentifier() );
        vendastaRmAccount.setEmail( accountVO.getEmail() );
        vendastaRmAccount.setEmployee( accountVO.getEmployee() );
        vendastaRmAccount.setFaxNumber( accountVO.getFaxNumber() );
        vendastaRmAccount.setFirstName( accountVO.getFirstName() );
        vendastaRmAccount.setLastName( accountVO.getLastName() );
        vendastaRmAccount.setMarketId( accountVO.getMarketId() );
        vendastaRmAccount.setSalesPersonEmail( accountVO.getSalesPersonEmail() );
        vendastaRmAccount.setService( accountVO.getService() );
        vendastaRmAccount.setSsoToken( accountVO.getSsoToken() );
        vendastaRmAccount.setTaxId( accountVO.getTaxId() );
        vendastaRmAccount.setTwitterSearches( accountVO.getTwitterSearches() );
        vendastaRmAccount.setWebsite( accountVO.getWebsite() );
        vendastaRmAccount.setWelcomeMessage( accountVO.getWelcomeMessage() );
        vendastaRmAccount.setWorkNumber( accountVO.getWorkNumber() );
        vendastaRmAccount.setDemoAccountFlag( accountVO.getDemoAccountFlag() );
        vendastaRmAccount.setSendAlertsFlag( accountVO.getSendAlertsFlag() );
        vendastaRmAccount.setSendReportsFlag( accountVO.getSendReportsFlag() );
        vendastaRmAccount.setSendTutorialsFlag( accountVO.getSendTutorialsFlag() );
        vendastaRmAccount.setLatitude( accountVO.getLatitude() );
        vendastaRmAccount.setLongitude( accountVO.getLongitude() );
        return vendastaRmAccount;
    }


    @Override
    public VendastaRmAccountVO transformDomainObjectToApiResponse( VendastaRmAccount d, Object... objects )
    {
        // TODO Auto-generated method stub
        return null;
    }

}
