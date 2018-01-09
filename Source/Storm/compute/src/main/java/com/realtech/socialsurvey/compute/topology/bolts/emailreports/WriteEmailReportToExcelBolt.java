/**
 *
 */
package com.realtech.socialsurvey.compute.topology.bolts.emailreports;

import com.realtech.socialsurvey.compute.common.LocalPropertyFileHandler;
import com.realtech.socialsurvey.compute.entities.ReportRequest;
import com.realtech.socialsurvey.compute.entities.SolrEmailMessageWrapper;
import com.realtech.socialsurvey.compute.enums.ReportStatus;
import com.realtech.socialsurvey.compute.services.FailedMessagesService;
import com.realtech.socialsurvey.compute.services.impl.FailedMessagesServiceImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import static com.realtech.socialsurvey.compute.common.ComputeConstants.APPLICATION_PROPERTY_FILE;
import static com.realtech.socialsurvey.compute.common.ComputeConstants.FILEUPLOAD_DIRECTORY_LOCATION;

/**
 * @author Subhrajit
 *
 */
public class WriteEmailReportToExcelBolt extends BaseComputeBoltWithAck {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( WriteEmailReportToExcelBolt.class );

    private transient XSSFWorkbook workbook;

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields( "isSuccess", "fileName", "file", "fileUploadId", "reportRequest", "status"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void executeTuple(Tuple input) {
        boolean success = false;
        File file = null;
        String fileName = null;
        String status = input.getStringByField( "status" );
        ReportRequest reportRequest = (ReportRequest) input.getValueByField("reportRequest");

        if( !input.getBooleanByField("isSuccess")){
            if(workbook != null) workbook = null;
        } else {
            List<SolrEmailMessageWrapper> solrEmailMessageWrapper = (List<SolrEmailMessageWrapper>) input.getValueByField("surveyMailList");
            String zoneId = input.getStringByField("zoneId");
            int startIndex = input.getIntegerByField( "startIndex" );
            int batchSize = input.getIntegerByField( "batchSize" );
            if( status.equals(ReportStatus.PROCESSING.getValue()) ) {
                workbook = writeEmailReportToWorkbook(solrEmailMessageWrapper, zoneId, startIndex, batchSize);
            }
            else if (workbook != null && status.equals(ReportStatus.PROCESSED.getValue())){
                fileName = "Email_Message_Report" + "-" + ( Calendar.getInstance().getTimeInMillis() ) + ".xlsx";
                file = createFileInLocal( fileName, workbook, reportRequest );
                if( workbook == null || file == null  || !file.exists()){
                    status = ReportStatus.FAILED.getValue();
                }
            } else if(status.equals(ReportStatus.FAILED.getValue()) && workbook !=null ){
                workbook = null;
            }

            success = true;
        }
        LOG.info("Emitting tuple with success = {} , fileName = {}, status = {}", success, fileName, status);
        _collector.emit(input, Arrays.asList(success,fileName,file,input.getValueByField("fileUploadId"),
                input.getValueByField("reportRequest"), status));
    }

    @Override
    public List<Object> prepareTupleForFailure() {
        return Arrays.asList(false, null, null, -1, null, null);
    }

    private XSSFWorkbook writeEmailReportToWorkbook(List<SolrEmailMessageWrapper> solrEmailMessageWrapper,
                                                    String zoneId , int startIndex, int batchSize ){
        Map<Integer, List<Object>> data;
        if ( startIndex == 1 ) {
            data = writeEmailReportHeader();
            workbook = createWorkbook( data );
            data = getEmailReportToBeWrittenInSheet( solrEmailMessageWrapper, zoneId );
            workbook = writeToWorkbook( data, workbook, ((startIndex - 1)*batchSize) + 2 );
        } else if( startIndex > 1 && workbook != null ){
            data = getEmailReportToBeWrittenInSheet( solrEmailMessageWrapper, zoneId );
            workbook = writeToWorkbook( data, workbook, (startIndex - 1)*batchSize + 2 );
        }

        return workbook;
    }

    private Map<Integer, List<Object>> writeEmailReportHeader() {
        Map<Integer, List<Object>>  emailReportData = new TreeMap<>();
        List<Object> emailReportToPopulate = new ArrayList<>();
        // Setting up user sheet headers
        emailReportToPopulate.add( "Survey Source ID" );
        emailReportToPopulate.add( "Agent Name" );
        emailReportToPopulate.add( "Agent Email" );
        emailReportToPopulate.add( "Branch" );
        emailReportToPopulate.add( "Region Name" );
        emailReportToPopulate.add( "Customer Name" );
        emailReportToPopulate.add( "Customer Email" );
        emailReportToPopulate.add( "Survey Sent Date" );
        emailReportToPopulate.add( "Delivered" );
        emailReportToPopulate.add( "Bounced" );
        emailReportToPopulate.add( "Deferred" );
        emailReportToPopulate.add( "Opened" );
        emailReportToPopulate.add( "Clicked");
        emailReportData.put( 1, emailReportToPopulate );

        return emailReportData;
    }

    private XSSFWorkbook createWorkbook( Map<Integer, List<Object>> data ) {
        // Blank workbook
        XSSFWorkbook workBook = new XSSFWorkbook();

        // Create a blank sheet
        XSSFSheet sheet = workBook.createSheet();
        XSSFDataFormat df = workBook.createDataFormat();
        CellStyle style = workBook.createCellStyle();
        style.setDataFormat( df.getFormat( "MM/dd/yyyy" ) );

        // Iterate over data and write to sheet
        Set<Integer> keyset = data.keySet();
        int rownum = 0;
        for ( Integer key : keyset ) {
            Row row = sheet.createRow( rownum++ );
            List<Object> objArr = data.get( key );

            int cellnum = 0;
            for ( Object obj : objArr ) {
                Cell cell = row.createCell( cellnum++ );
                if ( obj instanceof String ) {
                    cell.setCellValue( (String) obj );
                } else if ( obj instanceof Integer ) {
                    cell.setCellValue( (Integer) obj );
                } else if ( obj instanceof Date ) {
                    cell.setCellStyle( style );
                    cell.setCellValue( (Date) obj );
                } else if ( obj instanceof Long ) {
                    cell.setCellValue( String.valueOf( (Long) obj ) );
                } else if ( obj instanceof Double ) {
                    cell.setCellValue( (Double) obj );
                }
            }
        }
        return workBook;
    }

    private Map<Integer, List<Object>> getEmailReportToBeWrittenInSheet(
            List<SolrEmailMessageWrapper> solrEmailMessageWrapper, String zoneId) {
        Map<Integer, List<Object>> emailReportData = new TreeMap<>();
        List<Object> emailReportToPopulate;
        int enterNext = 1;
        for(SolrEmailMessageWrapper solrEmailMessageData : solrEmailMessageWrapper){
            emailReportToPopulate = new ArrayList<>();
            // Set data to list.
            emailReportToPopulate.add(solrEmailMessageData.getSurveySourceId());
            emailReportToPopulate.add(solrEmailMessageData.getSenderName());
            emailReportToPopulate.add(solrEmailMessageData.getAgentEmailId());
            emailReportToPopulate.add(solrEmailMessageData.getBranchName());
            emailReportToPopulate.add(solrEmailMessageData.getRegionName());
            emailReportToPopulate.add(solrEmailMessageData.getRecipientsName() != null ? solrEmailMessageData.getRecipientsName().get(0) : null);
            emailReportToPopulate.add(solrEmailMessageData.getRecipients().get(0));
            emailReportToPopulate.add(ConversionUtils.convertFromGmt(solrEmailMessageData.getEmailAttemptedDate(),zoneId));
            emailReportToPopulate.add(ConversionUtils.convertFromGmt(solrEmailMessageData.getEmailDeliveredDate(),zoneId));
            emailReportToPopulate.add(ConversionUtils.convertFromGmt(solrEmailMessageData.getEmailBounceDate(),zoneId));
            emailReportToPopulate.add(ConversionUtils.convertFromGmt(solrEmailMessageData.getEmailDefferedDate(),zoneId));
            emailReportToPopulate.add(ConversionUtils.convertFromGmt(solrEmailMessageData.getEmailOpenedDate(),zoneId));
            emailReportToPopulate.add(ConversionUtils.convertFromGmt(solrEmailMessageData.getEmailLinkClickedDate(),zoneId));

            // Set the list to the map.
            emailReportData.put(++enterNext, emailReportToPopulate);
        }
        return emailReportData;
    }

    private XSSFWorkbook writeToWorkbook( Map<Integer, List<Object>> data , XSSFWorkbook workbook, int enterAt ) {
        //USE THE SAME SHEET
        XSSFSheet sheet = workbook.getSheetAt( 0 );
        //use style from the workbook
        XSSFDataFormat df = workbook.createDataFormat();
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat( df.getFormat( "MM/dd/yyyy" ) );        // Iterate over data and write to sheet
        Set<Integer> keyset = data.keySet();
        int rownum = enterAt;
        for ( Integer key : keyset ) {
            Row row = sheet.createRow( rownum++ );
            List<Object> objArr = data.get( key );

            int cellnum = 0;
            for ( Object obj : objArr ) {
                Cell cell = row.createCell( cellnum++ );
                if ( obj instanceof String ) {
                    cell.setCellValue( (String) obj );
                } else if ( obj instanceof Integer ) {
                    cell.setCellValue( (Integer) obj );
                } else if ( obj instanceof Date ) {
                    cell.setCellStyle( style );
                    cell.setCellValue( (Date) obj );
                } else if ( obj instanceof Long ) {
                    cell.setCellValue( String.valueOf( (Long) obj ) );
                } else if ( obj instanceof Double ) {
                    cell.setCellValue( (Double) obj );
                }
            }
        }
        return workbook;
    }

    private File createFileInLocal(String fileName, XSSFWorkbook workbook, ReportRequest reportRequest) {

        //create excel file
        LOG.info( "creating excel file on local system " );
        FileOutputStream fileOutput = null;
        File file = null;
        String fileDirectoryLocation = LocalPropertyFileHandler.getInstance()
                .getProperty(APPLICATION_PROPERTY_FILE, FILEUPLOAD_DIRECTORY_LOCATION).orElse(null);
        LOG.info("File Location : {}",fileDirectoryLocation);
        try {
            file = new File( fileDirectoryLocation + File.separator + fileName );
            //write output to the file
            if ( file.createNewFile() ) {
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug( "File created at {}. File Name {}", file.getAbsolutePath(), fileName );
                }
                fileOutput = new FileOutputStream( file );
                LOG.debug( "Created file output stream to write into {}", fileName );
                workbook.write( fileOutput );
                LOG.debug( "Wrote into file {}", fileName );
            }
            LOG.debug( "Excel creation status {}", file.exists() );
        }catch ( FileNotFoundException fe ) {
            LOG.error( "File not found exception while creating file {}", fileName, fe );
            FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();
            failedMessagesService.insertTemporaryFailedReportRequest(reportRequest);
        } catch ( IOException e ) {
            LOG.error( "IO  exception while creating file {}", fileName, e );
            FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();
            failedMessagesService.insertTemporaryFailedReportRequest(reportRequest);
        }  finally {
            try {
                if ( fileOutput != null )
                    fileOutput.close();
            } catch ( IOException e ) {
                LOG.error( "Exception caught while generating report " + fileName + ": " + e.getMessage() );
            }
        }
        return file;
    }
}