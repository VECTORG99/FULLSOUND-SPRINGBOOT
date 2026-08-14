package Fullsound.Fullsound.controller;
import Fullsound.Fullsound.dto.request.PedidoRequest;
import Fullsound.Fullsound.dto.response.PedidoResponse;
import Fullsound.Fullsound.service.PedidoService;
import Fullsound.Fullsound.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
@Tag(name = "🛒 Pedidos", description = "Gestión de pedidos y compras de beats")
public class PedidoController {
    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;
    @Operation(
        summary = "Crear pedido",
        description = "Crea un nuevo pedido con los beats seleccionados. Requiere autenticación.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente", content = @Content(schema = @Schema(implementation = PedidoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o beats no disponibles", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PedidoResponse> create(
            @Valid @RequestBody PedidoRequest request,
            Authentication authentication) {
        Integer usuarioId = usuarioService.getIdByNombreUsuario(authentication.getName());
        PedidoResponse response = pedidoService.create(request, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @Operation(
        summary = "Obtener pedido por ID",
        description = "Devuelve un pedido específico por su identificador.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido encontrado", content = @Content(schema = @Schema(implementation = PedidoResponse.class))),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PedidoResponse> getById(
            @Parameter(description = "ID del pedido", required = true, example = "1")
            @PathVariable Integer id) {
        PedidoResponse response = pedidoService.getById(id);
        return ResponseEntity.ok(response);
    }
    @Operation(
        summary = "Obtener pedido por número",
        description = "Devuelve un pedido específico por su número de pedido.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido encontrado", content = @Content(schema = @Schema(implementation = PedidoResponse.class))),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content)
    })
    @GetMapping("/numero/{numeroPedido}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PedidoResponse> getByNumeroPedido(
            @Parameter(description = "Número de pedido", required = true, example = "FS-20250101-123456")
            @PathVariable String numeroPedido) {
        PedidoResponse response = pedidoService.getByNumeroPedido(numeroPedido);
        return ResponseEntity.ok(response);
    }
    @Operation(
        summary = "Obtener mis pedidos",
        description = "Devuelve el historial de pedidos del usuario autenticado.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(responseCode = "200", description = "Lista de pedidos del usuario", content = @Content(schema = @Schema(implementation = PedidoResponse.class)))
    @GetMapping("/mis-pedidos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PedidoResponse>> getMisPedidos(Authentication authentication) {
        Integer usuarioId = usuarioService.getIdByNombreUsuario(authentication.getName());
        List<PedidoResponse> responses = pedidoService.getByUsuario(usuarioId);
        return ResponseEntity.ok(responses);
    }
    @Operation(
        summary = "Listar todos los pedidos (paginado)",
        description = "Obtiene una lista paginada de todos los pedidos. Requiere rol de administrador.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(responseCode = "200", description = "Página de pedidos", content = @Content(schema = @Schema(implementation = Page.class)))
    @GetMapping
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<Page<PedidoResponse>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        Page<PedidoResponse> responses = pedidoService.getAll(pageable);
        return ResponseEntity.ok(responses);
    }
    @Operation(
        summary = "Actualizar estado del pedido",
        description = "Actualiza el estado de un pedido (PENDIENTE, PROCESANDO, COMPLETADO, CANCELADO, REEMBOLSADO). Requiere rol de administrador.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado actualizado", content = @Content(schema = @Schema(implementation = PedidoResponse.class))),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos", content = @Content)
    })
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<PedidoResponse> updateEstado(
            @Parameter(description = "ID del pedido", required = true, example = "1")
            @PathVariable Integer id,
            @Parameter(description = "Nuevo estado", required = true, example = "COMPLETADO")
            @RequestParam String estado) {
        PedidoResponse response = pedidoService.updateEstado(id, estado);
        return ResponseEntity.ok(response);
    }
}
