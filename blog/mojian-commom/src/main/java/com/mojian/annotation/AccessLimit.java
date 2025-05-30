package com.mojian.annotation;

import io.swagger.annotations.ApiModelProperty;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: quequnlong
 * @date: 2024/12/28
 * @description: 限流注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimit {

    @ApiModelProperty(value = "访问次数")
    int count() default 10;

    @ApiModelProperty(value = "时间/单位秒")
    int time() default 60;
}
