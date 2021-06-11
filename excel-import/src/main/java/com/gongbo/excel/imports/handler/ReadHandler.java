package com.gongbo.excel.imports.handler;


import com.gongbo.excel.imports.entity.ImportContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * 导出执行
 */
public interface ReadHandler {

    CompletableFuture<Collection<?>> read(ImportContext importContext, InputStream inputStream) throws IOException;
}
