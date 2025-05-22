package com.postgrestoopensearch.api.config;

import java.io.IOException;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.aws.AwsSdk2Transport;
import org.opensearch.client.transport.aws.AwsSdk2TransportOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PreDestroy;

import lombok.extern.slf4j.Slf4j;

import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;

@Slf4j
@Configuration
public class OpenSearchClientConfig {

    @Value("${opensearch.host}")
    String host;

    @Value("${opensearch.scheme}")
    String scheme;

    private OpenSearchClient client;

    @Bean
    OpenSearchClient openSearchClient() {
        if (client == null) {
            Region region = Region.of("us-east-1");

            SdkHttpClient httpClient = ApacheHttpClient.builder().build();

            client = new OpenSearchClient(
                new AwsSdk2Transport(
                    httpClient, 
                    host, 
                    "es",
                    region,
                    AwsSdk2TransportOptions.builder().build()
            ));

        }
        return client;
    }

    @PreDestroy
    public void closeClient() throws IOException {
        if (client != null) {
            client._transport().close();
        }
    }
}