package com.vialink.atlantis.authorizationserver.configuration;

import com.vialink.atlantis.authorizationserver.service.SecurityContextService;
import com.vialink.atlantis.authorizationserver.user.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    public static class MFAAuthentication extends AnonymousAuthenticationToken {

        private static final long serialVersionUID = 1L;

        private final Authentication primaryAuthentication;

        public MFAAuthentication(Authentication authentication) {
            super("anonymous", "anonymousUser",
                    AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS", "ROLE_MFA"));
            this.primaryAuthentication = authentication;
        }

        public Authentication getPrimaryAuthentication() {
            return this.primaryAuthentication;
        }
    }

    private final AuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
    private final AuthenticationSuccessHandler simpleUrlAuthenticationSuccessHandler = new SimpleUrlAuthenticationSuccessHandler("/mfa");

    private final SecurityContextService securityContextService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof UsernamePasswordAuthenticationToken token) {
            if (token.getPrincipal() instanceof User user) {
                if (!user.isMfa()) {
                    handler.onAuthenticationSuccess(request, response, authentication);
                    return;
                }
            }
        }
        // MFA required
        saveAuthentication(request, response, new MFAAuthentication(authentication));
        simpleUrlAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
    }

    private void saveAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            MFAAuthentication authentication) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
        securityContextService.save(securityContext, request, response);
    }
}
