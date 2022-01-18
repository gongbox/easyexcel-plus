package com.gongbo.excel.example.constants;

import com.gongbo.excel.adapter.easyexcel.converter.ExcelValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GenderEnum {
    UNKNOWN(0, "未知"),
    MAN(1, "男"),
    WOMAN(2, "女");

    @ExcelValue(ExcelValue.Support.READ)
    private final Integer value;

    @ExcelValue(ExcelValue.Support.WRITE)
    private final String name;

    public static GenderEnum valueOf(int value) {
        for (GenderEnum genderEnum : values()) {
            if (genderEnum.value == value) {
                return genderEnum;
            }
        }
        return null;
    }
}
