package com.file.services;

import com.file.dto.BasicResponseDto;
import com.file.dto.FileInfoDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    BasicResponseDto<FileInfoDto> uploadSingleFile(MultipartFile file, HttpServletRequest request) throws IOException;

    BasicResponseDto<FileInfoDto> deleteSingleFile(String fileId, HttpServletRequest request);

    void downloadSingleFile(String fileId, HttpServletRequest request, HttpServletResponse response);
}
