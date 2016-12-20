package com.realtech.socialsurvey.core.services.organizationmanagement;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.VendastaProductSettings;
import com.realtech.socialsurvey.core.entities.VendastaSingleSignOnTicket;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;


public interface VendastaManagementService
{
    public boolean updateVendastaAccess( String collectionName, OrganizationUnitSettings unitSettings )
        throws InvalidInputException;


    public boolean updateVendastaRMSettings( String collectionName, OrganizationUnitSettings unitSettings,
        VendastaProductSettings vendastaReputationManagementSettings ) throws InvalidInputException;


    public String validateUrlGenerator( User user, String nextUrl, String productId, String ssoToken ) throws IOException, InvalidInputException;


    public boolean validateSSOTuple( String productId, String ssoToken, String ssoTicket );


    public String fetchSSOTokenForReputationManagementAccount( String entityType, long entityId, String productId )
        throws InvalidInputException, NoRecordsFetchedException;


    public Map<String, Object> getUnitSettingsForAHierarchy( String entityType, long entityId )
        throws InvalidInputException, NoRecordsFetchedException;


    public VendastaSingleSignOnTicket getSSOTicketById( long ticketId ) throws InvalidInputException;


    public List<VendastaSingleSignOnTicket> getAllInactiveTickets();


    public void usedVendastaTicketRemover();

}
