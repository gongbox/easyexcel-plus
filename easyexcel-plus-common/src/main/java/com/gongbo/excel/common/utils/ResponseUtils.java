package com.gongbo.excel.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseUtils {

    /**
     * 设置下载文件名
     *
     * @param fileName
     * @param response
     */
    public static void setDownloadFileHeader(HttpServletResponse response, String fileName) {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.addHeader("Content-Type", "application/vnd.ms-excel;charset=UTF-8");
        try {
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException ignored) {
        }

        response.addHeader("Content-Disposition", "attachment;filename*=utf-8''" + fileName);
    }
}
