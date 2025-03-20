package com.postgrestoopensearch.agregator.repositories.impl;

import java.io.IOException;
import java.util.Optional;

import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.reindex.DeleteByQueryRequest;
import org.opensearch.search.SearchHit;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postgrestoopensearch.agregator.domain.models.ResearchBoost;
import com.postgrestoopensearch.agregator.repositories.ResearchBoostRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class ResearchBoostRepositoryImpl implements ResearchBoostRepository {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private ObjectMapper objectMapper;    

    public void deleteAll() {    
        try {
            DeleteByQueryRequest request = new DeleteByQueryRequest("boost");
            request.setQuery(QueryBuilders.matchAllQuery());
            
            client.deleteByQuery(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("Error deleting index ", e);
        }
    }    

        public Optional<ResearchBoost> findById(int studentId) {
        try {
            SearchRequest searchRequest = new SearchRequest("boost");
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.termQuery("student_id", studentId));
            searchRequest.source(sourceBuilder);

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            if (searchHits.length > 0) {
                ResearchBoost boost = objectMapper.readValue(searchHits[0].getSourceAsString(), ResearchBoost.class);
                return Optional.of(boost);
            }
        } catch (IOException e) {
            log.error("Error reading admission index ", e);
        }
        return Optional.empty();
    }
    
    @Override
    public void save(ResearchBoost researchBoost) {
        try {
            IndexRequest indexRequest = new IndexRequest("boost")
            .source(objectMapper.writeValueAsString(researchBoost), XContentType.JSON);
        client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("Error saving boost index ", e);
        }
        
    }
    
}
