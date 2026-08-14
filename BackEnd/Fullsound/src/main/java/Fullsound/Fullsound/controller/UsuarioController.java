package Fullsound.Fullsound.controller;
import Fullsound.Fullsound.dto.request.UpdatePasswordRequest;
import Fullsound.Fullsound.dto.request.UpdateUsuarioRequest;
import Fullsound.Fullsound.dto.response.MessageResponse;
import Fullsound.Fullsound.dto.response.UsuarioResponse;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@Tag(name = "👥 Usuarios", description = "Gestión de usuarios y perfiles")
public class UsuarioController {
    private final UsuarioService usuarioService;
    @Operation(
        summary = "Obtener perfil propio",
        description = "Devuelve la información del usuario autenticado actualmente.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(responseCode = "200", description = "Perfil del usuario", content = @Content(schema = @Schema(implementation = UsuarioResponse.class)))
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponse> getProfile(Authentication authentication) {
        String nombreUsuario = authentication.getName();
        UsuarioResponse response = usuarioService.getByNombreUsuario(nombreUsuario);
        return ResponseEntity.ok(response);
    }
    @Operation(
        summary = "Actualizar perfil propio",
        description = "Actualiza la información del usuario autenticado actualmente.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil actualizado", content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponse> updateProfile(
            @Valid @RequestBody UpdateUsuarioRequest request,
            Authentication authentication) {
        String nombreUsuario = authentication.getName();
        UsuarioResponse response = usuarioService.updateProfile(nombreUsuario, request);
        return ResponseEntity.ok(response);
    }
    @Operation(
        summary = "Obtener usuario por ID",
        description = "Devuelve la información de un usuario específico. Requiere rol de administrador.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos", content = @Content)
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<UsuarioResponse> getById(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Integer id) {
        UsuarioResponse response = usuarioService.getById(id);
        return ResponseEntity.ok(response);
    }
    @Operation(
        summary = "Listar todos los usuarios (paginado)",
        description = "Obtiene una lista paginada de todos los usuarios. Requiere rol de administrador.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(responseCode = "200", description = "Página de usuarios", content = @Content(schema = @Schema(implementation = Page.class)))
    @GetMapping
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<Page<UsuarioResponse>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        Page<UsuarioResponse> responses = usuarioService.getAll(pageable);
        return ResponseEntity.ok(responses);
    }
    @Operation(
        summary = "Desactivar usuario",
        description = "Desactiva un usuario (soft delete). Requiere rol de administrador.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(responseCode = "200", description = "Usuario desactivado", content = @Content(schema = @Schema(implementation = MessageResponse.class)))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<MessageResponse> deactivate(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Integer id) {
        usuarioService.deactivate(id);
        return ResponseEntity.ok(new MessageResponse("Usuario desactivado correctamente", true));
    }
    @Operation(
        summary = "Activar usuario",
        description = "Reactiva un usuario previamente desactivado. Requiere rol de administrador.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(responseCode = "200", description = "Usuario activado", content = @Content(schema = @Schema(implementation = MessageResponse.class)))
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<MessageResponse> activate(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Integer id) {
        usuarioService.activate(id);
        return ResponseEntity.ok(new MessageResponse("Usuario activado correctamente", true));
    }
    @Operation(
        summary = "Cambiar contraseña",
        description = "Cambia la contraseña del usuario autenticado proporcionando la contraseña actual.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contraseña actualizada", content = @Content(schema = @Schema(implementation = MessageResponse.class))),
        @ApiResponse(responseCode = "400", description = "Contraseña actual incorrecta", content = @Content)
    })
    @PostMapping("/cambiar-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> cambiarPassword(
            @Valid @RequestBody UpdatePasswordRequest request,
            Authentication authentication) {
        String nombreUsuario = authentication.getName();
        usuarioService.cambiarPassword(nombreUsuario, request.getPasswordActual(), request.getPasswordNueva());
        return ResponseEntity.ok(new MessageResponse("Contraseña actualizada exitosamente", true));
    }
}
