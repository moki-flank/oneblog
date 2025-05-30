package com.mojian.config.properties;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author: quequnlong
 * @date: 2024/12/29
 * @description:
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wechat")
public class WechatProperties {

    @ApiModelProperty(value = "公众号appId")
    private String appId;

    @ApiModelProperty(value = "公众号appSecret")
    private String secret;

    @ApiModelProperty(value = "消息校验Token")
    private String token;
    @ApiModelProperty(value = "消息加解密密钥")
    private String aesKey;
}
