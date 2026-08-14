package Fullsound.Fullsound.controller;
import Fullsound.Fullsound.model.Beat;
import Fullsound.Fullsound.service.EstadisticasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/estadisticas")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
public class EstadisticasController {
    private final EstadisticasService estadisticasService;
    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return ResponseEntity.ok(estadisticasService.getDashboardStats());
    }
    @GetMapping("/ventas")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<Map<String, Object>> getVentasStats() {
        return ResponseEntity.ok(estadisticasService.getVentasStats());
    }
    @GetMapping("/beats-populares")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<Map<String, Object>> getBeatsPopulares(
            @RequestParam(defaultValue = "10") Integer limit) {
        Map<String, Object> stats = new HashMap<>();
        List<Beat> populares = estadisticasService.getBeatsPopulares(limit);
        stats.put("message", "Beats más populares");
        stats.put("limit", limit);
        stats.put("beats", populares);
        return ResponseEntity.ok(stats);
    }
}
