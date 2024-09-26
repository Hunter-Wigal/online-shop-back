package com.shop.online_shop.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@Profile("development")
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthEntryPoint authEntryPoint;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final AuthenticationProvider authenticationProvider;

    @Autowired
    public SecurityConfig(JwtAuthEntryPoint authEntryPoint,
                          JwtAuthenticationFilter jwtAuthenticationFilter, AuthenticationProvider authenticationProvider) {
        this.authEntryPoint = authEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationProvider = authenticationProvider;
    }

    // Configures security options such as specific route permissions, cors, and csrf
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)

                // Determine who is authorized at which endpoints
                .authorizeHttpRequests(authorize -> authorize
                                // Only allow posting at this route. Used for logging in and registering
                                .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                                // Allow requesting user information if logged in
                                .requestMatchers(HttpMethod.GET, "/api/v1/user/**").authenticated()
                                // Allow anyone to view products
                                .requestMatchers(HttpMethod.GET, "api/v1/products/**").permitAll()
                                // Only allow admin to post to products api
                                .requestMatchers(HttpMethod.POST, "api/v1/products/**").permitAll() //.hasRole("ADMIN")
                                // Change to only admin
                                .requestMatchers(HttpMethod.PATCH, "api/v1/products/**").permitAll()

                                .requestMatchers(HttpMethod.DELETE, "api/v1/products").permitAll()
                                // Once tested, change to only allow admin to get order
                                .requestMatchers(HttpMethod.GET, "api/v1/orders/**").permitAll()

//                        .requestMatchers(HttpMethod.POST, "/api/v1/orders/**").authenticated()
                                // Temporary let every other request work
                                .anyRequest().permitAll()
                )
                .httpBasic(withDefaults())
                .csrf((csrf) -> csrf
                                // TODO figure out why this isn't working
                                // TODO add proper csrf protection
//                        .ignoringRequestMatchers("/api/v1/auth/**")

                                .disable()
//                        .ignoringRequestMatchers("http://localhost:5173/**")
                )
                // Cors defined below
                .cors((cors) -> {
                })
                // Handle exceptions elsewhere
                .exceptionHandling((exception) -> exception.authenticationEntryPoint(authEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider);

        http.addFilterBefore(this.jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow requests from local react app
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        // only allow these methods and headers
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Method", "Accept"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}


