package com.realtech.socialsurvey.compute.topology.bolts.widget.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.ComputeConstants;
import com.realtech.socialsurvey.compute.common.LocalPropertyFileHandler;
import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.entities.ReportRequest;
import com.realtech.socialsurvey.compute.entities.WidgetScript;
import com.realtech.socialsurvey.compute.entities.WidgetScriptData;
import com.realtech.socialsurvey.compute.enums.ReportStatus;
import com.realtech.socialsurvey.compute.enums.ReportType;
import com.realtech.socialsurvey.compute.exception.APIIntegrationException;
import com.realtech.socialsurvey.compute.services.FailedMessagesService;
import com.realtech.socialsurvey.compute.services.impl.FailedMessagesServiceImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;


public class WidgetDataProcessingBolt extends BaseComputeBoltWithAck
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( WidgetDataProcessingBolt.class );


    @Override
    public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declare( new Fields( "isSuccess", "widgetScriptMap", "fileUploadId", "status", "reportRequest" ) );
    }


    @Override
    public List<Object> prepareTupleForFailure()
    {
        return Arrays.asList( false, null, -1, null, null );
    }


    @Override
    public void executeTuple( Tuple input )
    {

        ReportRequest reportRequest = ConversionUtils.deserialize( input.getString( 0 ), ReportRequest.class );
        boolean success = false;
        String reportType = reportRequest.getReportType();

        if ( reportType.equals( ReportType.WIDGET_REPORT.getName() ) ) {

            LOG.info( "getting script data and profile name data for widget" );

            long fileUploadId = reportRequest.getFileUploadId();
            long companyId = reportRequest.getCompanyId();

            Map<String, Map<String, String>> profileNameData = null;
            List<String> scripts = null;
            String status = null;
            WidgetScriptData scriptMapData = null;

            try {

                profileNameData = SSAPIOperations.getInstance().getProfileNameDateDataForWidgetReport( companyId );
                scripts = SSAPIOperations.getInstance().getTemplateDateDataForWidgetReport();

                if ( profileNameData == null || scripts == null || scripts.isEmpty() || profileNameData.isEmpty() ) {
                    status = ReportStatus.BLANK.getValue();
                } else {
                    status = ReportStatus.PROCESSED.getValue();

                    scriptMapData = new WidgetScriptData();
                    processWidgetScripts( scripts );
                    populateWidgetScripts( scriptMapData, profileNameData, scripts );
                }

                success = true;
                LOG.info( "Emitting tuple with success = {}, fileUploadId = {}, status = {}, companyId = {}", success,
                    fileUploadId, status, companyId );
                _collector.emit( input, Arrays.asList( success, scriptMapData, fileUploadId, status, reportRequest ) );


            } catch ( APIIntegrationException | IllegalArgumentException | IOException e ) {
                success = true;
                LOG.error( "Exception occurred while fetching widget data  ", e );
                FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();
                failedMessagesService.insertTemporaryFailedReportRequest( reportRequest );
                LOG.error( "Emitting tuple with success = {}, fileUploadId = {}, processingCompleted = {}", success,
                    fileUploadId, ReportStatus.FAILED.getValue() );
                _collector.emit( input,
                    Arrays.asList( success, scriptMapData, fileUploadId, ReportStatus.FAILED.getValue(), reportRequest ) );
            }
        } else {
            LOG.info( "Emitting tuple with success = {}, fileUploadId = {}, status = {}", success, -1, null );
            _collector.emit( input, Arrays.asList( success, null, -1, null, null ) );
        }

    }


    private void populateWidgetScripts( WidgetScriptData scriptMapData, Map<String, Map<String, String>> profileNameData,
        List<String> scripts )
    {
        Map<String, String> companyData = profileNameData.get( ComputeConstants.PROFILE_LEVEL_COMPANY );
        Map<String, String> regionData = profileNameData.get( ComputeConstants.PROFILE_LEVEL_REGION );
        Map<String, String> branchData = profileNameData.get( ComputeConstants.PROFILE_LEVEL_BRANCH );
        Map<String, String> agentData = profileNameData.get( ComputeConstants.PROFILE_LEVEL_INDIVIDUAL );

        if ( companyData == null || companyData.isEmpty() ) {
            LOG.error( "Company profile data not present" );
            throw new IllegalArgumentException( "Company profile data not present" );
        }

        String companyProfileName = companyData.entrySet().iterator().next().getKey();
        String scriptPaf = null;
        String scriptCc = null;
        String scriptJI = null;

        String resourcesUrl = LocalPropertyFileHandler.getInstance()
            .getProperty( ComputeConstants.APPLICATION_PROPERTY_FILE, ComputeConstants.WIDGET_RESOURCES_URL )
            .orElseGet( () -> "" );

        for ( String template : scripts ) {
            if ( StringUtils.contains( template, "iframe" ) ) {
                scriptJI = template;
            } else if ( StringUtils.contains( template, "container" ) ) {
                scriptCc = template;
            } else {
                scriptPaf = template;
            }
        }


        scriptMapData.setCompanyScript( new ArrayList<>() );
        for ( Entry<String, String> entry : companyData.entrySet() ) {
            if ( StringUtils.isNotEmpty( entry.getKey() ) ) {
                WidgetScript script = new WidgetScript();
                script.setProfileName( entry.getKey() );
                script.setName( entry.getValue() == null ? "" : entry.getValue() );
                buildWidgetScripts( script, scriptPaf, scriptCc, scriptJI, companyProfileName, entry.getKey(),
                    ComputeConstants.PROFILE_LEVEL_COMPANY, resourcesUrl );
                scriptMapData.getCompanyScript().add( script );
            }
        }


        if ( regionData != null && !regionData.isEmpty() ) {
            scriptMapData.setRegionScript( new ArrayList<>() );
            for ( Entry<String, String> entry : regionData.entrySet() ) {
                if ( StringUtils.isNotEmpty( entry.getKey() ) ) {
                    WidgetScript script = new WidgetScript();
                    script.setProfileName( entry.getKey() );
                    script.setName( entry.getValue() == null ? "" : entry.getValue() );
                    buildWidgetScripts( script, scriptPaf, scriptCc, scriptJI, companyProfileName, entry.getKey(),
                        ComputeConstants.PROFILE_LEVEL_REGION, resourcesUrl );
                    scriptMapData.getRegionScript().add( script );
                }
            }
        }

        if ( branchData != null && !branchData.isEmpty() ) {
            scriptMapData.setBranchScript( new ArrayList<>() );
            for ( Entry<String, String> entry : branchData.entrySet() ) {
                if ( StringUtils.isNotEmpty( entry.getKey() ) ) {
                    WidgetScript script = new WidgetScript();
                    script.setProfileName( entry.getKey() );
                    script.setName( entry.getValue() == null ? "" : entry.getValue() );
                    buildWidgetScripts( script, scriptPaf, scriptCc, scriptJI, companyProfileName, entry.getKey(),
                        ComputeConstants.PROFILE_LEVEL_BRANCH, resourcesUrl );
                    scriptMapData.getBranchScript().add( script );
                }
            }
        }

        if ( agentData != null && !agentData.isEmpty() ) {
            scriptMapData.setAgentScript( new ArrayList<>() );
            for ( Entry<String, String> entry : agentData.entrySet() ) {
                if ( StringUtils.isNotEmpty( entry.getKey() ) ) {
                    WidgetScript script = new WidgetScript();
                    script.setProfileName( entry.getKey() );
                    script.setName( entry.getValue() == null ? "" : entry.getValue() );
                    buildWidgetScripts( script, scriptPaf, scriptCc, scriptJI, companyProfileName, entry.getKey(),
                        ComputeConstants.PROFILE_LEVEL_INDIVIDUAL, resourcesUrl );
                    scriptMapData.getAgentScript().add( script );
                }
            }
        }
    }


    private void buildWidgetScripts( WidgetScript script, String scriptPaf, String scriptCc, String scriptJI,
        String companyProfileName, String profileName, String profileLevel, String resourcesUrl )
    {
        String paf = StringUtils.replaceOnce( scriptPaf, "%s", companyProfileName );
        paf = StringUtils.replaceOnce( paf, "%s", profileName );
        paf = StringUtils.replaceOnce( paf, "%s", profileLevel );
        paf = StringUtils.replaceOnce( paf, "%s", resourcesUrl );

        String cc = StringUtils.replaceOnce( scriptCc, "%s", companyProfileName );
        cc = StringUtils.replaceOnce( cc, "%s", profileName );
        cc = StringUtils.replaceOnce( cc, "%s", profileLevel );
        cc = StringUtils.replaceOnce( cc, "%s", resourcesUrl );

        String ji = StringUtils.replaceOnce( scriptJI, "%s", companyProfileName );
        ji = StringUtils.replaceOnce( ji, "%s", profileName );
        ji = StringUtils.replaceOnce( ji, "%s", profileLevel );
        ji = StringUtils.replaceOnce( ji, "%s", resourcesUrl );

        script.setScriptPAF( paf );
        script.setScriptCc( cc );
        script.setScriptJi( ji );
    }


    private void processWidgetScripts( List<String> scripts )
    {
        for ( int i = 0; i < scripts.size(); i++ ) {
            scripts.set( i, StringUtils.replace( scripts.get( i ), "&lt;", "<" ) );
            scripts.set( i, StringUtils.replace( scripts.get( i ), "&gt;", ">" ) );
            scripts.set( i, StringUtils.replace( scripts.get( i ), "\\n", "" ) );
            scripts.set( i, StringUtils.replace( scripts.get( i ), "\\t", " " ) );
            scripts.set( i, StringUtils.replace( scripts.get( i ), "\\\"", "\"" ) );
        }

    }


}
