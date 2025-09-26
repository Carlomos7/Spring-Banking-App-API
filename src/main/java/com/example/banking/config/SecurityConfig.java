package com.example.banking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
    UserDetails user = User.withUsername("spring")
        .password(passwordEncoder.encode("secret"))
        .roles("USER")
        .build();
    return new InMemoryUserDetailsManager(user);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable()) // Disable CSRF for H2 console access in dev
        .headers(h -> h.frameOptions(f -> f.sameOrigin())) // Allow H2 console to load in a frame
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/auth/**", // allow ALL auth endpoints (register, login)
                "/h2-console/**",
                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
            .permitAll() // Permit access to H2 console and auth endpoints
            .anyRequest().authenticated())
        .httpBasic(Customizer.withDefaults());
    // .formLogin(Customizer.withDefaults());
    return http.build();
  }

}
