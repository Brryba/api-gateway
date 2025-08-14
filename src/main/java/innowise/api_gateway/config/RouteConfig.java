package innowise.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {
    @Value("${services.url.user_service}")
    private String userServiceUrl;

    @Value("${services.url.auth_service}")
    private String authServiceUrl;

    @Value("${services.url.order_service}")
    private String orderServiceUrl;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/api/user/**",
                                "/api/card/**")
                        .uri("lb://user-service"))
                .route(p -> p
                        .path("/api/auth/**")
                        .uri("lb://auth-service"))
                .route(p -> p
                        .path("/api/order/**",
                                "/api/item/**")
                        .uri("lb://order-service"))
                .build();
    }
}
