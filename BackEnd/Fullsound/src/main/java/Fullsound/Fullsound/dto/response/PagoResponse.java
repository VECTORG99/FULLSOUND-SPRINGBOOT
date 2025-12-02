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
public class PagoResponse {
    private Integer id;
    private Integer pedidoId;
    private String stripePaymentIntentId;
    private String stripeChargeId;
    private String estado;  
    private Integer monto;
    private String moneda;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String clientSecret;  
}
