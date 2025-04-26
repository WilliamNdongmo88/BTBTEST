package will.dev.BTBTEST.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.config.http.SessionCreationPolicy;
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
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/inscription").permitAll()
                        .requestMatchers("/activation").permitAll()
                        .requestMatchers("/new-activation-code").permitAll()
                        .requestMatchers("/connexion").permitAll()
                        .requestMatchers("/modified-password").permitAll()
                        .requestMatchers("/new-password").permitAll()
                        .requestMatchers("/refresh-token").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                                .loginPage("/")// <-- Custom login page
                                .successHandler((request, response, authentication) -> {
                                    OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
                                    OAuth2User user = token.getPrincipal();
                                    log.info("Info user connecté :: " + user);
                                    jwtService.upsertUser(user);
                                    response.sendRedirect(request.getContextPath() + "/produits");// Redirection manuelle après traitement
                                })
                        //.defaultSuccessUrl("/profile", true) ignore l'execution de successHandler
                )
                // Debut: Pour autoriser l'utilisateur a effectuer des actions dans l'application grace au token
//                .sessionManagement(httpSecuritySessionManagementConfigurer ->
//                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )STATELESS(Mode sans session): l'utilisateur n'est pas conservé entre le successHandler et "/profile"
//                        //ce qui fait échouer la redirection

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                // Fin

                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
