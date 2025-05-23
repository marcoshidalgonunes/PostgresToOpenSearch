package com.postgrestoopensearch.agregator.repositories;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.postgrestoopensearch.agregator.domain.models.ResearchBoost;

@Repository
public interface ResearchBoostRepository  {
    boolean createIndex();
    
    void deleteIndex();

    Optional<ResearchBoost> findById(int studentId);
    
    void save(ResearchBoost researchBoost);
}
