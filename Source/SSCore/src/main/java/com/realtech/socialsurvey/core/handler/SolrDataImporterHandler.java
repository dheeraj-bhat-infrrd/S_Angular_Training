package com.realtech.socialsurvey.core.handler;

import com.realtech.socialsurvey.core.utils.solr.BranchesFullImport;
import com.realtech.socialsurvey.core.utils.solr.RegionsFullImport;
import com.realtech.socialsurvey.core.utils.solr.UsersFullImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Component
public class SolrDataImporterHandler
{
    public static final Logger LOG = LoggerFactory.getLogger(SolrDataImporterHandler.class);

    private ExecutorService executor;

    @Autowired
    private RegionsFullImport regionsFullImport;

    @Autowired
    private BranchesFullImport branchesFullImport;

    @Autowired
    private UsersFullImport usersFullImport;

    public void importData() {
        executor = Executors.newFixedThreadPool(3);
        executor.execute(regionsFullImport);
        executor.execute( branchesFullImport );
        executor.execute( usersFullImport );
    }
}
