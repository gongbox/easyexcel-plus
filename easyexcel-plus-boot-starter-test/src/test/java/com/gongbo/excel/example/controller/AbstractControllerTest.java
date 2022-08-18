package com.gongbo.excel.example.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.listener.ReadListener;
import com.gongbo.excel.example.view.ExportDemoView;
import org.junit.jupiter.api.Assertions;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * @author gongbo
 * @date 2022/8/18
 */
public class AbstractControllerTest {

    private static final boolean DEBUG = false;

    protected static final BiFunction<Path, String, List<ExportDemoView>> READ_TO_EXPORT_DEMO_VIEW = (path, sheetName) -> {
        List<ExportDemoView> result = new ArrayList<>();
        System.err.println(path);
        EasyExcel.read(path.toFile(), ExportDemoView.class, new AnalysisEventListener<ExportDemoView>() {
            @Override
            public void invoke(ExportDemoView exportDemoView, AnalysisContext analysisContext) {
                result.add(exportDemoView);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
            }
        }).sheet(sheetName).doRead();
        return result;
    };

    protected static final BiFunction<Path, String, List<Map<String, String>>> READ_TO_MAP = (path, sheetName) -> {
        List<Map<String, String>> result = new ArrayList<>();
        EasyExcel.read(path.toFile(), new ReadListener<Map<String, String>>() {
            @Override
            public void invoke(Map<String, String> o, AnalysisContext analysisContext) {
                result.add(o);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
            }
        }).sheet(sheetName).doRead();
        return result;
    };


    protected <T> List<T> readExcelFile(MvcResult mvcResult, String sheetName, BiFunction<Path, String, List<T>> apply) throws IOException {
        //保存为文件
        Path path = saveTempFile(mvcResult);
        if (DEBUG) {
            Path target = Paths.get("E:\\temp\\" + path.getFileName());
            Files.deleteIfExists(target);
            Files.copy(path, target);
        }
        //检查文件是否存在
        Assertions.assertTrue(Files.exists(path));
        //检查文件大小
        long size = Files.size(path);
        Assertions.assertTrue(size > 0);
        //读取文件内容
        return apply.apply(path, sheetName);
    }

    protected Path saveTempFile(MvcResult mvcResult) throws IOException {
        String requestURI = mvcResult.getRequest().getRequestURI();
        assert requestURI != null;
        String suffix = ".xlsx";
        if (requestURI.contains("test-template-formula") || requestURI.contains("test-excelType")) {
            suffix = ".xls";
        }
        String export_tag = mvcResult.getRequest().getParameter("export_tag");
        if (StrUtil.isNotEmpty(export_tag) && export_tag.equalsIgnoreCase("xls")) {
            suffix = ".xls";
        }
        String fileName = requestURI.replaceFirst("/", "").replace("/", "-") + suffix;
        Path tempFile = Files.createTempFile("", fileName);
        FileOutputStream fout = new FileOutputStream(tempFile.toFile());
        ByteArrayInputStream bin = new ByteArrayInputStream(mvcResult.getResponse().getContentAsByteArray());
        StreamUtils.copy(bin, fout);
        fout.close();
        return tempFile;
    }
}
