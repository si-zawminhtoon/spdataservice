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

    /** 特定のロガーの指定がないため、rootが適用される */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal OidcUser principal) {

        logger.info("ログテスト");

        if (principal != null) {
            model.addAttribute("profile", principal.getClaims());
        }
        return "index";
    }

}
