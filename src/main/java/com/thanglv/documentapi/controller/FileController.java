package com.thanglv.documentapi.controller;

import com.thanglv.documentapi.config.CheckUmaPermission;
import com.thanglv.documentapi.dto.BasicResponseDto;
import com.thanglv.documentapi.dto.FileInfoDto;
import com.thanglv.documentapi.services.FileService;
import com.thanglv.documentapi.util.Constant;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.keycloak.adapters.authorization.PolicyEnforcer;
import org.keycloak.adapters.authorization.integration.jakarta.ServletPolicyEnforcerFilter;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.keycloak.representations.idm.authorization.PermissionTicketRepresentation;
import org.keycloak.representations.idm.authorization.ResourceOwnerRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.keycloak.representations.idm.authorization.UmaPermissionRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileController {

    private final CheckUmaPermission checkUmaPermission;
    private final FileService fileService;

    public FileController(CheckUmaPermission checkUmaPermission, FileService fileService) {
        this.checkUmaPermission = checkUmaPermission;
        this.fileService = fileService;
    }

    @GetMapping("{fileId}")
    public void viewFile(@PathVariable @NotBlank(message = "fileId is not null") String fileId, HttpServletRequest request, HttpServletResponse response) {
        fileService.downloadSingleFile(fileId, request, response);
    }

    @PostMapping
    public BasicResponseDto<FileInfoDto> createFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        return fileService.uploadSingleFile(file, request);
    }

    @DeleteMapping("{fileId}")
    public BasicResponseDto<FileInfoDto> delete(@PathVariable @NotNull(message = "fileId is not null") String fileId, HttpServletRequest request) {
        return fileService.deleteSingleFile(fileId, request);
    }
}
