/**
 *
 */
package com.realtech.socialsurvey.compute.topology.bolts.emailreports;

import com.realtech.socialsurvey.compute.common.LocalPropertyFileHandler;
import com.realtech.socialsurvey.compute.entities.ReportRequest;
import com.realtech.socialsurvey.compute.entity.SurveyInvitationEmailCountMonth;
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
import java.text.DateFormatSymbols;
import java.util.*;

import static com.realtech.socialsurvey.compute.common.ComputeConstants.APPLICATION_PROPERTY_FILE;
import static com.realtech.socialsurvey.compute.common.ComputeConstants.FILEUPLOAD_DIRECTORY_LOCATION;

/**
 * @author Subhrajit
 *
 */
public class WriteReportToExcelBolt extends BaseComputeBoltWithAck {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(WriteReportToExcelBolt.class);
	private static final String SURVEY_INVITATION_EMAIL_REPORT_HEADER = "Agent Name,Agent Email,Agent Branch,Agent Region,"
			+ "Transactions Received for the Agent this month,Number of Emails sent for this agent this month,Number of emails delivered,"
			+ "Number of email bounced,Number of emails dropped,Number of emails deferred,Number of emails opened,Number of Surveys Clicked";
	private static final String EXCEL_FILE_EXTENSION = ".xlsx";

	private transient XSSFWorkbook workbook;

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		outputFieldsDeclarer
				.declare(new Fields("isSuccess", "fileName", "fileBytes", "fileUploadId", "reportRequest", "status"));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void executeTuple(Tuple input) {
		boolean success = false;
        File file = null;
        String fileName = null;
        byte[] fileBytes = null;
        String status = input.getStringByField( "status" );
        ReportRequest reportRequest = (ReportRequest) input.getValueByField("reportRequest");

        if( !input.getBooleanByField("isSuccess")){
            if(workbook != null) workbook = null;
        } else {
			List<SurveyInvitationEmailCountMonth> emailReportWrapper = (List<SurveyInvitationEmailCountMonth>) input
					.getValueByField("surveyMailList");
			workbook = writeReportToWorkbook(emailReportWrapper);
			if (status.equals(ReportStatus.PROCESSED.getValue())) {
				int month = emailReportWrapper.get(0).getMonth();
				String monthStr = "All_Time";
				if(month != 0 ) {
					monthStr = new DateFormatSymbols().getMonths()[month - 1];
				}
				fileName = "Email_Message_Report_Month_" + monthStr + "_" + (Calendar.getInstance().getTimeInMillis())
						+ EXCEL_FILE_EXTENSION;
				file = createFileInLocal(fileName, workbook, reportRequest);
				try {
					fileBytes = ConversionUtils.convertFileToBytes(file);
				} catch (IOException e) {
					LOG.error("IO  exception while converting file to bytes {}", file.getName(), e);
					FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();
					failedMessagesService.insertTemporaryFailedReportRequest(reportRequest);
				}
				if (workbook == null || file == null || !file.exists() || fileBytes == null) {
					status = ReportStatus.FAILED.getValue();
				}
            } else if(status.equals(ReportStatus.FAILED.getValue()) && workbook !=null ){
                workbook = null;
            }

            success = true;
        }
        LOG.info("Emitting tuple with success = {} , fileName = {}, status = {}", success, fileName, status);
        _collector.emit(input, Arrays.asList(success,fileName,fileBytes,input.getValueByField("fileUploadId"),
                input.getValueByField("reportRequest"), status));
        //if the file is successfully created , delete from the local
        if(file != null && file.exists()) {
            if(file.delete()) LOG.info(" {} has been successfully deleted ", fileName);
            else LOG.info(" Unable to delete {} " , fileName);
        }
    }

	@Override
	public List<Object> prepareTupleForFailure() {
		return Arrays.asList(false, null, null, -1, null, null);
	}

	private XSSFWorkbook writeReportToWorkbook(List<SurveyInvitationEmailCountMonth> emailReportWrapper) {
		Map<Integer, List<Object>> data;
		data = writeEmailReportHeader(SURVEY_INVITATION_EMAIL_REPORT_HEADER);
		workbook = createWorkbook(data);
		data = getEmailReportToBeWrittenInSheet(emailReportWrapper);
		workbook = writeToWorkbook(data, workbook, 1);
		return workbook;
	}

	private Map<Integer, List<Object>> writeEmailReportHeader(String headers) {
		Map<Integer, List<Object>> reportDataToPopulate = new TreeMap<Integer, List<Object>>();
		List<Object> headerList = new ArrayList<Object>();
		String[] headerArr = headers.split(",");
		for(String header : headerArr) {
			headerList.add(header);
		}
		reportDataToPopulate.put(1, headerList);
		return reportDataToPopulate;
	}

	private XSSFWorkbook createWorkbook(Map<Integer, List<Object>> data) {
		// Blank workbook
		XSSFWorkbook workBook = new XSSFWorkbook();

		// Create a blank sheet
		XSSFSheet sheet = workBook.createSheet();
		XSSFDataFormat df = workBook.createDataFormat();
		CellStyle style = workBook.createCellStyle();
		style.setDataFormat(df.getFormat("MM/dd/yyyy"));

		// Iterate over data and write to sheet
		Set<Integer> keyset = data.keySet();
		int rownum = 0;
		for (Integer key : keyset) {
			Row row = sheet.createRow(rownum++);
			List<Object> objArr = data.get(key);

			int cellnum = 0;
			for (Object obj : objArr) {
				Cell cell = row.createCell(cellnum++);
				if (obj instanceof String) {
					cell.setCellValue((String) obj);
				} else if (obj instanceof Integer) {
					cell.setCellValue((Integer) obj);
				} else if (obj instanceof Date) {
					cell.setCellStyle(style);
					cell.setCellValue((Date) obj);
				} else if (obj instanceof Long) {
					cell.setCellValue(String.valueOf((Long) obj));
				} else if (obj instanceof Double) {
					cell.setCellValue((Double) obj);
				}
			}
		}
		return workBook;
	}

