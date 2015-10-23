package com.realtech.socialsurvey.core.utils.solr;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.dao.SocialPostDao;
import com.realtech.socialsurvey.core.dao.SolrImportDao;
import com.realtech.socialsurvey.core.entities.SocialPost;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;


public class SocialPostsFullImport
{
    public static final Logger LOG = LoggerFactory.getLogger( SocialPostsFullImport.class );
    

    @Resource
    @Qualifier("solrimport")
    private SolrImportDao solrImportDao;

    @Autowired
    private SolrSearchService solrSearchService;

    @Value("${BATCH_SIZE}")
    private int pageSize;
    
    @Autowired
    private SocialPostDao socialPostDao;
    
    /**
     * Method to fetch social posts from mongodb and index it into Solr
     */
    @Transactional
    public void importSocialPostsIntoSolr() {
        LOG.info("Started run method of SocialPostsFullImport");
        int pageNo = 1;
        List<SocialPost> socialPosts = null;
        
        do{
            try{
                socialPosts = socialPostDao.fetchSocialPostsPage( pageSize * (pageNo - 1), pageSize );
            } catch (NoRecordsFetchedException e) {
                LOG.info("NoRecordsFetchedException occurred while fetching social posts");
            }
            
    
            if (socialPosts == null || socialPosts.isEmpty()) {
                break;
            }
            
            LOG.debug("Adding social posts to Solr");
            try {
                solrSearchService.addSocialPostsToSolr( socialPosts );
            }
            catch (SolrException e) {
                LOG.error("SolrException occurred while adding social posts to solr");
            }
            pageNo++;
        }
        while (!socialPosts.isEmpty());
        LOG.info("Finished run method of SocialPostsFullImport");
        
    }
}
