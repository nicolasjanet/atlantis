package com.vialink.atlantis.authorizationserver.controller;

import com.vialink.atlantis.authorizationserver.configuration.CustomLoginSuccessHandler.MFAAuthentication;
import com.vialink.atlantis.authorizationserver.service.MFAService;
import com.vialink.atlantis.authorizationserver.service.SecurityContextService;
import com.vialink.atlantis.authorizationserver.user.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class MFAController {

    private final MFAService service;

    private final SecurityContextService securityContextService;

    private final AuthenticationFailureHandler authenticatorFailureHandler =
            new SimpleUrlAuthenticationFailureHandler("/mfa?error");
    private final AuthenticationSuccessHandler authenticationSuccessHandler =
            new SavedRequestAwareAuthenticationSuccessHandler();

    @GetMapping("/mfa")
    public String mfa() {
        return "mfa";
    }

    @PostMapping("/mfa")
    public void validateCode(
            @RequestParam("code") String code,
            HttpServletRequest request,
            HttpServletResponse response,
            @CurrentSecurityContext SecurityContext context) throws ServletException, IOException {
        if (service.check(getUser(context), code)) {
            var authentication = getAuthentication();
            saveAuthentication(request, response, context, authentication);
            this.authenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
            return;
        }
        this.authenticatorFailureHandler.onAuthenticationFailure(request, response, new BadCredentialsException("bad credentials"));
    }

    private Authentication getAuthentication() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        MFAAuthentication mfaAuthentication = (MFAAuthentication) securityContext.getAuthentication();
        return mfaAuthentication.getPrimaryAuthentication();
    }

    private void saveAuthentication(HttpServletRequest request,
                                    HttpServletResponse response,
                                    SecurityContext securityContext,
                                    Authentication authentication) {
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
        securityContextService.save(securityContext, request, response);
    }

    private User getUser(SecurityContext context) {
        MFAAuthentication mfaAuthentication = (MFAAuthentication) context.getAuthentication();
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                (UsernamePasswordAuthenticationToken) mfaAuthentication.getPrimaryAuthentication();
        return (User) usernamePasswordAuthenticationToken.getPrincipal();
    }

}
