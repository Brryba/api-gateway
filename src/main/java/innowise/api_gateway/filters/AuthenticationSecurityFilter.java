package innowise.api_gateway.filters;

import innowise.api_gateway.exception.security.InvalidJwtTokenException;
import innowise.api_gateway.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class AuthenticationSecurityFilter implements WebFilter {
    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authHeader = authHeader.replace("Bearer ", "");

            try {
                jwtUtil.validateJwtToken(authHeader);
                Long userId = jwtUtil.getUserIdFromToken(authHeader);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        null);

                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));
            } catch (InvalidJwtTokenException e) {
                return chain.filter(exchange);
            }
        }

        return chain.filter(exchange);
    }
}