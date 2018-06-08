package com.realtech.socialsurvey.web.controller;
import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.*;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.enums.SocialFeedStatus;
import com.realtech.socialsurvey.core.enums.TextActionType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.reportingmanagement.ReportingDashboardManagement;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.api.builder.SSApiIntergrationBuilder;
import com.realtech.socialsurvey.web.common.JspResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/*
 * Controller for Social Monitor Web Page 
 */
@Controller
public class SocialMonitorWebController {

    private static final Logger LOG = LoggerFactory.getLogger( SocialMonitorWebController.class );
    
    private static final String START_INDEX = "startIndex";
    private static final String BATCH_SIZE = "batchSize";
    private static final String MONITOR_TYPE = "monitorType";
    private static final String STATUS = "status";
    private static final String FLAG = "flag";
    private static final String MESSAGE = "message";
    private static final String SUCCESS = "Success";
    
    @Value("${SOCIAL_MONITOR_AUTH_HEADER}")
    private String authHeader;
    
    @Autowired
	private MessageUtils messageUtils;
    
    @Autowired
	private SessionHelper sessionHelper;
    
    @Autowired
    private SSApiIntergrationBuilder ssApiIntergrationBuilder;
    
    private ReportingDashboardManagement reportingDashboardManagement;
    
    @Autowired
    private OrganizationManagementService organizationManagementService;
    
    @Autowired
    public void setReportingDashboardManagement( ReportingDashboardManagement reportingDashboardManagement )
    {
        this.reportingDashboardManagement = reportingDashboardManagement;
    }
    /*
     * Web API to return JSP name for social monitor web page (Add monitor page)
     */
    
    @RequestMapping ( value = "/getstreamcontainer", method = RequestMethod.GET)
    public String getStreamContainer( Model model, HttpServletRequest request )
    {
        LOG.info("Social Monitor stream container page fetched");
        
        return JspResolver.STREAM_CONTAINER_PAGE;
    }
    
    @RequestMapping ( value = "/getstreamactioncontainer", method = RequestMethod.GET)
    public String getStreamActionContainer( Model model, HttpServletRequest request )
    {
        LOG.info( "Social Monitor stream action container page fetched" );
        
        return JspResolver.STREAM_ACTION_CONTAINER_PAGE;
    }
    
    @RequestMapping ( value = "/showsocialduplicate", method = RequestMethod.GET)
    public String showSocialMonitorDuplicate( Model model, HttpServletRequest request )
    {
        LOG.info( "Social Monitor, show Duplicate Started" );
        
        return JspResolver.SOCIAL_MONITOR_DUPLICATE_POPUP;
    }
    
    @RequestMapping ( value = "/showsocialmonitorpage", method = RequestMethod.GET)
    public String showSocialMonitorPage( Model model, HttpServletRequest request )
    {
        LOG.info( "Social Monitor Add monitors Page Started" );
        
        return JspResolver.SOCIAL_MONITOR_PAGE;
    }
    
    /*
     * Web API to return JSP name for social monitor Stream page
     */
    @RequestMapping ( value = "/showsocialmonitorstreampage", method = RequestMethod.GET)
    public String showSocialMonitorStreamPage( Model model, HttpServletRequest request )
    {
        LOG.info( "Social Monitor Stream Page Started" );
        
        return JspResolver.SOCIAL_MONITOR_STREAM_PAGE;
    }
    
    /*
     * Web API to return JSP name for social monitor Macro page
     */
    @RequestMapping ( value = "/showsocialmonitormacropage", method = RequestMethod.GET)
    public String showSocialMonitorMacroPage( Model model, HttpServletRequest request )
    {
        LOG.info( "Social Monitor Macro Page Started" );
        
        return JspResolver.SOCIAL_MONITOR_MACRO_PAGE;
    }
    
    /*
     * Web API to return JSP name for page to Add social monitor Macros
     */
    @RequestMapping ( value = "/showsocialmonitoraddmacropage", method = RequestMethod.GET)
    public String showSocialMonitorAddMacroPage( Model model, HttpServletRequest request )
    {
        LOG.info( "Social Monitor Macro Page Started" );
        
        return JspResolver.SOCIAL_MONITOR_ADD_MACRO_PAGE;
    }
    
