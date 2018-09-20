package com.realtech.socialsurvey.core.services.widget.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.widget.WidgetConfiguration;
import com.realtech.socialsurvey.core.entities.widget.WidgetConfigurationRequest;
import com.realtech.socialsurvey.core.entities.widget.WidgetHistory;
import com.realtech.socialsurvey.core.entities.widget.WidgetLockHistory;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.widget.WidgetManagementService;


@Component
public class WidgetManagementServiceImpl implements WidgetManagementService
{
    private static final Logger LOG = LoggerFactory.getLogger( WidgetManagementServiceImpl.class );


    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private ProfileManagementService profileManagementService;

    @Resource
    @Qualifier ( "branch")
    private BranchDao branchDao;

    @Autowired
    private ProcessWidgetOverrideAndLock processWidgetOverrideAndLock;


    @Value ( "${APPLICATION_BASE_URL}")
    private String applicationBaseUrl;


    @Override
    public WidgetConfiguration saveWidgetConfigurationForEntity( long entityId, String entityType, long userId,
        WidgetConfigurationRequest widgetConfigurationRequest ) throws InvalidInputException
    {
        LOG.info( "Started saveWidgetConfigurationForEntity() method." );
        WidgetConfiguration widgetConfiguration = null;
        Map<String, String> historyMap = new HashMap<>();

        //check if widget configuration is present in the unitSettings else add default configuration
        widgetConfiguration = getWidgetConfigurationForEntity( entityId, entityType, true );

        //configuring widget and adding to history
        buildHistoryMap( widgetConfigurationRequest, widgetConfiguration, historyMap );

        boolean override = StringUtils.equals( widgetConfigurationRequest.getOverrideLowerHierarchy(), "true" );
        boolean overrideAndLock = StringUtils.equals( widgetConfigurationRequest.getLockLowerHierarchy(), "true" );

        if ( historyMap.isEmpty() && !override
            && Boolean.compare( hasLockedLowerHierarchy( widgetConfiguration ), overrideAndLock ) == 0 && !overrideAndLock ) {
            LOG.warn( "Nothing to save" );
            throw new InvalidInputException( "Nothing to save" );
        }


        // get the saved mongoDB config
        widgetConfiguration = getWidgetConfigurationForEntity( entityId, entityType, false );

        if ( widgetConfiguration == null ) {
            widgetConfiguration = new WidgetConfiguration();
        }

        if ( !historyMap.isEmpty() ) {
            saveChangesToconfiguration( widgetConfiguration, historyMap );
        }

        // process overriding widget configuration and or lock them in
        if ( override ) {
            processWidgetOverrideAndLock.processOverrideAndLock( entityId, entityType, userId, widgetConfigurationRequest,
                false, false, true );
        } else if ( overrideAndLock
            || Boolean.compare( hasLockedLowerHierarchy( widgetConfiguration ), overrideAndLock ) != 0 ) {
            updateLockSettingsLog( widgetConfiguration, overrideAndLock );
            processWidgetOverrideAndLock.processOverrideAndLock( entityId, entityType, userId,
                createWidgetRequestCopy( widgetConfigurationRequest ), true, overrideAndLock, false );
        }


        //setting history
        WidgetHistory history = new WidgetHistory();

        history.setTimestamp( System.currentTimeMillis() );
        history.setUserId( userId );
        history.setChanges( historyMap );
        history.setRequestMessage( widgetConfigurationRequest.getRequestMessage() );

        if ( widgetConfiguration.getHistory() == null ) {
            widgetConfiguration.setHistory( new ArrayList<WidgetHistory>() );
        }

        widgetConfiguration.getHistory().add( history );

        saveConfigurationInMongo( entityType, entityId, widgetConfiguration );
        LOG.info( "Finished saveWidgetConfigurationForEntity() method." );
        return widgetConfiguration;

    }


    private WidgetConfigurationRequest createWidgetRequestCopy( WidgetConfigurationRequest widgetConfigurationRequest )
    {
        WidgetConfigurationRequest request = new WidgetConfigurationRequest();
        request.setButtonOneLink( widgetConfigurationRequest.getButtonOneLink() );
        request.setAllowModestBranding( widgetConfigurationRequest.getAllowModestBranding() );
        request.setBackgroundColor( widgetConfigurationRequest.getBackgroundColor() );
        request.setBarGraphColor( widgetConfigurationRequest.getBarGraphColor() );
        request.setButtonOneName( widgetConfigurationRequest.getButtonOneName() );
        request.setButtonOneOpacity( widgetConfigurationRequest.getButtonOneOpacity() );
        request.setButtonTwoLink( widgetConfigurationRequest.getButtonTwoLink() );
        request.setButtonTwoName( widgetConfigurationRequest.getButtonTwoName() );
        request.setButtonTwoOpacity( widgetConfigurationRequest.getButtonTwoOpacity() );
        request.setEmbeddedFontTheme( widgetConfigurationRequest.getEmbeddedFontTheme() );
        request.setFont( widgetConfigurationRequest.getFont() );
        request.setFontTheme( widgetConfigurationRequest.getFontTheme() );
        request.setForegroundColor( widgetConfigurationRequest.getForegroundColor() );
        request.setHideBarGraph( widgetConfigurationRequest.getHideBarGraph() );
        request.setHideOptions( widgetConfigurationRequest.getHideOptions() );
        request.setInitialNumberOfReviews( widgetConfigurationRequest.getInitialNumberOfReviews() );
        request.setLockLowerHierarchy( widgetConfigurationRequest.getOverrideLowerHierarchy() );
        request.setMaxReviewsOnLoadMore( widgetConfigurationRequest.getMaxReviewsOnLoadMore() );
        request.setOverrideLowerHierarchy( widgetConfigurationRequest.getOverrideLowerHierarchy() );
        request.setRatingAndStarColor( widgetConfigurationRequest.getRatingAndStarColor() );
        request.setRequestMessage( widgetConfigurationRequest.getRequestMessage() );
        request.setReviewLoaderName( widgetConfigurationRequest.getReviewLoaderName() );
        request.setReviewLoaderOpacity( widgetConfigurationRequest.getReviewLoaderOpacity() );
        request.setReviewSortOrder( widgetConfigurationRequest.getReviewSortOrder() );
        request.setReviewSources( widgetConfigurationRequest.getReviewSources() );
        request.setSeoDescription( widgetConfigurationRequest.getSeoDescription() );
        request.setSeoKeywords( widgetConfigurationRequest.getSeoKeywords() );
        request.setSeoTitle( widgetConfigurationRequest.getSeoTitle() );
        return request;
    }


