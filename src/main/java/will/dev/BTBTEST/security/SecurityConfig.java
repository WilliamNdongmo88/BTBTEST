package will.dev.BTBTEST.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Slf4j
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtService jwtService;
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/inscription","/register").permitAll()
                        .requestMatchers("/activation").permitAll()
                        .requestMatchers("/check-email").permitAll()
                        .requestMatchers("/check-password").permitAll()
                        .requestMatchers("/new-activation-code").permitAll()
                        .requestMatchers("/connexion","/login").permitAll()
                        .requestMatchers("/modified-password").permitAll()
                        .requestMatchers("/new-password").permitAll()
                        .requestMatchers("/refresh-token").permitAll()
                        .requestMatchers("/auth/google").permitAll()
                        .requestMatchers("/product/all_product").authenticated()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults()) // Facultatif : pour tests
                .formLogin(AbstractHttpConfigurer::disable)// Désactive la redirection HTML
                .logout(AbstractHttpConfigurer::disable)
                .cors(withDefaults -> {})
                //.cors(Customizer.withDefaults()) // active le CORS
//                .oauth2Login(oauth -> oauth
//                                //.loginPage("/")// <-- Custom login page
//                                .successHandler((request, response, authentication) -> {
//                                    OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
//                                    OAuth2User user = token.getPrincipal();
//                                    log.info("Info user connecté :: " + user);
//                                    String jwt = jwtService.oAuth2UserToken(user);
//                                    response.sendRedirect("http://localhost:8050/api/auth/oauth/google?token=" + jwt);
//                                })
//                        .failureHandler((request, response, exception) -> {
//                            response.sendRedirect("http://localhost:8050/api/oauth2/failure?error=" + exception.getMessage());
//                        })
//                )
//                .oauth2Login(oauth2 -> oauth2
//                        .defaultSuccessUrl("/auth/google/success", true)
//                        .failureUrl("/auth/google/failure")
//                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling((exceptions) -> {//Envoie un msgError clair coté angular
                    exceptions.authenticationEntryPoint((request, response, authException) -> {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\": \"Non autorisé - token manquant ou invalide\"}");
                    });
                })
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
