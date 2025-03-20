package com.postgrestoopensearch.api.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.postgrestoopensearch.api.models.ResearchBoost;

@Repository
public interface ResearchBoostRepository extends ElasticsearchRepository<ResearchBoost, Integer> {
    Iterable<ResearchBoost> findByResearch(int research);
}
