package Fullsound.Fullsound.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta para Pedido.
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
public class PedidoResponse {
    private Integer id;
    private String numeroPedido;
    private UsuarioResponse usuario;
    private LocalDateTime fechaCompra;
    private Integer total;
    private String estado; // PENDIENTE, PROCESANDO, COMPLETADO, CANCELADO, REEMBOLSADO
    private String metodoPago; // STRIPE, PAYPAL, TRANSFERENCIA
    private List<PedidoItemResponse> items;
}
