package com.gongbo.excel.export.entity;


import com.gongbo.excel.common.enums.ExcelType;
import com.gongbo.excel.common.utils.StringUtil;
import com.gongbo.excel.export.annotations.Export;
import com.gongbo.excel.export.config.ExportProperties;
import com.gongbo.excel.export.core.resulthandler.ResultHandler;
import com.gongbo.excel.export.param.ExportParam;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportContext {

    /**
     * 导出请求参数
     */
    private ExportParam exportParam;

    /**
     * 导出注解信息
     */
    private Export export;

    /**
     * 导出对应模型类
     */
    private Class<?> model;

    /**
     * 导出文件名
     */
    private String fileName;

    /**
     * 导出sheet名称
     */
    private String sheetName;

    /**
     * 导出模板文件名称
     */
    private String template;

    /**
     * excel文件格式
     */
    private ExcelType excelType;

    /**
     * 输出文件地址
     */
    private String outputPath;

    /**
     * 是否包含公式
     */
    private boolean formula;

    /**
     *
     */
    private boolean responseResult;

    /**
     * 导出字段信息
     */
    private List<ExportFieldInfo> fieldInfos;

    /**
     * 用户额外添加的信息
     */
    private Map<Object, Object> userContext;

    /**
     * 配置信息
     */
    private ExportProperties exportProperties;

    /**
     *
     */
    private ResultHandler resultHandler;

    /**
     * 是否输出目录
     *
     * @return
     */
    public boolean isOutputFile() {
        return StringUtil.isNotEmpty(outputPath);
    }


    /**
     * 添加填充数据
     *
     * @param exportFillData
     */
    public void addExportFillData(ExportFillData exportFillData) {
        listExportFillData().add(exportFillData);
    }

    /**
     * 获取填充数据
     *
     * @return
     */
    public List<ExportFillData> listExportFillData() {
        return (List<ExportFillData>) userContext.computeIfAbsent(exportProperties.getFillKey(), _ignored -> new ArrayList<>());
    }
}
