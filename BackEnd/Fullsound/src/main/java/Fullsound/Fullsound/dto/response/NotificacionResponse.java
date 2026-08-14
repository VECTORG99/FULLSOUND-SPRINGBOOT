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
@Schema(description = "Respuesta con los datos de una notificación")
public class NotificacionResponse {

    @Schema(description = "ID de la notificación", example = "1")
    private Integer id;

    @Schema(description = "ID del usuario destinatario", example = "3")
    private Integer usuarioId;

    @Schema(description = "Tipo de notificación", example = "COMPRA")
    private String tipo;

    @Schema(description = "Mensaje", example = "Tu pedido fue completado")
    private String mensaje;

    @Schema(description = "Indica si fue leída", example = "false")
    private Boolean leido;

    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;
}
