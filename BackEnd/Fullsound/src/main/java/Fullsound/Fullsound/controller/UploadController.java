package Fullsound.Fullsound.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class UploadController {

    private static final Logger log = LoggerFactory.getLogger(UploadController.class);

    @Value("${supabase.url:https://your-project.supabase.co}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @PostMapping("/imagen")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<?> uploadImagen(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, "Imagenes");
    }

    @PostMapping("/audio")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<?> uploadAudio(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, "audios");
    }

    private ResponseEntity<?> uploadFile(MultipartFile file, String bucket) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El archivo está vacío"));
            }

            // Validar tamaño (max 50MB)
            if (file.getSize() > 50 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(Map.of("error", "El archivo es demasiado grande (máx 50MB)"));
            }

            // Generar nombre único para el archivo manteniendo el nombre original
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            String baseName = "file";
            
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                baseName = originalFilename.substring(0, originalFilename.lastIndexOf("."))
                    .replaceAll("[^a-zA-Z0-9-_]", "_");
            }
            
            String filename = baseName + "_" + System.currentTimeMillis() + extension;

            // Construir URL de Supabase Storage - usar service_role key para escribir
            String url = supabaseUrl + "/storage/v1/object/" + bucket + "/" + filename;

            // Crear cliente HTTP
            HttpClient client = HttpClient.newHttpClient();
            
            // Usar service role key si está disponible, sino usar anon key
            String authKey = System.getenv("SUPABASE_SERVICE_KEY") != null 
                ? System.getenv("SUPABASE_SERVICE_KEY") 
                : supabaseKey;
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + authKey)
                .header("apikey", authKey)
                .header("Content-Type", file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                .header("x-upsert", "true")
                .POST(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                // URL pública del archivo
                String publicUrl = supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + filename;
                
                Map<String, Object> result = new HashMap<>();
                result.put("url", publicUrl);
                result.put("filename", filename);
                result.put("bucket", bucket);
                result.put("size", file.getSize());
                
                return ResponseEntity.ok(result);
            } else {
                // Log detallado del error
                log.error("Supabase upload error - Status: {}", response.statusCode());
                log.error("Response body: {}", response.body());
                
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Error al subir archivo a Supabase");
                error.put("status", response.statusCode());
                error.put("details", response.body());
                error.put("bucket", bucket);
                error.put("filename", filename);
                
                return ResponseEntity.status(response.statusCode()).body(error);
            }

        } catch (IOException e) {
            log.error("Error al leer el archivo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al leer el archivo"));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Proceso interrumpido", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Proceso interrumpido"));
        } catch (Exception e) {
            log.error("Error inesperado al subir archivo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error inesperado al subir archivo"));
        }
    }
}
