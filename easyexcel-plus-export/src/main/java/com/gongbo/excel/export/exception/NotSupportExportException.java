package com.gongbo.excel.export.exception;

/**
 * 方法不支持导出异常，即方法没有添加ExcelExport注解
 */
public class NotSupportExportException extends RuntimeException {

    /**
     * @param message
     */
    public NotSupportExportException(String message) {
        super(message);
    }
}
