package com.gongbo.excel.export.exception;

/**
 * 导出失败异常
 */
public class ExportFailedException extends RuntimeException {

    /**
     *
     */
    public ExportFailedException() {
        super("export error");
    }

    /**
     * @param message
     */
    public ExportFailedException(String message) {
        super(message);
    }
}
