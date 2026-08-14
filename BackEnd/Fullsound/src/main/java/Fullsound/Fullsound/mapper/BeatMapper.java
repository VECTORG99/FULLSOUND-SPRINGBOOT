package Fullsound.Fullsound.mapper;
import Fullsound.Fullsound.dto.request.BeatRequest;
import Fullsound.Fullsound.dto.response.BeatResponse;
import Fullsound.Fullsound.model.Beat;
import org.mapstruct.*;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BeatMapper {
    @Mapping(source = "id", target = "idBeat")
    @Mapping(target = "precioFormateado", expression = "java(beat.getPrecioFormateado())")
    @Mapping(target = "enlaceProducto", expression = "java(beat.getEnlaceProducto())")
    BeatResponse toResponse(Beat beat);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "reproducciones", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Beat toEntity(BeatRequest request);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "reproducciones", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
    void updateEntity(BeatRequest request, @MappingTarget Beat beat);
}
