package com.gongbo.excel.export.custom;

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
