package com.example.springmodulithkickstart.shared.infrastructure.caching

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@EnableCaching
@Profile("dev")
class CachingConfiguration {
    @Bean
    fun cacheManager(): CacheManager {
        return ConcurrentMapCacheManager("movies")
    }
}