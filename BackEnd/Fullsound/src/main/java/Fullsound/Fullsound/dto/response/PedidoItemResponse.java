package Fullsound.Fullsound.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
