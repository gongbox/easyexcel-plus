package com.gongbo.excel.imports.param;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportParam {

    public static final String IMPORT = "import";

    /**
     * 导出参数
     */
    private Type type;

    /**
     * 是否是获取导入模板
     */
    public boolean isTemplate() {
        return Type.IMPORT_TEMPLATE.equals(type);
    }

    /**
     * 是否是导入数据
     */
    public boolean isExcel() {
        return Type.IMPORT_EXCEL.equals(type);
    }

    @RequiredArgsConstructor
    public enum Type {
        IMPORT_TEMPLATE("template"),
        IMPORT_EXCEL("excel");

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
