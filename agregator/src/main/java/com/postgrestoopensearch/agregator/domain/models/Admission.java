package com.postgrestoopensearch.agregator.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admission {
    @JsonProperty("student_id")
    int studentId;
    
    @JsonProperty("gre")
    int gre;

    @JsonProperty("toefl")
    int toefl;

    @JsonProperty("cpga")
    double cpga;

    @JsonProperty("admit_chance")
    double admitChance;
}
