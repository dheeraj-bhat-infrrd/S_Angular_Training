package com.realtech.socialsurvey.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.Actions;
import com.realtech.socialsurvey.core.entities.Keyword;
import com.realtech.socialsurvey.core.entities.MonitorType;
import com.realtech.socialsurvey.core.entities.SocialFeedsActionUpdate;
import com.realtech.socialsurvey.core.entities.SocialMonitorMacro;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.enums.SocialFeedStatus;
import com.realtech.socialsurvey.core.enums.TextActionType;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.api.builder.SSApiIntergrationBuilder;
import com.realtech.socialsurvey.web.common.JspResolver;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

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
    
    @Autowired
	private MessageUtils messageUtils;
    
    @Autowired
	private SessionHelper sessionHelper;
    
    @Autowired
    private SSApiIntergrationBuilder ssApiIntergrationBuilder;
    
    /*
     * Web API to return JSP name for social monitor web page (Add monitor page)
     */
    
    @RequestMapping ( value = "/getstreamcontainer", method = RequestMethod.GET)
    public String getStreamContainer( Model model, HttpServletRequest request )
    {
        LOG.info( "Social Monitor stream container page fetched" );
        
        return JspResolver.STREAM_CONTAINER_PAGE;
    }
    
    @RequestMapping ( value = "/getstreamactioncontainer", method = RequestMethod.GET)
    public String getStreamActionContainer( Model model, HttpServletRequest request )
    {
        LOG.info( "Social Monitor stream action container page fetched" );
        
        return JspResolver.STREAM_ACTION_CONTAINER_PAGE;
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
        
        
        Response response = ssApiIntergrationBuilder.getIntegrationApi().getCompanyKeywords(companyId, startIndex, batchSize, monitorType);
        		
        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );
       
    }
    
    private Keyword createKeywordFromRequest(HttpServletRequest request) {
    	
    	LOG.debug("Method to create Keyword object called.");
		String keyPhrase = request.getParameter("monitor-keyphrase");
		String monitorType = request.getParameter("monitor-type");
		
		// Created VO and added required fields.
		Keyword newKeyword = new Keyword();
		
		newKeyword.setPhrase(keyPhrase);
		newKeyword.setMonitorType(MonitorType.valueOf(monitorType));
		
		return newKeyword;
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
		
		List<Keyword> keywordsRequest = new ArrayList<>();
    	Keyword newKeyword = createKeywordFromRequest(request);
    	keywordsRequest.add(newKeyword);
    	
    	Response response =null;
    	
    	try {
    		response = ssApiIntergrationBuilder.getIntegrationApi().addKeywordsToCompany(companyId, keywordsRequest);
    		message = messageUtils.getDisplayMessage(DisplayMessageConstants.ADD_MONITOR_SUCCESSFUL,
					DisplayMessageType.SUCCESS_MESSAGE).getMessage();
    		String keywords = new String(((TypedByteArray) response.getBody()).getBytes());
    		statusMap.put("keywords", keywords);
			statusMap.put(STATUS, CommonConstants.SUCCESS_ATTRIBUTE);
    	}catch(Exception e){
    		LOG.error("Exception occured in SS-API while adding new monitor.", e);
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.ADD_MONITOR_UNSUCCESSFUL,
					DisplayMessageType.ERROR_MESSAGE).getMessage();
			statusMap.put(STATUS, CommonConstants.ERROR);
    	}
    	
    	statusMap.put(MESSAGE, message);
		statusJson = new Gson().toJson(statusMap);
		
    	return statusJson;
    }
    
    @ResponseBody
    @RequestMapping ( value = "/getmacrosbycompanyid", method = RequestMethod.GET)
    public String getMacros(Model model, HttpServletRequest request) {
    	
        LOG.info( "Method to fetch Macros for Social Monitor,  getMacros() Started" );
        
        User user = sessionHelper.getCurrentUser();
        Long companyId = user.getCompany().getCompanyId();
       
        Response response = ssApiIntergrationBuilder.getIntegrationApi().showMacrosForEntity(companyId);
        		
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
		socialMonitorMacro.setCount(count);
		socialMonitorMacro.setLast7DaysMacroCount(0);
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
    	
    	try {
    		response = ssApiIntergrationBuilder.getIntegrationApi().updateMacrosForEntity(socialMonitorMacro, companyId);
    		message = messageUtils.getDisplayMessage(DisplayMessageConstants.ADD_MACRO_SUCCESSFUL,
					DisplayMessageType.SUCCESS_MESSAGE).getMessage();
    		String status = new String(((TypedByteArray) response.getBody()).getBytes());
    		statusMap.put("success", status);
			statusMap.put(STATUS, CommonConstants.SUCCESS_ATTRIBUTE);
    	}catch(Exception e){
    		LOG.error("Exception occured in SS-API while updating macro.", e);
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.ADD_MACRO_UNSUCCESSFUL,
					DisplayMessageType.ERROR_MESSAGE).getMessage();
			statusMap.put("STATUS", CommonConstants.ERROR);
    	}
    	
    	statusMap.put(MESSAGE, message);
		statusJson = new Gson().toJson(statusMap);
		
    	return statusJson;
    }
    
    @ResponseBody
    @RequestMapping ( value = "/getsocialpostsforstream", method = RequestMethod.GET)
    public String getSocialPostsForStream(Model model, HttpServletRequest request) {
    	
        LOG.info( "Method to fetch Social Posts for Social Monitor Stream,  getSocialPostsForStream() Started" );
        
        User user = sessionHelper.getCurrentUser();
        Long companyId = user.getCompany().getCompanyId();
       
        String startIndexStr = request.getParameter(START_INDEX);
        String batchSizeStr = request.getParameter(BATCH_SIZE);
        String status = request.getParameter(STATUS);
        String flagStr = request.getParameter(FLAG);
        
        List<String> feedType = new ArrayList<>();
        feedType.add("FACEBOOK");feedType.add("TWITTER");feedType.add("LINKEDIN");
        List<Long> regionIds = null;
        List<Long> branchIds = null;
        List<Long> agentIds = null;
        int startIndex=0;
        int batchSize=10;
        boolean flag = false;
        
        if ( startIndexStr != null && !startIndexStr.isEmpty() ) {
        	startIndex = Integer.valueOf( startIndexStr );
        }
        
        if ( batchSizeStr != null && !batchSizeStr.isEmpty() ) {
        	batchSize = Integer.valueOf( batchSizeStr );
        }
        
       if ( flagStr != null && !flagStr.isEmpty() ) {
        	flag = Boolean.valueOf( flagStr );
        }
       
        Response response = ssApiIntergrationBuilder.getIntegrationApi().showStreamSocialPosts(startIndex, batchSize, status, flag, feedType, companyId, regionIds, branchIds, agentIds);
        		
        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );
       
    }
    
    private SocialFeedsActionUpdate createSFAUFromRequest(HttpServletRequest request, String userName) {
    
    	String postId = request.getParameter("form-post-id");
    	String flaggedStr = request.getParameter("form-flagged");
    	String statusStr = request.getParameter("form-status");
    	String textActType = request.getParameter("form-text-act-type");
    	String text = request.getParameter("form-post-textbox");
    	String macroId = request.getParameter("form-post-act-macro-id");
    	List<String> postIds = new ArrayList<>();
    	postIds.add(postId);
    	SocialFeedsActionUpdate socialFeedsActionUpdate = new SocialFeedsActionUpdate();
    	socialFeedsActionUpdate.setPostIds(postIds);
    	
    	boolean flagged = false;
    	if(flaggedStr!=null && !flaggedStr.isEmpty() && flaggedStr.equalsIgnoreCase("true")) {
    		flagged = true;
    	}
    	socialFeedsActionUpdate.setFlagged(flagged);
    	
    	socialFeedsActionUpdate.setStatus(SocialFeedStatus.valueOf(statusStr));
    	socialFeedsActionUpdate.setTextActionType(TextActionType.valueOf(textActType));
    	socialFeedsActionUpdate.setText(text);
    	socialFeedsActionUpdate.setMacroId(macroId);
    	socialFeedsActionUpdate.setUserName(userName);
    	
    	return socialFeedsActionUpdate;
    }
    
    @ResponseBody
    @RequestMapping ( value = "/updatepostaction", method = RequestMethod.POST)
    public String updatePostAction( Model model, HttpServletRequest request)
    {
    	
    	LOG.info("Method updatePostAction of SocialMonitorWebController called");
    	
    	User user = sessionHelper.getCurrentUser();
        Long companyId = user.getCompany().getCompanyId();
        String userName = user.getProfileName();
        
    	Map<String, String> statusMap = new HashMap<>();
    	String message = "";
    	String statusJson = "";
    	
    	SocialFeedsActionUpdate socialFeedsActionUpdate = createSFAUFromRequest(request,userName);
    	
    	Response response =null;
    	
    	try {
    		response = ssApiIntergrationBuilder.getIntegrationApi().saveSocialFeedsForAction(socialFeedsActionUpdate, companyId);
    		message = messageUtils.getDisplayMessage(DisplayMessageConstants.UPDATE_POST_SUCCESSFUL,
    				DisplayMessageType.SUCCESS_MESSAGE).getMessage();
    		String status = new String(((TypedByteArray) response.getBody()).getBytes());
    		statusMap.put("userName", userName);
    		statusMap.put("success", status);
    		statusMap.put(STATUS, CommonConstants.SUCCESS_ATTRIBUTE);
    	}catch(Exception e){
    		LOG.error("Exception occured in SS-API while updating post action.", e);
    		message = messageUtils.getDisplayMessage(DisplayMessageConstants.UPDATE_POST_UNSUCCESSFUL,
    				DisplayMessageType.ERROR_MESSAGE).getMessage();
    		statusMap.put("STATUS", CommonConstants.ERROR);
    	}
    	
    	statusMap.put(MESSAGE, message);
    	statusJson = new Gson().toJson(statusMap);
    	
    	return statusJson;
    }
    
    private SocialFeedsActionUpdate createSFAUFromRequestForMacro(HttpServletRequest request, String userName) {
        
    	String postId = request.getParameter("macro-form-post-id");
    	String flaggedStr = request.getParameter("macro-form-flagged");
    	String statusStr = request.getParameter("macro-form-status");
    	String textActType = request.getParameter("macroform-text-act-type");
    	String text = request.getParameter("macro-form-text");
    	String macroId = request.getParameter("macro-form-macro-id");
    	List<String> postIds = new ArrayList<>();
    	postIds.add(postId);
    	
    	SocialFeedsActionUpdate socialFeedsActionUpdate = new SocialFeedsActionUpdate();
    	socialFeedsActionUpdate.setPostIds(postIds);
    	
    	boolean flagged = false;
    	if(flaggedStr!=null && !flaggedStr.isEmpty() && flaggedStr.equalsIgnoreCase("true")) {
    		flagged = true;
    	}
    	socialFeedsActionUpdate.setFlagged(flagged);
    	
    	socialFeedsActionUpdate.setStatus(SocialFeedStatus.valueOf(statusStr));
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
        String userName = user.getProfileName();
        
    	Map<String, String> statusMap = new HashMap<>();
    	String message = "";
    	String statusJson = "";
    	
    	SocialFeedsActionUpdate socialFeedsActionUpdate = createSFAUFromRequestForMacro(request, userName);
    	
    	Response response =null;
    	
    	try {
    		response = ssApiIntergrationBuilder.getIntegrationApi().saveSocialFeedsForAction(socialFeedsActionUpdate, companyId);
    		message = messageUtils.getDisplayMessage(DisplayMessageConstants.UPDATE_POST_SUCCESSFUL,
    				DisplayMessageType.SUCCESS_MESSAGE).getMessage();
    		String status = new String(((TypedByteArray) response.getBody()).getBytes());
    		statusMap.put("userName", userName);
    		statusMap.put("success", status);
    		statusMap.put(STATUS, CommonConstants.SUCCESS_ATTRIBUTE);
    	}catch(Exception e){
    		LOG.error("Exception occured in SS-API while updating post action.", e);
    		message = messageUtils.getDisplayMessage(DisplayMessageConstants.UPDATE_POST_UNSUCCESSFUL,
    				DisplayMessageType.ERROR_MESSAGE).getMessage();
    		statusMap.put("STATUS", CommonConstants.ERROR);
    	}
    	
    	statusMap.put(MESSAGE, message);
    	statusJson = new Gson().toJson(statusMap);
    	
    	return statusJson;
    }

}
