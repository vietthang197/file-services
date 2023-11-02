package com.thanglv.documentapi.controller;

import com.thanglv.documentapi.util.Constant;
import jakarta.servlet.http.HttpServletRequest;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.representations.idm.authorization.PermissionTicketRepresentation;
import org.keycloak.representations.idm.authorization.ResourceOwnerRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.keycloak.representations.idm.authorization.UmaPermissionRepresentation;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {

    @GetMapping("{fileId}")
    public String viewFile(@PathVariable String fileId) {
        return "OK-" + fileId;
    }

    @PostMapping
    public String createFile(HttpServletRequest request) {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) request.getUserPrincipal();
        AuthzClient authzClient = AuthzClient.create();
        HashSet<ScopeRepresentation> scopes = new HashSet<>();
        scopes.add(new ScopeRepresentation(Constant.FILE_RESOURCE.SCOPE_VIEW));
        scopes.add(new ScopeRepresentation(Constant.FILE_RESOURCE.SCOPE_DELETE));
        String uuid = UUID.randomUUID().toString();
        ResourceRepresentation resource = new ResourceRepresentation("file-" + uuid, scopes, "/file/" + uuid,
                Constant.FILE_RESOURCE.TYPE);
        resource.setOwnerManagedAccess(true);
        resource.setOwner(jwtAuthenticationToken.getName());
        authzClient.protection().resource().create(resource);

        return "/file/" + uuid;
    }

    @DeleteMapping("{fileId}")
    public String delete(@PathVariable String fileId) {
        return "OK-" + fileId;
    }
}
