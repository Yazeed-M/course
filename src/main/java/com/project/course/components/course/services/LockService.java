package com.project.course.components.course.services;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LockService {
    @Autowired
    private final StringRedisTemplate redisTemplate;
    
    public LockService(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }


    public boolean createLock(String redisLockKey){

        Boolean lock = redisTemplate.opsForValue().setIfAbsent(redisLockKey, "Lock", 2, TimeUnit.MINUTES);
        boolean result= Boolean.TRUE.equals(lock);
        return result;
    }

    public boolean checkIfLocked(String redisLockKey){
        if(redisLockKey == null){
            return false;
        }
       String exists= redisTemplate.opsForValue().get(redisLockKey);
       return exists != null;
    }
}
