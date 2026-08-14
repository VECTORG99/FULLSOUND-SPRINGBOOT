package Fullsound.Fullsound.mapper;
import Fullsound.Fullsound.dto.response.UsuarioResponse;
import Fullsound.Fullsound.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
import java.util.stream.Collectors;
@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    @Mapping(target = "roles", expression = "java(mapRoles(usuario))")
    UsuarioResponse toResponse(Usuario usuario);
    default List<String> mapRoles(Usuario usuario) {
        if (usuario == null || usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            return List.of();
        }
        return usuario.getRoles().stream()
                .map(rol -> rol.getTipo())
                .collect(Collectors.toList());
    }
}
