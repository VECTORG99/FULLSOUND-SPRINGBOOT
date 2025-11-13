package Fullsound.Fullsound.controller;

import Fullsound.Fullsound.dto.request.PedidoRequest;
import Fullsound.Fullsound.dto.response.PedidoResponse;
import Fullsound.Fullsound.enums.EstadoPedido;
import Fullsound.Fullsound.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de pedidos.
 */
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:4200", "http://localhost:8080"})
public class PedidoController {

    private final PedidoService pedidoService;

    /**
     * Crea un nuevo pedido para el usuario autenticado.
     *
     * @param request datos del pedido
     * @param authentication información del usuario autenticado
     * @return pedido creado
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PedidoResponse> create(
            @Valid @RequestBody PedidoRequest request,
            Authentication authentication) {
        String nombreUsuario = authentication.getName();
        PedidoResponse response = pedidoService.create(request, nombreUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtiene un pedido por su ID (solo administradores o el usuario propietario).
     *
     * @param id ID del pedido
     * @return pedido encontrado
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PedidoResponse> getById(@PathVariable Integer id) {
        PedidoResponse response = pedidoService.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene un pedido por su número de pedido.
     *
     * @param numeroPedido número de pedido (formato: FS-YYYYMMDD-XXXXXX)
     * @return pedido encontrado
     */
    @GetMapping("/numero/{numeroPedido}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PedidoResponse> getByNumeroPedido(@PathVariable String numeroPedido) {
        PedidoResponse response = pedidoService.getByNumeroPedido(numeroPedido);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene todos los pedidos del usuario autenticado.
     *
     * @param authentication información del usuario autenticado
     * @return lista de pedidos del usuario
     */
    @GetMapping("/mis-pedidos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PedidoResponse>> getMisPedidos(Authentication authentication) {
        String nombreUsuario = authentication.getName();
        List<PedidoResponse> responses = pedidoService.getByUsuario(nombreUsuario);
        return ResponseEntity.ok(responses);
    }

    /**
     * Obtiene todos los pedidos (solo administradores).
     *
     * @return lista de todos los pedidos
     */
    @GetMapping
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<List<PedidoResponse>> getAll() {
        List<PedidoResponse> responses = pedidoService.getAll();
        return ResponseEntity.ok(responses);
    }

    /**
     * Actualiza el estado de un pedido (solo administradores).
     *
     * @param id ID del pedido
     * @param estado nuevo estado
     * @return pedido actualizado
     */
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<PedidoResponse> updateEstado(
            @PathVariable Integer id,
            @RequestParam EstadoPedido estado) {
        PedidoResponse response = pedidoService.updateEstado(id, estado);
        return ResponseEntity.ok(response);
    }
}
