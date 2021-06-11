package com.gongbo.excel.export.entity;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportInfo {

    private String fileName;

    private List<ExportFieldInfo> fieldInfos;

    /**
     * @param exportContext
     * @return
     */
    public static ExportInfo buildExportInfo(ExportContext exportContext) {
        return ExportInfo.builder()
                .fileName(exportContext.getFileName())
                .fieldInfos(exportContext.getFieldInfos())
                .build();
    }
}
