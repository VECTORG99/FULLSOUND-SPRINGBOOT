package Fullsound.Fullsound.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fix")
@CrossOrigin(originPatterns = "*")
@RequiredArgsConstructor
public class FixController {
    
    private final JdbcTemplate jdbcTemplate;
    
    @PostMapping("/imagenes-urls")
    public String fixImagenesUrls() {
        try {
            String sql = "UPDATE slide SET imagen = REPLACE(imagen, '/imagenes/', '/Imagenes/') WHERE imagen LIKE '%/imagenes/%'";
            int updated = jdbcTemplate.update(sql);
            return "✅ URLs actualizadas correctamente. Filas afectadas: " + updated;
        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }
    
    @GetMapping("/verify-urls")
    public String verifyUrls() {
        try {
            String sql = "SELECT id, imagen FROM slide ORDER BY orden";
            var results = jdbcTemplate.queryForList(sql);
            StringBuilder sb = new StringBuilder("URLs actuales:\n");
            for (var row : results) {
                sb.append("ID: ").append(row.get("id"))
                  .append(" - ").append(row.get("imagen"))
                  .append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }
}
