package com.realtech.socialsurvey.compute.topology.bolts.transactioningestion;

import static com.realtech.socialsurvey.compute.common.ComputeConstants.ACCESS_TOKEN;
import static com.realtech.socialsurvey.compute.common.ComputeConstants.APPLICATION_PROPERTY_FILE;
import static com.realtech.socialsurvey.compute.common.ComputeConstants.FTP_SURVEY_BATCH_SIZE;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.compute.common.LocalPropertyFileHandler;
import com.realtech.socialsurvey.compute.common.RetrofitApiBuilder;
import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.entities.BulkSurveyPutVO;
import com.realtech.socialsurvey.compute.entities.SurveyPutVO;
import com.realtech.socialsurvey.compute.entities.TransactionIngestionMessage;
import com.realtech.socialsurvey.compute.entities.response.BulkSurveyProcessResponseVO;
import com.realtech.socialsurvey.compute.entities.response.FtpSurveyResponse;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;

import retrofit2.Call;
import retrofit2.Response;


public class BatchSurveyAndSendToApi extends BaseComputeBoltWithAck
{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( ConvertToSurveyObject.class );
    
    @Override
    public void declareOutputFields( OutputFieldsDeclarer arg0 )
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void executeTuple( Tuple input )
    {
        LOG.info( "Executing batch survey and send to api bolt." );
        //check if the last tuple was success
        boolean prevSuccess = input.getBooleanByField( "isSuccess" );
        TransactionIngestionMessage transactionIngestionMessage = (TransactionIngestionMessage) input.getValueByField( "transactionIngestionMessage" );
        if(prevSuccess) {
            try {
                //get the bulk survey put vo object 
                BulkSurveyPutVO bulkSurveyPutVO  = (BulkSurveyPutVO) input.getValueByField( "bulkSurvey" );
                boolean ranCompletely = batchSurveyListCallApi(bulkSurveyPutVO,transactionIngestionMessage);
                if(!ranCompletely) {
                    LOG.warn( "There is an error with the api processing either token has expired or api is not able to hie endpoint" );
                    SSAPIOperations.getInstance().processFailedFtpRequest(
                            "There is an error with the api processing either token has expired or api is not able to hie endpoint",
                            transactionIngestionMessage, true );
                }
                LOG.debug( "the response for the bulkSurveyPutVO : {} \n ranCompletely: {}" , bulkSurveyPutVO,ranCompletely);
                //Batch the surveys 
                //check if the response is correct 
            } catch ( IOException e ) {
                LOG.error( "Failed to send survey object api" );
            }  
        }
        
    }

