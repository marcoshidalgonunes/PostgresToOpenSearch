package com.postgrestoopensearch.agregator.components;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;

import com.postgrestoopensearch.agregator.domain.models.ResearchBoost;

@Component
public class BoostKafkaComponent implements KafkaComponent<Integer, ResearchBoost> {

    @Autowired
    private StreamsBuilderFactoryBean streamsBuilderFactoryBean;

    @Override
    public KeyValueIterator<Integer, ResearchBoost> getTopics() {
        KafkaStreams kafkaStreams = streamsBuilderFactoryBean.getKafkaStreams();
        ReadOnlyKeyValueStore<Integer, ResearchBoost> keyValueStore = kafkaStreams.store(
            StoreQueryParameters.fromNameAndType("researchs-boost-view", QueryableStoreTypes.keyValueStore())
        );
        return keyValueStore.all();
    }

    @Override
    public void start() {
        streamsBuilderFactoryBean.start();
    }

    @Override
    public void stop() {
        streamsBuilderFactoryBean.stop();
    }
    
}
