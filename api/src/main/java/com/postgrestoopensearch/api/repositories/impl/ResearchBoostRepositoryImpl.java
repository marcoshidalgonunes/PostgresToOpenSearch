package com.postgrestoopensearch.api.repositories.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postgrestoopensearch.api.models.ResearchBoost;
import com.postgrestoopensearch.api.repositories.ResearchBoostRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class ResearchBoostRepositoryImpl implements ResearchBoostRepository {

    private static final String INDEX_NAME = "boost";

    @Autowired
    protected OpenSearchClient client;

    @Autowired
    protected ObjectMapper objectMapper;
    
    @Override
    public Optional<ResearchBoost> findByStudentId(int studentId) {
        try {
            Query query = Query.of(q -> q.term(t -> t.field("studentId").value(FieldValue.of(String.valueOf(studentId)))));

            var searchRequest = new SearchRequest.Builder()
                    .index(INDEX_NAME)
                    .query(query)
                    .size(1)
                    .build();
            var response = client.search(searchRequest, ResearchBoost.class);
            if (response.hits().hits() != null && !response.hits().hits().isEmpty()) {
                return Optional.ofNullable(response.hits().hits().get(0).source());
            }
        } catch (Exception e) {
            log.error("Error reading boost index ", e);
        }
        return Optional.empty();
    }
    
    @Override
    public List<ResearchBoost> findByResearch(int research) {
        try {
            Query query = Query.of(q -> q.term(t -> t.field("research").value(FieldValue.of(String.valueOf(research)))));

            var searchRequest = new SearchRequest.Builder()
                    .index(INDEX_NAME)
                    .query(query)
                    .size(1000)
                    .build();
            var response = client.search(searchRequest, ResearchBoost.class);

            List<ResearchBoost> researchs = new ArrayList<>();
            if (response.hits().hits() != null) {
                response.hits().hits().forEach(hit -> {
                    ResearchBoost boost = hit.source();
                    if (boost != null) researchs.add(boost);
                });
            }
            return researchs;
        } catch (Exception e) {
            log.error("Error reading boost index ", e);
            return Collections.emptyList();
        }
    }    
}
