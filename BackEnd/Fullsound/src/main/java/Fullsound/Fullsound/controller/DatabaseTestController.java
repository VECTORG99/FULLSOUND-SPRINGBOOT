package Fullsound.Fullsound.controller;
import Fullsound.Fullsound.repository.BeatRepository;
import Fullsound.Fullsound.repository.UsuarioRepository;
import Fullsound.Fullsound.repository.PedidoRepository;
import Fullsound.Fullsound.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.*;
@RestController
@RequestMapping("/api/test/database")
@CrossOrigin(origins = "*")
public class DatabaseTestController {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private BeatRepository beatRepository;
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private RolRepository rolRepository;
    @GetMapping
    public ResponseEntity<Map<String, Object>> testDatabase() {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("status", "SUCCESS");
            response.put("message", "Conexión a base de datos exitosa");
            response.put("timestamp", LocalDateTime.now());
            try (Connection conn = dataSource.getConnection()) {
                DatabaseMetaData metaData = conn.getMetaData();
                Map<String, Object> connectionInfo = new HashMap<>();
                connectionInfo.put("databaseProductName", metaData.getDatabaseProductName());
                connectionInfo.put("databaseProductVersion", metaData.getDatabaseProductVersion());
                connectionInfo.put("driverName", metaData.getDriverName());
                connectionInfo.put("driverVersion", metaData.getDriverVersion());
                connectionInfo.put("url", metaData.getURL());
                connectionInfo.put("username", metaData.getUserName());
                connectionInfo.put("isReadOnly", conn.isReadOnly());
                connectionInfo.put("catalog", conn.getCatalog());
                response.put("connectionInfo", connectionInfo);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Error al conectar con la base de datos");
            response.put("error", e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(response);
        }
    }
    @GetMapping("/tables")
    public ResponseEntity<Map<String, Object>> getTables() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, String>> tables = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            while (rs.next()) {
                Map<String, String> table = new HashMap<>();
                table.put("tableName", rs.getString("TABLE_NAME"));
                table.put("tableType", rs.getString("TABLE_TYPE"));
                table.put("tableSchema", rs.getString("TABLE_SCHEM"));
                tables.add(table);
            }
            response.put("status", "SUCCESS");
            response.put("totalTables", tables.size());
            response.put("tables", tables);
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Error al obtener las tablas");
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> response = new HashMap<>();
        Map<String, Long> counts = new HashMap<>();
        try {
            counts.put("usuarios", usuarioRepository.count());
            counts.put("beats", beatRepository.count());
            counts.put("pedidos", pedidoRepository.count());
            counts.put("roles", rolRepository.count());
            response.put("status", "SUCCESS");
            response.put("message", "Estadísticas obtenidas correctamente");
            response.put("counts", counts);
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Error al obtener estadísticas");
            response.put("error", e.getMessage());
            response.put("stackTrace", Arrays.toString(e.getStackTrace()));
            return ResponseEntity.status(500).body(response);
        }
    }
    @GetMapping("/table/{tableName}")
    public ResponseEntity<Map<String, Object>> getTableStructure(@PathVariable String tableName) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, String>> columns = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getColumns(null, null, tableName, "%");
            while (rs.next()) {
                Map<String, String> column = new HashMap<>();
                column.put("columnName", rs.getString("COLUMN_NAME"));
                column.put("dataType", rs.getString("TYPE_NAME"));
                column.put("columnSize", rs.getString("COLUMN_SIZE"));
                column.put("nullable", rs.getString("IS_NULLABLE"));
                column.put("defaultValue", rs.getString("COLUMN_DEF"));
                columns.add(column);
            }
            List<String> primaryKeys = new ArrayList<>();
            ResultSet pkRs = metaData.getPrimaryKeys(null, null, tableName);
            while (pkRs.next()) {
                primaryKeys.add(pkRs.getString("COLUMN_NAME"));
            }
            response.put("status", "SUCCESS");
            response.put("tableName", tableName);
            response.put("totalColumns", columns.size());
            response.put("columns", columns);
            response.put("primaryKeys", primaryKeys);
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Error al obtener estructura de la tabla");
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    @GetMapping("/query-test")
    public ResponseEntity<Map<String, Object>> executeTestQuery() {
        Map<String, Object> response = new HashMap<>();
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT CURRENT_TIMESTAMP as current_time, CURRENT_DATABASE() as current_db, VERSION() as version";
            try (var stmt = conn.createStatement(); var rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    Map<String, Object> queryResult = new HashMap<>();
                    queryResult.put("currentTime", rs.getString("current_time"));
                    queryResult.put("currentDatabase", rs.getString("current_db"));
                    queryResult.put("version", rs.getString("version"));
                    response.put("status", "SUCCESS");
                    response.put("message", "Query ejecutado correctamente");
                    response.put("result", queryResult);
                }
            }
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Error al ejecutar query de prueba");
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    @PostMapping("/write-test")
    public ResponseEntity<Map<String, Object>> testWrite() {
        Map<String, Object> response = new HashMap<>();
        try (Connection conn = dataSource.getConnection()) {
            boolean canWrite = !conn.isReadOnly();
            response.put("status", "SUCCESS");
            response.put("canWrite", canWrite);
            response.put("message", canWrite ? 
                "Base de datos permite escritura" : 
                "Base de datos en modo solo lectura");
            response.put("autoCommit", conn.getAutoCommit());
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Error al verificar permisos de escritura");
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping() {
        Map<String, Object> response = new HashMap<>();
        try (Connection conn = dataSource.getConnection()) {
            boolean isValid = conn.isValid(5);
            response.put("status", isValid ? "SUCCESS" : "ERROR");
            response.put("message", isValid ? "Base de datos responde" : "Base de datos no responde");
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "No se pudo hacer ping a la base de datos");
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
