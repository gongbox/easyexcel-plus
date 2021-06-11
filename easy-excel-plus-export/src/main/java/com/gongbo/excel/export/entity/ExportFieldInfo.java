package com.gongbo.excel.export.entity;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ExportFieldInfo {
    /**
     * 导出列名称
     */
    private String name;

    /**
     * 对应实体字段名称
     */
    private String fieldName;

    /**
     * 字段排序
     */
    private int order;

}
