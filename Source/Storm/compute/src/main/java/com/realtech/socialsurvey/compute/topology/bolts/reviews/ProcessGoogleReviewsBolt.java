
package com.realtech.socialsurvey.compute.topology.bolts.reviews;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.TupleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.mybusiness.v4.MyBusiness;
import com.google.api.services.mybusiness.v4.model.Account;
import com.google.api.services.mybusiness.v4.model.ListAccountsResponse;
import com.google.api.services.mybusiness.v4.model.ListLocationsResponse;
import com.google.api.services.mybusiness.v4.model.ListReviewsResponse;
import com.google.api.services.mybusiness.v4.model.Location;
import com.google.api.services.mybusiness.v4.model.Review;
import com.realtech.socialsurvey.compute.common.ComputeConstants;
import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.entities.OrganizationUnitIds;
import com.realtech.socialsurvey.compute.entities.SurveyDetailsVO;
import com.realtech.socialsurvey.compute.enums.SurveySource;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;


/*
 * @author Subhrajit
 */


public class ProcessGoogleReviewsBolt extends BaseComputeBoltWithAck
{

    private static final long serialVersionUID = 1L;
    private static MyBusiness myBusiness;

    // Subhrajit's account using as default
    //private static final String backupGMBAccount = "accounts/117721066461395528877";
    private static boolean fetchReviewFlag = true;
    private static String pageToken;

    private static final Logger LOG = LoggerFactory.getLogger( ProcessGoogleReviewsBolt.class );


    /*(non-Javadoc)
     * @see org.apache.storm.topology.IComponent#declareOutputFields(org.apache.storm.topology.OutputFieldsDeclarer)
    */

