package com.gongbo.excel.imports.exception;

/**
 * 导出失败异常
 */
public class ImportFailedException extends RuntimeException {

    /**
     * @param message
     */
    public ImportFailedException(String message) {
        super(message);
    }
}
