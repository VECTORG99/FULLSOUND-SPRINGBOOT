package Fullsound.Fullsound.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Filtro de rate limiting para los endpoints de autenticación.
 *
 * Limita a 5 intentos por minuto por dirección IP en los endpoints
 * /api/auth/login y /api/auth/register. Devuelve 429 Too Many Requests
 * cuando se excede el límite.
 */
@Component
@Order(1)
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);
    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_MS = 60_000L; // 1 minuto

    private final Map<String, RateLimitEntry> attempts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (isAuthEndpoint(path) && "POST".equalsIgnoreCase(request.getMethod())) {
            String clientIp = getClientIp(request);
            if (isRateLimited(clientIp)) {
                log.warn("Rate limit excedido para IP {} en endpoint {}", clientIp, path);
                writeRateLimitResponse(response, path);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isAuthEndpoint(String path) {
        return "/api/auth/login".equals(path) || "/api/auth/register".equals(path);
    }

    private boolean isRateLimited(String clientIp) {
        long now = System.currentTimeMillis();
        RateLimitEntry entry = attempts.compute(clientIp, (key, existing) -> {
            if (existing == null || now - existing.windowStart > WINDOW_MS) {
                return new RateLimitEntry(now);
            }
            existing.count.incrementAndGet();
            return existing;
        });
        return entry.count.get() > MAX_ATTEMPTS;
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }

    private void writeRateLimitResponse(HttpServletResponse response, String path) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(429);
        response.setHeader("Retry-After", "60");
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", 429,
                "error", "Too Many Requests",
                "message", "Demasiados intentos. Intenta nuevamente en 1 minuto.",
                "path", path
        );
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.writeValue(response.getOutputStream(), body);
    }

    private static class RateLimitEntry {
        final long windowStart;
        final AtomicInteger count;

        RateLimitEntry(long windowStart) {
            this.windowStart = windowStart;
            this.count = new AtomicInteger(1);
        }
    }
}