    private Map<String, Object> putBulkSurveyApi(BulkSurveyPutVO bulkSurveyPutVO) {
        //add the auth token to properties files
        String token = LocalPropertyFileHandler.getInstance()
            .getProperty( APPLICATION_PROPERTY_FILE, ACCESS_TOKEN ).orElse( null );
        Call<Map<String, Object>> requestCall = RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationService().postBulkSurveyTransactions(token , bulkSurveyPutVO );
        try {
            Response<Map<String, Object>> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "response {}", response.body() );
            }
            return response.body();
        } catch ( IOException e ) {
            LOG.error( "IOException/ APIIntergrationException caught", e );
            return null;
        }
        
    }
    
    @SuppressWarnings ( "unchecked")
    private boolean batchSurveyListCallApi(BulkSurveyPutVO bulkSurveyPutVO,TransactionIngestionMessage transactionIngestionMessage) throws IOException {
       LOG.debug( "run the survey list batch wise" );
      //get the bulk survey object
      //get complete surveyList
      List<SurveyPutVO> bulkSurveyList = bulkSurveyPutVO.getSurveys();
      //get the size of survey list
      int size = bulkSurveyList.size();
      int startIndex = 0;
      String ftpBatch = LocalPropertyFileHandler.getInstance()
          .getProperty( APPLICATION_PROPERTY_FILE, FTP_SURVEY_BATCH_SIZE ).orElse( null );
      int batchSize = Integer.parseInt( ftpBatch );
      double responseCode = 0;
      boolean ranCompletely = true;
      BulkSurveyPutVO newBulkSurveyObj = new BulkSurveyPutVO();
      newBulkSurveyObj.setCompanyId( bulkSurveyPutVO.getCompanyId() );
      newBulkSurveyObj.setSource( bulkSurveyPutVO.getSource() );
      FtpSurveyResponse ftpSurveyResponse = new FtpSurveyResponse();
      ftpSurveyResponse.setTotalTransaction( size );
      do {
          if( (startIndex + batchSize) >= size ) batchSize = size - startIndex;
          List<SurveyPutVO> batchSurveyList= new ArrayList<>();
          for(int pos = startIndex ; pos < startIndex + batchSize ;pos++) 
              batchSurveyList.add( bulkSurveyList.get( pos ) );
          newBulkSurveyObj.setSurveys( batchSurveyList );
          Map<String, Object> response = putBulkSurveyApi( newBulkSurveyObj );
          //fetch message from response object with msg as key
          Map<String, Object> msg =  (Map<String, Object>) response.get( "msg" );
          //fetch code from msg value with code as key
          responseCode = (double) msg.get( "code" );
          ArrayList<BulkSurveyProcessResponseVO> responseData = convertToArray(response);
          processResponse( ftpSurveyResponse, responseData, startIndex );
          startIndex += batchSize;
      }while(startIndex < size && responseCode == 201);
      LOG.debug( "ftpSurveyResponse : {}",ftpSurveyResponse );
      //check if the batch ran completely without error
      if(startIndex < size && responseCode != 201) ranCompletely = false;
      //Send completion mail if it ran completely
      if(ranCompletely)
          //connect to api and send mail with the ftpResponseObject , file name , time , companyId , file location
          SSAPIOperations.getInstance().processSuccessFtpRequest( transactionIngestionMessage.getCompanyId() 
              ,transactionIngestionMessage.getFtpId(),transactionIngestionMessage.getS3FileLocation(), ftpSurveyResponse );
      return ranCompletely;
    }
    
    //converting the data object to a list of BulkSurveyProcessResponseVO
    @SuppressWarnings ( "unchecked")
    private ArrayList<BulkSurveyProcessResponseVO> convertToArray(Map<String, Object> response) {
        Map<String, Object> data =   (Map<String, Object>) response.get( "data" );
        String jsonRes = new Gson().toJson( data.get( "response" ) );
        
        Gson gson=new Gson();
        Type typeOfList = new TypeToken<ArrayList<BulkSurveyProcessResponseVO>>(){}.getType();
        return gson.fromJson(jsonRes,typeOfList );
        
    }
    
    //method to process response and get values
    //total transations
    //total surveys
    //customer 1 count 
    //customer2 count 
    //error list 
    //input shd be response and start index of batch
    private FtpSurveyResponse processResponse(FtpSurveyResponse ftpSurveyResponse,ArrayList<BulkSurveyProcessResponseVO> responseData , int startIndex) {
        int totalSurvey = ftpSurveyResponse.getTotalSurveys();
        int customer1Count = ftpSurveyResponse.getCustomer1Count();
        int customer2Count = ftpSurveyResponse.getCustomer2Count();
        int errorNum = ftpSurveyResponse.getErrorNum();
        //since startIndex starts from 0 we need to add 1
        //since we are not including header line we need to include that one too
        int countLoop = 1+1+startIndex;
        Map<Integer, String> errorMessage = ftpSurveyResponse.getErrorMessage();
        for(BulkSurveyProcessResponseVO bulkResponse:responseData) {
            //if processed is true update totalSurveys,customer1Count,customer2Count
            if(bulkResponse.isProcessed()) {
                ++totalSurvey;
                ++customer1Count;
                if(bulkResponse.getSurveyIds().size()>1) {
                    ++totalSurvey;
                    ++customer2Count;
                }
                    
            }else {
                //else update errorNum,errorMessage
                ++errorNum;
                errorMessage.put( countLoop, bulkResponse.getErrorMessage() );
            }
            ++countLoop;
        }
        ftpSurveyResponse.setTotalSurveys( totalSurvey );
        ftpSurveyResponse.setCustomer1Count( customer1Count );
        ftpSurveyResponse.setCustomer2Count( customer2Count );
        ftpSurveyResponse.setErrorNum( errorNum );
        ftpSurveyResponse.setErrorMessage( errorMessage );
        
        return ftpSurveyResponse;
    }

    @Override
    public List<Object> prepareTupleForFailure()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    
}
