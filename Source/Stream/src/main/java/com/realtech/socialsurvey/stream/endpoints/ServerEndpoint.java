package com.realtech.socialsurvey.stream.endpoints;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.stereotype.Component;

@Component
public class ServerEndpoint implements Endpoint<List<String>>
{

    private static final Logger LOG = LoggerFactory.getLogger( ServerEndpoint.class );
    
    @Override
    public String getId()
    {
        return "server";
    }


    @Override
    public List<String> invoke()
    {
        List<String> details = new ArrayList<>();
        try {
            details.add( "Server IP Address: " + InetAddress.getLocalHost().getHostAddress() );
            details.add( "Server OS: "+ System.getProperty( "os.name" ).toLowerCase() );
        } catch ( UnknownHostException e ) {
            LOG.error( "Exception", e );
        }
        return details;
    }


    @Override
    public boolean isEnabled()
    {
        return true;
    }


    @Override
    public boolean isSensitive()
    {
        return false;
    }

}
