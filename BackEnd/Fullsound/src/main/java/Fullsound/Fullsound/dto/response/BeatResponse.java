package Fullsound.Fullsound.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para Beat.
 * Adaptado al schema de PostgreSQL.
 * 
 * @author VECTORG99
 * @version 2.0.0
 * @since 2025-11-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeatResponse {
    private Integer idBeat;
    private String titulo;
    private String slug;
    private String artista;
    private Integer precio;
    private String precioFormateado; // Campo calculado @Transient
    private Integer bpm;
    private String tonalidad;
    private Integer duracion; // Duración en segundos
    private String genero; // Trap, Lo-Fi, Hip Hop, etc.
    private String etiquetas; // Tags separados por comas
    private String descripcion;
    private String imagenUrl;
    private String audioUrl;
    private String audioDemoUrl;
    private Integer reproducciones;
    private String estado; // DISPONIBLE, VENDIDO, RESERVADO, INACTIVO
    private String enlaceProducto; // Campo calculado @Transient
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
