package com.mojian.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 内存版Redis工具类
 * 使用ConcurrentHashMap模拟Redis功能
 */
@Slf4j
@Component
public class RedisUtil {

    // 主存储容器
    private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();

    // 过期时间存储
    private final ConcurrentHashMap<String, Long> expireMap = new ConcurrentHashMap<>();

    // Hash结构存储
    private final ConcurrentHashMap<String, Map<Object, Object>> hashCache = new ConcurrentHashMap<>();

    // List结构存储
    private final ConcurrentHashMap<String, List<Object>> listCache = new ConcurrentHashMap<>();

    // Set结构存储
    private final ConcurrentHashMap<String, Set<Object>> setCache = new ConcurrentHashMap<>();

    /**
     * 检查key是否过期并清理
     */
    private boolean isExpired(String key) {
        Long expireTime = expireMap.get(key);
        if (expireTime != null && System.currentTimeMillis() > expireTime) {
            // 过期了，清理所有相关缓存
            cache.remove(key);
            expireMap.remove(key);
            hashCache.remove(key);
            listCache.remove(key);
            setCache.remove(key);
            return true;
        }
        return false;
    }

    /**
     * 设置缓存
     */
    public void set(String key, Object value) {
        cache.put(key, value);
        log.debug("内存缓存设置: {} = {}", key, value);
    }

    /**
     * 设置缓存并设置过期时间
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        cache.put(key, value);
        long expireTime = System.currentTimeMillis() + unit.toMillis(timeout);
        expireMap.put(key, expireTime);
        log.debug("内存缓存设置(带过期): {} = {}, 过期时间: {}", key, value, expireTime);
    }

    /**
     * 获取缓存
     */
    public Object get(String key) {
        if (isExpired(key)) {
            return null;
        }
        return cache.get(key);
    }

    /**
     * 删除缓存
     */
    public Boolean delete(String key) {
        cache.remove(key);
        expireMap.remove(key);
        hashCache.remove(key);
        listCache.remove(key);
        setCache.remove(key);
        log.debug("内存缓存删除: {}", key);
        return true;
    }

