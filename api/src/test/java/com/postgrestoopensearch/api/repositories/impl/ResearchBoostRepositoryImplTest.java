package com.postgrestoopensearch.api.repositories.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.opensearch.core.search.HitsMetadata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postgrestoopensearch.api.models.ResearchBoost;

class ResearchBoostRepositoryImplTest {

    @Mock
    private OpenSearchClient client;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ResearchBoostRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findByStudentId_returnsResearchBoost_whenHitFound() throws Exception {
        int studentId = 123;
        ResearchBoost boost = new ResearchBoost();
        Hit<ResearchBoost> hit = new Hit.Builder<ResearchBoost>().source(boost).build();
        SearchResponse<ResearchBoost> response = mock(SearchResponse.class);

        HitsMetadata<ResearchBoost> hitsMetadata = mock(HitsMetadata.class);
        when(hitsMetadata.hits()).thenReturn(List.of(hit));
        when(response.hits()).thenReturn(hitsMetadata);
        when(client.search(any(SearchRequest.class), eq(ResearchBoost.class))).thenReturn(response);

        Optional<ResearchBoost> result = repository.findByStudentId(studentId);

        assertTrue(result.isPresent());
        assertEquals(boost, result.get());
    }

    @Test
    void findByStudentId_returnsEmpty_whenNoHits() throws Exception {
        int studentId = 123;
        SearchResponse<ResearchBoost> response = mock(SearchResponse.class);

        HitsMetadata<ResearchBoost> hitsMetadata = mock(HitsMetadata.class);
        when(hitsMetadata.hits()).thenReturn(Collections.emptyList());
        when(response.hits()).thenReturn(hitsMetadata);
        when(client.search(any(SearchRequest.class), eq(ResearchBoost.class))).thenReturn(response);

        Optional<ResearchBoost> result = repository.findByStudentId(studentId);

        assertFalse(result.isPresent());
    }

    @Test
    void findByStudentId_returnsEmpty_whenException() throws Exception {
        int studentId = 123;
        when(client.search(any(SearchRequest.class), eq(ResearchBoost.class))).thenThrow(new RuntimeException("error"));

        Optional<ResearchBoost> result = repository.findByStudentId(studentId);

        assertFalse(result.isPresent());
    }

    @Test
    void findByResearch_returnsList_whenHitsFound() throws Exception {
        int research = 42;
        ResearchBoost boost1 = new ResearchBoost();
        ResearchBoost boost2 = new ResearchBoost();
        Hit<ResearchBoost> hit1 = new Hit.Builder<ResearchBoost>().source(boost1).build();
        Hit<ResearchBoost> hit2 = new Hit.Builder<ResearchBoost>().source(boost2).build();
        SearchResponse<ResearchBoost> response = mock(SearchResponse.class);


        HitsMetadata<ResearchBoost> hitsMetadata = mock(HitsMetadata.class);
        when(hitsMetadata.hits()).thenReturn(List.of(hit1, hit2));
        when(response.hits()).thenReturn(hitsMetadata);
        when(client.search(any(SearchRequest.class), eq(ResearchBoost.class))).thenReturn(response);

        List<ResearchBoost> result = repository.findByResearch(research);

        assertEquals(2, result.size());
        assertTrue(result.contains(boost1));
        assertTrue(result.contains(boost2));
    }

    @Test
    void findByResearch_returnsEmptyList_whenNoHits() throws Exception {
        int research = 42;
        SearchResponse<ResearchBoost> response = mock(SearchResponse.class);

        HitsMetadata<ResearchBoost> hitsMetadata = mock(HitsMetadata.class);
        when(hitsMetadata.hits()).thenReturn(Collections.emptyList());
        when(response.hits()).thenReturn(hitsMetadata);
        when(client.search(any(SearchRequest.class), eq(ResearchBoost.class))).thenReturn(response);

        List<ResearchBoost> result = repository.findByResearch(research);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByResearch_returnsEmptyList_whenException() throws Exception {
        int research = 42;
        when(client.search(any(SearchRequest.class), eq(ResearchBoost.class))).thenThrow(new RuntimeException("error"));

        List<ResearchBoost> result = repository.findByResearch(research);

        assertTrue(result.isEmpty());
    }
}