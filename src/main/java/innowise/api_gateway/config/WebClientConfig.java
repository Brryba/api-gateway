package innowise.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${services.urls.user-service}")
    private String userServiceUrl;

    @Value("${services.urls.auth-service}")
    private String authServiceUrl;

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    @Bean
    public WebClient userServiceClient() {
        return webClientBuilder()
                .baseUrl(userServiceUrl)
                .build();
    }

    @Bean
    public WebClient authServiceClient() {
        return webClientBuilder()
                .baseUrl(authServiceUrl)
                .build();
    }
}
