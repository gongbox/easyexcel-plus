package com.gongbo.excel.imports.param;

import com.gongbo.excel.common.utils.StringUtil;
import com.gongbo.excel.common.utils.Utils;
import lombok.*;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.Objects;

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

    public static ImportParam parse(HttpServletRequest request) {
        String imports = Utils.firstNotEmpty(() -> request.getParameter(IMPORT), () -> request.getHeader(IMPORT));

        if (StringUtil.isEmpty(imports)) {
            return null;
        }

        Type type = Type.of(imports);

        Objects.requireNonNull(type, MessageFormat.format("Import request parameter error:{0}", imports));

        return ImportParam.builder()
                .type(type)
                .build();
    }

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
