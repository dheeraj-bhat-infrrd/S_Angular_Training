package com.realtech.socialsurvey.compute.topology.bolts;

import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
import org.apache.storm.kafka.bolt.selector.DefaultTopicSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author manish
 *
 */
public class KafkaBoltBuilder
{
    private static final Logger LOG = LoggerFactory.getLogger( KafkaBoltBuilder.class );
    private static final String BROKER_URL = "localhost:9092";
    private static final String SOCIAL_POST_TOPIC_DEV = "social-post-topic-dev";


    private KafkaBoltBuilder()
    {}


    public static KafkaBolt<String, String> buildKafkaBolt()
    {
        LOG.info( "Build KafkaBolt for pussing messages in kafka queue." );
        //Configure the Producer
        Properties props = new Properties();
        props.put( ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_URL );
        props.put( ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer" );
        props.put( ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put( ProducerConfig.CLIENT_ID_CONFIG, SOCIAL_POST_TOPIC_DEV );

        return new KafkaBolt<String, String>().withProducerProperties( props )
            .withTopicSelector( new DefaultTopicSelector( SOCIAL_POST_TOPIC_DEV ) )
            .withTupleToKafkaMapper( new FieldNameBasedTupleToKafkaMapper<>("companyId", "post") );
    }

}
