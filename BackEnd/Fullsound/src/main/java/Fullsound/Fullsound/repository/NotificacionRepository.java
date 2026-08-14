package Fullsound.Fullsound.repository;

import Fullsound.Fullsound.model.Notificacion;
import Fullsound.Fullsound.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {
    Page<Notificacion> findByUsuarioOrderByCreatedAtDesc(Usuario usuario, Pageable pageable);
    List<Notificacion> findByUsuarioAndLeidoFalseOrderByCreatedAtDesc(Usuario usuario);

    @Query("SELECT COUNT(n) FROM Notificacion n WHERE n.usuario.id = :usuarioId AND n.leido = false")
    Long countUnreadByUsuarioId(@Param("usuarioId") Integer usuarioId);

    @Modifying
    @Query("UPDATE Notificacion n SET n.leido = true WHERE n.id = :id")
    int markAsRead(@Param("id") Integer id);
}
