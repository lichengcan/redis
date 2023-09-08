package com.example.redis.constants;

/**
 * @author lichengcan
 *
 * redis常量
 */
public interface RedisConstants {
    /**
     * 空值缓存过期时间(分钟)
     */
    Long CACHE_NULL_TTL = 2L;

    /**
     * 城市redis缓存key
     */
    String CACHE_CITY_KEY = "cache:city:";
    /**
     * 城市redis缓存过期时间(分钟)
     */
    Long CACHE_CITY_TTL = 30L;

    /**
     * 城市redis互斥锁key
     */
    String LOCK_CITY_KEY = "lock:city:";
}
