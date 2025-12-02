package Fullsound.Fullsound.repository;
import Fullsound.Fullsound.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    Optional<Rol> findByTipo(String tipo);
    boolean existsByTipo(String tipo);
}
