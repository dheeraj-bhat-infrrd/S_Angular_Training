package com.realtech.socialsurvey.compute.topology.spouts;

import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.ComputeConstants;
import com.realtech.socialsurvey.compute.common.EnvConstants;
import com.realtech.socialsurvey.compute.common.LocalPropertyFileHandler;
import com.realtech.socialsurvey.compute.utils.ChararcterUtils;


/**
 * Builds Kafka spouts for Storm topologies
 * @author nishit
 *
 */
public class KafkaTopicSpoutBuilder
{

    // Private constructor to avoid instantiation
    // Make this a singleton class
    private KafkaTopicSpoutBuilder() {
        LocalPropertyFileHandler.getInstance().prepare(EnvConstants.getProfile());
        zookeeperBrokers = LocalPropertyFileHandler.getInstance()
                .getProperty( ComputeConstants.APPLICATION_PROPERTY_FILE, ComputeConstants.ZOOKEEPER_BROKERS_ENDPOINT ).orElse( null );
    }

    private static final Logger LOG = LoggerFactory.getLogger( KafkaTopicSpoutBuilder.class );

    private static KafkaTopicSpoutBuilder kafkaTopicSpoutBuilder;

    private String zookeeperBrokers;

    private static final String ZOOKEEPER_ROOT = "";

    // SendGrid Event Spout
    private static final String SENDGRID_EVENT_TOPIC = "mail-events-topic";
    private static final String SENDGRID_EVENT_CONSUMER_GROUP = "sgecg01";

    // Email topic spout
    private static final String MAIL_TOPIC = "mail-topic";
    private static final String MAIL_CONSUMER_GROUP = "mcg01";

    // Socail post topic
    private static final String SOCIAL_POST_TOPIC = "social-post-topic";
    private static final String SOCIAL_POST_CONSUMER_GROUP = "spcg01";

    //Report topic
    private static final String REPORT_TOPIC = "report-topic";
    private static final String REPORT_CONSUMER_GROUP = "rcg01";
    
    // Batch processing topic
    private static final String BATCH_TOPIC = "batch-topic";
    private static final String BATCH_CONSUMER_GROUP = "bcg01";
    
    
    // Batch processing topic
    private static final String USER_EVENT_TOPIC = "user-event-topic";
    private static final String USER_EVENT_CONSUMER_GROUP = "uecg01";
    

    public static synchronized KafkaTopicSpoutBuilder getInstance(){
        if (kafkaTopicSpoutBuilder == null){
            kafkaTopicSpoutBuilder = new KafkaTopicSpoutBuilder();
        }

        return kafkaTopicSpoutBuilder;
    }

    /**
     * Sendgrid kafka spout
     * @return
     */
    public KafkaSpout sendGridEventTopicSpout()
    {
        ZkHosts zkHosts = new ZkHosts( zookeeperBrokers );
        String topicName = ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ? SENDGRID_EVENT_TOPIC
                : ChararcterUtils.appendWithHypen( SENDGRID_EVENT_TOPIC, EnvConstants.getProfile() );
        String consumerGroup = ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ? SENDGRID_EVENT_CONSUMER_GROUP
                : ChararcterUtils.appendWithHypen( SENDGRID_EVENT_CONSUMER_GROUP, EnvConstants.getProfile() );
        SpoutConfig sendGridEventSpoutConfig = new SpoutConfig( zkHosts, topicName, ZOOKEEPER_ROOT, consumerGroup );
        sendGridEventSpoutConfig.ignoreZkOffsets = false;
        sendGridEventSpoutConfig.scheme = new SchemeAsMultiScheme( new StringScheme() );
        LOG.info( "Sendgrid spout initiated. Topic: {}, Consumer Group: {}", topicName, consumerGroup );
        return new KafkaSpout( sendGridEventSpoutConfig );
    }


    /**
     * Email topic kafka spout
     */
    public KafkaSpout emailTopicKafkaSpout()
    {
        ZkHosts zkHosts = new ZkHosts( zookeeperBrokers );
        String topicName = ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ? MAIL_TOPIC
                : ChararcterUtils.appendWithHypen( MAIL_TOPIC, EnvConstants.getProfile() );
        String consumerGroup = ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ? MAIL_CONSUMER_GROUP
                : ChararcterUtils.appendWithHypen( MAIL_CONSUMER_GROUP, EnvConstants.getProfile() );
        SpoutConfig mailSpoutConfig = new SpoutConfig( zkHosts, topicName, ZOOKEEPER_ROOT, consumerGroup );
        mailSpoutConfig.ignoreZkOffsets = false;
        mailSpoutConfig.scheme = new SchemeAsMultiScheme( new StringScheme() );
        LOG.info( "Mail topic spout initiated. Topic: {}, Consumer Group: {}", topicName, consumerGroup );
        return new KafkaSpout( mailSpoutConfig );
    }

