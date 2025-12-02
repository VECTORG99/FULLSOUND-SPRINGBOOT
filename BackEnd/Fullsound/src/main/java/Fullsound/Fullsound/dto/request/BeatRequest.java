package Fullsound.Fullsound.dto.request;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    @Min(value = 1, message = "El precio debe ser mayor a 0")
    private Integer precio;
    @Min(value = 1, message = "El BPM debe ser al menos 1")
    @Max(value = 300, message = "El BPM no puede exceder 300")
    private Integer bpm;
    @Size(max = 10, message = "La tonalidad no puede exceder 10 caracteres")
    private String tonalidad;
    @Min(value = 1, message = "La duración debe ser al menos 1 segundo")
    private Integer duracion;  
    @Size(max = 50, message = "El género no puede exceder 50 caracteres")
    private String genero;  
    private String etiquetas;  
    private String descripcion;  
    @Size(max = 500, message = "La URL de la imagen no puede exceder 500 caracteres")
    private String imagenUrl;
    @Size(max = 500, message = "La URL del audio no puede exceder 500 caracteres")
    private String audioUrl;
    @Size(max = 500, message = "La URL del audio demo no puede exceder 500 caracteres")
    private String audioDemoUrl;
    @Pattern(regexp = "DISPONIBLE|VENDIDO|RESERVADO|INACTIVO", message = "Estado debe ser: DISPONIBLE, VENDIDO, RESERVADO o INACTIVO")
    private String estado;  
}
