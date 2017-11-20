package com.realtech.socialsurvey.web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class BotRequestUtils implements InitializingBean
{

    private static final Logger LOG = LoggerFactory.getLogger( BotRequestUtils.class );

    public static final String USER_AGENT_HEADER = "User-Agent";

    private String[] listBots;

    @Value ( "${BOTS_USER_AGENT_LIST}")
    private String botUserAgentList;


    public boolean checkBotRequest( String userAgent )
    {
        // Get the user agent. If its a BOT, then return the no java script page
        boolean isBotRequest = false;
        LOG.debug( "User header found : {}", userAgent );
        if ( userAgent != null ) {
            for ( String bot : listBots ) {
                if ( userAgent.indexOf( bot ) != -1 ) {
                    LOG.debug( "Found a crawler: {}", bot );
                    isBotRequest = true;
                    break;
                }
            }
        }
        return isBotRequest;
    }


    @Override
    public void afterPropertiesSet() throws Exception
    {
        LOG.info( "Get the list of bots" );
        listBots = botUserAgentList.split( "," );
    }

}
