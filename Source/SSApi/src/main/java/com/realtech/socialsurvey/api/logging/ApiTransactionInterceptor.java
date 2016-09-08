package com.realtech.socialsurvey.api.logging;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


@Component
public class ApiTransactionInterceptor extends HandlerInterceptorAdapter
{

    @Autowired
    private ThreadPoolTaskExecutor loggingTaskExecutor;

    @Autowired
    private WebApplicationContext context;

    @Override
    public void postHandle( HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView )
        throws Exception
    {

        String data = getRequestBody( request );
        System.out.println( getFullURL( request ) );
        System.out.println( data );
        System.out.println( new Date() );
        System.out.println( request.getMethod() );
        System.out.println( response.getStatus() );
        System.out.println( request.getAttribute( "output" ) );

        request.removeAttribute( "output" );

        ApiLoggingProcessorThread thread = (ApiLoggingProcessorThread) getContext().getBean( "apiLoggingProcessorThread" );        
        getLoggingTaskExecutor().execute( thread );

        System.out.println( "finish" );
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
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ( ( line = reader.readLine() ) != null ) {
            buffer.append( line );
        }
        return buffer.toString();
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
