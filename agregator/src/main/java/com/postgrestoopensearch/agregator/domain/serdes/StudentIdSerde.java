package com.postgrestoopensearch.agregator.domain.serdes;

import org.springframework.kafka.support.serializer.JsonSerde;

import com.postgrestoopensearch.agregator.domain.models.StudentId;

public class StudentIdSerde extends JsonSerde<StudentId> { }
