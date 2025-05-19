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

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;

import com.postgrestoopensearch.interceptors.AwsSdkV2SigV4Interceptor;

@Slf4j
@Configuration
public class OpenSearchClientConfig {

    @Value("${opensearch.host}")
    String host;

    @Value("${opensearch.scheme}")
    String scheme;

    @Value("${aws.accessKeyId}")
    String accessKeyId;

    @Value("${aws.secretAccessKey}")
    String secretAccessKey;

    private RestClient restClient;

    @Bean
    OpenSearchClient openSearchClient() {
        AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKeyId, secretAccessKey)
        );
        Region region = Region.of(System.getenv().getOrDefault("AWS_REGION", "us-east-1"));

        AwsSdkV2SigV4Interceptor interceptor = new AwsSdkV2SigV4Interceptor(credentialsProvider, region, "es");

        restClient = RestClient.builder(HttpHost.create(scheme + "://" + host))
            .setHttpClientConfigCallback(httpClientConfigBuilder ->
                httpClientConfigBuilder.addInterceptorLast(interceptor)
            )
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