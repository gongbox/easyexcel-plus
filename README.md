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

导入、导出通用工具包 - 简化导入，导出开发

技术讨论 QQ 群 ： 779290098

# 优点 | Advantages

- **易使用**：只需极少的配置，便可接入
- **更简洁**：通过在原有的查询（批量新增）接口上添加相关注解即可实现导出(导入)功能，不需要为导出单独写一份代码，直接复用相应的查询接口即可，开发更简单，维护更方便
- **更规范**：查询与导出（批量新增与导入）使用同一请求地址，你只需要知道查询接口请求地址，便能进行导出
- **损耗小**：对使用性能几乎没有影响  
- **可扩展**：默认已支持使用阿里EasyExcel进行导入、导出，你还可以通过添加适配器接入的方式使用其他导入、导出工具

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

若要实现导出excel，只需要在接口上增加注解@Export即可，如下所示：
```java
@GetMapping(value = "test-normal")
@Export
public Result<List<ExportDemoView>> testNormal() {
    return Result.success(ExportDemoView.data());
}
```
添加该注解后，接口便同时支持查询、导出，不影响原有的查询。若要导出，则只需要添加请求参数export=excel即可，此时导出数据与查询结果一致，如下所示：
> 演示地址（请复制地址到浏览器地址栏，按回车健访问）：http://8.129.7.25/export/test-normal?export=excel

## 更多示例
EasyExcelPlus支持多种多样的自定义配置，比如设置导出文件名、文件格式，模版导出，导出数据转换等等。

查看示例工程源码，请前往：https://github.com/gongbox/easyexcel-plus-example

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
  
      @ExcelProperty(value = "性别", converter = DefaultEnumConvert.class)
      private GenderEnum gender = GenderEnum.valueOf(RandomUtil.randomInt(3));
  
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
      public ResultHandler resultBuilder() {
          return new DefaultResultHandler() {
              @Override
              public Class<?> resultClass() {
                  return Result.class;
              }
              @Override
              public Object getResultData(Object result) {
                  if (result instanceof Result) {
                      return ((Result<?>) result).getData();
                  }
                  return super.getResultData(result);
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

  server:
    port: 80
  #以下配置项可根据需要配置，或者不配置使用默认值即可
  easyexcel-plus:
    export:
      #默认导出Sheet名称，不配则取默认值：Sheet1
      default-sheet-name: Sheet1
      #模板文件路径，不配则取默认值：空
      template-dir: classpath:templates
      #默认导出文件格式，不配则取默认值：xlsx
      default-excel-type: xlsx
      #默认导出方式，不配则取默认值：easy_excel
      default-export-by: easy_excel
    import:
      #默认导入读取的Sheet名称，不配则取默认值：Sheet1
      default-sheet-name: Sheet1
      #模板文件路径，不配则取默认值：空
      template-dir: classpath:templates
      #默认导入方式，不配则取默认值：easy_excel
      default-import-by: easy_excel
      #读取excel超时时间（单位ms），不设置或设置为0时无读取时间限制
      read-timeout: 60000
  ```
### 使用

- **导出-设置导出文件名称**
    ```java
    @GetMapping(value = "test-fileName")
    @Export(fileName = "文件名称")
    public Result<List<ExportDemoView>> testFilename() {
        return Result.success(ExportDemoView.data());
    }
    ```
  >演示地址：http://8.129.7.25/export/test-fileName?export=excel
- **导出-动态设置文件名称**
    ```java
    @GetMapping(value = "test-fileName-convert")
    @Export(fileNameConvert = CustomFileNameConvert.class)
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
    @Export
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
    @Export(sheetName = "Sheet0")
    public Result<List<ExportDemoView>> testSheetName() {
        return Result.success(ExportDemoView.data());
    }
    ```
  >演示地址：http://8.129.7.25/export/test-sheetName?export=excel
- **导出-动态设置Sheet名称**
    ```java
    @GetMapping(value = "test-sheetName-business")
    @Export
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
    @Export(outputPath = "D:\\WorkDir\\temp\\file")
    public Result<List<ExportDemoView>> testOutPath() {
        return Result.success(ExportDemoView.data());
    }
    ```
  >演示地址：http://8.129.7.25/export/test-out-path?export=excel
- **导出-字段过滤**
    ```java
    @GetMapping(value = "test-filter")
    @Export(fieldFilter = CustomFieldFilter.class)
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
    @Export(excelType = ExcelType.XLS)
    public Result<List<ExportDemoView>> testExcelType() {
        return Result.success(ExportDemoView.data());
    }
    ```
  >演示地址：http://8.129.7.25/export/test-excelType?export=excel
- **导出-数据转换**
    ```java
    @GetMapping(value = "test-dataConvert")
    @Export(dataConvert = CustomExportDataConvert.class)
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
    @Export(tag = "xls", excelType = ExcelType.XLS)
    @Export(tag = "xlsx", excelType = ExcelType.XLSX)
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
    @Export(template = "template-simply.xlsx")
    public Result<List<ExportDemoView>> testTemplateSimple() {
        return Result.success(ExportDemoView.data());
    }
    ```
  >演示地址：http://8.129.7.25/export/test-template-simple?export=excel
- **导出-模版导出（单个Sheet）**
    ```java
    @GetMapping(value = "test-template-single-sheet")
    @Export(template = "template-single-sheet.xlsx", dataConvert = TemplateSingleSheetDataConvert.class)
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
    @Export(template = "template-much-sheet.xlsx", dataConvert = TemplateMuchSheetDataConvert.class)
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
    @Export(template = "template-formula.xls", dataConvert = TemplateFormulaDataConvert.class)
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
- **导出-无包装类**
  ```java
    /**
     * 导出-简单导出(直接返回数组)
     */
    @GetMapping(value = "test-normal-array")
    @Export
    public ExportDemoView[] testNormalData() {
        return ExportDemoView.data().toArray(new ExportDemoView[0]);
    }

    /**
     * 导出-简单导出(直接返回集合)
     */
    @GetMapping(value = "test-normal-list")
    @Export
    public List<ExportDemoView> testNormalList() {
        return ExportDemoView.data();
    }

    /**
     * 导出-简单导出(直接返回迭代器)
     */
    @GetMapping(value = "test-normal-iterable")
    @Export
    public Iterable<ExportDemoView> testNormalIterable() {
        return ExportDemoView.data();
    }
  ```
  >演示地址：http://8.129.7.25/export/test-normal-array?export=excel
  
  >演示地址：http://8.129.7.25/export/test-normal-list?export=excel
  
  >演示地址：http://8.129.7.25/export/test-normal-iterable?export=excel
- **导入-模板下载**
    ```java
    @GetMapping(value = "test-template")
    @Import(modelClass = ExportDemoView.class)
    public void testTemplate() {
    }
   ```
  >演示地址：
  > 导入-模版下载：http://8.129.7.25/import/test-template?import=template
- **导入-自定义模版下载**
    ```java
    @GetMapping(value = "test-custom-template")
    @Import(modelClass = ExportDemoView.class, template = "template-import.xlsx", templateFilename = "自定义模板")
    public void testCustomTemplate() {
    }
   ```
  >演示地址：
  > 导入-自定义模版下载：http://8.129.7.25/import/test-custom-template?import=template
- **导入-导入**
    ```java
    @PostMapping(value = "test-import")
    @Import
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
    @Import
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