    @Override
    public boolean hasLockedLowerHierarchy( WidgetConfiguration widgetConfiguration )
    {
        if ( widgetConfiguration != null && widgetConfiguration.getLockHistory() != null ) {
            if ( !widgetConfiguration.getLockHistory().isEmpty() ) {
                return StringUtils.equals(
                    widgetConfiguration.getLockHistory().get( widgetConfiguration.getLockHistory().size() - 1 ).getAction(),
                    CommonConstants.WIDGET_LOCK );
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    @Override
    public WidgetConfiguration resetWidgetConfigurationForEntity( long entityId, String entityType, long userId,
        String requestMessage ) throws InvalidInputException
    {
        LOG.info( "Started resetWidgetConfigurationForEntity() method." );
        WidgetConfiguration widgetConfiguration = null;

        OrganizationUnitSettings unitSettings = organizationManagementService.getEntitySettings( entityId, entityType );
        widgetConfiguration = unitSettings.getWidgetConfiguration();
        Map<String, String> historyMap = getDefaultConfigurationMap( unitSettings, entityType, entityId );


        if ( widgetConfiguration == null ) {
            widgetConfiguration = new WidgetConfiguration();
        } else {

            // keep only history
            if ( widgetConfiguration.getHistory() != null ) {
                WidgetConfiguration tempWidgetConfig = new WidgetConfiguration();
                tempWidgetConfig.setHistory( widgetConfiguration.getHistory() );
                widgetConfiguration = tempWidgetConfig;
            }
        }


        //setting history
        WidgetHistory history = new WidgetHistory();

        history.setTimestamp( System.currentTimeMillis() );
        history.setUserId( userId );
        history.setChanges( historyMap );
        history.setRequestMessage( requestMessage );


        if ( widgetConfiguration.getHistory() == null ) {
            widgetConfiguration.setHistory( new ArrayList<WidgetHistory>() );
        }

        widgetConfiguration.getHistory().add( history );

        saveConfigurationInMongo( entityType, entityId, widgetConfiguration );
        LOG.info( "Finished resetWidgetConfigurationForEntity() method." );
        return widgetConfiguration;

    }


    private Map<String, String> getDefaultConfigurationMap( OrganizationUnitSettings unitSettings, String entityType,
        long entityId )
    {
        Map<String, String> historyMap = new HashMap<>();

        historyMap.put( CommonConstants.WIDGET_FONT, CommonConstants.WIDGET_DEFAULT_FONT );
        historyMap.put( CommonConstants.WIDGET_BACKGROUND_COLOR, CommonConstants.WIDGET_DEFAULT_BACKGROUND_COLOR );
        historyMap.put( CommonConstants.WIDGET_RATING_AND_STAR_COLOR, CommonConstants.WIDGET_DEFAULT_RATING_AND_STAR_COLOR );
        historyMap.put( CommonConstants.WIDGET_FOREGROUND_COLOR, CommonConstants.WIDGET_DEFAULT_FOREGROUND_COLOR );
        historyMap.put( CommonConstants.WIDGET_FONT_THEME, CommonConstants.WIDGET_DEFAULT_FONT_THEME );
        historyMap.put( CommonConstants.WIDGET_EMBEDDED_FONT_THEME, CommonConstants.WIDGET_DEFAULT_EMBEDDED_FONT_THEME );
        historyMap.put( CommonConstants.WIDGET_BUTTON1_TEXT, CommonConstants.WIDGET_DEFAULT_BUTTON1_TEXT );
        historyMap.put( CommonConstants.WIDGET_BUTTON1_LINK,
            unitSettings.getCompleteProfileUrl() + CommonConstants.CONTACT_US_HASH );
        historyMap.put( CommonConstants.WIDGET_BUTTON1_OPACITY, CommonConstants.WIDGET_DEFAULT_BUTTON1_OPACITY );
        historyMap.put( CommonConstants.WIDGET_BUTTON2_TEXT, CommonConstants.WIDGET_DEFAULT_BUTTON2_TEXT );
        historyMap.put( CommonConstants.WIDGET_BUTTON2_LINK, getDefaultButtonTwoLink( entityType, entityId ) );
        historyMap.put( CommonConstants.WIDGET_BUTTON2_OPACITY, CommonConstants.WIDGET_DEFAULT_BUTTON2_OPACITY );
        historyMap.put( CommonConstants.WIDGET_LOAD_MORE_BUTTON_TEXT, CommonConstants.WIDGET_DEFAULT_LOAD_MORE_BUTTON_TEXT );
        historyMap.put( CommonConstants.WIDGET_LOAD_MORE_BUTTON_OPACITY,
            CommonConstants.WIDGET_DEFAULT_LOAD_MORE_BUTTON_OPACITY );
        historyMap.put( CommonConstants.WIDGET_MAX_REVIEWS_ON_LOAD_MORE,
            CommonConstants.WIDGET_DEFAULT_MAX_REVIEWS_ON_LOAD_MORE );
        historyMap.put( CommonConstants.WIDGET_INITIAL_NUMBER_OF_REVIEWS,
            CommonConstants.WIDGET_DEFAULT_INITIAL_NUMBER_OF_REVIEWS );
        historyMap.put( CommonConstants.WIDGET_HIDE_BAR_GRAPH, CommonConstants.WIDGET_DEFAULT_HIDE_BAR_GRAPH );
        historyMap.put( CommonConstants.WIDGET_HIDE_OPTIONS, CommonConstants.WIDGET_DEFAULT_HIDE_OPTIONS );
        historyMap.put( CommonConstants.WIDGET_SORT_ORDER, CommonConstants.WIDGET_DEFAULT_SORT_ORDER );
        historyMap.put( CommonConstants.WIDGET_ALLOW_MODEST_BRANDING, CommonConstants.WIDGET_DEFAULT_ALLOW_MODEST_BRANDING );
        historyMap.put( CommonConstants.WIDGET_LOCK_LOWER_HIERARCHY, CommonConstants.WIDGET_DEFAULT_LOCK_LOWER_HIERARCHY );
        historyMap.put( CommonConstants.WIDGET_BAR_GRAPH_COLOR, null );
        historyMap.put( CommonConstants.WIDGET_REVIEW_SOURCES, null );
        historyMap.put( CommonConstants.WIDGET_SEO_TITLE, null );
        historyMap.put( CommonConstants.WIDGET_SEO_KEYWORDS, null );
        historyMap.put( CommonConstants.WIDGET_SEO_DESCRIPTION, null );
        historyMap.put( CommonConstants.WIDGET_HIDE_CONTACT, CommonConstants.WIDGET_DEFAULT_HIDE_CONTACT );
        historyMap.put( CommonConstants.WIDGET_HIDE_REVIEW, CommonConstants.WIDGET_DEFAULT_HIDE_REVIEW );
        historyMap.put( CommonConstants.WIDGET_MAX_BUTTON_SIZE, CommonConstants.WIDGET_DEFAULT_BUTTON_SIZE );

        return historyMap;
    }


    private void buildHistoryMap( WidgetConfigurationRequest widgetConfigurationRequest,
        WidgetConfiguration widgetConfiguration, Map<String, String> historyMap )
    {
        if ( StringUtils.isNotEmpty( widgetConfigurationRequest.getFont() ) ) {
            if ( !StringUtils.equals( widgetConfigurationRequest.getFont(), widgetConfiguration.getFont() ) ) {
                historyMap.put( CommonConstants.WIDGET_FONT, widgetConfigurationRequest.getFont() );
                widgetConfiguration.setFont( widgetConfigurationRequest.getFont() );
            }
        }

        if ( StringUtils.isNotEmpty( widgetConfigurationRequest.getBackgroundColor() ) ) {
            if ( !StringUtils.equals( widgetConfigurationRequest.getBackgroundColor(),
                widgetConfiguration.getBackgroundColor() ) ) {
                historyMap.put( CommonConstants.WIDGET_BACKGROUND_COLOR, widgetConfigurationRequest.getBackgroundColor() );
                widgetConfiguration.setBackgroundColor( widgetConfigurationRequest.getBackgroundColor() );
            }
        }

        if ( StringUtils.isNotEmpty( widgetConfigurationRequest.getRatingAndStarColor() ) ) {
            if ( !StringUtils.equals( widgetConfigurationRequest.getRatingAndStarColor(),
                widgetConfiguration.getRatingAndStarColor() ) ) {
                historyMap.put( CommonConstants.WIDGET_RATING_AND_STAR_COLOR,
                    widgetConfigurationRequest.getRatingAndStarColor() );
                widgetConfiguration.setRatingAndStarColor( widgetConfigurationRequest.getRatingAndStarColor() );
            }
        }


        if ( StringUtils.isEmpty( widgetConfigurationRequest.getBarGraphColor() )
            && StringUtils.isNotEmpty( widgetConfiguration.getBarGraphColor() ) ) {
            widgetConfiguration.setBarGraphColor( null );
            historyMap.put( CommonConstants.WIDGET_BAR_GRAPH_COLOR, null );
        } else if ( !( StringUtils.isEmpty( widgetConfiguration.getBarGraphColor() )
            && StringUtils.isEmpty( widgetConfigurationRequest.getBarGraphColor() ) ) ) {

            if ( !StringUtils.equals( widgetConfiguration.getBarGraphColor(),
                widgetConfigurationRequest.getBarGraphColor() ) ) {
                widgetConfiguration.setBarGraphColor( widgetConfigurationRequest.getBarGraphColor() );
                historyMap.put( CommonConstants.WIDGET_BAR_GRAPH_COLOR, widgetConfigurationRequest.getBarGraphColor() );
            }
        }


        if ( StringUtils.isNotEmpty( widgetConfigurationRequest.getForegroundColor() ) ) {
            if ( !StringUtils.equals( widgetConfigurationRequest.getForegroundColor(),
                widgetConfiguration.getForegroundColor() ) ) {
                historyMap.put( CommonConstants.WIDGET_FOREGROUND_COLOR, widgetConfigurationRequest.getForegroundColor() );
                widgetConfiguration.setForegroundColor( widgetConfigurationRequest.getForegroundColor() );
            }
        }

        if ( StringUtils.isNotEmpty( widgetConfigurationRequest.getFontTheme() ) ) {
            if ( !StringUtils.equals( widgetConfigurationRequest.getFontTheme(), widgetConfiguration.getFontTheme() ) ) {
                historyMap.put( CommonConstants.WIDGET_FONT_THEME, widgetConfigurationRequest.getFontTheme() );
                widgetConfiguration.setFontTheme( widgetConfigurationRequest.getFontTheme() );
            }
        }

        if ( StringUtils.isNotEmpty( widgetConfigurationRequest.getEmbeddedFontTheme() ) ) {
            if ( !StringUtils.equals( widgetConfigurationRequest.getEmbeddedFontTheme(),
                widgetConfiguration.getEmbeddedFontTheme() ) ) {
                historyMap.put( CommonConstants.WIDGET_EMBEDDED_FONT_THEME, widgetConfigurationRequest.getEmbeddedFontTheme() );
                widgetConfiguration.setEmbeddedFontTheme( widgetConfigurationRequest.getEmbeddedFontTheme() );
            }
        }

        if ( StringUtils.isNotEmpty( widgetConfigurationRequest.getButtonOneName() ) ) {
            if ( !StringUtils.equals( widgetConfigurationRequest.getButtonOneName(),
                widgetConfiguration.getButtonOneName() ) ) {
                historyMap.put( CommonConstants.WIDGET_BUTTON1_TEXT, widgetConfigurationRequest.getButtonOneName() );
                widgetConfiguration.setButtonOneName( widgetConfigurationRequest.getButtonOneName() );
            }
        }

        if ( StringUtils.isNotEmpty( widgetConfigurationRequest.getButtonOneLink() ) ) {
            if ( !StringUtils.equals( widgetConfigurationRequest.getButtonOneLink(),
                widgetConfiguration.getButtonOneLink() ) ) {
                historyMap.put( CommonConstants.WIDGET_BUTTON1_LINK, widgetConfigurationRequest.getButtonOneLink() );
                widgetConfiguration.setButtonOneLink( widgetConfigurationRequest.getButtonOneLink() );
            }
        }


        if ( StringUtils.isNotEmpty( widgetConfigurationRequest.getButtonOneOpacity() ) ) {
            if ( !StringUtils.equals( widgetConfigurationRequest.getButtonOneOpacity(),
                widgetConfiguration.getButtonOneOpacity() ) ) {
                historyMap.put( CommonConstants.WIDGET_BUTTON1_OPACITY, widgetConfigurationRequest.getButtonOneOpacity() );
                widgetConfiguration.setButtonOneOpacity( widgetConfigurationRequest.getButtonOneOpacity() );
            }
        }

        if ( StringUtils.isNotEmpty( widgetConfigurationRequest.getButtonTwoName() ) ) {
            if ( !StringUtils.equals( widgetConfigurationRequest.getButtonTwoName(),
                widgetConfiguration.getButtonTwoName() ) ) {
                historyMap.put( CommonConstants.WIDGET_BUTTON2_TEXT, widgetConfigurationRequest.getButtonTwoName() );
                widgetConfiguration.setButtonTwoName( widgetConfigurationRequest.getButtonTwoName() );
            }
        }

        if ( StringUtils.isNotEmpty( widgetConfigurationRequest.getButtonTwoLink() ) ) {
            if ( !StringUtils.equals( widgetConfigurationRequest.getButtonTwoLink(),
                widgetConfiguration.getButtonTwoLink() ) ) {
                historyMap.put( CommonConstants.WIDGET_BUTTON2_LINK, widgetConfigurationRequest.getButtonTwoLink() );
                widgetConfiguration.setButtonTwoLink( widgetConfigurationRequest.getButtonTwoLink() );
            }
        }

        if ( StringUtils.isNotEmpty( widgetConfigurationRequest.getButtonTwoOpacity() ) ) {
            if ( !StringUtils.equals( widgetConfigurationRequest.getButtonTwoOpacity(),
                widgetConfiguration.getButtonTwoOpacity() ) ) {
                historyMap.put( CommonConstants.WIDGET_BUTTON2_OPACITY, widgetConfigurationRequest.getButtonTwoOpacity() );
                widgetConfiguration.setButtonTwoOpacity( widgetConfigurationRequest.getButtonTwoOpacity() );
            }
        }

        if ( StringUtils.isNotEmpty( widgetConfigurationRequest.getReviewLoaderName() ) ) {
            if ( !StringUtils.equals( widgetConfigurationRequest.getReviewLoaderName(),
                widgetConfiguration.getReviewLoaderName() ) ) {
                historyMap.put( CommonConstants.WIDGET_LOAD_MORE_BUTTON_TEXT,
                    widgetConfigurationRequest.getReviewLoaderName() );
                widgetConfiguration.setReviewLoaderName( widgetConfigurationRequest.getReviewLoaderName() );
            }
        }

        if ( StringUtils.isNotEmpty( widgetConfigurationRequest.getReviewLoaderOpacity() ) ) {
            if ( !StringUtils.equals( widgetConfigurationRequest.getReviewLoaderOpacity(),
                widgetConfiguration.getReviewLoaderOpacity() ) ) {
                historyMap.put( CommonConstants.WIDGET_LOAD_MORE_BUTTON_OPACITY,
                    widgetConfigurationRequest.getReviewLoaderOpacity() );
                widgetConfiguration.setReviewLoaderOpacity( widgetConfigurationRequest.getReviewLoaderOpacity() );
            }
        }

        if ( StringUtils.isNotEmpty( widgetConfigurationRequest.getReviewSortOrder() ) ) {
            if ( !StringUtils.equals( widgetConfigurationRequest.getReviewSortOrder(),
                widgetConfiguration.getReviewSortOrder() ) ) {
                historyMap.put( CommonConstants.WIDGET_SORT_ORDER, widgetConfigurationRequest.getReviewSortOrder() );
                widgetConfiguration.setReviewSortOrder( widgetConfigurationRequest.getReviewSortOrder() );
            }
        }

        if ( StringUtils.isNotEmpty( widgetConfigurationRequest.getMaxReviewsOnLoadMore() ) ) {
            if ( !StringUtils.equals( widgetConfigurationRequest.getMaxReviewsOnLoadMore(),
                widgetConfiguration.getMaxReviewsOnLoadMore() ) ) {
                historyMap.put( CommonConstants.WIDGET_MAX_REVIEWS_ON_LOAD_MORE,
                    widgetConfigurationRequest.getMaxReviewsOnLoadMore() );
                widgetConfiguration.setMaxReviewsOnLoadMore( widgetConfigurationRequest.getMaxReviewsOnLoadMore() );
            }
        }
        
        if ( StringUtils.isNotEmpty( widgetConfigurationRequest.getMaxWidgetBtnSize() ) ) {
            if ( !StringUtils.equals( widgetConfigurationRequest.getMaxWidgetBtnSize(),
                widgetConfiguration.getMaxWidgetBtnSize() ) ) {
                historyMap.put( CommonConstants.WIDGET_MAX_BUTTON_SIZE,
                    widgetConfigurationRequest.getMaxWidgetBtnSize() );
                widgetConfiguration.setMaxWidgetBtnSize( widgetConfigurationRequest.getMaxWidgetBtnSize() );
            }
        }

        if ( StringUtils.isNotEmpty( widgetConfigurationRequest.getInitialNumberOfReviews() ) ) {
            if ( !StringUtils.equals( widgetConfigurationRequest.getInitialNumberOfReviews(),
                widgetConfiguration.getInitialNumberOfReviews() ) ) {
                historyMap.put( CommonConstants.WIDGET_INITIAL_NUMBER_OF_REVIEWS,
                    widgetConfigurationRequest.getInitialNumberOfReviews() );
                widgetConfiguration.setInitialNumberOfReviews( widgetConfigurationRequest.getInitialNumberOfReviews() );
            }
        }

        if ( StringUtils.isNotEmpty( widgetConfigurationRequest.getHideBarGraph() ) ) {
            if ( !StringUtils.equals( widgetConfigurationRequest.getHideBarGraph(), widgetConfiguration.getHideBarGraph() ) ) {
                historyMap.put( CommonConstants.WIDGET_HIDE_BAR_GRAPH, widgetConfigurationRequest.getHideBarGraph() );
                widgetConfiguration.setHideBarGraph( widgetConfigurationRequest.getHideBarGraph() );
            }
        }

        if ( StringUtils.isNotEmpty( widgetConfigurationRequest.getHideOptions() ) ) {
            if ( !StringUtils.equals( widgetConfigurationRequest.getHideOptions(), widgetConfiguration.getHideOptions() ) ) {
                historyMap.put( CommonConstants.WIDGET_HIDE_OPTIONS, widgetConfigurationRequest.getHideOptions() );
                widgetConfiguration.setHideOptions( widgetConfigurationRequest.getHideOptions() );
            }
        }


        if ( StringUtils.isNotEmpty( widgetConfigurationRequest.getAllowModestBranding() ) ) {
            if ( !StringUtils.equals( widgetConfigurationRequest.getAllowModestBranding(),
                widgetConfiguration.getAllowModestBranding() ) ) {
                historyMap.put( CommonConstants.WIDGET_ALLOW_MODEST_BRANDING,
                    widgetConfigurationRequest.getAllowModestBranding() );
                widgetConfiguration.setAllowModestBranding( widgetConfigurationRequest.getAllowModestBranding() );
            }
        }

        if ( StringUtils.isEmpty( widgetConfigurationRequest.getReviewSources() )
            && StringUtils.isNotEmpty( widgetConfiguration.getReviewSources() ) ) {
            widgetConfiguration.setReviewSources( null );
            historyMap.put( CommonConstants.WIDGET_REVIEW_SOURCES, null );
        } else if ( !( StringUtils.isEmpty( widgetConfiguration.getReviewSources() )
            && StringUtils.isEmpty( widgetConfigurationRequest.getReviewSources() ) ) ) {

            if ( !areReviewSourcesEqual( widgetConfiguration.getReviewSources(),
                widgetConfigurationRequest.getReviewSources() ) ) {
                widgetConfiguration.setReviewSources( widgetConfigurationRequest.getReviewSources() );
                historyMap.put( CommonConstants.WIDGET_REVIEW_SOURCES, widgetConfigurationRequest.getReviewSources() );
            }
        }

        if ( StringUtils.isEmpty( widgetConfigurationRequest.getSeoTitle() )
            && StringUtils.isNotEmpty( widgetConfiguration.getSeoTitle() ) ) {
            widgetConfiguration.setSeoTitle( null );
            historyMap.put( CommonConstants.WIDGET_SEO_TITLE, null );
        } else if ( !( StringUtils.isEmpty( widgetConfiguration.getSeoTitle() )
            && StringUtils.isEmpty( widgetConfigurationRequest.getSeoTitle() ) ) ) {
            if ( !StringUtils.equals( widgetConfiguration.getSeoTitle(), widgetConfigurationRequest.getSeoTitle() ) ) {
                widgetConfiguration.setSeoTitle( widgetConfigurationRequest.getSeoTitle() );
                historyMap.put( CommonConstants.WIDGET_SEO_TITLE, widgetConfigurationRequest.getSeoTitle() );
            }
        }

        if ( StringUtils.isEmpty( widgetConfigurationRequest.getSeoKeywords() )
            && StringUtils.isNotEmpty( widgetConfiguration.getSeoKeywords() ) ) {
            widgetConfiguration.setSeoKeywords( null );
            historyMap.put( CommonConstants.WIDGET_SEO_KEYWORDS, null );
        } else if ( !( StringUtils.isEmpty( widgetConfiguration.getSeoKeywords() )
            && StringUtils.isEmpty( widgetConfigurationRequest.getSeoKeywords() ) ) ) {
            if ( !StringUtils.equals( widgetConfiguration.getSeoKeywords(), widgetConfigurationRequest.getSeoKeywords() ) ) {
                widgetConfiguration.setSeoKeywords( widgetConfigurationRequest.getSeoKeywords() );
                historyMap.put( CommonConstants.WIDGET_SEO_KEYWORDS, widgetConfigurationRequest.getSeoKeywords() );
            }
        }

        if ( StringUtils.isEmpty( widgetConfigurationRequest.getSeoDescription() )
            && StringUtils.isNotEmpty( widgetConfiguration.getSeoDescription() ) ) {
            widgetConfiguration.setSeoDescription( null );
            historyMap.put( CommonConstants.WIDGET_SEO_DESCRIPTION, null );
        } else if ( !( StringUtils.isEmpty( widgetConfiguration.getSeoDescription() )
            && StringUtils.isEmpty( widgetConfigurationRequest.getSeoDescription() ) ) ) {
            if ( !StringUtils.equals( widgetConfiguration.getSeoDescription(),
                widgetConfigurationRequest.getSeoDescription() ) ) {
                widgetConfiguration.setSeoDescription( widgetConfigurationRequest.getSeoDescription() );
                historyMap.put( CommonConstants.WIDGET_SEO_DESCRIPTION, widgetConfigurationRequest.getSeoDescription() );
            }
        }

        if ( StringUtils.isNotEmpty( widgetConfigurationRequest.getHideContactBtn()) ) {
            if ( !StringUtils.equals( widgetConfigurationRequest.getHideContactBtn(), widgetConfiguration.getHideContactBtn() ) ) {
                historyMap.put( CommonConstants.WIDGET_HIDE_CONTACT, widgetConfigurationRequest.getHideContactBtn() );
                widgetConfiguration.setHideContactBtn( widgetConfigurationRequest.getHideContactBtn() );
            }
        }
        
        if ( StringUtils.isNotEmpty( widgetConfigurationRequest.getHideReviewBtn()) ) {
            if ( !StringUtils.equals( widgetConfigurationRequest.getHideReviewBtn(), widgetConfiguration.getHideReviewBtn() ) ) {
                historyMap.put( CommonConstants.WIDGET_HIDE_REVIEW, widgetConfigurationRequest.getHideReviewBtn() );
                widgetConfiguration.setHideReviewBtn( widgetConfigurationRequest.getHideReviewBtn() );
            }
        }
    }


    private boolean areReviewSourcesEqual( String reviewSources, String reviewSources2 )
    {
        if ( StringUtils.equals( reviewSources, reviewSources2 )
            || ( ( reviewSources == null || reviewSources == "" ) && ( reviewSources2 == null || reviewSources2 == "" ) ) ) {
            return true;
        }

        String[] str1 = StringUtils.split( reviewSources );
        String[] str2 = StringUtils.split( reviewSources2 );

        if ( str1 != null && str2 != null && str1.length == str2.length ) {
            return new HashSet<String>( Arrays.asList( str2 ) ).equals( new HashSet<String>( Arrays.asList( str1 ) ) );
        } else {
            return false;
        }
    }


    private void saveChangesToconfiguration( WidgetConfiguration widgetConfiguration, Map<String, String> historyMap )
        throws InvalidInputException
    {
        try {
            for ( Entry<String, String> entry : historyMap.entrySet() ) {
                WidgetConfiguration.class.getMethod( "set" + StringUtils.capitalize( entry.getKey() ), String.class )
                    .invoke( widgetConfiguration, entry.getValue() );
            }
        } catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
            | SecurityException e ) {
            LOG.error( "could not update value", e );
            throw new InvalidInputException( "Invalid field to update" );
        }
    }


    @Override
    public WidgetConfiguration getDefaultWidgetConfiguration( OrganizationUnitSettings unitSettings, String unitSettingType )
    {
        LOG.info( "Started getDefaultWidgetConfiguration() method." );
        WidgetConfiguration widgetConfiguration = new WidgetConfiguration();
        List<WidgetHistory> history = new ArrayList<>();

        widgetConfiguration.setFont( CommonConstants.WIDGET_DEFAULT_FONT );
        widgetConfiguration.setBackgroundColor( CommonConstants.WIDGET_DEFAULT_BACKGROUND_COLOR );
        widgetConfiguration.setRatingAndStarColor( CommonConstants.WIDGET_DEFAULT_RATING_AND_STAR_COLOR );
        widgetConfiguration.setForegroundColor( CommonConstants.WIDGET_DEFAULT_FOREGROUND_COLOR );
        widgetConfiguration.setFontTheme( CommonConstants.WIDGET_DEFAULT_FONT_THEME );
        widgetConfiguration.setEmbeddedFontTheme( CommonConstants.WIDGET_DEFAULT_EMBEDDED_FONT_THEME );

        widgetConfiguration.setButtonOneName( CommonConstants.WIDGET_DEFAULT_BUTTON1_TEXT );
        widgetConfiguration.setButtonOneLink( unitSettings.getCompleteProfileUrl() + CommonConstants.CONTACT_US_HASH );
        widgetConfiguration.setButtonOneOpacity( CommonConstants.WIDGET_DEFAULT_BUTTON1_OPACITY );

        widgetConfiguration.setButtonTwoName( CommonConstants.WIDGET_DEFAULT_BUTTON2_TEXT );
        widgetConfiguration.setButtonTwoLink( getDefaultButtonTwoLink( unitSettingType, unitSettings.getIden() ) );
        widgetConfiguration.setButtonTwoOpacity( CommonConstants.WIDGET_DEFAULT_BUTTON2_OPACITY );

        widgetConfiguration.setReviewLoaderName( CommonConstants.WIDGET_DEFAULT_LOAD_MORE_BUTTON_TEXT );
        widgetConfiguration.setReviewLoaderOpacity( CommonConstants.WIDGET_DEFAULT_LOAD_MORE_BUTTON_OPACITY );

        widgetConfiguration.setMaxReviewsOnLoadMore( CommonConstants.WIDGET_DEFAULT_MAX_REVIEWS_ON_LOAD_MORE );
        widgetConfiguration.setInitialNumberOfReviews( CommonConstants.WIDGET_DEFAULT_INITIAL_NUMBER_OF_REVIEWS );
        widgetConfiguration.setHideBarGraph( CommonConstants.WIDGET_DEFAULT_HIDE_BAR_GRAPH );
        widgetConfiguration.setHideOptions( CommonConstants.WIDGET_DEFAULT_HIDE_OPTIONS );
        widgetConfiguration.setReviewSortOrder( CommonConstants.WIDGET_DEFAULT_SORT_ORDER );
        widgetConfiguration.setAllowModestBranding( CommonConstants.WIDGET_DEFAULT_ALLOW_MODEST_BRANDING );
        
        widgetConfiguration.setHideContactBtn(CommonConstants.WIDGET_DEFAULT_HIDE_CONTACT);
        widgetConfiguration.setHideReviewBtn(CommonConstants.WIDGET_DEFAULT_HIDE_REVIEW);
        widgetConfiguration.setMaxWidgetBtnSize(CommonConstants.WIDGET_DEFAULT_BUTTON_SIZE);
        widgetConfiguration.setHistory( history );

        LOG.info( "Finished getDefaultWidgetConfiguration() method." );
        return widgetConfiguration;
    }


    @Override
    public void saveConfigurationInMongo( String entityType, long entityId, WidgetConfiguration widgetConfiguration )
        throws InvalidInputException
    {
        if ( entityId <= 0 ) {
            throw new InvalidInputException( "Id is invalid" );
        }
        if ( widgetConfiguration == null ) {
            throw new InvalidInputException( "Widget Configuration is null" );
        }

        String collection = null;
        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            collection = MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION;
        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            collection = MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION;
        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            collection = MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION;
        } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
            collection = MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION;
        }
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettingsByIden(
            CommonConstants.WIDGET_CONFIGURATION_COLUMN, widgetConfiguration, entityId, collection );

    }


