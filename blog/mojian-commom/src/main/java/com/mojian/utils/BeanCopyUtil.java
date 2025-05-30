package com.mojian.utils;

/**
 * @author: quequnlong
 * @date: 2025/2/25
 * @description:
 */
public class BeanCopyUtil {

    /**
     * 对象拷贝
     * @param source
     * @param targetClass
     * @return
     * @param <S>
     * @param <T>
     */
    public static <S, T> T copyObj(S source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        T target = null;
        try {
            target = targetClass.newInstance();
            org.springframework.beans.BeanUtils.copyProperties(source, target);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return target;
    }

}
