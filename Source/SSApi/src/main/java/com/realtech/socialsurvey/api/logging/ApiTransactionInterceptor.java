package com.realtech.socialsurvey.api.logging;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.gson.Gson;
import com.realtech.socialsurvey.core.entities.ApiRequestDetails;
import com.realtech.socialsurvey.core.entities.ApiRequestEntity;
import com.realtech.socialsurvey.core.entities.ApiResponseEntity;


/**
 * Api transaction Intercepter to log all API request/response in mongoDB.
 * @author manish
 *
 */
@Component
public class ApiTransactionInterceptor extends HandlerInterceptorAdapter
{
	private static final Logger LOG = LoggerFactory.getLogger( ApiTransactionInterceptor.class );


    @Autowired
    private ThreadPoolTaskExecutor loggingTaskExecutor;

    @Autowired
    private WebApplicationContext context;

    @SuppressWarnings("unchecked")
	@Override
    public void postHandle( HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView )
        throws Exception
    {

    	LOG.debug("method postHandle started ");
    	
        String data = getRequestBody( request );
        
        ResponseEntity<Map<String, Object>> responseObj =  (ResponseEntity<Map<String, Object>>) request.getAttribute( "output" );
        request.removeAttribute( "output" );
        
        long companyId = 0;
        try{
        	if(request.getAttribute( "companyId" ) != null)
        		companyId = (long) request.getAttribute( "companyId" );

        }catch(Exception e){
        	LOG.error("Error while getting companyId from the request : {}", e.getMessage());
        }
        		
        
        ApiRequestEntity apiRequestEntity = new ApiRequestEntity();
        apiRequestEntity.setUrl( getFullURL( request ) );
        apiRequestEntity.setBody(data);
        apiRequestEntity.setRequestMethod(request.getMethod());
        
        ApiResponseEntity apiResponseEntity = new ApiResponseEntity();
        
        if(responseObj.getStatusCode() != null) {
            apiResponseEntity.setStatusCode(responseObj.getStatusCode().toString());
        }
        
        if(responseObj.getHeaders()!= null) {
            apiResponseEntity.setHeader(responseObj.getHeaders().toString());
        }
        
        
        // Not required to store response payload details.       
        //apiResponseEntity.setBody(gson.toJson(responseObj.getBody()));
        
        ApiRequestDetails apiRequestDetails = new ApiRequestDetails();
        apiRequestDetails.setRequest(apiRequestEntity);
        apiRequestDetails.setResponse(apiResponseEntity);
        apiRequestDetails.setCreatedOn( new Date(System.currentTimeMillis()));
        
        apiRequestDetails.setCompanyId(companyId);
        request.removeAttribute( "companyId" );
        
        ApiLoggingProcessorThread thread = (ApiLoggingProcessorThread) getContext().getBean( "apiLoggingProcessorThread" ); 
        thread.setApiRequestDetails(apiRequestDetails);
        
        getLoggingTaskExecutor().execute( thread );

    	LOG.debug("method postHandle finished ");
        super.postHandle( request, response, handler, modelAndView );
    }


    public String getFullURL( HttpServletRequest request )
    {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();

        if ( queryString == null ) {
            return requestURL.toString();
        } else {
            return requestURL.append( '?' ).append( queryString ).toString();
        }
    }


    public String getRequestBody( HttpServletRequest request ) throws IOException
    {
    	
    	Gson gson = new Gson();
    	Object input = request.getAttribute( "input");
    	String jsonOutput = new String();
    	if(input != null){
    		jsonOutput =  gson.toJson(input);
    		request.removeAttribute("input");
    	}
    	return jsonOutput;

    }


    public ThreadPoolTaskExecutor getLoggingTaskExecutor()
    {
        return loggingTaskExecutor;
    }


    public void setLoggingTaskExecutor( ThreadPoolTaskExecutor loggingTaskExecutor )
    {
        this.loggingTaskExecutor = loggingTaskExecutor;
    }


    public WebApplicationContext getContext()
    {
        return context;
    }


    public void setContext( WebApplicationContext context )
    {
        this.context = context;
    }

}
