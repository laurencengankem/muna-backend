package com.example.kulvida.cache;


import java.io.Serializable;
import java.util.Collection;

public interface BaseCacheManager {


    <T extends Serializable> T get(String mapName, String key);

    <T extends Serializable> boolean put(String mapName, String key, T object);

    boolean clearCacheEntry(String mapName, String key);

    Collection<String> getCacheNames();

}
