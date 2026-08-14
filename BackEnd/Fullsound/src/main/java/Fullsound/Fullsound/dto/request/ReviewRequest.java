package Fullsound.Fullsound.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Solicitud para crear o actualizar una reseña de un beat")
public class ReviewRequest {

    @Schema(description = "Calificación de 1 a 5", example = "5", required = true)
    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer rating;

    @Schema(description = "Comentario de la reseña", example = "Beat increíble, calidad de estudio")
    @Size(max = 1000, message = "El comentario no puede exceder los 1000 caracteres")
    private String comentario;
}
