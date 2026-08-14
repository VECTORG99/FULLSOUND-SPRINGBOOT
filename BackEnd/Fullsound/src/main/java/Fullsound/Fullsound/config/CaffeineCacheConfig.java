package Fullsound.Fullsound.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuración de caché con Caffeine.
 *
 * Define dos caches:
 * - "beats": cachea beats individuales por ID y por slug (TTL largo, cambian raramente).
 * - "beatList": cachea listados de beats (TTL corto para reflejar cambios rápidamente).
 */
@Configuration
@EnableCaching
public class CaffeineCacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.registerCustomCache("beats", Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .maximumSize(500)
                .recordStats()
                .build());
        cacheManager.registerCustomCache("beatList", Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(100)
                .recordStats()
                .build());
        return cacheManager;
    }
}