    @Override
    public WidgetConfiguration getWidgetConfigurationForEntity( long entityId, String entityType, boolean fillDefault )
        throws InvalidInputException
    {
        WidgetConfiguration widgetConfiguration = null;
        OrganizationUnitSettings unitSettings = organizationManagementService.getEntitySettings( entityId, entityType );
        WidgetConfiguration defaultConfigration = getDefaultWidgetConfiguration( unitSettings, entityType );

        if ( unitSettings != null && unitSettings.getWidgetConfiguration() != null ) {
            widgetConfiguration = unitSettings.getWidgetConfiguration();

            if ( fillDefault ) {
                // Load up defaults
                if ( StringUtils.isEmpty( widgetConfiguration.getFont() ) ) {
                    widgetConfiguration.setFont( CommonConstants.WIDGET_DEFAULT_FONT );
                }

                if ( StringUtils.isEmpty( widgetConfiguration.getBackgroundColor() ) ) {
                    widgetConfiguration.setBackgroundColor( CommonConstants.WIDGET_DEFAULT_BACKGROUND_COLOR );
                }

                if ( StringUtils.isEmpty( widgetConfiguration.getRatingAndStarColor() ) ) {
                    widgetConfiguration.setRatingAndStarColor( CommonConstants.WIDGET_DEFAULT_RATING_AND_STAR_COLOR );
                }


                if ( StringUtils.isEmpty( widgetConfiguration.getForegroundColor() ) ) {
                    widgetConfiguration.setForegroundColor( CommonConstants.WIDGET_DEFAULT_FOREGROUND_COLOR );
                }

                if ( StringUtils.isEmpty( widgetConfiguration.getFontTheme() ) ) {
                    widgetConfiguration.setFontTheme( CommonConstants.WIDGET_DEFAULT_FONT_THEME );
                }

                if ( StringUtils.isEmpty( widgetConfiguration.getEmbeddedFontTheme() ) ) {
                    widgetConfiguration.setEmbeddedFontTheme( CommonConstants.WIDGET_DEFAULT_EMBEDDED_FONT_THEME );
                }

                if ( StringUtils.isEmpty( widgetConfiguration.getButtonOneName() ) ) {
                    widgetConfiguration.setButtonOneName( CommonConstants.WIDGET_DEFAULT_BUTTON1_TEXT );
                }

                if ( StringUtils.isEmpty( widgetConfiguration.getButtonOneLink() ) ) {
                    widgetConfiguration
                        .setButtonOneLink( unitSettings.getCompleteProfileUrl() + CommonConstants.CONTACT_US_HASH );
                }


                if ( StringUtils.isEmpty( widgetConfiguration.getButtonOneOpacity() ) ) {
                    widgetConfiguration.setButtonOneOpacity( CommonConstants.WIDGET_DEFAULT_BUTTON1_OPACITY );
                }

                if ( StringUtils.isEmpty( widgetConfiguration.getButtonTwoName() ) ) {
                    widgetConfiguration.setButtonTwoName( CommonConstants.WIDGET_DEFAULT_BUTTON2_TEXT );
                }

                if ( StringUtils.isEmpty( widgetConfiguration.getButtonTwoLink() ) ) {
                    widgetConfiguration.setButtonTwoLink( getDefaultButtonTwoLink( entityType, unitSettings.getIden() ) );
                }

                if ( StringUtils.isEmpty( widgetConfiguration.getButtonTwoOpacity() ) ) {
                    widgetConfiguration.setButtonTwoOpacity( CommonConstants.WIDGET_DEFAULT_BUTTON2_OPACITY );
                }

                if ( StringUtils.isEmpty( widgetConfiguration.getReviewLoaderName() ) ) {
                    widgetConfiguration.setReviewLoaderName( CommonConstants.WIDGET_DEFAULT_LOAD_MORE_BUTTON_TEXT );
                }

                if ( StringUtils.isEmpty( widgetConfiguration.getReviewLoaderOpacity() ) ) {
                    widgetConfiguration.setReviewLoaderOpacity( CommonConstants.WIDGET_DEFAULT_LOAD_MORE_BUTTON_OPACITY );
                }

                if ( StringUtils.isEmpty( widgetConfiguration.getReviewSortOrder() ) ) {
                    widgetConfiguration.setReviewSortOrder( CommonConstants.WIDGET_DEFAULT_SORT_ORDER );
                }

                if ( StringUtils.isEmpty( widgetConfiguration.getMaxReviewsOnLoadMore() ) ) {
                    widgetConfiguration.setMaxReviewsOnLoadMore( CommonConstants.WIDGET_DEFAULT_MAX_REVIEWS_ON_LOAD_MORE );
                }

                if ( StringUtils.isEmpty( widgetConfiguration.getInitialNumberOfReviews() ) ) {
                    widgetConfiguration.setInitialNumberOfReviews( CommonConstants.WIDGET_DEFAULT_INITIAL_NUMBER_OF_REVIEWS );
                }

                if ( StringUtils.isEmpty( widgetConfiguration.getHideBarGraph() ) ) {
                    widgetConfiguration.setHideBarGraph( CommonConstants.WIDGET_DEFAULT_HIDE_BAR_GRAPH );
                }

                if ( StringUtils.isEmpty( widgetConfiguration.getHideOptions() ) ) {
                    widgetConfiguration.setHideOptions( CommonConstants.WIDGET_DEFAULT_HIDE_OPTIONS );
                }
                
                if ( StringUtils.isEmpty( widgetConfiguration.getHideContactBtn() ) ) {
                    widgetConfiguration.setHideContactBtn( CommonConstants.WIDGET_DEFAULT_HIDE_CONTACT);
                }

                
                if ( StringUtils.isEmpty( widgetConfiguration.getHideReviewBtn()) ) {
                    widgetConfiguration.setHideReviewBtn( CommonConstants.WIDGET_DEFAULT_HIDE_REVIEW );
                }

                if ( StringUtils.isEmpty( widgetConfiguration.getAllowModestBranding() ) ) {
                    widgetConfiguration.setAllowModestBranding( CommonConstants.WIDGET_DEFAULT_ALLOW_MODEST_BRANDING );
                }
                
                if ( StringUtils.isEmpty( widgetConfiguration.getMaxWidgetBtnSize() ) ) {
                    widgetConfiguration.setMaxWidgetBtnSize( CommonConstants.WIDGET_DEFAULT_BUTTON_SIZE );
                }

            }

        } else {
            if ( fillDefault ) {
                widgetConfiguration = defaultConfigration;
            }
        }
        return widgetConfiguration;
    }


