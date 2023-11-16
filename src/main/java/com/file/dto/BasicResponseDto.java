package com.file.dto;

import com.file.util.Constant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class BasicResponseDto<T> implements Serializable {
    private String status;
    private String message;
    private T data;

    public BasicResponseDto() {
    }

    public static <T> BasicResponseDto<T> ok(T data) {
        BasicResponseDto<T> basicResponseDto = new BasicResponseDto<>();
        basicResponseDto.setData(data);
        basicResponseDto.setStatus(Constant.ERR_CODE.SUCCESS);
        basicResponseDto.setMessage("Success");
        return basicResponseDto;
    }

    public static <T> BasicResponseDto<T> ok() {
        BasicResponseDto<T> basicResponseDto = new BasicResponseDto<>();
        basicResponseDto.setStatus(Constant.ERR_CODE.SUCCESS);
        basicResponseDto.setMessage("Success");
        return basicResponseDto;
    }

    public static <T> BasicResponseDto<T> failed(T data) {
        BasicResponseDto<T> basicResponseDto = new BasicResponseDto<>();
        basicResponseDto.setData(data);
        basicResponseDto.setStatus(Constant.ERR_CODE.ERR_400);
        basicResponseDto.setMessage("Failed");
        return basicResponseDto;
    }
}
