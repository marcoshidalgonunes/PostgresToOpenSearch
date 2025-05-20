package com.postgrestoopensearch.api.config;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.opensearch.client.RestClient;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
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

    private RestClient restClient;

    @Bean
    OpenSearchClient openSearchClient() {

        restClient = RestClient.builder(HttpHost.create(scheme + "://" + host))
            .build();

        RestClientTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        return new OpenSearchClient(transport);    
    }

    @PreDestroy
    public void closeClient() throws IOException {
        if (restClient != null) {
            restClient.close();
        }
    }
}