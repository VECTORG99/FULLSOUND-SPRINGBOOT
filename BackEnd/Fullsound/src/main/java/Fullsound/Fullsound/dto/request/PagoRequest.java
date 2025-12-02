package Fullsound.Fullsound.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoRequest {
    @NotNull(message = "El ID del pedido es obligatorio")
    private Integer pedidoId;
    @NotBlank(message = "El Payment Method ID de Stripe es obligatorio")
    private String paymentMethodId;  
    private String customerEmail;  
    private Boolean saveCard;  
}
