package com.realtech.socialsurvey.compute.topology.bolts.widget.reports;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.entities.ReportRequest;
import com.realtech.socialsurvey.compute.entities.WidgetScript;
import com.realtech.socialsurvey.compute.entities.WidgetScriptData;
import com.realtech.socialsurvey.compute.enums.ReportStatus;
import com.realtech.socialsurvey.compute.services.FailedMessagesService;
import com.realtech.socialsurvey.compute.services.impl.FailedMessagesServiceImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import com.realtech.socialsurvey.compute.utils.FileUtils;
import com.realtech.socialsurvey.compute.utils.WorkBookUtils;


public class WriteWidgetReportToExcelBolt extends BaseComputeBoltWithAck
{


    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger( WriteWidgetReportToExcelBolt.class );
    private static final String WIDGET_REPORT_HEADER = "Name,Profile Identifier,Javascript code ( place and forget ),Javascript code ( place the widget in custom div container ),Javascript code with I-Frame";
    private static final String FILE_EXTENSION = ".xlsx";
    private static final String WIDGET_COMPANY_SHEET = "Company";
    private static final String WIDGET_REGION_SHEET = "Region";
    private static final String WIDGET_BRANCH_SHEET = "Branch";
    private static final String WIDGET_USERS_SHEET = "Users";


    @Override
    public void declareOutputFields( OutputFieldsDeclarer outputFieldsDeclarer )
    {
        outputFieldsDeclarer
            .declare( new Fields( "isSuccess", "fileName", "fileBytes", "fileUploadId", "reportRequest", "status" ) );
    }


    @Override
    public List<Object> prepareTupleForFailure()
    {
        return Arrays.asList( false, null, null, -1, null, null );
    }


    @Override
    public void executeTuple( Tuple input )
    {
        boolean success;
        File file = null;
        String fileName = null;
        byte[] fileBytes = null;
        XSSFWorkbook workbook = null;
        String status = input.getStringByField( "status" );
        ReportRequest reportRequest = (ReportRequest) input.getValueByField( "reportRequest" );
        long fileUploadId = (long) input.getValueByField( "fileUploadId" );

        if ( status == ReportStatus.PROCESSED.getValue() ) {
            WidgetScriptData widgetScriptData = (WidgetScriptData) input.getValueByField( "widgetScriptMap" );
            workbook = writeReportToWorkbook( widgetScriptData );
            if ( workbook != null && status.equals( ReportStatus.PROCESSED.getValue() ) ) {
                fileName = reportRequest.getReportType() + "_" + ( Calendar.getInstance().getTimeInMillis() ) + FILE_EXTENSION;
                try {
                    file = FileUtils.createFileInLocal( fileName, workbook );
                    fileBytes = FileUtils.convertFileToBytes( file );
                } catch ( IOException e ) {
                    LOG.error( "IO  exception occured ", e );
                    FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();
                    failedMessagesService.insertTemporaryFailedReportRequest( reportRequest );
                }
            }
        }
        success = true;
        LOG.info( "Emitting tuple with success = {} , fileUploadId = {}, fileName = {}, status = {}", success, fileUploadId,
            fileName, status );
        _collector.emit( input, Arrays.asList( success, fileName, fileBytes, fileUploadId, reportRequest, status ) );

        //if the file is successfully created , delete from the local
        try {
            if ( file != null && Files.deleteIfExists( file.toPath() ) ) {
                LOG.debug( " {} has been successfully deleted ", fileName );
            }

        } catch ( IOException e ) {
            LOG.error( " Unable to delete {} ", fileName );
        }

    }


    private XSSFWorkbook writeReportToWorkbook( WidgetScriptData widgetScriptData )
    {
        XSSFWorkbook widgetReport = WorkBookUtils.updateWorkBook( WorkBookUtils.writeReportHeader( WIDGET_REPORT_HEADER ), null,
            WIDGET_COMPANY_SHEET, 0, null, true, true, 40, true, true );
        WorkBookUtils.updateWorkBook( createRowData( widgetScriptData.getCompanyScript() ), widgetReport, WIDGET_COMPANY_SHEET,
            1, null, false, true, 40, true, true );

        WorkBookUtils.updateWorkBook( WorkBookUtils.writeReportHeader( WIDGET_REPORT_HEADER ), widgetReport,
            WIDGET_REGION_SHEET, 0, null, true, true, 40, true, true );
        WorkBookUtils.updateWorkBook( createRowData( widgetScriptData.getRegionScript() ), widgetReport, WIDGET_REGION_SHEET, 1,
            null, false, true, 40, true, true );

        WorkBookUtils.updateWorkBook( WorkBookUtils.writeReportHeader( WIDGET_REPORT_HEADER ), widgetReport,
            WIDGET_BRANCH_SHEET, 0, null, true, true, 40, true, true );
        WorkBookUtils.updateWorkBook( createRowData( widgetScriptData.getBranchScript() ), widgetReport, WIDGET_BRANCH_SHEET, 1,
            null, false, true, 40, true, true );

        WorkBookUtils.updateWorkBook( WorkBookUtils.writeReportHeader( WIDGET_REPORT_HEADER ), widgetReport, WIDGET_USERS_SHEET,
            0, null, true, true, 40, true, true );
        WorkBookUtils.updateWorkBook( createRowData( widgetScriptData.getAgentScript() ), widgetReport, WIDGET_USERS_SHEET, 1,
            null, false, true, 40, true, true );

        return widgetReport;
    }


    private Map<Integer, List<Object>> createRowData( List<WidgetScript> widgetScriptList )
    {
        Map<Integer, List<Object>> reportDataToPopulate = new TreeMap<>();

        if ( widgetScriptList != null ) {
            for ( int i = 0; i < widgetScriptList.size(); i++ ) {
                List<Object> row = new ArrayList<>();
                row.add( widgetScriptList.get( i ).getName() );
                row.add( widgetScriptList.get( i ).getProfileName() );
                row.add( widgetScriptList.get( i ).getScriptPAF() );
                row.add( widgetScriptList.get( i ).getScriptCc() );
                row.add( widgetScriptList.get( i ).getScriptJi() );
                reportDataToPopulate.put( i, row );
            }
        }

        return reportDataToPopulate;
    }

}
