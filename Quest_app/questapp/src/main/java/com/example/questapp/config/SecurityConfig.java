package com.example.questapp.config;

import com.example.questapp.security.JwtAuthenticationEntryPoint;
import com.example.questapp.security.JwtAuthenticationFilter;
import com.example.questapp.services.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationEntryPoint handler;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtAuthenticationEntryPoint handler) {
        this.userDetailsService = userDetailsService;
        this.handler = handler;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // 1. Adım: CORS yapılandırmasını doğrudan SecurityFilterChain'e entegre et
                .cors(cors -> {
                    CorsConfigurationSource source = request -> {
                        CorsConfiguration config = new CorsConfiguration();
                        // Frontend uygulamanızın adresini buraya yazın
                        config.setAllowedOrigins(List.of("http://localhost:3000"));
                        // İzin verilen HTTP metodları
                        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
                        // İzin verilen tüm başlıklar
                        config.setAllowedHeaders(List.of("*"));
                        // Kimlik bilgilerinin (cookie vb.) gönderilmesine izin ver
                        config.setAllowCredentials(true);
                        return config;
                    };
                    cors.configurationSource(source);
                })
                // 2. Adım: Diğer güvenlik ayarları
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(handler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 3. Adım: Hangi yolların public, hangilerinin korumalı olacağını belirt
                        .requestMatchers("/auth/**").permitAll() // Giriş/kayıt yolları herkese açık
                        .requestMatchers(HttpMethod.GET, "/posts").permitAll() // Örnek: Postları listelemek herkese açık
                        .requestMatchers(HttpMethod.GET, "/comments").permitAll() // Örnek: Yorumları listelemek herkese açık
                        .anyRequest().authenticated() // Yukarıdakiler dışındaki tüm istekler kimlik doğrulaması gerektirir
                );

        httpSecurity.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}