package com.gongbo.excel.example.controller;

import com.gongbo.excel.example.result.Result;
import com.gongbo.excel.example.view.ExportDemoView;
import com.gongbo.excel.imports.annotations.Import;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 电量指标 前端控制器
 * </p>
 *
 * @author gongbo
 * @since 2021-01-04
 */
//@Api(tags = "import")
@RestController
@RequestMapping(value = "/import")
@Validated
public class ImportControllerTest {

    /**
     * 导入-数据导入
     * RequestBody注解required属性 必须设置为false
     * 参数可以是数组、集合
     */
    @PostMapping(value = "test-import")
    @Import
    public Result<ExportDemoView[]> testImport(@RequestBody(required = false) ExportDemoView[] param) {
        return Result.success(param);
    }

    /**
     * 导入-模版下载
     */
    @GetMapping(value = "test-template")
    @Import(model = ExportDemoView.class)
    public void testTemplate() {
    }

    /**
     * 导入-自定义模版下载
     */
    @GetMapping(value = "test-custom-template")
    @Import(model = ExportDemoView.class, template = "template-import.xlsx", templateFilename = "自定义模板")
    public void testCustomTemplate() {
    }

    /**
     * 导入-数据导入、模版下载
     * RequestBody注解required属性 必须设置为false
     * 必须支持GET、POST请求
     * 参数可以是数组、集合
     */
    @RequestMapping(value = "test-import-template", method = {RequestMethod.GET, RequestMethod.POST})
    @Import
    public Result<ExportDemoView[]> testImportTemplate(@RequestBody(required = false) ExportDemoView[] param) {
        return Result.success(param);
    }

}
