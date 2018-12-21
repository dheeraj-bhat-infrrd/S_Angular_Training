package com.realtech.socialsurvey.stream.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;


@Configuration
public class KafkaProducerConfig
{
    @Value ( "${kafka.brokerAddress}")
    private String brokerAddress;

    @Value ( "${kafka.topic.emailMessageTopic}")
    private String emailMessageTopic;
    
    @Value ( "${kafka.topic.emailEventsTopic}")
    private String emailEventsTopic;

    @Value ( "${kafka.topic.reportTopic}" )
    private String reportTopic;
    
    @Value ( "${kafka.topic.socialMonitorTopic}" )
    private String socialMonitorTopic;
    
	@Value("${kafka.topic.batchTopic}" )
    private String batchTopic;
	
	@Value ( "${kafka.topic.userEventTopic}")
    private String userEventTopic;

    @Value ( "${kafka.topic.transactionIngestionTopic}" )
    private String transactionIngestionTopic;
    
    @Value ( "${kafka.topic.surveyProcessorTopic}" )
    private String surveyProcessorTopic;
    
    @Bean
    public ProducerFactory<String, String> producerFactory()
    {
        Map<String, Object> props = new HashMap<>();
        props.put( ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerAddress );
        props.put( ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class );
        props.put( ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class );
        return new DefaultKafkaProducerFactory<>( props );
    }
    /**
     * Test template. Not to be used in production
     * @return
     */
    @Bean ( name = "genericMessageTemplate")
    public KafkaTemplate<String, String> kafkaTemplate()
    {
        return new KafkaTemplate<>( producerFactory() );
    }

    
    /**
     * Kafka template for sending emails
     * @return
     */
    @Bean(name = "emailMessageTemplate")
    public KafkaTemplate<String, String> kafkaEmailMessageTemplate(){
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>( producerFactory() );
        kafkaTemplate.setMessageConverter( new StringJsonMessageConverter() );
        kafkaTemplate.setDefaultTopic( emailMessageTopic );
        return kafkaTemplate;
    }
    
    /**
     * Kafka template for capturing email events
     * @return
     */
    @Bean(name = "emailEventsTemplate")
    public KafkaTemplate<String, String> kafkaEmailEventsTemplate(){
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>( producerFactory() );
        kafkaTemplate.setMessageConverter( new StringJsonMessageConverter() );
        kafkaTemplate.setDefaultTopic( emailEventsTopic );
        return kafkaTemplate;
    }

    /**
     * Kafka template for capturing report generation information
     */
    @Bean(name = "reportTemplate")
    public KafkaTemplate<String, String> kafkaReportGenerationTemplate(){
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>( producerFactory() );
        kafkaTemplate.setMessageConverter( new StringJsonMessageConverter() );
        kafkaTemplate.setDefaultTopic(reportTopic);
        return kafkaTemplate;
    }
    
    /**
     * Kafka template for capturing social monitor information
     */
    @Bean(name = "socialMonitorTemplate")
    public KafkaTemplate<String, String> kafkaSocialMonitorTemplate(){
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>( producerFactory() );
        kafkaTemplate.setMessageConverter( new StringJsonMessageConverter() );
        kafkaTemplate.setDefaultTopic(socialMonitorTopic);
        return kafkaTemplate;
    }
    
    @Bean(name = "batchTemplate")
    public KafkaTemplate<String, String> kafkaBatchProcessingTemplate(){
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>( producerFactory() );
        kafkaTemplate.setMessageConverter( new StringJsonMessageConverter() );
        kafkaTemplate.setDefaultTopic(batchTopic);
        return kafkaTemplate;
    }
    
    
    /**
     * Kafka template for capturing user event originated from the browser
     * @return
     */
    @Bean(name = "userEventTemplate")
    public KafkaTemplate<String, String> kafkaUserEventTemplate(){
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>( producerFactory() );
        kafkaTemplate.setMessageConverter( new StringJsonMessageConverter() );
        kafkaTemplate.setDefaultTopic( userEventTopic );
        return kafkaTemplate;
    }
    
    
    /**
     * Kafka template for uploading surveys from s3
     */
    @Bean(name = "transactionIngestionTemplate")
    public KafkaTemplate<String, String> kafkaTransactionIngestionTemplate(){
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>( producerFactory() );
        kafkaTemplate.setMessageConverter( new StringJsonMessageConverter() );
        kafkaTemplate.setDefaultTopic(transactionIngestionTopic);
        return kafkaTemplate;
    }
    
    @Bean(name = "kafkaSurveyProcessorTemplate")
    public KafkaTemplate<String, String> kafkaSurveyProcessorTemplate(){
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>( producerFactory() );
        kafkaTemplate.setMessageConverter( new StringJsonMessageConverter() );
        kafkaTemplate.setDefaultTopic(surveyProcessorTopic);
        return kafkaTemplate;
    }
}
