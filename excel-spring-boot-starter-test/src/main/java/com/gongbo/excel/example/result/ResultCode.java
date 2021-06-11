package com.gongbo.excel.example.result;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ResultCode {
    SUCCESS(0, "success"),
    FAIL(100, "fail"),
    ERROR(500, "error");

    private final int code;
    private final String message;
}
