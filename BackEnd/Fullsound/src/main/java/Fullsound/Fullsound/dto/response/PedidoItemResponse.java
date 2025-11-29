package Fullsound.Fullsound.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para PedidoItem.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoItemResponse {
    private Integer id;
    private Integer beatId;
    private String nombreItem;
    private Integer cantidad;
    private Integer precioUnitario;
    private Integer subtotal;
}
