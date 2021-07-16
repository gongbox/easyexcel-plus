package com.gongbo.excel.export.exception;

import java.text.MessageFormat;

public class FillKeyNotFoundException extends RuntimeException {

    /**
     * @param sheetNo
     * @param sheetName
     * @param key
     */
    public FillKeyNotFoundException(Integer sheetNo, String sheetName, String key) {
        this(MessageFormat.format("on sheet(sheetNo:{0},sheetName:{1}) not found fill key:{2}", sheetNo, sheetName, key));
    }

    /**
     * @param message
     */
    public FillKeyNotFoundException(String message) {
        super(message);
    }
}
