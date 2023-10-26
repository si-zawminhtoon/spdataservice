package com.iibc.spdataservice;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the home page.
 */
@Controller
public class HomeController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal OidcUser principal) {
        if (principal != null) {
            model.addAttribute("profile", principal.getClaims());
        }

        logger.trace("Hello Trace");
        logger.debug("Hello Debug");
        logger.info("Hello Info");
        logger.warn("Hello Warn");
        logger.error("Hello Error");

        return "index";
    }

}
