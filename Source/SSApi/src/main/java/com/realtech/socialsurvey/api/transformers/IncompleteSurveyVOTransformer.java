package com.realtech.socialsurvey.api.transformers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.api.models.v2.IncompeteSurveyGetVO;
import com.realtech.socialsurvey.api.models.v2.ServiceProviderInfoV2;
import com.realtech.socialsurvey.api.models.v2.TransactionInfoGetV2VO;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.utils.CommonUtils;
import com.realtech.socialsurvey.core.vo.SurveysAndReviewsVO;

@Component
public class IncompleteSurveyVOTransformer implements Transformer<List<IncompeteSurveyGetVO>, SurveysAndReviewsVO, List<IncompeteSurveyGetVO>>
{

    


    @Override
    public SurveysAndReviewsVO transformApiRequestToDomainObject( List<IncompeteSurveyGetVO> a, Object... objects )
        throws InvalidInputException
    {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public List<IncompeteSurveyGetVO> transformDomainObjectToApiResponse( SurveysAndReviewsVO d, Object... objects )
    {

        List<IncompeteSurveyGetVO> incompleteSurveyVOs = new ArrayList<IncompeteSurveyGetVO>();

        
        if(d.getPreInitiatedSurveys() != null && ! d.getPreInitiatedSurveys().isEmpty()){
        	for ( SurveyPreInitiation surveyPreInitiation : d.getPreInitiatedSurveys() ) {
        		
        		IncompeteSurveyGetVO incompeteSurveyGetVO = new IncompeteSurveyGetVO();
        		ServiceProviderInfoV2 serviceProviderInfo = new ServiceProviderInfoV2();
                TransactionInfoGetV2VO transactionInfo = new TransactionInfoGetV2VO();
				
                incompeteSurveyGetVO.setSurveyId( Long.valueOf( surveyPreInitiation.getSurveyPreIntitiationId() ) );
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
                if ( StringUtils.isBlank( transactionInfo.getTransactionType() ) )
                	transactionInfo.setTransactionType( surveyPreInitiation.getTransactionType() );                

                incompeteSurveyGetVO.setTransactionInfo( transactionInfo );
                incompeteSurveyGetVO.setServiceProviderInfo( serviceProviderInfo );
                incompleteSurveyVOs.add(incompeteSurveyGetVO);
        	}
        }
        
        return incompleteSurveyVOs;
    }




}
