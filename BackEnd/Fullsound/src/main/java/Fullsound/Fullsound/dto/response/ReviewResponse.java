package Fullsound.Fullsound.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta con los datos de una reseña")
public class ReviewResponse {

    @Schema(description = "ID de la reseña", example = "1")
    private Integer id;

    @Schema(description = "ID del beat reseñado", example = "5")
    private Integer beatId;

    @Schema(description = "ID del usuario que reseña", example = "3")
    private Integer usuarioId;

    @Schema(description = "Nombre del usuario que reseña", example = "client_test")
    private String nombreUsuario;

    @Schema(description = "Calificación de 1 a 5", example = "5")
    private Integer rating;

    @Schema(description = "Comentario de la reseña", example = "Beat increíble")
    private String comentario;

    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de actualización")
    private LocalDateTime updatedAt;
}
