
package com.example.hospital.config;

import com.example.hospital.security.JwtAuthenticationFilter;
import com.example.hospital.security.JwtAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security配置类
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, 
                         JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login", "/auth/register").permitAll()
                .requestMatchers("/drugs/**").hasAnyAuthority("ADMIN", "PHARMACIST", "PURCHASER", "STOCK_MANAGER", "SPECIAL_PHARMACIST", "PHARMACY_DIRECTOR")
                .requestMatchers("/purchase/**").hasAnyAuthority("ADMIN", "PURCHASER", "PHARMACY_DIRECTOR")
                .requestMatchers("/inventory/**").hasAnyAuthority("ADMIN", "PHARMACIST", "STOCK_MANAGER")
                .requestMatchers("/pharmacy/**").hasAnyAuthority("ADMIN", "PHARMACIST")
                .requestMatchers("/clinical/**").hasAnyAuthority("ADMIN", "DOCTOR", "PHARMACIST")
                .requestMatchers("/special/**").hasAnyAuthority("ADMIN", "SPECIAL_PHARMACIST", "PHARMACY_DIRECTOR")
                .requestMatchers("/system/**").hasAuthority("ADMIN")
                .requestMatchers("/reports/**").hasAnyAuthority("ADMIN", "PHARMACIST", "PURCHASER", "STOCK_MANAGER", "SPECIAL_PHARMACIST", "PHARMACY_DIRECTOR")
                .requestMatchers("/doctor/**").hasAuthority("DOCTOR")
                .requestMatchers("/dashboard/admin").hasAuthority("ADMIN")
                .requestMatchers("/dashboard/doctor").hasAuthority("DOCTOR")
                .requestMatchers("/dashboard/pharmacist").hasAuthority("PHARMACIST")
                .requestMatchers("/dashboard/purchaser").hasAuthority("PURCHASER")
                .requestMatchers("/dashboard/stock-manager").hasAuthority("STOCK_MANAGER")
                .requestMatchers("/dashboard/special-pharmacist").hasAuthority("SPECIAL_PHARMACIST")
                .requestMatchers("/dashboard/pharmacy-director").hasAuthority("PHARMACY_DIRECTOR")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
