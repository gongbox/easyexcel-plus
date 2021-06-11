package com.gongbo.excel.imports.exception;

/**
 * 方法不支持导出异常，即方法没有添加EnableExport注解
 */
public class NotSupportImportException extends RuntimeException {

    /**
     * @param message
     */
    public NotSupportImportException(String message) {
        super(message);
    }
}
