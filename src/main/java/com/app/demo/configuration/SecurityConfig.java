package com.app.demo.configuration;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .contentTypeOptions(Customizer.withDefaults())
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET,"/api/v1/candidates/**").hasAnyRole("ADMIN","SPECTATOR")
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/candidates/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/v1/candidates/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/v1/candidates/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/shutdown").hasRole("ADMIN")
                        .requestMatchers("/images/**", "/css/**", "/js/**", "/favicon.ico").permitAll()
                        .anyRequest().authenticated()
                ).formLogin(form ->form
                        .defaultSuccessUrl("/app",true)
                        .loginPage("/login")
                        .permitAll()

                )


                .logout(logout -> logout
                                .logoutUrl("/api/v1/candidates/logout")
                                .deleteCookies("JSESSIONID")
                                .logoutSuccessUrl("/login?logout")
                                .invalidateHttpSession(true)
                                .clearAuthentication(true)
                        .permitAll()

                );
        return http.build();
    }



}
