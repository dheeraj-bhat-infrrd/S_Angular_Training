package com.realtech.socialsurvey.core.utils.solr;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.dao.SolrImportDao;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;


@Component
public class UsersFullImport implements Runnable
{

    public static final Logger LOG = LoggerFactory.getLogger( UsersFullImport.class );

    @Resource
    @Qualifier ( "solrimport")
    private SolrImportDao solrImportDao;

    @Autowired
    private SolrSearchService solrSearchService;

    @Value ( "${BATCH_SIZE}")
    private int pageSize;


    @Override
    @Transactional
    public void run()
    {
        LOG.info( "Started run method of UsersFullImport" );
        int pageNo = 1;
        List<User> users = null;

        do {
            LOG.debug( "Fetching Users" );
            try {
                users = solrImportDao.fetchUsersPage( pageSize * ( pageNo - 1 ), pageSize );

            if ( users == null || users.isEmpty() ) {
                break;
            }

            LOG.debug( "Adding Users to Solr" );
            
                solrSearchService.addUsersToSolr( users );
            } catch ( InvalidInputException e ) {
                LOG.error( "SolrException occurred while adding user to solr", e );
            } catch ( SolrException e ) {
                LOG.error( "SolrException occurred while adding user to solr", e );
            }
            catch ( NoRecordsFetchedException e ) {
                LOG.info( "NoRecordsFetchedException occurred while fetching users" );
            }
            catch(Exception ex) {
                LOG.error( "SolrException occurred while adding user to solr", ex );
            }
            pageNo++;
        } while ( !users.isEmpty() );
        LOG.info( "Finished run method of UsersFullImport" );
    }
}