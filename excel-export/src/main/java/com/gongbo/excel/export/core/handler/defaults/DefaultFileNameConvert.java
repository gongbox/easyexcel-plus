package com.gongbo.excel.export.core.handler.defaults;

import com.gongbo.excel.export.core.handler.FileNameConvert;

public class DefaultFileNameConvert implements FileNameConvert {
    @Override
    public String apply(String fileName) {
        return fileName;
    }
}