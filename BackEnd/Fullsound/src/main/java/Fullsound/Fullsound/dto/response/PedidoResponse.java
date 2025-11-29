package Fullsound.Fullsound.dto.response;

import Fullsound.Fullsound.enums.EstadoPedido;
import Fullsound.Fullsound.enums.MetodoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta para Pedido.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
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
    private EstadoPedido estado;
    private MetodoPago metodoPago;
    private List<PedidoItemResponse> items;
}
