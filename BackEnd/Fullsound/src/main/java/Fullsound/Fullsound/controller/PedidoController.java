package Fullsound.Fullsound.controller;
import Fullsound.Fullsound.dto.request.PedidoRequest;
import Fullsound.Fullsound.dto.response.PedidoResponse;
import Fullsound.Fullsound.service.PedidoService;
import Fullsound.Fullsound.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
public class PedidoController {
    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PedidoResponse> create(
            @Valid @RequestBody PedidoRequest request,
            Authentication authentication) {
        Integer usuarioId = usuarioService.getIdByNombreUsuario(authentication.getName());
        PedidoResponse response = pedidoService.create(request, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PedidoResponse> getById(@PathVariable Integer id) {
        PedidoResponse response = pedidoService.getById(id);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/numero/{numeroPedido}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PedidoResponse> getByNumeroPedido(@PathVariable String numeroPedido) {
        PedidoResponse response = pedidoService.getByNumeroPedido(numeroPedido);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/mis-pedidos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PedidoResponse>> getMisPedidos(Authentication authentication) {
        Integer usuarioId = usuarioService.getIdByNombreUsuario(authentication.getName());
        List<PedidoResponse> responses = pedidoService.getByUsuario(usuarioId);
        return ResponseEntity.ok(responses);
    }
    @GetMapping
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<List<PedidoResponse>> getAll() {
        List<PedidoResponse> responses = pedidoService.getAll();
        return ResponseEntity.ok(responses);
    }
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<PedidoResponse> updateEstado(
            @PathVariable Integer id,
            @RequestParam String estado) {
        PedidoResponse response = pedidoService.updateEstado(id, estado);
        return ResponseEntity.ok(response);
    }
}
