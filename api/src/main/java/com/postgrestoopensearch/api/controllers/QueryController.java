package com.postgrestoopensearch.api.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.postgrestoopensearch.api.models.ResearchBoost;
import com.postgrestoopensearch.api.models.ResearchChance;
import com.postgrestoopensearch.api.services.QueryBoostService;

@RestController
@RequestMapping("/api")
public class QueryController {

    private final QueryBoostService queryService;

    public QueryController(QueryBoostService boostService) {
        queryService = boostService;
    }
    
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/boosts/chance/{studentId}")
    public ResponseEntity<ResearchBoost> getById(@PathVariable int studentId) {
        Optional<ResearchBoost> boost = queryService.getById(studentId);
        if (!boost.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);             
        }

        return ResponseEntity.ok().body(boost.get());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/boosts/{research}")
    public ResponseEntity<List<ResearchBoost>> getByResearch(@PathVariable int research) {
        List<ResearchBoost> boosts = queryService.getByResearch(research);
        if (boosts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
        }

        return ResponseEntity.ok().body(boosts);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/boosts/research/chance/{research}")
    public ResponseEntity<ResearchChance> getChanceByResearch(@PathVariable int research) {
        ResearchChance chance = queryService.getAverageChance(research);
        if (Double.isNaN(chance.getAverageChance())) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
        }

        return ResponseEntity.ok().body(chance);
    }
}
