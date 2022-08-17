package com.gongbo.excel.export.entity;


import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportFillData {

    /**
     * 填充所有Sheet，默认false，为true时，将对所有sheet填充，此时sheetNo/sheetName将不起作用
     */
    @Builder.Default
    private boolean fillAllSheet = false;

    /**
     * 填充的sheet，从0开始
     */
    private Integer sheetNo;

    /**
     * 填充的sheet
     */
    private String sheetName;

    /**
     * 填充配置
     */
    private Object fillConfig;

    /**
     * 填充数据 map/list/FillWrapper等类型
     */
    private Object data;

    /**
     * @param data
     */
    public ExportFillData(Object data) {
        this(data, false);
    }


    /**
     * @param data
     * @param fillAllSheet
     */
    public ExportFillData(Object data, boolean fillAllSheet) {
        this.fillAllSheet = fillAllSheet;
        this.data = data;
    }
}
