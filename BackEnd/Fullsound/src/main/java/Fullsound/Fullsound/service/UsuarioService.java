package Fullsound.Fullsound.service;

import Fullsound.Fullsound.dto.request.UpdateUsuarioRequest;
import Fullsound.Fullsound.dto.response.UsuarioResponse;

import java.util.List;

/**
 * Servicio para la gestión de usuarios.
 */
public interface UsuarioService {
    
    /**
     * Obtiene un usuario por su ID.
     *
     * @param id ID del usuario
     * @return datos del usuario
     */
    UsuarioResponse getById(Integer id);
    
    /**
     * Obtiene un usuario por su nombre de usuario.
     *
     * @param nombreUsuario nombre de usuario
     * @return datos del usuario
     */
    UsuarioResponse getByNombreUsuario(String nombreUsuario);
    
    /**
     * Obtiene todos los usuarios.
     *
     * @return lista de usuarios
     */
    List<UsuarioResponse> getAll();
    
    /**
     * Actualiza el perfil de un usuario.
     *
     * @param nombreUsuario nombre del usuario a actualizar
     * @param request datos a actualizar
     * @return usuario actualizado
     */
    UsuarioResponse updateProfile(String nombreUsuario, UpdateUsuarioRequest request);
    
    /**
     * Desactiva un usuario.
     *
     * @param id ID del usuario
     */
    void deactivate(Integer id);
    
    /**
     * Activa un usuario.
     *
     * @param id ID del usuario
     */
    void activate(Integer id);
}
