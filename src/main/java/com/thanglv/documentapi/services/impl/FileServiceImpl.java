package com.thanglv.documentapi.services.impl;

import com.thanglv.documentapi.config.CheckUmaPermission;
import com.thanglv.documentapi.dto.BasicResponseDto;
import com.thanglv.documentapi.dto.FileInfoDto;
import com.thanglv.documentapi.entity.FileInfo;
import com.thanglv.documentapi.mapper.FileInfoMapper;
import com.thanglv.documentapi.repository.FileInfoRepository;
import com.thanglv.documentapi.services.FileService;
import com.thanglv.documentapi.util.Constant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpStatus;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
public class FileServiceImpl implements FileService {

    private final FileInfoRepository fileInfoRepository;
    private final String rootPath;
    private final FileInfoMapper fileInfoMapper;
    private final CheckUmaPermission checkUmaPermission;

    public FileServiceImpl(FileInfoRepository fileInfoRepository, @Value("${file-management.root-path}") String rootPath,
                           FileInfoMapper fileInfoMapper,
                           CheckUmaPermission checkUmaPermission) {
        this.fileInfoRepository = fileInfoRepository;
        this.rootPath = rootPath;
        this.fileInfoMapper = fileInfoMapper;
        this.checkUmaPermission = checkUmaPermission;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public BasicResponseDto<FileInfoDto> uploadSingleFile(MultipartFile file, HttpServletRequest request) throws IOException {
        // Tạo đường dẫn đầy đủ cho file đích
        String fileExt = FilenameUtils.getExtension(file.getOriginalFilename());
        String uuid = UUID.randomUUID().toString();
        String newFileName = uuid  +  "." + fileExt;
        Path targetPath = Path.of(rootPath, newFileName);
        // Sử dụng Java NIO để sao chép dữ liệu từ InputStream của MultipartFile vào file đích
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        FileInfo fileInfo = FileInfo.builder()
                .name(newFileName)
                .originalName(file.getOriginalFilename())
                .size(file.getSize())
                .path(targetPath.toAbsolutePath().toString())
                .isPublic(Constant.STR_N)
                .extension(fileExt)
                .isDeleted(Constant.STR_N)
                .build();

        fileInfoRepository.save(fileInfo);

        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) request.getUserPrincipal();
        AuthzClient authzClient = AuthzClient.create();
        HashSet<ScopeRepresentation> scopes = new HashSet<>();
        scopes.add(new ScopeRepresentation(Constant.FILE_RESOURCE.SCOPE_VIEW));
        scopes.add(new ScopeRepresentation(Constant.FILE_RESOURCE.SCOPE_DELETE));
        ResourceRepresentation resource = new ResourceRepresentation("file-" + newFileName, scopes, "/file/" + fileInfo.getId(),
                Constant.FILE_RESOURCE.TYPE);
        resource.setOwnerManagedAccess(true);
        resource.setOwner(jwtAuthenticationToken.getName());
        ResourceRepresentation resourceRepresentation = authzClient.protection().resource().create(resource);

        fileInfo.setKcResourceId(resourceRepresentation.getId());

        return BasicResponseDto.ok(fileInfoMapper.toDto(fileInfo));
    }

    @Override
    @Transactional
    public BasicResponseDto<FileInfoDto> deleteSingleFile(String fileId, HttpServletRequest request) {
        Optional<FileInfo> fileInfoOptional = fileInfoRepository.findById(fileId);
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) request.getUserPrincipal();
        Map<String, Object> realmAccess = (Map<String, Object>) jwtAuthenticationToken.getToken().getClaims().get("realm_access");
        List<String> roles = (List<String>) realmAccess.get("roles");
        if (fileInfoOptional.isPresent()) {
            FileInfo fileInfo = fileInfoOptional.get();
            if (roles != null && roles.contains(Constant.ROLE.ROLE_ADMIN) || jwtAuthenticationToken.getName().equals(fileInfo.getCreatedBy())) {
                fileInfoRepository.deleteById(fileId);
                AuthzClient authzClient = AuthzClient.create();
                authzClient.protection().resource().delete(fileInfo.getKcResourceId());
                return BasicResponseDto.ok(fileInfoMapper.toDto(fileInfo));
            } else {
                BasicResponseDto<FileInfoDto> responseDto = new BasicResponseDto<>();
                responseDto.setStatus(Constant.ERR_CODE.ERR_403);
                responseDto.setMessage("User doesn't have permission");
                return responseDto;
            }
        } else {
            BasicResponseDto<FileInfoDto> responseDto = new BasicResponseDto<>();
            responseDto.setStatus(Constant.ERR_CODE.ERR_404);
            responseDto.setMessage("File not found");
            return responseDto;
        }
    }

    @Override
    public void downloadSingleFile(String fileId, HttpServletRequest request, HttpServletResponse response) {
        Optional<FileInfo> fileInfoOptional = fileInfoRepository.findById(fileId);
        if (fileInfoOptional.isEmpty()) {
            response.setStatus(HttpStatus.SC_NOT_FOUND);
            return;
        }

        FileInfo fileInfo = fileInfoOptional.get();
        boolean isGranted = false;
        if (Constant.STR_N.equals(fileInfo.getIsPublic())) {
            isGranted = checkUmaPermission.isGrant(request, response);
        } else
            isGranted = true;

        if (!isGranted) {
            response.setStatus(HttpStatus.SC_FORBIDDEN);
            return;
        }

        String contentType = "application/octet-stream";
        try {
            contentType = Files.probeContentType(Path.of(fileInfo.getPath()));
        } catch (IOException e) {
            log.error(e);
        }

        // Set the content type and headers for the response
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileInfo.getOriginalName() + "\"");

        // Write the file content to the response OutputStream
        try (var outputStream = response.getOutputStream()) {
            Files.copy(Path.of(fileInfo.getPath()), outputStream);
            outputStream.flush();
        } catch (IOException e) {
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            log.error(e);
        }
    }
}
