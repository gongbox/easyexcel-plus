package com.gongbo.excel.export.adapter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExportAdapters {
    private static final Map<String, ExportAdapter> ADAPTERS_MAP = new HashMap<>();

    public static void addAdapter(ExportAdapter adapter) {
        ADAPTERS_MAP.put(adapter.name(), adapter);
    }

    public static Collection<ExportAdapter> getAdapters() {
        return ADAPTERS_MAP.values();
    }

    public static ExportAdapter getAdapter(String name) {
        return ADAPTERS_MAP.get(name);
    }
}
