package com.file.services.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.keycloak.AuthorizationContext;
import org.keycloak.adapters.authorization.PolicyEnforcer;
import org.keycloak.adapters.authorization.TokenPrincipal;
import org.keycloak.adapters.authorization.integration.elytron.ServletHttpRequest;
import org.keycloak.adapters.authorization.integration.elytron.ServletHttpResponse;
import org.keycloak.adapters.authorization.spi.ConfigurationResolver;
import org.keycloak.adapters.authorization.spi.HttpRequest;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class KeycloakAuthzService {

    private final Map<PolicyEnforcerConfig, PolicyEnforcer> policyEnforcer;
    private final ConfigurationResolver configResolver;

    public KeycloakAuthzService(ConfigurationResolver configResolver) {
        this.configResolver = configResolver;
        this.policyEnforcer = Collections.synchronizedMap(new HashMap<>());
    }

    public boolean isGrant(final HttpServletRequest request, HttpServletResponse response) {
        ServletHttpRequest httpRequest = new ServletHttpRequest(request, new TokenPrincipal() {
            public String getRawToken() {
                return KeycloakAuthzService.extractBearerToken(request);
            }
        });
        PolicyEnforcer policyEnforcer = this.getOrCreatePolicyEnforcer(httpRequest);
        AuthorizationContext authzContext = policyEnforcer.enforce(httpRequest, new ServletHttpResponse(response));
        request.setAttribute(AuthorizationContext.class.getName(), authzContext);
        return authzContext.isGranted();
    }

    public static String extractBearerToken(HttpServletRequest request) {
        Enumeration<String> authorizationHeaderValues = request.getHeaders("Authorization");
        while(authorizationHeaderValues.hasMoreElements()) {
            String value = authorizationHeaderValues.nextElement();
            String[] parts = value.trim().split("\\s+");
            if (parts.length == 2) {
                String bearer = parts[0];
                if (bearer.equalsIgnoreCase("Bearer")) {
                    return parts[1];
                }
            }
        }
        return null;
    }

    private PolicyEnforcer getOrCreatePolicyEnforcer(HttpRequest request) {
        return this.policyEnforcer.computeIfAbsent(this.configResolver.resolve(request), KeycloakAuthzService.this::createPolicyEnforcer);
    }
    protected PolicyEnforcer createPolicyEnforcer(PolicyEnforcerConfig enforcerConfig) {
        String authServerUrl = enforcerConfig.getAuthServerUrl();
        return PolicyEnforcer.builder().authServerUrl(authServerUrl).realm(enforcerConfig.getRealm()).clientId(enforcerConfig.getResource()).credentials(enforcerConfig.getCredentials()).bearerOnly(false).enforcerConfig(enforcerConfig).build();
    }
}
