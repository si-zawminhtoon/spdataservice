package com.iibc.spdataservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * Controller for requests to the {@code /profile} resource. Populates the model
 * with the claims from the
 * {@linkplain OidcUser} for use by the view.
 */
@Configuration
@Controller
public class ProfileController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/profile")
    public String profile(Model model, @AuthenticationPrincipal OidcUser oidcUser,
            OAuth2AuthenticationToken oAuth2AuthenticationToken, HttpServletResponse response) {
        model.addAttribute("profile", oidcUser.getClaims());
        model.addAttribute("profileJson", claimsToJson(oidcUser.getClaims()));

        var oAuth2AuthorizedClient = authorizedClientService.loadAuthorizedClient(
                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId(), oidcUser.getName());

        OidcIdToken idToken = oidcUser.getIdToken();
        OAuth2AccessToken accessToken = oAuth2AuthorizedClient.getAccessToken();
        OAuth2RefreshToken refreshToken = oAuth2AuthorizedClient.getRefreshToken();

        var idTokenStr = "";
        var accessTokenStr = "";
        var refreshTokenStr = "";
        var idTokenExpire = "";
        var accessTokenExpire = "";
        if (idToken != null) {
            idTokenStr = idToken.getTokenValue();
            java.time.Instant expire = idToken.getExpiresAt();
            if (expire != null) {
                idTokenExpire = expire.toString();
            }
        }
        if (accessToken != null) {
            accessTokenStr = accessToken.getTokenValue();
            java.time.Instant expire = accessToken.getExpiresAt();
            if (expire != null) {
                accessTokenExpire = expire.toString();
            }
        }
        if (refreshToken != null) {
            refreshTokenStr = refreshToken.getTokenValue();
        }

        model.addAttribute("idToken", idTokenStr);
        model.addAttribute("accessToken", accessTokenStr);
        model.addAttribute("refreshToken", refreshTokenStr);
        model.addAttribute("idTokenExpire", idTokenExpire);
        model.addAttribute("accessTokenExpire", accessTokenExpire);
        return "profile";
    }

    private String claimsToJson(Map<String, Object> claims) {
        try {
            return objectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(claims);
        } catch (JsonProcessingException jpe) {
            log.error("Error parsing claims to JSON", jpe);
        }
        return "Error parsing claims to JSON.";
    }

    @Bean
    public ObjectMapper objectMapper() {
        JavaTimeModule module = new JavaTimeModule();
        return new ObjectMapper()
                .registerModule(module);
    }

}
