package com.example.springmodulithkickstart.shared.infrastructure.caching

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory


@Configuration
@EnableCaching
@Profile("prod")
class RedisCachingConfiguration {
    @Bean
    fun cacheManager(connectionFactory: RedisConnectionFactory): RedisCacheManager {
        return RedisCacheManager.create(connectionFactory)
    }
}
