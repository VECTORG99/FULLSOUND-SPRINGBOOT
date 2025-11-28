package Fullsound.Fullsound.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para petición de procesamiento de pago con Stripe.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoRequest {
    
    @NotNull(message = "El ID del pedido es obligatorio")
    private Integer pedidoId;
    
    @NotBlank(message = "El Payment Method ID de Stripe es obligatorio")
    private String paymentMethodId; // ID del método de pago de Stripe
    
    private String customerEmail; // Email del cliente (opcional, para crear Customer en Stripe)
    
    private Boolean saveCard; // Si el usuario quiere guardar la tarjeta
}
