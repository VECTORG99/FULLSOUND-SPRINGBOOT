package Fullsound.Fullsound.repository;

import Fullsound.Fullsound.model.PedidoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository para la entidad PedidoItem.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
@Repository
public interface PedidoItemRepository extends JpaRepository<PedidoItem, Integer> {
    // Métodos de consulta heredados de JpaRepository son suficientes
    // para la funcionalidad básica de PedidoItem
}
