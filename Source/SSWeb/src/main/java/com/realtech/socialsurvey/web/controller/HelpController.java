package com.realtech.socialsurvey.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.core.services.upload.impl.UploadUtils;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.core.utils.UrlValidationHelper;
import com.realtech.socialsurvey.web.common.JspResolver;
import com.realtech.socialsurvey.web.util.BotRequestUtils;

@Controller
public class HelpController {

	private static final Logger LOG = LoggerFactory.getLogger( ProfileManagementController.class );

    // JIRA SS-97 by RM-06 : BOC
    @Autowired
    private MessageUtils messageUtils;

    @Autowired
    private UploadUtils uploadUtils;

    @Autowired
    private SessionHelper sessionHelper;

    @Autowired
    private UrlValidationHelper urlValidationHelper;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private SolrSearchService solrSearchService;

    @Value ( "${APPLICATION_BASE_URL}")
    private String applicationBaseUrl;

    @Value ( "${CDN_PATH}")
    private String amazonEndpoint;

    @Value ( "${AMAZON_IMAGE_BUCKET}")
    private String amazonImageBucket;

    @Value ( "${AMAZON_LOGO_BUCKET}")
    private String amazonLogoBucket;

    @Autowired
    private BotRequestUtils botRequestUtils;


    @Transactional
    @RequestMapping ( value = "/showhelppage", method = RequestMethod.GET)
    public String showHelpPage( Model model, HttpServletRequest request )
    {
        LOG.info( "Method showProfileEditPage() called from ProfileManagementService" );
        return JspResolver.HELP_EDIT;
}
}