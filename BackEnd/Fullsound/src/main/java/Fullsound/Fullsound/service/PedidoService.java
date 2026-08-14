package Fullsound.Fullsound.service;
import Fullsound.Fullsound.dto.request.PedidoRequest;
import Fullsound.Fullsound.dto.response.PedidoResponse;
import java.util.List;
public interface PedidoService {
    PedidoResponse create(PedidoRequest request, Integer usuarioId);
    PedidoResponse getById(Integer id);
    PedidoResponse getByNumeroPedido(String numeroPedido);
    List<PedidoResponse> getByUsuario(Integer usuarioId);
    List<PedidoResponse> getAll();
    PedidoResponse updateEstado(Integer id, String estado);
}
