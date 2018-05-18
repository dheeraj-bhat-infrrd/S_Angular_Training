/**
 *
 */
package com.realtech.socialsurvey.compute.topology.bolts.emailreports;

import com.realtech.socialsurvey.compute.entities.ReportRequest;
import com.realtech.socialsurvey.compute.entity.SurveyInvitationEmailCountMonth;
import com.realtech.socialsurvey.compute.enums.ReportStatus;
import com.realtech.socialsurvey.compute.services.FailedMessagesService;
import com.realtech.socialsurvey.compute.services.impl.FailedMessagesServiceImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
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
import java.text.DateFormatSymbols;
import java.util.*;

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
				fileName = "Email_Message_Report_Month_" + monthStr + "_" + emailReportWrapper.get(0).getYear()
					+ (Calendar.getInstance().getTimeInMillis()) + EXCEL_FILE_EXTENSION;
				try {
					file = FileUtils.createFileInLocal(fileName, workbook);
					fileBytes = FileUtils.convertFileToBytes(file);
				} catch (IOException e) {
					LOG.error("IO  exception occured ", e);
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
		data = WorkBookUtils.writeReportHeader(SURVEY_INVITATION_EMAIL_REPORT_HEADER);
		workbook = WorkBookUtils.createWorkbook(data);
		data = getEmailReportToBeWrittenInSheet(emailReportWrapper);
		workbook = WorkBookUtils.writeToWorkbook(data, workbook, 1);
		return workbook;
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

}