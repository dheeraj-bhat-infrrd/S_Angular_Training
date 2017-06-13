package com.realtech.socialsurvey.web.controller;

import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.realtech.socialsurvey.web.api.SSApiIntegration;
import com.realtech.socialsurvey.web.api.builder.SSApiIntergrationBuilder;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

@Controller
public class ReportingWebController
{
    private static final Logger LOG = LoggerFactory.getLogger( AccountWebController.class );
    
    @Autowired
    private SSApiIntergrationBuilder apiBuilder;

    @RequestMapping ( value = "/showreportingpage", method = RequestMethod.GET)
    @ResponseBody
    public String openReportingPage()
    {
        String responseString = "";
        SSApiIntegration api = apiBuilder.getIntegrationApi();
        Response response = api.createDummyReporting( );
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        return responseString;
    }
}
