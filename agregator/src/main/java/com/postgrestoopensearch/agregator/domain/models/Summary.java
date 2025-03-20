package com.postgrestoopensearch.agregator.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Summary {
    int readedTopics;

    int writtenTopics;
}
