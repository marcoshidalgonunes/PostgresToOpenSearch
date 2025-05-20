package com.postgrestoopensearch.api.interceptors;

import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams;
import java.net.URI;

public class AwsSdkV2SigV4Interceptor implements HttpRequestInterceptor {
    private final AwsCredentialsProvider credentialsProvider;
    private final Region region;
    private final String serviceName;

    public AwsSdkV2SigV4Interceptor(AwsCredentialsProvider credentialsProvider, Region region, String serviceName) {
        this.credentialsProvider = credentialsProvider;
        this.region = region;
        this.serviceName = serviceName;
    }

    @Override
    public void process(HttpRequest request, HttpContext context) {
        try {
            // Convert Apache HttpRequest to SdkHttpFullRequest
            URI uri = new URI(request.getRequestLine().getUri());
            SdkHttpFullRequest sdkRequest = SdkHttpFullRequest.builder()
                    .method(SdkHttpMethod.valueOf(request.getRequestLine().getMethod()))
                    .uri(uri)
                    .build();

            Aws4Signer signer = Aws4Signer.create();
            Aws4SignerParams signerParams = Aws4SignerParams.builder()
                    .signingRegion(region)
                    .signingName(serviceName)
                    .awsCredentials(credentialsProvider.resolveCredentials())
                    .build();

            SdkHttpFullRequest signed = signer.sign(sdkRequest, signerParams);

            // Copy signed headers to Apache HttpRequest
            signed.headers().forEach((k, v) -> {
                request.setHeader(k, v.get(0));
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to sign request", e);
        }
    }
}