package com.gongbo.excel.export.core.handler;

/**
 * 文件名转换
 */
public interface FileNameConvert {

    /**
     * @param fileName
     * @return
     */
    String apply(String fileName);

}
