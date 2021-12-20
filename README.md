# EasyExcelPlus

<p align="center">
  为简化开发工作、提高生产率而生
</p>

<p align="center">
  <a href="https://search.maven.org/search?q=g:io.github.gongbox%20AND%20a:easyexcel-plus*">
    <img alt="maven" src="https://img.shields.io/maven-central/v/io.github.gongbox/easyexcel-plus-boot-starter.svg?style=flat-square">
  </a>

  <a href="https://www.apache.org/licenses/LICENSE-2.0">
    <img alt="code style" src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square">
  </a>
</p>

# 简介

EasyExcel 增强工具包 - 只做增强不做改变，简化导入，导出操作

技术讨论 QQ 群 ： 779290098

# 优点 | Advantages

- **无侵入**：EasyExcelPlus 在 EasyExcel 基础上开发，只做增强，不做改变，引入 EasyExcelPlus 不会对您现有的 EasyExcel 构架产生任何影响，而且 EasyExcelPlus 支持所有 EasyExcel 原生的特性
- **易使用**：通过注解的方式配置导出，配置简单，快速开发导出功能
- **更简洁**：导出与查询合二为一，减少重复代码
- **损耗小**：EasyExcelPlus 对使用性能几乎没有影响


# Latest Version: [![Maven Central](https://img.shields.io/maven-central/v/io.github.gongbox/easyexcel-plus.svg)](https://search.maven.org/search?q=g:io.github.gongbox%20a:easyexcel-plus*)

``` xml
<dependency>
    <groupId>io.github.gongbox</groupId>
    <artifactId>easyexcel-plus-boot-starter</artifactId>
    <version>Latest Version</version>
</dependency>
```
## 使用示例：
下面是一个普通查询接口：
```java
@GetMapping(value = "test-normal")
public Result<List<ExportDemoView>> testNormal() {
    return Result.success(ExportDemoView.data());
}
```
返回数据如下：
> 演示地址：http://8.129.7.25/export/test-normal

若要实现导出excel，只需要在接口上增加注解@ExcelExport即可，如下所示：
```java
@GetMapping(value = "test-normal")
@ExcelExport
public Result<List<ExportDemoView>> testNormal() {
    return Result.success(ExportDemoView.data());
}
```
添加该注解后，接口依然正常查询，导出时只需要添加请求参数export=excel即可，如下所示：
> 演示地址：http://8.129.7.25/export/test-normal?export=excel

## 更多示例
EasyExcelPlus支持多种多样的自定义配置，比如设置导出文件名、文件格式，模版导出，导出数据转换等等。

### 环境说明
- 模型类：
  ```java
  @Data
  @ColumnWidth(12)
  @ContentRowHeight(18)
  public class ExportDemoView {
  
      @ExcelProperty("文本")
      private String text = RandomUtil.randomString(8);
  
      @ExcelProperty("整数")
      private Integer integerValue = RandomUtil.randomInt(10000);
  
      @ExcelProperty("浮点数")
      private Float floatValue = (float) RandomUtil.randomDouble(-10000, 10000);
  
      @ExcelProperty("长浮点数")
      private Double doubleValue = RandomUtil.randomDouble(-10000, 10000);
  
      @ExcelProperty("定点数")
      private BigDecimal bigDecimal = RandomUtil.randomBigDecimal(BigDecimal.valueOf(10_000));
  
      @ExcelProperty("日期")
      private LocalDate localDate = LocalDate.now();
  
      @ExcelProperty("日期时间")
      @ColumnWidth(20)
      private LocalDateTime localDateTime = LocalDateTime.now();
  
      @ExcelProperty("时间")
      @ColumnWidth(20)
      private Date date = new Date();
  
      public static List<ExportDemoView> data() {
          return Stream.generate(ExportDemoView::new)
                  .limit(RandomUtil.randomInt(1, 20))
                  .collect(Collectors.toList());
      }
  }
  ```
- 配置类:
    ```java
    @Configuration
    public class EasyExcelPlusConfig {
    
        @Bean
        public ResultHandler<Result> resultBuilder() {
            return new ResultHandler<Result>() {
                @Override
                public Class<Result> resultClass() {
                    return Result.class;
                }
    
                @Override
                public Object getData(Result result) {
                    return result.getData();
                }
            };
        }
    }
    ```
- 配置文件:
  ```yaml
  spring:
    application:
      name: export_demo

  easyexcel-plus:
    export:
      template-dir: classpath:exportTemplates/
  ```
### 使用

- **导出-设置导出文件名称**
    ```java
    @GetMapping(value = "test-fileName")
    @ExcelExport(fileName = "文件名称")
    public Result<List<ExportDemoView>> testFilename() {
        return Result.success(ExportDemoView.data());
    }
    ```
  >演示地址：http://8.129.7.25/export/test-fileName?export=excel
