# easy-excel-plus

<p align="center">
  为简化开发工作、提高生产率而生
</p>

<p align="center">
  <a href="https://search.maven.org/search?q=g:com.gongbo%20a:easy-excel-*">
    <img alt="maven" src="https://img.shields.io/maven-central/v/com.baomidou/mybatis-plus.svg?style=flat-square">
  </a>

  <a href="https://www.apache.org/licenses/LICENSE-2.0">
    <img alt="code style" src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square">
  </a>
</p>

# 简介 | Intro

EasyExcel 增强工具包 - 只做增强不做改变，简化导入，导出操作

技术讨论 QQ 群 ： 779290098

# 优点 | Advantages

- **无侵入**：EasyExcelPlus 在 EasyExcel 的基础上进行扩展，只做增强不做改变，引入 EasyExcelPlus 不会对您现有的 EasyExcel 构架产生任何影响，而且 EasyExcelPlus 支持所有 EasyExcel 原生的特性
- **依赖少**：仅仅依赖 EasyExcel


# Latest Version: [![Maven Central](https://img.shields.io/maven-central/v/io.github.gongbox/easy-excel-plus.svg)](https://search.maven.org/search?q=g:com.gongbo%20a:easy-excel-plus*)

``` xml
<dependency>
    <groupId>io.github.gongbox</groupId>
    <artifactId>easy-excel-plus</artifactId>
    <version>Latest Version</version>
</dependency>
```

# 期望 | Futures

> 欢迎提出更好的意见，帮助完善 EasyExcelPlus

# 版权 | License

[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)

# 关注我 | About Me

