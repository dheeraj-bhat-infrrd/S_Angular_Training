package com.realtech.socialsurvey.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.maps.model.LatLng;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.vo.AdvancedSearchVO;
import com.realtech.socialsurvey.web.api.builder.SSApiIntergrationBuilder;
import com.realtech.socialsurvey.web.common.JspResolver;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


@Controller
public class SearchEngineWebController
{

    private static final Logger LOG = LoggerFactory.getLogger(SearchEngineWebController.class);

    @Autowired
    private SSApiIntergrationBuilder ssApiIntergrationBuilder;

    @Value ( "${APPLICATION_BASE_URL}")
    private String applicationBaseUrl;


    @RequestMapping ( value = "/showsearchenginepage", method = RequestMethod.GET)
    public String showSearchEnginePage( Model model, HttpServletRequest request, @QueryParam ( value = "basedOn") String basedOn, @QueryParam ( value = "companyProfileName") String companyProfileName )
    {

        LOG.info( "Method showSearchEnginePage() called from SearchEngineWebController" );
        model.addAttribute("title" , "Search Professionals | SocialSurvey.me");
        if((basedOn == null || basedOn.isEmpty())&&(companyProfileName == null || companyProfileName.isEmpty())) {
            model.addAttribute( "isPubPageSearch", false );
            model.addAttribute( "companyProfileName", "" );
            model.addAttribute( "basedOn", "" );
        }else {
            model.addAttribute( "basedOn", basedOn );
            model.addAttribute( "isPubPageSearch", true );
            model.addAttribute( "companyProfileName", companyProfileName );
        }
        
        return JspResolver.SEARCH_ENGINE;
    }

