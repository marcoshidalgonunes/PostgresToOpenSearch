package com.postgrestoopensearch.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResearchChance {
    int research;

    double averageChance;
}
