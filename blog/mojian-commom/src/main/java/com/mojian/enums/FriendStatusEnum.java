package com.mojian.enums;

import lombok.Getter;

/**
 * 友链状态枚举
 */
@Getter
public enum FriendStatusEnum {


    SHELF(0, "下架"),

    APPLY(1, "申请"),

    UP(2, "上架");

    private final Integer code;

    private final String desc;

    FriendStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}