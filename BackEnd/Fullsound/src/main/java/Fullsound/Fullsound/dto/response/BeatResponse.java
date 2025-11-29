package Fullsound.Fullsound.dto.response;

import Fullsound.Fullsound.enums.EstadoBeat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para Beat.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeatResponse {
    private Integer id;
    private String titulo;
    private String slug;
    private String artista;
    private Integer precio;
    private String precioFormateado;
    private Integer bpm;
    private String tonalidad;
    private String mood;
    private String tags;
    private EstadoBeat estado;
    private String archivoAudio;
    private String imagenPortada;
    private Integer reproducciones;
    private Integer descargas;
    private Integer likes;
    private Boolean destacado;
    private Boolean activo;
    private String enlaceProducto;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
