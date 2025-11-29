package Fullsound.Fullsound.dto.response;

import Fullsound.Fullsound.enums.EstadoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para Pago.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoResponse {
    private Integer id;
    private Integer pedidoId;
    private String stripePaymentIntentId;
    private String stripeChargeId;
    private EstadoPago estado;
    private Integer monto;
    private String moneda;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String clientSecret; // Para confirmar el pago en el frontend
}
