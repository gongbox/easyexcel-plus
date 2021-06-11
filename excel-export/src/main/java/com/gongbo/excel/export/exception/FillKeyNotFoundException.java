package com.gongbo.excel.export.exception;

import java.text.MessageFormat;

public class FillKeyNotFoundException extends RuntimeException {

    /**
     * @param sheetNo
     * @param sheetName
     * @param key
     */
    public FillKeyNotFoundException(Integer sheetNo, String sheetName, String key) {
        this(MessageFormat.format("在sheet(sheetNo:{0},sheetName:{1})中没有找到对应填充的key:{2}", sheetNo, sheetName, key));
    }

    /**
     * @param message
     */
    public FillKeyNotFoundException(String message) {
        super(message);
    }
}
