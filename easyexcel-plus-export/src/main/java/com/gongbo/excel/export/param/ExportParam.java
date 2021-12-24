package com.gongbo.excel.export.param;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportParam {

    public static final String EXPORT = "export";
    public static final String EXPORT_TAG = "export_tag";

    /**
     * 导出请求分组
     */
    private String exportTag;

    /**
     * 导出参数
     */
    private Type type;


    /**
     * 是否是导出Excel文件
     *
     * @return
     */
    public boolean isExcel() {
        return Type.EXPORT_EXCEL.equals(type);
    }

    @RequiredArgsConstructor
    public enum Type {
        EXPORT_EXCEL("excel");

        private final String value;

        public static Type of(String value) {
            for (Type type : values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            return null;
        }
    }
}
