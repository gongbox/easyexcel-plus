package com.gongbo.excel.export.custom.defaults;


import com.gongbo.excel.export.custom.FileNameConvert;

public class DefaultFileNameConvert implements FileNameConvert {
    @Override
    public String apply(String fileName) {
        return fileName;
    }
}