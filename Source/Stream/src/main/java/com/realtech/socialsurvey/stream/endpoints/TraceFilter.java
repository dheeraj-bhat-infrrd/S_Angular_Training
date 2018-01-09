package com.realtech.socialsurvey.stream.endpoints;

import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.actuate.trace.TraceProperties;
import org.springframework.boot.actuate.trace.TraceRepository;
import org.springframework.boot.actuate.trace.WebRequestTraceFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;


@Component
public class TraceFilter extends WebRequestTraceFilter
{

    private static final String[] INCLUDED_ENDPOINTS = new String[] { "/api/**" };


    public TraceFilter( TraceRepository repository, TraceProperties properties )
    {
        super( repository, properties );
    }


    @Override
    protected boolean shouldNotFilter( HttpServletRequest request ) throws ServletException
    {
        return !Arrays.stream( INCLUDED_ENDPOINTS ).anyMatch( e -> new AntPathMatcher().match( e, request.getServletPath() ) );
    }


}
