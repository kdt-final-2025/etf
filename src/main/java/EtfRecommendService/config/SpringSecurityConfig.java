package EtfRecommendService.config;

import EtfRecommendService.loginUtils.Sha256HexPasswordEncoder;
import EtfRecommendService.security.Role;
import EtfRecommendService.security.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.PrintWriter;
import java.util.List;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SpringSecurityConfig {

    private CorsConfigurationSource corsConfigurationSource;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(AbstractHttpConfigurer::disable)
                .cors(cors-> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests( authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/v1/admin/**").hasRole(Role.ADMIN.name())
                                .requestMatchers("/api/v1/comments/**").authenticated()
                                .requestMatchers("/api/v1/etfs/**").authenticated()
                                .requestMatchers("/api/v1/news/**").permitAll()
                                .requestMatchers("/api/v1/notifications/**").permitAll()
                                .requestMatchers("/api/v1/replies/**").authenticated()
                                .requestMatchers("/api/v1/reports/**").authenticated()
                                .requestMatchers("/api/v1/users/**").authenticated()
                                .requestMatchers("/**").permitAll()
                )
                .exceptionHandling((exceptionConfig) ->
                        exceptionConfig.authenticationEntryPoint(unauthorizedEntryPoint).accessDeniedHandler(accessDeniedHandler)
                );

        return http.build();
    }

    private final AuthenticationEntryPoint unauthorizedEntryPoint =
            (request, response, authException) -> {
                ErrorResponse fail = new ErrorResponse(HttpStatus.UNAUTHORIZED, "Spring security unauthorized...");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                String json = new ObjectMapper().writeValueAsString(fail);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                PrintWriter writer = response.getWriter();
                writer.write(json);
                writer.flush();
            };

    private final AccessDeniedHandler accessDeniedHandler =
            (request, response, accessDeniedException) -> {
                ErrorResponse fail = new ErrorResponse(HttpStatus.FORBIDDEN, "Spring security forbidden...");
                response.setStatus(HttpStatus.FORBIDDEN.value());
                String json = new ObjectMapper().writeValueAsString(fail);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                PrintWriter writer = response.getWriter();
                writer.write(json);
                writer.flush();
            };
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Sha256HexPasswordEncoder();
    }
}