![简书](https://www.jianshu.com/u/9d2985772d9a "程序员日记")


#使用示例：
####模型类
```java
@Data
public class ExportDemoView {

    @ExcelProperty
    private String text = "text";

    @ExcelProperty
    private Integer integerValue = 12;

    @ExcelProperty
    private Float floatValue = 13.0f;

    @ExcelProperty
    private Double doubleValue = 213.4566;

    @ExcelProperty
    private BigDecimal bigDecimal = BigDecimal.TEN;

    @ExcelProperty
    private LocalDate localDate = LocalDate.now();

    @ExcelProperty
    private LocalDateTime localDateTime = LocalDateTime.now();

    @ExcelProperty
    private Date date = new Date();

    public static List<ExportDemoView> data() {
        return Stream.generate(ExportDemoView::new)
                .limit(new Random().nextInt(10))
                .collect(Collectors.toList());
    }
}
```
####配置文件
```yaml
server:
  port: 8888

excel-plus:
  export:
    responseClassName: com.gongbo.excel.example.result.Result
```

- 简单导出：
    ```java
    @GetMapping(value = "commonDefault")
    @EnableExport
    public Result<List<ExportDemoView>> commonDefault() {
        return Result.success(ExportDemoView.data());
    }
    ```
    - 查询地址：http://127.0.0.1:8888/export/commonDefault
    - 导出地址：http://127.0.0.1:8888/export/commonDefault?export=excel
- 导出到本地固定文件夹：
    ```java
    @GetMapping(value = "commonFile")
    @EnableExport(outputPath = "D:\\WorkDir\\temp\\file")
    public Result<List<ExportDemoView>> commonFile() {
        return Result.success(ExportDemoView.data());
    }
    ```
    - 查询地址：http://127.0.0.1:8888/export/commonFile
    - 导出地址：http://127.0.0.1:8888/export/commonFile?export=excel
- 导出-固定文件名称：
    ```java
    @GetMapping(value = "commonFile")
    @EnableExport(fileName = "通用导出1")
    public Result<List<ExportDemoView>> commonFile() {
        return Result.success(ExportDemoView.data());
    }
    ```
  - 查询地址：http://127.0.0.1:8888/export/commonFile
  - 导出地址：http://127.0.0.1:8888/export/commonFile?export=excel
- 导出-动态设置文件名称：
    ```java
    @GetMapping(value = "commonFile")
    @EnableExport(fileNameConvert = CustomFileNameConvert.class)
    public Result<List<ExportDemoView>> commonFile() {
        return Result.success(ExportDemoView.data());
    }
    public static class CustomFileNameConvert implements FileNameConvert {
        @Override
        public String apply(String fileName) {
            return LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        }
    }
    ```
    ```java
    @GetMapping(value = "commonFile")
    @EnableExport
    public Result<List<ExportDemoView>> commonFile() {
        if (ExportContextHolder.isExportExcel()) {
            ExportContextHolder.getContext().setFileName("业务中修改文件名称");
        }
        return Result.success(ExportDemoView.data());
    }
    ```
  - 查询地址：http://127.0.0.1:8888/export/commonFile
  - 导出地址：http://127.0.0.1:8888/export/commonFile?export=excel
- 导出-固定Sheet名称：
    ```java
    @GetMapping(value = "commonFile")
    @EnableExport(sheetName = "Sheet0")
    public Result<List<ExportDemoView>> commonFile() {
        return Result.success(ExportDemoView.data());
    }
    ```
  - 查询地址：http://127.0.0.1:8888/export/commonFile
  - 导出地址：http://127.0.0.1:8888/export/commonFile?export=excel
- 导出-动态设置Sheet名称：
    ```java
    @GetMapping(value = "commonFile")
    @EnableExport
    public Result<List<ExportDemoView>> commonFile() {
        if (ExportContextHolder.isExportExcel()) {
             ExportContextHolder.getContext().setSheetName("业务中修改Sheet名称");
        }
        return Result.success(ExportDemoView.data());
    }
    ```
  - 查询地址：http://127.0.0.1:8888/export/commonFile
  - 导出地址：http://127.0.0.1:8888/export/commonFile?export=excel
- 导出-字段过滤：
    ```java
    @GetMapping(value = "commonFile")
    @EnableExport(fieldFilter = CustomFieldFilter.class)
    public Result<List<ExportDemoView>> commonFile() {
        return Result.success(ExportDemoView.data());
    }
    public static class CustomFieldFilter implements FieldFilter {
        @Override
        public boolean predict(Field field) {
            return RandomUtil.randomBoolean();
        }
    }
    ```
  - 查询地址：http://127.0.0.1:8888/export/commonFile
  - 导出地址：http://127.0.0.1:8888/export/commonFile?export=excel
- 导出-设置导出文件格式：
    ```java
    @GetMapping(value = "commonFile")
    @EnableExport(excelType = ExcelType.XLS)
    public Result<List<ExportDemoView>> commonFile() {
        return Result.success(ExportDemoView.data());
    }
    ```
  - 查询地址：http://127.0.0.1:8888/export/commonFile
  - 导出地址：http://127.0.0.1:8888/export/commonFile?export=excel
- 导出-数据转换：
    ```java
    @GetMapping(value = "commonFile")
    @EnableExport(dataConvert = CustomExportDataConvert.class)
    public Result<List<ExportDemoView>> commonFile() {
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
  - 查询地址：http://127.0.0.1:8888/export/commonFile
  - 导出地址：http://127.0.0.1:8888/export/commonFile?export=excel
- 导出-同一接口多种导出方式：
    ```java
    @GetMapping(value = "commonFile")
    @EnableExport(tag = "excel_type_xls", excelType = ExcelType.XLS)
    @EnableExport(tag = "excel_type_xlsx", excelType = ExcelType.XLSX)
    public Result<List<ExportDemoView>> commonFile() {
        return Result.success(ExportDemoView.data());
    }
    ```
  - 查询地址：http://127.0.0.1:8888/export/commonFile
  - 导出地址：http://127.0.0.1:8888/export/commonFile?export=excel&export_tag=excel_type_xls
  - 导出地址：http://127.0.0.1:8888/export/commonFile?export=excel&export_tag=excel_type_xlsx
- 导出-模版导出（单个Sheet）：
    ```java
    @GetMapping(value = "commonFile")
    @EnableExport(template = "template1.xls", dataConvert = Template1DataConvert.class)
    public Result<List<ExportDemoView>> commonFile() {
        return Result.success(ExportDemoView.data());
    }
    public static class Template1DataConvert implements ExportDataConvert {
        @Override
        public List<?> convert(ExportContext exportContext, Object data) {
            Result<?> responseEntity = (Result<?>) data;

            ExportFillData exportFillData1 = ExportFillData.builder()
                    .data(responseEntity.getData())
                    .build();

            Map<String, String> map = new HashMap<>();

            map.put("name", "名称");
            map.put("date_start", LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
            map.put("date_end", LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
            ExportFillData exportFillData2 = ExportFillData.builder()
                    .data(map)
                    .build();

            return Lists.newArrayList(exportFillData1, exportFillData2);
        }
    }
    ```
  - 查询地址：http://127.0.0.1:8888/export/commonFile
  - 导出地址：http://127.0.0.1:8888/export/commonFile?export=excel
- 导出-模版导出（多个Sheet）：
    ```java
    @GetMapping(value = "commonFile")
    @EnableExport(template = "template2.xls", dataConvert = Template2DataConvert.class)
    public Result<List<ExportDemoView>> commonFile() {
        return Result.success(ExportDemoView.data());
    }
    public static class Template2DataConvert implements ExportDataConvert {
        @Override
        public List<?> convert(ExportContext exportContext, Object data) {
            Result<?> responseEntity = (Result<?>) data;

            ExportFillData exportFillData1 = ExportFillData.builder()
                    .sheetName("Sheet1")
                    .data(responseEntity.getData())
                    .build();

            Map<String, String> map = new HashMap<>();

            map.put("name", "名称");
            map.put("date_start", LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
            map.put("date_end", LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
            ExportFillData exportFillData2 = ExportFillData.builder()
                    .sheetNo(0)
                    .data(map)
                    .build();

            return Lists.newArrayList(exportFillData1, exportFillData2);
        }
    }
    ```
  - 查询地址：http://127.0.0.1:8888/export/commonFile
  - 导出地址：http://127.0.0.1:8888/export/commonFile?export=excel
    