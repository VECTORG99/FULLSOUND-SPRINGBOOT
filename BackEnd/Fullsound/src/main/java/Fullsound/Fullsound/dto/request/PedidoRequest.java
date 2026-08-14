package Fullsound.Fullsound.dto.request;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoRequest {
    @NotEmpty(message = "Debe incluir al menos un beat")
    private List<Integer> beatIds;
    @NotNull(message = "El método de pago es obligatorio")
    @Pattern(regexp = "STRIPE|PAYPAL|TRANSFERENCIA", message = "Método de pago debe ser: STRIPE, PAYPAL o TRANSFERENCIA")
    private String metodoPago;  
}