    private String getDefaultButtonTwoLink( String unitSettingType, long iden )
    {
        if ( unitSettingType.equals( CommonConstants.AGENT_ID_COLUMN )
            || unitSettingType.equals( CommonConstants.PROFILE_LEVEL_INDIVIDUAL ) ) {
            return applicationBaseUrl + "rest/survey/showsurveypage/" + iden;
        } else {
            String profileLevel = null;
            if ( unitSettingType.equals( CommonConstants.COMPANY_ID_COLUMN )
                || unitSettingType.equals( CommonConstants.PROFILE_LEVEL_COMPANY ) ) {
                profileLevel = CommonConstants.PROFILE_LEVEL_COMPANY;
            } else if ( unitSettingType.equalsIgnoreCase( CommonConstants.BRANCH_ID_COLUMN )
                || unitSettingType.equals( CommonConstants.PROFILE_LEVEL_BRANCH ) ) {
                profileLevel = CommonConstants.PROFILE_LEVEL_BRANCH;
            } else if ( unitSettingType.equalsIgnoreCase( CommonConstants.REGION_ID_COLUMN )
                || unitSettingType.equals( CommonConstants.PROFILE_LEVEL_REGION ) ) {
                profileLevel = CommonConstants.PROFILE_LEVEL_REGION;
            }
            return applicationBaseUrl + "initfindapro.do?profileLevel=" + profileLevel + "&iden=" + iden;
        }
    }


