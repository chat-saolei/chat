package com.chat.core.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class CacheManager {

    public static void init() {
        Cache cache = Caffeine.newBuilder()
                .initialCapacity(10000)
                .maximumSize(100000)
                .build(new CacheLoader() {
                    @Nullable
                    public Object load(@NonNull Object o) throws Exception {
                        return null;
                    }
                });
    }

}
