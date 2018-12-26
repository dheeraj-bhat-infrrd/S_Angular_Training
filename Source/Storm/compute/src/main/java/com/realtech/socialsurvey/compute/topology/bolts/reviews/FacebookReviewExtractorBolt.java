package com.realtech.socialsurvey.compute.topology.bolts.reviews;

import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.dao.RedisSocialMediaStateDao;
import com.realtech.socialsurvey.compute.dao.impl.RedisSocialMediaStateDaoImpl;
import com.realtech.socialsurvey.compute.entities.BranchVO;
import com.realtech.socialsurvey.compute.entities.FacebookTokenForSM;
import com.realtech.socialsurvey.compute.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.compute.entities.response.FacebookReviewData;
import com.realtech.socialsurvey.compute.entities.SurveyDetailsVO;
import com.realtech.socialsurvey.compute.enums.ProfileType;
import com.realtech.socialsurvey.compute.enums.SurveySource;
import com.realtech.socialsurvey.compute.exception.APIIntegrationException;
import com.realtech.socialsurvey.compute.reviews.FacebookReviewProcessor;
import com.realtech.socialsurvey.compute.reviews.impl.FacebookReviewProcessorImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import com.realtech.socialsurvey.compute.utils.UrlHelper;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.TupleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;



/**
 * @author Lavanya
 * Bolt for fetching facebook reviews
 */

public class FacebookReviewExtractorBolt extends BaseComputeBoltWithAck
{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( FacebookReviewExtractorBolt.class );

    private RedisSocialMediaStateDao socialMediaStateDao;
    private FacebookReviewProcessor facebookReviewProcessor;

    @Override public void prepare( Map stormConf, TopologyContext context, OutputCollector collector )
    {
        super.prepare( stormConf, context, collector );
        this.socialMediaStateDao = new RedisSocialMediaStateDaoImpl();
        this.facebookReviewProcessor = new FacebookReviewProcessorImpl();
    }


    @Override public void executeTuple( Tuple input )
    {
        if( !TupleUtils.isTick( input ) ) {
            long companyId = input.getLongByField( "companyId" );
            SocialMediaTokenResponse mediaToken = (SocialMediaTokenResponse) input.getValueByField( "facebookToken" );
            //set the companyId for mediaToken
            mediaToken.setCompanyId( companyId );

            FacebookTokenForSM fbToken =
                mediaToken.getSocialMediaTokens() != null ? mediaToken.getSocialMediaTokens().getFacebookToken() : null;
            try {

                if ( fbToken != null ) {
                    //Check fot facebook rate limiting
                    if ( isRateLimitExceeded( mediaToken ) ) {
                        LOG.warn( "Rate limit exceeded" );
                    }
                    //check if the facebook token has expired
                    else if ( mediaToken.getSocialMediaTokens().getFacebookToken().isTokenExpiryAlertSent() ) {
                        LOG.warn( "Socialmedia Token has been expired for profileLink {}",
                            mediaToken.getSocialMediaTokens().getFacebookToken().getFacebookPageLink() );
                    } else {
                        String pageId = UrlHelper.getFacebookPageIdFromURL( fbToken.getFacebookPageLink() );
                        String lastFetchedKey = "FbReview_" + mediaToken.getProfileType().toString() + "_" + mediaToken.getIden() + "_" + pageId;
                        final List<FacebookReviewData> reviews = facebookReviewProcessor.fetchReviews( mediaToken );
                        //todo change to debug
                        LOG.info( "Total reviews fetched for {} : {}", lastFetchedKey, reviews.size() );
                        for ( FacebookReviewData review : reviews ) {
                                SurveyDetailsVO surveyDetails = createSurveyDetailsVO( review, mediaToken );
                                _collector.emit( input, new Values( companyId, surveyDetails, mediaToken.getIden() ) );
                                //todo change this to debug log
                                LOG.debug( "Tuple with companyID= {} and surveyDetails {} emitted successfully", companyId,
                                    surveyDetails );
                        }
                    }

                } else
                    LOG.warn( "No facebook token found for company {}", companyId );

            } catch ( JedisConnectionException jce ) {
                LOG.error( "Redis might be down !!! Error message is {}", jce.getMessage() );
            } catch ( APIIntegrationException | IOException e ) {
                LOG.error( "ApiIntergration / IOException {} ", e );
                LOG.error( "Something went wrong while accessing ssApi so resetting the lastFetchedKey " );
                /*String pageId = UrlHelper.getFacebookPageIdFromURL( fbToken.getFacebookPageLink() );
                String lastFetchedKey = "FbReview_" + mediaToken.getProfileType().toString() + "_" + mediaToken.getIden() + "_" + pageId;
                socialMediaStateDao.resetLastFetched( lastFetchedKey );*/
                SSAPIOperations.getInstance().resetSocialMediaLastFetched(mediaToken.getProfileType().getValue(),
                    mediaToken.getIden(), SurveySource.FACEBOOK.getValue());

            } catch ( Exception e ) {
                LOG.error( " Error while reviews from facebook {} ", e );
            }
        }
    }


