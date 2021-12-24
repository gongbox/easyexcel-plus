package com.gongbo.excel.export.adapter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExportAdapters {
    private static final List<ExportAdapter> ADAPTERS = new LinkedList<>();

    /**
     * 添加适配器
     *
     * @param adapter
     */
    public static void addAdapter(ExportAdapter adapter) {
        ADAPTERS.add(adapter);
    }

    /**
     * 获取所有适配器
     *
     * @return
     */
    public static Collection<ExportAdapter> getAdapters() {
        return ADAPTERS;
    }
}
