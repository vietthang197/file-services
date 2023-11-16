package com.file.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.concurrent.TimeUnit;

@Configuration
public class MvcConfig {

    @Bean
    public MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspection) {
        return new MvcRequestMatcher.Builder(introspection);
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*:*");
        config.addAllowedOriginPattern("*");
        config.addAllowedMethod("*");
        config.addExposedHeader("*");
        config.addAllowedHeader("*");
        config.setMaxAge(1800L);
        if (config.getAllowedOrigins() != null && !config.getAllowedOrigins().isEmpty()) {
            source.registerCorsConfiguration("/**", config);
        }
        return new CorsFilter(source);
    }
}