- **导出-动态设置文件名称**
    ```java
    @GetMapping(value = "test-fileName-convert")
    @ExcelExport(fileNameConvert = CustomFileNameConvert.class)
    public Result<List<ExportDemoView>> testFileNameConvert() {
        return Result.success(ExportDemoView.data());
    }

    public static class CustomFileNameConvert implements FileNameConvert {
        @Override
        public String apply(String fileName) {
            return LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        }
    }
    ```
  >演示地址：http://8.129.7.25/export/test-fileName-convert?export=excel
  
  或者
    ```java
    @GetMapping(value = "test-fileName-business")
    @ExcelExport
    public Result<List<ExportDemoView>> testFileNameBusiness() {
        if (ExportContextHolder.isExportExcel()) {
            ExportContextHolder.getContext().setFileName("动态文件名称");
        }
        return Result.success(ExportDemoView.data());
    }
    ```
  >演示地址：http://8.129.7.25/export/test-fileName-business?export=excel
- **导出-固定Sheet名称**
    ```java
    @GetMapping(value = "test-sheetName")
    @ExcelExport(sheetName = "Sheet0")
    public Result<List<ExportDemoView>> testSheetName() {
        return Result.success(ExportDemoView.data());
    }
    ```
  >演示地址：http://8.129.7.25/export/test-sheetName?export=excel
- **导出-动态设置Sheet名称**
    ```java
    @GetMapping(value = "test-sheetName-business")
    @ExcelExport
    public Result<List<ExportDemoView>> testSheetNameBusiness() {
        if (ExportContextHolder.isExportExcel()) {
            ExportContextHolder.getContext().setSheetName("业务中修改Sheet名称");
        }
        return Result.success(ExportDemoView.data());
    }
    ```
   >演示地址：http://8.129.7.25/export/test-sheetName-business?export=excel
- **导出到固定文件夹**
    ```java
    @GetMapping(value = "test-out-path")
    @ExcelExport(outputPath = "D:\\WorkDir\\temp\\file")
    public Result<List<ExportDemoView>> testOutPath() {
        return Result.success(ExportDemoView.data());
    }
    ```
  >演示地址：http://8.129.7.25/export/test-out-path?export=excel
- **导出-字段过滤**
    ```java
    @GetMapping(value = "test-filter")
    @ExcelExport(fieldFilter = CustomFieldFilter.class)
    public Result<List<ExportDemoView>> testFilter() {
        return Result.success(ExportDemoView.data());
    }

    public static class CustomFieldFilter implements FieldFilter {
        @Override
        public boolean predict(Field field) {
            return RandomUtil.randomBoolean();
        }
    }
    ```
  >演示地址：http://8.129.7.25/export/test-filter?export=excel
- **导出-设置导出文件格式**
    ```java
    @GetMapping(value = "test-excelType")
    @ExcelExport(excelType = ExcelType.XLS)
    public Result<List<ExportDemoView>> testExcelType() {
        return Result.success(ExportDemoView.data());
    }
    ```
  >演示地址：http://8.129.7.25/export/test-excelType?export=excel
- **导出-数据转换**
    ```java
    @GetMapping(value = "test-dataConvert")
    @ExcelExport(dataConvert = CustomExportDataConvert.class)
    public Result<List<ExportDemoView>> testDataConvert() {
        return Result.success(ExportDemoView.data());
    }

    public static class CustomExportDataConvert implements ExportDataConvert {
        @Override
        public List<?> convert(ExportContext exportContext, Object data) {
            Result<?> responseEntity = (Result<?>) data;
            List<ExportDemoView> list = (List<ExportDemoView>) responseEntity.getData();
            for (int i = 0; i < 20; i++) {
                list.add(new ExportDemoView());
            }
            return list;
        }
    }
    ```
  >演示地址：http://8.129.7.25/export/test-dataConvert?export=excel
- **导出-同一接口多种导出方式**
    ```java
    @GetMapping(value = "test-tag")
    @ExcelExport(tag = "xls", excelType = ExcelType.XLS)
    @ExcelExport(tag = "xlsx", excelType = ExcelType.XLSX)
    public Result<List<ExportDemoView>> testTag() {
        return Result.success(ExportDemoView.data());
    }
  ```
  同一接口可以添加多个注解，以实现支持多种导出，通过注解tag属性设置标签，导出时，需要使用参数export_tag指定标签。
  > 演示地址，导出XLS ：http://8.129.7.25/export/test-tag?export=excel&export_tag=xls
  
  > 演示地址，导出XLSX：http://8.129.7.25/export/test-tag?export=excel&export_tag=xlsx
- **导出-简单模版导出**
    ```java
    /**
     * 导出-简单模版导出
     */
    @GetMapping(value = "test-template-simple")
    @ExcelExport(template = "template-simply.xlsx")
    public Result<List<ExportDemoView>> testTemplateSimple() {
        return Result.success(ExportDemoView.data());
    }
    ```
  >演示地址：http://8.129.7.25/export/test-template-simple?export=excel
