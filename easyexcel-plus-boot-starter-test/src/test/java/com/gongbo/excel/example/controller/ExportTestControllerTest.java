package com.gongbo.excel.example.controller;

import com.gongbo.excel.example.ServerApplication;
import com.gongbo.excel.example.view.ExportDemoView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author gongbo
 * @date 2022/8/17
 */
@SpringBootTest(classes = {ServerApplication.class})
@AutoConfigureMockMvc
class ExportTestControllerTest extends AbstractControllerTest{

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @ValueSource(strings = {"/export/test-normal", "/export/test-normal-array", "/export/test-normal-list", "/export/test-normal-iterable"})
    void testNormal(String url) throws Exception {
        //普通请求
        mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print());

        //导出请求
        mockMvc.perform(get(url + "?export=excel")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    List<ExportDemoView> exportDemoViews = readExcelFile(mvcResult, "Sheet1", READ_TO_EXPORT_DEMO_VIEW);
                    Assertions.assertEquals(exportDemoViews.size(), 10);
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"/export/test-fileName"})
    void testFilename(String url) throws Exception {
        //普通请求
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print());

        //导出请求
        mockMvc.perform(get(url + "?export=excel")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    String contentType = mvcResult.getResponse().getHeader("Content-Type");
                    Assertions.assertEquals(contentType, "application/vnd.ms-excel;charset=UTF-8");

                    String contentDisposition = mvcResult.getResponse().getHeader("Content-Disposition");
                    String value = "attachment;filename*=utf-8''" + URLEncoder.encode("文件名称", StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20") + ".xlsx";
                    Assertions.assertEquals(contentDisposition, value);

                    List<ExportDemoView> exportDemoViews = readExcelFile(mvcResult, "Sheet1", READ_TO_EXPORT_DEMO_VIEW);
                    Assertions.assertEquals(exportDemoViews.size(), 10);
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"/export/test-fileName-convert"})
    void testFileNameConvert(String url) throws Exception {
        //普通请求
        mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print());

        //导出请求
        mockMvc.perform(get(url + "?export=excel")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    String contentType = mvcResult.getResponse().getHeader("Content-Type");
                    Assertions.assertEquals(contentType, "application/vnd.ms-excel;charset=UTF-8");

                    String contentDisposition = mvcResult.getResponse().getHeader("Content-Disposition");
                    String fileName = LocalDate.of(2022, 1, 1).format(DateTimeFormatter.BASIC_ISO_DATE);
                    String value = "attachment;filename*=utf-8''" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20") + ".xlsx";
                    Assertions.assertEquals(contentDisposition, value);

                    List<ExportDemoView> exportDemoViews = readExcelFile(mvcResult, "Sheet1", READ_TO_EXPORT_DEMO_VIEW);
                    Assertions.assertEquals(exportDemoViews.size(), 10);
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"/export/test-fileName-business"})
    void testFileNameBusiness(String url) throws Exception {
        //普通请求
        mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print());

        //导出请求
        mockMvc.perform(get(url + "?export=excel")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    String contentType = mvcResult.getResponse().getHeader("Content-Type");
                    Assertions.assertEquals(contentType, "application/vnd.ms-excel;charset=UTF-8");

                    String contentDisposition = mvcResult.getResponse().getHeader("Content-Disposition");
                    String fileName = "动态文件名称";
                    String value = "attachment;filename*=utf-8''" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20") + ".xlsx";
                    Assertions.assertEquals(contentDisposition, value);

                    List<ExportDemoView> exportDemoViews = readExcelFile(mvcResult, "Sheet1", READ_TO_EXPORT_DEMO_VIEW);
                    Assertions.assertEquals(exportDemoViews.size(), 10);
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"/export/test-sheetName"})
    void testSheetName(String url) throws Exception {
        //普通请求
        mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print());

        //导出请求
        mockMvc.perform(get(url + "?export=excel")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    List<ExportDemoView> exportDemoViews = readExcelFile(mvcResult, "Sheet0", READ_TO_EXPORT_DEMO_VIEW);
                    Assertions.assertEquals(exportDemoViews.size(), 10);
                });

    }

    @ParameterizedTest
    @ValueSource(strings = {"/export/test-sheetName-business"})
    void testSheetNameBusiness(String url) throws Exception {
        //普通请求
        mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print());

        //导出请求
        mockMvc.perform(get(url + "?export=excel")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    List<ExportDemoView> exportDemoViews = readExcelFile(mvcResult, "业务中修改Sheet名称", READ_TO_EXPORT_DEMO_VIEW);
                    Assertions.assertEquals(exportDemoViews.size(), 10);
                });

    }

    @ParameterizedTest
    @ValueSource(strings = {"/export/test-out-path"})
    void testOutPath(String url) throws Exception {
        //普通请求
        mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print());

        //导出请求
        mockMvc.perform(get(url + "?export=excel")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk()).andDo(print());
        Path path = Paths.get("E:\\temp", "test-out-path.xlsx");
        boolean exists = Files.exists(path);
        Assertions.assertTrue(exists);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/export/test-filter"})
    void testFilter(String url) throws Exception {
        //普通请求
        mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print());

        //导出请求
        mockMvc.perform(get(url + "?export=excel")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    List<ExportDemoView> exportDemoViews = readExcelFile(mvcResult, "Sheet1", READ_TO_EXPORT_DEMO_VIEW);
                    Assertions.assertEquals(exportDemoViews.size(), 10);
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"/export/test-excelType"})
    void testExcelType(String url) throws Exception {
        //普通请求
        mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print());

        //导出请求
        mockMvc.perform(get(url + "?export=excel")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    List<ExportDemoView> exportDemoViews = readExcelFile(mvcResult, "Sheet1", READ_TO_EXPORT_DEMO_VIEW);
                    Assertions.assertEquals(exportDemoViews.size(), 10);
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"/export/test-dataConvert"})
    void testDataConvert(String url) throws Exception {
        //普通请求
        mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print());

        //导出请求
        mockMvc.perform(get(url + "?export=excel")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    List<ExportDemoView> exportDemoViews = readExcelFile(mvcResult, "Sheet1", READ_TO_EXPORT_DEMO_VIEW);
                    Assertions.assertEquals(exportDemoViews.size(), 30);
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"/export/test-tag"})
    void testTag(String url) throws Exception {
        //普通请求
        mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print());

        //导出请求
        mockMvc.perform(get(url + "?export=excel&export_tag=xls")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    String contentDisposition = mvcResult.getResponse().getHeader("Content-Disposition");
                    assert contentDisposition != null;
                    Assertions.assertTrue(contentDisposition.endsWith(".xls"));
                    List<ExportDemoView> exportDemoViews = readExcelFile(mvcResult, "Sheet1", READ_TO_EXPORT_DEMO_VIEW);
                    Assertions.assertEquals(exportDemoViews.size(), 10);
                });

        //导出请求
        mockMvc.perform(get(url + "?export=excel&export_tag=xlsx")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    String contentDisposition = mvcResult.getResponse().getHeader("Content-Disposition");
                    assert contentDisposition != null;
                    Assertions.assertTrue(contentDisposition.endsWith(".xlsx"));
                    List<ExportDemoView> exportDemoViews = readExcelFile(mvcResult, "Sheet1", READ_TO_EXPORT_DEMO_VIEW);
                    Assertions.assertEquals(exportDemoViews.size(), 10);
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"/export/test-template-simple"})
    void testTemplateSimple(String url) throws Exception {
        //普通请求
        mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print());

        //导出请求
        mockMvc.perform(get(url + "?export=excel")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    List<ExportDemoView> exportDemoViews = readExcelFile(mvcResult, "Sheet1", READ_TO_EXPORT_DEMO_VIEW);
                    Assertions.assertEquals(exportDemoViews.size(), 10);
                });
    }


    @ParameterizedTest
    @ValueSource(strings = {"/export/test-template-single-sheet"})
    void testTemplateSingleSheet(String url) throws Exception {
        //普通请求
        mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print());

        //导出请求
        mockMvc.perform(get(url + "?export=excel")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    List<Map<String, String>> exportDemoViews = readExcelFile(mvcResult, "Sheet1", READ_TO_MAP);
                    Assertions.assertEquals(exportDemoViews.size(), 11);
                });
    }


    @ParameterizedTest
    @ValueSource(strings = {"/export/test-template-much-sheet"})
    void testTemplateMuchSheet(String url) throws Exception {
        //普通请求
        mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print());

        //导出请求
        mockMvc.perform(get(url + "?export=excel")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    List<Map<String, String>> exportDemoViews = readExcelFile(mvcResult, "Sheet1", READ_TO_MAP);
                    Assertions.assertEquals(exportDemoViews.size(), 0);

                    List<Map<String, String>> exportDemoViews2 = readExcelFile(mvcResult, "Sheet2", READ_TO_MAP);
                    Assertions.assertEquals(exportDemoViews2.size(), 10);
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"/export/test-template-formula"})
    void testTemplateFormula(String url) throws Exception {
        //普通请求
        mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print());

        //导出请求
        mockMvc.perform(get(url + "?export=excel")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    List<Map<String, String>> exportDemoViews = readExcelFile(mvcResult, "Sheet1", READ_TO_MAP);
                    Assertions.assertEquals(exportDemoViews.size(), 10);
                });
    }


}