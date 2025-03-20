package com.postgrestoopensearch.agregator.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentId {
    @JsonProperty("student_id")
    int studentId;   
}
