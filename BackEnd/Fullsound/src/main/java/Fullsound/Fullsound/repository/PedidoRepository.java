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
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    Optional<Pedido> findByNumeroPedido(String numeroPedido);
    List<Pedido> findByUsuarioOrderByFechaCompraDesc(Usuario usuario);
    List<Pedido> findByUsuarioAndEstado(Usuario usuario, String estado);
    List<Pedido> findByEstadoOrderByFechaCompraDesc(String estado);
    List<Pedido> findByFechaCompraBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.usuario = :usuario AND p.estado = 'COMPLETADO'")
    Long countCompletedOrdersByUser(@Param("usuario") Usuario usuario);
    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE p.usuario = :usuario AND p.estado = 'COMPLETADO'")
    Double getTotalSpentByUser(@Param("usuario") Usuario usuario);
    @Query("SELECT p FROM Pedido p ORDER BY p.fechaCompra DESC LIMIT :limit")
    List<Pedido> findRecentOrders(@Param("limit") int limit);
}
