package com.gongbo.excel.example.controller;

import com.gongbo.excel.example.result.Result;
import com.gongbo.excel.example.view.ExportDemoView;
import com.gongbo.excel.imports.annotations.EnableImport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 电量指标 前端控制器
 * </p>
 *
 * @author gongbo
 * @since 2021-01-04
 */
@Api(tags = "export")
@RestController
@RequestMapping(value = "/import")
@Validated
public class ImportControllerTest {

    @ApiOperation("test1")
    @RequestMapping(value = "test1", method = {RequestMethod.GET, RequestMethod.POST})
    @EnableImport
    public Result<ExportDemoView[]> test1(@RequestBody(required = false) ExportDemoView[] param) {
        return Result.success(param);
    }

    @ApiOperation("test2")
    @RequestMapping(value = "test2", method = {RequestMethod.GET, RequestMethod.POST})
    @EnableImport
    public Result<List<ExportDemoView>> test2(@RequestBody(required = false) List<ExportDemoView> param) {
        return Result.success(param);
    }

    @ApiOperation("test3")
    @RequestMapping(value = "test3", method = {RequestMethod.GET, RequestMethod.POST})
    @EnableImport
    public Result<Collection<ExportDemoView>> test3(@RequestBody(required = false) Collection<ExportDemoView> param) {
        return Result.success(param);
    }

    @ApiOperation("test4")
    @PostMapping(value = "test4")
    @EnableImport
    public Result<ExportDemoView[]> test4(@RequestBody(required = false) ExportDemoView[] param) {
        return Result.success(param);
    }

    @ApiOperation("test5")
    @GetMapping(value = "test5")
    @EnableImport(modelClass = ExportDemoView.class)
    public void test5() {
    }
}
