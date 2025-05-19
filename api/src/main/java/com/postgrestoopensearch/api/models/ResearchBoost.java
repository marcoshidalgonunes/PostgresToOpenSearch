package com.postgrestoopensearch.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResearchBoost {
    int studentId;

    int research;

    double admitChance;
}
