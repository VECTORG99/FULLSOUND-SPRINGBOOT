package Fullsound.Fullsound.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoResponse {
    private Integer id;
    private String numeroPedido;
    private UsuarioResponse usuario;
    private LocalDateTime fechaCompra;
    private Integer total;
    private String estado;  
    private String metodoPago;  
    private List<PedidoItemResponse> items;
}
