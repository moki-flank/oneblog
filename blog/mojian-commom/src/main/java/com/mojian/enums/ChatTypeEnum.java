package com.mojian.enums;

import lombok.Getter;

/**
 * @author: quequnlong
 * @date: 2025/2/25
 * @description: 聊天消息类型枚举
 */
@Getter
public enum ChatTypeEnum {

    TEXT("text"),

    IMAGE("image"),

    VIDEO("audio"),

    FILE("file");

    private final String type;

    ChatTypeEnum(String type) {
        this.type = type;
    }

}
