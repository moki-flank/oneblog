package com.mojian.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微博登录配置
 * @author: quequnlong
 * @date: 2024/12/30
 * @description:
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "weibo")
public class WeiboConfigProperties {

    /**
     * 微博appId
     */
    private String appId;

    /**
     * 微博appSecret
     */
    private String appSecret;

    /**
     * 微博回调域名
     */
    private String redirectUrl;



}

