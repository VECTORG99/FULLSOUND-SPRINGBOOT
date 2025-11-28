package Fullsound.Fullsound.repository;

import Fullsound.Fullsound.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para la entidad Usuario.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    /**
     * Busca un usuario por su nombre de usuario.
     * 
     * @param nombreUsuario el nombre de usuario
     * @return Optional conteniendo el usuario si existe
     */
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
    
    /**
     * Busca un usuario por su correo electrónico.
     * 
     * @param correo el correo electrónico
     * @return Optional conteniendo el usuario si existe
     */
    Optional<Usuario> findByCorreo(String correo);
    
    /**
     * Verifica si existe un usuario con el nombre de usuario especificado.
     * 
     * @param nombreUsuario el nombre de usuario
     * @return true si existe, false en caso contrario
     */
    boolean existsByNombreUsuario(String nombreUsuario);
    
    /**
     * Verifica si existe un usuario con el correo especificado.
     * 
     * @param correo el correo electrónico
     * @return true si existe, false en caso contrario
     */
    boolean existsByCorreo(String correo);
    
    /**
     * Busca usuarios activos.
     * 
     * @param activo true para activos, false para inactivos
     * @return lista de usuarios
     */
    List<Usuario> findByActivo(Boolean activo);
    
    /**
     * Busca usuarios por rol.
     * 
     * @param rolTipo el tipo de rol ("cliente" o "administrador")
     * @return lista de usuarios con ese rol
     */
    @Query("SELECT u FROM Usuario u JOIN u.roles r WHERE r.tipo = :rolTipo")
    List<Usuario> findByRolTipo(String rolTipo);
}
