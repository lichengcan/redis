package com.example.redis.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.redis.constants.RedisConstants;
import com.example.redis.domain.City;
import com.example.redis.mapper.CityMapper;
import com.example.redis.service.CityService;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author lcc
 * @date 2023/09/08
 */
@Service
@Slf4j
public class CityServiceImpl extends ServiceImpl<CityMapper, City> implements CityService {

    private StringRedisTemplate stringRedisTemplate;
    private CityMapper cityMapper;

    @Autowired
    public CityServiceImpl(StringRedisTemplate stringRedisTemplate,
                           CityMapper cityMapper){
        this.stringRedisTemplate = stringRedisTemplate;
        this.cityMapper = cityMapper;
    }

    @Override
    public City getByCode(String cityCode) {
        String key = RedisConstants.CACHE_CITY_KEY+cityCode;
        return queryCityWithMutex(key, cityCode);
    }

    /**
     * 通过互斥锁机制查询城市信息
     * @param key
     */
    private City queryCityWithMutex(String key, String cityCode) {
        City city = null;
        // 1.查询缓存
        String cityJson = stringRedisTemplate.opsForValue().get(key);
        // 2.判断缓存是否有数据
        if (StringUtils.isNotBlank(cityJson)) {
            // 3.有,则返回
            city = JSONObject.parseObject(cityJson, City.class);
            return city;
        }
        // 4.无,则获取互斥锁
        String lockKey = RedisConstants.LOCK_CITY_KEY + cityCode;
        Boolean isLock = tryLock(lockKey);
        // 5.判断获取锁是否成功
        try {
            if (!isLock) {
                // 6.获取失败, 休眠并重试
                Thread.sleep(100);
                return queryCityWithMutex(key, cityCode);
            }
            // 7.获取成功, 查询数据库
            city = baseMapper.selectById(cityCode);
            // 8.判断数据库是否有数据
            if (city == null) {
                // 9.无,则将空数据写入redis
                stringRedisTemplate.opsForValue().set(key, "", RedisConstants.CACHE_NULL_TTL, TimeUnit.MINUTES);
                return null;
            }
            // 10.有,则将数据写入redis
            stringRedisTemplate.opsForValue().set(key, JSONObject.toJSONString(city), RedisConstants.CACHE_CITY_TTL, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 11.释放锁
            unLock(lockKey);
        }
        // 12.返回数据
        return city;
    }

    /**
     * 获取互斥锁
     * @return
     */
    private Boolean tryLock(String key) {
        //如果key不存在则进行存储，当存在时，则代表当前互斥锁被其他线程获取了
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtils.isTrue(flag);
    }

    /**
     * 释放锁
     * @param key
     */
    private void unLock(String key) {
        stringRedisTemplate.delete(key);
    }

}
