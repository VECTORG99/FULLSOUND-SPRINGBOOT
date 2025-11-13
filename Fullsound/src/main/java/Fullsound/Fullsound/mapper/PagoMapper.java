package Fullsound.Fullsound.mapper;

import Fullsound.Fullsound.dto.response.PagoResponse;
import Fullsound.Fullsound.model.Pago;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para entidad Pago.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
@Mapper(componentModel = "spring")
public interface PagoMapper {
    
    /**
     * Convierte una entidad Pago a PagoResponse DTO.
     */
    @Mapping(target = "pedidoId", source = "pedido.id")
    @Mapping(target = "clientSecret", ignore = true)
    PagoResponse toResponse(Pago pago);
}
