package com.realtech.socialsurvey.api.transformers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.api.models.v2.ReviewV2VO;
import com.realtech.socialsurvey.api.models.v2.ServiceProviderInfoV2;
import com.realtech.socialsurvey.api.models.v2.SurveyGetV2VO;
import com.realtech.socialsurvey.api.models.v2.SurveyResponseV2VO;
import com.realtech.socialsurvey.api.models.v2.TransactionInfoGetV2VO;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.SurveyResponse;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.utils.CommonUtils;


@Component
public class SurveyV2Transformer implements Transformer<SurveyGetV2VO, SurveyDetails, SurveyGetV2VO>
{



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
        		//Customer details
            transactionInfo.setCustomerEmail( d.getCustomerEmail() );
            transactionInfo.setCustomerFirstName( d.getCustomerFirstName() );
            transactionInfo.setCustomerLastName( d.getCustomerLastName() );

          //check is customer last name is null or customer first name have more then one words
            if(  d.getCustomerEmail().indexOf(" ") > 0 ) {
            		if(  d.getCustomerFirstName().length() >=  d.getCustomerFirstName() .indexOf(" ") + 2 ) {
            			String newFirstName =  d.getCustomerFirstName().substring(0,  d.getCustomerFirstName() .indexOf(" ") );
            			String partialLastName =  d.getCustomerFirstName().substring(  d.getCustomerFirstName() .indexOf(" ") + 1 ,  d.getCustomerFirstName() .length());
            			transactionInfo.setCustomerFirstName(newFirstName);
            			//append if there is already last name 
            			if(StringUtils.isEmpty( d.getCustomerLastName() ))
            				transactionInfo.setCustomerLastName(partialLastName);
            			else
            				transactionInfo.setCustomerLastName(partialLastName + " " +  d.getCustomerLastName() );
        			}
            }
            
            //Transaction details
            transactionInfo.setTransactionCity( d.getCity() );
            transactionInfo.setTransactionState( d.getState() );
            if ( d.getSurveyTransactionDate() != null )
                transactionInfo.setTransactionDateTime(
                    CommonUtils.formatDate( d.getSurveyTransactionDate(), CommonConstants.SURVEY_API_DATE_FORMAT ) );
            transactionInfo.setTransactionRef( d.getSourceId() );
            if ( objects[0] != null && objects[0] instanceof SurveyPreInitiation )
                transactionInfo.setSurveySentDateTime( CommonUtils.formatDate(
                    ( (SurveyPreInitiation) objects[0] ).getLastReminderTime(), CommonConstants.SURVEY_API_DATE_FORMAT ) );
            
            //Service provider details
            serviceProviderInfo.setServiceProviderEmail( d.getAgentEmailId() );
            serviceProviderInfo.setServiceProviderName( d.getAgentName() );
            serviceProviderInfo.setServiceProviderId(d.getAgentId());
            //branch name
            if ( StringUtils.equalsIgnoreCase( d.getBranchName(), CommonConstants.DEFAULT_BRANCH_NAME ) ) {
                serviceProviderInfo.setServiceProviderOfficeName( "N/A" );
                serviceProviderInfo.setServiceProviderOfficeId( CommonConstants.DEFAULT_BRANCH_ID );
            } else {
                serviceProviderInfo.setServiceProviderOfficeName( d.getBranchName() );
                serviceProviderInfo.setServiceProviderOfficeId( d.getBranchId() );
            }
            //region name
            if ( StringUtils.equalsIgnoreCase( d.getRegionName(), CommonConstants.DEFAULT_REGION_NAME ) ) {
                serviceProviderInfo.setServiceProviderRegionName( "N/A" );
                serviceProviderInfo.setServiceProviderRegionId( CommonConstants.DEFAULT_REGION_ID );
            } else {
                serviceProviderInfo.setServiceProviderRegionName( d.getRegionName() );
                serviceProviderInfo.setServiceProviderRegionId( d.getRegionId() );
            }

            survey.setReviewId( d.get_id() );
            review.setSummary( d.getSummary() );
            review.setDescription( d.getReview() );
            review.setRating( String.valueOf( d.getScore() ) );
            review.setReviewCompletedDateTime(
                CommonUtils.formatDate( d.getSurveyCompletedDate(), CommonConstants.SURVEY_API_DATE_FORMAT ) );
            review.setRetakeSurvey( d.isRetakeSurvey() );
            if ( d.getSurveyUpdatedDate() != null ) {
                review.setReviewUpdatedDateTime(
                    CommonUtils.formatDate( d.getSurveyUpdatedDate(), CommonConstants.SURVEY_API_DATE_FORMAT ) );
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
                		|| d.getSource().equalsIgnoreCase( "API" ) || d.getSource().equalsIgnoreCase( "FTP" ) || d.getSource().equalsIgnoreCase( "LONEWOLF" ) )
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
                transactionInfo.setTransactionDateTime( CommonUtils.formatDate( surveyPreInitiation.getEngagementClosedTime(),
                    CommonConstants.SURVEY_API_DATE_FORMAT ) );
            if ( StringUtils.isBlank( transactionInfo.getTransactionRef() ) )
                transactionInfo.setTransactionRef( surveyPreInitiation.getSurveySourceId() );

          //check is customer last name is null or customer first name have more then one words
            if( surveyPreInitiation.getCustomerFirstName().indexOf(" ") > 0 ) {
            		if( surveyPreInitiation.getCustomerFirstName().length() >= surveyPreInitiation.getCustomerFirstName().indexOf(" ") + 2 ) {
            			String newFirstName = surveyPreInitiation.getCustomerFirstName().substring(0, surveyPreInitiation.getCustomerFirstName().indexOf(" ") );
            			String partialLastName = surveyPreInitiation.getCustomerFirstName().substring( surveyPreInitiation.getCustomerFirstName().indexOf(" ") + 1 , surveyPreInitiation.getCustomerFirstName().length());
            			transactionInfo.setCustomerFirstName(newFirstName);
            			//append if there is already last name 
            			if(StringUtils.isEmpty(surveyPreInitiation.getCustomerLastName()))
            				transactionInfo.setCustomerLastName(partialLastName);
            			else
            				transactionInfo.setCustomerLastName(partialLastName + " " + surveyPreInitiation.getCustomerLastName());
        			}
            }
            
            //but since we aren't updating the type feild in mongo we directly update 
            transactionInfo.setTransactionType( surveyPreInitiation.getTransactionType() );

        }

        if ( d != null && d.getStage() == -1 ) {
            survey.setReviewStatus( "completed" );
        } else {
            survey.setReviewStatus( "incomplete" );
        }

        survey.setReview( review );
        survey.setTransactionInfo( transactionInfo );
        survey.setServiceProviderInfo( serviceProviderInfo );

        return survey;
    }

}
