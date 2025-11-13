package Fullsound.Fullsound.mapper;

import Fullsound.Fullsound.dto.response.PedidoItemResponse;
import Fullsound.Fullsound.dto.response.PedidoResponse;
import Fullsound.Fullsound.model.Pedido;
import Fullsound.Fullsound.model.PedidoItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para entidad Pedido.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
@Mapper(componentModel = "spring", uses = {UsuarioMapper.class})
public interface PedidoMapper {
    
    /**
     * Convierte una entidad Pedido a PedidoResponse DTO.
     */
    PedidoResponse toResponse(Pedido pedido);
    
    /**
     * Convierte una entidad PedidoItem a PedidoItemResponse DTO.
     */
    @Mapping(target = "beatId", source = "beat.id")
    @Mapping(target = "subtotal", expression = "java(pedidoItem.getSubtotal())")
    PedidoItemResponse toItemResponse(PedidoItem pedidoItem);
}
