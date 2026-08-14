package Fullsound.Fullsound.mapper;
import Fullsound.Fullsound.dto.response.PagoResponse;
import Fullsound.Fullsound.model.Pago;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring")
public interface PagoMapper {
    @Mapping(target = "pedidoId", source = "pedido.id")
    @Mapping(target = "clientSecret", ignore = true)
    PagoResponse toResponse(Pago pago);
}
