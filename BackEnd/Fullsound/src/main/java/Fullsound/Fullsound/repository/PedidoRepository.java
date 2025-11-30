package Fullsound.Fullsound.repository;

import Fullsound.Fullsound.model.Pedido;
import Fullsound.Fullsound.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para la entidad Pedido.
 * Adaptado al schema de PostgreSQL.
 * 
 * @author VECTORG99
 * @version 2.0.0
 * @since 2025-11-30
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    
    /**
     * Busca un pedido por su número de pedido.
     * 
     * @param numeroPedido el número de pedido
     * @return Optional conteniendo el pedido si existe
     */
    Optional<Pedido> findByNumeroPedido(String numeroPedido);
    
    /**
     * Busca todos los pedidos de un usuario.
     * 
     * @param usuario el usuario
     * @return lista de pedidos del usuario
     */
    List<Pedido> findByUsuarioOrderByFechaCompraDesc(Usuario usuario);
    
    /**
     * Busca pedidos por usuario y estado.
     * 
     * @param usuario el usuario
     * @param estado el estado del pedido (PENDIENTE, PROCESANDO, COMPLETADO, etc.)
     * @return lista de pedidos
     */
    List<Pedido> findByUsuarioAndEstado(Usuario usuario, String estado);
    
    /**
     * Busca pedidos por estado.
     * 
     * @param estado el estado del pedido (PENDIENTE, PROCESANDO, COMPLETADO, etc.)
     * @return lista de pedidos con ese estado
     */
    List<Pedido> findByEstadoOrderByFechaCompraDesc(String estado);
    
    /**
     * Busca pedidos realizados en un rango de fechas.
     * 
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @return lista de pedidos en el rango
     */
    List<Pedido> findByFechaCompraBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    /**
     * Cuenta los pedidos completados de un usuario.
     * 
     * @param usuario el usuario
     * @return número de pedidos completados
     */
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.usuario = :usuario AND p.estado = 'COMPLETADO'")
    Long countCompletedOrdersByUser(@Param("usuario") Usuario usuario);
    
    /**
     * Calcula el total gastado por un usuario en pedidos completados.
     * 
     * @param usuario el usuario
     * @return total gastado
     */
    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE p.usuario = :usuario AND p.estado = 'COMPLETADO'")
    Double getTotalSpentByUser(@Param("usuario") Usuario usuario);
    
    /**
     * Obtiene los últimos N pedidos ordenados por fecha.
     * 
     * @param limit número de pedidos a obtener
     * @return lista de pedidos recientes
     */
    @Query("SELECT p FROM Pedido p ORDER BY p.fechaCompra DESC LIMIT :limit")
    List<Pedido> findRecentOrders(@Param("limit") int limit);
}
