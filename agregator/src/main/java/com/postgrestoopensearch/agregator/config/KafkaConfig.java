package com.postgrestoopensearch.agregator.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;

import com.postgrestoopensearch.agregator.domain.models.Admission;
import com.postgrestoopensearch.agregator.domain.models.Research;
import com.postgrestoopensearch.agregator.domain.models.ResearchBoost;
import com.postgrestoopensearch.agregator.domain.serdes.AdmissionSerde;
import com.postgrestoopensearch.agregator.domain.serdes.ResearchBoostSerde;
import com.postgrestoopensearch.agregator.domain.serdes.ResearchSerde;
import com.postgrestoopensearch.agregator.domain.serdes.StudentIdSerde;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value("${spring.application.name}")
    private String appplicationId;

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    KafkaStreamsConfiguration kafkaStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, appplicationId);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 2000);
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_DOC, 0);

        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    StreamsBuilderFactoryBean streamsBuilderFactoryBean(KafkaStreamsConfiguration kafkaStreamsConfig) {
        return new StreamsBuilderFactoryBean(kafkaStreamsConfig);
    }

    @Bean
    KTable<Integer, ResearchBoost> boostTable(StreamsBuilder streamsBuilder) {
        final AdmissionSerde admissionSerde = new AdmissionSerde();
        final ResearchBoostSerde researchBoostSerde = new ResearchBoostSerde();
        final ResearchSerde researchSerde = new ResearchSerde();
        final StudentIdSerde studentIdSerde = new StudentIdSerde();
        final Serde<Integer> integerSerde = Serdes.Integer();

        final KTable<Integer, Admission> admissions = streamsBuilder.stream("dbserver1.public.admission", Consumed.with(studentIdSerde, admissionSerde))
            .map((k, v) -> new KeyValue<>(v.getStudentId(), v))
            .toTable(Materialized.<Integer, Admission, KeyValueStore<Bytes, byte[]>>as("admissions-view")
                .withKeySerde(integerSerde)
                .withValueSerde(admissionSerde)
                .withCachingDisabled());

        final KTable<Integer, Research> researchs = streamsBuilder.stream("dbserver1.public.research", Consumed.with(studentIdSerde, researchSerde))
            .map((k, v) -> new KeyValue<>(v.getStudentId(), v))
            .toTable(Materialized.<Integer, Research, KeyValueStore<Bytes, byte[]>>as("researchs-view")
                .withKeySerde(integerSerde)
                .withValueSerde(researchSerde)
                .withCachingDisabled());

        return admissions.leftJoin(researchs, Admission::getStudentId, 
            (admission, research) -> new ResearchBoost(admission.getStudentId(), research.getResearch(), admission.getAdmitChance()),
            Materialized.<Integer, ResearchBoost, KeyValueStore<Bytes, byte[]>>as("researchs-boost-view")
                .withKeySerde(integerSerde)
                .withValueSerde(researchBoostSerde)
                .withCachingDisabled());
    }    
}
