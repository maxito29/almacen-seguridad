package com.seguridad.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/webjars/**"
                ).permitAll()
                .requestMatchers(HttpMethod.GET, "/proveedores/**").hasAnyRole("ADMIN", "ALMACEN")
                .requestMatchers("/usuarios/**").hasRole("ADMIN")
                .requestMatchers("/sedes/**").hasRole("ADMIN")
                .requestMatchers("/proveedores/**").hasRole("ADMIN")
                .requestMatchers("/export/**").hasRole("ADMIN")
                .requestMatchers("/notificaciones/**").hasAnyRole("ADMIN", "ALMACEN")
                .requestMatchers(HttpMethod.POST, "/dashboard/alerta-stock/ajax").hasRole("ADMIN")
                .requestMatchers(
                    "/", "/dashboard/**",
                    
                    "/productos/**",
                    "/ingresos/**",
                    "/salidas/**",
                    "/trabajadores/**",
                    "/kardex/**"
                ).hasAnyRole("ADMIN", "ALMACEN")

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/?welcome=true", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            )
            .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}