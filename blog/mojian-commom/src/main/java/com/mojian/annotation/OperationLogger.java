package com.mojian.annotation;


import io.swagger.annotations.ApiModel;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiModel(value = "操作日志注解")
public @interface OperationLogger {

    /**
     * 业务名称
     */
    String value() default "";

    /**
     * 是否将当前日志记录到数据库中
     */
    boolean save() default true;
}
