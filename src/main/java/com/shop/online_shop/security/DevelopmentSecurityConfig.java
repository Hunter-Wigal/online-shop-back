package com.shop.online_shop.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

// The following configuration is not actually used anymore and is just kept as a reference

//@Configuration
//@Profile("development")
////@EnableWebSecurity
////public class DevelopmentSecurityConfig {
//
////    protected void configure(HttpSecurity http) throws Exception {
////        System.out.println("Configuring security");
////        http.authorizeRequests()
////                .anyRequest().permitAll();
////    }
//public class DevelopmentSecurityConfig {
//
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring()
//                .requestMatchers(new AntPathRequestMatcher("/**"));
//    }
//}
//}