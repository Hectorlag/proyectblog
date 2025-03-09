package com.proyecto.blog.security.config;

import com.proyecto.blog.security.config.filter.JwtTokenValidator;
import com.proyecto.blog.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtils jwtUtils;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF si es necesario
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) // Usar sesión para el login con formulario
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/login",
                                "/api/users/register",
                                "/oauth2/authorization/github",
                                "/login/oauth2/code/github"
                        ).permitAll() // Permitir acceso sin autenticación
                        .anyRequest().authenticated() // Requiere autenticación para cualquier otra solicitud
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/auth/login") // Página de login personalizada para OAuth2
                        .defaultSuccessUrl("/", true) // Redirige al home después de login exitoso
                )
                .formLogin(form -> form
                        .loginPage("/auth/login") // Página de login personalizada para formulario
                        .permitAll() // Permite acceso a la página de login
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL para cerrar sesión
                        .logoutSuccessUrl("/auth/login?logout") // Redirige al login con parámetro logout después de cerrar sesión
                        .invalidateHttpSession(true) // Invalida la sesión HTTP
                        .clearAuthentication(true) // Limpia la autenticación
                        .permitAll() // Permite acceso para cerrar sesión
                )
                .addFilterBefore(new JwtTokenValidator(jwtUtils), BasicAuthenticationFilter.class) // Si usas JWT, lo validas aquí
                .build();
    }




    @Bean
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return new OAuth2AuthenticationSuccessHandler(jwtUtils);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

