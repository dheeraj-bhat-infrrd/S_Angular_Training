package com.realtech.socialsurvey.compute.topology.bolts.emailreports;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.APIOperations;
import com.realtech.socialsurvey.compute.common.EmailConstants;
import com.realtech.socialsurvey.compute.entities.ReportRequest;
import com.realtech.socialsurvey.compute.entities.SolrEmailMessageWrapper;
import com.realtech.socialsurvey.compute.entities.response.SOLRResponse;
import com.realtech.socialsurvey.compute.enums.ReportStatus;
import com.realtech.socialsurvey.compute.enums.ReportType;
import com.realtech.socialsurvey.compute.exception.APIIntegrationException;
import com.realtech.socialsurvey.compute.services.FailedMessagesService;
import com.realtech.socialsurvey.compute.services.impl.FailedMessagesServiceImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;


public class QuerySolrToFetchSurveyRelatedMailBolt extends BaseComputeBoltWithAck
{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( QuerySolrToFetchSurveyRelatedMailBolt.class );
    private static final int BATCH_SIZE = 1000;


    @Override
    public void declareOutputFields( OutputFieldsDeclarer outputFieldsDeclarer ) {
        outputFieldsDeclarer.declare( new Fields( "isSuccess", "surveyMailList", "fileUploadId", "zoneId", "status",
                "startIndex", "batchSize", "reportRequest" ) );
    }


    @Override
    public void executeTuple( Tuple input ) {
        LOG.info( "Executing query to fetch survey invitation mails from solr" );
        boolean success = false;

        // get the report request from the tuple
        ReportRequest reportRequest = ConversionUtils.deserialize( input.getString( 0 ), ReportRequest.class );

        if ( reportRequest.getReportType().equals( ReportType.SURVEY_INVITATION_EMAIL_REPORT.getName() ) ) {
            int pageNum = 0;
            String status = null;
            SOLRResponse<SolrEmailMessageWrapper> solrResponse;


            LOG.info( "Conversion to GMT timezone stared" );
            //convert the startDate and endDate to GMT
            String startDateInGmt = getStartDateTimeInGmt( reportRequest.getStartDateExpectedTimeZone() );
            String endDateInGmt = getEndDateTimeInGmt( reportRequest.getEndDateExpectedTimeZone() );

            String zoneId = reportRequest.getExpectedTimeZone();
            long fileUploadId = reportRequest.getFileUploadId();

            String fieldQuery = formulateFieldQuery(Arrays.asList( EmailConstants.EMAIL_TYPE_SURVEY_INVITATION_MAIL),
                    startDateInGmt, endDateInGmt, reportRequest.getProfileLevel(), reportRequest.getProfileValue());
            String fieldList = formulateFieldList(Arrays.asList("surveySourceId","senderName", "branchName", "regionName",
                    "agentEmailId", "recipientsName", "recipients", "emailAttemptedDate", "emailDeliveredDate", "emailBounceDate",
                    "emailBlockedDate", "emailDefferedDate", "emailOpenedDate", "emailLinkClickedDate"));

            try {
                do {
                    pageNum++;
                    solrResponse = APIOperations.getInstance().getEmailMessagesFromSolr( "*:*", fieldQuery, fieldList,
                            BATCH_SIZE, pageNum );
                    if ( solrResponse.getNumFound() > 0 && !solrResponse.getDocs().isEmpty() ) {
                        status = ReportStatus.PROCESSING.getValue();

                    } else if ( solrResponse.getNumFound() == 0 && solrResponse.getDocs().isEmpty() ) {
                        status = ReportStatus.BLANK.getValue();
                    } else if ( solrResponse.getNumFound() > 0 && solrResponse.getDocs().isEmpty() ) {
                        status = ReportStatus.PROCESSED.getValue();
                    }
                    success = true;
                    LOG.info( "Emitting tuple with success = {}, surveyInvitationMails = {}, fileUploadId = {}, zoneId = {}, " +
                                    "status = {}, startIndex = {}, batchSize = {}", success, solrResponse.getDocs(),
                            fileUploadId, zoneId, status, pageNum, BATCH_SIZE );
                    _collector.emit( input, Arrays.asList( success, solrResponse.getDocs(), fileUploadId, zoneId, status,
                            pageNum, BATCH_SIZE, reportRequest ) );

                } while ( solrResponse.getNumFound() != 0 && solrResponse.getStart() < solrResponse.getNumFound() );
            } catch ( APIIntegrationException | IllegalArgumentException | IOException ex ) {
                success = true;
                LOG.error( "Exception occurred while querying solr " + ex.getMessage() );
                FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();
                failedMessagesService.insertTemporaryFailedReportRequest( reportRequest );
                LOG.info("Emitting tuple with success = {}, surveyInvitationMails = {}, fileUploadId = {}, zoneId = {}, "
                                + "processingCompleted = {}, startIndex = {}, batchSize = {}",
                        success, null, fileUploadId, zoneId, ReportStatus.FAILED.getValue(), pageNum, BATCH_SIZE );
                _collector.emit( input, Arrays.asList( success, null, fileUploadId, zoneId, ReportStatus.FAILED.getValue(),
                        pageNum, BATCH_SIZE, reportRequest ) );
            }
        } else {
            LOG.info( "Emitting tuple with success = {}, surveyInvitationMails = {}, fileUploadId = {}, zoneId = {}, "
                    + "status = {}, startIndex = {}, batchSize = {}", success, null, -1, null, null, -1, 0 );
            _collector.emit( input, Arrays.asList( success, null, -1, null, null, -1, 0, null ) );
        }


    }