- **导出-模版导出（单个Sheet）**
    ```java
    @GetMapping(value = "test-template-single-sheet")
    @ExcelExport(template = "template-single-sheet.xlsx", dataConvert = TemplateSingleSheetDataConvert.class)
    public Result<List<ExportDemoView>> testTemplateSingleSheet() {
        return Result.success(ExportDemoView.data());
    }

    public static class TemplateSingleSheetDataConvert implements ExportDataConvert {
        @Override
        public List<?> convert(ExportContext exportContext, Object data) {
            Result<?> responseEntity = (Result<?>) data;

            ExportFillData exportFillData1 = ExportFillData.builder()
                    .data(responseEntity.getData())
                    .build();

            Map<String, String> map = new HashMap<>();
            map.put("name", "名称");
            map.put("date", LocalDate.now().format(Times.Formatter.DEFAULT_DATE));
            ExportFillData exportFillData2 = ExportFillData.builder()
                    .data(map)
                    .build();

            return Lists.newArrayList(exportFillData1, exportFillData2);
        }
    }
    ```
  >演示地址：http://8.129.7.25/export/test-template-single-sheet?export=excel
- **导出-模版导出（多个Sheet）**
    ```java
    @GetMapping(value = "test-template-much-sheet")
    @ExcelExport(template = "template-much-sheet.xlsx", dataConvert = TemplateMuchSheetDataConvert.class)
    public Result<List<ExportDemoView>> testTemplateMuchSheet() {
        return Result.success(ExportDemoView.data());
    }

    public static class TemplateMuchSheetDataConvert implements ExportDataConvert {
        @Override
        public List<?> convert(ExportContext exportContext, Object data) {
            Result<?> responseEntity = (Result<?>) data;

            Map<String, String> map = new HashMap<>();

            map.put("name", "名称");
            map.put("date", LocalDate.now().format(Times.Formatter.DEFAULT_DATE));
            ExportFillData exportFillData2 = ExportFillData.builder()
                    .sheetName("Sheet1")
                    .data(map)
                    .build();

            ExportFillData exportFillData1 = ExportFillData.builder()
                    .sheetName("Sheet2")
                    .data(responseEntity.getData())
                    .build();

            return Lists.newArrayList(exportFillData1, exportFillData2);
        }
    }
    ```
   >演示地址：http://8.129.7.25/export/test-template-much-sheet?export=excel
- **导出-模版导出（公式）**
    ```java
    @GetMapping(value = "test-template-formula")
    @ExcelExport(template = "template-formula.xls", dataConvert = TemplateFormulaDataConvert.class)
    public Result<List<ExportDemoView>> testTemplateFormula() {
        return Result.success(ExportDemoView.data());
    }

    public static class TemplateFormulaDataConvert implements ExportDataConvert {
        @Override
        public List<?> convert(ExportContext exportContext, Object data) {
            Result<?> responseEntity = (Result<?>) data;
            Collection<?> list = (Collection<?>) responseEntity.getData();

            ExportFillData exportFillData1 = ExportFillData.builder()
                    .fillConfig(FillConfig.builder().forceNewRow(true).build())
                    .data(new FillWrapper("data", list))
                    .build();

            int start = 1;
            int end = start + (CollUtil.isEmpty(list) ? 0 : list.size() - 1);

            Map<String, Object> constantMap2 = new HashMap<>();
            constantMap2.put("data_end", end);

            ExportFillData exportFillData2 = ExportFillData.builder()
                    .data(constantMap2)
                    .build();

            return Lists.newArrayList(exportFillData1, exportFillData2);
        }
    }
    ```
  >演示地址：http://8.129.7.25/export/test-template-formula?export=excel
- **导入-模板下载**
    ```java
    @GetMapping(value = "test-template")
    @ExcelImport(modelClass = ExportDemoView.class)
    public void testTemplate() {
    }
   ```
  >演示地址：
  > 导入-模版下载：http://8.129.7.25/import/test-template?import=template
- **导入-导入**
    ```java
    @PostMapping(value = "test-import")
    @ExcelImport
    public Result<ExportDemoView[]> testImport(@RequestBody(required = false) ExportDemoView[] param) {
        return Result.success(param);
    }
   ```
  ![img_1.png](img_1.png)
  >演示地址：
  > 导入-模版下载：http://8.129.7.25/import/test-import?import=excel
- **导入-模板下载、数据导入**
    ```java
    @RequestMapping(value = "test-import-template", method = {RequestMethod.GET, RequestMethod.POST})
    @ExcelImport
    public Result<ExportDemoView[]> testImportTemplate(@RequestBody(required = false) ExportDemoView[] param) {
        return Result.success(param);
    }
   ```
  ![img_1.png](img_1.png)
  >演示地址： 导入-模版下载：http://8.129.7.25/import/test-import?import=template
    
  >演示地址： 导入-数组参数：http://8.129.7.25/import/test-import?import=excel
# 期望 | Futures

> 欢迎提出更好的意见，帮助完善 EasyExcelPlus

# 版权 | License

[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)

# 关注我 | About Me

[简书](https://www.jianshu.com/u/9d2985772d9a)

