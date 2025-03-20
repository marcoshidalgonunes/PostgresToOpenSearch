package com.postgrestoopensearch.agregator.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.postgrestoopensearch.agregator.domain.models.Summary;
import com.postgrestoopensearch.agregator.services.AgregatorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping("/agregator")
public class AgregatorController {

    @Autowired
    private AgregatorService service;
    
    @PostMapping
    public Summary create() {
        return service.create();
    }

    @DeleteMapping()
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete() {
        service.delete();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }    
}
