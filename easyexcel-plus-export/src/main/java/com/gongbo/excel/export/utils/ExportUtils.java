package com.gongbo.excel.export.utils;

import com.gongbo.excel.export.config.ExportProperties;
import com.gongbo.excel.export.core.ExportHelper;
import com.gongbo.excel.export.entity.ExportContext;
import com.gongbo.excel.export.exception.ExportFailedException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileUrlResource;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExportUtils {

    public static final String CLASSPATH_PATH_PREFIX = "classpath:";
    public static final String FILE_PATH_PREFIX = "file:";

    /**
     * 获取方法返回模型类
     *
     * @param method
     * @param exportProperties
     * @return
     */
    public static Class<?> getModelClass(Method method, ExportProperties exportProperties) {
        Class<?> returnType = method.getReturnType();
        if (!returnType.getName().equals(exportProperties.getResponseClassName())) {
            throw new ExportFailedException(MessageFormat.format("request method return type must be class[{0}]", exportProperties.getResponseClassName()));
        }

        Type genericReturnType = method.getGenericReturnType();
        //不是泛型类型，则返回空
        if (!(genericReturnType instanceof ParameterizedType)) {
            return null;
        }

        ParameterizedType parameterizedReturnType = (ParameterizedType) genericReturnType;

        //获取泛型参数
        Type[] actualTypeArguments = parameterizedReturnType.getActualTypeArguments();

        Type actualTypeArgument = actualTypeArguments[0];
        //如果泛型参数是泛型类型
        if (actualTypeArgument instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) actualTypeArgument;
            if (parameterizedType.getRawType() instanceof Class) {
                Class<?> rawType = (Class<?>) parameterizedType.getRawType();
                if (Iterable.class.isAssignableFrom(rawType)) {
                    Type actualType2 = parameterizedType.getActualTypeArguments()[0];
                    if (actualType2 instanceof Class) {
                        return (Class<?>) actualType2;
                    } else if (actualType2 instanceof WildcardType) {
                        return null;
                    }
                }
            }
        }
        //如果泛型参数是泛数组类型
        else if (actualTypeArgument instanceof Class && ((Class<?>) actualTypeArgument).isArray()) {
            return ((Class<?>) actualTypeArgument).getComponentType();
        }

        return null;
    }

    /**
     * 数组或集合类型转化为List类型
     *
     * @param result
     * @return
     */
    public static List<?> objectToList(Object result) {
        if (result == null) {
            return Collections.emptyList();
        }

        if (result instanceof Collection) {
            if (result instanceof List) {
                return (List<?>) result;
            } else {
                return new ArrayList<>((Collection<?>) result);
            }
        } else if (result.getClass().isArray()) {
            return Arrays.asList((Object[]) result);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param exportContext
     * @return
     */
    public static OutputStream getExportOutputStream(ExportContext exportContext, HttpServletResponse response) throws IOException {
        if (exportContext.isOutputFile()) {
            Files.createDirectories(Paths.get(exportContext.getOutputPath()));
            File file = new File(exportContext.getOutputPath(), exportContext.getFileName() + exportContext.getExcelType().getValue());
            return new FileOutputStream(file);
        } else {
            //设置响应头信息
            ExportHelper.setDownloadResponseHeaders(response, exportContext);
            return response.getOutputStream();
        }
    }

    /**
     * @param exportContext
     * @return
     * @throws IOException
     */
    public static InputStream getTemplateInputStream(ExportContext exportContext) throws IOException {
        String template = exportContext.getTemplate();
        String templatePath;
        if (template.startsWith(CLASSPATH_PATH_PREFIX) || template.startsWith(FILE_PATH_PREFIX)) {
            templatePath = template;
        } else {
            templatePath = exportContext.getExportProperties().getTemplateDir() + "/" + template;
        }

        InputStream inputStream;
        try {
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
        } catch (FileNotFoundException e) {
            throw new ExportFailedException(MessageFormat.format("not found template file of path:{0}", templatePath));
        }
    }
}
