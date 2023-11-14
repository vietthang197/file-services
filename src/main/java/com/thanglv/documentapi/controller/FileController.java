package com.thanglv.documentapi.controller;

import com.thanglv.documentapi.dto.BasicResponseDto;
import com.thanglv.documentapi.dto.FileInfoDto;
import com.thanglv.documentapi.services.FileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
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

    private final FileService fileService;

    public FileController(FileService fileService) {
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
