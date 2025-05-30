package com.mojian.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mojian.common.Constants;
import com.mojian.common.RedisConstants;
import com.mojian.common.Result;
import com.mojian.entity.SysNotice;
import com.mojian.mapper.SysNoticeMapper;
import com.mojian.service.HomeService;
import com.mojian.entity.SysWebConfig;
import com.mojian.mapper.SysWebConfigMapper;
import com.mojian.utils.IpUtil;
import com.mojian.utils.RedisUtil;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final SysWebConfigMapper sysWebConfigMapper;

    private final RedisUtil redisUtil;

    private final SysNoticeMapper noticeMapper;

    @Override
    public Result<SysWebConfig> getWebConfig() {
        LambdaQueryWrapper<SysWebConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.last("limit 1");
        SysWebConfig sysWebConfig = sysWebConfigMapper.selectOne(wrapper);

        //获取浏览量和访问量
        long blogViewsCount = 0;
        long visitorCount = 0;
        if (redisUtil.hasKey(RedisConstants.BLOG_VIEWS_COUNT)) {
            blogViewsCount = Long.parseLong(redisUtil.get(RedisConstants.BLOG_VIEWS_COUNT).toString());
        }
        if (redisUtil.hasKey(RedisConstants.UNIQUE_VISITOR_COUNT)) {
            visitorCount = Long.parseLong(redisUtil.get(RedisConstants.UNIQUE_VISITOR_COUNT).toString());
        }

        return Result.success(sysWebConfig).putExtra("blogViewsCount", blogViewsCount).putExtra("visitorCount", visitorCount);
    }

    @Override
    public JSONObject getHotSearch(String type) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("access-key", "f94be500c45148bc185be24a38c04ad3");
        paramMap.put("secret-key", "27563ca627d5db0d57e831ca4de0f75f");
        String url = "https://www.coderutil.com/api/resou/v1/" + type;
        String result= HttpUtil.get(url, paramMap);
        return com.alibaba.fastjson2.JSONObject.parseObject(result);
    }

    @Override
    public void report() {
        // 获取ip
        String ipAddress = IpUtil.getIp();
        // 通过浏览器解析工具类UserAgent获取访问设备信息
        UserAgent userAgent = IpUtil.getUserAgent(Objects.requireNonNull(IpUtil.getRequest()));
        Browser browser = userAgent.getBrowser();
        OperatingSystem operatingSystem = userAgent.getOperatingSystem();
        // 生成唯一用户标识
        String uuid = ipAddress + browser.getName() + operatingSystem.getName();
        String md5 = DigestUtils.md5DigestAsHex(uuid.getBytes());
        // 判断是否访问
        if (!redisUtil.sIsMember(RedisConstants.UNIQUE_VISITOR, md5)) {
            // 访客量+1
            redisUtil.increment(RedisConstants.UNIQUE_VISITOR_COUNT, 1);
            // 保存唯一标识
            redisUtil.sAdd(RedisConstants.UNIQUE_VISITOR, md5);
        }
        // 访问量+1
        redisUtil.increment(RedisConstants.BLOG_VIEWS_COUNT, 1);
    }

    @Override
    public Map<String, List<SysNotice>> getNotice() {

        List<SysNotice> sysNotices = noticeMapper.selectList(new LambdaQueryWrapper<SysNotice>()
                .eq(SysNotice::getIsShow, Constants.YES));
        return sysNotices.stream()
                .collect(Collectors.groupingBy(SysNotice::getPosition));
    }
}
