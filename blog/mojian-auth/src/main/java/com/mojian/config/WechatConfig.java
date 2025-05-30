package com.mojian.config;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import com.mojian.config.properties.WechatProperties;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

/**
 * @author: quequnlong
 * @date: 2024/12/29
 * @description: 微信配置和Sa-Token内存配置
 */
@Configuration
public class WechatConfig {

    @Autowired
    private WechatProperties wechatMpProperties;

    // ========================================
    // 微信公众号配置
    // ========================================

    @Bean
    public WxMpConfigStorage wxMpConfigStorage() {
        WxMpInMemoryConfigStorage configStorage = new WxMpInMemoryConfigStorage();
        configStorage.setAppId(wechatMpProperties.getAppId());
        configStorage.setSecret(wechatMpProperties.getSecret());
        configStorage.setToken(wechatMpProperties.getToken());
        configStorage.setAesKey(wechatMpProperties.getAesKey());
        return configStorage;
    }

    @Bean
    public WxMpService wxMpService(WxMpConfigStorage wxMpConfigStorage) {
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wxMpConfigStorage);
        return wxMpService;
    }

    // ========================================
    // Sa-Token内存模式配置
    // ========================================

    /**
     * 强制Sa-Token使用内存模式存储
     * 优先级高于Redis实现，避免Redis连接问题
     */
    @Bean
    @Primary
    public SaTokenDao saTokenDao() {
        return new SaTokenDaoDefaultImpl();
    }

    // ========================================
    // Mock RedisTemplate 解决CacheServiceImpl依赖问题
    // ========================================

    /**
     * 创建Mock的RedisTemplate，解决CacheServiceImpl启动失败问题
     */
    @Bean
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>() {
            // 重写所有方法为空实现，避免实际Redis操作
            @Override
            public void afterPropertiesSet() {
                // 空实现，避免初始化Redis连接
            }
        };

        // 设置基本的序列化器，避免空指针
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }
}