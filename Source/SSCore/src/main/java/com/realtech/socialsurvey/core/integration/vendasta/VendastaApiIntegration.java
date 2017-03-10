package com.realtech.socialsurvey.core.integration.vendasta;


import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;


public interface VendastaApiIntegration
{
    @GET ( "/api/v2/account/get/")
    Response getAccountById( @Query ( "apiUser") String apiUser, @Query ( "apiKey") String apiKey,
        @Query ( "customerIdentifier") String customerIdentifier );


    @POST ( "/api/v2/account/create/")
    Response createAccount( @Query ( "apiUser") String apiUser, @Query ( "apiKey") String apiKey,
        @Query ( "address") String address, @Query ( "city") String city, @Query ( "companyName") String companyName,
        @Query ( "country") String country, @Query ( "zip") String zip, @Query ( "state") String state,
        @Query ( "accountGroupId") String accountGroupId, @Query ( "adminNotes") String adminNotes,
        @Query ( "alternateEmail") String alternateEmail, @Query ( "billingCode") String billingCode,
        @Query ( "businessCategory") String businessCategory, @Query ( "callTrackingNumber") String callTrackingNumber,
        @Query ( "cellNumber") String cellNumber, @Query ( "commonCompanyName") String commonCompanyName,
        @Query ( "competitor") String competitor, @Query ( "customerIdentifier") String customerIdentifier,
        @Query ( "email") String email, @Query ( "employee") String employee, @Query ( "faxNumber") String faxNumber,
        @Query ( "firstName") String firstName, @Query ( "lastName") String lastName, @Query ( "marketId") String marketId,
        @Query ( "salesPersonEmail") String salesPersonEmail, @Query ( "service") String service,
        @Query ( "ssoToken") String ssoToken, @Query ( "taxId") String taxId,
        @Query ( "twitterSearches") String twitterSearches, @Query ( "website") String website,
        @Query ( "welcomeMessage") String welcomeMessage, @Query ( "workNumber") String workNumber,
        @Query ( "demoAccountFlag") boolean demoAccountFlag, @Query ( "sendAlertsFlag") boolean sendAlertsFlag,
        @Query ( "sendReportsFlag") boolean sendReportsFlag, @Query ( "sendTutorialsFlag") boolean sendTutorialsFlag,
        @Query ( "latitude") float latitude, @Query ( "longitude") String longitude );
}
