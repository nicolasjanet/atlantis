package com.vialink.atlantis.authorizationserver.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
public class SecurityContextService {

    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public void save(SecurityContext context,
                      HttpServletRequest request,
                      HttpServletResponse response) {
        securityContextRepository.saveContext(context, request, response);
    }
}