    @ResponseBody
    @RequestMapping ( value = "/getmonitorslistbytype", method = RequestMethod.GET)
    public String getMonitors(Model model, HttpServletRequest request) {
    	
        LOG.info( "Method to fetch Monitors for Social Monitor,  getMonitors() Started" );
        
        User user = sessionHelper.getCurrentUser();
        Long companyId = user.getCompany().getCompanyId();
       
        String startIndexStr = request.getParameter(START_INDEX);
        String batchSizeStr = request.getParameter(BATCH_SIZE);
        String searchText =  request.getParameter("text");
        
        int startIndex=0;
        int batchSize=10;
        String monitorType=null;
        
        monitorType=request.getParameter(MONITOR_TYPE);
        
        if ( startIndexStr != null && !startIndexStr.isEmpty() ) {
        	startIndex = Integer.valueOf( startIndexStr );
        }
        
        if ( batchSizeStr != null && !batchSizeStr.isEmpty() ) {
        	batchSize = Integer.valueOf( batchSizeStr );
        }
        
        if(searchText == null) {
            searchText = "";
        }
        
        String authorizationHeader = "Basic " + authHeader;

        Response response = ssApiIntergrationBuilder.getIntegrationApi().getCompanyKeywords(companyId, startIndex, batchSize, monitorType,searchText, authorizationHeader);

        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );
       
    }
    
    private List<MultiplePhrasesVO> createKeywordFromRequest(HttpServletRequest request) {
    	
    	LOG.debug("Method to create Keyword object called.");
		String keyPhrase = request.getParameter("monitor-keyphrase");
		String monitorTypeStr = request.getParameter("monitor-type");
		int monitorTypeNumVal = 0;
		
		List<String> keyphraseList=new ArrayList<>();
		if(!keyPhrase.isEmpty() && keyPhrase != null) {
		    String[] phraeList = keyPhrase.split(",");
		       for(int i=0;i<phraeList.length;i++) {
		           keyphraseList.add(phraeList[i]);
		       }
		}
		
		List<MultiplePhrasesVO> multiplePhrasesVOList = new ArrayList<>();  
        MultiplePhrasesVO newKeyword = new MultiplePhrasesVO();
        
		if(!monitorTypeStr.isEmpty() && monitorTypeStr != null) {
		    monitorTypeNumVal = Integer.valueOf( monitorTypeStr );
    		    
		    if(monitorTypeNumVal==999) {
		        return multiplePhrasesVOList;
		    }else if(monitorTypeNumVal==2) {
		        
		        multiplePhrasesVOList.add( new MultiplePhrasesVO() );
		        multiplePhrasesVOList.get(0).setPhrases( keyphraseList );
                multiplePhrasesVOList.get(0).setMonitorType(MonitorType.KEYWORD_MONITOR);
		        
		        multiplePhrasesVOList.add( new MultiplePhrasesVO() );
                multiplePhrasesVOList.get(1).setPhrases( keyphraseList );
                multiplePhrasesVOList.get(1).setMonitorType(MonitorType.GOOGLE_ALERTS);
                
		    }else{
		        newKeyword.setPhrases(keyphraseList);
		        newKeyword.setMonitorType(MonitorType.values()[monitorTypeNumVal]);
		        multiplePhrasesVOList.add(newKeyword);
		    }
		}
		// Created VO and added required fields.
		
		return multiplePhrasesVOList;
    }
    
    @ResponseBody
    @RequestMapping ( value = "/addmonitorkeyword", method = RequestMethod.POST)
    public String addMonitorKeyword( Model model, HttpServletRequest request)
    {
    	
    	LOG.info("Method addMonitorKeyword of SocialMonitorWebController called");
    	
    	User user = sessionHelper.getCurrentUser();
        Long companyId = user.getCompany().getCompanyId();
        
    	Map<String, String> statusMap = new HashMap<>();
		String message = "";
		String statusJson = "";
		
		List<MultiplePhrasesVO> monitorList = createKeywordFromRequest(request);
		
		String authorizationHeader = "Basic " + authHeader;
		
		int currentCount = 0;
		
		try {
			currentCount = organizationManagementService.getKeywordCount(companyId);
		} catch (InvalidInputException e) {
			 LOG.error("Exception occured while fetching monitor count, while adding new monitor.", e);
	            message = "Unable to add monitor";
	            statusMap.put(STATUS, CommonConstants.ERROR);
		}
		
		Response response =null;
    	Integer successCount = 0;
    	try {
        	if(!monitorList.isEmpty() && monitorList != null) {
        	    MultiplePhrasesVO newKeyword = new MultiplePhrasesVO();
        	    for(int i=0;i<monitorList.size();i++) {
        	            
        	            newKeyword = monitorList.get(i);
        	            
                        response = ssApiIntergrationBuilder.getIntegrationApi().addMultiplePhrasesToCompany( companyId, newKeyword, authorizationHeader );
                        
                        int newCount = organizationManagementService.getKeywordCount(companyId);
                        
                        if(newCount > currentCount);
                        {
                        	successCount++;
                        }
                        currentCount = newCount;
                        
        	    }
        	    message = messageUtils.getDisplayMessage(DisplayMessageConstants.ADD_MONITOR_SUCCESSFUL,
                    DisplayMessageType.SUCCESS_MESSAGE).getMessage();
               String keywords = new String(((TypedByteArray) response.getBody()).getBytes());
               statusMap.put("keywords", keywords);
               statusMap.put(STATUS, CommonConstants.SUCCESS_ATTRIBUTE);
        	} else {
        	    message = messageUtils.getDisplayMessage(DisplayMessageConstants.ADD_MONITOR_UNSUCCESSFUL,
                    DisplayMessageType.ERROR_MESSAGE).getMessage();
        	    statusMap.put(STATUS, CommonConstants.ERROR);
        	}
        }catch(Exception e){
            LOG.error("Exception occured in SS-API while adding new monitor.", e);
            message = messageUtils.getDisplayMessage(DisplayMessageConstants.ADD_MONITOR_UNSUCCESSFUL,
                    DisplayMessageType.ERROR_MESSAGE).getMessage();
            statusMap.put(STATUS, CommonConstants.ERROR);
        }
    	    	
    	statusMap.put(MESSAGE, message);
    	statusMap.put("successCount", successCount.toString());
		statusJson = new Gson().toJson(statusMap);
		
    	return statusJson;
    }
    
    @ResponseBody
    @RequestMapping ( value = "/getmacrosbycompanyid", method = RequestMethod.GET)
    public String getMacros(Model model, HttpServletRequest request) {
    	
        LOG.info( "Method to fetch Macros for Social Monitor,  getMacros() Started" );
        
        User user = sessionHelper.getCurrentUser();
        Long companyId = user.getCompany().getCompanyId();

        String searchText =  request.getParameter("text");
        if(searchText == null) {
            searchText = "";
        }
        
        String authorizationHeader = "Basic " + authHeader;
        
        Response response = ssApiIntergrationBuilder.getIntegrationApi().showMacrosForEntity(companyId,searchText, authorizationHeader);

        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );
       
    }
    
    private SocialMonitorMacro createMacroFromRequest(HttpServletRequest request) {
    	
    	LOG.debug("Method to create Macro object called.");
		String macroId = request.getParameter("macro-id");
		String macroName = request.getParameter("macro-name");
		String description = request.getParameter("macro-description");
		String status = request.getParameter("macro-status");
		String alertString = request.getParameter("macro-alert");
		String actionType = request.getParameter("macro-action-type");
		String actionText = request.getParameter("macro-action-text");
		String usageText = request.getParameter("macro-usage");
		
		boolean active = true;
		int count = 0;
		
		Actions actions= new Actions();
		boolean flagged = false;
		String socialFeedStatus;
		int alert=0;
		
		if(alertString != null && !alertString.isEmpty()) {
			alert= Integer.valueOf(alertString);
		}
		
		switch(alert) {
			case 0: socialFeedStatus = "NEW";
				break;
				
			case 1: socialFeedStatus = "NEW";
				flagged = true;
				break;
			
			case 2: socialFeedStatus = "ESCALATED";
				flagged = true;
				break;
			
			case 3: socialFeedStatus = "RESOLVED";
				break;
			
			default: socialFeedStatus="";
		}
		
		actions.setSocialFeedStatus(SocialFeedStatus.valueOf(socialFeedStatus));
		actions.setFlagged(flagged);
		actions.setText(actionText);
		actions.setTextActionType(TextActionType.valueOf(actionType));
		
		if(status.equalsIgnoreCase("false")){
			active = false;
		}
		
		if(usageText != null) {
			count = Integer.valueOf(usageText);
		}
		
		// Created VO and added required fields.
		SocialMonitorMacro socialMonitorMacro = new SocialMonitorMacro();
		socialMonitorMacro.setMacroId(macroId);
		socialMonitorMacro.setMacroName(macroName);
		socialMonitorMacro.setDescription(description);
		socialMonitorMacro.setActions(actions);
		socialMonitorMacro.setActive(active);
		socialMonitorMacro.setLast7DaysMacroCount(count);
		socialMonitorMacro.setMacroUsageTime(new ArrayList<Long>());
		return socialMonitorMacro;
    }
    
    @ResponseBody
    @RequestMapping ( value = "/updatemacro", method = RequestMethod.POST)
    public String updateMacro( Model model, HttpServletRequest request)
    {
    	
    	LOG.info("Method updateMacro of SocialMonitorWebController called");
    	
    	User user = sessionHelper.getCurrentUser();
        Long companyId = user.getCompany().getCompanyId();
        
    	Map<String, String> statusMap = new HashMap<>();
		String message = "";
		String statusJson = "";
		
		SocialMonitorMacro socialMonitorMacro = createMacroFromRequest(request);
    	
    	Response response =null;
    	
        String authorizationHeader = "Basic " + authHeader;
    	
    	try {
    		response = ssApiIntergrationBuilder.getIntegrationApi().updateMacrosForEntity(socialMonitorMacro, companyId, authorizationHeader);
    		message = messageUtils.getDisplayMessage(DisplayMessageConstants.ADD_MACRO_SUCCESSFUL,
					DisplayMessageType.SUCCESS_MESSAGE).getMessage();
    		String status = new String(((TypedByteArray) response.getBody()).getBytes());
    		statusMap.put(SUCCESS, status);
			statusMap.put(STATUS, CommonConstants.SUCCESS_ATTRIBUTE);
    	}catch(Exception e){
    		LOG.error("Exception occured in SS-API while updating macro.", e);
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.ADD_MACRO_UNSUCCESSFUL,
					DisplayMessageType.ERROR_MESSAGE).getMessage();
			statusMap.put(STATUS, CommonConstants.ERROR);
    	}
    	
    	statusMap.put(MESSAGE, message);
		statusJson = new Gson().toJson(statusMap);
		
    	return statusJson;
    }
    
    @ResponseBody
    @RequestMapping ( value = "/getsocialpostsforstream", method = RequestMethod.POST)
    public String getSocialPostsForStream(Model model, HttpServletRequest request) {
    	
        LOG.info( "Method to fetch Social Posts for Social Monitor Stream,  getSocialPostsForStream() Started" );
        
        User user = sessionHelper.getCurrentUser();
        Long companyId = user.getCompany().getCompanyId();
        
        boolean isCompanySet = false;
        
        String startIndexStr = request.getParameter(START_INDEX);
        String batchSizeStr = request.getParameter(BATCH_SIZE);
        String status = request.getParameter(STATUS);
        String flagStr = request.getParameter(FLAG);
        String companyIdStr = request.getParameter( "company" );
        String regionIdStr = request.getParameter( "region" );
        String branchIdStr = request.getParameter( "branch" );
        String agentIdStr = request.getParameter( "user" );
        String feedsStr = request.getParameter( "feeds" );
        String text = request.getParameter("text");
        
        List<String> feedType = new ArrayList<>();
        List<Long> regionIds;
        List<Long> branchIds;
        List<Long> agentIds;
        
        int startIndex=0;
        int batchSize=10;
        boolean flag = false;
        
        if ( startIndexStr != null && !startIndexStr.isEmpty() ) {
        	startIndex = Integer.valueOf( startIndexStr );
        }
        
        if ( batchSizeStr != null && !batchSizeStr.isEmpty() ) {
        	batchSize = Integer.valueOf( batchSizeStr );
        }
        
        if(status.equalsIgnoreCase("none") || status.isEmpty()){
            status=null;
        }
        
       if ( flagStr != null && !flagStr.isEmpty() ) {
        	flag = Boolean.valueOf( flagStr );
        }
  
       regionIds = splitList( regionIdStr );
       branchIds = splitList( branchIdStr );
       agentIds = splitList( agentIdStr );
       
       String[] feedsList = feedsStr.split(",");
       for(int i=0;i<feedsList.length;i++) {
           feedType.add(feedsList[i]);
       }
       
       if(companyIdStr != null && !companyIdStr.isEmpty()) {
           companyId = Long.valueOf( companyIdStr );
           if(companyId == 0) {
               companyId = -1l;
           }
           isCompanySet = true;
       }
       
       if(text == null) {
           text = "";
       }
       
       String authorizationHeader = "Basic " + authHeader;
       
       SocialFeedFilter filter = new SocialFeedFilter();
       
       filter.setStartIndex( startIndex );
       filter.setLimit( batchSize );
       filter.setStatus( status );
       filter.setFlag( flag );
       filter.setFeedtype( feedType );
       filter.setCompanyId( companyId );
       filter.setRegionIds( regionIds );
       filter.setBranchIds( branchIds );
       filter.setAgentIds( agentIds );
       filter.setSearchText( text );
       filter.setCompanySet( isCompanySet );
       
       Response response = ssApiIntergrationBuilder.getIntegrationApi().showStreamSocialPosts(filter, authorizationHeader);
       
        return new String( ( (TypedByteArray) response.getBody() ).getBytes(),Charset.forName("UTF-8") );
       
    }
    
    private List<Long> splitList(String listStr) {
        List<Long> listOfIds = new ArrayList<>();
        
        if(listStr!=null && !listStr.isEmpty()) {
           
            String[] splittedList = listStr.split(",");
            
            for(int i=0;i<splittedList.length;i++) {
                listOfIds.add(Long.valueOf(splittedList[i]));
            }
        }
        
        return listOfIds;   
    }
    
    private SocialFeedsActionUpdate createSFAUFromRequest(HttpServletRequest request, String userName, String userEmailId) {
    
    	String postId = request.getParameter("form-post-id");
    	String flaggedStr = request.getParameter("form-flagged");
    	String statusStr = request.getParameter("form-status");
    	String textActType = request.getParameter("form-text-act-type");
    	String text = request.getParameter("form-post-textbox");
    	String macroId = request.getParameter("form-post-act-macro-id");
    	    	
    	String[] postIdList = postId.split(",");
    	List<String> postIds = new ArrayList<>();
    	
    	for(int i=0;i<postIdList.length;i++) {
    		postIds.add(postIdList[i]);
    	}
    	
    	Set<String> postIdSet = new HashSet<>();
    	postIdSet.addAll( postIds );
    	
    	SocialFeedsActionUpdate socialFeedsActionUpdate = new SocialFeedsActionUpdate();
    	socialFeedsActionUpdate.setPostIds(postIdSet);
    	
    	boolean flagged = false;
    	if(flaggedStr!=null && !flaggedStr.isEmpty() && flaggedStr.equalsIgnoreCase("true")) {
    		flagged = true;
    	}
    	socialFeedsActionUpdate.setFlagged(flagged);
    	
    	if(statusStr.equalsIgnoreCase("NONE")){
    		socialFeedsActionUpdate.setStatus(null);
    	}else {
    		socialFeedsActionUpdate.setStatus(SocialFeedStatus.valueOf(statusStr));
    	}
    	
    	socialFeedsActionUpdate.setTextActionType(TextActionType.valueOf(textActType));
    	socialFeedsActionUpdate.setText(text);
    	socialFeedsActionUpdate.setMacroId(macroId);
    	socialFeedsActionUpdate.setUserName(userName);
    	socialFeedsActionUpdate.setUserEmailId( userEmailId );
    	
    	
    	return socialFeedsActionUpdate;
    }
    
    @ResponseBody
    @RequestMapping ( value = "/updatepostaction", method = RequestMethod.POST)
    public String updatePostAction( Model model, HttpServletRequest request)
    {
    	
    	LOG.info("Method updatePostAction of SocialMonitorWebController called");
    	
    	User user = sessionHelper.getCurrentUser();
        Long companyId = user.getCompany().getCompanyId();
        String userName = user.getFirstName() + " " + user.getLastName();
        String userEmailId = user.getEmailId();
        
    	Map<String, String> statusMap = new HashMap<>();
    	String message = "";
    	String statusJson = "";
    	
    	SocialFeedsActionUpdate socialFeedsActionUpdate = createSFAUFromRequest(request,userName, userEmailId);
    	
    	String isDup = request.getParameter("form-is-dup");
    	boolean duplicateFlag = false;
    	
    	if(!isDup.isEmpty() && isDup != null) {
    	    duplicateFlag = Boolean.valueOf( isDup );
    	}
        
    	Response response =null;
    	
        String authorizationHeader = "Basic " + authHeader;
    	
    	try {
    		response = ssApiIntergrationBuilder.getIntegrationApi().saveSocialFeedsForAction(socialFeedsActionUpdate, companyId, duplicateFlag, authorizationHeader);
    		message = messageUtils.getDisplayMessage(DisplayMessageConstants.UPDATE_POST_SUCCESSFUL,
    				DisplayMessageType.SUCCESS_MESSAGE).getMessage();
    		String status = new String(((TypedByteArray) response.getBody()).getBytes());
    		statusMap.put("userName", userName);
    		statusMap.put(SUCCESS, status);
    		statusMap.put(STATUS, CommonConstants.SUCCESS_ATTRIBUTE);
    	}catch(Exception e){
    		LOG.error("Exception occured in SS-API while updating post action.", e);
    		message = messageUtils.getDisplayMessage(DisplayMessageConstants.UPDATE_POST_UNSUCCESSFUL,
    				DisplayMessageType.ERROR_MESSAGE).getMessage();
    		statusMap.put(STATUS, CommonConstants.ERROR);
    	}
    	
    	statusMap.put(MESSAGE, message);
    	statusJson = new Gson().toJson(statusMap);
    	
    	return statusJson;
    }
    
    private SocialFeedsActionUpdate createSFAUFromRequestForMacro(HttpServletRequest request, String userName) {
        
    	String postId = request.getParameter("macro-form-post-id");
    	String flaggedStr = request.getParameter("macro-form-flagged");
    	String statusStr = request.getParameter("macro-form-status");
    	String textActType = request.getParameter("macro-form-text-act-type");
    	String text = request.getParameter("macro-form-text");
    	String macroId = request.getParameter("macro-form-macro-id");
    	
    	String[] postIdList = postId.split(",");
    	List<String> postIds = new ArrayList<>();
    	
    	for(int i=0;i<postIdList.length;i++) {
    		postIds.add(postIdList[i]);
    	}
    	   
        Set<String> postIdSet = new HashSet<>();
        postIdSet.addAll( postIds );
    	
        SocialFeedsActionUpdate socialFeedsActionUpdate = new SocialFeedsActionUpdate();
    	socialFeedsActionUpdate.setPostIds(postIdSet);
    	
    	boolean flagged = false;
    	if(flaggedStr!=null && !flaggedStr.isEmpty() && flaggedStr.equalsIgnoreCase("true")) {
    		flagged = true;
    	}
    	socialFeedsActionUpdate.setFlagged(flagged);
    	
    	if(statusStr.equalsIgnoreCase("NONE")){
    		socialFeedsActionUpdate.setStatus(null);
    	}else {
    		socialFeedsActionUpdate.setStatus(SocialFeedStatus.valueOf(statusStr));
    	}
    	
    	socialFeedsActionUpdate.setTextActionType(TextActionType.valueOf(textActType));
    	socialFeedsActionUpdate.setText(text);
    	socialFeedsActionUpdate.setMacroId(macroId);
    	socialFeedsActionUpdate.setUserName(userName);
    	
    	return socialFeedsActionUpdate;
    }
    
    @ResponseBody
    @RequestMapping ( value = "/updatepostactionwithmacro", method = RequestMethod.POST)
    public String updatePostActionWithMacro( Model model, HttpServletRequest request)
    {
    	
    	LOG.info("Method updatePostAction of SocialMonitorWebController called");
    	
    	User user = sessionHelper.getCurrentUser();
        Long companyId = user.getCompany().getCompanyId();
        String userName = user.getFirstName() + " " + user.getLastName();
        
    	Map<String, String> statusMap = new HashMap<>();
    	String message = "";
    	String statusJson = "";
    	
    	SocialFeedsActionUpdate socialFeedsActionUpdate = createSFAUFromRequestForMacro(request, userName);
    	
    	Response response =null;
    	
    	String isDup = request.getParameter("macro-form-is-dup");
        boolean duplicateFlag = false;
        
        if(!isDup.isEmpty() && isDup != null) {
            duplicateFlag = Boolean.valueOf( isDup );
        }
    	
        String authorizationHeader = "Basic " + authHeader;
    	
    	try {
    		response = ssApiIntergrationBuilder.getIntegrationApi().saveSocialFeedsForAction(socialFeedsActionUpdate, companyId, duplicateFlag, authorizationHeader);
    		message = messageUtils.getDisplayMessage(DisplayMessageConstants.UPDATE_POST_SUCCESSFUL,
    				DisplayMessageType.SUCCESS_MESSAGE).getMessage();
    		String status = new String(((TypedByteArray) response.getBody()).getBytes());
    		statusMap.put("userName", userName);
    		statusMap.put(SUCCESS, status);
    		statusMap.put(STATUS, CommonConstants.SUCCESS_ATTRIBUTE);
    	}catch(Exception e){
    		LOG.error("Exception occured in SS-API while updating post action.", e);
    		message = messageUtils.getDisplayMessage(DisplayMessageConstants.UPDATE_POST_UNSUCCESSFUL,
    				DisplayMessageType.ERROR_MESSAGE).getMessage();
    		statusMap.put(STATUS, CommonConstants.ERROR);
    	}
    	
    	statusMap.put(MESSAGE, message);
    	statusJson = new Gson().toJson(statusMap);
    	
    	return statusJson;
    }
    
    @ResponseBody
    @RequestMapping ( value = "/getsegmentsbycompanyid", method = RequestMethod.GET)
    public String getSegmentsByCompanyId(Model model, HttpServletRequest request) {
        
        User user = sessionHelper.getCurrentUser();
        Long companyId = user.getCompany().getCompanyId();
        
        String authorizationHeader = "Basic " + authHeader;
        
        Response response = ssApiIntergrationBuilder.getIntegrationApi().getSegmentsByCompanyId(companyId, authorizationHeader);
        
        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );
    }
    
    @ResponseBody
    @RequestMapping ( value = "/getusersbycompanyid", method = RequestMethod.GET)
    public String getUsersByCompanyId(Model model, HttpServletRequest request) {
        
        User user = sessionHelper.getCurrentUser();
        Long companyId = user.getCompany().getCompanyId();
        
        String authorizationHeader = "Basic " + authHeader;
        
        Response response = ssApiIntergrationBuilder.getIntegrationApi().getUsersByCompanyId( companyId, authorizationHeader );
        
        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );
    }
    
    @ResponseBody
    @RequestMapping ( value = "/deletemonitorsbyid", method = RequestMethod.POST)
    public String deleteMonitor(Model model, HttpServletRequest request) {
        
        String monitorIdStr = request.getParameter( "monitorIds" );
        List<String> monitorIds= new ArrayList<>();
        
        User user = sessionHelper.getCurrentUser();
        Long companyId = user.getCompany().getCompanyId();
        
        Map<String, String> statusMap = new HashMap<>();
        String message = "";
        String statusJson = "";
        
        String[] monitorIdList = monitorIdStr.split(",");
        for(int i=0;i<monitorIdList.length;i++) {
            monitorIds.add(monitorIdList[i]);
        }
        
        Response response = null;
        
        String authorizationHeader = "Basic " + authHeader;
        
        try {
           
            response = ssApiIntergrationBuilder.getIntegrationApi().deleteKeywordsFromCompany( companyId, monitorIds, authorizationHeader );
            message = messageUtils.getDisplayMessage(DisplayMessageConstants.UPDATE_POST_SUCCESSFUL,
                    DisplayMessageType.SUCCESS_MESSAGE).getMessage();
            String keywords = new String(((TypedByteArray) response.getBody()).getBytes());
            statusMap.put("keywords", keywords);
            statusMap.put(STATUS, CommonConstants.SUCCESS_ATTRIBUTE);
         }catch(Exception e){
            LOG.error("Exception occured in SS-API while updating post action.", e);
            message = messageUtils.getDisplayMessage(DisplayMessageConstants.UPDATE_POST_UNSUCCESSFUL,
                    DisplayMessageType.ERROR_MESSAGE).getMessage();
            statusMap.put(STATUS, CommonConstants.ERROR);
         }
        
        statusMap.put(MESSAGE, message);
        statusJson = new Gson().toJson(statusMap);
        
        return statusJson;
    }
    
    @ResponseBody
    @RequestMapping ( value = "/getfeedsbycompanyid", method = RequestMethod.GET)
    public String getFeedsByCompanyId(Model model, HttpServletRequest request) {
        
        User user = sessionHelper.getCurrentUser();
        Long companyId = user.getCompany().getCompanyId();
        
        String authorizationHeader = "Basic " + authHeader;
        
        Response response = ssApiIntergrationBuilder.getIntegrationApi().getFeedTypesByCompanyId( companyId, authorizationHeader );
        
        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );
    }
    
    @ResponseBody
    @RequestMapping ( value = "/fetchrecentactivitiesforsocialmonitor", method = RequestMethod.GET)
    public String fetchRecentActivity( Model model, HttpServletRequest request )
    {
        LOG.info( "Fetching Recent Activity for Social Monitor" );
        HttpSession session = request.getSession( false );

        int startIndex = 0;
        int batchSize = 0;
        String startIndexStr = request.getParameter( "startIndex" );
        String batchSizeStr = request.getParameter( "batchSize" );
        if ( startIndexStr != null && !startIndexStr.isEmpty() ) {
            startIndex = Integer.parseInt( startIndexStr );
        }
        if ( batchSizeStr != null && !batchSizeStr.isEmpty() ) {
            batchSize = Integer.parseInt( batchSizeStr );
        }
        
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        
        LOG.debug( "Getting recent activity for Social Monitor for entity id {}, entity type {}, startIndex {} and barch size {}", entityId,
            entityType, startIndex, batchSize );
        Response response = ssApiIntergrationBuilder.getIntegrationApi().getRecentActivityForSocialMonitor(entityId, entityType, startIndex, batchSize);
        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );
    }
    
    @ResponseBody
    @RequestMapping ( value = "/fetchrecentactivitiescountforsocialmonitor")
    public String getIncompleteSurveyCount( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to get recent activity count for social monitor." );
        HttpSession session = request.getSession( false );
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        LOG.debug( "Method to get recent activity count for social monitor for entityType {} and entityId {}", entityType, entityId );
        long count = reportingDashboardManagement.getRecentActivityCountForSocialMonitor( entityId, entityType );
        LOG.info( "Method to get recent activity count for social monitor finished." );
        return String.valueOf( count );
    }
    
    @ResponseBody
    @RequestMapping ( value = "/addtrustedsource", method = RequestMethod.POST)
    public String addTrustedSource(Model model, HttpServletRequest request) {
    	
    	LOG.info( "Method to add trusted source for social monitor." );
        String trustedSource = request.getParameter( "trustedSource" );
        
        User user = sessionHelper.getCurrentUser();
        Long companyId = user.getCompany().getCompanyId();
        
        Map<String, String> statusMap = new HashMap<>();
        String message = "";
        String statusJson = "";
        
        Response response = null;
        
        String authorizationHeader = "Basic " + authHeader;
        
        try {
           
            response = ssApiIntergrationBuilder.getIntegrationApi().addTrustedSourceToCompany(companyId, trustedSource, authorizationHeader);
            message =  "Successfully added Trusted Source";
            String trustedSources = new String(((TypedByteArray) response.getBody()).getBytes());
            statusMap.put("trustedSources", trustedSources);
            statusMap.put(STATUS, CommonConstants.SUCCESS_ATTRIBUTE);
         }catch(Exception e){
            LOG.error("Exception occured in SS-API while updating post action.", e);
            message = "Unable to add Trusted Source";
            statusMap.put(STATUS, CommonConstants.ERROR);
         }
        
        statusMap.put(MESSAGE, message);
        statusJson = new Gson().toJson(statusMap);
        
        LOG.info( "Method to add trusted source for social monitor finished." );
        
        return statusJson;
    }
    
    @ResponseBody
    @RequestMapping ( value = "/removetrustedsource", method = RequestMethod.POST)
    public String removeTrustedSource(Model model, HttpServletRequest request) {
        
    	LOG.info( "Method to remove trusted source for social monitor." );
    	
        String trustedSource = request.getParameter( "trustedSource" );
        
        User user = sessionHelper.getCurrentUser();
        Long companyId = user.getCompany().getCompanyId();
        
        Map<String, String> statusMap = new HashMap<>();
        String message = "";
        String statusJson = "";
        
        Response response = null;
        
        String authorizationHeader = "Basic " + authHeader;
        
        try {
           
            response = ssApiIntergrationBuilder.getIntegrationApi().removeTrustedSourceToCompany(companyId, trustedSource, authorizationHeader);
            message =  "Successfully removed Trusted Source";
            String trustedSources = new String(((TypedByteArray) response.getBody()).getBytes());
            statusMap.put("trustedSources", trustedSources);
            statusMap.put(STATUS, CommonConstants.SUCCESS_ATTRIBUTE);
         }catch(Exception e){
            LOG.error("Exception occured in SS-API while updating post action.", e);
            message = "Unable to remove Trusted Source";
            statusMap.put(STATUS, CommonConstants.ERROR);
         }
        
        statusMap.put(MESSAGE, message);
        statusJson = new Gson().toJson(statusMap);
        
        LOG.info( "Method to remove trusted source for social monitor finished." );
        
        return statusJson;
    }

}
