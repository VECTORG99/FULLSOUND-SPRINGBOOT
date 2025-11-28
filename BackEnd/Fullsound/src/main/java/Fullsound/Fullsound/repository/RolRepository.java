package Fullsound.Fullsound.repository;

import Fullsound.Fullsound.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para la entidad Rol.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    
    /**
     * Busca un rol por su tipo (ej: "cliente", "administrador").
     * 
     * @param tipo el tipo de rol
     * @return Optional conteniendo el rol si existe
     */
    Optional<Rol> findByTipo(String tipo);
    
    /**
     * Verifica si existe un rol con el tipo especificado.
     * 
     * @param tipo el tipo de rol
     * @return true si existe, false en caso contrario
     */
    boolean existsByTipo(String tipo);
}
