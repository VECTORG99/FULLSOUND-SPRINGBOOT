package Fullsound.Fullsound.mapper;

import Fullsound.Fullsound.dto.response.ReviewResponse;
import Fullsound.Fullsound.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "beat.id", target = "beatId")
    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "usuario.nombreUsuario", target = "nombreUsuario")
    ReviewResponse toResponse(Review review);
}
