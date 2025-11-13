package Fullsound.Fullsound.mapper;

import Fullsound.Fullsound.dto.response.UsuarioResponse;
import Fullsound.Fullsound.model.Rol;
import Fullsound.Fullsound.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para entidad Usuario.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    
    /**
     * Convierte una entidad Usuario a UsuarioResponse DTO.
     */
    @Mapping(target = "roles", expression = "java(mapRoles(usuario))")
    UsuarioResponse toResponse(Usuario usuario);
    
    /**
     * Método helper para mapear roles.
     */
    default List<String> mapRoles(Usuario usuario) {
        if (usuario == null || usuario.getRoles() == null) {
            return List.of();
        }
        return usuario.getRoles().stream()
                .map(Rol::getTipo)
                .collect(Collectors.toList());
    }
}
