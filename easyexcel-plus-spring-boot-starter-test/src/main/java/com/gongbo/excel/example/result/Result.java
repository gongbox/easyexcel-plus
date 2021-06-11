package com.gongbo.excel.example.result;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result<T> {

    private Integer code;

    private String message;

    private T data;

    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Result(ResultCode resultCode, T data) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<T>(ResultCode.SUCCESS, data);
    }

    public static Result<Void> success() {
        return success(null);
    }

    public static <T> Result<T> fail(T data) {
        return new Result<T>(ResultCode.FAIL, data);
    }

}
