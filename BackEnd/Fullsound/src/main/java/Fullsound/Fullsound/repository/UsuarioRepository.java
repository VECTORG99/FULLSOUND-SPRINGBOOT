package Fullsound.Fullsound.repository;
import Fullsound.Fullsound.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
    Optional<Usuario> findByCorreo(String correo);
    boolean existsByNombreUsuario(String nombreUsuario);
    boolean existsByCorreo(String correo);
    List<Usuario> findByActivo(Boolean activo);
    @Query("SELECT u FROM Usuario u WHERE u.rol.tipo = :rolTipo")
    List<Usuario> findByRolTipo(String rolTipo);
}
