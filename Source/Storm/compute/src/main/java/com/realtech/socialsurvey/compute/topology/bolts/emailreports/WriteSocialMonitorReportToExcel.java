package com.realtech.socialsurvey.compute.topology.bolts.emailreports;

import com.realtech.socialsurvey.compute.entities.ReportRequest;
import com.realtech.socialsurvey.compute.entities.response.ActionHistory;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.enums.ActionHistoryType;
import com.realtech.socialsurvey.compute.enums.ReportStatus;
import com.realtech.socialsurvey.compute.enums.ReportType;
import com.realtech.socialsurvey.compute.services.FailedMessagesService;
import com.realtech.socialsurvey.compute.services.impl.FailedMessagesServiceImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;
import com.realtech.socialsurvey.compute.utils.FileUtils;
import com.realtech.socialsurvey.compute.utils.WorkBookUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class WriteSocialMonitorReportToExcel extends BaseComputeBoltWithAck
{

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(WriteSocialMonitorReportToExcel.class);

    private static final String EXCEL_FILE_EXTENSION = ".xlsx";
    private transient XSSFWorkbook workbook;

    public static final String SOCIAL_MONITOR_DATE_REPORT_FOR_KEYWORD = "SocialSurvey User Name,Social Post Source,Post content,Post Link,Action,Owner Name,"
        + "Action Date,Comments,MessageType,Message";
    public static final String SOCIAL_MONITOR_DATE_REPORT = "SocialSurvey User Name,Social Post Source,Post content,Post Link,Flagged Manually,"

        + "Action,Owner Name,Action Date,Comments,MessageType,Message";

    @Override
    @SuppressWarnings( "unchecked" )
    public void executeTuple( Tuple input )
    {
        boolean success ;
        File file = null;
        String fileName = null;
        byte[] fileBytes = null;
        String status = input.getStringByField( "status" );
        ReportRequest reportRequest = (ReportRequest) input.getValueByField("reportRequest");
        int enterAt = (int) input.getValueByField( "enterAt" );
        long fileUploadId = (long) input.getValueByField("fileUploadId");

        if( status == ReportStatus.FAILED.getValue()){
            workbook = null;
        } else {
            List<SocialResponseObject> socialResponseWrapper = (List<SocialResponseObject>) input.getValueByField("socialFeed");
            workbook = writeReportToWorkbook(socialResponseWrapper, reportRequest.getReportType(), enterAt );
            if ( workbook != null && status.equals(ReportStatus.PROCESSED.getValue()) ) {
                fileName = reportRequest.getReportType() + "_" + ( Calendar.getInstance().getTimeInMillis()) + EXCEL_FILE_EXTENSION;
                try {
                    file = FileUtils.createFileInLocal(fileName, workbook);
                    fileBytes = FileUtils.convertFileToBytes(file);
                } catch (IOException e) {
                    LOG.error("IO  exception occured ", e);
                    FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();
                    failedMessagesService.insertTemporaryFailedReportRequest(reportRequest);
                }
            }
        }
        success = true;
        LOG.info("Emitting tuple with success = {} , fileUploadId = {}, fileName = {}, status = {}", success, fileUploadId, fileName, status);
        _collector.emit(input, Arrays.asList(success, fileName, fileBytes, fileUploadId,
            reportRequest, status));

        //if the file is successfully created , delete from the local
        if(file != null && file.exists()) {
            if(file.delete()) LOG.debug(" {} has been successfully deleted ", fileName);
            else LOG.error(" Unable to delete {} " , fileName);
        }
    }


    private XSSFWorkbook writeReportToWorkbook( List<SocialResponseObject> socialResponseWrapper, String reportType,
        int enterAt )
    {
        Map<Integer, List<Object>> data;
        if(reportType.equals( ReportType.SOCIAL_MONITOR_DATE_REPORT_FOR_KEYWORD.getName() )) {
            if( enterAt == 1 ){
                data = WorkBookUtils.writeReportHeader(SOCIAL_MONITOR_DATE_REPORT_FOR_KEYWORD);
                workbook = WorkBookUtils.createWorkbook(data);
                enterAt = 2;
            }
            else
                enterAt = workbook.getSheetAt( 0 ).getLastRowNum()+1;

            data = getSocialMonitorReportForKeywordToBeWrittenInSheet(socialResponseWrapper);
        }
        else {
            if ( enterAt == 1 ){
                data = WorkBookUtils.writeReportHeader(SOCIAL_MONITOR_DATE_REPORT);
                workbook = WorkBookUtils.createWorkbook(data);
                enterAt = 2;
            }
            else
                enterAt = workbook.getSheetAt( 0 ).getLastRowNum()+1;

            data = getSocialMonitorReportDateBasedToBeWrittenInSheet(socialResponseWrapper);
        }
        workbook = WorkBookUtils.writeToWorkbook(data, workbook, enterAt);
        return workbook;
    }

    @SuppressWarnings( "unchecked" )
    private Map<Integer,List<Object>> getSocialMonitorReportDateBasedToBeWrittenInSheet( List<SocialResponseObject> socialResponseWrapper )
    {
        Map<Integer, List<Object>> socialFeedData = new TreeMap<>();
        if(workbook != null) {
            List<Object> socialMonitorReportToPopulate;
            int enterNext = 1;
            for (SocialResponseObject socialFeed : socialResponseWrapper) {

                if( socialFeed.getActionHistory() == null || socialFeed.getActionHistory().isEmpty() ){
                    socialMonitorReportToPopulate = new ArrayList<>();

                    socialMonitorReportToPopulate.add(socialFeed.getOwnerName());
                    socialMonitorReportToPopulate.add(socialFeed.getType().toString());
                    socialMonitorReportToPopulate.add(socialFeed.getText());
                    socialMonitorReportToPopulate.add(socialFeed.getPostLink());
                    socialMonitorReportToPopulate.add( "No" );
                    socialMonitorReportToPopulate.add( socialFeed.getStatus() );

                    socialFeedData.put(enterNext++, socialMonitorReportToPopulate);
                }
                else {
                    List<ActionHistory> actionHistories = socialFeed.getActionHistory();
                    for( ActionHistory actionHistory : actionHistories ){

                        socialMonitorReportToPopulate = new ArrayList<>();

                        socialMonitorReportToPopulate.add(socialFeed.getOwnerName());
                        socialMonitorReportToPopulate.add(socialFeed.getType().toString());
                        socialMonitorReportToPopulate.add(socialFeed.getText());
                        socialMonitorReportToPopulate.add(socialFeed.getPostLink());
                        socialMonitorReportToPopulate.add(socialFeed.getFoundKeywords()== null &&
                            actionHistory.getActionType().equals( ActionHistoryType.FLAGGED ) ? "Yes" : "No");
                        socialMonitorReportToPopulate.add(actionHistory.getActionType().toString());
                        socialMonitorReportToPopulate.add(actionHistory.getOwnerName());
                        socialMonitorReportToPopulate.add(ConversionUtils.convertToEst( actionHistory.getCreatedDate() ));
                        socialMonitorReportToPopulate.add( StringUtils.isEmpty( actionHistory.getText() ) ? "" : Jsoup.parse( actionHistory.getText() ).text() );
                        if(actionHistory.getMessageType() != null){
                            socialMonitorReportToPopulate.add( actionHistory.getMessageType().toString() );
                            socialMonitorReportToPopulate.add( actionHistory.getMessage() );
                        }
                        socialFeedData.put(enterNext++, socialMonitorReportToPopulate);
                    }
                }
            }
        }
        return socialFeedData;
    }


    @SuppressWarnings( "unchecked" )
    private Map<Integer,List<Object>> getSocialMonitorReportForKeywordToBeWrittenInSheet( List<SocialResponseObject> socialResponseWrapper )
    {
        Map<Integer, List<Object>> socialFeedData = new TreeMap<>();
        if(workbook != null) {
            List<Object> socialMonitorReportToPopulate;
            int enterNext = 1;
            for (SocialResponseObject socialFeed : socialResponseWrapper) {
                List<ActionHistory> actionHistories = socialFeed.getActionHistory();
                for( ActionHistory actionHistory : actionHistories ){

                    socialMonitorReportToPopulate = new ArrayList<>();

                    socialMonitorReportToPopulate.add(socialFeed.getOwnerName());
                    socialMonitorReportToPopulate.add(socialFeed.getType().toString());
                    socialMonitorReportToPopulate.add(socialFeed.getText());
                    socialMonitorReportToPopulate.add(socialFeed.getPostLink());
                    socialMonitorReportToPopulate.add(actionHistory.getActionType().toString());
                    socialMonitorReportToPopulate.add(actionHistory.getOwnerName());
                    socialMonitorReportToPopulate.add(ConversionUtils.convertToEst( actionHistory.getCreatedDate() ));
                    socialMonitorReportToPopulate.add(actionHistory.getText());
                    if(actionHistory.getMessageType() != null){
                        socialMonitorReportToPopulate.add( actionHistory.getMessageType().toString() );
                        socialMonitorReportToPopulate.add( actionHistory.getMessage() );
                    }

                    socialFeedData.put(enterNext++, socialMonitorReportToPopulate);
                }
            }
        }
        return socialFeedData;
    }


    @Override public List<Object> prepareTupleForFailure()
    {
        return Arrays.asList(false, null, null, -1, null, null);
    }


    @Override public void declareOutputFields( OutputFieldsDeclarer outputFieldsDeclarer )
    {
        outputFieldsDeclarer
            .declare(new Fields("isSuccess", "fileName", "fileBytes", "fileUploadId", "reportRequest", "status"));
    }
}
