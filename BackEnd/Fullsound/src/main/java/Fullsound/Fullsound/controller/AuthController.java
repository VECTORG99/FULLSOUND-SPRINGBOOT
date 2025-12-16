package Fullsound.Fullsound.controller;
import Fullsound.Fullsound.dto.request.LoginRequest;
import Fullsound.Fullsound.dto.request.RegisterRequest;
import Fullsound.Fullsound.dto.response.AuthResponse;
import Fullsound.Fullsound.dto.response.MessageResponse;
import Fullsound.Fullsound.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@Tag(name = "🔐 Autenticación", description = "Endpoints para registro, login y gestión de autenticación JWT")
public class AuthController {
    private final AuthService authService;
    @Operation(
        summary = "Registrar nuevo usuario",
        description = "Crea una nueva cuenta de usuario en el sistema. El usuario recibe por defecto el rol 'cliente'."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Usuario registrado exitosamente",
            content = @Content(schema = @Schema(implementation = MessageResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos o usuario ya existe",
            content = @Content
        )
    })
    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        System.out.println("[AUTH] Registro recibido - Usuario: " + request.getNombreUsuario() + 
                         ", Correo: " + request.getCorreo() + 
                         ", Rol: " + request.getRol());
        MessageResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica un usuario y devuelve un token JWT válido por 24 horas."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login exitoso, token JWT generado",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Credenciales inválidas",
            content = @Content
        )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    @Operation(
        summary = "Health Check",
        description = "Verifica que el servicio de autenticación está funcionando correctamente."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Servicio activo",
        content = @Content(schema = @Schema(implementation = MessageResponse.class))
    )
    @GetMapping("/health")
    public ResponseEntity<MessageResponse> health() {
        return ResponseEntity.ok(new MessageResponse("FullSound API - Servicio de autenticación activo", true));
    }
}
