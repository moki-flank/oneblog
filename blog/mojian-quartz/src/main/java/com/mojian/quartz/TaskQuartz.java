package com.mojian.quartz;

import com.mojian.common.RedisConstants;
import com.mojian.entity.SysArticle;
import com.mojian.mapper.SysArticleMapper;
import com.mojian.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("task")
@RequiredArgsConstructor
public class TaskQuartz {

    private final RedisUtil redisUtil;

    private final SysArticleMapper articleMapper;

    public void neatMultipleParams(String s, Boolean b, Long l, Double d, Integer i) {
      //  System.out.println(StringUtils.format("执行多参方法： 字符串类型{}，布尔类型{}，长整型{}，浮点型{}，整形{}", s, b, l, d, i));
    }

    public void neatParams(String params) {
        System.out.println("执行有参方法：" + params);
    }

    public void neatNoParams() {
        System.out.println("执行无参方法");
    }


    /**
     * 定时同步阅读量
     */
    public void syncQuantity() {
        // 获取带阅读量的前缀key集合
        List<SysArticle> articles = new ArrayList<>();
        Map<Object, Object> map = redisUtil.hGetAll(RedisConstants.ARTICLE_QUANTITY);
        // 取出所有数据更新到数据库
        for (Map.Entry<Object, Object> stringEntry : map.entrySet()) {
            Object id = stringEntry.getKey();
            List<String> list = (List<String>) stringEntry.getValue();
            SysArticle article = SysArticle.builder()
                    .id(Long.parseLong(id.toString())).quantity(list.size())
                    .build();
            articles.add(article);
        }
        articleMapper.updateBatchQuantity(articles);
    }


}
