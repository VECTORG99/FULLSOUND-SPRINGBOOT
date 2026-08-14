package Fullsound.Fullsound.mapper;

import Fullsound.Fullsound.dto.response.NotificacionResponse;
import Fullsound.Fullsound.model.Notificacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificacionMapper {

    @Mapping(source = "usuario.id", target = "usuarioId")
    NotificacionResponse toResponse(Notificacion notificacion);
}
