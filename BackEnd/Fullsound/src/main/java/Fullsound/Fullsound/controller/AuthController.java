package Fullsound.Fullsound.controller;
import Fullsound.Fullsound.dto.request.ForgotPasswordRequest;
import Fullsound.Fullsound.dto.request.LoginRequest;
import Fullsound.Fullsound.dto.request.RegisterRequest;
import Fullsound.Fullsound.dto.request.ResetPasswordRequest;
import Fullsound.Fullsound.dto.response.AuthResponse;
import Fullsound.Fullsound.dto.response.MessageResponse;
import Fullsound.Fullsound.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@Tag(name = "🔐 Autenticación", description = "Endpoints para registro, login y gestión de autenticación JWT")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
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
        ),
        @ApiResponse(
            responseCode = "429",
            description = "Demasiados intentos",
            content = @Content
        )
    })
    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registro recibido - Usuario: {}, Correo: {}", request.getNombreUsuario(), request.getCorreo());
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
        ),
        @ApiResponse(
            responseCode = "429",
            description = "Demasiados intentos",
            content = @Content
        )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    @Operation(
        summary = "Solicitar restablecimiento de contraseña",
        description = "Genera un token de restablecimiento y lo registra en logs (no hay servicio de email). Siempre devuelve éxito por seguridad."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Solicitud procesada",
        content = @Content(schema = @Schema(implementation = MessageResponse.class))
    )
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        MessageResponse response = authService.forgotPassword(request);
        return ResponseEntity.ok(response);
    }
    @Operation(
        summary = "Restablecer contraseña",
        description = "Valida el token de restablecimiento y actualiza la contraseña del usuario."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contraseña restablecida", content = @Content(schema = @Schema(implementation = MessageResponse.class))),
        @ApiResponse(responseCode = "400", description = "Token inválido o expirado", content = @Content)
    })
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        MessageResponse response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }
    @Operation(
        summary = "Verificar disponibilidad de nombre de usuario",
        description = "Comprueba si un nombre de usuario está disponible para registro."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Disponibilidad del nombre de usuario",
        content = @Content(schema = @Schema(implementation = Map.class))
    )
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Object>> checkUsername(
            @Parameter(description = "Nombre de usuario a verificar", required = true, example = "nuevousuario")
            @RequestParam String username) {
        boolean available = authService.isUsernameAvailable(username);
        return ResponseEntity.ok(Map.of("available", available, "username", username));
    }
    @Operation(
        summary = "Verificar disponibilidad de correo",
        description = "Comprueba si un correo electrónico está disponible para registro."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Disponibilidad del correo",
        content = @Content(schema = @Schema(implementation = Map.class))
    )
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmail(
            @Parameter(description = "Correo a verificar", required = true, example = "nuevo@example.com")
            @RequestParam String email) {
        boolean available = authService.isEmailAvailable(email);
        return ResponseEntity.ok(Map.of("available", available, "email", email));
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
