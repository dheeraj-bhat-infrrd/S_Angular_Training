package com.realtech.socialsurvey.api.transformers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.api.models.v2.ReviewV2VO;
import com.realtech.socialsurvey.api.models.v2.ServiceProviderInfoV2;
import com.realtech.socialsurvey.api.models.v2.SurveyGetV2VO;
import com.realtech.socialsurvey.api.models.v2.SurveyResponseV2VO;
import com.realtech.socialsurvey.api.models.v2.TransactionInfoGetV2VO;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.SurveyResponse;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.utils.CommonUtils;


@Component
public class SurveyV2Transformer implements Transformer<SurveyGetV2VO, SurveyDetails, SurveyGetV2VO>
{

    private String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    @Autowired
    UserManagementService userManagementService;


    @Override
    public SurveyDetails transformApiRequestToDomainObject( SurveyGetV2VO a, Object... objects ) throws InvalidInputException
    {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public SurveyGetV2VO transformDomainObjectToApiResponse( SurveyDetails d, Object... objects )
    {

        SurveyPreInitiation surveyPreInitiation;
        TransactionInfoGetV2VO transactionInfo = new TransactionInfoGetV2VO();
        ServiceProviderInfoV2 serviceProviderInfo = new ServiceProviderInfoV2();
        ReviewV2VO review = new ReviewV2VO();
        SurveyGetV2VO survey = new SurveyGetV2VO();
        List<SurveyResponseV2VO> surveyResponses = new ArrayList<SurveyResponseV2VO>();

        if ( d != null ) {
            transactionInfo.setCustomerEmail( d.getCustomerEmail() );
            transactionInfo.setCustomerFirstName( d.getCustomerFirstName() );
            transactionInfo.setCustomerLastName( d.getCustomerLastName() );

            transactionInfo.setTransactionCity( d.getCity() );
            transactionInfo.setTransactionState( d.getState() );
            if ( d.getSurveyTransactionDate() != null )
                transactionInfo
                    .setTransactionDateTime( CommonUtils.formatDate( d.getSurveyTransactionDate(), dateTimeFormat ) );
            transactionInfo.setTransactionRef( d.getSourceId() );
            if ( objects[0] != null && objects[0] instanceof SurveyPreInitiation )
                transactionInfo.setSurveySentDateTime(
                    CommonUtils.formatDate( ( (SurveyPreInitiation) objects[0] ).getLastReminderTime(), dateTimeFormat ) );
            serviceProviderInfo.setServiceProviderEmail( d.getAgentEmailId() );
            serviceProviderInfo.setServiceProviderName( d.getAgentName() );
            survey.setReviewId( d.get_id() );
            review.setSummary( d.getSummary() );
            review.setDescription( d.getReview() );
            review.setRating( String.valueOf( d.getScore() ) );
            review.setReviewCompletedDateTime( CommonUtils.formatDate( d.getModifiedOn(), dateTimeFormat ) );
            review.setRetakeSurvey( d.isRetakeSurvey() );
            if ( d.getModifiedOn() != null ) {
                review.setReviewUpdatedDateTime( CommonUtils.formatDate( d.getModifiedOn(), dateTimeFormat ) );
            }
            review.setSource( d.getSource() );
            review.setAgreedToShare( Boolean.parseBoolean( d.getAgreedToShare() ) );
            review.setReportedAbusive( d.isAbusive() );

            if ( d.getSurveyResponse() != null ) {
                for ( SurveyResponse response : d.getSurveyResponse() ) {
                    SurveyResponseV2VO responseVO = new SurveyResponseV2VO();
                    if ( Pattern.matches( "sb-range.*", response.getQuestionType() ) ) {
                        responseVO.setType( "Numeric" );
                    } else {
                        responseVO.setType( "Text" );
                    }
                    responseVO.setQuestion( response.getQuestion() );
                    responseVO.setAnswer( response.getAnswer() );
                    surveyResponses.add( responseVO );
                }
            }
            SurveyResponseV2VO responseVO = new SurveyResponseV2VO();
            responseVO.setType( "Experience" );
            responseVO.setQuestion( "How would you rate your overall experience?" );
            responseVO.setAnswer( d.getMood() );
            surveyResponses.add( responseVO );
            review.setSurveyResponses( surveyResponses );
            boolean isCRMVerified = false;
            if ( d.getSource() != null )
                if ( d.getSource().equalsIgnoreCase( "encompass" ) || d.getSource().equalsIgnoreCase( "DOTLOOP" )
                    || d.getSource().equalsIgnoreCase( "FTP" ) || d.getSource().equalsIgnoreCase( "LONEWOLF" ) )
                    isCRMVerified = true;
            review.setVerifiedCustomer( isCRMVerified );

        }

        if ( objects[0] != null && objects[0] instanceof SurveyPreInitiation ) {
            surveyPreInitiation = (SurveyPreInitiation) objects[0];
            survey.setSurveyId( Long.valueOf( surveyPreInitiation.getSurveyPreIntitiationId() ) );
            if ( StringUtils.isBlank( transactionInfo.getCustomerEmail() ) )
                transactionInfo.setCustomerEmail( surveyPreInitiation.getCustomerEmailId() );
            if ( StringUtils.isBlank( transactionInfo.getCustomerFirstName() ) )
                transactionInfo.setCustomerFirstName( surveyPreInitiation.getCustomerFirstName() );
            if ( StringUtils.isBlank( transactionInfo.getCustomerLastName() ) )
                transactionInfo.setCustomerLastName( surveyPreInitiation.getCustomerLastName() );

            if ( StringUtils.isBlank( serviceProviderInfo.getServiceProviderEmail() ) )
                serviceProviderInfo.setServiceProviderEmail( surveyPreInitiation.getAgentEmailId() );
            if ( StringUtils.isBlank( serviceProviderInfo.getServiceProviderName() ) )
                serviceProviderInfo.setServiceProviderName( surveyPreInitiation.getAgentName() );

            if ( StringUtils.isBlank( transactionInfo.getTransactionCity() ) )
                transactionInfo.setTransactionCity( surveyPreInitiation.getCity() );
            if ( StringUtils.isBlank( transactionInfo.getTransactionState() ) )
                transactionInfo.setTransactionState( surveyPreInitiation.getState() );
            if ( StringUtils.isBlank( transactionInfo.getTransactionDateTime() ) )
                transactionInfo.setTransactionDateTime(
                    CommonUtils.formatDate( surveyPreInitiation.getEngagementClosedTime(), dateTimeFormat ) );
            if ( StringUtils.isBlank( transactionInfo.getTransactionRef() ) )
                transactionInfo.setTransactionRef( surveyPreInitiation.getSurveySourceId() );

        }

        if(d != null && d.getStage() == -1){
        	survey.setReviewStatus("completed");
        }else{
        	survey.setReviewStatus("incompleted");
        }
        
        survey.setReview( review );
        survey.setTransactionInfo( transactionInfo );
        survey.setServiceProviderInfo( serviceProviderInfo );

        return survey;
    }

}
