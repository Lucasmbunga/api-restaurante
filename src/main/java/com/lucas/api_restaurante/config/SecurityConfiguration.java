package com.lucas.api_restaurante.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final SecurityFilter securityFilter;

    public SecurityConfiguration(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        HeaderWriterLogoutHandler clearSiteData=new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(ClearSiteDataHeaderWriter.Directive.ALL));
        return httpSecurity
                .csrf(csrf->csrf.disable())
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize->authorize
                        .requestMatchers(HttpMethod.POST,"/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/garcons").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/produtos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/produtos/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/produtos/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/categorias").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/categorias/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/produtos/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/garcons/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/garcons/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/usuarios").permitAll()
                        .anyRequest().authenticated()
                )
                .logout(logout->logout.addLogoutHandler(clearSiteData))
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
