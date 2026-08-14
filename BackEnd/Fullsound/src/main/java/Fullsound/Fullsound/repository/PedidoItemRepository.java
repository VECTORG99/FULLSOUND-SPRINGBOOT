package Fullsound.Fullsound.repository;
import Fullsound.Fullsound.model.PedidoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface PedidoItemRepository extends JpaRepository<PedidoItem, Integer> {
}