    @ResponseBody
    @RequestMapping ( value = "/searchengine/applosetting", method = RequestMethod.GET)
    public String getApplicationLOSettings( Model model, HttpServletRequest request )
    {

        LOG.info( "Method getApplicationLOSettings() called from SearchEngineWebController" );
        
        Response response = ssApiIntergrationBuilder.getIntegrationApi().getApplicationLOSetttings();

        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );
    }

    @ResponseBody
    @RequestMapping ( value = "/searchengine/searchresults", method = RequestMethod.POST)
    public String getSearchResults( Model model, HttpServletRequest request )
    {

        LOG.info( "Method getSearchResults() called from SearchEngineWebController" );
        
        AdvancedSearchVO advancedSearchVO = new AdvancedSearchVO();

        try {
            JSONObject payload = new JSONObject(request.getParameter( "payload" ));
            LOG.info( payload.toString() );
            String companyProfileName = payload.getString( "companyProfileName" );
            long startIndex = payload.getLong( "startIndex" );
            long batchSize = payload.getLong( "batchSize" );
            String lngStr = payload.getString( "lng" );
            String latStr = payload.getString( "lat" );
            String findBasedOn = payload.getString( "findBasedOn" );
            JSONArray categoryFilterListJarray = payload.getJSONArray( "categoryFilterList" );
            String sortBy = payload.getString( "sortBy" );
            long distanceCriteriaStr= payload.getLong( "distanceCriteria" );
            long reviewCountCriteriaStr = payload.getLong( "reviewCountCriteria" );
            long ratingCriteria = payload.getLong( "ratingCriteria" );
            String profileFilter = payload.getString( "profileFilter" );
            String cityName = payload.getString("cityName");
            String stateCode = payload.getString("stateCode");
            
            long distanceCriteria = CommonConstants.DEFAULT_DISTANCE_CRITERIA;
            long reviewCountCriteria = CommonConstants.DEFAULT_REVIEW_COUNT_CRITERIA;
            double lat = 0.0;
            double lng = 0.0;

            if ( distanceCriteriaStr != 0) {
                distanceCriteria = Long.valueOf( distanceCriteriaStr );
            }
            
            if ( reviewCountCriteriaStr != 0) {
                reviewCountCriteria = Long.valueOf( reviewCountCriteriaStr );
            }

            if ( latStr != null && !latStr.isEmpty() ) {
                lat = Double.valueOf( latStr );
            }

            if ( lngStr != null && !lngStr.isEmpty() ) {
                lng = Double.valueOf( lngStr );
            }
            
            List<String> categoryFilterList = new ArrayList<>();
            for(int i=0;i<categoryFilterListJarray.length();i++) {
                categoryFilterList.add( categoryFilterListJarray.getString( i ) );
            }
            
            LatLng latLng = new LatLng( lat, lng );
            
            advancedSearchVO.setBatchSize( batchSize );
            advancedSearchVO.setStartIndex( startIndex );
            advancedSearchVO.setSortBy( sortBy );
            advancedSearchVO.setProfileFilter( profileFilter );
            advancedSearchVO.setFindBasedOn( findBasedOn );
            advancedSearchVO.setCategoryFilterList( categoryFilterList );
            advancedSearchVO.setDistanceCriteria( distanceCriteria );
            if(!(lat==0 && lng==0)){
                advancedSearchVO.setNearLocation( latLng );
            }
            advancedSearchVO.setRatingCriteria( ratingCriteria );
            advancedSearchVO.setReviewCountCriteria( reviewCountCriteria );
            advancedSearchVO.setCompanyProfileName(companyProfileName);
            advancedSearchVO.setCityName(cityName);
            advancedSearchVO.setStateCode(stateCode);
            
            Response response = ssApiIntergrationBuilder.getIntegrationApi().getSearchResults( advancedSearchVO );

            return new String( ( (TypedByteArray) response.getBody() ).getBytes() );
            
        } catch ( JSONException e ) {
            e.printStackTrace();
            LOG.error( "Error while getting payload : " + e.getMessage());
            return "error";
        }        
    }
    
    @ResponseBody
    @RequestMapping ( value = "/searchengine/nearmesuggestions", method = RequestMethod.GET)
    public String getNearMeSuggestions( Model model, HttpServletRequest request )
    {
        String searchString = request.getParameter( "searchString" );
        int startIndex = 0;
        int batchSize = 15;
        
        String startIndexStr = request.getParameter( "startIndex" );
        String batchSizeStr = request.getParameter( "batchSize" );
        
        
        if ( startIndexStr != null && !startIndexStr.isEmpty() ) {
            startIndex = Integer.valueOf( startIndexStr );
        }
        
        if ( batchSizeStr != null && !batchSizeStr.isEmpty() ) {
            batchSize = Integer.valueOf( batchSizeStr );
        }

        Response response = ssApiIntergrationBuilder.getIntegrationApi().getNearMe( searchString,startIndex,batchSize );

        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );
    }
    
    @ResponseBody
    @RequestMapping ( value = "/searchengine/searchresults/count", method = RequestMethod.POST)
    public String getSearchResultsCount( Model model, HttpServletRequest request )
    {

        LOG.info( "Method getSearchResultsCount() called from SearchEngineWebController" );
        
        AdvancedSearchVO advancedSearchVO = new AdvancedSearchVO();

        try {
            JSONObject payload = new JSONObject(request.getParameter( "payload" ));
            LOG.info( payload.toString() );
            String companyProfileName = payload.getString( "companyProfileName" );
            long startIndex = payload.getLong( "startIndex" );
            long batchSize = payload.getLong( "batchSize" );
            String lngStr = payload.getString( "lng" );
            String latStr = payload.getString( "lat" );
            String findBasedOn = payload.getString( "findBasedOn" );
            JSONArray categoryFilterListJarray = payload.getJSONArray( "categoryFilterList" );
            String sortBy = payload.getString( "sortBy" );
            long distanceCriteriaStr= payload.getLong( "distanceCriteria" );
            long reviewCountCriteriaStr = payload.getLong( "reviewCountCriteria" );
            long ratingCriteria = payload.getLong( "ratingCriteria" );
            String profileFilter = payload.getString( "profileFilter" );
            String cityName = payload.getString("cityName");
            String stateCode = payload.getString("stateCode");
            
            long distanceCriteria = CommonConstants.DEFAULT_DISTANCE_CRITERIA;
            long reviewCountCriteria = CommonConstants.DEFAULT_REVIEW_COUNT_CRITERIA;
            double lat = 0.0;
            double lng = 0.0;

            if ( distanceCriteriaStr != 0) {
                distanceCriteria = Long.valueOf( distanceCriteriaStr );
            }
            
            if ( reviewCountCriteriaStr != 0) {
                reviewCountCriteria = Long.valueOf( reviewCountCriteriaStr );
            }

            if ( latStr != null && !latStr.isEmpty() ) {
                lat = Double.valueOf( latStr );
            }

            if ( lngStr != null && !lngStr.isEmpty() ) {
                lng = Double.valueOf( lngStr );
            }
            
            List<String> categoryFilterList = new ArrayList<>();
            for(int i=0;i<categoryFilterListJarray.length();i++) {
                categoryFilterList.add( categoryFilterListJarray.getString( i ) );
            }
            
            LatLng latLng = new LatLng( lat, lng );
            
            advancedSearchVO.setBatchSize( batchSize );
            advancedSearchVO.setStartIndex( startIndex );
            advancedSearchVO.setSortBy( sortBy );
            advancedSearchVO.setProfileFilter( profileFilter );
            advancedSearchVO.setFindBasedOn( findBasedOn );
            advancedSearchVO.setCategoryFilterList( categoryFilterList );
            advancedSearchVO.setDistanceCriteria( distanceCriteria );
            if(!(lat==0 && lng==0)){
                advancedSearchVO.setNearLocation( latLng );
            }
            advancedSearchVO.setRatingCriteria( ratingCriteria );
            advancedSearchVO.setReviewCountCriteria( reviewCountCriteria );
            advancedSearchVO.setCompanyProfileName(companyProfileName);
            advancedSearchVO.setCityName(cityName);
            advancedSearchVO.setStateCode(stateCode);
            
            Response response = ssApiIntergrationBuilder.getIntegrationApi().getSearchResultsCount( advancedSearchVO );

            return new String( ( (TypedByteArray) response.getBody() ).getBytes() );
            
        } catch ( JSONException e ) {
            e.printStackTrace();
            LOG.error( "Error while getting payload : " + e.getMessage());
            return "error";
        }               
    }
}
