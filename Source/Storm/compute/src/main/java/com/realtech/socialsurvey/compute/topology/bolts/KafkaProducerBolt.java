package com.realtech.socialsurvey.compute.topology.bolts;

import com.realtech.socialsurvey.compute.common.ComputeConstants;
import com.realtech.socialsurvey.compute.common.LocalPropertyFileHandler;
import com.realtech.socialsurvey.compute.common.RedisKeyConstants;
import com.realtech.socialsurvey.compute.dao.impl.RedisSocialMediaStateDaoImpl;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;


public class KafkaProducerBolt extends BaseComputeBolt
{

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( KafkaProducerBolt.class );
    private String brokerUrl;
    private String socialPostTopicDev;

    Properties props;
    private RedisSocialMediaStateDaoImpl redisSinceRecordFetchedDao;

    @Override
    public void execute( Tuple tuple )
    {
        LOG.info( "Build KafkaBolt for pushing messages in kafka queue." );
        //Configure the Producer
        boolean success = true;

        try ( KafkaProducer<String, String> kafkaWriter = new KafkaProducer<>( props ) ) {
            ProducerRecord<String, String> msg = new ProducerRecord<>( socialPostTopicDev, tuple.getString( 0 ),
                tuple.getString( 1 ) );
            RecordMetadata recordMetadata = kafkaWriter.send( msg ).get();
            //DO NOT REMOVE THIS DEBUG LOG
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "Offset = {}", recordMetadata.offset() );
            }

            kafkaWriter.flush();
        } catch ( Exception e ) {
            success = false;

            if ( redisSinceRecordFetchedDao.getTTLForKey( RedisKeyConstants.IS_KAFKA_DOWN ) < 0 ) {
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug( "Resetting last fetched for {}", tuple.getStringByField( "lastFetchedKey" ) );
                }
                redisSinceRecordFetchedDao.addWithExpire( RedisKeyConstants.IS_KAFKA_DOWN, "true", 60 );
                redisSinceRecordFetchedDao.resetLastFetched( tuple.getStringByField( "lastFetchedKey" ) );
            }

            // TODO reset lastfetched and add time to live
            LOG.warn( "Kakfa server might be down !!! Needs to be handled immediately" );
        }
        LOG.info( "Emitting message from kafkaproducer bolt with companyId = {}, success = {}", tuple.getString( 0 ), success );
        _collector.emit( tuple, Arrays.asList( tuple.getString( 0 ), tuple.getString( 1 ), success ) );
    }


    @Override
    public void declareOutputFields( OutputFieldsDeclarer declarer )
    {
        declarer.declare( new Fields( "companyId", "post", "success" ) );
    }


    @Override
    public void prepare( Map stormConf, TopologyContext context, OutputCollector collector )
    {
        super.prepare( stormConf, context, collector );

        brokerUrl = LocalPropertyFileHandler.getInstance()
            .getProperty( ComputeConstants.APPLICATION_PROPERTY_FILE, ComputeConstants.BROKER_URL ).orElseGet( null );
        socialPostTopicDev = LocalPropertyFileHandler.getInstance()
            .getProperty( ComputeConstants.APPLICATION_PROPERTY_FILE, ComputeConstants.SOCIAL_POST_TOPIC ).orElseGet( null );
        
        this.redisSinceRecordFetchedDao = new RedisSocialMediaStateDaoImpl();

        props = new Properties();
        props.put( ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl );
        props.put( ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer" );
        props.put( ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer" );
    }
}
