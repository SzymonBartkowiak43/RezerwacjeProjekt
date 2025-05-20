package com.example.systemrezerwacji.infrastructure.security;


import com.example.systemrezerwacji.domain.usermodule.UserFacade;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private final JwtAuthTokenFilter jwtAuthTokenFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserFacade userFacade) {
        return new LoginUserDetailsService(userFacade);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return
                httpSecurity.csrf(AbstractHttpConfigurer::disable)
                        .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/swagger-ui").permitAll()
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .requestMatchers("/api-docs/**").permitAll()
                                .requestMatchers("/webjars/**").permitAll()
                                .requestMatchers("/token/**").permitAll()
                                .requestMatchers("/register/**").permitAll()
                                .requestMatchers("/swagger-resources/**").permitAll()
                                .requestMatchers("/salons/**").permitAll()
                                .requestMatchers("/salons").permitAll()
                                .requestMatchers("/reservation").permitAll()
                                .requestMatchers("/employee-to-offer/**").permitAll()
                                .requestMatchers("/offers/**").permitAll()
                                .requestMatchers("/employee/available-dates/**").permitAll()
                                .requestMatchers("/reservation-service/code/generateCode").permitAll()
                                .anyRequest().permitAll()
                        )
                        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .httpBasic(Customizer.withDefaults())
                        .addFilterBefore(new ConditionalJwtAuthTokenFilter(jwtAuthTokenFilter, "/reservation"), UsernamePasswordAuthenticationFilter.class)
                        .addFilterBefore(jwtAuthTokenFilter, UsernamePasswordAuthenticationFilter.class)
                        .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