    /**
     * Creates surveyDetails object from the Facebook review data
     * @param review
     * @param mediaToken
     * @return
     */
    private SurveyDetailsVO createSurveyDetailsVO( FacebookReviewData review, SocialMediaTokenResponse mediaToken )
        throws APIIntegrationException, IOException
    {
        SurveyDetailsVO surveyDetails = new SurveyDetailsVO();

        //Date createdTime = ConversionUtils.secondsToDateTime( review.getCreatedTime() );

        surveyDetails.setAgreedToShare( "true" );
        if(mediaToken.getProfileType().equals( ProfileType.COMPANY )){
            surveyDetails.setCompanyId( mediaToken.getCompanyId() );
        } else if( mediaToken.getProfileType().equals( ProfileType.REGION ) ) {
            surveyDetails.setCompanyId( mediaToken.getCompanyId() );
            surveyDetails.setRegionId( mediaToken.getIden() );
            surveyDetails.setRegionName( mediaToken.getContactDetails().getName() );
        } else if(mediaToken.getProfileType().equals( ProfileType.BRANCH )) {
            surveyDetails.setCompanyId( mediaToken.getCompanyId() );
            surveyDetails.setBranchId( mediaToken.getIden() );
            surveyDetails.setBranchName( mediaToken.getContactDetails().getName() );
            //get the region details from the branch
            //todo make it a different method
            final Optional<BranchVO> branchDetails = fetchBranchDetails( mediaToken.getIden() );
            if(branchDetails.isPresent() && branchDetails.get() != null)
            {
                surveyDetails.setRegionId( branchDetails.get().getRegionId() );
                surveyDetails.setRegionName( branchDetails.get().getRegionName() );
            }
        } else if( mediaToken.getProfileType().equals( ProfileType.AGENT ) ){
            surveyDetails.setCompanyId( mediaToken.getCompanyId() );
            surveyDetails.setAgentId( mediaToken.getIden() );
            surveyDetails.setAgentName( mediaToken.getContactDetails().getName() );
            // get the region and branch details using the agentId
            final Optional<Map<String, Long>> agentDetailsMap = fetchAgentDetails(mediaToken.getIden());
            if(agentDetailsMap.isPresent() && agentDetailsMap.get() != null &&
                !agentDetailsMap.get().isEmpty()){
                //using branch id, get branch name and region name
                Optional<BranchVO> branchDetails = fetchBranchDetails( agentDetailsMap.get().get( "branchId" ) );
                if(branchDetails.isPresent() && branchDetails.get() != null)
                {
                    surveyDetails.setBranchName( branchDetails.get().getBranch() );
                    surveyDetails.setRegionName( branchDetails.get().getRegionName() );
                }
                surveyDetails.setRegionId( agentDetailsMap.get().get( "regionId" ) );
                surveyDetails.setBranchId( agentDetailsMap.get().get( "branchId" ) );
            }
        }
        surveyDetails.setCompleteProfileUrl( mediaToken.getSocialMediaTokens().getFacebookToken().getFacebookPageLink() );
        if(review.getReviewer() != null){
            surveyDetails.setCustomerFirstName( review.getReviewer().getName() );
        }
        surveyDetails.setSource( SurveySource.FACEBOOK.getValue() );
        surveyDetails.setSourceId( review.getOpenGraphStory().getId() );
        surveyDetails.setReview( review.getReviewText() );
        surveyDetails.setScore( review.getRating() );
        surveyDetails.setSurveyTransactionDate( review.getCreatedTime() * 1000l );
        surveyDetails.setStage( -1 );
        surveyDetails.setAgreedToShare( "true" );
        surveyDetails.setSurveySentDate( review.getCreatedTime() * 1000l );
        surveyDetails.setSurveyCompletedDate( review.getCreatedTime() * 1000l );
        surveyDetails.setCreatedOn( new Date().getTime() );
        surveyDetails.setModifiedOn( new Date().getTime() );
        surveyDetails.setSurveyUpdatedDate( review.getCreatedTime() * 1000l );
        surveyDetails.setShowSurveyOnUI(true);
        surveyDetails.setProfileType( mediaToken.getProfileType().getValue() );
        surveyDetails.setFbRecommendationType( review.getRecommendationType() );
        return surveyDetails;
    }


    private Optional<Map<String,Long>> fetchAgentDetails(long iden) throws APIIntegrationException, IOException
    {
        return SSAPIOperations.getInstance().findPrimaryUserProfileByAgentId( iden );
    }


    private Optional<BranchVO> fetchBranchDetails( long iden ) throws APIIntegrationException, IOException
    {
        return SSAPIOperations.getInstance().getBranchDetails( iden );
    }


    @Override public List<Object> prepareTupleForFailure()
    {
        return new Values( 0l, null, 0l );
    }


    @Override public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declare( new Fields( "companyId", "surveyDetails", "iden" ) );
    }

    private boolean isRateLimitExceeded(SocialMediaTokenResponse mediaToken){
        String pageId = UrlHelper.getFacebookPageIdFromURL( mediaToken.getSocialMediaTokens().getFacebookToken().getFacebookPageLink());
        return socialMediaStateDao.isFacebookApplicationLockSet() || socialMediaStateDao.isFacebookPageLockSet( pageId )
            || socialMediaStateDao
            .isFacebookTokenLockSet( mediaToken.getSocialMediaTokens().getFacebookToken().getFacebookAccessToken() );
    }
}
