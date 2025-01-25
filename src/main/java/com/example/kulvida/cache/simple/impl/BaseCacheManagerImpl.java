package com.example.kulvida.cache.simple.impl;


import com.example.kulvida.cache.BaseCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
@Scope("singleton")
public class BaseCacheManagerImpl implements BaseCacheManager {

    @Autowired
    private CacheManager cacheManager;

    @Override
    public <T extends Serializable> T get(String mapName, String key) {
        T clone=null;
        ConcurrentMap<String,Object> cacheMap= (ConcurrentMap<String,Object>) cacheManager.getCache(mapName).getNativeCache();

        if(cacheMap!=null) {
            T attribute= (T) cacheMap.get(key);
            if(attribute!=null){
                clone = (T) SerializationUtils.clone(attribute);
                log.info("object:{} with key:{} extracted from simple cache",clone,key);
            }
        }
        return clone;
    }


    @Override
    public <T extends Serializable> boolean put(String mapName, String key, T object) {
        ConcurrentMap<String,Object> cacheMap= (ConcurrentMap<String,Object>) cacheManager.getCache(mapName).getNativeCache();
        if(cacheMap!=null) {
            cacheMap.put(key,object);
            return true;
        }
        return false;

    }

    @Override
    public Collection<String> getCacheNames() {
        return cacheManager.getCacheNames();
    }


    @Override
    public  boolean clearCacheEntry(String mapName, String key) {
        return cacheManager.getCache(mapName).evictIfPresent(key);

    }



}
