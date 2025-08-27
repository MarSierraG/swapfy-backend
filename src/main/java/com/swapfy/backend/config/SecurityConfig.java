package com.swapfy.backend.config;

import com.swapfy.backend.security.JwtFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoderApp() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   @Qualifier("apiCorsConfigurationSource") CorsConfigurationSource corsSource) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Usa explícitamente TU CorsConfigurationSource para evitar ambigüedad
                .cors(cors -> cors.configurationSource(corsSource))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // preflight
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/check-email").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/transactions/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/transactions/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/transactions/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/transactions/user/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/credits/extract").authenticated()

                        .requestMatchers("/api/transactions/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/tags").authenticated()
                        .requestMatchers("/api/tags/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/users/**").authenticated()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Da un NOMBRE a tu bean de CORS para poder "qualificarlo"
    @Bean(name = "apiCorsConfigurationSource")
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOriginPatterns(List.of(
                "http://localhost:4200",
                "https://swapfy-frontend.vercel.app",
                "https://*.vercel.app"
        ));
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));
        cors.setExposedHeaders(List.of("Authorization")); // solo si usas el token en header
        cors.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }

    // Inyecta explícitamente TU bean (evita chocar con mvcHandlerMappingIntrospector)
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration(
            @Qualifier("apiCorsConfigurationSource") CorsConfigurationSource source) {
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
