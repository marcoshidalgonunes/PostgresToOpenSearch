package com.postgrestoopensearch.api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.postgrestoopensearch.api.models.ResearchBoost;

@Repository
public interface ResearchBoostRepository  {
    Optional<ResearchBoost> findByStudentId(int studentId);
    
    List<ResearchBoost> findByResearch(int research);
}
