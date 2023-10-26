package com.iibc.spdataservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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

        var accessToken = oAuth2AuthorizedClient.getAccessToken().getTokenValue();
        JSONObject myJSON;
        try {
            com.mashape.unirest.http.HttpResponse<JsonNode> resp = Unirest.post("https://dev-zawminhto/oauth/token")
                    .header("content-type", "application/x-www-form-urlencoded")
                    .body("grant_type=client_credentials&client_id=Qcz08WqWkdQKlxIeDFofTtluuLbjZ1rf&client_secret=KwriXi2mPlv56WdlUpAYZv58bsDzMdal6BBNnOgDBM0g6bIkaXRr7ibr4QepXa2J&audience=https://test-api")
                    .asJson();

            myJSON = resp.getBody().getObject();
        } catch (UnirestException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        model.addAttribute("accessToken", accessToken);
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
