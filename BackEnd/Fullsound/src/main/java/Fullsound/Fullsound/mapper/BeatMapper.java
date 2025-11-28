package Fullsound.Fullsound.mapper;

import Fullsound.Fullsound.dto.request.BeatRequest;
import Fullsound.Fullsound.dto.response.BeatResponse;
import Fullsound.Fullsound.model.Beat;
import org.mapstruct.*;

/**
 * Mapper para entidad Beat.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
@Mapper(componentModel = "spring")
public interface BeatMapper {
    
    /**
     * Convierte una entidad Beat a BeatResponse DTO.
     */
    @Mapping(target = "precioFormateado", expression = "java(beat.getPrecioFormateado())")
    @Mapping(target = "enlaceProducto", expression = "java(beat.getEnlaceProducto())")
    BeatResponse toResponse(Beat beat);
    
    /**
     * Convierte un BeatRequest DTO a entidad Beat.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "reproducciones", ignore = true)
    @Mapping(target = "descargas", ignore = true)
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Beat toEntity(BeatRequest request);
    
    /**
     * Actualiza una entidad Beat existente con datos de BeatRequest.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "reproducciones", ignore = true)
    @Mapping(target = "descargas", ignore = true)
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(BeatRequest request, @MappingTarget Beat beat);
}
