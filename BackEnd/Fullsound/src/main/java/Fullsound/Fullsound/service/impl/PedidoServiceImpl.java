package Fullsound.Fullsound.service.impl;
import Fullsound.Fullsound.dto.request.PedidoRequest;
import Fullsound.Fullsound.dto.response.PedidoResponse;
import Fullsound.Fullsound.exception.BadRequestException;
import Fullsound.Fullsound.exception.ResourceNotFoundException;
import Fullsound.Fullsound.mapper.PedidoMapper;
import Fullsound.Fullsound.model.Beat;
import Fullsound.Fullsound.model.Pedido;
import Fullsound.Fullsound.model.PedidoItem;
import Fullsound.Fullsound.model.Usuario;
import Fullsound.Fullsound.repository.BeatRepository;
import Fullsound.Fullsound.repository.PedidoRepository;
import Fullsound.Fullsound.repository.UsuarioRepository;
import Fullsound.Fullsound.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final BeatRepository beatRepository;
    private final PedidoMapper pedidoMapper;
    @Override
    @Transactional
    public PedidoResponse create(PedidoRequest request, Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId.toString()));
        if (request.getBeatIds() == null || request.getBeatIds().isEmpty()) {
            throw new BadRequestException("El pedido debe contener al menos un beat");
        }
        List<Beat> beats = new ArrayList<>();
        for (Integer beatId : request.getBeatIds()) {
            Beat beat = beatRepository.findById(beatId)
                    .orElseThrow(() -> new ResourceNotFoundException("Beat", "id", beatId.toString()));
            if (!"DISPONIBLE".equals(beat.getEstado())) {
                throw new BadRequestException("El beat '" + beat.getTitulo() + "' no está disponible");
            }
            beats.add(beat);
        }
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFechaCompra(LocalDateTime.now());
        pedido.setEstado("PENDIENTE");
        pedido.setMetodoPago(request.getMetodoPago());
        List<PedidoItem> items = new ArrayList<>();
        Integer total = 0;
        for (Beat beat : beats) {
            PedidoItem item = new PedidoItem();
            item.setPedido(pedido);
            item.setBeat(beat);
            item.setNombreItem(beat.getTitulo());  
            item.setCantidad(1);  
            item.setPrecioUnitario(beat.getPrecio());
            items.add(item);
            total += beat.getPrecio();
        }
        pedido.setItems(items);
        pedido.setTotal(total);
        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        return pedidoMapper.toResponse(pedidoGuardado);
    }
    @Override
    @Transactional(readOnly = true)
    public PedidoResponse getById(Integer id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id.toString()));
        return pedidoMapper.toResponse(pedido);
    }
    @Override
    @Transactional(readOnly = true)
    public PedidoResponse getByNumeroPedido(String numeroPedido) {
        Pedido pedido = pedidoRepository.findByNumeroPedido(numeroPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "numeroPedido", numeroPedido));
        return pedidoMapper.toResponse(pedido);
    }
    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponse> getByUsuario(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId.toString()));
        List<Pedido> pedidos = pedidoRepository.findByUsuarioOrderByFechaCompraDesc(usuario);
        return pedidos.stream()
                .map(pedidoMapper::toResponse)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponse> getAll() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        return pedidos.stream()
                .map(pedidoMapper::toResponse)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional
    public PedidoResponse updateEstado(Integer id, String estado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id.toString()));
        pedido.setEstado(estado);
        if ("COMPLETADO".equals(estado)) {
            for (PedidoItem item : pedido.getItems()) {
                Beat beat = item.getBeat();
                beat.setEstado("VENDIDO");
                beatRepository.save(beat);
            }
        }
        if ("CANCELADO".equals(estado) || "REEMBOLSADO".equals(estado)) {
            for (PedidoItem item : pedido.getItems()) {
                Beat beat = item.getBeat();
                if ("VENDIDO".equals(beat.getEstado()) || "RESERVADO".equals(beat.getEstado())) {
                    beat.setEstado("DISPONIBLE");
                    beatRepository.save(beat);
                }
            }
        }
        Pedido pedidoActualizado = pedidoRepository.save(pedido);
        return pedidoMapper.toResponse(pedidoActualizado);
    }
}
