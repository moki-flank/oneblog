package com.mojian.enums;

import lombok.Getter;


@Getter
public enum NoticePosttionEnum {


    TOP("top", "顶部"),

    RIGHT("right", "右侧");

    private final String code;

    private final String desc;

    NoticePosttionEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
