package com.postgrestoopensearch.agregator.config;

import java.io.IOException;

import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.core5.http.HttpHost;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder;

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

    @Value("${opensearch.port}")
    int port;

    @Value("${opensearch.scheme}")
    String scheme;

    @Bean
    OpenSearchClient client() {
        HttpHost httpHost = new HttpHost(scheme, host, port);

        ApacheHttpClient5TransportBuilder builder = ApacheHttpClient5TransportBuilder.builder(httpHost)
            .setHttpClientConfigCallback(
                httpClientBuilder -> {
                    final PoolingAsyncClientConnectionManager connectionManager = PoolingAsyncClientConnectionManagerBuilder
                        .create()
                        .build();

                    return httpClientBuilder
                        .setConnectionManager(connectionManager);
                }
            );

        OpenSearchTransport transport = builder.build();
        return new OpenSearchClient(transport);
    }

    @PreDestroy
    public void closeClient() throws IOException {
        client()._transport().close();
    }
}