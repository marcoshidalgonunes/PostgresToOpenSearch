package com.postgrestoopensearch.agregator.services;

import org.apache.kafka.streams.state.KeyValueIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.postgrestoopensearch.agregator.components.BoostKafkaComponent;
import com.postgrestoopensearch.agregator.domain.models.ResearchBoost;
import com.postgrestoopensearch.agregator.domain.models.Summary;
import com.postgrestoopensearch.agregator.repositories.ResearchBoostRepository;

@Service
public class AgregatorService {

    @Autowired
    private BoostKafkaComponent boostKafkaComponent;

    @Autowired 
    private ResearchBoostRepository researchBoostRepository;

    public Summary create() {
        Summary summary = new Summary(0, 0);

        if (researchBoostRepository.createIndex()) {
            boostKafkaComponent.start();

            try (KeyValueIterator<Integer, ResearchBoost> topics = boostKafkaComponent.getTopics()) {
                topics.forEachRemaining(entry -> {
                    summary.setReadedTopics(summary.getReadedTopics() + 1);
                    if (!researchBoostRepository.findById(entry.key).isPresent()) {
                        researchBoostRepository.save(entry.value);
                        summary.setWrittenTopics(summary.getWrittenTopics() + 1);
                    }
                });
            }
    
            boostKafkaComponent.stop();   
        }

        return summary;
    }

    public void delete() {
        researchBoostRepository.deleteAll();
    }
}

