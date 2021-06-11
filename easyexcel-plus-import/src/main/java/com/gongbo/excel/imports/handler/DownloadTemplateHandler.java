package com.gongbo.excel.imports.handler;


import com.alibaba.excel.support.ExcelTypeEnum;
import com.gongbo.excel.imports.entity.ImportContext;
import com.gongbo.excel.common.utils.StringUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 下载模板处理
 */
public interface DownloadTemplateHandler {

    void download(ImportContext importContext, HttpServletResponse response) throws IOException;

    default void addHeader(ImportContext importContext, HttpServletResponse response) {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.addHeader("Content-Type", "application/vnd.ms-excel;charset=UTF-8");

        String templateFileName = importContext.getTemplateFileName();

        if (StringUtil.isEmpty(templateFileName)) {
            templateFileName = String.valueOf(System.currentTimeMillis());
        }
        try {
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            templateFileName = URLEncoder.encode(templateFileName, StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException ignored) {
        }

        String excelFileSuffix = ExcelTypeEnum.XLS.getValue();

        response.addHeader("Content-Disposition", "attachment;filename*=utf-8''" + templateFileName + excelFileSuffix);
    }
}
