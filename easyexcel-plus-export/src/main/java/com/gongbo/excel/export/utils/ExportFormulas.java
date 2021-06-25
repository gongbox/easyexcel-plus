package com.gongbo.excel.export.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExportFormulas {

    /**
     * 求平均值公式
     */
    private static final String AVERAGE_FORMULA_FORMAT = "=IF(ISERROR(AVERAGE(%s:%s)),\"\",AVERAGE(%s:%s))";

    /**
     * 求和公式
     */
    private static final String SUM_FORMULA_FORMAT = "=IF(ISERROR(SUM(%s:%s)),\"\",SUM(%s:%s))";

    /**
     * 生成求平均值公式
     *
     * @param column 所在列
     * @param start  行起始坐标
     * @param end    行结束坐标
     */
    public static String averageColumnFormula(String column, int start, int end) {
        String columnStart = column + start;
        String columnEnd = column + end;

        return String.format(AVERAGE_FORMULA_FORMAT, columnStart, columnEnd, columnStart, columnEnd);
    }

    /**
     * 生成求平均值公式
     *
     * @param row   所在行
     * @param start 列起始坐标
     * @param end   列结束坐标
     */
    public static String averageRowFormula(int row, String start, String end) {
        String rowStart = row + start;
        String rowEnd = row + end;

        return String.format(AVERAGE_FORMULA_FORMAT, rowStart, rowEnd, rowStart, rowEnd);
    }

    /**
     * 生成求和值公式
     *
     * @param column 所在列
     * @param start  行起始坐标
     * @param end    行结束坐标
     */
    public static String sumColumnFormula(String column, int start, int end) {
        String columnStart = column + start;
        String columnEnd = column + end;

        return String.format(SUM_FORMULA_FORMAT, columnStart, columnEnd, columnStart, columnEnd);
    }

}
