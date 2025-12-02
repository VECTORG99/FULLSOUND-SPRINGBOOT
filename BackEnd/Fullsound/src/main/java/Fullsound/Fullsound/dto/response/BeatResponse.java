package Fullsound.Fullsound.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
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
    private String precioFormateado;  
    private Integer bpm;
    private String tonalidad;
    private Integer duracion;  
    private String genero;  
    private String etiquetas;  
    private String descripcion;
    private String imagenUrl;
    private String audioUrl;
    private String audioDemoUrl;
    private Integer reproducciones;
    private String estado;  
    private String enlaceProducto;  
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
