package com.realtech.socialsurvey.core.utils.solr;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;


public class SetIsProfileImageSetFieldForUsers
{
    public static final Logger LOG = LoggerFactory.getLogger( SetIsProfileImageSetFieldForUsers.class );

    @Resource
    @Qualifier("solrSearchService")
    SolrSearchService searchService;

    @Value ( "${SOCIAL_POST_BATCH_SIZE}")
    private int pageSize;


    /**
     * Method to set the setIsProfileImageSet field for all users in solr
     */
    public void setIsProfileImageSetField()
    {
        LOG.info( "Started setIsProfileImageSetField method" );
        int pageNo = 1;
        SolrDocumentList users = null;
        do {
            Map<Long, Boolean> isProfileSetMap = new HashMap<Long, Boolean>();
            try {
                users = searchService.getAllUsers( pageSize * ( pageNo - 1 ), pageSize );
                for ( SolrDocument user : users ) {
                    String profileImageUrl = (String) user.getFieldValue( CommonConstants.PROFILE_IMAGE_URL_SOLR );
                    Long iden = (Long) user.getFirstValue( CommonConstants.USER_ID_SOLR );
                    if ( profileImageUrl == null || profileImageUrl.isEmpty() ) {
                        isProfileSetMap.put( iden, false );
                    } else {
                        isProfileSetMap.put( iden, true );
                    }
                }
                searchService.updateIsProfileImageSetFieldForMultipleUsers( isProfileSetMap );
            } catch ( SolrException e ) {
                LOG.error( "SolrException occured during batch process. Reason : ", e );
            } catch ( InvalidInputException e ) {
                LOG.error( "Invalid parameter passed. Reason : ", e );
            }
        } while ( !( users == null || users.isEmpty() ) );
    }
}
