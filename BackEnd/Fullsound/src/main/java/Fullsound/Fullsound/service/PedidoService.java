package Fullsound.Fullsound.service;

import Fullsound.Fullsound.dto.request.PedidoRequest;
import Fullsound.Fullsound.dto.response.PedidoResponse;

import java.util.List;

/**
 * Servicio de gestión de pedidos.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
public interface PedidoService {
    
    /**
     * Crea un nuevo pedido.
     */
    PedidoResponse create(PedidoRequest request, Integer usuarioId);
    
    /**
     * Obtiene un pedido por ID.
     */
    PedidoResponse getById(Integer id);
    
    /**
     * Obtiene un pedido por número de pedido.
     */
    PedidoResponse getByNumeroPedido(String numeroPedido);
    
    /**
     * Obtiene todos los pedidos de un usuario.
     */
    List<PedidoResponse> getByUsuario(Integer usuarioId);
    
    /**
     * Obtiene todos los pedidos (admin).
     */
    List<PedidoResponse> getAll();
    
    /**
     * Actualiza el estado de un pedido.
     */
    PedidoResponse updateEstado(Integer id, String estado);
}
