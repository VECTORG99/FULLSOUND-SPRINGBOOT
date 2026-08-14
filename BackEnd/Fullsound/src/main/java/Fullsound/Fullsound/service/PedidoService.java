package Fullsound.Fullsound.service;
import Fullsound.Fullsound.dto.request.PedidoRequest;
import Fullsound.Fullsound.dto.response.PedidoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
public interface PedidoService {
    PedidoResponse create(PedidoRequest request, Integer usuarioId);
    PedidoResponse getById(Integer id);
    PedidoResponse getByNumeroPedido(String numeroPedido);
    List<PedidoResponse> getByUsuario(Integer usuarioId);
    List<PedidoResponse> getAll();
    Page<PedidoResponse> getAll(Pageable pageable);
    PedidoResponse updateEstado(Integer id, String estado);
}
