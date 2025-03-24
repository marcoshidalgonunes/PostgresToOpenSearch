package com.postgrestoopensearch.api.repositories.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.SearchHit;
import org.opensearch.search.builder.SearchSourceBuilder;
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
    protected RestHighLevelClient client;

    @Autowired
    protected ObjectMapper objectMapper;
    
    @Override
    public Optional<ResearchBoost> findByStudentId(int studentId) {
        try {
            SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.termQuery("studentId", studentId));
            searchRequest.source(sourceBuilder);

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            if (searchHits.length > 0) {
                ResearchBoost boost = objectMapper.readValue(searchHits[0].getSourceAsString(), ResearchBoost.class);
                return Optional.of(boost);
            }
        } catch (IOException e) {
            log.error("Error reading boost index ", e);
        }
        return Optional.empty();
    }

    @Override
    public List<ResearchBoost> findByResearch(int research) {
        try {
            SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.matchQuery("research", research));
            sourceBuilder.size(1000); // Set the size to a large number to fetch all items according to the criteria
            searchRequest.source(sourceBuilder);

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            List<ResearchBoost> researchs = new ArrayList<>();
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                researchs.add(objectMapper.readValue(hit.getSourceAsString(), ResearchBoost.class));
            }

            return researchs;
        } catch (IOException e) {
            log.error("Error reading boost index ", e);
            return Collections.emptyList();
        }
    }
}