    @Override
    public List<String> getListOfAvailableSources( String profileLevel, long iden ) throws InvalidInputException
    {
        LOG.debug( "method getListOfAvailableSources() started" );
        Set<String> availableSources = null;

        List<String> mongoSources = profileManagementService.getAvailableSurveySources( profileLevel, iden );
        if ( mongoSources != null && !mongoSources.isEmpty() ) {

            availableSources = new HashSet<>();

            for ( String source : mongoSources ) {
                switch ( source.trim() ) {
                    case CommonConstants.SURVEY_REQUEST_ADMIN:
                    case CommonConstants.SURVEY_REQUEST_AGENT:
                    case CommonConstants.RETAKE_REQUEST_CUSTOMER: {
                        availableSources.add( CommonConstants.POST_SOURCE_SOCIAL_SURVEY );
                        break;
                    }
                    case CommonConstants.SURVEY_SOURCE_ZILLOW: {
                        availableSources.add( CommonConstants.SURVEY_SOURCE_ZILLOW );
                        break;
                    }
                    case CommonConstants.CRM_INFO_SOURCE_ENCOMPASS:
                    case CommonConstants.CRM_SOURCE_LONEWOLF:
                    case CommonConstants.CRM_SOURCE_DOTLOOP:
                    case CommonConstants.CRM_INFO_SOURCE_FTP:
                    case CommonConstants.CRM_INFO_SOURCE_API: {
                        availableSources.add( CommonConstants.SURVEY_SOURCE_SOCIAL_SURVEY_VERIFIED );
                        break;
                    }
                    case CommonConstants.CHR_FACEBOOK: {
                        availableSources.add( CommonConstants.CHR_FACEBOOK );
                        break;
                    }
                    case CommonConstants.CHR_WIDGET_LINKEDIN: {
                        availableSources.add( CommonConstants.CHR_WIDGET_LINKEDIN );
                        break;
                    }
                    case CommonConstants.CHR_GOOGLE: {
                        availableSources.add( CommonConstants.CHR_GOOGLE );
                        break;
                    }
                    default: {
                        if ( StringUtils.isNotEmpty( source ) ) {
                            availableSources.add( source );
                        }
                    }
                }
            }
        }
        LOG.debug( "method getListOfAvailableSources() finished" );
        return availableSources == null ? null : new ArrayList<>( availableSources );
    }


