package com.gongbo.excel.common.result;

public interface ResultHandler {

    /**
     * @param result
     * @return
     */
    boolean check(Object result);

    /**
     * @param data
     * @return
     */
    Object success(Object data);

    /**
     * @param result
     * @return
     */
    Object getData(Object result);
}
