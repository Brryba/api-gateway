package innowise.api_gateway.security;

import innowise.api_gateway.filters.AuthenticationSecurityFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    @Value("${front-end.url}")
    private String frontEndUrl;

    private final AuthenticationSecurityFilter authenticationSecurityFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(frontEndUrl));
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE", "PATCH"));
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .addFilterAt(authenticationSecurityFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(exchange -> {
                    exchange.pathMatchers(HttpMethod.POST, "/api/auth/signup").denyAll();
                    exchange.pathMatchers(HttpMethod.DELETE, "/api/auth/**").denyAll();

                    exchange.pathMatchers("/api/auth/**").permitAll();
                    exchange.pathMatchers(HttpMethod.POST, "/api/user/**").denyAll();

                    exchange.pathMatchers(HttpMethod.GET, "/api/item/**").permitAll();
                    exchange.pathMatchers("/api/register").permitAll();
                    exchange.pathMatchers("/actuator/**").permitAll();
                    exchange.anyExchange().authenticated();
                })
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((exchange, authException) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        })
                        .accessDeniedHandler((exchange, denied) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            return exchange.getResponse().setComplete();
                        })
                )
                .build();
    }
}
