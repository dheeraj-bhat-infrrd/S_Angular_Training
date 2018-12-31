package com.realtech.socialsurvey.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;

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

        String startIndexStr = request.getParameter( "startIndex" );
        String batchSizeStr = request.getParameter( "batchSize" );
        String sortBy = request.getParameter( "sortBy" );
        String distanceCriteriaStr = request.getParameter( "distanceCriteria" );
        String ratingCriteriaStr = request.getParameter( "ratingCriteria" );
        String reviewCountCriteriaStr = request.getParameter( "reviewCountCriteria" );
        String latStr = request.getParameter( "lat" );
        String lngStr = request.getParameter( "lng" );
        String profileFilter = request.getParameter( "profileFilter" );
        String findBasedOn = request.getParameter( "findBasedOn" );
        String categoryFilterListStr = request.getParameter( "categoryFilterList" );
        String companyProfileName = request.getParameter( "companyProfileName" );

        long startIndex = CommonConstants.SEARCH_ENGINE_START_INDEX;
        long batchSize = CommonConstants.SEARCH_ENGINE_START_INDEX;
        long distanceCriteria = CommonConstants.DEFAULT_DISTANCE_CRITERIA;
        long ratingCriteria = CommonConstants.DEFAULT_RATING_CRITERIA;
        long reviewCountCriteria = CommonConstants.DEFAULT_REVIEW_COUNT_CRITERIA;
        double lat = 0.0;
        double lng = 0.0;

        if ( startIndexStr != null && !startIndexStr.isEmpty() ) {
            startIndex = Long.valueOf( startIndexStr );
        }

        if ( batchSizeStr != null && !batchSizeStr.isEmpty() ) {
            batchSize = Long.valueOf( batchSizeStr );
        }

        if ( distanceCriteriaStr != null && !distanceCriteriaStr.isEmpty() ) {
            distanceCriteria = Long.valueOf( distanceCriteriaStr );
        }

        if ( ratingCriteriaStr != null && !ratingCriteriaStr.isEmpty() ) {
            ratingCriteria = Long.valueOf( ratingCriteriaStr );
        }

        if ( reviewCountCriteriaStr != null && !reviewCountCriteriaStr.isEmpty() ) {
            reviewCountCriteria = Long.valueOf( reviewCountCriteriaStr );
        }

        if ( latStr != null && !latStr.isEmpty() ) {
            lat = Double.valueOf( latStr );
        }

        if ( lngStr != null && !lngStr.isEmpty() ) {
            lng = Double.valueOf( lngStr );
        }
        
        List<String> categoryFilterList = new ArrayList<>();

        if ( categoryFilterListStr != null && !categoryFilterListStr.isEmpty() ) {
            for ( String categoryFilter : categoryFilterListStr.split( "," ) ) {
                categoryFilterList.add( categoryFilter );
            }
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
        
        Response response = ssApiIntergrationBuilder.getIntegrationApi().getSearchResults( advancedSearchVO );

        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );
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

        String startIndexStr = request.getParameter( "startIndex" );
        String batchSizeStr = request.getParameter( "batchSize" );
        String sortBy = request.getParameter( "sortBy" );
        String distanceCriteriaStr = request.getParameter( "distanceCriteria" );
        String ratingCriteriaStr = request.getParameter( "ratingCriteria" );
        String reviewCountCriteriaStr = request.getParameter( "reviewCountCriteria" );
        String latStr = request.getParameter( "lat" );
        String lngStr = request.getParameter( "lng" );
        String profileFilter = request.getParameter( "profileFilter" );
        String findBasedOn = request.getParameter( "findBasedOn" );
        String categoryFilterListStr = request.getParameter( "categoryFilterList" );
        String companyProfileName = request.getParameter( "companyProfileName" );

        long startIndex = CommonConstants.SEARCH_ENGINE_START_INDEX;
        long batchSize = CommonConstants.SEARCH_ENGINE_START_INDEX;
        long distanceCriteria = CommonConstants.DEFAULT_DISTANCE_CRITERIA;
        long ratingCriteria = CommonConstants.DEFAULT_RATING_CRITERIA;
        long reviewCountCriteria = CommonConstants.DEFAULT_REVIEW_COUNT_CRITERIA;
        double lat = 0l;
        double lng = 0l;

        if ( startIndexStr != null && !startIndexStr.isEmpty() ) {
            startIndex = Long.valueOf( startIndexStr );
        }

        if ( batchSizeStr != null && !batchSizeStr.isEmpty() ) {
            batchSize = Long.valueOf( batchSizeStr );
        }

        if ( distanceCriteriaStr != null && !distanceCriteriaStr.isEmpty() ) {
            distanceCriteria = Long.valueOf( distanceCriteriaStr );
        }

        if ( ratingCriteriaStr != null && !ratingCriteriaStr.isEmpty() ) {
            ratingCriteria = Long.valueOf( ratingCriteriaStr );
        }

        if ( reviewCountCriteriaStr != null && !reviewCountCriteriaStr.isEmpty() ) {
            reviewCountCriteria = Long.valueOf( reviewCountCriteriaStr );
        }

        if ( latStr != null && !latStr.isEmpty() ) {
            lat = Double.valueOf( latStr );
        }

        if ( lngStr != null && !lngStr.isEmpty() ) {
            lng = Double.valueOf( lngStr );
        }

        List<String> categoryFilterList = new ArrayList<>();

        if ( categoryFilterListStr != null && !categoryFilterListStr.isEmpty() ) {
            for ( String categoryFilter : categoryFilterListStr.split( "," ) ) {
                categoryFilterList.add( categoryFilter );
            }
        }

        LatLng latLng = new LatLng( lat, lng );
        
        advancedSearchVO.setBatchSize( batchSize );
        advancedSearchVO.setStartIndex( startIndex );
        advancedSearchVO.setSortBy( sortBy );
        advancedSearchVO.setProfileFilter( profileFilter );
        advancedSearchVO.setFindBasedOn( findBasedOn );
        advancedSearchVO.setCategoryFilterList( categoryFilterList );
        advancedSearchVO.setDistanceCriteria( distanceCriteria );
        advancedSearchVO.setNearLocation( latLng );
        advancedSearchVO.setRatingCriteria( ratingCriteria );
        advancedSearchVO.setReviewCountCriteria( reviewCountCriteria );
        advancedSearchVO.setCompanyProfileName(companyProfileName);
        
        Response response = ssApiIntergrationBuilder.getIntegrationApi().getSearchResultsCount( advancedSearchVO );

        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );
    }
}
