package innowise.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {
    @Value("${services.urls.user-service}")
    private String userServiceUrl;

    @Value("${services.urls.auth-service}")
    private String authServiceUrl;

    @Value("${services.urls.order-service}")
    private String orderServiceUrl;

    @Value("${services.urls.payment-service}")
    private String paymentServiceUrl;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/api/user/**",
                                "/api/card/**")
                        .uri(userServiceUrl))
                .route(p -> p
                        .path("/api/auth/**")
                        .uri(authServiceUrl))
                .route(p -> p
                        .path("/api/order/**",
                                "/api/item/**")
                        .uri(orderServiceUrl))
                .route(p -> p
                        .path("/api/payment/**")
                        .uri(paymentServiceUrl))
                .build();
    }
}
