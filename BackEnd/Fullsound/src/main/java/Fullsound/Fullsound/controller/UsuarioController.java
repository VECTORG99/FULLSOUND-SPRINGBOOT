package Fullsound.Fullsound.controller;
import Fullsound.Fullsound.dto.request.UpdatePasswordRequest;
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
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:4200", "http://localhost:8080"})
public class UsuarioController {
    private final UsuarioService usuarioService;
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponse> getProfile(Authentication authentication) {
        String nombreUsuario = authentication.getName();
        UsuarioResponse response = usuarioService.getByNombreUsuario(nombreUsuario);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponse> updateProfile(
            @Valid @RequestBody UpdateUsuarioRequest request,
            Authentication authentication) {
        String nombreUsuario = authentication.getName();
        UsuarioResponse response = usuarioService.updateProfile(nombreUsuario, request);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<UsuarioResponse> getById(@PathVariable Integer id) {
        UsuarioResponse response = usuarioService.getById(id);
        return ResponseEntity.ok(response);
    }
    @GetMapping
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<List<UsuarioResponse>> getAll() {
        List<UsuarioResponse> responses = usuarioService.getAll();
        return ResponseEntity.ok(responses);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<MessageResponse> deactivate(@PathVariable Integer id) {
        usuarioService.deactivate(id);
        return ResponseEntity.ok(new MessageResponse("Usuario desactivado correctamente", true));
    }
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<MessageResponse> activate(@PathVariable Integer id) {
        usuarioService.activate(id);
        return ResponseEntity.ok(new MessageResponse("Usuario activado correctamente", true));
    }
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
