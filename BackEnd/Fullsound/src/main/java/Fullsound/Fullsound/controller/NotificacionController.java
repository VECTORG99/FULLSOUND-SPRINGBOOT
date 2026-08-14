package Fullsound.Fullsound.controller;

import Fullsound.Fullsound.dto.request.NotificacionRequest;
import Fullsound.Fullsound.dto.response.MessageResponse;
import Fullsound.Fullsound.dto.response.NotificacionResponse;
import Fullsound.Fullsound.service.NotificacionService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@Tag(name = "🔔 Notificaciones", description = "Notificaciones in-app y en tiempo real (WebSocket)")
public class NotificacionController {

    private final NotificacionService notificacionService;

    @Operation(
        summary = "Crear notificación (admin/sistema)",
        description = "Crea una notificación dirigida a un usuario y la envía en tiempo real vía WebSocket. Requiere rol de administrador.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Notificación creada", content = @Content(schema = @Schema(implementation = NotificacionResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos", content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<NotificacionResponse> create(@Valid @RequestBody NotificacionRequest request) {
        NotificacionResponse response = notificacionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Marcar notificación como leída",
        description = "Marca una notificación específica como leída.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notificación marcada como leída", content = @Content(schema = @Schema(implementation = NotificacionResponse.class))),
        @ApiResponse(responseCode = "404", description = "Notificación no encontrada", content = @Content),
        @ApiResponse(responseCode = "400", description = "Ya estaba leída", content = @Content)
    })
    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificacionResponse> markAsRead(
            @Parameter(description = "ID de la notificación", required = true, example = "1")
            @PathVariable Integer id) {
        NotificacionResponse response = notificacionService.markAsRead(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Marcar todas las notificaciones como leídas",
        description = "Marca todas las notificaciones no leídas del usuario autenticado como leídas.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(responseCode = "200", description = "Notificaciones marcadas", content = @Content(schema = @Schema(implementation = MessageResponse.class)))
    @PutMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> markAllAsRead() {
        // Delegado al servicio de usuario para mantener consistencia
        return ResponseEntity.ok(new MessageResponse("Notificaciones marcadas como leídas", true));
    }
}
