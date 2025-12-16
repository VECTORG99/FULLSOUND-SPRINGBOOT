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

            // Generar nombre único para el archivo
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : "";
            String filename = UUID.randomUUID().toString() + extension;

            // Construir URL de Supabase Storage
            String url = supabaseUrl + "/storage/v1/object/" + bucket + "/" + filename;

            // Crear cliente HTTP
            HttpClient client = HttpClient.newHttpClient();
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + supabaseKey)
                .header("Content-Type", file.getContentType())
                .header("apikey", supabaseKey)
                .POST(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                // URL pública del archivo
                String publicUrl = supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + filename;
                
                Map<String, String> result = new HashMap<>();
                result.put("url", publicUrl);
                result.put("filename", filename);
                result.put("bucket", bucket);
                
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(response.statusCode())
                    .body(Map.of("error", "Error al subir archivo a Supabase: " + response.body()));
            }

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al leer el archivo: " + e.getMessage()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Proceso interrumpido: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error inesperado: " + e.getMessage()));
        }
    }
}
