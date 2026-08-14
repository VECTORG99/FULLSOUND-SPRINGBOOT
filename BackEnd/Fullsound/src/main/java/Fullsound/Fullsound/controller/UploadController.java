package Fullsound.Fullsound.controller;

import lombok.RequiredArgsConstructor;
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

    @Value("${supabase.url:https://kivpcepyhfpqjfoycwel.supabase.co}")
    private String supabaseUrl;

    @Value("${supabase.key:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImtpdnBjZXB5aGZwcWpmb3ljd2VsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzMxODQyMDIsImV4cCI6MjA0ODc2MDIwMn0.TQZY9-Zs6rrZj5nTvs0k_JXi1OnNQd7g8sCaT0IfUc4}")
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
                System.err.println("Supabase upload error - Status: " + response.statusCode());
                System.err.println("Response body: " + response.body());
                
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Error al subir archivo a Supabase");
                error.put("status", response.statusCode());
                error.put("details", response.body());
                error.put("bucket", bucket);
                error.put("filename", filename);
                
                return ResponseEntity.status(response.statusCode()).body(error);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al leer el archivo: " + e.getMessage()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Proceso interrumpido: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error inesperado: " + e.getMessage(), "type", e.getClass().getName()));
        }
    }
}
