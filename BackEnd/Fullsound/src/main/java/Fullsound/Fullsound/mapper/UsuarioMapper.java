package Fullsound.Fullsound.mapper;
import Fullsound.Fullsound.dto.response.UsuarioResponse;
import Fullsound.Fullsound.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    @Mapping(target = "roles", expression = "java(mapRoles(usuario))")
    UsuarioResponse toResponse(Usuario usuario);
    default List<String> mapRoles(Usuario usuario) {
        if (usuario == null || usuario.getRol() == null) {
            return List.of();
        }
        return List.of(usuario.getRol().getTipo());
    }
}
