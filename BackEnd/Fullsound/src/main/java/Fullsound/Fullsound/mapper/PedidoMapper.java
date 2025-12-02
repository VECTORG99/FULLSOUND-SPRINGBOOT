package Fullsound.Fullsound.mapper;
import Fullsound.Fullsound.dto.response.PedidoItemResponse;
import Fullsound.Fullsound.dto.response.PedidoResponse;
import Fullsound.Fullsound.model.Pedido;
import Fullsound.Fullsound.model.PedidoItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring", uses = {UsuarioMapper.class})
public interface PedidoMapper {
    PedidoResponse toResponse(Pedido pedido);
    @Mapping(target = "beatId", source = "beat.id")
    @Mapping(target = "subtotal", expression = "java(pedidoItem.getSubtotal())")
    PedidoItemResponse toItemResponse(PedidoItem pedidoItem);
}
