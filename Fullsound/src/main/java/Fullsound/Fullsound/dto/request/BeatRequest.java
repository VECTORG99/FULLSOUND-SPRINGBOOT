package Fullsound.Fullsound.dto.request;

import Fullsound.Fullsound.enums.EstadoBeat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para petición de creación/actualización de beat.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeatRequest {
    
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 255, message = "El título no puede exceder 255 caracteres")
    private String titulo;
    
    @Size(max = 100, message = "El artista no puede exceder 100 caracteres")
    private String artista;
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precio;
    
    @Min(value = 1, message = "El BPM debe ser al menos 1")
    @Max(value = 300, message = "El BPM no puede exceder 300")
    private Integer bpm;
    
    @Size(max = 10, message = "La tonalidad no puede exceder 10 caracteres")
    private String tonalidad;
    
    @Size(max = 50, message = "El mood no puede exceder 50 caracteres")
    private String mood;
    
    @Size(max = 255, message = "Los tags no pueden exceder 255 caracteres")
    private String tags;
    
    private EstadoBeat estado;
    
    @Size(max = 255, message = "La ruta del archivo de audio no puede exceder 255 caracteres")
    private String archivoAudio;
    
    @Size(max = 255, message = "La ruta de la imagen de portada no puede exceder 255 caracteres")
    private String imagenPortada;
    
    private Boolean destacado;
    
    private Boolean activo;
}
