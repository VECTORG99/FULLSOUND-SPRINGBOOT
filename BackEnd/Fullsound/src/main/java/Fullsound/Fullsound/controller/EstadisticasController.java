package Fullsound.Fullsound.controller;
import Fullsound.Fullsound.model.Beat;
import Fullsound.Fullsound.service.EstadisticasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "📊 Estadísticas", description = "Dashboard y métricas del marketplace (solo administrador)")
public class EstadisticasController {
    private final EstadisticasService estadisticasService;
    @Operation(
        summary = "Estadísticas del dashboard",
        description = "Devuelve métricas generales: total de beats, usuarios, pedidos y ventas. Requiere rol de administrador.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(responseCode = "200", description = "Estadísticas del dashboard", content = @Content(schema = @Schema(implementation = Map.class)))
    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return ResponseEntity.ok(estadisticasService.getDashboardStats());
    }
    @Operation(
        summary = "Estadísticas de ventas",
        description = "Devuelve métricas de ventas por período (hoy, semana, mes) e ingresos totales. Requiere rol de administrador.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(responseCode = "200", description = "Estadísticas de ventas", content = @Content(schema = @Schema(implementation = Map.class)))
    @GetMapping("/ventas")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<Map<String, Object>> getVentasStats() {
        return ResponseEntity.ok(estadisticasService.getVentasStats());
    }
    @Operation(
        summary = "Beats más populares",
        description = "Devuelve los beats más reproducidos. Requiere rol de administrador.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(responseCode = "200", description = "Lista de beats populares", content = @Content(schema = @Schema(implementation = Map.class)))
    @GetMapping("/beats-populares")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<Map<String, Object>> getBeatsPopulares(
            @Parameter(description = "Número máximo de resultados", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        Map<String, Object> stats = new HashMap<>();
        List<Beat> populares = estadisticasService.getBeatsPopulares(limit);
        stats.put("message", "Beats más populares");
        stats.put("limit", limit);
        stats.put("beats", populares);
        return ResponseEntity.ok(stats);
    }
}