    /**
     * Social post topic kafka spout
     */
    public KafkaSpout socialPostTopicKafkaSpout()
    {
        ZkHosts zkHosts = new ZkHosts( zookeeperBrokers );
        String topicName = ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ? SOCIAL_POST_TOPIC
                : ChararcterUtils.appendWithHypen( SOCIAL_POST_TOPIC, EnvConstants.getProfile() );
        String consumerGroup = ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ? SOCIAL_POST_CONSUMER_GROUP
                : ChararcterUtils.appendWithHypen( SOCIAL_POST_CONSUMER_GROUP, EnvConstants.getProfile() );
        SpoutConfig socialPostSpoutConfig = new SpoutConfig( zkHosts, topicName, ZOOKEEPER_ROOT, consumerGroup );
        socialPostSpoutConfig.ignoreZkOffsets = true;
        socialPostSpoutConfig.scheme = new SchemeAsMultiScheme( new StringScheme() );
        LOG.info( "Social post topic spout initiated. Topic: {}, Consumer Group: {}",  topicName, consumerGroup );
        return new KafkaSpout( socialPostSpoutConfig );
    }

    /**
     * Report topic kafka spout
     */
    public  KafkaSpout reportGenerationSpout() {
        ZkHosts zkHosts = new ZkHosts( zookeeperBrokers );
        String topicName = ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ? REPORT_TOPIC
                : ChararcterUtils.appendWithHypen(REPORT_TOPIC, EnvConstants.getProfile() );
        String consumerGroup = ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ? REPORT_CONSUMER_GROUP
                : ChararcterUtils.appendWithHypen(REPORT_CONSUMER_GROUP, EnvConstants.getProfile() );
        SpoutConfig reportSpoutConfig = new SpoutConfig( zkHosts, topicName, ZOOKEEPER_ROOT, consumerGroup );
        reportSpoutConfig.ignoreZkOffsets = false;
        reportSpoutConfig.scheme = new SchemeAsMultiScheme( new StringScheme() );
        LOG.info( "Report topic spout initiated. Topic: {}, Consumer Group: {}", topicName, consumerGroup );
        return new KafkaSpout( reportSpoutConfig );
    }
    
    /**
     * Batch processing topic kafka spout
     */
    public  KafkaSpout batchProcessingSpout() {
        ZkHosts zkHosts = new ZkHosts( zookeeperBrokers );
        String topicName = ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ? BATCH_TOPIC
                : ChararcterUtils.appendWithHypen(BATCH_TOPIC, EnvConstants.getProfile() );
        String consumerGroup = ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ? BATCH_CONSUMER_GROUP
                : ChararcterUtils.appendWithHypen(BATCH_CONSUMER_GROUP, EnvConstants.getProfile() );
        SpoutConfig batchSpoutConfig = new SpoutConfig( zkHosts, topicName, ZOOKEEPER_ROOT, consumerGroup );
        batchSpoutConfig.ignoreZkOffsets = false;
        batchSpoutConfig.scheme = new SchemeAsMultiScheme( new StringScheme() );
        LOG.info( "Batch topic spout initiated. Topic: {}, Consumer Group: {}", topicName, consumerGroup );
        return new KafkaSpout( batchSpoutConfig );
    }
    
    
    /**
     * user event topic kafka spout
     */
    public  KafkaSpout userEventTopicSpout() {
        ZkHosts zkHosts = new ZkHosts( zookeeperBrokers );
        String topicName = ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ? USER_EVENT_TOPIC
                : ChararcterUtils.appendWithHypen(USER_EVENT_TOPIC, EnvConstants.getProfile() );
        String consumerGroup = ( EnvConstants.getProfile().equals( EnvConstants.PROFILE_PROD ) ) ? USER_EVENT_CONSUMER_GROUP
                : ChararcterUtils.appendWithHypen(USER_EVENT_CONSUMER_GROUP, EnvConstants.getProfile() );
        SpoutConfig userEventSpoutConfig = new SpoutConfig( zkHosts, topicName, ZOOKEEPER_ROOT, consumerGroup );
        userEventSpoutConfig.ignoreZkOffsets = false;
        userEventSpoutConfig.scheme = new SchemeAsMultiScheme( new StringScheme() );
        LOG.info( "user event topic spout initiated. Topic: {}, Consumer Group: {}", topicName, consumerGroup );
        return new KafkaSpout( userEventSpoutConfig );
    }
}
