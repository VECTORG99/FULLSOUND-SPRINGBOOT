package Fullsound.Fullsound.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Solicitud para crear una notificación (admin/sistema)")
public class NotificacionRequest {

    @Schema(description = "ID del usuario destinatario", example = "3", required = true)
    private Integer usuarioId;

    @Schema(description = "Tipo de notificación", example = "SISTEMA", required = true)
    @NotBlank(message = "El tipo de notificación es obligatorio")
    @Size(max = 50, message = "El tipo no puede exceder 50 caracteres")
    private String tipo;

    @Schema(description = "Mensaje de la notificación", example = "Tu pedido fue completado", required = true)
    @NotBlank(message = "El mensaje es obligatorio")
    @Size(max = 500, message = "El mensaje no puede exceder 500 caracteres")
    private String mensaje;
}