    //create a function to add lock to history at the higher hierarchy
    @Override
    public WidgetConfiguration updateLockSettingsLog( WidgetConfiguration widgetConfiguration, Boolean isLocked )
    {
        LOG.debug( "Adding lock/unlock settings for higher heirarchy, isLocked:{}", isLocked );

        if ( widgetConfiguration == null ) {
            widgetConfiguration = new WidgetConfiguration();
        }

        if ( widgetConfiguration.getLockHistory() == null ) {
            widgetConfiguration.setLockHistory( new ArrayList<WidgetLockHistory>() );
        }

        WidgetLockHistory lockHistory = new WidgetLockHistory();
        lockHistory.setTimestamp( System.currentTimeMillis() );
        lockHistory.setAction( isLocked ? CommonConstants.WIDGET_LOCK : CommonConstants.WIDGET_UNLOCK );

        widgetConfiguration.getLockHistory().add( lockHistory );
        return widgetConfiguration;
    }


    //create a function to change lock settings, for all the lower hierarchy and update  latest action
    //the locking is done by bitwise or of the lock flag with the settings ( settingFlag | lockFlag)
    //the unlocking is done by bitwise and of the negated lock flag ( settingFlag & ~lockFlag) 
    //Note - negation is done by createLockFlag function hence it's not done here 
    @Override
    public OrganizationUnitSettings updateLowerHeirarchyLock( String profileLevel, long profileId,
        OrganizationUnitSettings unitSettings, int lockFlag, boolean isLocked )
    {
        LOG.debug( "Updating lock/unlock settings for lower heirarchy,by profileLevel:{},profileId:{},isLocked:{}, lockFlag:{}",
            profileLevel, profileId, isLocked, lockFlag );
        WidgetConfiguration widgetConfiguration = unitSettings.getWidgetConfiguration();

        if ( widgetConfiguration == null ) {
            widgetConfiguration = new WidgetConfiguration();
            unitSettings.setWidgetConfiguration( widgetConfiguration );
        }

        widgetConfiguration.setActionByProfileId( profileId );
        widgetConfiguration.setActionByProfileLevel( profileLevel );
        widgetConfiguration.setActionOn( System.currentTimeMillis() );
        if ( isLocked ) {
            widgetConfiguration.setLockFlag( widgetConfiguration.getLockFlag() | lockFlag );
        } else {
            widgetConfiguration.setLockFlag( widgetConfiguration.getLockFlag() & lockFlag );
        }

        return unitSettings;
    }


    //creating lock flag
    //each position in the lock flag(111) represents Company,Region,Branch respectively
    //each position in the unlock flag(000) represents Company,Region,Branch respectively
    //this is done to know which postion in the heirarchy has locked the lower heirarchy
    //and the number 111 is a bit representation for 6
    @Override
    public int createLockFlag( String profileLevel, boolean isLocked )
    {
        int lockFlag;
        if ( profileLevel.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            lockFlag = CommonConstants.WIDGET_LOCK_SET_BY_COMPANY;
        } else if ( profileLevel.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            lockFlag = CommonConstants.WIDGET_LOCK_SET_BY_REGION;
        } else if ( profileLevel.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            lockFlag = CommonConstants.WIDGET_LOCK_SET_BY_BRANCH;
        } else {
            //fall back if the profileLevel is wrong , we return 0
            lockFlag = 0;
            isLocked = true;
        }
        if ( !isLocked ) {
            lockFlag = ~lockFlag;
        }
        return lockFlag;
    }
}
