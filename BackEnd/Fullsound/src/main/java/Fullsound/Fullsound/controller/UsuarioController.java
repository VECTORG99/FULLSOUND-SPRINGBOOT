package Fullsound.Fullsound.controller;

import Fullsound.Fullsound.dto.request.UpdateUsuarioRequest;
import Fullsound.Fullsound.dto.response.MessageResponse;
import Fullsound.Fullsound.dto.response.UsuarioResponse;
import Fullsound.Fullsound.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de usuarios.
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:4200", "http://localhost:8080"})
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Obtiene el perfil del usuario autenticado.
     *
     * @param authentication información del usuario autenticado
     * @return datos del usuario
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponse> getProfile(Authentication authentication) {
        String nombreUsuario = authentication.getName();
        UsuarioResponse response = usuarioService.getByNombreUsuario(nombreUsuario);
        return ResponseEntity.ok(response);
    }

    /**
     * Actualiza el perfil del usuario autenticado.
     *
     * @param request datos a actualizar
     * @param authentication información del usuario autenticado
     * @return usuario actualizado
     */
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponse> updateProfile(
            @Valid @RequestBody UpdateUsuarioRequest request,
            Authentication authentication) {
        String nombreUsuario = authentication.getName();
        UsuarioResponse response = usuarioService.updateProfile(nombreUsuario, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene un usuario por su ID (solo administradores).
     *
     * @param id ID del usuario
     * @return datos del usuario
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<UsuarioResponse> getById(@PathVariable Integer id) {
        UsuarioResponse response = usuarioService.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene todos los usuarios (solo administradores).
     *
     * @return lista de todos los usuarios
     */
    @GetMapping
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<List<UsuarioResponse>> getAll() {
        List<UsuarioResponse> responses = usuarioService.getAll();
        return ResponseEntity.ok(responses);
    }

    /**
     * Desactiva un usuario (solo administradores).
     *
     * @param id ID del usuario
     * @return mensaje de confirmación
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<MessageResponse> deactivate(@PathVariable Integer id) {
        usuarioService.deactivate(id);
        return ResponseEntity.ok(new MessageResponse("Usuario desactivado correctamente", true));
    }

    /**
     * Activa un usuario (solo administradores).
     *
     * @param id ID del usuario
     * @return mensaje de confirmación
     */
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<MessageResponse> activate(@PathVariable Integer id) {
        usuarioService.activate(id);
        return ResponseEntity.ok(new MessageResponse("Usuario activado correctamente", true));
    }
}
