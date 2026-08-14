package Fullsound.Fullsound.controller;
import Fullsound.Fullsound.model.Beat;
import Fullsound.Fullsound.model.Pedido;
import Fullsound.Fullsound.repository.BeatRepository;
import Fullsound.Fullsound.repository.PedidoRepository;
import Fullsound.Fullsound.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/estadisticas")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
public class EstadisticasController {
    private final BeatRepository beatRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        long totalBeats = beatRepository.count();
        long totalUsuarios = usuarioRepository.count();
        long totalPedidos = pedidoRepository.count();
        // Sumar el total de los pedidos completados para las ventas totales
        double ventasTotales = pedidoRepository.findByEstadoOrderByFechaCompraDesc("COMPLETADO")
                .stream()
                .mapToLong(Pedido::getTotal)
                .sum();
        stats.put("message", "Estadísticas del dashboard");
        stats.put("totalBeats", totalBeats);
        stats.put("totalUsuarios", totalUsuarios);
        stats.put("totalPedidos", totalPedidos);
        stats.put("ventasTotales", ventasTotales);
        return ResponseEntity.ok(stats);
    }
    @GetMapping("/ventas")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<Map<String, Object>> getVentasStats() {
        Map<String, Object> stats = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();
        LocalDateTime startOfWeek = startOfToday.minusDays(7);
        LocalDateTime startOfMonth = startOfToday.minusDays(30);
        long ventasHoy = pedidoRepository.findByFechaCompraBetween(startOfToday, now)
                .stream()
                .filter(p -> "COMPLETADO".equals(p.getEstado()))
                .count();
        long ventasSemana = pedidoRepository.findByFechaCompraBetween(startOfWeek, now)
                .stream()
                .filter(p -> "COMPLETADO".equals(p.getEstado()))
                .count();
        long ventasMes = pedidoRepository.findByFechaCompraBetween(startOfMonth, now)
                .stream()
                .filter(p -> "COMPLETADO".equals(p.getEstado()))
                .count();
        double ingresosTotales = pedidoRepository.findByEstadoOrderByFechaCompraDesc("COMPLETADO")
                .stream()
                .mapToLong(Pedido::getTotal)
                .sum();
        stats.put("message", "Estadísticas de ventas");
        stats.put("ventasHoy", ventasHoy);
        stats.put("ventasSemana", ventasSemana);
        stats.put("ventasMes", ventasMes);
        stats.put("ingresosTotales", ingresosTotales);
        return ResponseEntity.ok(stats);
    }
    @GetMapping("/beats-populares")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<Map<String, Object>> getBeatsPopulares(
            @RequestParam(defaultValue = "10") Integer limit) {
        Map<String, Object> stats = new HashMap<>();
        List<Beat> populares = beatRepository.findTopByOrderByReproduccionesDesc(limit);
        stats.put("message", "Beats más populares");
        stats.put("limit", limit);
        stats.put("beats", populares);
        return ResponseEntity.ok(stats);
    }
}
