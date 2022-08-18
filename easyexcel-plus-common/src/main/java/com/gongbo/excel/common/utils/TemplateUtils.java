package com.gongbo.excel.common.utils;

import com.gongbo.excel.common.enums.ExcelType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileUrlResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TemplateUtils {

    public static final String CLASSPATH_PATH_PREFIX = "classpath:";
    public static final String FILE_PATH_PREFIX = "file:";

    public static InputStream getTemplateInputStream(String templateDir, String template) throws IOException {
        String templatePath;
        if (template.startsWith(CLASSPATH_PATH_PREFIX) || template.startsWith(FILE_PATH_PREFIX)) {
            templatePath = template;
        } else {
            //没有指定具体路径时必须配置templateDir
            Objects.requireNonNull(templateDir);
            if (templateDir.endsWith(File.separator) || template.startsWith(File.separator)) {
                templatePath = templateDir + template;
            } else {
                templatePath = templateDir + File.separator + template;
            }
        }

        InputStream inputStream;
        if (templatePath.startsWith(CLASSPATH_PATH_PREFIX)) {
            ClassPathResource resource = new ClassPathResource(templatePath.replaceFirst(CLASSPATH_PATH_PREFIX, ""));
            inputStream = resource.getInputStream();
        } else if (templatePath.startsWith(FILE_PATH_PREFIX)) {
            FileUrlResource fileUrlResource = new FileUrlResource(templatePath.replaceFirst(FILE_PATH_PREFIX, ""));
            inputStream = fileUrlResource.getInputStream();
        } else {
            inputStream = Files.newInputStream(Paths.get(templatePath));
        }
        return inputStream;
    }

    /**
     * 获取模板格式
     *
     * @param template
     * @return
     */
    public static ExcelType getTemplateExcelType(String template) {
        int i = template.lastIndexOf(".");
        String type = template.substring(i);
        for (ExcelType value : ExcelType.values()) {
            if (value.getValue().equalsIgnoreCase(type)) {
                return value;
            }
        }
        throw new IllegalArgumentException();
    }
}
