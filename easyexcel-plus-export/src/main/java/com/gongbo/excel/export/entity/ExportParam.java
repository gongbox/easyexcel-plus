package com.gongbo.excel.export.entity;

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
     * 构建ExportParam对象
     *
     * @param request
     * @return
     */
    public static ExportParam parse(HttpServletRequest request) {
        String export = Utils.firstNotEmpty(() -> request.getParameter(ExportParam.EXPORT), () -> request.getHeader(ExportParam.EXPORT));

        if (StringUtil.isEmpty(export)) {
            return null;
        }

        Type type = Type.of(export);

        Objects.requireNonNull(type, MessageFormat.format("Export request parameter error:{0}", export));

        //获取exportTag参数
        String exportTag = Utils.firstNotEmpty(() -> request.getParameter(ExportParam.EXPORT_TAG), () -> request.getHeader(ExportParam.EXPORT_TAG));

        return ExportParam.builder()
                .exportTag(exportTag)
                .type(type)
                .build();
    }

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
