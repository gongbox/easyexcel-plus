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
     * @param e
     * @return
     */
    Object error(Throwable e);

    /**
     * @param result
     * @return
     */
    Object getData(Object result);
}
