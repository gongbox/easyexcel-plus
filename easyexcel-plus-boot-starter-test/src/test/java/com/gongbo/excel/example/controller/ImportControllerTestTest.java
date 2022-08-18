package com.gongbo.excel.example.controller;

import cn.hutool.core.io.FileUtil;
import com.gongbo.excel.example.ServerApplication;
import com.gongbo.excel.example.view.ExportDemoView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author gongbo
 * @date 2022/8/17
 */
@SpringBootTest(classes = {ServerApplication.class})
@AutoConfigureMockMvc
class ImportControllerTestTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testImport() throws Exception {
        File file = ResourceUtils.getFile("classpath:import-test.xlsx");

        MockPart mockPart = new MockPart("file", FileUtil.readBytes(file));

        mockMvc.perform(multipart("/import/test-import?import=excel")
                        .part(mockPart))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(9));
    }

    @Test
    void testTemplate() throws Exception {
        mockMvc.perform(get("/import/test-template?import=template")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    List<ExportDemoView> exportDemoViews = readExcelFile(mvcResult, "Sheet1", READ_TO_EXPORT_DEMO_VIEW);
                    Assertions.assertEquals(exportDemoViews.size(), 0);
                });
    }

    @Test
    void testCustomTemplate() throws Exception {
        File file = ResourceUtils.getFile("classpath:templates/template-import.xlsx");
        //导出请求
        mockMvc.perform(get("/import/test-custom-template?import=template")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    //保存为文件
                    Path path = saveTempFile(mvcResult);
                    Assertions.assertEquals(file.length(), Files.size(path));

                    String contentType = mvcResult.getResponse().getHeader("Content-Type");
                    Assertions.assertEquals(contentType, "application/vnd.ms-excel;charset=UTF-8");

                    String contentDisposition = mvcResult.getResponse().getHeader("Content-Disposition");
                    String fileName = "自定义模板";
                    String value = "attachment;filename*=utf-8''" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20") + ".xlsx";
                    Assertions.assertEquals(contentDisposition, value);
                });
    }

    @Test
    void testImportTemplate() throws Exception {
        //导出请求
        mockMvc.perform(get("/import/test-import-template?import=template")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    List<ExportDemoView> exportDemoViews = readExcelFile(mvcResult, "Sheet1", READ_TO_EXPORT_DEMO_VIEW);
                    Assertions.assertEquals(exportDemoViews.size(), 0);
                });

        File file = ResourceUtils.getFile("classpath:import-test.xlsx");

        MockPart mockPart = new MockPart("file", FileUtil.readBytes(file));
        mockMvc.perform(multipart("/import/test-import-template?import=excel")
                        .part(mockPart)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(9));
    }
}