package com.proyecto.gestioncitas.config.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Cadena de filtros de la API (instruccion M5).
 *
 * Dos cosas que cambian respecto de Spring Boot 3 y que, si se omiten, rompen
 * la API sin un mensaje claro:
 *
 * 1. Spring Security 7 activa CSRF por defecto. En una API stateless con JWT
 *    hay que desactivarlo explicitamente o todo POST, PATCH y DELETE responde
 *    403 aunque el token sea valido.
 * 2. Sin SessionCreationPolicy.STATELESS el servidor crea una JSESSIONID por
 *    peticion, lo que en Render consume memoria para nada.
 */
@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    /** Rutas abiertas: login, registro, refresh, documentacion y health. */
    private static final String[] RUTAS_PUBLICAS = {
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/actuator/health",
            "/actuator/health/**",
            "/actuator/info",

            // Sin esta ruta, cualquier excepcion no manejada se reenvia a
            // /error, que al exigir autenticacion responde 401 y oculta el
            // error real. Eso convierte un 500 en un 401 enganoso.
            "/error"
    };

    private final JwtAuthenticationFilter jwtFilter;
    private final JwtAuthenticationEntryPoint entryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter,
            JwtAuthenticationEntryPoint entryPoint,
            CustomAccessDeniedHandler accessDeniedHandler,
            CorsConfigurationSource corsConfigurationSource) {
        this.jwtFilter = jwtFilter;
        this.entryPoint = entryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // Obligatorio en Spring Security 7 para una API stateless.
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // El preflight de CORS viaja sin token.
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(RUTAS_PUBLICAS).permitAll()
                        // El resto exige token; el rol lo afina cada @PreAuthorize
                        // segun la matriz de la seccion 2.4.
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * BCrypt con el coste por defecto (10). No se sube en Render: cada
     * incremento duplica el tiempo de hash y el plan gratuito tiene 0.1 CPU.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
