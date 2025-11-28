package Fullsound.Fullsound.dto.request;

import Fullsound.Fullsound.enums.MetodoPago;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para petición de creación de pedido.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoRequest {
    
    @NotEmpty(message = "Debe incluir al menos un beat")
    private List<Integer> beatIds;
    
    @NotNull(message = "El método de pago es obligatorio")
    private MetodoPago metodoPago;
}