    /**
     * Converts the given endDate timezone to GMT
     * @param endDateExpectedTimeZone
     * @return
     */
    private String getEndDateTimeInGmt( String endDateExpectedTimeZone ) {
        String endDateGmtTimeZone;
        if ( endDateExpectedTimeZone == null )
            endDateGmtTimeZone = "NOW";
        else
            endDateGmtTimeZone = ConversionUtils.convertToGmt( endDateExpectedTimeZone );
        return endDateGmtTimeZone;
    }


    /**
     * Converts the given startDate timezone to GMT
     * @param startDateExpectedTimeZone
     * @return
     */
    private String getStartDateTimeInGmt( String startDateExpectedTimeZone ) {
        String startDateGmtTimeZone;
        if ( startDateExpectedTimeZone == null )
            startDateGmtTimeZone = "NOW-30DAYS";
        else
            startDateGmtTimeZone = ConversionUtils.convertToGmt( startDateExpectedTimeZone );
        return startDateGmtTimeZone;
    }


    /**
     * Formulates the fields list
     * @param fieldList
     * @return
     */
    private String formulateFieldList( List<String> fieldList ) {
        StringBuilder fieldsListBuilder = new StringBuilder();
        for ( String field : fieldList ) {
            fieldsListBuilder.append( field ).append( " " );
        }
        return fieldsListBuilder.toString();
    }


    /**
     * Formulates the fields query for getting mails based on mailType , start and end date
     */
    public String formulateFieldQuery( List<String> mailTypes, String startDate, String endDate, String profileLevel,
                                       long profileValue ) {
        LOG.info( "Formulating the field query for getting mails based on mailType , start and end date" );
        StringBuilder mailTypeInQueryBuilder = new StringBuilder( "(" );
        for ( String mailType : mailTypes ) {
            mailTypeInQueryBuilder.append( " " ).append( mailType );
        }
        mailTypeInQueryBuilder.append( ")" );

        StringBuilder fieldQuery = new StringBuilder( "mailType:" ).append( mailTypeInQueryBuilder )
                .append( " AND emailAttemptedDate:[" ).append( startDate ).append( " TO " ).append( endDate ).append( "]" )
                .append( " AND " ).append( profileLevel ).append( ":" ).append( profileValue );

        LOG.info( "Field query: {}", fieldQuery );
        return fieldQuery.toString();
    }


    @Override
    public List<Object> prepareTupleForFailure()
    {
        return Arrays.asList( false, null, -1, null, false, 0, 0, null );
    }


}
