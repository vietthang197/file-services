package com.thanglv.documentapi.mapper;

import com.thanglv.documentapi.dto.FileInfoDto;
import com.thanglv.documentapi.entity.FileInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileInfoMapper {
    FileInfoDto toDto(FileInfo fileInfo);
}
