package Fullsound.Fullsound.repository;
import Fullsound.Fullsound.model.Pago;
import Fullsound.Fullsound.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
    Optional<Pago> findByStripePaymentIntentId(String stripePaymentIntentId);
    Optional<Pago> findByStripeChargeId(String stripeChargeId);
    List<Pago> findByPedido(Pedido pedido);
    List<Pago> findByEstado(String estado);
    boolean existsByPedido(Pedido pedido);
}
