package Fullsound.Fullsound.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Tag(name = "❤️ Health", description = "Endpoint de verificación de salud de la aplicación")
public class HealthController {

    private final DataSource dataSource;
    private final CacheManager cacheManager;

    @Autowired(required = false)
    private Flyway flyway;

    public HealthController(DataSource dataSource, CacheManager cacheManager) {
        this.dataSource = dataSource;
        this.cacheManager = cacheManager;
    }

    @Operation(
        summary = "Health Check completo",
        description = "Verifica el estado de la aplicación, la conectividad con la base de datos, las migraciones de Flyway y las estadísticas de caché."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Estado de la aplicación",
        content = @Content(schema = @Schema(implementation = Map.class))
    )
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "UP");
        response.put("service", "FullSound Backend API");
        response.put("version", "2.0.0");
        response.put("timestamp", java.time.LocalDateTime.now().toString());

        // Database connectivity
        Map<String, Object> db = new HashMap<>();
        try (Connection conn = dataSource.getConnection()) {
            db.put("status", "UP");
            db.put("database", conn.getMetaData().getDatabaseProductName());
            db.put("version", conn.getMetaData().getDatabaseProductVersion());
            db.put("isValid", conn.isValid(5));
        } catch (Exception e) {
            db.put("status", "DOWN");
            db.put("error", e.getMessage());
        }
        response.put("database", db);

        // Flyway migration status
        Map<String, Object> flywayInfo = new HashMap<>();
        if (flyway != null) {
            try {
                var info = flyway.info();
                flywayInfo.put("status", "OK");
                flywayInfo.put("appliedMigrations", info.applied() != null ? info.applied().length : 0);
                flywayInfo.put("pendingMigrations", info.pending() != null ? info.pending().length : 0);
                if (info.applied() != null && info.applied().length > 0) {
                    var latest = info.applied()[info.applied().length - 1];
                    flywayInfo.put("latestMigration", latest.getVersion() != null ? latest.getVersion().getVersion() : "unknown");
                    flywayInfo.put("latestMigrationDescription", latest.getDescription());
                }
            } catch (Exception e) {
                flywayInfo.put("status", "ERROR");
                flywayInfo.put("error", e.getMessage());
            }
        } else {
            flywayInfo.put("status", "DISABLED");
        }
        response.put("flyway", flywayInfo);

        // Cache stats
        Map<String, Object> cacheStats = new HashMap<>();
        try {
            for (String cacheName : cacheManager.getCacheNames()) {
                var cache = cacheManager.getCache(cacheName);
                if (cache != null && cache.getNativeCache() instanceof com.github.benmanes.caffeine.cache.Cache) {
                    @SuppressWarnings("unchecked")
                    com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeine =
                            (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cache.getNativeCache();
                    Map<String, Object> stats = new HashMap<>();
                    stats.put("size", caffeine.estimatedSize());
                    stats.put("hitCount", caffeine.stats().hitCount());
                    stats.put("missCount", caffeine.stats().missCount());
                    stats.put("evictionCount", caffeine.stats().evictionCount());
                    cacheStats.put(cacheName, stats);
                } else {
                    cacheStats.put(cacheName, Map.of("size", "unknown"));
                }
            }
        } catch (Exception e) {
            cacheStats.put("error", e.getMessage());
        }
        response.put("cache", cacheStats);

        return ResponseEntity.ok(response);
    }
}
