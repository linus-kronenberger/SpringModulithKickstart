package com.example.springmodulithkickstart.movie.infrastructure

import com.example.springmodulithkickstart.movie.api.dto.MovieDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.cache.interceptor.SimpleKey
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("dev")
class MovieServiceImplCachingInTest {

    @Autowired
    private lateinit var movieService: MovieServiceImpl

    @Autowired
    private lateinit var cacheManager: CacheManager

    @Test
    fun `cache should store movies after first retrieval`() {
        val firstResult: List<MovieDto> = movieService.retrieveAllMovies()
        assertThat(firstResult).isNotEmpty()

        val cache = cacheManager.getCache("movies")
        assertThat(cache).isNotNull()

        val cached = cache?.get(SimpleKey.EMPTY, List::class.java)
        assertThat(cached).isNotNull
    }

    @Test
    fun `subsequent calls should return identical cached results`() {
        val firstResult: List<MovieDto> = movieService.retrieveAllMovies()
        val secondResult: List<MovieDto> = movieService.retrieveAllMovies()

        assertThat(firstResult).isNotEmpty()
        assertThat(secondResult).containsExactlyElementsOf(firstResult)
    }

    @Test
    fun `cache manager should be ConcurrentMapCacheManager for dev profile`() {
        assertThat(cacheManager).isInstanceOf(ConcurrentMapCacheManager::class.java)
        val cm = cacheManager as ConcurrentMapCacheManager
        assertThat(cm.cacheNames).contains("movies")
    }
}
