package Fullsound.Fullsound.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para Pago.
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
public class PagoResponse {
    private Integer id;
    private Integer pedidoId;
    private String stripePaymentIntentId;
    private String stripeChargeId;
    private String estado; // PENDIENTE, PROCESANDO, EXITOSO, FALLIDO, REEMBOLSADO
    private Integer monto;
    private String moneda;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String clientSecret; // Para confirmar el pago en el frontend
}
