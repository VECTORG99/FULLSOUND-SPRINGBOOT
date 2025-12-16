package Fullsound.Fullsound.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/api/estadisticas")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
public class EstadisticasController {
    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("message", "Estadísticas del dashboard");
        stats.put("totalBeats", 0);
        stats.put("totalUsuarios", 0);
        stats.put("totalPedidos", 0);
        stats.put("ventasTotales", 0.0);
        return ResponseEntity.ok(stats);
    }
    @GetMapping("/ventas")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<Map<String, Object>> getVentasStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("message", "Estadísticas de ventas");
        stats.put("ventasHoy", 0);
        stats.put("ventasSemana", 0);
        stats.put("ventasMes", 0);
        stats.put("ingresosTotales", 0.0);
        return ResponseEntity.ok(stats);
    }
    @GetMapping("/beats-populares")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<Map<String, Object>> getBeatsPopulares(
            @RequestParam(defaultValue = "10") Integer limit) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("message", "Beats más populares");
        stats.put("limit", limit);
        return ResponseEntity.ok(stats);
    }
}
