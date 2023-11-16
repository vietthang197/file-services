package com.file.mapper;

import com.file.entity.FileInfo;
import com.file.dto.FileInfoDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileInfoMapper {
    FileInfoDto toDto(FileInfo fileInfo);
}
