package com.shop.online_shop.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthEntryPoint authEntryPoint;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final AuthenticationProvider authenticationProvider;

    @Value("${ALLOWED_ORIGINS}")
    String allowedOrigins;

    public SecurityConfig(JwtAuthEntryPoint authEntryPoint,
                          JwtAuthenticationFilter jwtAuthenticationFilter, AuthenticationProvider authenticationProvider) {
        this.authEntryPoint = authEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationProvider = authenticationProvider;
    }

    // Configures security options such as specific route permissions, cors, and csrf
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String apiV = "/api/v1/";
        http
                // Determine who is authorized at which endpoints
                .authorizeHttpRequests(authorize -> authorize
                        //Controllers
                        //Auth anybody can log in, register, logout etc.
                        .requestMatchers(HttpMethod.POST, apiV + "auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, apiV + "auth/**").permitAll()
//                        .requestMatchers(HttpMethod.POST, apiV + "auth/make_me_admin").authenticated()
                        //Health not implemented yet
                        .requestMatchers(apiV + "custom/**").permitAll()
                        //Product
                        .requestMatchers(HttpMethod.GET, apiV + "products/**").permitAll()
                        // only admin can update products
                        .requestMatchers(HttpMethod.POST, apiV + "products/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, apiV + "products/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, apiV + "products/**").hasAuthority("ADMIN")
                        //Transaction
                                .requestMatchers(HttpMethod.POST, apiV + "orders/**").permitAll()
                        /*.requestMatchers(HttpMethod.GET, apiV + "orders/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, apiV + "orders/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, apiV + "orders/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, apiV + "orders/**").hasAuthority("ADMIN")*/
                        //User
                        .requestMatchers(HttpMethod.GET, apiV + "user/**").authenticated()
                        .requestMatchers(HttpMethod.POST, apiV + "user/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, apiV + "user/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, apiV + "user/**").authenticated()
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().permitAll()
                )
                .httpBasic(withDefaults())
                .cors(withDefaults()) // Use corsConfigurationSource bean
                // Handle exceptions elsewhere
                .exceptionHandling((exception) -> exception.authenticationEntryPoint(authEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider);

        http.addFilterBefore(this.jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        XorCsrfTokenRequestAttributeHandler delegate = new XorCsrfTokenRequestAttributeHandler();
        // set the name of the attribute the CsrfToken will be populated on
        delegate.setCsrfRequestAttributeName("_csrf");
        // Use only the handle() method of XorCsrfTokenRequestAttributeHandler and the
        // default implementation of resolveCsrfTokenValue() from CsrfTokenRequestHandler
        CsrfTokenRequestHandler requestHandler = delegate::handle;
        http.csrf(csrf -> csrf.csrfTokenRepository(tokenRepository).csrfTokenRequestHandler(requestHandler)
                .ignoringRequestMatchers(apiV +"auth/csrf")
                .ignoringRequestMatchers(apiV +"orders/**"));
//        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        List<String> allowed = List.of(allowedOrigins.split(","));
        // Allow requests from specified origins
        configuration.setAllowedOriginPatterns(allowed);
        // only allow these methods and headers
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Method", "Accept", "Access-Control-Allow-Origin", "X-XSRF-TOKEN"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}


