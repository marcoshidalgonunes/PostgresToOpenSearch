package com.postgrestoopensearch.agregator.repositories.impl;

import java.util.Optional;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.postgrestoopensearch.agregator.domain.models.ResearchBoost;
import com.postgrestoopensearch.agregator.repositories.ResearchBoostRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class ResearchBoostRepositoryImpl implements ResearchBoostRepository {

    private static final String INDEX_NAME = "boost";

    @Autowired
    private OpenSearchClient client;

    @Override
    public boolean createIndex() {

        try {
            // Check if index exists using OpenSearchClient
            var existsResponse = client.indices().exists(b -> b.index(INDEX_NAME));
            if (!existsResponse.value()) {
                // Create index using OpenSearchClient
                client.indices().create(b -> b.index(INDEX_NAME));
                return true;
            }
        } catch (Exception e) {
            log.error("Error creating index ", e);
        }
        
        return false;
    }
    
    @Override
    public void deleteIndex() {
        try {
            // Use OpenSearchClient's deleteByQuery API
            client.indices().delete(b -> b.index(INDEX_NAME));
        } catch (Exception e) {
            log.error("Error deleting boost index ", e);
        }
    }
    
    @Override
    public Optional<ResearchBoost> findById(int studentId) {
        try {
            // Use OpenSearchClient's search API
            var response = client.search(s -> s
                    .index(INDEX_NAME)
                    .query(q -> q.term(t -> t.field("student_id").value(FieldValue.of(studentId))))
                    .size(1),
                ResearchBoost.class
            );
            var hits = response.hits().hits();
            if (hits != null && !hits.isEmpty()) {
                ResearchBoost boost = hits.get(0).source();
                return Optional.ofNullable(boost);
            }
        } catch (Exception e) {
            log.error("Error reading boost index ", e);
        }
        return Optional.empty();
    }
    
    @Override
    public void save(ResearchBoost researchBoost) {
        try {
            // Use OpenSearchClient's index API
            client.index(i -> i
                .index(INDEX_NAME)
                .id(String.valueOf(researchBoost.getStudentId()))
                .document(researchBoost)
            );
        } catch (Exception e) {
            log.error("Error saving boost index ", e);
        }
    }
}
