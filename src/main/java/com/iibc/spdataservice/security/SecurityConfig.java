package com.iibc.spdataservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.springframework.security.config.Customizer.withDefaults;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Configures our application with Spring Security to restrict access to our API
 * endpoints.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${okta.oauth2.issuer}")
    private String issuer;
    @Value("${okta.oauth2.client-id}")
    private String clientId;
    @Value("${okta.oauth2.audience}")
    private String audience;

    private final ClientRegistrationRepository clientRegistrationRepository;

    public SecurityConfig(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        /*
         * This is where we configure the security required for our endpoints and setup
         * our app to serve as
         * an OAuth2 Resource Server, using JWT validation.
         */
        http
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .authorizationRequestResolver(
                                        authorizationRequestResolver(this.clientRegistrationRepository)))
                        .userInfoEndpoint(userInfo -> userInfo
                                .userAuthoritiesMapper(grantedAuthoritiesMapper())))
                .logout(logout -> logout
                        .addLogoutHandler(logoutHandler()))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/", "/images/**").permitAll()
                        .requestMatchers("/api/public").permitAll()
                        .requestMatchers("/api/private").authenticated()
                        .requestMatchers("/api/private-scoped").hasAuthority("SCOPE_read:messages")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));
        return http.build();
    }

    private OAuth2AuthorizationRequestResolver authorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository) {

        DefaultOAuth2AuthorizationRequestResolver authorizationRequestResolver = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository, "/oauth2/authorization");
        authorizationRequestResolver.setAuthorizationRequestCustomizer(
                authorizationRequestCustomizer());

        return authorizationRequestResolver;
    }

    private Consumer<OAuth2AuthorizationRequest.Builder> authorizationRequestCustomizer() {
        return customizer -> customizer
                .additionalParameters(params -> params.put("audience", audience));
    }

    private LogoutHandler logoutHandler() {
        return (request, response, authentication) -> {
            try {
                String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                response.sendRedirect(issuer + "v2/logout?client_id=" + clientId + "&returnTo=" + baseUrl);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Bean
    JwtDecoder jwtDecoder() {
        /*
         * By default, Spring Security does not validate the "aud" claim of the token,
         * to ensure that this token is
         * indeed intended for our app. Adding our own validator is easy to do:
         */

        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromOidcIssuerLocation(issuer);

        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(audience);
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

        jwtDecoder.setJwtValidator(withAudience);

        return jwtDecoder;
    }

    private GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
        return authorities -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            authorities.forEach(authority -> {
                GrantedAuthority mappedAuthority;

                if (authority instanceof OidcUserAuthority) {
                    OidcUserAuthority userAuthority = (OidcUserAuthority) authority;
                    mappedAuthority = new OidcUserAuthority(
                            "OIDC_USER", userAuthority.getIdToken(), userAuthority.getUserInfo());

                } else if (authority instanceof OAuth2UserAuthority) {
                    OAuth2UserAuthority userAuthority = (OAuth2UserAuthority) authority;
                    mappedAuthority = new OAuth2UserAuthority(
                            "OAUTH2_USER", userAuthority.getAttributes());
                } else {
                    mappedAuthority = authority;
                }

                mappedAuthorities.add(mappedAuthority);
            });

            return mappedAuthorities;
        };
    }

}
