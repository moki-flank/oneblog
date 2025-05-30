package com.mojian.service;

/**
 * @author: quequnlong
 * @date: 2025/2/8
 * @description:
 */
public interface SignService {
    /**
     * 签到
     * @return
     */
    Boolean sign();

    /**
     * 是否签到
     * @return
     */
    Boolean isSignedToday();

    /**
     * 获取累计签到天数
     * @return
     */
    Long getCumulativeSignDays();

    /**
     * 获取连续签到天数
     * @return
     */
    int getConsecutiveSignDays();
}
