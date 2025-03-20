package com.postgrestoopensearch.agregator.domain.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document(indexName = "boost")
@AllArgsConstructor
@NoArgsConstructor
public class ResearchBoost {
    @Id
    int studentId;

    int research;

    double admitChance;
}
