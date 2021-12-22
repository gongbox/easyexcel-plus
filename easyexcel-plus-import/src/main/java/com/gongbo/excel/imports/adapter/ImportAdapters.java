package com.gongbo.excel.imports.adapter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImportAdapters {
    private static final List<ImportAdapter> ADAPTERS = new LinkedList<>();

    /**
     * 添加导入适配器
     *
     * @param adapter
     */
    public static void addAdapter(ImportAdapter adapter) {
        ADAPTERS.add( adapter);
    }

    /**
     * 获取所有导入适配器
     *
     * @return
     */
    public static Collection<ImportAdapter> getAdapters() {
        return ADAPTERS;
    }
}
