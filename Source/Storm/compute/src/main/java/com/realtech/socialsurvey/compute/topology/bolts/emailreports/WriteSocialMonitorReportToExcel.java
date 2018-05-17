package com.realtech.socialsurvey.compute.topology.bolts.emailreports;

import com.realtech.socialsurvey.compute.entities.ReportRequest;
import com.realtech.socialsurvey.compute.entities.response.ActionHistory;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.entity.SurveyInvitationEmailCountMonth;
import com.realtech.socialsurvey.compute.enums.ReportStatus;
import com.realtech.socialsurvey.compute.enums.ReportType;
import com.realtech.socialsurvey.compute.services.FailedMessagesService;
import com.realtech.socialsurvey.compute.services.impl.FailedMessagesServiceImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;
import com.realtech.socialsurvey.compute.utils.FileUtils;
import com.realtech.socialsurvey.compute.utils.WorkBookUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
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
        + "Action Date,Comments";

    @Override public void executeTuple( Tuple input )
    {
        boolean success ;
        File file = null;
        String fileName = null;
        byte[] fileBytes = null;
        String status = input.getStringByField( "status" );
        ReportRequest reportRequest = (ReportRequest) input.getValueByField("reportRequest");
        long comapanyId = (long) input.getValueByField( "companyId" );
        int enterAt = (int) input.getValueByField( "enterAt" );

        if( status == ReportStatus.FAILED.getValue()){
            workbook = null;
        } else {
            List<SocialResponseObject> socialResponseWrapper = (List<SocialResponseObject>) input.getValueByField("socialFeed");
            workbook = writeReportToWorkbook(socialResponseWrapper, reportRequest.getReportType(), enterAt );
            if ( workbook != null && status.equals(ReportStatus.PROCESSED.getValue()) ) {
                fileName = reportRequest.getReportType() + "_" + comapanyId + "_" + ( Calendar.getInstance().getTimeInMillis()) + EXCEL_FILE_EXTENSION;
                try {
                    file = FileUtils.createFileInLocal(fileName, workbook);
                    fileBytes = ConversionUtils.convertFileToBytes(file);
                } catch (IOException e) {
                    LOG.error("IO  exception occured ", e);
                    FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();
                    failedMessagesService.insertTemporaryFailedReportRequest(reportRequest);
                }
                /*if (workbook == null || file == null || !file.exists() || fileBytes == null) {
                    status = ReportStatus.FAILED.getValue();
                }*/
            }
        }
        success = true;
        LOG.info("Emitting tuple with success = {} , fileName = {}, status = {}", success, fileName, status);
        _collector.emit(input, Arrays.asList(success,fileName,fileBytes,input.getValueByField("fileUploadId"),
            input.getValueByField("reportRequest"), status));
        //if the file is successfully created , delete from the local
        if(file != null && file.exists()) {
            if(file.delete()) LOG.info(" {} has been successfully deleted ", fileName);
            else LOG.info(" Unable to delete {} " , fileName);
        }
    }


    private XSSFWorkbook writeReportToWorkbook( List<SocialResponseObject> socialResponseWrapper, String reportType,
        int enterAt )
    {
        Map<Integer, List<Object>> data;
        if(reportType == ReportType.SOCIAL_MONITOR_DATE_REPORT_FOR_KEYWORD.getName()) {
            if(enterAt ==1){
                data = WorkBookUtils.writeReportHeader(SOCIAL_MONITOR_DATE_REPORT_FOR_KEYWORD);
                workbook = WorkBookUtils.createWorkbook(data);
            }
            data = getSocialMonitorReportForKeywordToBeWrittenInSheet(socialResponseWrapper);
        }
        else {
            if ( enterAt == 1 ){
                data = WorkBookUtils.writeReportHeader(SURVEY_INVITATION_EMAIL_REPORT_HEADER);
                workbook = WorkBookUtils.createWorkbook(data);
            }
            data = getEmailReportToBeWrittenInSheet(socialResponseWrapper);
        }
        workbook = WorkBookUtils.writeToWorkbook(data, workbook, (enterAt-1)*10+2);
        return workbook;
    }


    private Map<Integer,List<Object>> getSocialMonitorReportForKeywordToBeWrittenInSheet( List<SocialResponseObject> socialResponseWrapper )
    {
        if(workbook != null) {
            {
                Map<Integer, List<Object>> socialFeedData = new TreeMap<>();
                List<Object> socialMonitorReportToPopulate;
                int enterNext = 1;
                for (SocialResponseObject socialFeed : socialResponseWrapper) {
                    List<ActionHistory> actionHistories = socialFeed.getActionHistory();
                    for( ActionHistory actionHistory : actionHistories ){

                        socialMonitorReportToPopulate = new ArrayList<Object>();

                        socialMonitorReportToPopulate.add(socialFeed.getOwnerName());
                        socialMonitorReportToPopulate.add(socialFeed.getType());
                        socialMonitorReportToPopulate.add(socialFeed.getText());
                        socialMonitorReportToPopulate.add(socialFeed.getPageLink());
                        socialMonitorReportToPopulate.add(actionHistory.getActionType());
                        socialMonitorReportToPopulate.add(actionHistory.getOwnerName());
                        socialMonitorReportToPopulate.add(actionHistory.getDelivered());
                        socialMonitorReportToPopulate.add(socialFeed.getBounced());
                        socialMonitorReportToPopulate.add(socialFeed.getDropped());
                        socialMonitorReportToPopulate.add(socialFeed.getDiffered());
                        socialMonitorReportToPopulate.add(socialFeed.getOpened());
                        socialMonitorReportToPopulate.add(socialFeed.getLinkClicked());

                        socialFeedData.put(enterNext++, socialMonitorReportToPopulate);
                    }

                }
                return socialFeedData;
            }
        }

    }


    @Override public List<Object> prepareTupleForFailure()
    {
        return null;
    }


    @Override public void declareOutputFields( OutputFieldsDeclarer outputFieldsDeclarer )
    {
        outputFieldsDeclarer
            .declare(new Fields("isSuccess", "fileName", "fileBytes", "fileUploadId", "reportRequest", "status"));
    }
}