    @Override
    public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declare( new Fields( "surveyDetails", "iden" ) );

    }


    @Override
    public void prepare( @SuppressWarnings ( "rawtypes") Map stormConf, TopologyContext context, OutputCollector collector )
    {
        super.prepare( stormConf, context, collector );
    }


    /*(non-Javadoc)
     * @see com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck#executeTuple(org.apache.storm.tuple.Tuple)
    */

    @Override
    public void executeTuple( Tuple input )
    {
        if ( !TupleUtils.isTick( input ) ) {
            LOG.info( "Started processing google reviews." );
            myBusiness = (MyBusiness) input.getValueByField( "myBusiness" );
            try {
                List<Account> accounts = listAccounts();
                for ( Account account : accounts ) {
                    List<Location> locations = listLocations( account.getName() );
                    for ( Location location : locations ) {
                        String placeId = location.getLocationKey().getPlaceId();
                        if ( placeId != null ) {
                            fetchReviewFlag = true;
                            List<OrganizationUnitIds> ouIds = SSAPIOperations.getInstance().getDetailsFromPlaceId( placeId );
                            listSurveys( location.getName(), ouIds, input );
                        }
                    }
                }
            } catch ( Exception e ) {
                LOG.error( "Failed processing google reviews.", e );
            }
        }
    }
    
    private static List<Account> listAccounts() throws IOException  {
        MyBusiness.Accounts.List accountsList = myBusiness.accounts().list();
        ListAccountsResponse response = accountsList.execute();
        List<Account> accounts = response.getAccounts();
        if(accounts != null && !accounts.isEmpty()) {
            return accounts;
        } else {
            return new ArrayList<>();
        }
      }


    private void listSurveys( String name, List<OrganizationUnitIds> ouIds, Tuple input )
        throws IOException, ParseException
    {
        while ( fetchReviewFlag ) {
            List<Review> reviews = listReviews( name );
            if ( reviews != null && !reviews.isEmpty() ) {
                for ( OrganizationUnitIds ouId : ouIds ) {
                    long key = getKeyFromOUId( ouId );
                    long lastBatchTime = 0;
                    if(ouId.getSocialMediaLastFetched() != null && ouId.getSocialMediaLastFetched().getGoogleReviewLastFetched() != null) {
                        lastBatchTime = ouId.getSocialMediaLastFetched().getGoogleReviewLastFetched().getCurrent();
                    }
                    for ( Review review : reviews ) {
                        if ( getDateFromString( review.getUpdateTime() ).getTime() > lastBatchTime ) {
                            _collector.emit( input, new Values( setReviewToVO( review, ouId ), key ) );
                        } else {
                            fetchReviewFlag = false;
                            pageToken = null;
                            break;
                        }
                    }
                    //Update lastFetchKey
                    SSAPIOperations.getInstance().updateSocialMediaLastFetched( key,
                        getDateFromString( reviews.get( 0 ).getUpdateTime() ).getTime(), lastBatchTime, ouId.getProfileType(),
                        SurveySource.GOOGLE.getValue() );
                }
            }
        }
    }


    private long getKeyFromOUId( OrganizationUnitIds ouId )
    {
        long id = 0;
        switch ( ouId.getProfileType() ) {
            case ComputeConstants.COMPANY_SETTINGS_COLLECTION:
                id = ouId.getCompanyId();
                break;
            case ComputeConstants.REGION_SETTINGS_COLLECTION:
                id = ouId.getRegionId();
                break;
            case ComputeConstants.BRANCH_SETTINGS_COLLECTION:
                id = ouId.getBranchId();
                break;
            case ComputeConstants.AGENT_SETTINGS_COLLECTION:
                id = ouId.getAgentId();
                break;
        }
        return id;
    }


    private SurveyDetailsVO setReviewToVO( Review review, OrganizationUnitIds ouIds ) throws ParseException
    {
        SurveyDetailsVO surveyDetailsVO = new SurveyDetailsVO();
        // From SSAPI
        surveyDetailsVO.setAgentId( ouIds.getAgentId() );
        surveyDetailsVO.setBranchId( ouIds.getBranchId() );
        surveyDetailsVO.setCompanyId( ouIds.getCompanyId() );
        surveyDetailsVO.setRegionId( ouIds.getRegionId() );
        surveyDetailsVO.setAgentName( ouIds.getAgentName() );
        surveyDetailsVO.setCompleteProfileUrl( ouIds.getCompleteProfileUrl() );
        surveyDetailsVO.setProfileType( ouIds.getProfileType() );
        // From Google review
        surveyDetailsVO.setCustomerFirstName( getCustomerName( review.getReviewer().getDisplayName(), true ) );
        surveyDetailsVO.setCustomerLastName( getCustomerName( review.getReviewer().getDisplayName(), false ) );
        surveyDetailsVO.setSourceId( review.getReviewId() );
        surveyDetailsVO.setReview( review.getComment() );
        //surveyDetailsVO.setSummary( review.getComment() );
        surveyDetailsVO.setScore( getScore( review.getStarRating() ) );
        surveyDetailsVO.setSurveyCompletedDate( getDateFromString( review.getCreateTime() ).getTime() );
        surveyDetailsVO.setSurveyUpdatedDate( getDateFromString( review.getUpdateTime() ).getTime() );
        surveyDetailsVO.setSurveyTransactionDate( getDateFromString( review.getCreateTime() ).getTime() );
        surveyDetailsVO.setSurveySentDate( getDateFromString( review.getCreateTime() ).getTime() );
        // Default values
        surveyDetailsVO.setSource( SurveySource.GOOGLE.getValue() );
        surveyDetailsVO.setStage( -1 );
        surveyDetailsVO.setAgreedToShare( "true" );
        surveyDetailsVO.setShowSurveyOnUI( true );
        Date date = new Date();
        surveyDetailsVO.setCreatedOn( date.getTime() );
        surveyDetailsVO.setModifiedOn( date.getTime() );

        return surveyDetailsVO;
    }


    private Date getDateFromString( String createTime ) throws ParseException
    {
        createTime = createTime.substring( 0, createTime.indexOf( "." ) );
        createTime = createTime.concat( ".000Z" );
        DateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" );
        df.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
        return df.parse( createTime );
    }


    private double getScore( String starRating )
    {
        switch ( starRating ) {
            case "FIVE":
                return 5;
            case "FOUR":
                return 4;
            case "THREE":
                return 3;
            case "TWO":
                return 2;
            case "ONE":
                return 1;
        }
        return 0;
    }


    private String getCustomerName( String displayName, boolean flag )
    {
        int lastSpacePos = displayName.lastIndexOf( ' ' );
        if(flag && lastSpacePos > 0) {
            return displayName.substring( 0, lastSpacePos );
        } else if(flag && lastSpacePos < 0 ) {
          return displayName;  
        } else if(!flag && lastSpacePos > 0) {
            return displayName.substring( lastSpacePos );
        } else {
            return "";
        }
    }


    private List<Review> listReviews( String locationName ) throws IOException
    {
        MyBusiness.Accounts.Locations.Reviews.List reviewsList = myBusiness.accounts().locations().reviews()
            .list( locationName );
        reviewsList.setPageSize( 50 );
        reviewsList.setPageToken( pageToken );
        ListReviewsResponse response = reviewsList.execute();
        pageToken = response.getNextPageToken();
        if ( response.getNextPageToken() == null ) {
            fetchReviewFlag = false;
        }
        return response.getReviews();
    }


    private List<Location> listLocations( String accountName ) throws IOException
    {
        MyBusiness.Accounts.Locations.List locationsList = myBusiness.accounts().locations().list( accountName );
        ListLocationsResponse response = locationsList.execute();
        List<Location> locations = response.getLocations();
        if(locations != null && !locations.isEmpty()) {
            return locations;
        } else {
            return new ArrayList<>();
        }
    }


    /*(non-Javadoc)
     * @see com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck#prepareTupleForFailure()
     */

    @Override
    public List<Object> prepareTupleForFailure()
    {
        return null;
    }

}