    /**
     * 批量删除缓存
     */
    public Long delete(Collection<String> keys) {
        long count = 0;
        for (String key : keys) {
            if (delete(key)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 设置过期时间
     */
    public void expire(String key, long timeout, TimeUnit unit) {
        if (cache.containsKey(key)) {
            long expireTime = System.currentTimeMillis() + unit.toMillis(timeout);
            expireMap.put(key, expireTime);
        }
    }

    /**
     * 获取过期时间
     */
    public Long getExpire(String key, TimeUnit unit) {
        Long expireTime = expireMap.get(key);
        if (expireTime != null) {
            long remaining = expireTime - System.currentTimeMillis();
            return remaining > 0 ? unit.convert(remaining, TimeUnit.MILLISECONDS) : -1;
        }
        return -1L;
    }

    /**
     * 判断key是否存在
     */
    public Boolean hasKey(String key) {
        if (isExpired(key)) {
            return false;
        }
        return cache.containsKey(key) || hashCache.containsKey(key) ||
                listCache.containsKey(key) || setCache.containsKey(key);
    }

    /**
     * 按delta递增
     */
    public void increment(String key, long delta) {
        Object value = get(key);
        if (value instanceof Number) {
            long newValue = ((Number) value).longValue() + delta;
            set(key, newValue);
        } else {
            set(key, delta);
        }
    }

    /**
     * 按delta递减
     */
    public Long decrement(String key, long delta) {
        Object value = get(key);
        long newValue;
        if (value instanceof Number) {
            newValue = ((Number) value).longValue() - delta;
        } else {
            newValue = -delta;
        }
        set(key, newValue);
        return newValue;
    }

    // ========================================
    // Hash 结构操作
    // ========================================

    /**
     * 获取Hash结构中的属性
     */
    public Object hGet(String key, String hashKey) {
        if (isExpired(key)) {
            return null;
        }
        Map<Object, Object> hash = hashCache.get(key);
        return hash != null ? hash.get(hashKey) : null;
    }

    /**
     * 向Hash结构中放入一个属性
     */
    public void hSet(String key, String hashKey, Object value) {
        hashCache.computeIfAbsent(key, k -> new ConcurrentHashMap<>()).put(hashKey, value);
    }

    /**
     * 向Hash结构中放入一个属性并设置过期时间
     */
    public void hSet(String key, String hashKey, Object value, long timeout, TimeUnit unit) {
        hSet(key, hashKey, value);
        expire(key, timeout, unit);
    }

    /**
     * 直接获取整个Hash结构
     */
    public Map<Object, Object> hGetAll(String key) {
        if (isExpired(key)) {
            return new HashMap<>();
        }
        Map<Object, Object> hash = hashCache.get(key);
        return hash != null ? new HashMap<>(hash) : new HashMap<>();
    }

    /**
     * 直接设置整个Hash结构
     */
    public void hSetAll(String key, Map<Object, Object> map) {
        hashCache.put(key, new ConcurrentHashMap<>(map));
    }

    /**
     * 删除Hash结构中的属性
     */
    public void hDel(String key, Object... hashKeys) {
        Map<Object, Object> hash = hashCache.get(key);
        if (hash != null) {
            for (Object hashKey : hashKeys) {
                hash.remove(hashKey);
            }
        }
    }

    /**
     * 判断Hash结构中是否有该属性
     */
    public Boolean hHasKey(String key, String hashKey) {
        if (isExpired(key)) {
            return false;
        }
        Map<Object, Object> hash = hashCache.get(key);
        return hash != null && hash.containsKey(hashKey);
    }

    /**
     * Hash结构增加
     */
    public void hIncr(String key, String hashKey, Long delta) {
        Map<Object, Object> hash = hashCache.computeIfAbsent(key, k -> new ConcurrentHashMap<>());
        Object value = hash.get(hashKey);
        long newValue = (value instanceof Number) ? ((Number) value).longValue() + delta : delta;
        hash.put(hashKey, newValue);
    }

    /**
     * Hash结构减少
     */
    public void hDecr(String key, String hashKey, Long delta) {
        hIncr(key, hashKey, -delta);
    }

    // ========================================
    // List 结构操作
    // ========================================

    /**
     * List结构中添加属性
     */
    public Long lPush(String key, Object value) {
        List<Object> list = listCache.computeIfAbsent(key, k -> Collections.synchronizedList(new ArrayList<>()));
        list.add(value);
        return (long) list.size();
    }

    /**
     * List结构中添加属性
     */
    public Long lPushAll(String key, Object... values) {
        List<Object> list = listCache.computeIfAbsent(key, k -> Collections.synchronizedList(new ArrayList<>()));
        Collections.addAll(list, values);
        return (long) list.size();
    }

    /**
     * List结构中获取属性
     */
    public List<Object> lRange(String key, long start, long end) {
        if (isExpired(key)) {
            return new ArrayList<>();
        }
        List<Object> list = listCache.get(key);
        if (list == null) {
            return new ArrayList<>();
        }

        int size = list.size();
        int startIndex = (int) Math.max(0, start);
        int endIndex = end == -1 ? size : (int) Math.min(size, end + 1);

        if (startIndex >= size || startIndex >= endIndex) {
            return new ArrayList<>();
        }

        return new ArrayList<>(list.subList(startIndex, endIndex));
    }

    // ========================================
    // Set 结构操作
    // ========================================

    /**
     * Set结构添加属性
     */
    public void sAdd(String key, Object... values) {
        Set<Object> set = setCache.computeIfAbsent(key, k -> Collections.synchronizedSet(new HashSet<>()));
        Collections.addAll(set, values);
    }

    public Boolean sIsMember(String key, Object value) {
        if (isExpired(key)) {
            return false;
        }
        Set<Object> set = setCache.get(key);
        return set != null && set.contains(value);
    }

    /**
     * Set结构获取所有属性
     */
    public Set<Object> sMembers(String key) {
        if (isExpired(key)) {
            return new HashSet<>();
        }
        Set<Object> set = setCache.get(key);
        return set != null ? new HashSet<>(set) : new HashSet<>();
    }

    /**
     * Set结构删除
     */
    public void sRemove(String key, Object... values) {
        Set<Object> set = setCache.get(key);
        if (set != null) {
            for (Object value : values) {
                set.remove(value);
            }
        }
    }

    // ========================================
    // ZSet 结构操作（简化实现）
    // ========================================

    /**
     * 向ZSet结构中添加属性
     */
    public Boolean zAdd(String key, Object value, double score) {
        // 简化实现：使用Map存储
        Map<Object, Object> zset = hashCache.computeIfAbsent(key + ":zset", k -> new ConcurrentHashMap<>());
        zset.put(value, score);
        return true;
    }

    /**
     * 获取ZSet结构中的属性
     */
    public Set<Object> zRangeByScore(String key, double min, double max) {
        Map<Object, Object> zset = hashCache.get(key + ":zset");
        if (zset == null) {
            return new HashSet<>();
        }

        Set<Object> result = new HashSet<>();
        for (Map.Entry<Object, Object> entry : zset.entrySet()) {
            if (entry.getValue() instanceof Number) {
                double score = ((Number) entry.getValue()).doubleValue();
                if (score >= min && score <= max) {
                    result.add(entry.getKey());
                }
            }
        }
        return result;
    }

    // ========================================
    // 其他操作
    // ========================================

    /**
     * 获取所有匹配的键
     */
    public Set<String> keys(String pattern) {
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(cache.keySet());
        allKeys.addAll(hashCache.keySet());
        allKeys.addAll(listCache.keySet());
        allKeys.addAll(setCache.keySet());

        if ("*".equals(pattern)) {
            return allKeys;
        }

        // 简单的通配符匹配
        Set<String> matchedKeys = new HashSet<>();
        String regex = pattern.replace("*", ".*");
        for (String key : allKeys) {
            if (key.matches(regex)) {
                matchedKeys.add(key);
            }
        }
        return matchedKeys;
    }

    /**
     * 获取指定key的类型
     */
    public String type(String key) {
        if (cache.containsKey(key)) return "string";
        if (hashCache.containsKey(key)) return "hash";
        if (listCache.containsKey(key)) return "list";
        if (setCache.containsKey(key)) return "set";
        return "none";
    }

    /**
     * 获取指定key的大小
     */
    public Long size(String key) {
        Object value = cache.get(key);
        if (value instanceof String) {
            return (long) ((String) value).length();
        }
        return 0L;
    }

    /**
     * 设置位图中的某一位（简化实现）
     */
    public void setBit(String key, long offset, boolean value) {
        String bitKey = key + ":bit:" + offset;
        set(bitKey, value);
    }

    /**
     * 获取位图中的某一位（简化实现）
     */
    public Boolean getBit(String key, long offset) {
        String bitKey = key + ":bit:" + offset;
        Object value = get(bitKey);
        return value instanceof Boolean ? (Boolean) value : false;
    }

    /**
     * 统计位图中指定范围内置为 1 的位数（简化实现）
     */
    public Long bitCount(String key, long start, long end) {
        long count = 0;
        for (long i = start; i <= end; i++) {
            if (getBit(key, i)) {
                count++;
            }
        }
        return count;
    }
}