	private Map<Integer, List<Object>> getEmailReportToBeWrittenInSheet(
			List<SurveyInvitationEmailCountMonth> emailReportWrapper) {
		Map<Integer, List<Object>> surveyInvitationReportData = new TreeMap<>();
		List<Object> surveyInvitationMailReportToPopulate;
		int enterNext = 1;
		for (SurveyInvitationEmailCountMonth surveyInvitationEmailMonth : emailReportWrapper) {
			surveyInvitationMailReportToPopulate = new ArrayList<Object>();

			surveyInvitationMailReportToPopulate.add(surveyInvitationEmailMonth.getAgentName());
			surveyInvitationMailReportToPopulate.add(surveyInvitationEmailMonth.getEmailId());
			surveyInvitationMailReportToPopulate.add(surveyInvitationEmailMonth.getBranchName());
			surveyInvitationMailReportToPopulate.add(surveyInvitationEmailMonth.getRegionName());
			surveyInvitationMailReportToPopulate.add(surveyInvitationEmailMonth.getReceived());
			surveyInvitationMailReportToPopulate.add(surveyInvitationEmailMonth.getAttempted());
			surveyInvitationMailReportToPopulate.add(surveyInvitationEmailMonth.getDelivered());
			surveyInvitationMailReportToPopulate.add(surveyInvitationEmailMonth.getBounced());
			surveyInvitationMailReportToPopulate.add(surveyInvitationEmailMonth.getDropped());
			surveyInvitationMailReportToPopulate.add(surveyInvitationEmailMonth.getDiffered());
			surveyInvitationMailReportToPopulate.add(surveyInvitationEmailMonth.getOpened());
			surveyInvitationMailReportToPopulate.add(surveyInvitationEmailMonth.getLinkClicked());

			surveyInvitationReportData.put(enterNext++, surveyInvitationMailReportToPopulate);
		}
		return surveyInvitationReportData;
	}

	private XSSFWorkbook writeToWorkbook(Map<Integer, List<Object>> data, XSSFWorkbook workbook, int enterAt) {
		// USE THE SAME SHEET
		XSSFSheet sheet = workbook.getSheetAt(0);
		// use style from the workbook
		XSSFDataFormat df = workbook.createDataFormat();
		CellStyle style = workbook.createCellStyle();
		style.setDataFormat(df.getFormat("MM/dd/yyyy")); // Iterate over data and write to sheet
		Set<Integer> keyset = data.keySet();
		int rownum = enterAt;
		for (Integer key : keyset) {
			Row row = sheet.createRow(rownum++);
			List<Object> objArr = data.get(key);

			int cellnum = 0;
			for (Object obj : objArr) {
				Cell cell = row.createCell(cellnum++);
				if (obj instanceof String) {
					cell.setCellValue((String) obj);
				} else if (obj instanceof Integer) {
					cell.setCellValue((Integer) obj);
				} else if (obj instanceof Date) {
					cell.setCellStyle(style);
					cell.setCellValue((Date) obj);
				} else if (obj instanceof Long) {
					cell.setCellValue(String.valueOf((Long) obj));
				} else if (obj instanceof Double) {
					cell.setCellValue((Double) obj);
				}
			}
		}
		return workbook;
	}

	private File createFileInLocal(String fileName, XSSFWorkbook workbook, ReportRequest reportRequest) {

		// create excel file
		LOG.info("creating excel file on local system ");
		FileOutputStream fileOutput = null;
		File file = null;
		String fileDirectoryLocation = LocalPropertyFileHandler.getInstance()
				.getProperty(APPLICATION_PROPERTY_FILE, FILEUPLOAD_DIRECTORY_LOCATION).orElse(null);
		LOG.info("File Location : {}", fileDirectoryLocation);
		try {
			file = new File(fileDirectoryLocation + File.separator + fileName);
			// write output to the file
			if (file.createNewFile()) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("File created at {}. File Name {}", file.getAbsolutePath(), fileName);
				}
				fileOutput = new FileOutputStream(file);
				LOG.debug("Created file output stream to write into {}", fileName);
				workbook.write(fileOutput);
				LOG.debug("Wrote into file {}", fileName);
			}
			LOG.debug("Excel creation status {}", file.exists());
		} catch (FileNotFoundException fe) {
			LOG.error("File not found exception while creating file {}", fileName, fe);
			FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();
			failedMessagesService.insertTemporaryFailedReportRequest(reportRequest);
		} catch (IOException e) {
			LOG.error("IO  exception while creating file {}", fileName, e);
			FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();
			failedMessagesService.insertTemporaryFailedReportRequest(reportRequest);
		} finally {
			try {
				if (fileOutput != null)
					fileOutput.close();
			} catch (IOException e) {
				LOG.error("Exception caught while generating report " + fileName + ": " + e.getMessage());
			}
		}
		return file;
	}
}