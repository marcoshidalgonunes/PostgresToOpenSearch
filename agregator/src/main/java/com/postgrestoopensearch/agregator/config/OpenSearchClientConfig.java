package com.postgrestoopensearch.agregator.config;

import java.io.IOException;

import org.apache.http.HttpHost;

import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PreDestroy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class OpenSearchClientConfig {

    @Value("${opensearch.host}")
    String host;

    @Value("${opensearch.scheme}")
    String scheme;

    @Bean
    RestHighLevelClient client() {
        return new RestHighLevelClient(
            RestClient.builder(
                HttpHost.create(scheme + "://" + host)
            )
        );
    }

    @PreDestroy
    public void closeClient() throws IOException {
        client().close();
    }
}