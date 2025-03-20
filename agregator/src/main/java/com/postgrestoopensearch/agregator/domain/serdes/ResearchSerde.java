package com.postgrestoopensearch.agregator.domain.serdes;

import org.springframework.kafka.support.serializer.JsonSerde;

import com.postgrestoopensearch.agregator.domain.models.Research;

public class ResearchSerde extends JsonSerde<Research> { }

