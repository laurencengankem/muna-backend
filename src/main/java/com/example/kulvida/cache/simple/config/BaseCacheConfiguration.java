package com.example.kulvida.cache.simple.config;

import com.google.common.cache.CacheBuilder;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class BaseCacheConfiguration extends CachingConfigurerSupport {

    //@Value("")
    private final int MAX_SIZE = 1000;

    //@Value("")
    private final int IDLE_EXPIRATION = 30;

    @Bean
    @Override
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager() {

            @Override
            protected Cache createConcurrentMapCache(final String name) {
                if(name.equals("KEY_CACHE")){
                    return new ConcurrentMapCache(name, CacheBuilder.newBuilder().expireAfterAccess(40,TimeUnit.MINUTES)
                            .maximumSize(MAX_SIZE).build().asMap(), false);
                }
                if(name.equals("REGISTRATION_CACHE")){
                    return new ConcurrentMapCache(name, CacheBuilder.newBuilder().expireAfterAccess(30,TimeUnit.MINUTES)
                            .maximumSize(MAX_SIZE).build().asMap(), false);
                }
                if(name.equals("USER_CACHE")){
                    return new ConcurrentMapCache(name, CacheBuilder.newBuilder().expireAfterAccess(30,TimeUnit.MINUTES)
                            .maximumSize(MAX_SIZE).build().asMap(), false);
                }
                if(name.equals("RESET_CACHE")){
                    return new ConcurrentMapCache(name, CacheBuilder.newBuilder().expireAfterAccess(5,TimeUnit.MINUTES)
                            .maximumSize(MAX_SIZE).build().asMap(), false);
                }
                if(name.equals("CART_CACHE")){
                    return new ConcurrentMapCache(name, CacheBuilder.newBuilder().expireAfterAccess(5,TimeUnit.MINUTES)
                            .maximumSize(MAX_SIZE).build().asMap(), false);
                }
                return new ConcurrentMapCache(name, CacheBuilder.newBuilder().expireAfterAccess(IDLE_EXPIRATION,TimeUnit.MINUTES)
                        .maximumSize(MAX_SIZE).build().asMap(), false);

            }
        };
        cacheManager.setCacheNames(Arrays.asList("KEY_CACHE","REGISTRATION_CACHE",
                "BASE_CACHE", "RESET_CACHE", "CART_CACHE"));
        return cacheManager;
    }
}






