package com.postgrestoopensearch.api.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.postgrestoopensearch.api.models.Agregation;
import com.postgrestoopensearch.api.models.ResearchBoost;
import com.postgrestoopensearch.api.models.ResearchChance;
import com.postgrestoopensearch.api.repositories.ResearchBoostRepository;

@Service
public class QueryBoostService {
    
    @Autowired 
    private ResearchBoostRepository researchBoostRepository;

    public List<ResearchBoost> getByResearch(int research) {
        List<ResearchBoost> boosts = new ArrayList<>();
        researchBoostRepository.findByResearch(research)
            .forEach(boosts::add);
        
        return boosts;
    }

    public ResearchChance getAverageChance(int research) {
        Agregation agregation = new Agregation(0.0, 0);
        researchBoostRepository.findByResearch(research)
            .forEach(boost -> {
                agregation.setSumChance(agregation.getSumChance() + boost.getAdmitChance());
                agregation.setCountChance(agregation.getCountChance() + 1);
            });
        
        return new ResearchChance(research, agregation.getSumChance() / agregation.getCountChance());
    }

    public Optional<ResearchBoost> getById(int studentId) {
        return researchBoostRepository.findById(studentId);
    }
}
