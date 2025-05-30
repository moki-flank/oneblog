package com.mojian.enums;


import lombok.Getter;

/**
 * @author: quequnlong
 * @date: 2024/12/29
 * @description:
 */
@Getter
public enum LoginTypeEnum {


    EMAIL("email", "邮箱登录"),

    QQ("qq", "QQ登录"),

    WEIBO("weibo", "微博登录"),

    GITEE("gitee", "码云登录"),

    WECHAT("wechat", "微信登录"),

    APPLET("applet", "小程序登录"),

    GITHUB("github", "github登录");


    private final String type;


    private final String desc;

    LoginTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

}
