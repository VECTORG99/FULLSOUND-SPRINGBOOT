package Fullsound.Fullsound.repository;

import Fullsound.Fullsound.model.Pago;
import Fullsound.Fullsound.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para la entidad Pago.
 * Adaptado al schema de PostgreSQL.
 * 
 * @author VECTORG99
 * @version 2.0.0
 * @since 2025-11-30
 */
@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
    
    /**
     * Busca un pago por su Stripe Payment Intent ID.
     * 
     * @param stripePaymentIntentId el ID del Payment Intent de Stripe
     * @return Optional conteniendo el pago si existe
     */
    Optional<Pago> findByStripePaymentIntentId(String stripePaymentIntentId);
    
    /**
     * Busca un pago por su Stripe Charge ID.
     * 
     * @param stripeChargeId el ID del Charge de Stripe
     * @return Optional conteniendo el pago si existe
     */
    Optional<Pago> findByStripeChargeId(String stripeChargeId);
    
    /**
     * Busca todos los pagos asociados a un pedido.
     * 
     * @param pedido el pedido
     * @return lista de pagos del pedido
     */
    List<Pago> findByPedido(Pedido pedido);
    
    /**
     * Busca pagos por estado.
     * 
     * @param estado el estado del pago (PENDIENTE, COMPLETADO, FALLIDO, REEMBOLSADO)
     * @return lista de pagos con ese estado
     */
    List<Pago> findByEstado(String estado);
    
    /**
     * Verifica si existe un pago para un pedido específico.
     * 
     * @param pedido el pedido
     * @return true si existe, false en caso contrario
     */
    boolean existsByPedido(Pedido pedido);
}
